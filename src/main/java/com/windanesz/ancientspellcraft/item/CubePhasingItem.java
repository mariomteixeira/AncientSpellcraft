package com.windanesz.ancientspellcraft.item;

import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.core.platform.Services;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

/**
 * Cubo de Phasing (1.12.2 ItemCubePhasing): carrega 1 mana/s (cap 1000); use casta BLINK do
 * wizardry pagando com a carga do cubo (sneak-use recarrega por cristal, como os demais).
 * Desvio: o sneak do 1.12.2 castava phase_step — no port o sneak ficou com a recarga.
 */
public class CubePhasingItem extends ManaArtifactItem {

    public CubePhasingItem(Rarity rarity) {
        super(rarity, 1000, new com.koomplo.wizardry.core.IArtifactEffect() {
            @Override
            public void onTick(Player player, Level level, ItemStack artifact) {
                if (!level.isClientSide && player.tickCount % 20 == 0
                        && artifact.getItem() instanceof ManaArtifactItem mana && !mana.isManaFull(artifact)) {
                    mana.rechargeMana(artifact, 1);
                }
            }
        });
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (player.isShiftKeyDown()) return super.use(level, player, hand);
        ItemStack stack = player.getItemInHand(hand);
        var spell = Services.REGISTRY_UTIL.getSpell(ResourceLocation.fromNamespaceAndPath("ebwizardry", "blink"));
        if (spell == null) return InteractionResultHolder.pass(stack);
        int cost = spell.getCost();
        if (getMana(stack) < cost) return InteractionResultHolder.fail(stack);
        if (spell.cast(new PlayerCastContext(level, player, hand, 0, new SpellModifiers()))) {
            if (!level.isClientSide) consumeMana(stack, cost, player);
            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.fail(stack);
    }
}
