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
    public static double measureReach(Player player, Entity target) {
        Minecraft client = Minecraft.getInstance();
        HitResult result = client.hitResult;
        Vec3 eyePos = player.getEyePosition();

        if (DisplayConfig.distanceCalculationMethod == RAY_HIT_POINT) {
            if (!(result instanceof EntityHitResult hitResult)) return -1;
            return eyePos.distanceTo(hitResult.getLocation());
        }

        AABB box = target.getBoundingBox();
        double cx = Math.clamp(eyePos.x, box.minX, box.maxX);
        double cy = Math.clamp(eyePos.y, box.minY, box.maxY);
        double cz = Math.clamp(eyePos.z, box.minZ, box.maxZ);
        return eyePos.distanceTo(new Vec3(cx, cy, cz));
    }
}
