package com.windanesz.ancientspellcraft.client;

import com.koomplo.wizardry.content.item.CrystalItem;
import com.koomplo.wizardry.content.item.ScrollItem;
import com.koomplo.wizardry.content.item.SpellBookItem;
import com.windanesz.ancientspellcraft.block.SphereCognizanceBlockEntity;
import com.windanesz.ancientspellcraft.registry.ASMenus;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SphereCognizanceMenu extends AbstractContainerMenu {

    private final Container container;
    public final ContainerData data;

    public SphereCognizanceMenu(int id, Inventory playerInv) {
        this(id, playerInv, new SimpleContainer(2), new SimpleContainerData(4));
    }

    public SphereCognizanceMenu(int id, Inventory playerInv, SphereCognizanceBlockEntity be) {
        this(id, playerInv, be, new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> be.researchProgress;
                    case 1 -> be.researchDuration;
                    case 2 -> be.hintTypeId;
                    default -> be.hintId;
                };
            }

            @Override
            public void set(int index, int value) {
            }

            @Override
            public int getCount() {
                return 4;
            }
        });
    }

    public SphereCognizanceMenu(int id, Inventory playerInv, Container container, ContainerData data) {
        super(ASMenus.SPHERE_COGNIZANCE.get(), id);
        this.container = container;
        this.data = data;
        addSlot(new Slot(container, SphereCognizanceBlockEntity.CRYSTAL_SLOT, 14, 90) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.getItem() instanceof CrystalItem;
            }
        });
        addSlot(new Slot(container, SphereCognizanceBlockEntity.BOOK_SLOT, 80, 22) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.getItem() instanceof SpellBookItem || stack.getItem() instanceof ScrollItem
                        || stack.getItem() instanceof com.windanesz.ancientspellcraft.item.RelicItem;
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 9; col++)
                addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 120 + row * 18));
        for (int col = 0; col < 9; col++)
            addSlot(new Slot(playerInv, col, 8 + col * 18, 178));
        addDataSlots(data);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        if (index < 2) {
            if (!moveItemStackTo(stack, 2, slots.size(), true)) return ItemStack.EMPTY;
        } else if (stack.getItem() instanceof CrystalItem) {
            if (!moveItemStackTo(stack, 0, 1, false)) return ItemStack.EMPTY;
        } else if (stack.getItem() instanceof SpellBookItem || stack.getItem() instanceof ScrollItem
                || stack.getItem() instanceof com.windanesz.ancientspellcraft.item.RelicItem) {
            if (!moveItemStackTo(stack, 1, 2, false)) return ItemStack.EMPTY;
        } else {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
        else slot.setChanged();
        return original;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return container.stillValid(player);
    }
}
