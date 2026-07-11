package com.windanesz.ancientspellcraft.item;

import com.koomplo.wizardry.api.content.item.ArtifactItem;
import com.koomplo.wizardry.api.content.util.EntityUtil;
import com.windanesz.ancientspellcraft.registry.ASEffects;
import com.windanesz.ancientspellcraft.registry.ASSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * Devoritium Magnet (1.12.2 ItemDevoritiumMagnet): use toca o berrante e aplica exaustão mágica V
 * por 8s em todas as criaturas num raio de 15 (cooldown de 60s). Carregar/equipar pune com exaustão
 * e bater com ele escala a exaustão do alvo (delegates do IDevoritium).
 */
public class DevoritiumMagnetItem extends ArtifactItem implements com.windanesz.ancientspellcraft.material.IDevoritium {

    public DevoritiumMagnetItem(Rarity rarity) {
        // onTick cobre o magnet equipado no Curios (1.12.2 onWornTick — exaustão I)
        super(rarity, new com.koomplo.wizardry.core.IArtifactEffect() {
            @Override
            public void onTick(Player player, Level level, ItemStack artifact) {
                com.windanesz.ancientspellcraft.material.IDevoritium.applyCarryPenalty(artifact, level, player, false);
            }
        });
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull net.minecraft.world.entity.Entity entity, int slotId, boolean isSelected) {
        onUpdateDelegate(stack, level, entity, slotId, isSelected);
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull net.minecraft.world.entity.LivingEntity target, @NotNull net.minecraft.world.entity.LivingEntity attacker) {
        hitEntityDelegate(attacker, target);
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(this)) return InteractionResultHolder.fail(stack);
        player.getCooldowns().addCooldown(this, 1200);
        if (!level.isClientSide) {
            for (var target : EntityUtil.getLivingWithinRadius(15, player.getX(), player.getY(), player.getZ(), level)) {
                if (target instanceof PathfinderMob) {
                    target.addEffect(new MobEffectInstance(ASEffects.MAGICAL_EXHAUSTION, 160, 4));
                }
            }
        }
        player.swing(hand);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                ASSounds.WAR_HORN.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        return InteractionResultHolder.success(stack);
    }
}
