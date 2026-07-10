package com.windanesz.ancientspellcraft.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * Entidade fantasma usada como render view do sistema de câmera client (1.12.2 ASFakePlayer).
 * Só existe no ClientLevel; nunca é salva nem sincronizada.
 */
public class CameraDummyEntity extends Entity {

    public CameraDummyEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {}
}
