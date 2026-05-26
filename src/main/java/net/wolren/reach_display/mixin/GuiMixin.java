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
    private Minecraft minecraft; //= Minecraft.getInstance();

    @Inject(at = @At("TAIL"), method = "extractRenderState")
    public void render(GuiGraphicsExtractor graphics, DeltaTracker tickCounter, CallbackInfo ci) {
        if (!DisplayConfig.enabled) return;
        Player player = minecraft.player;
        HitResult target = minecraft.hitResult;
        if (player == null || target == null) return;

        SharedData sharedData = SharedData.getInstance();

        if(DisplayConfig.distanceEnable){
            if (target.getType() == HitResult.Type.ENTITY){
                Entity targetEntity = ((EntityHitResult) target).getEntity();
                if (!targetEntity.isInvisibleTo(player)){
                    if (!DisplayConfig.showPlayersOnly || targetEntity instanceof Player){
                        double reachDistance = MeasureReach(player, targetEntity);
                        if (reachDistance != -1) {
                            CustomRender.renderText(minecraft, graphics, reachDistance, DisplayConfig.distanceDecimalPlaces, 0, DisplayConfig.distanceShadow, DisplayConfig.distanceScale, DisplayConfig.xOffset, DisplayConfig.yOffset);
                        }
                    }
                }
            }
        }

        if (sharedData.getEntity() == null){
            return;
        }

        if (DisplayConfig.hitDistanceEnable) {
            int hitDistanceDecimalPlaces = DisplayConfig.hitDistanceDecimalPlaces;
            double reachDistance = sharedData.getDistance();
            boolean shadow = DisplayConfig.hitDistanceShadow;
            float scale = DisplayConfig.hitDistanceScale;
            int xOffset = DisplayConfig.hitXOffset;
            int yOffset = DisplayConfig.hitYOffset;

            CustomRender.renderText(minecraft, graphics, reachDistance, hitDistanceDecimalPlaces, 1, shadow, scale, xOffset, yOffset);
        }

        if (DisplayConfig.averageHitDistanceEnable) {

            int averageHitDistanceDecimalPlaces = DisplayConfig.averageHitDistanceDecimalPlaces;
            double reachDistance = sharedData.getAverageDistance();
            boolean shadow = DisplayConfig.averageHitDistanceShadow;
            float scale = DisplayConfig.averageHitDistanceScale;
            int xOffset = DisplayConfig.averageHitXOffset;
            int yOffset = DisplayConfig.averageHitYOffset;

            CustomRender.renderText(minecraft, graphics, reachDistance, averageHitDistanceDecimalPlaces, 2, shadow, scale, xOffset, yOffset);
        }
    }
}