package com.windanesz.ancientspellcraft.entity.construct;

import com.koomplo.wizardry.api.content.entity.construct.MagicConstructEntity;
import com.koomplo.wizardry.api.content.util.BlockUtil;
import com.koomplo.wizardry.core.AllyDesignation;
import com.windanesz.ancientspellcraft.block.TemporaryBlockEntity;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

/** Pedregulho fundido (1.12.2 EntityMoltenBoulder): rola deixando magma conjurado e fogo, incendeia quem tocar. */
public class MoltenBoulderConstruct extends MagicConstructEntity {

    private double velocityX, velocityZ;

    public MoltenBoulderConstruct(EntityType<?> type, Level world) {
        super(type, world);
    }

    public void setHorizontalVelocity(double x, double z) {
        this.velocityX = x;
        this.velocityZ = z;
    }

    @Override
    public void tick() {
        super.tick();
        setDeltaMovement(velocityX, onGround() ? 0 : getDeltaMovement().y - 0.08, velocityZ);
        move(MoverType.SELF, getDeltaMovement());

        if (!level().isClientSide && tickCount % 15 == 0) {
            LivingEntity caster = getCaster() instanceof LivingEntity living ? living : null;
            for (BlockPos pos : BlockUtil.getBlockSphere(blockPosition().below(), (int) getBbWidth())) {
                if (pos.getY() != blockPosition().below().getY() || level().isEmptyBlock(pos)) continue;
                if (random.nextInt(10) == 0 && level().isEmptyBlock(pos.above())
                        && !pos.above().equals(blockPosition())) {
                    level().setBlockAndUpdate(pos.above(), Blocks.FIRE.defaultBlockState());
                }
                if (caster != null && level().getBlockState(pos).getDestroySpeed(level(), pos) >= 0) {
                    TemporaryBlockEntity.place(caster, level(), ASBlocks.CONJURED_MAGMA.get(), pos, 600);
                }
            }
        }
        if (!level().isClientSide) {
            LivingEntity caster = getCaster() instanceof LivingEntity living ? living : null;
            for (LivingEntity entity : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox())) {
                if (caster != null && !AllyDesignation.isValidTarget(caster, entity)) continue;
                entity.igniteForSeconds(6);
            }
        }
    }
}
