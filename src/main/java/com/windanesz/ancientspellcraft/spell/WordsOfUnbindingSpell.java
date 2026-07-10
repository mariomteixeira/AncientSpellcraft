package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.item.ICastItem;
import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperties;
import com.koomplo.wizardry.api.content.util.CastItemDataHelper;
import com.koomplo.wizardry.setup.registries.WandUpgrades;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Words of Unbinding (1.12.2 WordsOfUnbinding): desfaz um upgrade da wand na mão principal e
 * devolve o item — com o item de upgrade na offhand escolhe qual; sem, remove o primeiro que
 * achar. Usa a API removeUpgrade do Redux 0.1.27. Com ring_disenchanter, remove os
 * ENCANTAMENTOS do item na offhand em vez de mexer na wand.
 */
public class WordsOfUnbindingSpell extends Spell {

    @Override
    public boolean cast(PlayerCastContext ctx) {
        var caster = ctx.caster();

        // ring_disenchanter (1.12.2): desencanta o item da offhand
        if (com.koomplo.wizardry.core.integrations.ArtifactChannel.isEquipped(caster, ASItems.RING_DISENCHANTER.get())) {
            ItemStack offhandStack = caster.getOffhandItem();
            if (offhandStack.isEnchanted()) {
                if (!ctx.world().isClientSide) {
                    offhandStack.set(net.minecraft.core.component.DataComponents.ENCHANTMENTS,
                            net.minecraft.world.item.enchantment.ItemEnchantments.EMPTY);
                    this.playSound(ctx.world(), caster, ctx.castingTicks(), -1);
                }
                return true;
            }
            return false;
        }

        ItemStack wand = caster.getMainHandItem();
        if (!(wand.getItem() instanceof ICastItem)) {
            caster.displayClientMessage(Component.translatable("spell.ancientspellcraft.words_of_unbinding.no_wand"), true);
            return false;
        }
        if (ctx.world().isClientSide) return true;

        Item toRemove = null;
        Item offhand = caster.getOffhandItem().getItem();
        if (WandUpgrades.getWandUpgrades().containsKey(offhand)) {
            toRemove = offhand;
        } else {
            for (Item upgrade : WandUpgrades.getWandUpgrades().keySet()) {
                if (CastItemDataHelper.getUpgradeLevel(wand, upgrade) > 0) {
                    toRemove = upgrade;
                    break;
                }
            }
        }
        if (toRemove == null || !CastItemDataHelper.removeUpgrade(wand, toRemove)) {
            caster.displayClientMessage(Component.translatable("spell.ancientspellcraft.words_of_unbinding.no_upgrades"), true);
            return false;
        }
        caster.getInventory().placeItemBackInInventory(new ItemStack(toRemove));
        this.playSound(ctx.world(), caster, ctx.castingTicks(), -1);
        return true;
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == ASItems.ANCIENT_SPELLCRAFT_SPELL_BOOK.get() || item == ASItems.ANCIENT_SPELLCRAFT_SCROLL.get();
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.empty();
    }
}
