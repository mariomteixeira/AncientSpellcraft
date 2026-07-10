package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;

import java.util.List;
import java.util.Optional;

/**
 * Engarrafa o primeiro efeito ativo do warlock: garrafa vazia no offhand vira SPLASH potion do
 * efeito (agachado: LINGERING com 10% da duracao). Fiel ao 1.12.2, esse ramo é do
 * charm_potion_kit — sem o charm a spell recusa (o ramo alternativo do 1.12.2, mapping de
 * poções por config, fica para o marco 7).
 */
public class AlterPotion extends WarlockChannelSpell {

    @Override
    protected boolean isValidOffhand(net.minecraft.world.entity.player.Player caster, ItemStack stack) {
        return stack.is(Items.GLASS_BOTTLE)
                && com.koomplo.wizardry.core.integrations.ArtifactChannel.isEquipped(caster,
                com.windanesz.ancientspellcraft.registry.ASItems.CHARM_POTION_KIT.get());
    }

    @Override
    protected String invalidMessage() {
        return "spell.ancientspellcraft.alter_potion.no_bottle";
    }

    @Override
    protected boolean absorb(PlayerCastContext ctx, ItemStack offhand) {
        var effects = ctx.caster().getActiveEffects();
        if (effects.isEmpty()) return false;
        MobEffectInstance first = effects.iterator().next();
        boolean lingering = ctx.caster().isShiftKeyDown();

        ItemStack potion = new ItemStack(lingering ? Items.LINGERING_POTION : Items.SPLASH_POTION);
        int duration = (int) (first.getDuration() * (lingering ? 0.1f : 0.5f));
        potion.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.empty(),
                Optional.of(first.getEffect().value().getColor()),
                List.of(new MobEffectInstance(first.getEffect(), Math.max(20, duration), first.getAmplifier()))));

        ctx.caster().removeEffect(first.getEffect());
        offhand.shrink(1);
        if (!ctx.caster().addItem(potion)) ctx.caster().drop(potion, false);
        return true;
    }
}
