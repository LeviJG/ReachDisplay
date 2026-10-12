package net.blueskiez77.reach_display.utils;

import net.blueskiez77.reach_display.config.DisplayConfig;
import net.blueskiez77.reach_display.data.SharedData;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragonPart;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.AttackRange;
import net.minecraft.world.phys.Vec3;

import static net.blueskiez77.reach_display.utils.ReachCalculation.MeasureReach;

/// Spear hits are resolved on the server only (PiercingWeapon.attack, KineticWeapon.damageEntities), so they are
/// recorded when the server's damage event says the local player hurt an entity directly while holding a spear.
public class SpearHitTracker {

    public static void onDamageEvent(Entity target, DamageSource source) {
        DisplayConfig displayConfig = DisplayConfig.INSTANCE;
        Player player = Minecraft.getInstance().player;
        if (!displayConfig.enabled || player == null || target == null || target == player) return;
        // Direct melee damage from the local player only (not their arrows, tridents or pets)
        if (source.getEntity() != player || source.getDirectEntity() != player) return;

        ItemStack weapon = spearInUse(player);
        if (weapon == null) return;

        if (target instanceof EnderDragonPart part) target = part.parentMob;
        if (displayConfig.showPlayersOnly && !(target instanceof Player)) return;

        double reach = MeasureReach(player, target, rayHitPoint(player, target, weapon));
        if (reach == -1) return;

        SharedData data = SharedData.getInstance();
        data.setDistanceAndTarget(reach, target);
        data.addDistanceToAverage(reach);
    }

    /// The spear being charged, or the one in the main hand for a jab; null when neither is a spear.
    /// Ordinary melee (swords, axes, fists) is already measured by PlayerAttackMixin.
    private static ItemStack spearInUse(Player player) {
        ItemStack using = player.getUseItem();
        if (player.isUsingItem() && using.has(DataComponents.KINETIC_WEAPON)) return using;
        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.has(DataComponents.PIERCING_WEAPON) || mainHand.has(DataComponents.KINETIC_WEAPON)) return mainHand;
        return null;
    }

    /// Where the spear's ray (as in ProjectileUtil.getHitEntitiesAlong) meets the target's hitbox, for RAY_HIT_POINT
    private static Vec3 rayHitPoint(Player player, Entity target, ItemStack weapon) {
        AttackRange range = player.getAttackRangeWith(weapon);
        Vec3 eyePos = player.getEyePosition();
        Vec3 end = eyePos.add(player.getLookAngle().scale(range.effectiveMaxRange(player)));
        return target.getBoundingBox().inflate(range.hitboxMargin()).clip(eyePos, end).orElse(null);
    }
}
