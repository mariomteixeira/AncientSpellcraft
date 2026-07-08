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

/** Areia movedica no ponto mirado + 4 adjacentes aleatorios (1.12.2 SummonQuicksand). */
public class SummonQuicksand extends ASRaySpell {

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
            TemporaryBlockEntity.place(ctx.caster(), ctx.world(), ASBlocks.QUICKSAND.get(), pos, 600);
            Direction[] horizontals = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
            for (int i = 0; i < 4; i++) {
                Direction offset = horizontals[ctx.world().random.nextInt(horizontals.length)];
                TemporaryBlockEntity.place(ctx.caster(), ctx.world(), ASBlocks.QUICKSAND.get(), pos.relative(offset), 600);
            }
        }
        if (ctx.caster() != null) this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return true;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ParticleBuilder.create(EBParticles.DUST).pos(x, y, z).color(0x8c7250).spawn(ctx.world());
    }
}
