package net.blueskiez77.reach_display.config;

import net.blueskiez77.reach_display.data.SharedData;
import net.blueskiez77.reach_display.utils.CustomRender;
import net.blueskiez77.reach_display.utils.ReachCalculation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;

import java.util.function.Supplier;


public class HUDConfig extends Screen {

    private static final int SELECTION_COLOR = 0xFFFFD24A;
    private static final int DISABLED_TEXT_COLOR = 0xFF6A6A6A;

    // Options edited in the side menu change DisplayConfig directly, so the screen we return to is rebuilt rather than reused: a reused YACL screen would still hold the old values and write them back on save.
    private final Supplier<Screen> returnTo;
    private final LayoutPanel panel;

    private final double[] SAMPLE = { 3.1415926535, 2.7182818284, 1.6180339887 };

    private final double[] tempX = new double[3];
    private final double[] tempY = new double[3];

    private int selectedIndex = 0;
    private int draggingIndex = -1;
    private double grabOffsetX, grabOffsetY;
    private boolean cursorInPanel;

    static boolean isEnabled(int i) {
        DisplayConfig c = DisplayConfig.INSTANCE;
        return switch (i) {
            case 0 -> c.distanceEnabled;
            case 1 -> c.hitDistanceEnabled;
            default -> c.averageHitEnabled;
        };
    }

    private String label(int i) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        DisplayConfig c = DisplayConfig.INSTANCE;

        int places = switch (i) {
            case 0 -> c.distanceDecimalPlaces;
            case 1 -> c.hitDistanceDecimalPlaces;
            default -> c.averageHitDecimalPlaces;
        };

        if (player == null) return format(SAMPLE[i], places);

