package net.wolren.reach_display.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.wolren.reach_display.data.SharedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class PlayerAttackMixin {

    @Inject(method = "attackEntity(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/entity/Entity;)V", at = @At("HEAD"))
    private void onAttack(net.minecraft.entity.player.PlayerEntity player, Entity target, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();

        // Get the actual ray that determined the hit
        HitResult result = client.crosshairTarget;
        if (!(result instanceof EntityHitResult hitResult)) return;

        Entity hitEntity = hitResult.getEntity();
        if (hitEntity == null || !hitEntity.equals(target)) return;

        Vec3d eyePos = player.getEyePos();
        Vec3d hitPos = hitResult.getPos();
        double rayDistance = eyePos.distanceTo(hitPos);

        SharedData.getInstance().setDistanceAndTarget(rayDistance, target);
        SharedData.getInstance().addDistanceToAverage(rayDistance);
    }
}
