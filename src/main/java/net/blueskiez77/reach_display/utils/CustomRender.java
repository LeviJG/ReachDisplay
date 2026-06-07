package net.blueskiez77.reach_display.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.blueskiez77.reach_display.config.DisplayConfig;
import org.jetbrains.annotations.UnknownNullability;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import net.minecraft.client.gui.Font;

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

    /// textClass values mapping: 0 = distance; 1 = hitDistance; 2 = averageHitDistance
    public static void renderText(Minecraft minecraft, @UnknownNullability GuiGraphicsExtractor context, double displayDouble, int displayDecimalPlaces, int textClass, boolean shadow, float scale, int xOffset, int yOffset){
        String roundedDisplayDouble = getRoundedDouble(displayDouble, displayDecimalPlaces);

        int color = switch (textClass) {
            case 0 -> getDistanceARGBColor();
            case 1 -> getHitDistanceARGBColor();
            case 2 -> getAverageHitDistanceARGBColor();
            default -> 0xFFFFFF;
        };

        float y = (minecraft.getWindow().getGuiScaledHeight() / 2.0F) - yOffset;
        float x = (minecraft.getWindow().getGuiScaledWidth() / 2.0F - ((minecraft.font.width(roundedDisplayDouble) / 2.0F) * scale) - xOffset);

        context.pose().pushMatrix();
        context.pose().scale(scale, scale);
        float invScale = 1.0f / scale;
        context.text(getFont(), roundedDisplayDouble, (int) (x * invScale), (int) (y * invScale), color, shadow);
        context.pose().popMatrix();
    }

    private static String getRoundedDouble(double distance, int precision) {
        RoundingMode roundingMode = RoundingMode.DOWN;
        int cacheKey = (precision * 10) + roundingMode.ordinal();

        if (precision < 1 || precision > 16) {
            //LOG HERE
            precision = 2;
        }

        final int p = precision;

        DecimalFormat df = FORMATTER_CACHE.computeIfAbsent(cacheKey, key -> {
            DecimalFormat newDf = new DecimalFormat("0." + "0".repeat(p));
            newDf.setRoundingMode(roundingMode);
            return newDf;
        });

        return df.format(distance);
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
        if (colorHex.isEmpty()) return 0xFFFFFF;
        int colorInt = parseColorWithDefault(colorHex);
        int alpha = (int) (opacityScale * 255) & 0xFF;
        return (alpha << 24) | (colorInt & 0xFFFFFF);
    }

    private static int getDistanceARGBColor() {
        String colorHex = DisplayConfig.distanceColor;
        float opacityScale = DisplayConfig.distanceOpacity;
        if (!Objects.equals(colorHex, cachedDistanceColorHex) || opacityScale != cachedDistanceOpacityScale) {
            int ARGBColorInt = parseARGBColorWithOpacity(colorHex, opacityScale);
            cachedDistanceColorHex = colorHex;
            cachedDistanceOpacityScale = opacityScale;
            cachedDistanceARGBColor = ARGBColorInt;
        }
        return cachedDistanceARGBColor;
    }

    private static int getHitDistanceARGBColor(){
        String colorHex = DisplayConfig.hitDistanceColor;
        float opacityScale = DisplayConfig.hitDistanceOpacity;
        if (!Objects.equals(colorHex, cachedHitDistanceColorHex) || opacityScale != cachedHitDistanceOpacityScale) {
            int ARGBColorInt = parseARGBColorWithOpacity(colorHex, opacityScale);
            cachedHitDistanceColorHex = colorHex;
            cachedHitDistanceOpacityScale = opacityScale;
            cachedHitDistanceARGBColor = ARGBColorInt;
        }
        return cachedHitDistanceARGBColor;
    }

    private static int getAverageHitDistanceARGBColor() {
        String colorHex = DisplayConfig.averageHitDistanceColor;
        float opacityScale = DisplayConfig.averageHitDistanceOpacity;
        if (!Objects.equals(colorHex, cachedAverageHitDistanceColorHex) || opacityScale != cachedAverageHitDistanceOpacityScale) {
            int ARGBColorInt = parseARGBColorWithOpacity(colorHex, opacityScale);
            cachedAverageHitDistanceColorHex = colorHex;
            cachedAverageHitDistanceOpacityScale = opacityScale;
            cachedAverageHitDistanceARGBColor = ARGBColorInt;
        }
        return cachedAverageHitDistanceARGBColor;
    }
}