package com.windanesz.ancientspellcraft.block;

import com.windanesz.ancientspellcraft.registry.ASBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/** BE compartilhado dos blocos temporarios do AS: caster + lifetime (ITemporaryBlock do 1.12.2). */
public class TemporaryBlockEntity extends BlockEntity {

    private int lifetime = -1;
    private UUID casterUUID;

    public TemporaryBlockEntity(BlockPos pos, BlockState state) {
        super(ASBlocks.TEMPORARY_BE.get(), pos, state);
    }

    public void setLifetime(int lifetime) {
        this.lifetime = lifetime;
    }

    public void setCaster(LivingEntity caster) {
        this.casterUUID = caster == null ? null : caster.getUUID();
    }

    public Player getCaster() {
        return casterUUID == null || level == null ? null : level.getPlayerByUUID(casterUUID);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TemporaryBlockEntity be) {
        if (be.lifetime > 0 && --be.lifetime == 0) {
            level.removeBlock(pos, false);
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Lifetime", lifetime);
        if (casterUUID != null) tag.putUUID("Caster", casterUUID);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        lifetime = tag.getInt("Lifetime");
        if (tag.hasUUID("Caster")) casterUUID = tag.getUUID("Caster");
    }
}
