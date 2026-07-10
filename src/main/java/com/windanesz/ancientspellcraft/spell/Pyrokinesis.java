package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.api.content.spell.internal.CastContext;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperty;
import com.koomplo.wizardry.api.content.util.EntityUtil;
import com.koomplo.wizardry.api.content.util.MagicDamageSource;
import com.koomplo.wizardry.setup.registries.EBDamageSources;
import com.koomplo.wizardry.setup.registries.client.EBParticles;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/** Pyrokinesis (1.12.2): ray de fogo — incendeia, dá dano FIRE sem knockback e desacelera o alvo. */
public class Pyrokinesis extends ASRaySpell {

    public static final SpellProperty<Integer> BURN_DURATION = SpellProperty.intProperty("burn_duration", 10);
    public static final SpellProperty<Integer> SLOW_DURATION = SpellProperty.intProperty("slow_duration", 30);
    public static final SpellProperty<Float> DAMAGE = SpellProperty.floatProperty("damage", 3f);

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if (!(entityHit.getEntity() instanceof LivingEntity target)) return false;
        if (ctx.world().isClientSide) return true;
        if (MagicDamageSource.isEntityImmune(EBDamageSources.FIRE, target)) return false;
        target.igniteForSeconds(property(BURN_DURATION));
        EntityUtil.attackEntityWithoutKnockback(target,
                MagicDamageSource.causeDirectMagicDamage(ctx.caster(), EBDamageSources.FIRE),
                property(DAMAGE) * ctx.modifiers().get(SpellModifiers.POTENCY));
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, property(SLOW_DURATION), 1));
        return true;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        return false;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ParticleBuilder.create(EBParticles.MAGIC_FIRE).pos(x, y, z).spawn(ctx.world());
    }
}
