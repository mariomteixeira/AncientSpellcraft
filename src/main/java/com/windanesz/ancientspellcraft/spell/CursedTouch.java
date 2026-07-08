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

/** Transfere uma curse aleatoria do caster para um alvo humanoide (1.12.2 CursedTouch). */
public class CursedTouch extends ASRaySpell {

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if (!(entityHit.getEntity() instanceof LivingEntity target) || ctx.caster() == null) return false;
        boolean humanoid = target instanceof net.minecraft.world.entity.npc.Npc || target instanceof Player
                || target instanceof com.koomplo.wizardry.content.entity.living.EvilWizard;
        if (!humanoid) return false;
        if (!ctx.world().isClientSide) {
            var curses = ctx.caster().getActiveEffects().stream()
                    .filter(effect -> effect.getEffect().value() instanceof com.koomplo.wizardry.api.content.effect.CurseMobEffect)
                    .toList();
            if (curses.isEmpty()) return false;
            var curse = curses.get(ctx.world().random.nextInt(curses.size()));
            target.addEffect(new net.minecraft.world.effect.MobEffectInstance(curse.getEffect(), curse.getDuration(), curse.getAmplifier()));
            ctx.caster().removeEffect(curse.getEffect());
        }
        return true;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        return false;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ParticleBuilder.create(EBParticles.DARK_MAGIC).pos(x, y, z).color(0x571e65).spawn(ctx.world());
    }
}
