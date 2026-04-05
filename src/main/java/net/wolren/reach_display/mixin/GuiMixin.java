package net.wolren.reach_display.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.DeltaTracker;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.wolren.reach_display.config.DisplayConfig;
import net.wolren.reach_display.data.SharedData;
import net.wolren.reach_display.utils.CustomRender;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import static net.wolren.reach_display.utils.ReachCalculation.MeasureReach;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Shadow
    @Final
    private Minecraft minecraft = Minecraft.getInstance();

    @Inject(at = @At("TAIL"), method = "extractRenderState")
    public void render(GuiGraphicsExtractor graphics, DeltaTracker tickCounter, CallbackInfo ci) {
        if (!DisplayConfig.enabled) return;

        Player player = minecraft.player;
        HitResult target = minecraft.hitResult;

        if (player == null || target == null) return;

        if (target.getType() == HitResult.Type.ENTITY && DisplayConfig.distanceEnable) {
            Entity targetEntity = ((EntityHitResult) target).getEntity();
            if (!targetEntity.isInvisibleTo(player)) {
                if (DisplayConfig.showPlayers && !targetEntity.isAlwaysTicking()) return;

                double reachDistance = MeasureReach(player, targetEntity);
                if (reachDistance != -1) {
                    int distanceDecimalPlaces = DisplayConfig.distanceDecimalPlaces;
                    boolean shadow = DisplayConfig.distanceShadow;
                    float scale = DisplayConfig.distanceScale;
                    int xOffset = DisplayConfig.xOffset;
                    int yOffset = DisplayConfig.yOffset;

                    CustomRender.renderText(minecraft, graphics, reachDistance, distanceDecimalPlaces, 0, shadow, scale, xOffset, yOffset);
                }
            }
        }

        if (DisplayConfig.hitDistanceEnable) {
            Entity entity = SharedData.getInstance().getEntity();
            if (entity != null) {
                if (DisplayConfig.showPlayers && !entity.isAlwaysTicking()) return;
                else {
                    int hitDistanceDecimalPlaces = DisplayConfig.hitDistanceDecimalPlaces;
                    double reachDistance = SharedData.getInstance().getDistance();
                    boolean shadow = DisplayConfig.hitDistanceShadow;
                    float scale = DisplayConfig.hitDistanceScale;
                    int xOffset = DisplayConfig.hitXOffset;
                    int yOffset = DisplayConfig.hitYOffset;

                    CustomRender.renderText(minecraft, graphics, reachDistance, hitDistanceDecimalPlaces, 1, shadow, scale, xOffset, yOffset);
                }
            }
        }

        if (DisplayConfig.averageHitDistanceEnable) {
            Entity entity = SharedData.getInstance().getEntity();
            if (entity != null) {
                if (DisplayConfig.showPlayers && !entity.isAlwaysTicking()) return;
                int averageHitDistanceDecimalPlaces = DisplayConfig.averageHitDistanceDecimalPlaces;
                double reachDistance = SharedData.getInstance().getAverageDistance();
                boolean shadow = DisplayConfig.averageHitDistanceShadow;
                float scale = DisplayConfig.averageHitDistanceScale;
                int xOffset = DisplayConfig.averageHitXOffset;
                int yOffset = DisplayConfig.averageHitYOffset;

                 CustomRender.renderText(minecraft, graphics, reachDistance, averageHitDistanceDecimalPlaces, 2, shadow, scale, xOffset, yOffset);
            }
        }
    }
}