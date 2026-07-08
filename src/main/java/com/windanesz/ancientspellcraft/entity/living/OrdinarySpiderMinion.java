package com.windanesz.ancientspellcraft.entity.living;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.Level;

/** Aranha invocada (1.12.2 EntityOrdinarySpiderMinion). Dono/lifetime/targeting via MINION_DATA do Redux. */
public class OrdinarySpiderMinion extends Spider {

    public OrdinarySpiderMinion(EntityType<? extends Spider> type, Level level) {
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
