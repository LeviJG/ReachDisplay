package net.blueskiez77.reach_display.utils;

import net.blueskiez77.reach_display.config.DisplayConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import static net.blueskiez77.reach_display.config.DisplayConfig.DistanceCalculationMethod.RAY_HIT_POINT;

public class ReachCalculation {

    public static double MeasureReach(Player player, Entity target){
        HitResult result = Minecraft.getInstance().hitResult;
        Vec3 hitPoint = result instanceof EntityHitResult hitResult ? hitResult.getLocation() : null;
        return MeasureReach(player, target, hitPoint);
    }

    /// hitPoint is where the attack ray met the target; null when there is none (RAY_HIT_POINT then returns -1)
    public static double MeasureReach(Player player, Entity target, Vec3 hitPoint){

        DisplayConfig displayConfig = DisplayConfig.INSTANCE;
        DisplayConfig.DistanceCalculationMethod distanceCalculationMethod = displayConfig.distanceCalculationMethod;

        Vec3 eyePos = player.getEyePosition();
        double distance;

        if(distanceCalculationMethod == RAY_HIT_POINT){
            if (hitPoint == null) return -1;
            distance = eyePos.distanceTo(hitPoint);
        }
        else{
            AABB box = target.getBoundingBox();

            double closestX = Math.clamp(eyePos.x, box.minX, box.maxX);
            double closestY = Math.clamp(eyePos.y, box.minY, box.maxY);
            double closestZ = Math.clamp(eyePos.z, box.minZ, box.maxZ);

            Vec3 closestPoint = new Vec3(closestX, closestY, closestZ);
            distance = eyePos.distanceTo(closestPoint);
        }

        return distance;
    }
}