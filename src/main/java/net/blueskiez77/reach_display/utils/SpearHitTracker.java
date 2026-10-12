package net.blueskiez77.reach_display.utils;

import net.blueskiez77.reach_display.config.DisplayConfig;
import net.blueskiez77.reach_display.data.SharedData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragonPart;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.AttackRange;
import net.minecraft.world.item.component.PiercingWeapon;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.EntityHitResult;

import java.util.Collection;
import java.util.List;

import static net.blueskiez77.reach_display.utils.ReachCalculation.MeasureReach;

/// Spear hits are resolved on the server only (PiercingWeapon.attack, KineticWeapon.damageEntities), so the client
/// repeats their ray (ProjectileUtil.getHitEntitiesAlong with the weapon's attack range) to find what was hit.
public class SpearHitTracker {

    public static void recordHit(Player player, ItemStack weapon) {
        DisplayConfig displayConfig = DisplayConfig.INSTANCE;
        if (!displayConfig.enabled || player == null || weapon.isEmpty()) return;

        AttackRange range = player.getAttackRangeWith(weapon);
        Collection<EntityHitResult> hits = ProjectileUtil.getHitEntitiesAlong(player, range, e -> PiercingWeapon.canHitEntity(player, e), ClipContext.Block.COLLIDER)
                .map(blockHit -> List.<EntityHitResult>of(), entityHits -> entityHits);

        // One spear attack counts as one hit: the closest target it went through
        Entity closestTarget = null;
        double closestReach = Double.MAX_VALUE;
        for (EntityHitResult hit : hits) {
            Entity target = hit.getEntity() instanceof EnderDragonPart part ? part.parentMob : hit.getEntity();
            if (displayConfig.showPlayersOnly && !(target instanceof Player)) continue;

            double reach = MeasureReach(player, target, hit.getLocation());
            if (reach != -1 && reach < closestReach) {
                closestReach = reach;
                closestTarget = target;
            }
        }
        if (closestTarget == null) return;

        SharedData data = SharedData.getInstance();
        data.setDistanceAndTarget(closestReach, closestTarget);
        data.addDistanceToAverage(closestReach);
    }
}
