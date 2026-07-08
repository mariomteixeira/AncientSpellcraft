package com.windanesz.ancientspellcraft.item;

import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.spell.SpellTier;
import com.koomplo.wizardry.api.content.util.RegistryUtils;
import com.koomplo.wizardry.setup.registries.Spells;
import com.windanesz.ancientspellcraft.data.SpellComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/** Reliquia (1.12.2 ItemRelic): tabuleta com uma spell aleatoria do tier, decifravel no scribing desk. */
public class RelicItem extends Item {

    private final SpellTier tier;

    public RelicItem(SpellTier tier, Rarity rarity) {
        super(new Properties().stacksTo(16).rarity(rarity));
        this.tier = tier;
    }

    public SpellTier getTier() {
        return tier;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slot, boolean selected) {
        if (level.isClientSide || level.getGameTime() % 40 != 0 || !(entity instanceof Player)) return;
        if (RegistryUtils.getSpell(stack) != Spells.NONE) return;
        List<Spell> spells = SpellComponents.spellsByTier(tier);
        if (!spells.isEmpty()) {
            RegistryUtils.setSpell(stack, spells.get(level.random.nextInt(spells.size())));
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        Spell spell = RegistryUtils.getSpell(stack);
        if (spell == Spells.NONE) {
            tooltip.add(Component.translatable("item.ancientspellcraft.relic.unidentified").withStyle(ChatFormatting.GRAY));
            return;
        }
        tooltip.add(Component.translatable(spell.getDescriptionId()).withStyle(ChatFormatting.GOLD));
        var entry = SpellComponents.entryFor(spell);
        if (entry != null) {
            tooltip.add(Component.translatable("item.ancientspellcraft.relic.components").withStyle(ChatFormatting.GRAY));
            for (var item : SpellComponents.itemsOf(entry)) {
                tooltip.add(Component.literal("  ").append(item.getDescription()).withStyle(ChatFormatting.DARK_AQUA));
            }
        }
    }
}
