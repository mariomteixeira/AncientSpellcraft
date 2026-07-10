package com.windanesz.ancientspellcraft.item;

import com.koomplo.wizardry.api.content.spell.SpellTier;
import com.koomplo.wizardry.api.content.util.InventoryUtil;
import com.koomplo.wizardry.content.item.WandItem;
import com.koomplo.wizardry.content.item.armor.WizardArmorItem;
import com.koomplo.wizardry.content.item.armor.WizardArmorType;
import com.koomplo.wizardry.setup.registries.Elements;
import com.windanesz.ancientspellcraft.handler.ASSpellEvents;
import com.windanesz.ancientspellcraft.spell.WarlockSpellEffects;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jetbrains.annotations.NotNull;

/**
 * Espada do battlemage (1.12.2 ItemBattlemageSword): wand completa do Redux COM dano melee por
 * tier; golpes aplicam o efeito elemental do set battlemage vestido. TODO: progression para
 * upgrade no anvil, texturas por elemento, efeitos EnumElementalSwordEffect 1:1.
 */
public class BattlemageSwordItem extends WandItem {

    private final float attackDamage;

    public BattlemageSwordItem(SpellTier tier, float attackDamage) {
        super(tier, Elements.MAGIC);
        this.attackDamage = attackDamage;
    }

    @Override
    public @NotNull ItemAttributeModifiers getDefaultAttributeModifiers(@NotNull ItemStack stack) {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID,
                        attackDamage, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID,
                        -2.4, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build();
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        if (attacker instanceof Player player
                && ASSpellEvents.isWearingFullSet(player, WizardArmorType.BATTLEMAGE)
                && player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof WizardArmorItem armor
                && armor.getElement() != null) {
            WarlockSpellEffects.affectEntity(target, armor.getElement(), attacker, false);
        }
        return true;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull net.minecraft.world.level.Level level,
                              @NotNull net.minecraft.world.entity.Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
        // fury decai 1 stack a cada 2s (1.12.2 RunewordFury.tick)
        if (!level.isClientSide && level.getGameTime() % 40 == 0
                && com.windanesz.ancientspellcraft.spell.RunewordSpell.getFuryStacks(stack) > 0) {
            com.windanesz.ancientspellcraft.spell.RunewordSpell.setFuryStacks(stack,
                    com.windanesz.ancientspellcraft.spell.RunewordSpell.getFuryStacks(stack) - 1);
        }
    }
}
