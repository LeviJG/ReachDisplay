package net.wolren.reach_display.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Font;
import net.wolren.reach_display.config.DisplayConfig;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CustomRender {
    private static final Map<Integer, DecimalFormat> FORMATTER_CACHE = new HashMap<>();
    private static String cachedDistanceColorHex;
    private static float cachedDistanceOpacityScale;
    private static int cachedDistanceARGBColor;
    private static String cachedHitDistanceColorHex;
    private static float cachedHitDistanceOpacityScale;
    private static int cachedHitDistanceARGBColor;
    private static String cachedAverageHitDistanceColorHex;
    private static float cachedAverageHitDistanceOpacityScale;
    private static int cachedAverageHitDistanceARGBColor;

    public static void renderText(Minecraft minecraft, GuiGraphicsExtractor context, double displayDouble, int displayDecimalPlaces, int textClass, boolean shadow, float scale, int xOffset, int yOffset) {
        String rounded = getRoundedDouble(displayDouble, displayDecimalPlaces);

        int color = switch (textClass) {
            case 0 -> getDistanceARGBColor();
            case 1 -> getHitDistanceARGBColor();
            case 2 -> getAverageHitDistanceARGBColor();
            default -> 0xFFFFFF;
        };

        float y = (minecraft.getWindow().getGuiScaledHeight() / 2.0F) - yOffset;
        float x = (minecraft.getWindow().getGuiScaledWidth() / 2.0F - ((minecraft.font.width(rounded) / 2.0F) * scale) - xOffset);

        context.pose().pushMatrix();
        context.pose().scale(scale, scale);
        float invScale = 1.0f / scale;
        context.text(getFont(), rounded, (int) (x * invScale), (int) (y * invScale), color, shadow);
        context.pose().popMatrix();
    }

    private static String getRoundedDouble(double distance, int precision) {
        int key = (precision * 10) + RoundingMode.DOWN.ordinal();
        return FORMATTER_CACHE.computeIfAbsent(key, k -> {
            DecimalFormat df = new DecimalFormat("0." + "0".repeat(precision));
            df.setRoundingMode(RoundingMode.DOWN);
            return df;
        }).format(distance);
    }

    private static Font getFont() {
        return Minecraft.getInstance().font;
    }

    private static int parseColorWithDefault(String colorHex) {
        if (colorHex.isEmpty()) return 0xFFFFFF;
        try {
            return (int) Long.parseLong(colorHex, 16);
        } catch (NumberFormatException e) {
            return 0xFFFFFF;
        }
    }

    private static int parseARGBColorWithOpacity(String colorHex, float opacityScale) {
        int alpha = (int) (opacityScale * 255) & 0xFF;
        return (alpha << 24) | (parseColorWithDefault(colorHex) & 0xFFFFFF);
    }

    private static int getDistanceARGBColor() {
        String hex = DisplayConfig.distanceColor;
        float op = DisplayConfig.distanceOpacity;
        if (!Objects.equals(hex, cachedDistanceColorHex) || op != cachedDistanceOpacityScale) {
            cachedDistanceARGBColor = parseARGBColorWithOpacity(hex, op);
            cachedDistanceColorHex = hex;
            cachedDistanceOpacityScale = op;
        }
        return cachedDistanceARGBColor;
    }

    private static int getHitDistanceARGBColor() {
        String hex = DisplayConfig.hitDistanceColor;
        float op = DisplayConfig.hitDistanceOpacity;
        if (!Objects.equals(hex, cachedHitDistanceColorHex) || op != cachedHitDistanceOpacityScale) {
            cachedHitDistanceARGBColor = parseARGBColorWithOpacity(hex, op);
            cachedHitDistanceColorHex = hex;
            cachedHitDistanceOpacityScale = op;
        }
        return cachedHitDistanceARGBColor;
    }

    private static int getAverageHitDistanceARGBColor() {
        String hex = DisplayConfig.averageHitDistanceColor;
        float op = DisplayConfig.averageHitDistanceOpacity;
        if (!Objects.equals(hex, cachedAverageHitDistanceColorHex) || op != cachedAverageHitDistanceOpacityScale) {
            cachedAverageHitDistanceARGBColor = parseARGBColorWithOpacity(hex, op);
            cachedAverageHitDistanceColorHex = hex;
            cachedAverageHitDistanceOpacityScale = op;
        }
        return cachedAverageHitDistanceARGBColor;
    }
}
