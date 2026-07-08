package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperty;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.ArrayList;

/**
 * Estende um efeito ativo aleatorio: min(x2, +max_bonus_duration) (1.12.2 Extension).
 * Corrige o bug do original que trocava duration/amplifier no PotionEffect novo. TODO blacklist configuravel.
 */
public class Extension extends SageSpell {

    public static final SpellProperty<Integer> MAX_BONUS_DURATION = SpellProperty.intProperty("max_bonus_duration");

    @Override
    public boolean cast(PlayerCastContext ctx) {
        if (ctx.world().isClientSide) return true;
        var potions = new ArrayList<>(ctx.caster().getActiveEffects());
        potions.removeIf(e -> e.isInfiniteDuration());
        if (potions.isEmpty()) return false;

        var old = potions.get(ctx.world().random.nextInt(potions.size()));
        int newDuration = Math.min(old.getDuration() * 2, old.getDuration() + property(MAX_BONUS_DURATION));
        var effect = old.getEffect();
        int amplifier = old.getAmplifier();
        ctx.caster().removeEffect(effect);
        ctx.caster().addEffect(new MobEffectInstance(effect, newDuration, amplifier));
        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return true;
    }
}
