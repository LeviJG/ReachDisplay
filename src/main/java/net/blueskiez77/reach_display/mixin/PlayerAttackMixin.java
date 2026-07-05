package net.blueskiez77.reach_display.mixin;

import net.blueskiez77.reach_display.config.DisplayConfig;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.blueskiez77.reach_display.data.SharedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.blueskiez77.reach_display.utils.ReachCalculation.MeasureReach;

@Mixin(MultiPlayerGameMode.class)
public abstract class PlayerAttackMixin {

    @Inject(method = "attack(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"))
    private void onAttack(Player player, Entity entity, CallbackInfo ci) {

        DisplayConfig displayConfig = DisplayConfig.INSTANCE;

        if (!displayConfig.enabled || entity == null) return;
        if (displayConfig.showPlayersOnly && !(entity instanceof Player)) return;

        double reach = MeasureReach(player, entity);

        if (reach == -1){
            return;
        }

        SharedData data = SharedData.getInstance();
        data.setDistanceAndTarget(reach, entity);
        data.addDistanceToAverage(reach);
    }
}