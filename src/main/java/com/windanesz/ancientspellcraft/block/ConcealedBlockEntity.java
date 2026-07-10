package com.windanesz.ancientspellcraft.block;

import com.windanesz.ancientspellcraft.registry.ASBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Guarda o estado + block entity do bloco escondido pelo conceal_object; revert() repõe tudo. */
public class ConcealedBlockEntity extends BlockEntity {

    private CompoundTag oldStateTag;
    private CompoundTag tileTag;

    public ConcealedBlockEntity(BlockPos pos, BlockState state) {
        super(ASBlocks.CONCEALED_BLOCK_ENTITY.get(), pos, state);
    }

    public void store(BlockState oldState, CompoundTag tileData) {
        this.oldStateTag = NbtUtils.writeBlockState(oldState);
        this.tileTag = tileData;
        setChanged();
    }

    public void revert() {
        if (level == null || level.isClientSide) return;
        BlockState oldState = oldStateTag == null ? Blocks.AIR.defaultBlockState()
                : NbtUtils.readBlockState(level.holderLookup(Registries.BLOCK), oldStateTag);
        CompoundTag storedTile = tileTag;
        BlockPos pos = worldPosition;
        level.setBlockAndUpdate(pos, oldState);
        if (storedTile != null && level.getBlockEntity(pos) != null) {
            level.getBlockEntity(pos).loadWithComponents(storedTile, level.registryAccess());
            level.getBlockEntity(pos).setChanged();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (oldStateTag != null) tag.put("OldState", oldStateTag);
        if (tileTag != null) tag.put("TileData", tileTag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("OldState")) oldStateTag = tag.getCompound("OldState");
        if (tag.contains("TileData")) tileTag = tag.getCompound("TileData");
    }
}
