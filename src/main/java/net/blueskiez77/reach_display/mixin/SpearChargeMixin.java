package net.blueskiez77.reach_display.mixin;

import net.blueskiez77.reach_display.utils.SpearHitTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/// The spear charge: the server tells the attacker it landed a kinetic hit with entity event 2 (onKineticHit)
@Mixin(LivingEntity.class)
public abstract class SpearChargeMixin {

    @Inject(method = "onKineticHit()V", at = @At("HEAD"))
    private void onKineticHit(CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;
        if ((Object) this != player) return;
        SpearHitTracker.recordHit(player, player.getUseItem());
    }
}
