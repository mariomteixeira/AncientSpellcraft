package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.content.spell.abstr.BuffSpell;
import com.windanesz.ancientspellcraft.handler.ASMetamagicEvents;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

/**
 * MetaSpellBuff do 1.12.2: buff metamagic que altera os modifiers do PRÓXIMO cast
 * (leitura em {@link ASMetamagicEvents}). Regras do original: só um metamagic ativo por vez;
 * re-cast com o efeito ativo amplifica (máx. nível III); re-cast no nível III dá 90% de
 * desconto no cooldown desta spell. Desvio: charm_metamagic_amplifier fica com o lote de artefatos.
 */
public class MetamagicBuffSpell extends BuffSpell {

    private final Supplier<Holder<MobEffect>> effectSupplier;

    public MetamagicBuffSpell(Supplier<Holder<MobEffect>> effect) {
        super(1.0f, 1.0f, 1.0f, effect); // 1.12.2: partículas brancas (255,255,255)
        this.effectSupplier = effect;
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == ASItems.ANCIENT_SPELLCRAFT_SPELL_BOOK.get() || item == ASItems.ANCIENT_SPELLCRAFT_SCROLL.get();
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        Holder<MobEffect> effect = effectSupplier.get();
        Player caster = ctx.caster();
        int currentAmplifier = caster.hasEffect(effect) ? caster.getEffect(effect).getAmplifier() : -1;
        // charm_metamagic_amplifier (1.12.2): sem buff ativo, já começa no nível II
        int bonusAmplifier = currentAmplifier >= 0 ? currentAmplifier + 1
                : com.koomplo.wizardry.core.integrations.ArtifactChannel.isEquipped(caster,
                com.windanesz.ancientspellcraft.registry.ASItems.CHARM_METAMAGIC_AMPLIFIER.get()) ? 1 : 0;
        if (bonusAmplifier > 2) return false;

        if (!ctx.world().isClientSide) {
            for (Holder<MobEffect> other : ASMetamagicEvents.metamagicEffects()) {
                if (!other.equals(effect) && caster.hasEffect(other)) {
                    caster.removeEffect(other);
                }
            }
            caster.removeEffect(effect);
            int duration = (int) (this.property(getEffectDurationProperty(effect)) * ctx.modifiers().get(SpellModifiers.DURATION));
            caster.addEffect(new MobEffectInstance(effect, duration, bonusAmplifier, false, true));
            if (currentAmplifier == 2) {
                ctx.modifiers().set(SpellModifiers.COOLDOWN, ctx.modifiers().get(SpellModifiers.COOLDOWN) * 0.1F);
            }
        } else {
            spawnParticles(ctx.world(), caster);
        }
        playSound(ctx.world(), caster, ctx.castingTicks(), -1);
        return true;
    }
}
