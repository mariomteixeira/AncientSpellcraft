package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.content.spell.abstr.ConjureItemSpell;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.world.item.Item;

/** ConjureItemSpell do Redux com bind restrito ao livro/scroll do AS. */
public class ASConjureItemSpell extends ConjureItemSpell {

    public ASConjureItemSpell(Item item) {
        super(item);
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == ASItems.ANCIENT_SPELLCRAFT_SPELL_BOOK.get() || item == ASItems.ANCIENT_SPELLCRAFT_SCROLL.get();
    }
}
