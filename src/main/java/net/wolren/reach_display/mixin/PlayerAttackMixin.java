package net.wolren.reach_display.mixin;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.wolren.reach_display.config.DisplayConfig;
import net.wolren.reach_display.data.SharedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.wolren.reach_display.utils.ReachCalculation.MeasureReach;

@Mixin(MultiPlayerGameMode.class)
public abstract class PlayerAttackMixin {

    @Inject(method = "attack(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"))
    private void onAttack(net.minecraft.world.entity.player.Player player, Entity entity, CallbackInfo ci) {

        if (!DisplayConfig.enabled) return;
        if (player == null || entity == null) return;
        if (DisplayConfig.showPlayersOnly && !entity.is(EntityType.PLAYER)) return;

        double reach = MeasureReach(player, entity);

        if (reach == -1){
            return;
        }

        SharedData data = SharedData.getInstance();
        data.setDistanceAndTarget(reach, entity);
        data.addDistanceToAverage(reach);
    }
}