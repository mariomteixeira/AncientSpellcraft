package com.windanesz.ancientspellcraft.block;

import com.windanesz.ancientspellcraft.client.ArcaneAnvilMenu;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * BE da bigorna arcana. Receitas 1.12.2: hilt+blade -> espada novice; crystal_silver_ingot ->
 * crystal_silver_plating do Redux; espada+ingot -> proximo tier (exige a progressao do proximo tier).
 */
public class ArcaneAnvilBlockEntity extends BaseContainerBlockEntity {

    public static final int INPUT_0 = 0, INPUT_1 = 1, OUTPUT = 2, SIZE = 3;

    private NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);

    public ArcaneAnvilBlockEntity(BlockPos pos, BlockState state) {
        super(ASBlocks.ARCANE_ANVIL_BE.get(), pos, state);
    }

    public ItemStack computeResult() {
        ItemStack input0 = getItem(INPUT_0);
        ItemStack input1 = getItem(INPUT_1);
        if (input0.is(ASItems.BATTLEMAGE_SWORD_HILT.get()) && input1.is(ASItems.BATTLEMAGE_SWORD_BLADE.get())) {
            return new ItemStack(ASItems.BATTLEMAGE_SWORD_NOVICE.get());
        }
        if (input0.is(ASItems.CRYSTAL_SILVER_INGOT.get()) && input1.isEmpty()) {
            return new ItemStack(com.koomplo.wizardry.setup.registries.EBItems.CRYSTAL_SILVER_PLATING.get());
        }
        if (input1.is(ASItems.CRYSTAL_SILVER_INGOT.get())) {
            ItemStack upgraded = ItemStack.EMPTY;
            if (input0.is(ASItems.BATTLEMAGE_SWORD_NOVICE.get())) upgraded = new ItemStack(ASItems.BATTLEMAGE_SWORD_APPRENTICE.get());
            else if (input0.is(ASItems.BATTLEMAGE_SWORD_APPRENTICE.get())) upgraded = new ItemStack(ASItems.BATTLEMAGE_SWORD_ADVANCED.get());
            else if (input0.is(ASItems.BATTLEMAGE_SWORD_ADVANCED.get())) upgraded = new ItemStack(ASItems.BATTLEMAGE_SWORD_MASTER.get());
            if (!upgraded.isEmpty()) {
                // 1.12.2: o upgrade de tier exige a progressão do próximo tier (excedente é preservado)
                int required = ((com.windanesz.ancientspellcraft.item.BattlemageSwordItem) upgraded.getItem())
                        .getTier(upgraded).getProgression();
                int progression = com.koomplo.wizardry.api.content.util.CastItemDataHelper.getProgression(input0);
                if (progression < required) return ItemStack.EMPTY;
                upgraded.applyComponents(input0.getComponents());
                com.koomplo.wizardry.api.content.util.CastItemDataHelper.setProgression(upgraded, progression - required);
                return upgraded;
            }
        }
        return ItemStack.EMPTY;
    }

    public void updateResult() {
        setItem(OUTPUT, computeResult());
    }

    public void onTakeResult() {
        ItemStack input0 = getItem(INPUT_0);
        if (input0.is(ASItems.CRYSTAL_SILVER_INGOT.get())) {
            removeItem(INPUT_0, 1);
        } else {
            removeItem(INPUT_0, 1);
            removeItem(INPUT_1, 1);
        }
        updateResult();
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("container.ancientspellcraft.arcane_anvil");
    }

    @Override
    protected @NotNull NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(@NotNull NonNullList<ItemStack> newItems) {
        this.items = newItems;
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory) {
        return new ArcaneAnvilMenu(id, inventory, this);
    }

    @Override
    public int getContainerSize() {
        return SIZE;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, items, registries);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, items, registries);
    }
}
