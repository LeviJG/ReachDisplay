package net.blueskiez77.reach_display.config;

import net.blueskiez77.reach_display.data.SharedData;
import net.blueskiez77.reach_display.utils.ReachCalculation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;


public class HUDConfig extends Screen {

    public static class HudElement {
        public boolean enabled = true;
        public double posX = 0.5;
        public double posY = 0.0;

        //public HudElement() {}                                  // keep for Gson / default use
        public HudElement(double posX, double posY, boolean enabled) {
            this.posX = posX;
            this.posY = posY;
            this.enabled = enabled;
        }
    }

    private static final int MARGIN = 4;
    private static final int PADDING = 2;

    private final Screen parent;

    private final HudElement[] elements;
    private final double[] SAMPLE = { 3.1415926535, 2.7182818284, 1.6180339887 };

    private final double[] tempX = new double[3];
    private final double[] tempY = new double[3];

    private int draggingIndex = -1;
    private double grabOffsetX, grabOffsetY;   // where inside the box we grabbed

    private HudElement[] buildElements() {
        DisplayConfig displayConfig = DisplayConfig.INSTANCE;
        HudElement[] arr = new HudElement[3];
        arr[0] = new HudElement(displayConfig.distanceXOffset, displayConfig.distanceYOffset, displayConfig.distanceEnabled);
        arr[1] = new HudElement(displayConfig.hitXOffset, displayConfig.hitYOffset,  displayConfig.hitDistanceEnabled);
        arr[2] = new HudElement(displayConfig.averageHitXOffset, displayConfig.averageHitYOffset, displayConfig.averageHitEnabled);
        return arr;
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
        return String.format("%." + Math.max(0, places) + "f", value);
    }

    public HUDConfig(Screen parent) {
        super(Component.literal("HUD Layout"));
        this.parent = parent;
        this.elements = buildElements();
        for (int i = 0; i < 3; i++) {
            tempX[i] = elements[i].posX;
            tempY[i] = elements[i].posY;
        }
    }

    /* ----- geometry helpers ----- */

    private int boxWidth(int i)  { return this.font.width(label(i)) + PADDING * 2; }
    private int boxHeight()      { return this.font.lineHeight + PADDING * 2; }

    private int pixelX(int i) {
        int avail = Math.max(0, this.width - boxWidth(i) - MARGIN * 2);
        return MARGIN + (int) Math.round(tempX[i] * avail);
    }
    private int pixelY(int i) {
        int avail = Math.max(0, this.height - boxHeight() - MARGIN * 2);
        return MARGIN + (int) Math.round(tempY[i] * avail);
    }
    private double ratioX(int i, double px) {
        int avail = Math.max(1, this.width - boxWidth(i) - MARGIN * 2);
        return Math.clamp((px - MARGIN) / avail, 0.0, 1.0);
    }
    private double ratioY(int i, double py) {
        int avail = Math.max(1, this.height - boxHeight() - MARGIN * 2);
        return Math.clamp((py - MARGIN) / avail, 0.0, 1.0);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partial) {
        g.fill(0, 0, this.width, this.height, 0x30101010);
        super.extractRenderState(g, mouseX, mouseY, partial);

        for (int i = 0; i < 3; i++) {
            if (!elements[i].enabled) continue;
            int x = pixelX(i), y = pixelY(i);
            boolean isHovering = posTest(i, mouseX, mouseY);
            g.fill(x, y, x + boxWidth(i), y + boxHeight(),
                    isHovering ? 0xA0303030 : 0x60000000);
            g.text(this.font, label(i), x + PADDING, y + PADDING, 0xFFFFFFFF, true);
        }
    }

    private boolean posTest(int i, double mx, double my) {
        int x = pixelX(i), y = pixelY(i);
        return mx >= x && mx < x + boxWidth(i) && my >= y && my < y + boxHeight();
    }


    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubled) {
        if (event.button() == 0) {
            for (int i = 2; i >= 0; i--) {           // reverse draw order: topmost wins
                if (elements[i].enabled && posTest(i, event.x(), event.y())) {
                    draggingIndex = i;
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
            return true;
        }
        return super.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (event.button() == 0) draggingIndex = -1;
        return super.mouseReleased(event);
    }

    /* ----- close: commit + save ----- */

    @Override
    public void onClose() {
        DisplayConfig c = DisplayConfig.INSTANCE;

        c.distanceXOffset   = tempX[0];  c.distanceYOffset   = tempY[0];
        c.hitXOffset        = tempX[1];  c.hitYOffset        = tempY[1];
        c.averageHitXOffset = tempX[2];  c.averageHitYOffset = tempY[2];

        ConfigManager.save();
        Minecraft.getInstance().setScreenAndShow(parent);
    }
}