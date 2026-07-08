package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.content.spell.abstr.MinionSpell;
import com.windanesz.ancientspellcraft.entity.living.FireAntMinion;
import com.windanesz.ancientspellcraft.registry.ASEntities;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.world.item.Item;

/** Invoca formigas de fogo kamikaze (1.12.2 SummonFireAnt). */
public class SummonFireAnt extends MinionSpell<FireAntMinion> {

    public SummonFireAnt() {
        super(level -> new FireAntMinion(ASEntities.FIRE_ANT_MINION.get(), level));
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == ASItems.ANCIENT_SPELLCRAFT_SPELL_BOOK.get() || item == ASItems.ANCIENT_SPELLCRAFT_SCROLL.get();
    }
}
