package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperties;
import com.windanesz.ancientspellcraft.registry.ASAttachments;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Attire Alteration (1.12.2 AttireAlteration): troca instantânea entre a armadura vestida e um
 * conjunto guardado (4 slots no attachment PLAYER_DATA). Com charm_wardrobe vira um guarda-roupa
 * de 5 conjuntos: cada cast salva o atual e veste o próximo do ciclo.
 */
public class AttireAlterationSpell extends Spell {

    private static final EquipmentSlot[] ARMOR_SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    @Override
    public boolean cast(PlayerCastContext ctx) {
        Player player = ctx.caster();
        if (!ctx.world().isClientSide) {
            boolean wardrobe = com.koomplo.wizardry.core.integrations.ArtifactChannel.isEquipped(
                    player, ASItems.CHARM_WARDROBE.get());
            CompoundTag tag = player.getData(ASAttachments.PLAYER_DATA);
            // sem o charm: 1 conjunto ("Attire"); com o charm: ciclo de 5 ("Wardrobe<N>Attire")
            String prefix = "Attire";
            if (wardrobe) {
                int current = tag.getInt("CurrentWardrobeSet");
                int next = (current + 1) % 5;
                tag.putInt("CurrentWardrobeSet", next);
                prefix = "Wardrobe" + next + "Attire";
            }
            for (EquipmentSlot slot : ARMOR_SLOTS) {
                String key = prefix + slot.getName();
                ItemStack stored = tag.contains(key)
                        ? ItemStack.parseOptional(ctx.world().registryAccess(), tag.getCompound(key))
                        : ItemStack.EMPTY;
                ItemStack worn = player.getItemBySlot(slot);
                if (worn.isEmpty()) {
                    tag.remove(key);
                } else {
                    tag.put(key, worn.save(ctx.world().registryAccess()));
                }
                player.setItemSlot(slot, stored);
            }
            player.setData(ASAttachments.PLAYER_DATA, tag);
        }
        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
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
