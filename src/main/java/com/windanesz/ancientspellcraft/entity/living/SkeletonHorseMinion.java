package com.windanesz.ancientspellcraft.entity.living;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.level.Level;

/** Cavalo esqueleto invocado e montavel (1.12.2 EntitySkeletonHorseMinion). */
public class SkeletonHorseMinion extends SkeletonHorse {

    public SkeletonHorseMinion(EntityType<? extends SkeletonHorse> type, Level level) {
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
