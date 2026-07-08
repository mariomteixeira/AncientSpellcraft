package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.api.content.spell.internal.CastContext;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperties;
import com.koomplo.wizardry.api.content.util.BlockUtil;
import com.koomplo.wizardry.content.spell.DefaultProperties;
import com.koomplo.wizardry.content.spell.abstr.RaySpell;
import com.koomplo.wizardry.setup.registries.client.EBParticles;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/** Exaustao magica no alvo, empowerment no caster (1.12.2 PowerSiphon). */
public class PowerSiphon extends ASRaySpell {

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if (!ctx.world().isClientSide && entityHit.getEntity() instanceof LivingEntity target) {
            int duration = property(DefaultProperties.EFFECT_DURATION);
            target.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    com.windanesz.ancientspellcraft.registry.ASEffects.MAGICAL_EXHAUSTION, duration, 0));
            if (ctx.caster() != null) {
                ctx.caster().addEffect(new net.minecraft.world.effect.MobEffectInstance(
                        com.koomplo.wizardry.setup.registries.EBMobEffects.holder(
                                com.koomplo.wizardry.setup.registries.EBMobEffects.EMPOWERMENT), duration, 0));
            }
            return true;
        }
        return !ctx.world().isClientSide ? false : true;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        return false;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z).color(0x8367bd).spawn(ctx.world());
    }
}
