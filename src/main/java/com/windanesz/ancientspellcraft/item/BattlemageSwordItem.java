package com.windanesz.ancientspellcraft.item;

import com.koomplo.wizardry.api.content.spell.SpellTier;
import com.koomplo.wizardry.api.content.util.InventoryUtil;
import com.koomplo.wizardry.content.item.WandItem;
import com.koomplo.wizardry.content.item.armor.WizardArmorItem;
import com.koomplo.wizardry.content.item.armor.WizardArmorType;
import com.koomplo.wizardry.setup.registries.Elements;
import com.windanesz.ancientspellcraft.handler.ASSpellEvents;
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
 * tier; golpes aplicam o efeito elemental do set battlemage vestido.
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
            // golpe totalmente carregado (runeword_empower/casts) = greater power, e reseta
            boolean charged = getCharge(stack) >= FULL_CHARGE;
            ElementalSwordEffects.hit(armor.getElement(), stack, target, player, charged);
            if (charged) resetCharge(stack);
            else addCharge(stack, com.windanesz.ancientspellcraft.ASServerConfig.SPELLBLADE_CHARGE_GAIN_PER_HIT.get());
        }
        return true;
    }

    // ---- carga da lâmina (1.12.2 WizardClassWeaponHelper.CHARGE_PROGRESS): 0-100 no CustomData ----

    public static final int FULL_CHARGE = 100;
    private static final String CHARGE_TAG = "SwordCharge";

    public static int getCharge(ItemStack sword) {
        net.minecraft.world.item.component.CustomData data = sword.get(DataComponents.CUSTOM_DATA);
        return data == null ? 0 : data.copyTag().getInt(CHARGE_TAG);
    }

    public static void addCharge(ItemStack sword, int amount) {
        net.minecraft.world.item.component.CustomData.update(DataComponents.CUSTOM_DATA, sword,
                tag -> tag.putInt(CHARGE_TAG, Math.min(tag.getInt(CHARGE_TAG) + amount, FULL_CHARGE)));
    }

    public static void resetCharge(ItemStack sword) {
        net.minecraft.world.item.component.CustomData.update(DataComponents.CUSTOM_DATA, sword,
                tag -> tag.putInt(CHARGE_TAG, 0));
    }

    /** runeword_shatter: golpe com carga da runeword derruba a guarda de quem bloqueia com escudo. */
    @Override
    public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {
        if (shield.getItem() instanceof net.minecraft.world.item.ShieldItem
                && "ancientspellcraft:runeword_shatter".equals(com.windanesz.ancientspellcraft.spell.RunewordSpell.getActive(stack))
                && com.windanesz.ancientspellcraft.spell.RunewordSpell.getCharges(stack) > 0) {
            com.windanesz.ancientspellcraft.spell.RunewordSpell.spendCharge(stack);
            return true;
        }
        return false;
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
        // LIGHTNING (1.12.2 onUpdateEffect): andar no chão com a espada e o set recarrega mana
        if (selected && entity instanceof Player player
                && ASSpellEvents.isWearingFullSet(player, WizardArmorType.BATTLEMAGE)
                && player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof WizardArmorItem armor
                && armor.getElement() != null) {
            ElementalSwordEffects.tick(armor.getElement(), stack, player);
        }
    }
}
