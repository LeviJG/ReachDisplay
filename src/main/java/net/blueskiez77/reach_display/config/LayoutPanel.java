package net.blueskiez77.reach_display.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import java.util.function.DoubleSupplier;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

/// The side menu of the HUD layout editor: a strip of tabs (one per number) docked to a screen edge, and an
/// expandable, scrolling list of that number's options. All widgets are drawn by hand so the menu can slide
/// between the two sides of the screen.
public class LayoutPanel {

    private static final String[] TAB_NAMES = { "Distance", "Hit Distance", "Average Hit Distance" };
    private static final String[] TAB_SHORT_NAMES = { "Dist", "Hit", "Avg" };

    private static final int STRIP_W = 14, TOGGLE_H = 14, TAB_PAD = 10;
    private static final int HEADER_H = 18, RESET_H = 18, ROW_INSET = 6, HANDLE_W = 3;
    private static final int MIN_WIDTH = 110, MAX_WIDTH = 260;
    private static final long SLIDE_MS = 180;

    private static final int BG = 0xD0101010, TAB_BG = 0xD0181818, TAB_SELECTED_BG = 0xF0303030;
    private static final int TEXT = 0xFFE0E0E0, TEXT_DIM = 0xFFA0A0A0, TEXT_DISABLED = 0xFF707070;
    private static final int ACCENT = 0xFF5A8AC6;

    private final HUDConfig screen;
    private final List<List<Row>> rows = new ArrayList<>();
    private final int[] scroll = new int[3];

    private boolean expanded = true;
    private boolean rightSide = false;
    private float sideProgress = 1;                // 0 = docked left, 1 = docked right
    private long lastFrameNanos = System.nanoTime();
    private int contentWidth = 150;

    private SliderRow activeSlider;
    private int activeSliderX, activeSliderW;
    private boolean resizing;
    private boolean confirmingReset;

    public LayoutPanel(HUDConfig screen) {
        this.screen = screen;
        DisplayConfig c = DisplayConfig.INSTANCE;

        rows.add(List.of(
                new ToggleRow("Enabled", () -> c.distanceEnabled, v -> c.distanceEnabled = v),
                new SliderRow("Scale", 0.1, 4.0, 0.1, v -> String.format("%.1f", v), () -> c.distanceTextScale, v -> c.distanceTextScale = (float) v),
                new ColorRow("Text Color", () -> c.distanceTextColor),
                new ChannelRow("Red", 16, () -> c.distanceTextColor, v -> c.distanceTextColor = v),
                new ChannelRow("Green", 8, () -> c.distanceTextColor, v -> c.distanceTextColor = v),
                new ChannelRow("Blue", 0, () -> c.distanceTextColor, v -> c.distanceTextColor = v),
                new SliderRow("Text Opacity", 0.01, 1.0, 0.01, v -> String.format("%.2f", v), () -> c.distanceTextOpacity, v -> c.distanceTextOpacity = v),
                new ToggleRow("Text Shadow", () -> c.distanceTextShadow, v -> c.distanceTextShadow = v),
                new StepperRow("Decimal Places", 1, 10, () -> c.distanceDecimalPlaces, v -> c.distanceDecimalPlaces = v)));

        rows.add(List.of(
                new ToggleRow("Enabled", () -> c.hitDistanceEnabled, v -> c.hitDistanceEnabled = v),
                new SliderRow("Scale", 0.1, 4.0, 0.1, v -> String.format("%.1f", v), () -> c.hitDistanceTextScale, v -> c.hitDistanceTextScale = (float) v),
                new ColorRow("Text Color", () -> c.hitDistanceTextColor),
                new ChannelRow("Red", 16, () -> c.hitDistanceTextColor, v -> c.hitDistanceTextColor = v),
                new ChannelRow("Green", 8, () -> c.hitDistanceTextColor, v -> c.hitDistanceTextColor = v),
                new ChannelRow("Blue", 0, () -> c.hitDistanceTextColor, v -> c.hitDistanceTextColor = v),
                new SliderRow("Text Opacity", 0.01, 1.0, 0.01, v -> String.format("%.2f", v), () -> c.hitDistanceTextOpacity, v -> c.hitDistanceTextOpacity = v),
                new ToggleRow("Text Shadow", () -> c.hitDistanceTextShadow, v -> c.hitDistanceTextShadow = v),
                new StepperRow("Decimal Places", 1, 10, () -> c.hitDistanceDecimalPlaces, v -> c.hitDistanceDecimalPlaces = v)));

        rows.add(List.of(
                new ToggleRow("Enabled", () -> c.averageHitEnabled, v -> c.averageHitEnabled = v),
                new StepperRow("Hits Counted", 2, 1024, () -> c.numberOfHitsCounted, v -> c.numberOfHitsCounted = v),
                new SliderRow("Scale", 0.1, 4.0, 0.1, v -> String.format("%.1f", v), () -> c.averageHitTextScale, v -> c.averageHitTextScale = (float) v),
                new ColorRow("Text Color", () -> c.averageHitTextColor),
                new ChannelRow("Red", 16, () -> c.averageHitTextColor, v -> c.averageHitTextColor = v),
                new ChannelRow("Green", 8, () -> c.averageHitTextColor, v -> c.averageHitTextColor = v),
                new ChannelRow("Blue", 0, () -> c.averageHitTextColor, v -> c.averageHitTextColor = v),
                new SliderRow("Text Opacity", 0.01, 1.0, 0.01, v -> String.format("%.2f", v), () -> c.averageHitTextOpacity, v -> c.averageHitTextOpacity = v),
                new ToggleRow("Text Shadow", () -> c.averageHitTextShadow, v -> c.averageHitTextShadow = v),
                new StepperRow("Decimal Places", 1, 10, () -> c.averageHitDecimalPlaces, v -> c.averageHitDecimalPlaces = v)));
    }

