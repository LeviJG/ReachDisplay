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

        DisplayConfig displayConfig = DisplayConfig.INSTANCE;
        DisplayConfig.DistanceCalculationMethod distanceCalculationMethod = displayConfig.distanceCalculationMethod;
        Minecraft client = Minecraft.getInstance();
        HitResult result = client.hitResult;

        Vec3 eyePos = player.getEyePosition();
        double distance;

        if(distanceCalculationMethod == RAY_HIT_POINT){
            if (!(result instanceof EntityHitResult hitResult)) return -1;
            distance = player.getEyePosition().distanceTo(hitResult.getLocation());
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