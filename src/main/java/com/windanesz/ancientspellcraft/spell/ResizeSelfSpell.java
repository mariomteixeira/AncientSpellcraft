package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.internal.CastContext;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.content.spell.DefaultProperties;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Supplier;

/**
 * shrink_self/grow_self (1.12.2 SpellResizeSelf); escala via Attributes.SCALE no efeito (substitui o ArtemisLib).
 * Agachado = nivel fraco. TODO rings de permanencia (ring_permanent_shrinkage/growth) portam com os artefatos.
 */
public class ResizeSelfSpell extends ASBuffSpell {

    private final Supplier<Holder<MobEffect>> effect;

    @SuppressWarnings("unchecked")
    public ResizeSelfSpell(float r, float g, float b, Supplier<Holder<MobEffect>> effect) {
        super(r, g, b);
        this.effect = effect;
    }

    @Override
    protected boolean applyEffects(CastContext ctx, LivingEntity target) {
        if (!target.level().isClientSide) {
            int amplifier = target.isShiftKeyDown() ? 0 : 1;
            int duration = (int) (property(DefaultProperties.EFFECT_DURATION) * ctx.modifiers().get(SpellModifiers.DURATION));
            target.addEffect(new MobEffectInstance(effect.get(), duration, amplifier, false, false));
        }
        return true;
    }
}