    /* ----- state ----- */

    /// Moves the menu to the other side of the screen
    public void flip() {
        rightSide = !rightSide;
    }

    public boolean isConfirmingReset() {
        return confirmingReset;
    }

    public void cancelReset() {
        confirmingReset = false;
    }

    /* ----- geometry ----- */

    private static Font font() {
        return Minecraft.getInstance().font;
    }

    private int width() {
        return Math.clamp(contentWidth, MIN_WIDTH, Math.max(MIN_WIDTH, Math.min(MAX_WIDTH, screen.width / 2)));
    }

    private int totalWidth() {
        return STRIP_W + (expanded ? width() : 0);
    }

    private int left(float progress) {
        return Math.round(progress * (screen.width - totalWidth()));
    }

    /// Input always uses the docked (target) position, so the menu sliding past the cursor can't trigger anything
    private int left() {
        return left(rightSide ? 1 : 0);
    }

    private int stripX(int left) {
        return rightSide ? left + totalWidth() - STRIP_W : left;
    }

    private int contentX(int left) {
        return rightSide ? left : left + STRIP_W;
    }

    public boolean contains(double mx, double my) {
        int left = left();
        return mx >= left && mx < left + totalWidth() && my >= 0 && my < screen.height;
    }

    private String[] tabLabels() {
        int needed = TOGGLE_H;
        for (String name : TAB_NAMES) needed += font().width(name) + TAB_PAD;
        return needed <= screen.height ? TAB_NAMES : TAB_SHORT_NAMES;
    }

    private int tabTop(int i) {
        String[] labels = tabLabels();
        int y = TOGGLE_H;
        for (int t = 0; t < i; t++) y += font().width(labels[t]) + TAB_PAD;
        return y;
    }

    private int tabHeight(int i) {
        return font().width(tabLabels()[i]) + TAB_PAD;
    }

    private int viewTop()    { return HEADER_H; }
    private int viewBottom() { return screen.height - RESET_H - 6; }

    /// Index of the last row that can be scrolled to the top while still filling the view
    private int maxScroll(int tab) {
        List<Row> list = rows.get(tab);
        int space = viewBottom() - viewTop();
        int first = list.size();
        while (first > 0 && space - list.get(first - 1).height() >= 0) {
            space -= list.get(first - 1).height();
            first--;
        }
        return first;
    }

    private int rowX(int left)  { return contentX(left) + ROW_INSET; }
    private int rowW()          { return width() - ROW_INSET * 2 - 2; }

    private int resetY() { return screen.height - RESET_H - 3; }

    /* ----- rendering ----- */

