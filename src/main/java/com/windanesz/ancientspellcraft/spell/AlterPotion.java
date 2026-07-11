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
 * efeito (agachado: LINGERING com 10% da duracao) — ramo do charm_potion_kit. Sem o charm,
 * uma poção no offhand tem cada efeito trocado pela contraparte do alter_potion_mapping
 * (bidirecional, fiel ao 1.12.2).
 */
public class AlterPotion extends WarlockChannelSpell {

    @Override
    protected boolean isValidOffhand(net.minecraft.world.entity.player.Player caster, ItemStack stack) {
        if (stack.is(Items.GLASS_BOTTLE)
                && com.koomplo.wizardry.core.integrations.ArtifactChannel.isEquipped(caster,
                com.windanesz.ancientspellcraft.registry.ASItems.CHARM_POTION_KIT.get())) {
            return true;
        }
        return stack.is(Items.POTION) || stack.is(Items.SPLASH_POTION) || stack.is(Items.LINGERING_POTION);
    }

    @Override
    protected String invalidMessage() {
        return "spell.ancientspellcraft.alter_potion.no_bottle";
    }

    @Override
    protected boolean absorb(PlayerCastContext ctx, ItemStack offhand) {
        if (!offhand.is(Items.GLASS_BOTTLE)) return alterPotion(ctx, offhand);
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

    /** Ramo sem charm (1.12.2): troca cada efeito da poção pela contraparte do mapping. */
    private boolean alterPotion(PlayerCastContext ctx, ItemStack offhand) {
        var mapping = new java.util.HashMap<String, String>();
        for (String entry : com.windanesz.ancientspellcraft.ASServerConfig.ALTER_POTION_MAPPING.get()) {
            String[] parts = entry.split("\\|");
            if (parts.length != 2) continue;
            mapping.put(parts[0], parts[1]);
            mapping.put(parts[1], parts[0]);
        }

        PotionContents contents = offhand.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        var newEffects = new java.util.ArrayList<MobEffectInstance>();
        boolean updated = false;
        for (MobEffectInstance effect : contents.getAllEffects()) {
            String key = net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT
                    .getKey(effect.getEffect().value()).toString();
            String counterpart = mapping.get(key);
            var replacement = counterpart == null ? null
                    : net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT
                            .getHolder(net.minecraft.resources.ResourceLocation.parse(counterpart)).orElse(null);
            if (replacement != null) {
                newEffects.add(new MobEffectInstance(replacement, effect.getDuration(), effect.getAmplifier()));
                updated = true;
            } else {
                newEffects.add(effect);
            }
        }
        if (!updated) return false;

        offhand.set(DataComponents.POTION_CONTENTS,
                new PotionContents(Optional.empty(), contents.customColor(), newEffects));
        return true;
    }
}
