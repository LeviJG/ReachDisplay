package net.blueskiez77.reach_display.utils;

import net.blueskiez77.reach_display.config.DisplayConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.UnknownNullability;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import net.minecraft.client.gui.Font;

public class CustomRender {
    private static final Map<Integer, DecimalFormat> FORMATTER_CACHE = new HashMap<>();
    private static int cachedDistanceColorInt;
    private static double cachedDistanceOpacityScale;
    private static int cachedDistanceARGBColor;
    private static int cachedHitDistanceColorInt;
    private static double cachedHitDistanceOpacityScale;
    private static int cachedHitDistanceARGBColor;
    private static int cachedAverageHitDistanceColorInt;
    private static double cachedAverageHitDistanceOpacityScale;
    private static int cachedAverageHitDistanceARGBColor;
    public static final int MARGIN = 4, PADDING = 2;

    /// textClass values mapping: 0 = distance; 1 = hitDistance; 2 = averageHitDistance
    public static void renderText(Minecraft minecraft, @UnknownNullability GuiGraphicsExtractor context, double displayDouble, int displayDecimalPlaces, int textClass, boolean shadow, float scale, double xOffset, double yOffset){
        String roundedDisplayDouble = getRoundedDouble(displayDouble, displayDecimalPlaces);

        int color = switch (textClass) {
            case 0 -> getDistanceARGBColor();
            case 1 -> getHitDistanceARGBColor();
            case 2 -> getAverageHitDistanceARGBColor();
            default -> 0xFFFFFF;
        };

        int windowWidth = minecraft.getWindow().getGuiScaledWidth();
        int windowHeight = minecraft.getWindow().getGuiScaledHeight();

        int boxW = boxWidth(minecraft.font, roundedDisplayDouble, scale);
        int boxH = boxHeight(minecraft.font, scale);

        float x = boxX(xOffset, windowWidth, boxW) + PADDING;
        float y = boxY(yOffset, windowHeight, boxH) + PADDING;

        drawScaledText(context, roundedDisplayDouble, x, y, scale, color, shadow);
    }

    // The geometry below is shared with the HUD layout editor (HUDConfig) so a number sits where it was dragged

    public static int boxWidth(Font font, String text, float scale) {
        return (int)(font.width(text) * scale) + PADDING * 2;
    }

    public static int boxHeight(Font font, float scale) {
        return (int)(font.lineHeight * scale) + PADDING * 2;
    }

    /// Left edge of a box whose position is the fraction xOffset of the free width
    public static float boxX(double xOffset, int screenWidth, int boxW) {
        int availX = Math.max(0, screenWidth - boxW - MARGIN * 2);
        return MARGIN + (float)(xOffset * availX);
    }

    public static float boxY(double yOffset, int screenHeight, int boxH) {
        int availY = Math.max(0, screenHeight - boxH - MARGIN * 2);
        return MARGIN + (float)(yOffset * availY);
    }

    public static void drawScaledText(GuiGraphicsExtractor context, String text, float x, float y, float scale, int color, boolean shadow) {
        context.pose().pushMatrix();
        context.pose().scale(scale, scale);
        float invScale = 1.0f / scale;
        context.text(getFont(), text, (int) (x * invScale), (int) (y * invScale), color, shadow);
        context.pose().popMatrix();
    }

    public static String getRoundedDouble(double distance, int precision) {
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

    private static int parseARGBColorWithOpacity(int colorInt, double opacityScale) {
        int alpha = (int) (opacityScale * 255) & 0xFF;
        return (alpha << 24) | (colorInt & 0xFFFFFF);
    }

    private static int getDistanceARGBColor() {
        int colorInt = DisplayConfig.INSTANCE.distanceTextColor;
        double opacityScale = DisplayConfig.INSTANCE.distanceTextOpacity;
        if (!Objects.equals(colorInt, cachedDistanceColorInt) || opacityScale != cachedDistanceOpacityScale) {
            int ARGBColorInt = parseARGBColorWithOpacity(colorInt, opacityScale);
            cachedDistanceColorInt = colorInt;
            cachedDistanceOpacityScale = opacityScale;
            cachedDistanceARGBColor = ARGBColorInt;
        }
        return cachedDistanceARGBColor;
    }

    private static int getHitDistanceARGBColor(){
        int colorInt = DisplayConfig.INSTANCE.hitDistanceTextColor;
        double opacityScale = DisplayConfig.INSTANCE.hitDistanceTextOpacity;
        if (!Objects.equals(colorInt, cachedHitDistanceColorInt) || opacityScale != cachedHitDistanceOpacityScale) {
            int ARGBColorInt = parseARGBColorWithOpacity(colorInt, opacityScale);
            cachedHitDistanceColorInt = colorInt;
            cachedHitDistanceOpacityScale = opacityScale;
            cachedHitDistanceARGBColor = ARGBColorInt;
        }
        return cachedHitDistanceARGBColor;
    }

    private static int getAverageHitDistanceARGBColor() {
        int colorInt = DisplayConfig.INSTANCE.averageHitTextColor;
        double opacityScale = DisplayConfig.INSTANCE.averageHitTextOpacity;
        if (!Objects.equals(colorInt, cachedAverageHitDistanceColorInt) || opacityScale != cachedAverageHitDistanceOpacityScale) {
            int ARGBColorInt = parseARGBColorWithOpacity(colorInt, opacityScale);
            cachedAverageHitDistanceColorInt = colorInt;
            cachedAverageHitDistanceOpacityScale = opacityScale;
            cachedAverageHitDistanceARGBColor = ARGBColorInt;
        }
        return cachedAverageHitDistanceARGBColor;
    }
}