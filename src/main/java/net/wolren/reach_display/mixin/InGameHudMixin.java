package net.wolren.reach_display.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.wolren.reach_display.config.DisplayConfig;
import net.wolren.reach_display.data.SharedData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.math.RoundingMode;
import java.text.DecimalFormat;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Shadow
    @Final
    private MinecraftClient client = MinecraftClient.getInstance();

    @Shadow
    public abstract TextRenderer getTextRenderer();

    @Inject(at = @At("TAIL"), method = "render")
    public void render(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (!DisplayConfig.enabled) return;

        PlayerEntity player = client.player;
        HitResult target = client.crosshairTarget;

        if (player == null || target == null) return;

        if (target.getType() == HitResult.Type.ENTITY && DisplayConfig.distanceEnable) {
            Entity targetEntity = ((EntityHitResult) target).getEntity();
            if (!targetEntity.isInvisibleTo(player)) {
                if (DisplayConfig.showPlayers && !targetEntity.isPlayer()) return;
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
                if (DisplayConfig.showPlayers && !entity.isPlayer()) return;
                else {
                    String displayString = getHitDisplayString(SharedData.getInstance().getDistance());

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
                if (DisplayConfig.showPlayers && !entity.isPlayer()) return;

                String displayString = getAverageHitDisplayString(SharedData.getInstance().getAverageDistance());

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

    @Unique
    private String getAverageHitDisplayString(Double distance) {
        int decimalPlaces = DisplayConfig.averageHitDistanceDecimalPlaces;

        DecimalFormat df = new DecimalFormat("0." + "0".repeat(decimalPlaces));

        return df.format(distance);
    }

    @Unique
    private String getHitDisplayString(Double distance) {
        int decimalPlaces = DisplayConfig.hitDistanceDecimalPlaces;

        DecimalFormat df = new DecimalFormat("0." + "0".repeat(decimalPlaces));

        return df.format(distance);
    }

    @Unique
    private String getDisplayString(PlayerEntity player, Entity targetEntity) {
        DisplayConfig.DistanceCalculationMethod distanceCalculationMethod = DisplayConfig.hitDistanceCalculationMethod;

        if (player.isSpectator()) return "";

        int decimalPlaces = DisplayConfig.distanceDecimalPlaces;
        DecimalFormat df = new DecimalFormat("0." + "0".repeat(decimalPlaces));
        df.setRoundingMode(RoundingMode.DOWN);

        double distance;
        Vec3d eyePos = player.getEyePos();

        if (distanceCalculationMethod == DisplayConfig.DistanceCalculationMethod.RAY_HIT_POINT) {
            HitResult result = client.crosshairTarget;
            if (!(result instanceof EntityHitResult entityHit) || entityHit.getEntity() != targetEntity) return "";

            Vec3d hitPos = entityHit.getPos();
            distance = eyePos.distanceTo(hitPos);

            double maxReach = player.isCreative() ? 5.0D : 3.0D;
            if (distance > maxReach) return "";
        } else{
            Box box = targetEntity.getBoundingBox();

            double closestX = Math.max(box.minX, Math.min(eyePos.x, box.maxX));
            double closestY = Math.max(box.minY, Math.min(eyePos.y, box.maxY));
            double closestZ = Math.max(box.minZ, Math.min(eyePos.z, box.maxZ));

            Vec3d closestPoint = new Vec3d(closestX, closestY, closestZ);
            distance = eyePos.distanceTo(closestPoint);
        }
        return df.format(distance);
    }


    @Unique
    private void renderText(DrawContext context, String text, float x, float y, int color, boolean shadow, float scale) {
        context.getMatrices().scale(scale, scale);
        context.drawText(this.getTextRenderer(), text, (int) (x * (1 / scale)), (int) (y * (1 / scale)), color, shadow);
        context.getMatrices().scale((1 / scale), (1 / scale));
    }

    @Unique
    public Vec2f getDistance(String displayString) {
        float y = (client.getWindow().getScaledHeight() / 2.0F) - DisplayConfig.yOffset;
        float x = (client.getWindow().getScaledWidth() / 2.0F - ((client.textRenderer.getWidth(displayString) / 2.0F) * DisplayConfig.distanceScale)) - DisplayConfig.xOffset;
        return new Vec2f(x, y);
    }

    @Unique
    public Vec2f getHitDistance(String displayString) {
        float y = (client.getWindow().getScaledHeight() / 2.0F) - DisplayConfig.hitYOffset;
        float x = (client.getWindow().getScaledWidth() / 2.0F - ((client.textRenderer.getWidth(displayString) / 2.0F) * DisplayConfig.hitDistanceScale)) - DisplayConfig.hitXOffset;
        return new Vec2f(x, y);
    }

    @Unique
    public Vec2f getAverageHitDistance(String displayString) {
        float y = (client.getWindow().getScaledHeight() / 2.0F) - DisplayConfig.averageHitYOffset;
        float x = (client.getWindow().getScaledWidth() / 2.0F - ((client.textRenderer.getWidth(displayString) / 2.0F) * DisplayConfig.averageHitDistanceScale)) - DisplayConfig.averageHitXOffset;
        return new Vec2f(x, y);
    }
}