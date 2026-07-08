package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.content.item.CrystalItem;
import com.windanesz.ancientspellcraft.registry.ASAttachments;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/** Absorve um cristal elemental do offhand: guarda o ELEMENTO absorvido (1.12.2 AbsorbCrystal).
 * TODO ring_absorb_crystal permite blocos de cristal. */
public class AbsorbCrystal extends WarlockChannelSpell {

    @Override
    protected boolean isValidOffhand(ItemStack stack) {
        return stack.getItem() instanceof CrystalItem;
    }

    @Override
    protected String invalidMessage() {
        return "spell.ancientspellcraft.absorb_crystal.no_crystal";
    }

    @Override
    protected boolean absorb(PlayerCastContext ctx, ItemStack offhand) {
        var tag = ctx.caster().getData(ASAttachments.WARLOCK_DATA);
        tag.putString("Element", BuiltInRegistries.ITEM.getKey(offhand.getItem()).getPath().replace("magic_crystal_", "").replace("magic_crystal", "magic"));
        ctx.caster().setData(ASAttachments.WARLOCK_DATA, tag);
        ctx.caster().displayClientMessage(Component.translatable("spell.ancientspellcraft.absorb_crystal.absorbed",
                offhand.getHoverName()), true);
        offhand.shrink(1);
        return true;
    }
}
