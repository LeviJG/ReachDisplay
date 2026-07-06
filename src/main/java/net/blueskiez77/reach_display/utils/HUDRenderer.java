package net.blueskiez77.reach_display.utils;

import net.blueskiez77.reach_display.ReachDisplay;
import net.blueskiez77.reach_display.config.DisplayConfig;
import net.blueskiez77.reach_display.data.SharedData;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import static net.blueskiez77.reach_display.utils.ReachCalculation.MeasureReach;
import net.minecraft.client.Minecraft;

public class HUDRenderer {
    public static void register() {
        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath(ReachDisplay.MOD_ID, "reach_display"),
                (graphics, deltaTracker) -> render(graphics)
        );
    }

    private static void render(GuiGraphicsExtractor graphics) {
        Minecraft minecraft = Minecraft.getInstance();
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