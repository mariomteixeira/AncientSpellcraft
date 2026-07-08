package com.windanesz.ancientspellcraft.entity;

import com.koomplo.wizardry.api.content.util.MagicDamageSource;
import com.koomplo.wizardry.setup.registries.EBDamageSources;
import com.windanesz.ancientspellcraft.registry.ASEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

/** Bloco levitante (1.12.2 EntityLevitatingBlock do wizardry): voa, fere quem atingir e se desfaz. */
public class LevitatingBlockEntity extends FallingBlockEntity {

    public float damageMultiplier = 1.0f;
    private UUID casterUUID;
    private BlockState state = net.minecraft.world.level.block.Blocks.STONE.defaultBlockState();

    public LevitatingBlockEntity(EntityType<? extends FallingBlockEntity> type, Level level) {
        super(type, level);
    }

    public LevitatingBlockEntity(Level level, double x, double y, double z, BlockState state) {
        this(ASEntities.LEVITATING_BLOCK.get(), level);
        this.state = state;
        this.blocksBuilding = true;
        this.setPos(x, y, z);
        this.setDeltaMovement(Vec3.ZERO);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.setStartPos(this.blockPosition());
        this.time = 1;
        this.dropItem = false;
    }

    public void setCaster(LivingEntity caster) {
        this.casterUUID = caster == null ? null : caster.getUUID();
    }

    @Override
    public @org.jetbrains.annotations.NotNull BlockState getBlockState() {
        return state;
    }

    @Override
    public void tick() {
        this.time++;
        if (this.time > 100) {
            if (!level().isClientSide) {
                level().levelEvent(2001, blockPosition(), net.minecraft.world.level.block.Block.getId(state));
                discard();
            }
            return;
        }
        // sem gravidade: levita na velocidade atual
        this.move(net.minecraft.world.entity.MoverType.SELF, getDeltaMovement());
        setDeltaMovement(getDeltaMovement().scale(0.98));

        if (!level().isClientSide && this.time % 2 == 0) {
            LivingEntity caster = casterUUID == null ? null
                    : level().getPlayerByUUID(casterUUID);
            for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(0.3))) {
                if (target == caster) continue;
                target.hurt(caster == null ? damageSources().fallingBlock(this)
                        : MagicDamageSource.causeIndirectMagicDamage(this, caster, EBDamageSources.FORCE),
                        4.0f * damageMultiplier);
            }
        }
    }
}
