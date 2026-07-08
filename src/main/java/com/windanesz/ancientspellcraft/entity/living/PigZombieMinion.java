package com.windanesz.ancientspellcraft.entity.living;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.level.Level;

/** Piglin zumbi invocado (1.12.2 EntityPigZombieMinion). */
public class PigZombieMinion extends ZombifiedPiglin {

    public PigZombieMinion(EntityType<? extends ZombifiedPiglin> type, Level level) {
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
