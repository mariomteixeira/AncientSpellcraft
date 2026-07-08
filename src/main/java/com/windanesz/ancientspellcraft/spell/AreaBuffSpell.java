package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.internal.CastContext;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.api.content.util.EntityUtil;
import com.koomplo.wizardry.content.spell.DefaultProperties;
import com.koomplo.wizardry.core.AllyDesignation;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Supplier;

/** Buff em area (1.12.2 Celerity/WaterWalking): aplica no caster e nos alvos em EFFECT_RADIUS. */
public class AreaBuffSpell extends ASBuffSpell {

    private final boolean alliesOnly;

    @SafeVarargs
    public AreaBuffSpell(float r, float g, float b, boolean alliesOnly, Supplier<Holder<MobEffect>>... effects) {
        super(r, g, b, effects);
        this.alliesOnly = alliesOnly;
    }

    @Override
    protected boolean applyEffects(CastContext ctx, LivingEntity target) {
        boolean result = super.applyEffects(ctx, target);
        double radius = property(DefaultProperties.EFFECT_RADIUS) * ctx.modifiers().get(SpellModifiers.BLAST);
        for (LivingEntity other : EntityUtil.getLivingWithinRadius(radius, target.getX(), target.getY(), target.getZ(), target.level())) {
            if (other == target) continue;
            if (alliesOnly && !AllyDesignation.isAllied(target, other)) continue;
            super.applyEffects(ctx, other);
        }
        return result;
    }
}
