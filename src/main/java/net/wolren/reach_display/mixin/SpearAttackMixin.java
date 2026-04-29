package net.wolren.reach_display.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.wolren.reach_display.config.DisplayConfig;
import net.wolren.reach_display.data.SharedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.wolren.reach_display.utils.ReachCalculation.MeasureReach;

@Mixin(Player.class)
public abstract class SpearAttackMixin {

    @Inject(method = "stabAttack(Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/entity/Entity;FZZZ)Z", at = @At("HEAD"))
    private void onStabAttack(EquipmentSlot slot, Entity target, float baseDamage,
                              boolean dealsDamage, boolean dealsKnockback, boolean dismounts,
                              CallbackInfoReturnable<Boolean> cir) {
        if (!DisplayConfig.enabled) return;
        if (target == null) return;
        Player player = (Player)(Object)this;
        if (DisplayConfig.showPlayersOnly && !(target instanceof Player)) return;

        double reach = MeasureReach(player, target);
        SharedData data = SharedData.getInstance();
        data.setDistanceAndTarget(reach, target);
        data.addDistanceToAverage(reach);
    }
}
