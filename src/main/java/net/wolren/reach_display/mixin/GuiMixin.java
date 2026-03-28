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
import net.minecraft.world.phys.Vec2;
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

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Unique
    private static final Map<Integer, DecimalFormat> FORMATTER_CACHE = new HashMap<>();

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

                    String colorHex = DisplayConfig.distanceColor;
                    int colorInt = parseColorWithDefault(colorHex);
                    float opacityScale = DisplayConfig.distanceOpacity;
                    int ARGBColorInt = parseARGBColorWithOpacity(opacityScale, colorInt);
                    boolean shadow = DisplayConfig.distanceShadow;
                    float scale = DisplayConfig.distanceScale;



                    renderText(context, displayString, getDistance(displayString).x, getDistance(displayString).y, ARGBColorInt, shadow, scale);
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

                    String colorHex = DisplayConfig.hitDistanceColor;
                    int colorInt = parseColorWithDefault(colorHex);
                    float opacityScale = DisplayConfig.hitDistanceOpacity;
                    int ARGBColorInt = parseARGBColorWithOpacity(opacityScale, colorInt);
                    boolean shadow = DisplayConfig.hitDistanceShadow;
                    float scale = DisplayConfig.hitDistanceScale;

                    renderText(context, displayString, getHitDistance(displayString).x, getHitDistance(displayString).y, ARGBColorInt, shadow, scale);
                }

            }
        }

        if (DisplayConfig.averageHitDistanceEnable) {
            Entity entity = SharedData.getInstance().getEntity();
            if (entity != null) {
                if (DisplayConfig.showPlayers && !entity.isAlwaysTicking()) return;
                int averageHitDistanceDecimalPlaces = DisplayConfig.averageHitDistanceDecimalPlaces;
                String displayString = getFormattedDistance(SharedData.getInstance().getAverageDistance(), averageHitDistanceDecimalPlaces, RoundingMode.DOWN);

                String colorHex = DisplayConfig.averageHitDistanceColor;
                int colorInt = parseColorWithDefault(colorHex);
                float opacityScale = DisplayConfig.averageHitDistanceOpacity;
                int ARGBColorInt = parseARGBColorWithOpacity(opacityScale, colorInt);
                boolean shadow = DisplayConfig.averageHitDistanceShadow;
                float scale = DisplayConfig.averageHitDistanceScale;

                renderText(context, displayString, getAverageHitDistance(displayString).x, getAverageHitDistance(displayString).y, ARGBColorInt, shadow, scale);

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
    private  static  int parseARGBColorWithOpacity(float opacityScale, int colorInt){
        int alpha = (int)(opacityScale * 255) & 0xFF;
        return (alpha << 24) | (colorInt & 0xFFFFFF);
    }

/*    @Unique
    private String getAverageHitDisplayString(Double distance) {
        int averageHitDistanceDecimalPlaces = DisplayConfig.averageHitDistanceDecimalPlaces;

        DecimalFormat df = new DecimalFormat("0." + "0".repeat(averageHitDistanceDecimalPlaces));

        return df.format(distance);
    }*/

/*    @Unique
    private String getHitDisplayString(Double distance) {
        int hitDistanceDecimalPlaces = DisplayConfig.hitDistanceDecimalPlaces;

        DecimalFormat df = new DecimalFormat("0." + "0".repeat(hitDistanceDecimalPlaces));

        return df.format(distance);
    }*/

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
        } else{
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
        context.text(this.getFont(), text, (int) (x * (1 / scale)), (int) (y * (1 / scale)), color, shadow);
        context.pose().popMatrix();
    }

    @Unique
    public Vec2 getDistance(String displayString) {
        float y = (minecraft.getWindow().getGuiScaledHeight() / 2.0F) - DisplayConfig.yOffset;
        float x = (minecraft.getWindow().getGuiScaledWidth() / 2.0F - ((minecraft.font.width(displayString) / 2.0F) * DisplayConfig.distanceScale)) - DisplayConfig.xOffset;
        return new Vec2(x, y);
    }

    @Unique
    public Vec2 getHitDistance(String displayString) {
        float y = (minecraft.getWindow().getGuiScaledHeight() / 2.0F) - DisplayConfig.hitYOffset;
        float x = (minecraft.getWindow().getGuiScaledWidth() / 2.0F - ((minecraft.font.width(displayString) / 2.0F) * DisplayConfig.hitDistanceScale)) - DisplayConfig.hitXOffset;
        return new Vec2(x, y);
    }

    @Unique
    public Vec2 getAverageHitDistance(String displayString) {
        float y = (minecraft.getWindow().getGuiScaledHeight() / 2.0F) - DisplayConfig.averageHitYOffset;
        float x = (minecraft.getWindow().getGuiScaledWidth() / 2.0F - ((minecraft.font.width(displayString) / 2.0F) * DisplayConfig.averageHitDistanceScale)) - DisplayConfig.averageHitXOffset;
        return new Vec2(x, y);
    }
}