package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperty;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Conjura bolsas de tinta (1.12.2 ConjureInk). */
public class ConjureInk extends SageSpell {

    public static final SpellProperty<Integer> ITEM_COUNT = SpellProperty.intProperty("item_count");

    @Override
    public boolean cast(PlayerCastContext ctx) {
        if (!ctx.world().isClientSide) {
            var stack = new ItemStack(Items.INK_SAC, property(ITEM_COUNT));
            if (!ctx.caster().addItem(stack)) ctx.caster().drop(stack, false);
        }
        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return true;
    }
}
