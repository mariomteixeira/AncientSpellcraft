package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.content.spell.abstr.MinionSpell;
import com.windanesz.ancientspellcraft.entity.living.RemnantMinion;
import com.windanesz.ancientspellcraft.registry.ASEntities;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.world.item.Item;

/** Invoca um remnant aliado (1.12.2 SummonRemnant). */
public class SummonRemnant extends MinionSpell<RemnantMinion> {

    public SummonRemnant() {
        super(level -> new RemnantMinion(ASEntities.REMNANT_MINION.get(), level));
        this.flying(true);
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == ASItems.ANCIENT_SPELLCRAFT_SPELL_BOOK.get() || item == ASItems.ANCIENT_SPELLCRAFT_SCROLL.get();
    }
}
