package net.wolren.reach_display.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.wolren.reach_display.config.DisplayConfig;

import static net.wolren.reach_display.config.DisplayConfig.DistanceCalculationMethod.RAY_HIT_POINT;

public class ReachCalculation {

    public static double MeasureReach(Player player, Entity target){

        DisplayConfig.DistanceCalculationMethod distanceCalculationMethod = DisplayConfig.distanceCalculationMethod;
        Minecraft client = Minecraft.getInstance();
        HitResult result = client.hitResult;
        if (!(result instanceof EntityHitResult hitResult)) return -1;
        Entity hitEntity = hitResult.getEntity();
        if (hitEntity != target) return -1;

        Vec3 eyePos = player.getEyePosition();
        double distance;

        if(distanceCalculationMethod == RAY_HIT_POINT){
            distance = player.getEyePosition().distanceTo(hitResult.getLocation());
        }
        else{
            AABB box = target.getBoundingBox();

            double closestX = Math.max(box.minX, Math.min(eyePos.x, box.maxX));
            double closestY = Math.max(box.minY, Math.min(eyePos.y, box.maxY));
            double closestZ = Math.max(box.minZ, Math.min(eyePos.z, box.maxZ));

            Vec3 closestPoint = new Vec3(closestX, closestY, closestZ);
            distance = eyePos.distanceTo(closestPoint);
        }

        return distance;
    }
}