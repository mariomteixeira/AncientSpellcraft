package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.util.RegistryUtils;
import com.koomplo.wizardry.content.item.SpellBookItem;
import com.koomplo.wizardry.setup.registries.Spells;
import com.windanesz.ancientspellcraft.registry.ASAttachments;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/** Absorve a spell de um livro comum do offhand (consome o livro) (1.12.2 AbsorbSpell). */
public class AbsorbSpell extends WarlockChannelSpell {

    public static final com.koomplo.wizardry.api.content.spell.properties.SpellProperty<Integer> TIER_LIMIT =
            com.koomplo.wizardry.api.content.spell.properties.SpellProperty.intProperty("tier_limit", 3);

    @Override
    protected boolean isValidOffhand(net.minecraft.world.entity.player.Player caster, ItemStack stack) {
        if (!(stack.getItem() instanceof SpellBookItem)) return false;
        Spell spell = RegistryUtils.getSpell(stack);
        if (spell == Spells.NONE || spell instanceof ClassSpell) return false;
        // 1.12.2 isSpellAllowed: satiety/replenish_hunger proibidas (o cast absorvido custa fome)
        var loc = spell.getLocation();
        if (loc.getNamespace().equals("ebwizardry")
                && (loc.getPath().equals("satiety") || loc.getPath().equals("replenish_hunger"))) return false;
        // 1.12.2: "This spell is too powerful to be absorbed"
        return spell.getTier().getLevel() <= property(TIER_LIMIT);
    }

    @Override
    protected String invalidMessage() {
        return "spell.ancientspellcraft.absorb_spell.no_book";
    }

    @Override
    protected boolean absorb(PlayerCastContext ctx, ItemStack offhand) {
        Spell spell = RegistryUtils.getSpell(offhand);
        var tag = ctx.caster().getData(ASAttachments.WARLOCK_DATA);
        tag.putString("Spell", spell.getLocation().toString());
        ctx.caster().setData(ASAttachments.WARLOCK_DATA, tag);
        ctx.caster().displayClientMessage(Component.translatable("spell.ancientspellcraft.absorb_spell.absorbed",
                offhand.getHoverName()), true);
        offhand.shrink(1);
        return true;
    }
}
