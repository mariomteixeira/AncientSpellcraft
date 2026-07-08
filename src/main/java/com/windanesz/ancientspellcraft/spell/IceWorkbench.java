package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.api.content.spell.internal.CastContext;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperty;
import com.koomplo.wizardry.setup.registries.client.EBParticles;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/** Conjura uma mesa de trabalho de gelo temporaria (1.12.2 IceWorkbench). */
public class IceWorkbench extends ASRaySpell {

    public static final SpellProperty<Integer> BLOCK_LIFETIME = SpellProperty.intProperty("block_lifetime");

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        return false;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        BlockPos pos = blockHit.getBlockPos().relative(blockHit.getDirection());
        if (ctx.world().isClientSide) {
            for (int i = 1; i < 4; i++) {
                ParticleBuilder.create(EBParticles.ICE)
                        .pos(pos.getX() + 0.8, pos.getY() + ctx.world().random.nextDouble() + 0.2, pos.getZ() + 0.8)
                        .time(30).spawn(ctx.world());
            }
            ParticleBuilder.create(EBParticles.FLASH).color(1f, 1f, 1f)
                    .pos(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5).time(30).scale(1.5f).spawn(ctx.world());
            return true;
        }
        if (ctx.world().getBlockState(pos).canBeReplaced()) {
            ctx.world().setBlockAndUpdate(pos, ASBlocks.ICE_CRAFTING_TABLE.get().defaultBlockState());
            int lifetime = (int) (property(BLOCK_LIFETIME) * ctx.modifiers().get(SpellModifiers.DURATION));
            ctx.world().scheduleTick(pos, ASBlocks.ICE_CRAFTING_TABLE.get(), lifetime);
            return true;
        }
        return false;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ParticleBuilder.create(EBParticles.ICE).pos(x, y, z).spawn(ctx.world());
    }
}
