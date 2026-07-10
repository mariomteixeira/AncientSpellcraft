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
 * por 8s em todas as criaturas num raio de 15 (cooldown de 60s). TODO marco 6: os delegates
 * anti-magia do devoritium (IDevoritium) quando os handlers do material forem portados.
 */
public class DevoritiumMagnetItem extends ArtifactItem {

    public DevoritiumMagnetItem(Rarity rarity) {
        super(rarity, null);
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
