package net.blueskiez77.reach_display.mixin;

import net.blueskiez77.reach_display.utils.SpearHitTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundDamageEventPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/// Spear hits (jab and charge) are only resolved on the server, so the client learns about them from the damage
/// event the server sends for the entity that was hit. TAIL so it runs once, on the render thread.
@Mixin(ClientPacketListener.class)
public abstract class SpearHitMixin {

    @Shadow private ClientLevel level;

    @Inject(method = "handleDamageEvent", at = @At("TAIL"))
    private void onDamageEvent(ClientboundDamageEventPacket packet, CallbackInfo ci) {
        if (this.level == null) return;
        SpearHitTracker.onDamageEvent(this.level.getEntity(packet.entityId()), packet.getSource(this.level));
    }
}
