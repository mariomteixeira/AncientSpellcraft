package com.windanesz.ancientspellcraft.block;

import com.koomplo.wizardry.core.AllyDesignation;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 1.12.2 TileSkullWatch: vigia raio 15 — ignora animais/armor stands/dono/aliados; intruso com
 * linha de visão dispara: mensagem ao dono (cooldown 12s por entidade), grito a cada 50t e sinal
 * de redstone. Com charm_sentinel_eye ao colocar, marca intrusos com GLOWING; com amulet_domus,
 * invoca um esqueleto minion (cooldown 60s). Desvio: som próprio do pack -> vanilla.
 */
public class SkullWatchBlockEntity extends BlockEntity {

    private static final double DETECT_RADIUS = 15.0;
    private static final int NOTIFICATION_COOLDOWN = 20 * 12;
    private static final int SUMMON_COOLDOWN = 1200;

    private final Map<UUID, Integer> detectedEntities = new HashMap<>();
    private UUID ownerUuid;
    private boolean triggered;
    private int tickCounter;
    private boolean markEntities;
    private boolean summonSkeleton;
    private int summonCooldown;

    public SkullWatchBlockEntity(BlockPos pos, BlockState state) {
        super(ASBlocks.SKULL_WATCH_BLOCK_ENTITY.get(), pos, state);
    }

    public void setOwner(UUID uuid) {
        this.ownerUuid = uuid;
        setChanged();
    }

    public void setMarkEntities(boolean markEntities) {
        this.markEntities = markEntities;
        setChanged();
    }

    public void setSummonSkeleton(boolean summonSkeleton) {
        this.summonSkeleton = summonSkeleton;
        setChanged();
    }

    public boolean isTriggered() {
        return triggered;
    }

    public void serverTick() {
        if (!(level instanceof ServerLevel serverLevel)) return;
        tickCounter++;

        Player owner = ownerUuid == null ? null : serverLevel.getPlayerByUUID(ownerUuid);
        LivingEntity target = null;
        Vec3 eye = Vec3.atCenterOf(worldPosition).add(0, 0.5, 0);

        for (LivingEntity entity : serverLevel.getEntitiesOfClass(LivingEntity.class,
                new AABB(worldPosition).inflate(DETECT_RADIUS))) {
            if (entity instanceof Animal || entity instanceof ArmorStand) continue;
            if (ownerUuid != null && (entity.getUUID().equals(ownerUuid)
                    || (owner != null && AllyDesignation.isAllied(owner, entity)))) continue;

            var hit = serverLevel.clip(new ClipContext(eye,
                    new Vec3(entity.getX(), entity.getEyeY(), entity.getZ()),
                    ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));
            if (hit.getType() == HitResult.Type.MISS) {
                target = entity;
                break;
            }
        }

        boolean wasTriggered = triggered;
        triggered = target != null;
        if (triggered != wasTriggered) {
            level.updateNeighborsAt(worldPosition, ASBlocks.SKULL_WATCH.get());
        }
        if (summonCooldown > 0) summonCooldown--;
        if (target == null) return;

        // charm_sentinel_eye: marca o intruso (1.12.2: GLOWING 2400t a cada 20t)
        if (markEntities && serverLevel.getGameTime() % 20 == 0) {
            target.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.GLOWING, 2400));
        }
        // amulet_domus: invoca um esqueleto minion equipado (1.12.2: cooldown 1200t)
        if (summonSkeleton && summonCooldown == 0) {
            var skeleton = new net.minecraft.world.entity.monster.Skeleton(net.minecraft.world.entity.EntityType.SKELETON, serverLevel);
            skeleton.setPos(worldPosition.getX() + 0.5, worldPosition.getY(), worldPosition.getZ() + 0.5);
            skeleton.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.WOODEN_SWORD));
            skeleton.setItemInHand(net.minecraft.world.InteractionHand.OFF_HAND, new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.SHIELD));
            skeleton.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD, new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.LEATHER_HELMET));
            skeleton.setItemSlot(net.minecraft.world.entity.EquipmentSlot.CHEST, new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.LEATHER_CHESTPLATE));
            for (var slot : net.minecraft.world.entity.EquipmentSlot.values()) skeleton.setDropChance(slot, 0.0F);
            var data = com.koomplo.wizardry.core.platform.Services.OBJECT_DATA.getMinionData(skeleton);
            data.setSummoned(true);
            if (ownerUuid != null) data.setOwnerUUID(ownerUuid);
            data.setLifetime(600);
            data.updateGoals();
            serverLevel.addFreshEntity(skeleton);
            summonCooldown = SUMMON_COOLDOWN;
        }

        if (owner != null) {
            if (detectedEntities.size() > 30) detectedEntities.clear();
            UUID targetId = target.getUUID();
            if (!detectedEntities.containsKey(targetId)
                    || tickCounter - detectedEntities.get(targetId) > NOTIFICATION_COOLDOWN) {
                detectedEntities.put(targetId, tickCounter);
                owner.sendSystemMessage(Component.translatable("message.ancientspellcraft.skull_sentinel.detected",
                        target.getName(), Mth.floor(target.getX()), Mth.floor(target.getY()), Mth.floor(target.getZ()))
                        .withStyle(ChatFormatting.DARK_PURPLE));
            }
        }
        if (serverLevel.getGameTime() % 50 == 0) {
            serverLevel.playSound(null, worldPosition, SoundEvents.SKELETON_AMBIENT, SoundSource.BLOCKS, 1.0F, 0.5F);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (ownerUuid != null) tag.putUUID("Owner", ownerUuid);
        tag.putBoolean("MarkEntities", markEntities);
        tag.putBoolean("SummonSkeleton", summonSkeleton);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.hasUUID("Owner")) ownerUuid = tag.getUUID("Owner");
        markEntities = tag.getBoolean("MarkEntities");
        summonSkeleton = tag.getBoolean("SummonSkeleton");
    }
}
