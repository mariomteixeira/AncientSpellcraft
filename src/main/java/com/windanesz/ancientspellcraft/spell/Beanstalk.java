package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.api.content.spell.internal.CastContext;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperty;
import com.koomplo.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Pé de feijão (1.12.2 Beanstalk): ray contínuo numa face horizontal; a cada 10t sobe (agachado:
 * desce) uma trepadeira presa à parede, até max_height_in_blocks.
 */
public class Beanstalk extends ASRaySpell {

    public static final SpellProperty<Integer> MAX_HEIGHT = SpellProperty.intProperty("max_height_in_blocks", 10);

    @Override
    public boolean isInstantCast() {
        return false;
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        return false;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        Direction side = blockHit.getDirection();
        if (side.getAxis() == Direction.Axis.Y || ctx.caster() == null) return false;
        if (ctx.castingTicks() % 10 != 0) return true;

        int offset = ctx.castingTicks() / 10;
        if (offset >= property(MAX_HEIGHT)) return true;

        Direction growth = ctx.caster().isShiftKeyDown() ? Direction.DOWN : Direction.UP;
        BlockPos anchor = blockHit.getBlockPos().relative(growth, offset);
        BlockPos position = anchor.relative(side);

        if (!ctx.world().isClientSide
                && !ctx.world().isEmptyBlock(anchor)
                && !ctx.world().getBlockState(position).is(Blocks.VINE)
                && ctx.world().getBlockState(position).canBeReplaced()) {
            var face = VineBlock.PROPERTY_BY_DIRECTION.get(side.getOpposite());
            ctx.world().setBlockAndUpdate(position, Blocks.VINE.defaultBlockState().setValue(face, true));
        }
        return true;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ParticleBuilder.create(EBParticles.LEAF).pos(x, y, z).velocity(vx, vy, vz).spawn(ctx.world());
    }
}