    public void render(GuiGraphicsExtractor g, int mouseX, int mouseY) {
        long now = System.nanoTime();
        float step = (now - lastFrameNanos) / 1_000_000f / SLIDE_MS;
        lastFrameNanos = now;
        float target = rightSide ? 1 : 0;
        sideProgress = sideProgress < target ? Math.min(target, sideProgress + step) : Math.max(target, sideProgress - step);

        int left = left(sideProgress);
        int selected = screen.selected();
        Font font = font();

        // tab strip
        int stripX = stripX(left);
        g.fill(stripX, 0, stripX + STRIP_W, screen.height, TAB_BG);
        boolean hoverToggle = mouseX >= stripX && mouseX < stripX + STRIP_W && mouseY >= 0 && mouseY < TOGGLE_H && !confirmingReset;
        String arrow = expanded == rightSide ? ">" : "<";      // points the way the menu will move
        g.text(font, arrow, stripX + (STRIP_W - font.width(arrow)) / 2 + 1, (TOGGLE_H - font.lineHeight) / 2 + 1, hoverToggle ? 0xFFFFFFFF : TEXT_DIM, false);

        String[] labels = tabLabels();
        for (int i = 0; i < 3; i++) {
            int top = tabTop(i), h = tabHeight(i);
            boolean isSelected = i == selected;
            boolean hover = !confirmingReset && mouseX >= stripX && mouseX < stripX + STRIP_W && mouseY >= top && mouseY < top + h;
            if (isSelected || hover) g.fill(stripX, top, stripX + STRIP_W, top + h, isSelected ? TAB_SELECTED_BG : 0xF0242424);
            g.fill(stripX, top + h - 1, stripX + STRIP_W, top + h, 0xFF000000);

            int color = !HUDConfig.isEnabled(i) ? TEXT_DISABLED : isSelected ? 0xFFFFFFFF : TEXT_DIM;
            g.pose().pushMatrix();
            g.pose().translate(stripX + STRIP_W / 2f, top + h / 2f);
            g.pose().rotate((float) (rightSide ? Math.PI / 2 : -Math.PI / 2));   // text runs along the screen edge
            g.text(font, labels[i], -font.width(labels[i]) / 2, -font.lineHeight / 2 + 1, color, false);
            g.pose().popMatrix();
        }

        if (!expanded) return;

        // content
        int contentX = contentX(left), w = width();
        g.fill(contentX, 0, contentX + w, screen.height, BG);
        int edgeX = rightSide ? contentX + w - 1 : contentX;
        g.fill(edgeX, 0, edgeX + 1, screen.height, 0xFF000000);

        boolean enabled = HUDConfig.isEnabled(selected);
        g.text(font, TAB_NAMES[selected], contentX + ROW_INSET, 5, enabled ? 0xFFFFFFFF : TEXT_DISABLED, true);
        if (!enabled) {
            String off = "(disabled)";
            g.text(font, off, contentX + w - ROW_INSET - font.width(off), 5, TEXT_DISABLED, false);
        }
        g.fill(contentX + ROW_INSET, HEADER_H - 3, contentX + w - ROW_INSET, HEADER_H - 2, 0xFF404040);

        List<Row> list = rows.get(selected);
        scroll[selected] = Math.clamp(scroll[selected], 0, maxScroll(selected));
        int y = viewTop(), x = rowX(left), rw = rowW();
        boolean interactive = !confirmingReset && !resizing;
        for (int r = scroll[selected]; r < list.size(); r++) {
            Row row = list.get(r);
            if (y + row.height() > viewBottom()) break;
            row.render(g, x, y, rw, interactive ? mouseX : -1, interactive ? mouseY : -1, enabled);
            y += row.height();
        }

        // scrollbar on the side away from the tabs
        int max = maxScroll(selected);
        if (max > 0) {
            int trackTop = viewTop(), trackH = viewBottom() - viewTop();
            int thumbH = Math.max(10, trackH * (list.size() - max) / list.size());
            int thumbY = trackTop + (trackH - thumbH) * scroll[selected] / max;
            int barX = rightSide ? contentX + 2 : contentX + w - 4;
            g.fill(barX, trackTop, barX + 2, trackTop + trackH, 0x40FFFFFF);
            g.fill(barX, thumbY, barX + 2, thumbY + thumbH, 0xC0FFFFFF);
        }

        // reset button
        int ry = resetY();
        boolean hoverReset = interactive && mouseX >= x && mouseX < x + rw && mouseY >= ry && mouseY < ry + RESET_H;
        drawButton(g, x, ry, rw, RESET_H, "Reset Position", hoverReset, 0xFF3A3A3A);

        // resize handle
        int handleX = handleX(left);
        boolean hoverHandle = resizing || (!confirmingReset && mouseX >= handleX && mouseX < handleX + HANDLE_W && mouseY >= 0 && mouseY < screen.height);
        if (hoverHandle) g.fill(handleX, 0, handleX + HANDLE_W, screen.height, 0x60FFFFFF);
    }

