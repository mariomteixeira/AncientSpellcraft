package com.windanesz.ancientspellcraft.block;

import com.windanesz.ancientspellcraft.registry.ASBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** XP guardado do Artefact Pensive (1.12.2 TileArtefactPensive). */
public class ArtefactPensiveBlockEntity extends BlockEntity {

    private int storedXp;

    public ArtefactPensiveBlockEntity(BlockPos pos, BlockState state) {
        super(ASBlocks.ARTEFACT_PENSIVE_BLOCK_ENTITY.get(), pos, state);
    }

    public int getStoredXp() {
        return storedXp;
    }

    public void setStoredXp(int storedXp) {
        this.storedXp = storedXp;
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("amount", storedXp);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        storedXp = tag.getInt("amount");
    }
}
