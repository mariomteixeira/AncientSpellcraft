package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.api.content.spell.internal.CastContext;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.api.content.util.EntityUtil;
import com.koomplo.wizardry.api.content.util.MagicDamageSource;
import com.koomplo.wizardry.content.spell.DefaultProperties;
import com.koomplo.wizardry.setup.registries.EBDamageSources;
import com.koomplo.wizardry.setup.registries.client.EBParticles;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/** Jato continuo de veneno (1.12.2 PoisonSpray). */
public class PoisonSpray extends SageRaySpell {

    @Override
    public boolean isInstantCast() {
        return false;
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if (!(entityHit.getEntity() instanceof LivingEntity target)) return false;
        if (MagicDamageSource.isEntityImmune(EBDamageSources.POISON, target)) {
            if (!ctx.world().isClientSide && ctx.castingTicks() == 1 && ctx.caster() instanceof Player player) {
                player.displayClientMessage(net.minecraft.network.chat.Component.translatable("spell.resist",
                        target.getName(), getDescriptionFormatted()), true);
            }
        } else if (!ctx.world().isClientSide && ctx.castingTicks() % 20 == 1) {
            target.addEffect(new MobEffectInstance(MobEffects.POISON,
                    (int) (property(DefaultProperties.EFFECT_DURATION) * ctx.modifiers().get(SpellModifiers.DURATION)),
                    property(DefaultProperties.EFFECT_STRENGTH)));
            EntityUtil.attackEntityWithoutKnockback(target,
                    MagicDamageSource.causeDirectMagicDamage(ctx.caster(), EBDamageSources.POISON),
                    property(DefaultProperties.DAMAGE) * ctx.modifiers().get(SpellModifiers.POTENCY));
        }
        return true;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        return false;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z).velocity(vx, vy, vz).color(0x4b8f28).spawn(ctx.world());
    }
}
