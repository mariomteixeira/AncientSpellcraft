package com.windanesz.ancientspellcraft.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

/**
 * Amuleto da Resistência (1.12.2 ItemAmuletOfResistance): sneak-use com uma poção na offhand
 * IMBUI o primeiro efeito dela (consome a poção); enquanto tiver mana (600, -1/s), o efeito
 * imbuído é removido de você na hora — imunidade que drena a reserva.
 */
public class ResistanceAmuletItem extends ManaArtifactItem {

    public static final String IMBUED_TAG = "ImbuedEffect";

    public ResistanceAmuletItem(Rarity rarity) {
        super(rarity, 600, new com.koomplo.wizardry.core.IArtifactEffect() {
            @Override
            public void onTick(Player player, Level level, ItemStack artifact) {
                if (level.isClientSide || !(artifact.getItem() instanceof ResistanceAmuletItem amulet)) return;
                CustomData data = artifact.get(DataComponents.CUSTOM_DATA);
                if (data == null || !data.copyTag().contains(IMBUED_TAG)) return;
                if (amulet.getMana(artifact) <= 0) return;
                var id = ResourceLocation.tryParse(data.copyTag().getString(IMBUED_TAG));
                var holder = id == null ? null : BuiltInRegistries.MOB_EFFECT.getHolder(id).orElse(null);
                if (holder == null) return;
                if (player.tickCount % 20 == 0) amulet.consumeMana(artifact, 1, player);
                if (player.hasEffect(holder)) player.removeEffect(holder);
            }
        });
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            ItemStack offhand = player.getOffhandItem();
            var contents = offhand.get(DataComponents.POTION_CONTENTS);
            if (contents != null) {
                for (var effect : contents.getAllEffects()) {
                    if (!level.isClientSide) {
                        var id = BuiltInRegistries.MOB_EFFECT.getKey(effect.getEffect().value());
                        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putString(IMBUED_TAG, id.toString()));
                        offhand.shrink(1);
                        player.displayClientMessage(Component.translatable(
                                "item.ancientspellcraft.amulet_of_resistance.imbued", effect.getEffect().value().getDisplayName()), true);
                    }
                    return InteractionResultHolder.success(stack);
                }
            }
            return super.use(level, player, hand); // sneak sem poção: recarga por cristal
        }
        return InteractionResultHolder.pass(stack);
    }
}
