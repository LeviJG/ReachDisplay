package net.blueskiez77.reach_display.mixin;

import net.blueskiez77.reach_display.utils.SpearHitTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.component.PiercingWeapon;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/// The spear jab: Minecraft.startAttack sends it through piercingAttack instead of attack
@Mixin(MultiPlayerGameMode.class)
public abstract class SpearJabMixin {

    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "piercingAttack(Lnet/minecraft/world/item/component/PiercingWeapon;)V", at = @At("HEAD"))
    private void onPiercingAttack(PiercingWeapon weapon, CallbackInfo ci) {
        LocalPlayer player = this.minecraft.player;
        if (player == null) return;
        SpearHitTracker.recordHit(player, player.getMainHandItem());
    }
}
