package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.content.spell.abstr.BuffSpell;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

/** BuffSpell do Redux restrito ao livro/scroll do AS. */
public class ASBuffSpell extends BuffSpell {

    @SafeVarargs
    public ASBuffSpell(float r, float g, float b, Supplier<Holder<MobEffect>>... effects) {
        super(r, g, b, effects);
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == ASItems.ANCIENT_SPELLCRAFT_SPELL_BOOK.get() || item == ASItems.ANCIENT_SPELLCRAFT_SCROLL.get();
    }
}
