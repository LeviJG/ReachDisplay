package net.blueskiez77.reach_display.config;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;

import java.lang.reflect.Method;
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

    // Mouse buttons must be compared with InputConstants (like YACL does): Minecraft 26.3 no longer uses the
    // old GLFW numbers (0 = left, 1 = right), so hard-coded 0/1 never matched and no option could be clicked.
    private static final int LEFT_CLICK = InputConstants.MOUSE_BUTTON_LEFT, RIGHT_CLICK = InputConstants.MOUSE_BUTTON_RIGHT;

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
    private ColorRow focusedColor;                 // hex field being typed in, if any

    public LayoutPanel(HUDConfig screen) {
        this.screen = screen;
        DisplayConfig c = DisplayConfig.INSTANCE;

        rows.add(List.of(
                new ToggleRow("Enabled", () -> c.distanceEnabled, v -> c.distanceEnabled = v),
                new SliderRow("Scale", 0.1, 4.0, 0.1, v -> String.format("%.1f", v), () -> c.distanceTextScale, v -> c.distanceTextScale = (float) v),
                new ColorRow("Text Color", () -> c.distanceTextColor, v -> c.distanceTextColor = v),
                new ChannelRow("Red", 16, () -> c.distanceTextColor, v -> c.distanceTextColor = v),
                new ChannelRow("Green", 8, () -> c.distanceTextColor, v -> c.distanceTextColor = v),
                new ChannelRow("Blue", 0, () -> c.distanceTextColor, v -> c.distanceTextColor = v),
                new SliderRow("Text Opacity", 0.01, 1.0, 0.01, v -> String.format("%.2f", v), () -> c.distanceTextOpacity, v -> c.distanceTextOpacity = v),
                new ToggleRow("Text Shadow", () -> c.distanceTextShadow, v -> c.distanceTextShadow = v),
                new StepperRow("Decimal Places", 1, 10, () -> c.distanceDecimalPlaces, v -> c.distanceDecimalPlaces = v)));

        rows.add(List.of(
                new ToggleRow("Enabled", () -> c.hitDistanceEnabled, v -> c.hitDistanceEnabled = v),
                new SliderRow("Scale", 0.1, 4.0, 0.1, v -> String.format("%.1f", v), () -> c.hitDistanceTextScale, v -> c.hitDistanceTextScale = (float) v),
                new ColorRow("Text Color", () -> c.hitDistanceTextColor, v -> c.hitDistanceTextColor = v),
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
                new ColorRow("Text Color", () -> c.averageHitTextColor, v -> c.averageHitTextColor = v),
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
        return Math.clamp(contentWidth, MIN_WIDTH, Math.clamp(screen.width / 2, MIN_WIDTH, MAX_WIDTH));
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
        int pw = Math.clamp(Math.max(font.width(line1), font.width(line2)) + 20, 160, screen.width - 20);
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
        int pw = Math.clamp(Math.max(font().width("Reset the position of"), font().width(TAB_NAMES[screen.selected()] + "?")) + 20, 160, screen.width - 20);
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
        setFocusedColor(null);                     // any click unfocuses the hex field; clicking it again refocuses it
        if (confirmingReset) return popupClick(mx, my);
        if (!contains(mx, my)) return false;

        int left = left();
        int stripX = stripX(left);
        if (mx >= stripX && mx < stripX + STRIP_W) {
            if (button != LEFT_CLICK) return true;
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
        if (button == LEFT_CLICK && mx >= handleX && mx < handleX + HANDLE_W) {
            resizing = true;
            return true;
        }

        int x = rowX(left), rw = rowW();
        if (button == LEFT_CLICK && inside(mx, my, x, resetY(), rw, RESET_H)) {
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

    public boolean mouseDragged(double mx) {
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

    private void setFocusedColor(ColorRow row) {
        if (row == focusedColor) return;
        focusedColor = row;
        setTextInputFocus(row != null);
    }

    /// Unfocuses the hex field, e.g. when the screen closes
    public void clearFocus() {
        setFocusedColor(null);
    }

    /// Minecraft 26.3 only delivers typed characters while a text field has told if it's focused (YACL does the same). Looked up by reflection, so versions without the method simply skip it instead of crashing.
    private void setTextInputFocus(boolean focused) {
        try {
            for (Method method : Minecraft.class.getMethods()) {
                Class<?>[] params = method.getParameterTypes();
                if (method.getName().equals("onTextInputFocusChange") && params.length == 2
                        && params[0].isInstance(screen) && params[1] == boolean.class) {
                    method.invoke(Minecraft.getInstance(), screen, focused);
                    return;
                }
            }
        } catch (ReflectiveOperationException | RuntimeException ignored) {
        }
    }

    /// Keys go to the focused hex field. Returns true if it used the key, so the screen must ignore it.
    public boolean keyPressed(KeyEvent event) {
        return focusedColor != null && focusedColor.keyPressed(event);
    }

    public boolean charTyped(CharacterEvent event) {
        return focusedColor != null && focusedColor.charTyped(event);
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
            if (button == LEFT_CLICK) set.accept(!get.getAsBoolean());
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
            if (button != LEFT_CLICK || my < y + 11) return false;
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

    /// Hex color field, copied from YACL's ColorController: the value shows as #RRGGBB with red, green and
    /// blue digits, and typing overwrites one digit at a time. Illegal input is silently ignored.
    private class ColorRow extends Row {
        private static final int RED = 0xFFFF5555, GREEN = 0xFF55FF55, BLUE = 0xFF5555FF;   // ChatFormatting.RED/GREEN/BLUE
        private static final int Y_PADDING = 2;
        private static final String HEX_CHARS = "0123456789abcdef";

        private final IntSupplier get;
        private final IntConsumer set;
        private String inputField = "#000000";
        private int caretPos, previousCaretPos, selectionLength;
        private long caretMovedAt;

        ColorRow(String name, IntSupplier get, IntConsumer set) {
            super(name);
            this.get = get;
            this.set = set;
        }

        @Override int height() { return 20; }

        private boolean focused() {
            return focusedColor == this;
        }

        private String hex() {
            return String.format("#%06X", get.getAsInt() & 0xFFFFFF);
        }

        @Override
        void render(GuiGraphicsExtractor g, int x, int y, int w, int mouseX, int mouseY, boolean enabled) {
            Font font = font();
            if (!focused()) inputField = hex();          // follow the sliders while not typing

            int xLimit = x + w, centerY = y + height() / 2;
            boolean hovered = inside(mouseX, mouseY, x, y, w, height()) || focused();
            int textY = (int) (y + height() / 2f - font.lineHeight / 2f);
            g.text(font, name, x, textY, nameColor(enabled), true);

            String text = hex();
            int textX = xLimit - font.width(text);
            int fieldW = Math.max(6, font.width(text));
            int fieldX = xLimit - fieldW, fieldY = centerY - font.lineHeight / 2, fieldYLimit = fieldY + font.lineHeight;

            // Preview square: small at the right edge, or bigger and left of the text while hovered
            int previewSize = (height() - Y_PADDING * 2) / 2;
            int previewX = xLimit - previewSize, previewY = centerY - previewSize / 2;

            if (hovered) {
                previewX -= fieldW + 8;
                previewY -= 2;
                previewSize += 4;

                drawColoredHex(g, font, text, textX, textY);
                g.fill(fieldX, fieldYLimit, xLimit, fieldYLimit + 1, 0xFFFFFFFF);                // underline
                g.fill(fieldX + 1, fieldYLimit + 1, xLimit + 1, fieldYLimit + 2, 0xFF404040);   // and its shadow

                if (focused()) {
                    caretPos = Math.min(caretPos, text.length());
                    int caretX = textX + font.width(text.substring(0, caretPos));
                    if (selectionLength != 0) {
                        int selectionX = textX + font.width(text.substring(0, caretPos + selectionLength));
                        g.fill(caretX, fieldY - 2, selectionX, fieldYLimit - 1, 0x803030FF);
                    }
                    long now = System.currentTimeMillis();
                    if (caretPos != previousCaretPos) {
                        previousCaretPos = caretPos;
                        caretMovedAt = now;
                    }
                    if ((now - caretMovedAt) % 1000 <= 550) g.fill(caretX, fieldY - 2, caretX + 1, fieldYLimit - 1, 0xFFFFFFFF);
                }
            }

            int rgb = get.getAsInt() & 0xFFFFFF;
            g.fill(previewX, previewY, previewX + previewSize, previewY + previewSize, 0xFF000000 | rgb);
            boolean overPreview = inside(mouseX, mouseY, previewX, previewY, previewSize, previewSize);
            outline(g, previewX, previewY, previewSize, previewSize, overPreview ? highlightedOutlineColor(rgb) : 0xFF000000);
        }

        private static void drawColoredHex(GuiGraphicsExtractor g, Font font, String text, int x, int y) {
            int[] colors = { 0xFFFFFFFF, RED, GREEN, BLUE };
            String[] parts = { text.substring(0, 1), text.substring(1, 3), text.substring(3, 5), text.substring(5, 7) };
            for (int i = 0; i < parts.length; i++) {
                g.text(font, parts[i], x, y, colors[i], true);
                x += font.width(parts[i]);
            }
        }

        private static void outline(GuiGraphicsExtractor g, int x, int y, int w, int h, int color) {
            g.fill(x, y, x + w, y + 1, color);
            g.fill(x, y + h - 1, x + w, y + h, color);
            g.fill(x, y + 1, x + 1, y + h - 1, color);
            g.fill(x + w - 1, y + 1, x + w, y + h - 1, color);
        }

        /// White, or light gray if the color itself is almost white, so the outline stays visible
        private static int highlightedOutlineColor(int rgb) {
            float[] hsb = java.awt.Color.RGBtoHSB((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF, null);
            return hsb[1] < 0.1f && hsb[2] > 0.9f ? 0xFFC6C6C6 : 0xFFFFFFFF;
        }

        @Override
        boolean click(int x, int y, int w, double mx, double my, int button) {
            if (button != LEFT_CLICK) return false;
            setFocusedColor(this);
            caretPos = Math.max(1, caretPos);
            setSelectionLength();
            return false;
        }

        /// The digit after the caret is highlighted, since typing replaces it
        private void setSelectionLength() {
            selectionLength = caretPos < inputField.length() && caretPos > 0 ? 1 : 0;
        }

        boolean keyPressed(KeyEvent event) {
            int prevSelectionLength = selectionLength;
            selectionLength = 0;
            if (handleKey(event)) {
                caretPos = Math.max(1, caretPos);       // never in front of the #
                setSelectionLength();
                return true;
            }
            selectionLength = prevSelectionLength;
            return false;
        }

        private boolean handleKey(KeyEvent event) {
            int key = event.key();
            if (key == InputConstants.KEY_ESCAPE || key == InputConstants.KEY_RETURN) {
                setFocusedColor(null);
                return true;
            }
            if (key == InputConstants.KEY_LEFT) {
                if (event.hasControlDown()) caretPos = 0;
                else if (caretPos > 0) caretPos--;
                return true;
            }
            if (key == InputConstants.KEY_RIGHT) {
                if (event.hasControlDown()) caretPos = inputField.length();
                else if (caretPos < inputField.length()) caretPos++;
                return true;
            }
            if (key == InputConstants.KEY_BACKSPACE) {
                // Digits are never removed, only reset to F
                if (caretPos > 1) {
                    resetDigit(caretPos - 1);
                    caretPos--;
                }
                return true;
            }
            if (key == InputConstants.KEY_DELETE) {
                if (caretPos >= 1 && caretPos < inputField.length()) resetDigit(caretPos);
                return true;
            }
            if (key == InputConstants.KEY_END) {
                caretPos = inputField.length();
                return true;
            }
            if (key == InputConstants.KEY_HOME) {
                caretPos = 0;
                return true;
            }
            if (event.isPaste()) {
                write(Minecraft.getInstance().keyboardHandler.getClipboard());
                return true;
            }
            return false;
        }

        boolean charTyped(CharacterEvent event) {
            write(event.codepointAsString());
            return true;
        }

        /// Overwrites digits from the caret onwards. Anything that isn't hex is ignored, without a warning.
        private void write(String string) {
            string = string.strip();
            if (string.startsWith("#")) string = string.substring(1);
            else if (string.startsWith("0x") || string.startsWith("0X")) string = string.substring(2);
            for (char chr : string.toCharArray()) {
                if (HEX_CHARS.indexOf(Character.toLowerCase(chr)) < 0) return;
            }
            if (caretPos == 0) return;

            String trimmed = string.substring(0, Math.min(inputField.length() - caretPos, string.length()));
            inputField = new StringBuilder(inputField).replace(caretPos, caretPos + trimmed.length(), trimmed).toString();
            apply();
            caretPos += trimmed.length();
            setSelectionLength();
        }

        /// Backspace and Delete never remove a digit, they reset it to F
        private void resetDigit(int index) {
            StringBuilder builder = new StringBuilder(inputField);
            builder.setCharAt(index, 'F');
            inputField = builder.toString();
            apply();
        }

        /// Applies the field straight away, like YACL's instant apply
        private void apply() {
            set.accept(Integer.parseInt(inputField.substring(1, 7), 16));
            inputField = hex();
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
            int amount = button == RIGHT_CLICK ? 10 : 1;
            Font font = font();
            int valueW = Math.max(font.width("0000"), font.width(String.valueOf(get.getAsInt()))) + 4;
            int plusX = x + w - BUTTON, minusX = plusX - valueW - BUTTON;
            if (inside(mx, my, plusX, y + 2, BUTTON, BUTTON)) set.accept(Math.min(max, get.getAsInt() + amount));
            else if (inside(mx, my, minusX, y + 2, BUTTON, BUTTON)) set.accept(Math.max(min, get.getAsInt() - amount));
            return false;
        }
    }
}
