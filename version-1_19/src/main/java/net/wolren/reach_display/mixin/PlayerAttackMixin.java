package net.wolren.reach_display.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.wolren.reach_display.data.SharedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerAttackMixin {
    @Inject(method = "attack(Lnet/minecraft/entity/Entity;)V", at = @At("HEAD"))
    private void onAttack(Entity target, CallbackInfo ci) {
        if (target instanceof LivingEntity) {
            PlayerEntity player = (PlayerEntity) (Object) this;
            double distance = player.getEyePos().distanceTo(closestPointToBox(player.getEyePos(), target.getBoundingBox()));
            SharedData.getInstance().setDistanceAndTarget(distance, target);
            SharedData.getInstance().addDistanceToAverage(distance);
        }
    }

    @Unique
    public Vec3d closestPointToBox(Vec3d start, Box box) {
        return new Vec3d(coerceIn(start.x, box.minX, box.maxX), coerceIn(start.y, box.minY, box.maxY), coerceIn(start.z, box.minZ, box.maxZ));
    }

    @Unique
    public double coerceIn(double target, double min, double max) {
        if (target > max) {
            return max;
        }
        return Math.max(target, min);
    }
}
