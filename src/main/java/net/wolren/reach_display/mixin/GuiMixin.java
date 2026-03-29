package net.wolren.reach_display.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.DeltaTracker;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.wolren.reach_display.config.DisplayConfig;
import net.wolren.reach_display.data.SharedData;
import org.jetbrains.annotations.UnknownNullability;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Unique
    private static final Map<Integer, DecimalFormat> FORMATTER_CACHE = new HashMap<>();
    @Unique
    private String cachedDistanceColorHex;
    @Unique
    private float cachedDistanceOpacityScale;
    @Unique
    private int cachedDistanceARGBColor;
    @Unique
    private String cachedHitDistanceColorHex;
    @Unique
    private float cachedHitDistanceOpacityScale;
    @Unique
    private int cachedHitDistanceARGBColor;
    @Unique
    private String cachedAverageHitDistanceColorHex;
    @Unique
    private float cachedAverageHitDistanceOpacityScale;
    @Unique
    private int cachedAverageHitDistanceARGBColor;

    @Shadow
    @Final
    private Minecraft minecraft = Minecraft.getInstance();

    @Shadow
    public abstract Font getFont();

    @Inject(at = @At("TAIL"), method = "extractRenderState")
    public void render(GuiGraphicsExtractor context, DeltaTracker tickCounter, CallbackInfo ci) {
        if (!DisplayConfig.enabled) return;

        Player player = minecraft.player;
        HitResult target = minecraft.hitResult;

        if (player == null || target == null) return;

        if (target.getType() == HitResult.Type.ENTITY && DisplayConfig.distanceEnable) {
            Entity targetEntity = ((EntityHitResult) target).getEntity();
            if (!targetEntity.isInvisibleTo(player)) {
                if (DisplayConfig.showPlayers && !targetEntity.isAlwaysTicking()) return;
                else {
                    String displayString = getDisplayString(player, targetEntity);
                    if (!displayString.isEmpty()) {
                        int ARGBColorInt = getARGBTextColor("distance", DisplayConfig.distanceColor, DisplayConfig.distanceOpacity);
                        boolean shadow = DisplayConfig.distanceShadow;
                        float scale = DisplayConfig.distanceScale;
                        int xOffset = DisplayConfig.xOffset;
                        int yOffset = DisplayConfig.yOffset;

                        renderAtPosition(context, displayString, ARGBColorInt, shadow, scale, xOffset, yOffset);
                    }
                }
            }
        }

        if (DisplayConfig.hitDistanceEnable) {
            Entity entity = SharedData.getInstance().getEntity();
            if (entity != null) {
                if (DisplayConfig.showPlayers && !entity.isAlwaysTicking()) return;
                else {
                    int hitDistanceDecimalPlaces = DisplayConfig.hitDistanceDecimalPlaces;
                    String displayString = getFormattedDistance(SharedData.getInstance().getDistance(), hitDistanceDecimalPlaces, RoundingMode.DOWN);
                    int ARGBColorInt = getARGBTextColor("hitDistance", DisplayConfig.hitDistanceColor, DisplayConfig.hitDistanceOpacity);
                    boolean shadow = DisplayConfig.hitDistanceShadow;
                    float scale = DisplayConfig.hitDistanceScale;
                    int xOffset = DisplayConfig.hitXOffset;
                    int yOffset = DisplayConfig.hitYOffset;

                    renderAtPosition(context, displayString, ARGBColorInt, shadow, scale, xOffset, yOffset);
                }

            }
        }

        if (DisplayConfig.averageHitDistanceEnable) {
            Entity entity = SharedData.getInstance().getEntity();
            if (entity != null) {
                if (DisplayConfig.showPlayers && !entity.isAlwaysTicking()) return;
                int averageHitDistanceDecimalPlaces = DisplayConfig.averageHitDistanceDecimalPlaces;
                String displayString = getFormattedDistance(SharedData.getInstance().getAverageDistance(), averageHitDistanceDecimalPlaces, RoundingMode.DOWN);
                int ARGBColorInt = getARGBTextColor("averageHitDistance", DisplayConfig.averageHitDistanceColor, DisplayConfig.averageHitDistanceOpacity);
                boolean shadow = DisplayConfig.averageHitDistanceShadow;
                float scale = DisplayConfig.averageHitDistanceScale;
                int xOffset = DisplayConfig.averageHitXOffset;
                int yOffset = DisplayConfig.averageHitYOffset;

                renderAtPosition(context, displayString, ARGBColorInt, shadow, scale, xOffset, yOffset);
            }
        }
    }

    @Unique
    private static int parseColorWithDefault(String colorHex) {
        if (colorHex.isEmpty()) return 0xFFFFFF;
        try {
            return (int) Long.parseLong(colorHex, 16);
        } catch (NumberFormatException e) {
            return 0xFFFFFF;
        }
    }

    @Unique
    private static int parseARGBColorWithOpacity(String colorHex, float opacityScale) {
        int colorInt = parseColorWithDefault(colorHex);
        int alpha = (int) (opacityScale * 255) & 0xFF;
        return (alpha << 24) | (colorInt & 0xFFFFFF);
    }

    @Unique
    private int getARGBTextColor(String processGroup, String colorHex, float opacityScale) {
        if (Objects.equals(processGroup, "distance")) {
            if (!Objects.equals(colorHex, cachedDistanceColorHex) || opacityScale != cachedDistanceOpacityScale) {
                int ARGBColorInt = parseARGBColorWithOpacity(colorHex, opacityScale);
                cachedDistanceColorHex = colorHex;
                cachedDistanceOpacityScale = opacityScale;
                cachedDistanceARGBColor = ARGBColorInt;
            }
            return cachedDistanceARGBColor;
        } else if (Objects.equals(processGroup, "hitDistance")) {
            if (!Objects.equals(colorHex, cachedHitDistanceColorHex) || opacityScale != cachedHitDistanceOpacityScale) {
                int ARGBColorInt = parseARGBColorWithOpacity(colorHex, opacityScale);
                cachedHitDistanceColorHex = colorHex;
                cachedHitDistanceOpacityScale = opacityScale;
                cachedHitDistanceARGBColor = ARGBColorInt;
            }
            return cachedHitDistanceARGBColor;
        } else {
            if (!Objects.equals(colorHex, cachedAverageHitDistanceColorHex) || opacityScale != cachedAverageHitDistanceOpacityScale) {
                int ARGBColorInt = parseARGBColorWithOpacity(colorHex, opacityScale);
                cachedAverageHitDistanceColorHex = colorHex;
                cachedAverageHitDistanceOpacityScale = opacityScale;
                cachedAverageHitDistanceARGBColor = ARGBColorInt;
            }
            return cachedAverageHitDistanceARGBColor;
        }
    }

    @Unique
    private String getFormattedDistance(double distance, int precision, RoundingMode mode) {
        int cacheKey = (precision * 10) + mode.ordinal();

        DecimalFormat df = FORMATTER_CACHE.computeIfAbsent(cacheKey, key -> {
            DecimalFormat newDf = new DecimalFormat("0." + "0".repeat(precision));
            newDf.setRoundingMode(mode);
            return newDf;
        });

        return df.format(distance);
    }

    @Unique
    private String getDisplayString(Player player, Entity targetEntity) {
        DisplayConfig.DistanceCalculationMethod distanceCalculationMethod = DisplayConfig.hitDistanceCalculationMethod;

        if (player.isSpectator()) return "";

        double distance;
        Vec3 eyePos = player.getEyePosition();

        if (distanceCalculationMethod == DisplayConfig.DistanceCalculationMethod.RAY_HIT_POINT) {
            HitResult result = minecraft.hitResult;
            if (!(result instanceof EntityHitResult entityHit) || entityHit.getEntity() != targetEntity) return "";

            Vec3 hitPos = entityHit.getLocation();
            distance = eyePos.distanceTo(hitPos);

            double maxReach = player.isCreative() ? 5.0D : 3.0D;
            if (distance > maxReach) return "";
        } else {
            AABB box = targetEntity.getBoundingBox();

            double closestX = Math.max(box.minX, Math.min(eyePos.x, box.maxX));
            double closestY = Math.max(box.minY, Math.min(eyePos.y, box.maxY));
            double closestZ = Math.max(box.minZ, Math.min(eyePos.z, box.maxZ));

            Vec3 closestPoint = new Vec3(closestX, closestY, closestZ);
            distance = eyePos.distanceTo(closestPoint);
        }

        int distanceDecimalPlaces = DisplayConfig.distanceDecimalPlaces;

        return getFormattedDistance(distance, distanceDecimalPlaces, RoundingMode.DOWN);
    }


    @Unique
    private void renderText(@UnknownNullability GuiGraphicsExtractor context, String text, float x, float y, int color, boolean shadow, float scale) {
        context.pose().pushMatrix();
        context.pose().scale(scale, scale);
        float invScale = 1.0f / scale;
        context.text(this.getFont(), text, (int) (x * invScale), (int) (y * invScale), color, shadow);
        context.pose().popMatrix();
    }

    @Unique
    private void renderAtPosition(@UnknownNullability GuiGraphicsExtractor context, String displayString, int color, boolean shadow, float scale, int xOffset, int yOffset) {
        float y = (minecraft.getWindow().getGuiScaledHeight() / 2.0F) - yOffset;
        float x = (minecraft.getWindow().getGuiScaledWidth() / 2.0F - ((minecraft.font.width(displayString) / 2.0F) * scale) - xOffset);

        renderText(context, displayString, x, y, color, shadow, scale);
    }
}