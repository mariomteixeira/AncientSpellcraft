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

/** Converte o chao num disco de magma conjurado (1.12.2 MoltenEarth). */
public class MoltenEarth extends ASRaySpell {

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        return false;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        BlockPos pos = blockHit.getBlockPos().below().relative(blockHit.getDirection());
        if (ctx.world().isClientSide) {
            ParticleBuilder.create(EBParticles.FLASH).pos(pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5)
                    .scale(6).time(20).color(0.55f, 0.29f, 0.04f).spawn(ctx.world());
        } else {
            int radius = (int) (property(DefaultProperties.EFFECT_RADIUS) * ctx.modifiers().get(SpellModifiers.BLAST));
            final BlockPos finalPos = pos;
            TemporaryBlockEntity.place(ctx.caster(), ctx.world(), ASBlocks.CONJURED_MAGMA.get(), pos, 600);
            for (BlockPos current : BlockUtil.getBlockSphere(pos, radius)) {
                if (!ctx.world().isEmptyBlock(current) && current.getY() == finalPos.getY()) {
                    TemporaryBlockEntity.place(ctx.caster(), ctx.world(), ASBlocks.CONJURED_MAGMA.get(), current, 600);
                }
            }
        }
        return true;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ParticleBuilder.create(EBParticles.MAGIC_FIRE).pos(x, y, z).spawn(ctx.world());
    }
}
