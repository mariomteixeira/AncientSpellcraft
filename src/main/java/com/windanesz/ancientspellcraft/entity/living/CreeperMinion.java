package com.windanesz.ancientspellcraft.entity.living;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;

/** Creeper conjurado (1.12.2 EntityCreeperMinion). */
public class CreeperMinion extends Creeper {

    public CreeperMinion(EntityType<? extends Creeper> type, Level level) {
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
