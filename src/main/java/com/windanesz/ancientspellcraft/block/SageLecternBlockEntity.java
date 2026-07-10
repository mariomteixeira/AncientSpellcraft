package com.windanesz.ancientspellcraft.block;

import com.windanesz.ancientspellcraft.client.SageLecternMenu;
import com.windanesz.ancientspellcraft.item.SageTomeItem;
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
 * BE do Sage Lectern (1.12.2 TileSageLectern): guarda o livro exposto e faz a progressão de tomos —
 * tomo + enchanted pages (5 x ordinal do próximo tier) = tomo do tier seguinte, componentes
 * preservados. Desvio: sem exigência de progression por uso (mesma simplificação do Arcane Anvil).
 */
public class SageLecternBlockEntity extends BaseContainerBlockEntity {

    public static final int BOOK = 0, PAGES = 1, RESULT = 2, SIZE = 3;

    private NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);

    public SageLecternBlockEntity(BlockPos pos, BlockState state) {
        super(ASBlocks.SAGE_LECTERN_BE.get(), pos, state);
    }

    public ItemStack getBook() {
        return getItem(BOOK);
    }

    public boolean hasBook() {
        return !getItem(BOOK).isEmpty();
    }

    /** Páginas necessárias para subir do tier atual (5 x ordinal do próximo: 5/10/15). */
    public static int requiredPages(ItemStack tome) {
        if (tome.is(ASItems.SAGE_TOME_NOVICE.get())) return 5;
        if (tome.is(ASItems.SAGE_TOME_APPRENTICE.get())) return 10;
        if (tome.is(ASItems.SAGE_TOME_ADVANCED.get())) return 15;
        return -1;
    }

    private static ItemStack nextTierTome(ItemStack tome) {
        if (tome.is(ASItems.SAGE_TOME_NOVICE.get())) return new ItemStack(ASItems.SAGE_TOME_APPRENTICE.get());
        if (tome.is(ASItems.SAGE_TOME_APPRENTICE.get())) return new ItemStack(ASItems.SAGE_TOME_ADVANCED.get());
        if (tome.is(ASItems.SAGE_TOME_ADVANCED.get())) return new ItemStack(ASItems.SAGE_TOME_MASTER.get());
        return ItemStack.EMPTY;
    }

    public ItemStack computeResult() {
        ItemStack tome = getItem(BOOK);
        ItemStack pages = getItem(PAGES);
        if (!(tome.getItem() instanceof SageTomeItem) || !pages.is(ASItems.ENCHANTED_PAGE.get())) return ItemStack.EMPTY;
        int required = requiredPages(tome);
        if (required < 0 || pages.getCount() < required) return ItemStack.EMPTY;
        ItemStack upgraded = nextTierTome(tome);
        if (!upgraded.isEmpty()) upgraded.applyComponents(tome.getComponents());
        return upgraded;
    }

    public void updateResult() {
        setItem(RESULT, computeResult());
    }

    public void onTakeResult() {
        int required = requiredPages(getItem(BOOK));
        removeItem(BOOK, 1);
        removeItem(PAGES, Math.max(required, 0));
        updateResult();
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("container.ancientspellcraft.sage_lectern");
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
        return new SageLecternMenu(id, inventory, this);
    }

    @Override
    public int getContainerSize() {
        return SIZE;
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack stack) {
        super.setItem(slot, stack);
        if (slot == BOOK && level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
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

    @Override
    public CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }
}
