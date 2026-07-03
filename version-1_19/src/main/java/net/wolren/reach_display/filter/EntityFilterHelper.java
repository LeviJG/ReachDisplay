package net.wolren.reach_display.filter;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.wolren.reach_display.config.DisplayConfig;
import net.wolren.reach_display.config.DisplayConfig.EntityFilterMode;

public class EntityFilterHelper {
    public static Category categorize(Entity entity) {
        if (entity instanceof PlayerEntity) return Category.PLAYER;

        String className = entity.getClass().getName();
        if (className.contains("EnderDragon") || className.contains("Wither") || className.contains("Warden")) {
            return Category.BOSS;
        }

        if (entity instanceof Monster) return Category.HOSTILE;
        if (entity instanceof AnimalEntity || entity instanceof PassiveEntity) return Category.PASSIVE;

        return Category.OTHER;
    }

    public static boolean shouldTrack(Entity entity) {
        if (!DisplayConfig.entityFilterEnable) return true;

        Category cat = categorize(entity);

        boolean categoryMatches;
        switch (cat) {
            case PLAYER:
                categoryMatches = DisplayConfig.entityFilterPlayers;
                break;
            case HOSTILE:
                categoryMatches = DisplayConfig.entityFilterHostile;
                break;
            case PASSIVE:
                categoryMatches = DisplayConfig.entityFilterPassive;
                break;
            case BOSS:
                categoryMatches = DisplayConfig.entityFilterBoss;
                break;
            default:
                categoryMatches = DisplayConfig.entityFilterOther;
                break;
        }

        boolean customMatch = false;
        if (!DisplayConfig.entityFilterCustomIDs.isEmpty()) {
            String entityId = EntityType.getId(entity.getType()).toString();
            String[] customIds = DisplayConfig.entityFilterCustomIDs.split(",");
            for (String customId : customIds) {
                if (customId.trim().equalsIgnoreCase(entityId)) {
                    customMatch = true;
                    break;
                }
            }
        }

        if (DisplayConfig.entityFilterMode == EntityFilterMode.WHITELIST) {
            return categoryMatches || customMatch;
        } else {
            // BLACKLIST: block only if category disabled AND not in custom list
            if (!categoryMatches && !customMatch) return false;
            return true;
        }
    }

    public enum Category {
        PLAYER, HOSTILE, PASSIVE, BOSS, OTHER
    }
}
