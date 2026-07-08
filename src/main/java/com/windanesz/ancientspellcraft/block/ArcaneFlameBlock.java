package com.windanesz.ancientspellcraft.block;

import com.koomplo.wizardry.api.content.util.MagicDamageSource;
import com.koomplo.wizardry.setup.registries.EBDamageSources;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

/** Chama arcana roxa: dano MAGIC em quem pisar; temporaria (1.12.2 BlockArcaneFlame). */
public class ArcaneFlameBlock extends TemporaryBlock {

    public ArcaneFlameBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull net.minecraft.world.level.BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public void entityInside(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Entity entity) {
        if (!level.isClientSide && entity instanceof LivingEntity living && living.tickCount % 10 == 0
                && !MagicDamageSource.isEntityImmune(EBDamageSources.MAGIC, living)) {
            var be = level.getBlockEntity(pos);
            var caster = be instanceof TemporaryBlockEntity temp ? temp.getCaster() : null;
            living.hurt(caster == null ? living.damageSources().magic()
                    : MagicDamageSource.causeDirectMagicDamage(caster, EBDamageSources.MAGIC), 1.0f);
        }
    }
}