    /// The edge of the content that faces the middle of the screen
    private int handleX(int left) {
        return rightSide ? contentX(left) : contentX(left) + width() - HANDLE_W;
    }

    /// Drawn after everything else, so the popup covers the HUD boxes too
    public void renderPopup(GuiGraphicsExtractor g, int mouseX, int mouseY) {
        if (!confirmingReset) return;
        Font font = font();
        g.fill(0, 0, screen.width, screen.height, 0x90000000);

        String line1 = "Reset the position of";
        String line2 = TAB_NAMES[screen.selected()] + "?";
        int pw = Math.min(screen.width - 20, Math.max(160, Math.max(font.width(line1), font.width(line2)) + 20));
        int ph = 62, px = (screen.width - pw) / 2, py = (screen.height - ph) / 2;
        g.fill(px - 1, py - 1, px + pw + 1, py + ph + 1, 0xFF505050);
        g.fill(px, py, px + pw, py + ph, 0xFF151515);
        g.text(font, line1, px + (pw - font.width(line1)) / 2, py + 8, TEXT, false);
        g.text(font, line2, px + (pw - font.width(line2)) / 2, py + 19, 0xFFFFFFFF, false);

        int bw = (pw - 30) / 2, by = py + ph - 24;
        drawButton(g, px + 10, by, bw, 16, "Reset", inside(mouseX, mouseY, px + 10, by, bw, 16), 0xFF7A2A2A);
        drawButton(g, px + 20 + bw, by, bw, 16, "Cancel", inside(mouseX, mouseY, px + 20 + bw, by, bw, 16), 0xFF3A3A3A);
    }

    private boolean popupClick(double mx, double my) {
        int pw = Math.min(screen.width - 20, Math.max(160, Math.max(font().width("Reset the position of"), font().width(TAB_NAMES[screen.selected()] + "?")) + 20));
        int ph = 62, px = (screen.width - pw) / 2, py = (screen.height - ph) / 2;
        int bw = (pw - 30) / 2, by = py + ph - 24;
        if (inside(mx, my, px + 10, by, bw, 16)) {
            screen.resetPosition(screen.selected());
            confirmingReset = false;
        } else if (inside(mx, my, px + 20 + bw, by, bw, 16) || !inside(mx, my, px, py, pw, ph)) {
            confirmingReset = false;
        }
        return true;    // the popup is modal
    }

    private static void drawButton(GuiGraphicsExtractor g, int x, int y, int w, int h, String text, boolean hover, int color) {
        Font font = font();
        g.fill(x, y, x + w, y + h, hover ? brighten(color) : color);
        g.fill(x, y + h - 1, x + w, y + h, 0xFF000000);
        g.text(font, text, x + (w - font.width(text)) / 2, y + (h - font.lineHeight) / 2 + 1, 0xFFFFFFFF, true);
    }

    private static int brighten(int color) {
        int r = Math.min(255, ((color >> 16) & 0xFF) + 30);
        int gr = Math.min(255, ((color >> 8) & 0xFF) + 30);
        int b = Math.min(255, (color & 0xFF) + 30);
        return (color & 0xFF000000) | (r << 16) | (gr << 8) | b;
    }

