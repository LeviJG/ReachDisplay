package net.wolren.reach_display.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
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
    public void render(DrawContext context, float tickDelta, CallbackInfo ci) {
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
                    boolean shadow = DisplayConfig.distanceShadow;
                    float scale = DisplayConfig.distanceScale;

                    renderText(context, displayString, getDistance(displayString).x, getDistance(displayString).y, colorInt, shadow, scale);
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
                    boolean shadow = DisplayConfig.hitDistanceShadow;
                    float scale = DisplayConfig.hitDistanceScale;

                    renderText(context, displayString, getHitDistance(displayString).x, getHitDistance(displayString).y, colorInt, shadow, scale);
                }

            }
        }

        if (DisplayConfig.averageHitDistanceEnable) {
            Entity entity = SharedData.getInstance().getEntity();
            if (entity != null) {
                if (DisplayConfig.showPlayers && !entity.isPlayer()) return;
                else {
                    String displayString = getAverageHitDisplayString(SharedData.getInstance().getAverageDistance());

                    String colorHex = DisplayConfig.averageHitDistanceColor;
                    int colorInt = parseColorWithDefault(colorHex);
                    boolean shadow = DisplayConfig.averageHitDistanceShadow;
                    float scale = DisplayConfig.averageHitDistanceScale;

                    renderText(context, displayString, getAverageHitDistance(displayString).x, getAverageHitDistance(displayString).y, colorInt, shadow, scale);
                }
            }
        }
    }

    @Unique
    private static int parseColorWithDefault(String colorHex) {
        if (colorHex.isEmpty()) return 16777215;
        try {
            return Integer.parseInt(colorHex, 16);
        } catch (NumberFormatException e) {
            return 16777215;
        }
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
        double distance = player.getEyePos().distanceTo(closestPointToBox(player.getEyePos(), targetEntity.getBoundingBox()));

        int decimalPlaces = DisplayConfig.distanceDecimalPlaces;
        DecimalFormat df = new DecimalFormat("0." + "0".repeat(decimalPlaces));
        df.setRoundingMode(RoundingMode.DOWN);

        if (player.isCreative() && distance <= 5.00) {
            return df.format(distance);
        } else if (player.isSpectator()) {
            return "";
        } else if (distance <= 3.00) {
            return df.format(distance);
        }
        return "";
    }

    @Unique
    private void renderText(DrawContext context, String text, float x, float y, int color, boolean shadow, float scale) {
        context.getMatrices().scale(scale, scale, scale);
        context.drawText(this.getTextRenderer(), text, (int) (x * (1 / scale)), (int) (y * (1 / scale)), color, shadow);
        context.getMatrices().scale((1 / scale), (1 / scale), (1 / scale));
    }

    @Unique
    public Vec3d closestPointToBox(Vec3d start, Box box) {
        return new Vec3d(coerceIn(start.x, box.minX, box.maxX), coerceIn(start.y, box.minY, box.maxY), coerceIn(start.z, box.minZ, box.maxZ));
    }

    @Unique
    public double coerceIn(double target, double min, double max) {
        if (target > max) {
            return max;
        }
        return Math.max(target, min);
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
