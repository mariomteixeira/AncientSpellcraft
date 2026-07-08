package com.windanesz.ancientspellcraft.block;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.api.content.util.MagicDamageSource;
import com.koomplo.wizardry.core.AllyDesignation;
import com.koomplo.wizardry.setup.registries.EBDamageSources;
import com.koomplo.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

/** Bloco eletrificado invisivel (1.12.2 BlockLightning): SHOCK a cada 5t, aliados do caster imunes. */
public class LightningBlock extends TemporaryBlock {

    public LightningBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull net.minecraft.world.level.BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public void entityInside(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Entity entity) {
        if (entity.tickCount % 5 != 0 || level.isClientSide) return;
        if (!(entity instanceof LivingEntity target)) return;
        if (!(level.getBlockEntity(pos) instanceof TemporaryBlockEntity be)) return;

        LivingEntity caster = be.getCaster();
        if (caster != null && (target == caster || AllyDesignation.isAllied(caster, target))) return;
        // TODO ring_kinetic: speed pro caster que pisa (artefatos)
        var motion = target.getDeltaMovement();
        target.hurt(caster != null ? MagicDamageSource.causeIndirectMagicDamage(target, caster, EBDamageSources.SHOCK)
                : target.damageSources().lightningBolt(), be.getDamage() > 0 ? be.getDamage() : 1.0f);
        target.setDeltaMovement(motion); // sem knockback, igual 1.12.2
    }

    @Override
    public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (random.nextInt(3) == 0) {
            ParticleBuilder.create(EBParticles.SPARK)
                    .pos(pos.getX() + random.nextDouble(), pos.getY() + random.nextDouble(), pos.getZ() + random.nextDouble())
                    .spawn(level);
        }
    }
}
