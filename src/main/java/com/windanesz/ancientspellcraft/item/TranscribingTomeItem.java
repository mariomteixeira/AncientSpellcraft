package com.windanesz.ancientspellcraft.item;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.api.content.item.ArtifactItem;
import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.util.RegistryUtils;
import com.koomplo.wizardry.content.item.ScrollItem;
import com.koomplo.wizardry.content.item.SpellBookItem;
import com.koomplo.wizardry.setup.registries.Spells;
import com.koomplo.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * Tome of Transcribing (1.12.2 ItemTranscribingTome, artefato charm): canaliza 3s com um scroll na
 * offhand e o tomo VIRA o spell book da spell do scroll (consome 1 scroll). O tipo de livro vem de
 * Spell.applicableForItem (scroll do AS -> livro do AS).
 */
public class TranscribingTomeItem extends ArtifactItem {

    public TranscribingTomeItem(Rarity rarity) {
        super(rarity, null);
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return 60;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity entity, @NotNull ItemStack stack, int remaining) {
        if (!level.isClientSide) return;
        ParticleBuilder.create(EBParticles.DUST, entity).color(0xf5ad42).velocity(0, 0.03, 0)
                .time(60).pos(0, 0.1, 0).scale(1.2f).spawn(level);
        ParticleBuilder.create(EBParticles.DUST, entity).color(0xbb28c9).velocity(0, 0.03, 0)
                .time(60).pos(0, 0.1, 0).scale(1.2f).spawn(level);
        ParticleBuilder.create(EBParticles.FLASH, entity).color(0xbb28c9).pos(0, 0.1, 0)
                .time(20).scale(1.2f).spawn(level);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity) {
        if (!(entity instanceof Player player)) return stack;
        player.getCooldowns().addCooldown(this, 60);
        ItemStack offhand = player.getOffhandItem();

        if (offhand.isEmpty()) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable(
                        "item.ancientspellcraft.charm_transcribing_tome.no_items_to_transcribe"), false);
            }
            return stack;
        }

        Spell spell = RegistryUtils.getSpell(offhand);
        if (offhand.getItem() instanceof ScrollItem && spell != Spells.NONE) {
            if (!level.isClientSide) {
                for (Item item : BuiltInRegistries.ITEM) {
                    if (item instanceof SpellBookItem && spell.applicableForItem(item)) {
                        offhand.shrink(1);
                        level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                                com.windanesz.ancientspellcraft.registry.ASSounds.TRANSMUTATION.get(), SoundSource.NEUTRAL, 1f, 1f);
                        return RegistryUtils.setSpell(new ItemStack(item), spell);
                    }
                }
            }
        } else if (!level.isClientSide) {
            player.displayClientMessage(Component.translatable(
                    "item.ancientspellcraft.charm_transcribing_tome.invalid_item"), false);
        }
        return stack;
    }
}
