package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.api.content.spell.internal.CastContext;
import com.koomplo.wizardry.content.spell.DefaultProperties;
import com.koomplo.wizardry.setup.registries.client.EBParticles;
import com.windanesz.ancientspellcraft.potion.ChaosEffect;
import com.windanesz.ancientspellcraft.registry.ASEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/** Toque do caos: aplica CHAOS com variante PAR aleatoria (1.12.2 ChaosTouch). */
public class ChaosTouch extends WarlockRaySpell {

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if (!(entityHit.getEntity() instanceof LivingEntity target)) return false;
        if (!ctx.world().isClientSide) {
            int even = ctx.world().random.nextInt((ChaosEffect.VARIANTS + 1) / 2) * 2;
            target.addEffect(new MobEffectInstance(ASEffects.CHAOS,
                    property(DefaultProperties.EFFECT_DURATION), even));
        }
        return true;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        return false;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ParticleBuilder.create(EBParticles.DARK_MAGIC).pos(x, y, z).color(0xfc0303).spawn(ctx.world());
        ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z).color(0xff9900).time(10 + ctx.world().random.nextInt(6)).spawn(ctx.world());
    }
}