    private static boolean inside(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    /* ----- input ----- */

    /// Returns true if the click landed on the menu (or a popup is open), so the boxes underneath must ignore it
    public boolean mouseClicked(double mx, double my, int button) {
        if (confirmingReset) return popupClick(mx, my);
        if (!contains(mx, my)) return false;

        int left = left();
        int stripX = stripX(left);
        if (mx >= stripX && mx < stripX + STRIP_W) {
            if (button != 0) return true;
            if (my < TOGGLE_H) {
                expanded = !expanded;
                return true;
            }
            for (int i = 0; i < 3; i++) {
                int top = tabTop(i);
                if (my >= top && my < top + tabHeight(i)) {
                    screen.select(i);
                    expanded = true;
                    return true;
                }
            }
            return true;
        }

        int handleX = handleX(left);
        if (button == 0 && mx >= handleX && mx < handleX + HANDLE_W) {
            resizing = true;
            return true;
        }

        int x = rowX(left), rw = rowW();
        if (button == 0 && inside(mx, my, x, resetY(), rw, RESET_H)) {
            confirmingReset = true;
            return true;
        }

        int selected = screen.selected();
        List<Row> list = rows.get(selected);
        int y = viewTop();
        for (int r = scroll[selected]; r < list.size(); r++) {
            Row row = list.get(r);
            if (y + row.height() > viewBottom()) break;
            if (my >= y && my < y + row.height()) {
                if (row.click(x, y, rw, mx, my, button) && row instanceof SliderRow slider) {
                    activeSlider = slider;
                    activeSliderX = x;
                    activeSliderW = rw;
                }
                break;
            }
            y += row.height();
        }
        return true;
    }

    public boolean mouseDragged(double mx, double my) {
        if (resizing) {
            int contentX = contentX(left());
            contentWidth = (int) (rightSide ? contentX + width() - mx : mx - contentX);
            contentWidth = width();      // clamp now, so the menu doesn't "remember" an impossible width
            return true;
        }
        if (activeSlider != null) {
            activeSlider.drag(activeSliderX, activeSliderW, mx);
            return true;
        }
        return false;
    }

    public void mouseReleased() {
        resizing = false;
        activeSlider = null;
    }

    public boolean mouseScrolled(double mx, double my, double amount) {
        if (confirmingReset) return true;
        if (!contains(mx, my)) return false;
        if (expanded && amount != 0) {
            int selected = screen.selected();
            scroll[selected] = Math.clamp(scroll[selected] - (int) Math.signum(amount), 0, maxScroll(selected));
        }
        return true;
    }

    /* ----- option rows ----- */

    private abstract static class Row {
        final String name;

        Row(String name) {
            this.name = name;
        }

        abstract int height();

        abstract void render(GuiGraphicsExtractor g, int x, int y, int w, int mouseX, int mouseY, boolean enabled);

        boolean click(int x, int y, int w, double mx, double my, int button) {
            return false;
        }

        static int nameColor(boolean enabled) {
            return enabled ? TEXT : TEXT_DISABLED;
        }
    }

    private static class ToggleRow extends Row {
        private final BooleanSupplier get;
        private final Consumer<Boolean> set;

        ToggleRow(String name, BooleanSupplier get, Consumer<Boolean> set) {
            super(name);
            this.get = get;
            this.set = set;
        }

        @Override int height() { return 16; }

        @Override
        void render(GuiGraphicsExtractor g, int x, int y, int w, int mouseX, int mouseY, boolean enabled) {
            Font font = font();
            g.text(font, name, x, y + 4, nameColor(enabled), false);
            boolean on = get.getAsBoolean();
            int pw = 28, px = x + w - pw;
            int color = on ? 0xFF2E7D32 : 0xFF8E2424;
            boolean hover = inside(mouseX, mouseY, x, y, w, height());
            g.fill(px, y + 2, px + pw, y + 14, hover ? brighten(color) : color);
            String text = on ? "ON" : "OFF";
            g.text(font, text, px + (pw - font.width(text)) / 2, y + 4, 0xFFFFFFFF, true);
        }

        @Override
        boolean click(int x, int y, int w, double mx, double my, int button) {
            if (button == 0) set.accept(!get.getAsBoolean());
            return false;
        }
    }

    private static class SliderRow extends Row {
        private final double min, max, step;
        private final DoubleFunction<String> format;
        private final DoubleSupplier get;
        private final DoubleConsumer set;

        SliderRow(String name, double min, double max, double step, DoubleFunction<String> format, DoubleSupplier get, DoubleConsumer set) {
            super(name);
            this.min = min;
            this.max = max;
            this.step = step;
            this.format = format;
            this.get = get;
            this.set = set;
        }

        @Override int height() { return 26; }

        @Override
        void render(GuiGraphicsExtractor g, int x, int y, int w, int mouseX, int mouseY, boolean enabled) {
            Font font = font();
            double value = get.getAsDouble();
            g.text(font, name, x, y + 2, nameColor(enabled), false);
            String text = format.apply(value);
            g.text(font, text, x + w - font.width(text), y + 2, TEXT_DIM, false);

            int trackY = y + 15;
            int knob = x + (int) Math.round((Math.clamp(value, min, max) - min) / (max - min) * (w - 4));
            boolean hover = inside(mouseX, mouseY, x, y + 12, w, 12);
            g.fill(x, trackY, x + w, trackY + 4, 0xFF2A2A2A);
            g.fill(x, trackY, knob + 2, trackY + 4, ACCENT);
            g.fill(knob, trackY - 3, knob + 4, trackY + 7, hover ? 0xFFFFFFFF : 0xFFC8C8C8);
        }

        @Override
        boolean click(int x, int y, int w, double mx, double my, int button) {
            if (button != 0 || my < y + 11) return false;
            drag(x, w, mx);
            return true;
        }

        void drag(int x, int w, double mx) {
            double fraction = Math.clamp((mx - x - 2) / (w - 4), 0.0, 1.0);
            double value = min + Math.round(fraction * (max - min) / step) * step;
            set.accept(Math.clamp(value, min, max));
        }
    }

    /// One 0-255 channel of an RGB color, at the given bit shift (16 = red, 8 = green, 0 = blue)
    private static class ChannelRow extends SliderRow {
        ChannelRow(String name, int shift, IntSupplier get, IntConsumer set) {
            super(name, 0, 255, 1, v -> String.valueOf((int) v),
                    () -> (get.getAsInt() >> shift) & 0xFF,
                    v -> set.accept((get.getAsInt() & ~(0xFF << shift) & 0xFFFFFF) | ((int) Math.round(v) << shift)));
        }
    }

    /// Label and preview swatch above the Red/Green/Blue sliders
    private static class ColorRow extends Row {
        private final IntSupplier get;

        ColorRow(String name, IntSupplier get) {
            super(name);
            this.get = get;
        }

        @Override int height() { return 14; }

        @Override
        void render(GuiGraphicsExtractor g, int x, int y, int w, int mouseX, int mouseY, boolean enabled) {
            Font font = font();
            int rgb = get.getAsInt() & 0xFFFFFF;
            g.text(font, name, x, y + 3, nameColor(enabled), false);
            String hex = String.format("#%06X", rgb);
            int swatchX = x + w - 14;
            g.text(font, hex, swatchX - 4 - font.width(hex), y + 3, TEXT_DIM, false);
            g.fill(swatchX - 1, y + 1, swatchX + 15, y + 13, 0xFF000000);
            g.fill(swatchX, y + 2, swatchX + 14, y + 12, 0xFF000000 | rgb);
        }
    }

    /// Integer field with - and + buttons. Left click steps by 1, right click by 10.
    private static class StepperRow extends Row {
        private static final int BUTTON = 12;
        private final int min, max;
        private final IntSupplier get;
        private final IntConsumer set;

        StepperRow(String name, int min, int max, IntSupplier get, IntConsumer set) {
            super(name);
            this.min = min;
            this.max = max;
            this.get = get;
            this.set = set;
        }

        @Override int height() { return 16; }

        @Override
        void render(GuiGraphicsExtractor g, int x, int y, int w, int mouseX, int mouseY, boolean enabled) {
            Font font = font();
            g.text(font, name, x, y + 4, nameColor(enabled), false);
            String value = String.valueOf(get.getAsInt());
            int valueW = Math.max(font.width("0000"), font.width(value)) + 4;
            int plusX = x + w - BUTTON, valueX = plusX - valueW, minusX = valueX - BUTTON;
            drawButton(g, minusX, y + 2, BUTTON, BUTTON, "-", inside(mouseX, mouseY, minusX, y + 2, BUTTON, BUTTON), 0xFF3A3A3A);
            drawButton(g, plusX, y + 2, BUTTON, BUTTON, "+", inside(mouseX, mouseY, plusX, y + 2, BUTTON, BUTTON), 0xFF3A3A3A);
            g.text(font, value, valueX + (valueW - font.width(value)) / 2, y + 4, 0xFFFFFFFF, false);
        }

        @Override
        boolean click(int x, int y, int w, double mx, double my, int button) {
            int amount = button == 1 ? 10 : 1;
            Font font = font();
            int valueW = Math.max(font.width("0000"), font.width(String.valueOf(get.getAsInt()))) + 4;
            int plusX = x + w - BUTTON, minusX = plusX - valueW - BUTTON;
            if (inside(mx, my, plusX, y + 2, BUTTON, BUTTON)) set.accept(Math.min(max, get.getAsInt() + amount));
            else if (inside(mx, my, minusX, y + 2, BUTTON, BUTTON)) set.accept(Math.max(min, get.getAsInt() - amount));
            return false;
        }
    }
}
