package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.content.spell.abstr.MinionSpell;
import com.windanesz.ancientspellcraft.entity.living.WolfMinion;
import com.windanesz.ancientspellcraft.registry.ASEntities;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.world.item.Item;

/** Chama a alcateia (1.12.2 CallOfThePack): lobos invocados, quantidade pelo JSON. */
public class CallOfThePack extends MinionSpell<WolfMinion> {

    public CallOfThePack() {
        super(level -> new WolfMinion(ASEntities.WOLF_MINION.get(), level));
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == ASItems.ANCIENT_SPELLCRAFT_SPELL_BOOK.get() || item == ASItems.ANCIENT_SPELLCRAFT_SCROLL.get();
    }
}
