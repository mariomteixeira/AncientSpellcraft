package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.content.spell.abstr.MinionSpell;
import com.windanesz.ancientspellcraft.entity.living.OrdinarySpiderMinion;
import com.windanesz.ancientspellcraft.registry.ASEntities;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.world.item.Item;

/** Invoca uma aranha comum (1.12.2 SummonSpider). */
public class SummonSpider extends MinionSpell<OrdinarySpiderMinion> {

    public SummonSpider() {
        super(level -> new OrdinarySpiderMinion(ASEntities.ORDINARY_SPIDER_MINION.get(), level));
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == ASItems.ANCIENT_SPELLCRAFT_SPELL_BOOK.get() || item == ASItems.ANCIENT_SPELLCRAFT_SCROLL.get();
    }
}
