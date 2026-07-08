package com.windanesz.ancientspellcraft.client;

import com.windanesz.ancientspellcraft.block.ScribingDeskBlockEntity;
import com.windanesz.ancientspellcraft.item.RelicItem;
import com.windanesz.ancientspellcraft.registry.ASMenus;
import com.koomplo.wizardry.content.item.CrystalItem;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class ScribingDeskMenu extends AbstractContainerMenu {

    private final Container container;
    private final ScribingDeskBlockEntity blockEntity;

    public ScribingDeskMenu(int id, Inventory playerInv) {
        this(id, playerInv, null);
    }

    public ScribingDeskMenu(int id, Inventory playerInv, ScribingDeskBlockEntity be) {
        super(ASMenus.SCRIBING_DESK.get(), id);
        this.blockEntity = be;
        this.container = be != null ? be : new SimpleContainer(ScribingDeskBlockEntity.SIZE);
        container.startOpen(playerInv.player);

        addSlot(new Slot(container, 0, 54, 61));
        addSlot(new Slot(container, 1, 80, 61));
        addSlot(new Slot(container, 2, 106, 61));
        addSlot(new Slot(container, 3, 19, 61) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.getItem() instanceof CrystalItem;
            }
        });
        addSlot(new Slot(container, 4, 19, 28) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.getItem() instanceof RelicItem;
            }
        });
        addSlot(new Slot(container, 5, 141, 38) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.is(Items.INK_SAC);
            }
        });
        addSlot(new Slot(container, 6, 80, 28));

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlot(new Slot(playerInv, x + y * 9 + 9, 8 + x * 18, 90 + y * 18));
            }
        }
        for (int x = 0; x < 9; x++) {
            addSlot(new Slot(playerInv, x, 8 + x * 18, 148));
        }
    }

    public boolean canScribe() {
        return blockEntity != null && blockEntity.canScribe();
    }

    @Override
    public boolean clickMenuButton(@NotNull Player player, int button) {
        if (button == 0 && blockEntity != null) {
            blockEntity.scribe(player);
            return true;
        }
        return false;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();
        if (index < ScribingDeskBlockEntity.SIZE) {
            if (!moveItemStackTo(stack, ScribingDeskBlockEntity.SIZE, slots.size(), true)) return ItemStack.EMPTY;
        } else if (!moveItemStackTo(stack, 0, ScribingDeskBlockEntity.SIZE, false)) {
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
