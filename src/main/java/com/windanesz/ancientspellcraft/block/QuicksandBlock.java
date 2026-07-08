package com.windanesz.ancientspellcraft.block;

import com.koomplo.wizardry.api.content.util.MagicDamageSource;
import com.koomplo.wizardry.setup.registries.EBDamageSources;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/** Areia movedica (1.12.2 BlockQuickSand): prende como teia e afoga quem submergir. */
public class QuicksandBlock extends TemporaryBlock {

    public QuicksandBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void entityInside(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Entity entity) {
        entity.makeStuckInBlock(state, new Vec3(0.25D, 0.05D, 0.25D));
        if (!level.isClientSide && entity instanceof LivingEntity living
                && level.getBlockState(BlockPos.containing(living.getEyePosition())).is(this)
                && living.tickCount % 20 == 0) {
            var be = level.getBlockEntity(pos);
            var caster = be instanceof TemporaryBlockEntity temp ? temp.getCaster() : null;
            living.hurt(caster == null ? living.damageSources().flyIntoWall()
                    : MagicDamageSource.causeDirectMagicDamage(caster, EBDamageSources.MAGIC), 0.5f);
        }
    }
}
