package com.windanesz.ancientspellcraft.block;

import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.util.RegistryUtils;
import com.koomplo.wizardry.content.item.ScrollItem;
import com.koomplo.wizardry.content.item.SpellBookItem;
import com.koomplo.wizardry.setup.registries.EBAttachments;
import com.koomplo.wizardry.setup.registries.Spells;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.NonNullList;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * Esfera da Cognicao (1.12.2 TileSphereCognizance): pesquisa spells nao descobertas gastando cristais.
 * 5% identifica, 35% falha com dica de falha, resto da uma dica por elemento/tipo.
 * Desvio documentado: pesquisa continua com a GUI fechada (1.12.2 exigia inUse); TODO relics/tablets e BER animado.
 */
public class SphereCognizanceBlockEntity extends BaseContainerBlockEntity {

    public static final int CRYSTAL_SLOT = 0;
    public static final int BOOK_SLOT = 1;
    public static final List<String> HINT_TYPES = List.of("none", "failed", "discovered", "heal_ally", "fire",
            "earth", "ice", "necromancy", "healing", "lightning", "sorcery", "ancient", "buff", "attack",
            "projectile", "defense", "utility", "construct", "minion", "alteration");
    private static final int[] HINT_COUNTS = {1, 9, 10, 4, 6, 5, 6, 5, 4, 4, 5, 7, 4, 4, 4, 4, 5, 4, 4, 4};

    private NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);
    public int researchProgress;
    public int researchDuration;
    public int hintTypeId;
    public int hintId;
    private UUID lastPlayer;

    public SphereCognizanceBlockEntity(BlockPos pos, BlockState state) {
        super(ASBlocks.SPHERE_COGNIZANCE_BE.get(), pos, state);
    }

    public void setLastPlayer(Player player) {
        this.lastPlayer = player.getUUID();
    }

    private Spell currentSpell() {
        ItemStack stack = items.get(BOOK_SLOT);
        if (stack.getItem() instanceof SpellBookItem || stack.getItem() instanceof ScrollItem) {
            Spell spell = RegistryUtils.getSpell(stack);
            return spell == Spells.NONE ? null : spell;
        }
        return null;
    }

    public static int researchDurationFor(Spell spell) {
        return 100 + spell.getTier().getLevel() * 50; // 100/150/200/250 do 1.12.2
    }

    public static int researchCostFor(Spell spell) {
        return spell.getTier().getLevel() >= 3 ? 2 : 1;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, SphereCognizanceBlockEntity be) {
        Spell spell = be.currentSpell();
        if (spell == null) {
            be.researchProgress = 0;
            be.researchDuration = 0;
            return;
        }
        ItemStack crystal = be.items.get(CRYSTAL_SLOT);
        if (crystal.isEmpty() || !(crystal.getItem() instanceof com.koomplo.wizardry.content.item.CrystalItem)
                || crystal.getCount() < researchCostFor(spell)) {
            return;
        }
        be.researchDuration = researchDurationFor(spell);
        if (++be.researchProgress >= be.researchDuration) {
            be.researchProgress = 0;
            crystal.shrink(researchCostFor(spell));
            be.completeResearch(level, spell);
            be.setChanged();
        }
    }

    private void completeResearch(Level level, Spell spell) {
        double roll = level.random.nextDouble();
        Player player = lastPlayer == null ? null : level.getPlayerByUUID(lastPlayer);
        if (roll < 0.05 && player != null) {
            hintTypeId = 2;
            hintId = 1 + level.random.nextInt(HINT_COUNTS[2]);
            if (player.getData(EBAttachments.SPELL_MANAGER_DATA).discoverSpell(spell)) {
                player.sendSystemMessage(Component.translatable("spell.discover", spell.getDescriptionFormatted()));
            }
        } else if (roll < 0.4) {
            hintTypeId = 1;
            hintId = 1 + level.random.nextInt(HINT_COUNTS[1]);
        } else {
            String key = level.random.nextBoolean()
                    ? spell.getElement().getName()
                    : spell.getType().name().toLowerCase();
            int idx = HINT_TYPES.indexOf(key);
            hintTypeId = idx >= 0 ? idx : 0;
            hintId = 1 + level.random.nextInt(HINT_COUNTS[Math.max(idx, 0)]);
        }
    }

    // ===== container =====

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("block.ancientspellcraft.sphere_cognizance");
    }

    @Override
    protected @NotNull NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(@NotNull NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    protected net.minecraft.world.inventory.@NotNull AbstractContainerMenu createMenu(int id, net.minecraft.world.entity.player.@NotNull Inventory inventory) {
        return new com.windanesz.ancientspellcraft.client.SphereCognizanceMenu(id, inventory, this);
    }

    @Override
    public int getContainerSize() {
        return 2;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, items, registries);
        tag.putInt("Progress", researchProgress);
        tag.putInt("HintType", hintTypeId);
        tag.putInt("HintId", hintId);
        if (lastPlayer != null) tag.putUUID("LastPlayer", lastPlayer);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        items = NonNullList.withSize(2, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, items, registries);
        researchProgress = tag.getInt("Progress");
        hintTypeId = tag.getInt("HintType");
        hintId = tag.getInt("HintId");
        if (tag.hasUUID("LastPlayer")) lastPlayer = tag.getUUID("LastPlayer");
    }
}
