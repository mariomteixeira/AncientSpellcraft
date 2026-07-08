package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.internal.CastContext;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.ArrayList;
import java.util.List;

/** Remove uma curse de item do offhand ou de uma armadura aleatoria (1.12.2 DispelItemCurse). */
public class DispelItemCurse extends ASBuffSpell {

    // Lista default do 1.12.2 (Settings.dispel_item_curse_list); ids de mods ausentes sao ignorados.
    // TODO configuravel quando o sistema de config do AS portar (AS-7)
    private static final List<String> CURSE_IDS = List.of(
            "minecraft:vanishing_curse", "minecraft:binding_curse",
            "charm:clumsiness_curse", "charm:harming_curse", "charm:haunting_curse", "charm:rusting_curse");

    public DispelItemCurse() {
        super(1f, 1f, 0.3f);
    }

    @Override
    protected boolean applyEffects(CastContext ctx, LivingEntity target) {
        if (!(target instanceof Player player) || player.level().isClientSide) return false;

        if (!player.getOffhandItem().isEmpty()) {
            return removeCurse(player, player.getOffhandItem());
        }
        List<ItemStack> armor = new ArrayList<>();
        player.getArmorSlots().forEach(stack -> {
            if (!stack.isEmpty()) armor.add(stack);
        });
        if (!armor.isEmpty()) {
            return removeCurse(player, armor.get(player.getRandom().nextInt(armor.size())));
        }
        return false;
    }

    private static boolean removeCurse(Player player, ItemStack stack) {
        var registry = player.level().registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        boolean[] removed = {false};
        EnchantmentHelper.updateEnchantments(stack, mutable -> mutable.removeIf(holder -> {
            ResourceLocation id = registry.getKey(holder.value());
            if (!removed[0] && id != null && CURSE_IDS.contains(id.toString())) {
                removed[0] = true;
                return true;
            }
            return false;
        }));
        return removed[0];
    }
}
