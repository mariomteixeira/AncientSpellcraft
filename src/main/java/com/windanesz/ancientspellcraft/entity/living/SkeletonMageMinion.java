package com.windanesz.ancientspellcraft.entity.living;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.level.Level;

/** Esqueleto mago invocado (1.12.2 EntitySkeletonMageMinion). */
public class SkeletonMageMinion extends SkeletonMageEntity {

    public SkeletonMageMinion(EntityType<? extends AbstractSkeleton> type, Level level) {
        super(type, level);
    }

    @Override
    public boolean shouldDropExperience() {
        return false;
    }

    @Override
    protected boolean shouldDropLoot() {
        return false;
    }

    @Override
    public boolean requiresCustomPersistence() {
        return true;
    }
}
