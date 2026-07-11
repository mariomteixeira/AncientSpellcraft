package com.windanesz.ancientspellcraft.item;

import com.koomplo.wizardry.api.content.item.ArtifactItem;
import com.koomplo.wizardry.core.IArtifactEffect;
import com.windanesz.ancientspellcraft.registry.ASSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Diamond Goose (1.12.2 ItemDiamondGoose): sneak-use num bloco grava o TIPO dele; equipado, grasna
 * quando o bloco está perto (<=4 sempre, 5-8 alternado — raio do diamond_goose_detection_range); segurando na mão,
 * a cada 10s indica a direção relativa do bloco mais próximo. 15% de chance de mensagem de quack.
 */
public class DiamondGooseItem extends ArtifactItem {

    private static final String BOUND_BLOCK_TAG = "boundBlock";
    private static final String LAST_DIRECTION_TAG = "lastDirectionTime";
    private static final String MID_QUACK_TAG = "lastMidRangeQuackState";
    private static int detectionRange() { return com.windanesz.ancientspellcraft.ASServerConfig.DIAMOND_GOOSE_DETECTION_RANGE.get(); }

    public DiamondGooseItem(Rarity rarity) {
        super(rarity, new IArtifactEffect() {
            @Override
            public void onTick(Player player, Level level, ItemStack artifact) {
                if (level.isClientSide || player.tickCount % 80 != 0) return;
                String bound = getBoundBlock(artifact);
                if (bound.isEmpty()) return;
                BlockPos nearest = findNearest(player, bound);
                if (nearest == null) return;
                int distance = (int) Math.sqrt(player.blockPosition().distSqr(nearest));
                if (distance <= 4) {
                    quack(player);
                } else if (distance >= 5) {
                    boolean last = artifact.getOrDefault(DataComponents.CUSTOM_DATA,
                            CustomData.EMPTY).copyTag().getBoolean(MID_QUACK_TAG);
                    if (!last) quack(player);
                    CustomData.update(DataComponents.CUSTOM_DATA, artifact, tag -> tag.putBoolean(MID_QUACK_TAG, !last));
                }
            }
        });
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null || !player.isShiftKeyDown()) return InteractionResult.PASS;
        ItemStack stack = context.getItemInHand();
        if (!context.getLevel().isClientSide) {
            if (!getBoundBlock(stack).isEmpty()) {
                player.displayClientMessage(Component.translatable(
                        "item.ancientspellcraft.charm_diamond_goose.already_bound"), false);
                return InteractionResult.FAIL;
            }
            Block block = context.getLevel().getBlockState(context.getClickedPos()).getBlock();
            String name = BuiltInRegistries.BLOCK.getKey(block).toString();
            CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putString(BOUND_BLOCK_TAG, name));
            player.displayClientMessage(Component.translatable(
                    "item.ancientspellcraft.charm_diamond_goose.bound"), false);
        }
        return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slot, boolean selected) {
        // na mão: indica direção a cada 40t, com cooldown de 200t
        if (level.isClientSide || !(entity instanceof Player player) || player.tickCount % 40 != 0) return;
        if (player.getMainHandItem() != stack && player.getOffhandItem() != stack) return;
        String bound = getBoundBlock(stack);
        if (bound.isEmpty()) return;
        long now = level.getGameTime();
        long last = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getLong(LAST_DIRECTION_TAG);
        if (now - last < 200) return;
        BlockPos nearest = findNearest(player, bound);
        if (nearest == null) return;
        indicateDirection(player, nearest);
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putLong(LAST_DIRECTION_TAG, now));
    }

    private static String getBoundBlock(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data == null ? "" : data.copyTag().getString(BOUND_BLOCK_TAG);
    }

    @Nullable
    private static BlockPos findNearest(Player player, String boundBlock) {
        BlockPos center = player.blockPosition();
        double best = Double.MAX_VALUE;
        BlockPos found = null;
        int range = detectionRange();
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-range, -range, -range),
                center.offset(range, range, range))) {
            if (center.distSqr(pos) > (double) range * range) continue;
            Block block = player.level().getBlockState(pos).getBlock();
            if (BuiltInRegistries.BLOCK.getKey(block).toString().equals(boundBlock)) {
                double dist = center.distSqr(pos);
                if (dist < best) {
                    best = dist;
                    found = pos.immutable();
                }
            }
        }
        return found;
    }

    /** 1.12.2 calculateRelativeDirection: direção relativa ao olhar do jogador. */
    private static void indicateDirection(Player player, BlockPos target) {
        double dx = target.getX() + 0.5 - player.getX();
        double dy = target.getY() + 0.5 - player.getY() - player.getEyeHeight();
        double dz = target.getZ() + 0.5 - player.getZ();
        double lookX = -Math.sin(Math.toRadians(player.getYRot()));
        double lookZ = Math.cos(Math.toRadians(player.getYRot()));
        double dot = dx * lookX + dz * lookZ;
        double cross = dx * lookZ - dz * lookX;
        String suffix;
        if (Math.abs(dy) > Math.abs(dot) && Math.abs(dy) > Math.abs(cross)) {
            suffix = dy > 0 ? "up" : "down";
        } else if (Math.abs(dot) > Math.abs(cross)) {
            suffix = dot > 0 ? "forward" : "backward";
        } else {
            suffix = cross > 0 ? "left" : "right";
        }
        player.displayClientMessage(Component.translatable(
                "item.ancientspellcraft.charm_diamond_goose." + suffix), false);
    }

    private static void quack(Player player) {
        if (player.getRandom().nextFloat() < 0.15f) {
            player.displayClientMessage(Component.translatable(
                    "item.ancientspellcraft.charm_diamond_goose.quack" + (1 + player.getRandom().nextInt(3))), false);
        }
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                ASSounds.GOOSE.get(), SoundSource.PLAYERS, 1.0F, 0.9F + player.getRandom().nextFloat() * 0.2F);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        String bound = getBoundBlock(stack);
        if (!bound.isEmpty()) {
            Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(bound));
            tooltip.add(Component.translatable("item.ancientspellcraft.charm_diamond_goose.tooltip.imprinted",
                    block.getName()).withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.add(Component.translatable("item.ancientspellcraft.charm_diamond_goose.tooltip.bind")
                    .withStyle(ChatFormatting.GRAY));
        }
    }
}
