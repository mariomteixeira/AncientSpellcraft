package com.windanesz.ancientspellcraft.block;

import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.util.RegistryUtils;
import com.koomplo.wizardry.content.item.CrystalItem;
import com.koomplo.wizardry.setup.registries.EBAttachments;
import com.koomplo.wizardry.setup.registries.Spells;
import com.windanesz.ancientspellcraft.client.ScribingDeskMenu;
import com.windanesz.ancientspellcraft.data.SpellComponents;
import com.windanesz.ancientspellcraft.item.RelicItem;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/** BE do scribing desk: reliquia + componentes + cristais + tinta + livro -> spell book do AS. */
public class ScribingDeskBlockEntity extends BaseContainerBlockEntity {

    public static final int INGREDIENT_1 = 0, INGREDIENT_2 = 1, INGREDIENT_3 = 2,
            CRYSTAL = 3, RELIC = 4, INK = 5, BOOK = 6, SIZE = 7;

    private NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);

    public ScribingDeskBlockEntity(BlockPos pos, BlockState state) {
        super(ASBlocks.SCRIBING_DESK_BE.get(), pos, state);
    }

    public static int researchCost(Spell spell) {
        return spell.getTier().getLevel() >= 3 ? 2 : 1;
    }

    public Spell getRelicSpell() {
        ItemStack relic = getItem(RELIC);
        if (!(relic.getItem() instanceof RelicItem)) return Spells.NONE;
        return RegistryUtils.getSpell(relic);
    }

    public boolean canScribe() {
        Spell spell = getRelicSpell();
        if (spell == Spells.NONE) return false;
        var entry = SpellComponents.entryFor(spell);
        if (entry == null) return false;
        List<net.minecraft.world.item.Item> components = SpellComponents.itemsOf(entry);
        for (int i = 0; i < components.size(); i++) {
            if (!getItem(INGREDIENT_1 + i).is(components.get(i))) return false;
        }
        return getItem(CRYSTAL).getItem() instanceof CrystalItem
                && getItem(CRYSTAL).getCount() >= researchCost(spell)
                && getItem(INK).is(Items.INK_SAC)
                && getItem(BOOK).is(Items.BOOK);
    }

    public void scribe(Player player) {
        if (!canScribe() || level == null || level.isClientSide) return;
        Spell spell = getRelicSpell();
        var entry = SpellComponents.entryFor(spell);
        for (int i = 0; i < SpellComponents.itemsOf(entry).size(); i++) {
            removeItem(INGREDIENT_1 + i, 1);
        }
        removeItem(CRYSTAL, researchCost(spell));
        removeItem(RELIC, 1);
        removeItem(INK, 1);
        removeItem(BOOK, 1);
        ItemStack book = RegistryUtils.setSpell(new ItemStack(ASItems.ANCIENT_SPELLCRAFT_SPELL_BOOK.get()), spell);
        setItem(BOOK, book);
        if (player.getData(EBAttachments.SPELL_MANAGER_DATA).discoverSpell(spell)) {
            player.sendSystemMessage(Component.translatable("spell.discover",
                    Component.translatable(spell.getDescriptionId())));
        }
        setChanged();
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("container.ancientspellcraft.scribing_desk");
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
        return new ScribingDeskMenu(id, inventory, this);
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