        return switch (i) {
            case 0 -> {
                if (mc.hitResult instanceof EntityHitResult ehr) {
                    double d = ReachCalculation.MeasureReach(player, ehr.getEntity());
                    yield d >= 0 ? format(d, places) : format(SAMPLE[i], places);
                }
                yield format(SAMPLE[i], places);
            }
            case 1 -> {
                double d = SharedData.getInstance().getDistance();
                yield d > 0 ? format(d, places) : format(SAMPLE[i], places);
            }
            default -> {
                double d = SharedData.getInstance().getAverageDistance();
                yield d > 0 ? format(d, places) : format(SAMPLE[i], places);
            }
        };
    }

    private static String format(double value, int places) {
        return CustomRender.getRoundedDouble(value, places);
    }

    private float scale(int i) {
        DisplayConfig c = DisplayConfig.INSTANCE;
        return switch (i) {
            case 0 -> c.distanceTextScale;
            case 1 -> c.hitDistanceTextScale;
            default -> c.averageHitTextScale;
        };
    }

    private boolean shadow(int i) {
        DisplayConfig c = DisplayConfig.INSTANCE;
        return switch (i) {
            case 0 -> c.distanceTextShadow;
            case 1 -> c.hitDistanceTextShadow;
            default -> c.averageHitTextShadow;
        };
    }

    public HUDConfig(Supplier<Screen> returnTo) {
        super(Component.literal("HUD Layout"));
        this.returnTo = returnTo;
        this.panel = new LayoutPanel(this);

        DisplayConfig c = DisplayConfig.INSTANCE;
        tempX[0] = c.distanceXOffset;    tempY[0] = c.distanceYOffset;
        tempX[1] = c.hitXOffset;         tempY[1] = c.hitYOffset;
        tempX[2] = c.averageHitXOffset;  tempY[2] = c.averageHitYOffset;
    }

    /* ----- selection (shared with the side menu) ----- */

    int selected() {
        return selectedIndex;
    }

    void select(int i) {
        selectedIndex = i;
    }

    void resetPosition(int i) {
        DisplayConfig defaults = new DisplayConfig();
        switch (i) {
            case 0 -> { tempX[0] = defaults.distanceXOffset;   tempY[0] = defaults.distanceYOffset; }
            case 1 -> { tempX[1] = defaults.hitXOffset;        tempY[1] = defaults.hitYOffset; }
            default -> { tempX[2] = defaults.averageHitXOffset; tempY[2] = defaults.averageHitYOffset; }
        }
    }

    /* ----- geometry helpers ----- */

    private int boxWidth(int i)  { return CustomRender.boxWidth(this.font, label(i), scale(i)); }
    private int boxHeight(int i) { return CustomRender.boxHeight(this.font, scale(i)); }

    private float boxX(int i) { return CustomRender.boxX(tempX[i], this.width, boxWidth(i)); }
    private float boxY(int i) { return CustomRender.boxY(tempY[i], this.height, boxHeight(i)); }
    private int pixelX(int i) { return (int) boxX(i); }
    private int pixelY(int i) { return (int) boxY(i); }
    private double ratioX(int i, double px) {
        int avail = Math.max(1, this.width - boxWidth(i) - CustomRender.MARGIN * 2);
        return Math.clamp((px - CustomRender.MARGIN) / avail, 0.0, 1.0);
    }
    private double ratioY(int i, double py) {
        int avail = Math.max(1, this.height - boxHeight(i) - CustomRender.MARGIN * 2);
        return Math.clamp((py - CustomRender.MARGIN) / avail, 0.0, 1.0);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partial) {
        g.fill(0, 0, this.width, this.height, 0x30101010);
        super.extractRenderState(g, mouseX, mouseY, partial);

        boolean popup = panel.isConfirmingReset();
        // The selected box is drawn last so it stays on top of any box it overlaps
        for (int n = 0; n < 3; n++) {
            int i = (selectedIndex + 1 + n) % 3;
            int x = pixelX(i), y = pixelY(i), w = boxWidth(i), h = boxHeight(i);
            boolean enabled = isEnabled(i);
            boolean isHovering = !popup && draggingIndex < 0 && !panel.contains(mouseX, mouseY) && posTest(i, mouseX, mouseY);

            int background = i == draggingIndex || isHovering ? 0xA0303030 : enabled ? 0x60000000 : 0x30000000;
            g.fill(x, y, x + w, y + h, background);
            if (i == selectedIndex) outline(g, x - 1, y - 1, w + 2, h + 2, SELECTION_COLOR);

            int color = enabled ? CustomRender.getARGBColor(i) : DISABLED_TEXT_COLOR;
            CustomRender.drawScaledText(g, label(i), boxX(i) + CustomRender.PADDING, boxY(i) + CustomRender.PADDING, scale(i), color, enabled && shadow(i));
        }

        panel.render(g, mouseX, mouseY);
        panel.renderPopup(g, mouseX, mouseY);
    }

    private static void outline(GuiGraphicsExtractor g, int x, int y, int w, int h, int color) {
        g.fill(x, y, x + w, y + 1, color);
        g.fill(x, y + h - 1, x + w, y + h, color);
        g.fill(x, y + 1, x + 1, y + h - 1, color);
        g.fill(x + w - 1, y + 1, x + w, y + h - 1, color);
    }

    private boolean posTest(int i, double mx, double my) {
        int x = pixelX(i), y = pixelY(i);
        return mx >= x && mx < x + boxWidth(i) && my >= y && my < y + boxHeight(i);
    }


    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubled) {
        if (panel.mouseClicked(event.x(), event.y(), event.button())) return true;
        if (event.button() == 0) {
            for (int n = 0; n < 3; n++) {           // reverse draw order: topmost wins
                int i = (selectedIndex + 3 - n) % 3;
                if (posTest(i, event.x(), event.y())) {
                    selectedIndex = i;
                    draggingIndex = i;
                    cursorInPanel = false;
                    grabOffsetX = event.x() - pixelX(i);
                    grabOffsetY = event.y() - pixelY(i);
                    return true;
                }
            }
        }
        return super.mouseClicked(event, doubled);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        if (draggingIndex >= 0 && event.button() == 0) {
            int i = draggingIndex;
            tempX[i] = ratioX(i, event.x() - grabOffsetX);
            tempY[i] = ratioY(i, event.y() - grabOffsetY);

            // Dragging into the menu sends it to the other side of the screen, where it stays until it's crossed again
            boolean inPanel = panel.contains(event.x(), event.y());
            if (inPanel && !cursorInPanel) panel.flip();
            cursorInPanel = inPanel;
            return true;
        }
        if (event.button() == 0 && panel.mouseDragged(event.x(), event.y())) return true;
        return super.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (event.button() == 0) {
            draggingIndex = -1;
            panel.mouseReleased();
        }
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (panel.mouseScrolled(mouseX, mouseY, scrollY)) return true;
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    /* ----- close: commit + save ----- */

    @Override
    public void onClose() {
        if (panel.isConfirmingReset()) {     // Esc closes the confirmation popup first
            panel.cancelReset();
            return;
        }

        DisplayConfig c = DisplayConfig.INSTANCE;

        c.distanceXOffset   = tempX[0];  c.distanceYOffset   = tempY[0];
        c.hitXOffset        = tempX[1];  c.hitYOffset        = tempY[1];
        c.averageHitXOffset = tempX[2];  c.averageHitYOffset = tempY[2];

        ConfigManager.save();
        Minecraft.getInstance().setScreenAndShow(returnTo.get());
    }
}
