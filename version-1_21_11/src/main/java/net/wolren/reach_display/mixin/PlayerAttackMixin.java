package net.wolren.reach_display.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.wolren.reach_display.config.DisplayConfig;
import net.wolren.reach_display.data.SharedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.wolren.reach_display.config.DisplayConfig.DistanceCalculationMethod.RAY_HIT_POINT;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class PlayerAttackMixin {

    @Inject(method = "attackEntity(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/entity/Entity;)V", at = @At("HEAD"))
    private void onAttack(net.minecraft.entity.player.PlayerEntity player, Entity target, CallbackInfo ci) {

        DisplayConfig.DistanceCalculationMethod distanceCalculationMethod = DisplayConfig.distanceCalculationMethod;

        if(distanceCalculationMethod.equals(RAY_HIT_POINT)){
            MinecraftClient client = MinecraftClient.getInstance();

            HitResult result = client.crosshairTarget;
            if (!(result instanceof EntityHitResult hitResult)) return;

            Entity hitEntity = hitResult.getEntity();
            if (hitEntity == null || hitEntity != target) return;

            Vec3d eyePos = player.getEyePos();
            Vec3d hitPos = hitResult.getPos();
            double rayDistance = eyePos.distanceTo(hitPos);

            SharedData data = SharedData.getInstance();
            data.setDistanceAndTarget(rayDistance, target);
            data.addDistanceToAverage(rayDistance);
        }
        else{
            Vec3d eyePos = player.getEyePos();

            Box box = target.getBoundingBox();

            double closestX = Math.max(box.minX, Math.min(eyePos.x, box.maxX));
            double closestY = Math.max(box.minY, Math.min(eyePos.y, box.maxY));
            double closestZ = Math.max(box.minZ, Math.min(eyePos.z, box.maxZ));

            Vec3d closestPoint = new Vec3d(closestX, closestY, closestZ);
            double distance = eyePos.distanceTo(closestPoint);

            SharedData data = SharedData.getInstance();
            data.setDistanceAndTarget(distance, target);
            data.addDistanceToAverage(distance);
        }
    }
}
