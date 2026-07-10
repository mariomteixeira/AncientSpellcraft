package com.windanesz.ancientspellcraft.item;

import com.koomplo.wizardry.api.content.item.IManaItem;
import com.koomplo.wizardry.content.item.armor.WizardArmorType;
import com.koomplo.wizardry.core.integrations.ArtifactChannel;
import com.windanesz.ancientspellcraft.handler.ASSpellEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Escudo do battlemage (1.12.2 ItemBattlemageShield): reserva de mana no lugar da durabilidade
 * (default 1000 do runic_shield_durability — config no marco 7); bloquear gasta a mana
 * (ASSpellEvents.onShieldBlock); mana vazia = guarda cai e não levanta; +5 de armadura e +5 de
 * toughness na offhand; só battlemage de set completo levanta a guarda. Com charm_glyph_shield_disable
 * equipado o cooldown de quebra de guarda (machado/runeword_shatter) some. Desvios: recarga por
 * sneak-use com magic_crystal na outra mão (o 1.12.2 usava workbench/condenser/flask; upgrades do
 * escudo ficam pro marco 7) e sem os 3 slots de artefato (Curios cobre o equipar).
 */
public class BattlemageShieldItem extends ShieldItem implements IManaItem {

    private static final String MANA_TAG = "Mana";
    private static final int CAPACITY = 1000;

    public BattlemageShieldItem() {
        super(new Properties().stacksTo(1).attributes(ItemAttributeModifiers.builder()
                .add(Attributes.ARMOR, new AttributeModifier(
                        rl("shield_armor"), 5.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.OFFHAND)
                .add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(
                        rl("shield_toughness"), 5.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.OFFHAND)
                .build()));
    }

    @Override
    public int getMana(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null || !data.copyTag().contains(MANA_TAG)) return CAPACITY;
        return data.copyTag().getInt(MANA_TAG);
    }

    @Override
    public void setMana(ItemStack stack, int mana) {
        int clamped = Math.max(0, Math.min(mana, CAPACITY));
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putInt(MANA_TAG, clamped));
    }

    @Override
    public int getManaCapacity(ItemStack stack) {
        return CAPACITY;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!ASSpellEvents.isWearingFullSet(player, WizardArmorType.BATTLEMAGE)) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable(
                        "item.ancientspellcraft.battlemage_shield.not_battlemage"), false);
            }
            return InteractionResultHolder.fail(stack);
        }
        // recarga: sneak-use com magic_crystal na outra mão
        if (player.isShiftKeyDown()) {
            InteractionHand other = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
            ItemStack crystal = player.getItemInHand(other);
            if (crystal.is(com.koomplo.wizardry.setup.registries.EBItems.MAGIC_CRYSTAL.get()) && !isManaFull(stack)) {
                if (!level.isClientSide) {
                    crystal.shrink(1);
                    rechargeMana(stack, com.koomplo.wizardry.core.config.EBServerConfig.MANA_PER_CRYSTAL.get());
                }
                return InteractionResultHolder.success(stack);
            }
        }
        if (isManaEmpty(stack)) return InteractionResultHolder.fail(stack);
        return super.use(level, player, hand);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slot, boolean selected) {
        // 1.12.2 linha 355: com o glyph equipado, o cooldown de quebra de guarda some
        if (!level.isClientSide && entity instanceof Player player
                && player.getCooldowns().isOnCooldown(this)
                && ArtifactChannel.isEquipped(player,
                com.windanesz.ancientspellcraft.registry.ASItems.CHARM_GLYPH_SHIELD_DISABLE.get())) {
            player.getCooldowns().removeCooldown(this);
        }
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return getMana(stack) < CAPACITY;
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        return Math.round(13.0F * getMana(stack) / CAPACITY);
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        return 0x56e8e3;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("item.ancientspellcraft.mana_artifact.mana",
                getMana(stack), CAPACITY).withStyle(ChatFormatting.BLUE));
    }

    private static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath("ancientspellcraft", path);
    }
}
