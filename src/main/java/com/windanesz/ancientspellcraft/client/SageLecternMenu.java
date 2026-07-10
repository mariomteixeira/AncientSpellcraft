package com.windanesz.ancientspellcraft.client;

import com.windanesz.ancientspellcraft.block.SageLecternBlockEntity;
import com.windanesz.ancientspellcraft.registry.ASMenus;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SageLecternMenu extends AbstractContainerMenu {

    private final Container container;
    private final SageLecternBlockEntity blockEntity;

    public SageLecternMenu(int id, Inventory playerInv) {
        this(id, playerInv, null);
    }

    public SageLecternMenu(int id, Inventory playerInv, SageLecternBlockEntity be) {
        super(ASMenus.SAGE_LECTERN.get(), id);
        this.blockEntity = be;
        this.container = be != null ? be : new SimpleContainer(SageLecternBlockEntity.SIZE);
        container.startOpen(playerInv.player);

        addSlot(new Slot(container, 0, 26, 24) {
            @Override
            public void setChanged() {
                super.setChanged();
                if (blockEntity != null) blockEntity.updateResult();
            }
        });
        addSlot(new Slot(container, 1, 75, 24) {
            @Override
            public void setChanged() {
                super.setChanged();
                if (blockEntity != null) blockEntity.updateResult();
            }
        });
        addSlot(new Slot(container, 2, 133, 24) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(@NotNull Player player, @NotNull ItemStack stack) {
                if (blockEntity != null) blockEntity.onTakeResult();
                super.onTake(player, stack);
            }
        });

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlot(new Slot(playerInv, x + y * 9 + 9, 8 + x * 18, 79 + y * 18));
            }
        }
        for (int x = 0; x < 9; x++) {
            addSlot(new Slot(playerInv, x, 8 + x * 18, 137));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();
        if (index < SageLecternBlockEntity.SIZE) {
            if (index == SageLecternBlockEntity.RESULT && blockEntity != null) blockEntity.onTakeResult();
            if (!moveItemStackTo(stack, SageLecternBlockEntity.SIZE, slots.size(), true)) return ItemStack.EMPTY;
        } else if (!moveItemStackTo(stack, 0, 2, false)) {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        return copy;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return container.stillValid(player);
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        container.stopOpen(player);
    }
}
