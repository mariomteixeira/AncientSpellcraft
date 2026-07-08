package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.internal.CastContext;
import com.windanesz.ancientspellcraft.registry.ASEffects;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Supplier;

/** Wards de projetil mutuamente exclusivos (1.12.2 SpellWard): remove os irmaos antes de aplicar. */
public class WardBuffSpell extends ASBuffSpell {

    @SafeVarargs
    public WardBuffSpell(Supplier<Holder<MobEffect>>... effects) {
        super(230 / 255f, 230 / 255f, 255 / 255f, effects);
    }

    @Override
    protected boolean applyEffects(CastContext ctx, LivingEntity target) {
        if (!target.level().isClientSide) {
            target.removeEffect(ASEffects.PROJECTILE_WARD);
            target.removeEffect(ASEffects.BULWARK);
            target.removeEffect(ASEffects.ARCANE_AEGIS);
        }
        return super.applyEffects(ctx, target);
    }
}
