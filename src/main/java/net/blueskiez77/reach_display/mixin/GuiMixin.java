package net.blueskiez77.reach_display.mixin;

import net.blueskiez77.reach_display.config.DisplayConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.DeltaTracker;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.blueskiez77.reach_display.data.SharedData;
import net.blueskiez77.reach_display.utils.CustomRender;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import static net.blueskiez77.reach_display.utils.ReachCalculation.MeasureReach;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(at = @At("TAIL"), method = "extractRenderState")
    public void render(GuiGraphicsExtractor graphics, DeltaTracker tickCounter, CallbackInfo ci) {
        DisplayConfig displayConfig = DisplayConfig.INSTANCE;
        if (!displayConfig.enabled) return;
        Player player = minecraft.player;
        HitResult target = minecraft.hitResult;
        if (player == null || target == null) return;

        SharedData sharedData = SharedData.getInstance();

        if(displayConfig.distanceEnabled){
            if (target.getType() == HitResult.Type.ENTITY){
                Entity targetEntity = ((EntityHitResult) target).getEntity();
                if (!targetEntity.isInvisibleTo(player)){
                    if (!displayConfig.showPlayersOnly || targetEntity instanceof Player){
                        double reachDistance = MeasureReach(player, targetEntity);
                        if (reachDistance != -1) {
                            CustomRender.renderText(minecraft, graphics, reachDistance, displayConfig.distanceDecimalPlaces, 0, displayConfig.distanceTextShadow, displayConfig.distanceTextScale, displayConfig.distanceXOffset, displayConfig.distanceYOffset);
                        }
                    }
                }
            }
        }

        if (sharedData.getEntity() == null){
           return;
        }

        if (displayConfig.hitDistanceEnabled) {
            int hitDistanceDecimalPlaces = displayConfig.hitDistanceDecimalPlaces;
            double reachDistance = sharedData.getDistance();
            boolean shadow = displayConfig.hitDistanceTextShadow;
            float scale = displayConfig.hitDistanceTextScale;
            double xOffset = displayConfig.hitXOffset;
            double yOffset = displayConfig.hitYOffset;

            CustomRender.renderText(minecraft, graphics, reachDistance, hitDistanceDecimalPlaces, 1, shadow, scale, xOffset, yOffset);
        }

        if (displayConfig.averageHitEnabled) {

            int averageHitDistanceDecimalPlaces = displayConfig.averageHitDecimalPlaces;
            double reachDistance = sharedData.getAverageDistance();
            boolean shadow = displayConfig.averageHitTextShadow;
            float scale = displayConfig.averageHitTextScale;
            double xOffset = displayConfig.averageHitXOffset;
            double yOffset = displayConfig.averageHitYOffset;

            CustomRender.renderText(minecraft, graphics, reachDistance, averageHitDistanceDecimalPlaces, 2, shadow, scale, xOffset, yOffset);
        }
    }
}