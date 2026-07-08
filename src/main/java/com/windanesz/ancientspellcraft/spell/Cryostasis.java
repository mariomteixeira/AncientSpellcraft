package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.internal.CastContext;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperty;
import com.koomplo.wizardry.api.content.util.EntityUtil;
import com.koomplo.wizardry.api.content.util.MagicDamageSource;
import com.koomplo.wizardry.content.spell.DefaultProperties;
import com.koomplo.wizardry.core.AllyDesignation;
import com.koomplo.wizardry.setup.registries.EBDamageSources;
import com.koomplo.wizardry.setup.registries.EBMobEffects;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/** Entomba o caster em gelo, congela inimigos proximos e alimenta (1.12.2 Cryostasis). */
public class Cryostasis extends ASBuffSpell {

    public static final SpellProperty<Integer> ICE_DURATION = SpellProperty.intProperty("ice_duration");

    @SuppressWarnings("unchecked")
    public Cryostasis() {
        super(11 / 255f, 215 / 255f, 222 / 255f, () -> EBMobEffects.holder(EBMobEffects.FROST),
                () -> net.minecraft.core.Holder.direct(MobEffects.REGENERATION.value()),
                () -> net.minecraft.core.Holder.direct(MobEffects.ABSORPTION.value()));
    }

    @Override
    protected boolean applyEffects(CastContext ctx, LivingEntity caster) {
        boolean result = super.applyEffects(ctx, caster);
        if (caster.level().isClientSide || !EntityUtil.canDamageBlocks(caster, caster.level())) return result;

        if (caster.isOnFire()) caster.clearFire();
        caster.teleportTo(caster.blockPosition().getX() + 0.5, caster.blockPosition().getY(), caster.blockPosition().getZ() + 0.5);
        BlockPos pos = caster.blockPosition();

        int effectDuration = (int) (property(DefaultProperties.EFFECT_DURATION) * ctx.modifiers().get(SpellModifiers.DURATION));
        for (LivingEntity target : EntityUtil.getLivingWithinRadius(4, caster.getX(), caster.getY(), caster.getZ(), caster.level())) {
            if (AllyDesignation.isValidTarget(caster, target) && !MagicDamageSource.isEntityImmune(EBDamageSources.FROST, target)) {
                EntityUtil.applyStandardKnockback(caster, target, 2f);
                target.addEffect(new MobEffectInstance(EBMobEffects.holder(EBMobEffects.FROST), effectDuration,
                        property(DefaultProperties.EFFECT_STRENGTH)));
            }
        }

        int iceDuration = (int) (property(ICE_DURATION) * ctx.modifiers().get(SpellModifiers.DURATION));
        for (BlockPos current : BlockPos.betweenClosed(pos.south().east().below(), pos.above(2).north().west())) {
            if (current.equals(pos) || current.equals(pos.above())) continue;
            if (caster.level().getBlockState(current).canBeReplaced()) {
                caster.level().setBlockAndUpdate(current, ASBlocks.HARD_FROSTED_ICE.get().defaultBlockState());
                caster.level().scheduleTick(current.immutable(), ASBlocks.HARD_FROSTED_ICE.get(), iceDuration);
            }
        }
        caster.level().removeBlock(pos, false);
        caster.level().removeBlock(pos.above(), false);

        if (caster instanceof Player player && player.getFoodData().needsFood()) {
            player.getFoodData().eat(5, 0.1f);
        }
        return true;
    }
}
