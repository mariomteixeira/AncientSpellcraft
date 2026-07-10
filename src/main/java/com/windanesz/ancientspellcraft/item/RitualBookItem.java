package com.windanesz.ancientspellcraft.item;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.setup.registries.client.EBParticles;
import com.windanesz.ancientspellcraft.ritual.ASRituals;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/** Livro de ritual (1.12.2 ItemRitualBook): canaliza 3s com as runas exigidas jogadas no chao. */
public class RitualBookItem extends Item {

    public RitualBookItem() {
        super(new Properties().stacksTo(1));
    }

    public static String getRitual(ItemStack stack) {
        CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        return data.copyTag().getString("Ritual");
    }

    public static ItemStack withRitual(String ritual) {
        ItemStack stack = new ItemStack(com.windanesz.ancientspellcraft.registry.ASItems.RITUAL_BOOK.get());
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putString("Ritual", ritual));
        return stack;
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
        String ritual = getRitual(stack);
        if (ritual.isEmpty()) return InteractionResultHolder.pass(stack);
        // clique normal lê o livro; sneak canaliza o ritual (1.12.2: GUI vs montagem do pattern)
        if (!player.isShiftKeyDown()) {
            if (level.isClientSide) {
                com.windanesz.ancientspellcraft.client.ASClientHooks.openRitualBook(ritual);
            }
            return InteractionResultHolder.success(stack);
        }
        if (ASRituals.matchRunes(player, ritual) == null) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable("item.ancientspellcraft.ritual_book.missing_runes"), true);
            }
            return InteractionResultHolder.fail(stack);
        }
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity entity, @NotNull ItemStack stack, int remaining) {
        if (level.isClientSide) {
            ParticleBuilder.create(EBParticles.FLASH, entity).scale(0.6f).color(0x9d2cf3)
                    .pos(0, 0.2 + level.random.nextDouble(), 0).time(20).spawn(level);
        }
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity) {
        if (!(entity instanceof Player player) || level.isClientSide) return stack;
        String ritual = getRitual(stack);
        var matched = ASRituals.matchRunes(player, ritual);
        if (matched != null && ASRituals.finish(player, ritual)) {
            ASRituals.consumeRunes(player, ritual, matched);
        }
        return stack;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        String ritual = getRitual(stack);
        if (!ritual.isEmpty()) {
            // 1.12.2: nome em elder futhark + hint até descobrir com o Scroll of Identification
            if (net.neoforged.fml.loading.FMLEnvironment.dist.isClient()
                    && !com.windanesz.ancientspellcraft.client.RitualDiscoveryClient.isDiscovered(ritual)) {
                tooltip.add(Component.literal(com.windanesz.ancientspellcraft.util.LangUtils.toElderFuthark(
                        Component.translatable("ritual.ancientspellcraft." + ritual).getString())).withStyle(ChatFormatting.GOLD));
                tooltip.add(Component.translatable("item.ancientspellcraft.ritual_book.desc").withStyle(ChatFormatting.GRAY));
            } else {
                tooltip.add(Component.translatable("ritual.ancientspellcraft." + ritual).withStyle(ChatFormatting.GOLD));
            }
            tooltip.add(Component.translatable("item.ancientspellcraft.ritual_book.runes").withStyle(ChatFormatting.GRAY));
            ASRituals.RUNES_REQUIRED.getOrDefault(ritual, java.util.Map.of()).forEach((rune, count) ->
                    tooltip.add(Component.literal("  " + count + "x ")
                            .append(Component.translatable("item.ancientspellcraft." + rune)).withStyle(ChatFormatting.DARK_AQUA)));
        }
    }
}
