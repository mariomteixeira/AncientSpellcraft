package com.windanesz.ancientspellcraft.entity.living;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.level.Level;

/** Lobo invocado (1.12.2 EntityWolfMinion). */
public class WolfMinion extends Wolf {

    public WolfMinion(EntityType<? extends Wolf> type, Level level) {
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
