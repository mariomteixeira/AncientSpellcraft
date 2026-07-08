package com.windanesz.ancientspellcraft.entity.living;

import com.koomplo.wizardry.content.entity.living.Remnant;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Remnant invocado (1.12.2 EntityRemnantMinion) sobre o Remnant do proprio Redux. */
public class RemnantMinion extends Remnant {

    public RemnantMinion(EntityType<? extends Monster> type, Level level) {
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
