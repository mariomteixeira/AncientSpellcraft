package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.api.content.spell.internal.CastContext;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperty;
import com.koomplo.wizardry.api.content.util.BlockUtil;
import com.koomplo.wizardry.content.spell.DefaultProperties;
import com.koomplo.wizardry.setup.registries.client.EBParticles;
import com.windanesz.ancientspellcraft.block.TemporaryBlockEntity;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/** Eletrifica o bloco mirado (1.12.2 Electrify). */
public class Electrify extends ASRaySpell {

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        return false;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        BlockPos pos = blockHit.getBlockPos().relative(blockHit.getDirection());
        if (!ctx.world().isClientSide && ctx.world().isEmptyBlock(pos)) {
            int duration = (int) (property(DefaultProperties.EFFECT_DURATION) * ctx.modifiers().get(SpellModifiers.DURATION));
            float damage = property(DefaultProperties.DAMAGE) * ctx.modifiers().get(SpellModifiers.POTENCY);
            TemporaryBlockEntity.place(ctx.caster(), ctx.world(), ASBlocks.LIGHTNING_BLOCK.get(), pos, duration);
            if (ctx.world().getBlockEntity(pos) instanceof TemporaryBlockEntity be) be.setDamage(damage);
            return true;
        }
        return ctx.world().isClientSide;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ParticleBuilder.create(EBParticles.SPARK).pos(x, y, z).spawn(ctx.world());
    }
}
