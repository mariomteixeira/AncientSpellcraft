package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.windanesz.ancientspellcraft.registry.ASAttachments;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionContents;

/** Absorve uma pocao do offhand: guarda efeito + 70% da duracao (1.12.2 AbsorbPotion). */
public class AbsorbPotion extends WarlockChannelSpell {

    @Override
    protected boolean isValidOffhand(net.minecraft.world.entity.player.Player caster, ItemStack stack) {
        return stack.getItem() instanceof PotionItem;
    }

    @Override
    protected String invalidMessage() {
        return "spell.ancientspellcraft.absorb_potion.no_potion";
    }

    @Override
    protected boolean absorb(PlayerCastContext ctx, ItemStack offhand) {
        PotionContents contents = offhand.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        for (var effect : contents.getAllEffects()) {
            if (effect.getEffect().value().isInstantenous()) continue;
            var id = BuiltInRegistries.MOB_EFFECT.getKey(effect.getEffect().value());
            if (id == null) continue;
            var tag = ctx.caster().getData(ASAttachments.WARLOCK_DATA);
            tag.putString("Effect", id.toString());
            tag.putInt("EffectDuration", (int) (effect.getDuration() * 0.7f));
            ctx.caster().setData(ASAttachments.WARLOCK_DATA, tag);
            ctx.caster().displayClientMessage(Component.translatable("spell.ancientspellcraft.absorb_potion.absorbed",
                    offhand.getHoverName()), true);
            offhand.shrink(1);
            return true;
        }
        return false;
    }
}
