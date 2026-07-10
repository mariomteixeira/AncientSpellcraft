package com.windanesz.ancientspellcraft.block;

import com.windanesz.ancientspellcraft.registry.ASBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.Comparator;
import java.util.UUID;

/**
 * BE das chamas mágicas (1.12.2 AbstractMagicFlameTileEntity + variantes):
 * - ARCANE: dano mágico em quem entra (o bloco também incendeia).
 * - WILDFIRE: a cada 30t anda um bloco em direção ao ser vivo mais próximo no spreadRadius.
 * - TELEPORTATION: quem ficar 20t na chama viaja para a chama alvo (rede do enchant_fireplace).
 */
public class MagicFlameBlockEntity extends BlockEntity {

    public enum Kind {WILDFIRE, TELEPORTATION}

    private final Kind kind;
    private int lifetime = -1;
    private int age;
    private UUID casterUuid;
    private float spreadRadius = 2;
    private int moveCooldown;
    private BlockPos targetFlame;
    private int teleportCounter;
    private LivingEntity inside;

    public MagicFlameBlockEntity(BlockEntityType<?> type, Kind kind, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.kind = kind;
    }

    public void setLifetime(int lifetime) {
        this.lifetime = lifetime;
        setChanged();
    }

    public void setCaster(LivingEntity caster) {
        this.casterUuid = caster.getUUID();
        setChanged();
    }

    public void setSpreadRadius(float spreadRadius) {
        this.spreadRadius = spreadRadius;
        setChanged();
    }

    public void setTargetFlame(BlockPos target) {
        this.targetFlame = target;
        setChanged();
    }

    public BlockPos getTargetFlame() {
        return targetFlame;
    }

    void onEntityInside(LivingEntity entity) {
        switch (kind) {
            case WILDFIRE -> {
                if (entity.getUUID().equals(casterUuid)) return;
                entity.hurt(entity.damageSources().magic(), 2.0F);
            }
            case TELEPORTATION -> inside = entity;
        }
    }

    public void serverTick() {
        if (!(level instanceof ServerLevel serverLevel)) return;
        age++;
        if (lifetime > 0 && age > lifetime) {
            serverLevel.removeBlock(worldPosition, false);
            return;
        }
        switch (kind) {
            case WILDFIRE -> tickWildfire(serverLevel);
            case TELEPORTATION -> tickTeleportation(serverLevel);
            default -> {
            }
        }
    }

    private void tickWildfire(ServerLevel serverLevel) {
        if (moveCooldown-- > 0) return;
        moveCooldown = 30;
        LivingEntity target = serverLevel.getEntitiesOfClass(LivingEntity.class,
                        new AABB(worldPosition).inflate(spreadRadius),
                        e -> !(e.getUUID().equals(casterUuid)) && e.isAlive()).stream()
                .min(Comparator.comparingDouble(e -> e.distanceToSqr(worldPosition.getCenter())))
                .orElse(null);
        if (target == null) return;
        var direction = target.blockPosition().subtract(worldPosition);
        int stepX = Integer.signum(direction.getX());
        int stepZ = Integer.signum(direction.getZ());
        BlockPos next = worldPosition.offset(stepX, 0, stepZ);
        if (!serverLevel.getBlockState(next).canBeReplaced()) next = next.above();
        if (serverLevel.getBlockState(next).canBeReplaced()) {
            BlockState self = getBlockState();
            int remaining = lifetime - age;
            LivingEntity caster = casterUuid == null ? null
                    : serverLevel.getEntity(casterUuid) instanceof LivingEntity l ? l : null;
            float spread = spreadRadius;
            serverLevel.removeBlock(worldPosition, false);
            serverLevel.setBlockAndUpdate(next, self);
            if (serverLevel.getBlockEntity(next) instanceof MagicFlameBlockEntity flame) {
                flame.setLifetime(remaining);
                flame.setSpreadRadius(spread);
                if (caster != null) flame.setCaster(caster);
            }
        }
    }

    private void tickTeleportation(ServerLevel serverLevel) {
        if (inside == null || !inside.isAlive()
                || inside.distanceToSqr(worldPosition.getCenter()) > 1.5) {
            teleportCounter = 0;
            inside = null;
            return;
        }
        if (++teleportCounter < 20 || targetFlame == null) return;
        if (serverLevel.getBlockEntity(targetFlame) instanceof MagicFlameBlockEntity destination
                && destination.kind == Kind.TELEPORTATION) {
            inside.teleportTo(targetFlame.getX() + 0.5, targetFlame.getY(), targetFlame.getZ() + 0.5);
            if (inside instanceof Player player) {
                player.displayClientMessage(net.minecraft.network.chat.Component.translatable(
                        "spell.ancientspellcraft.teleportation_flame.teleported"), true);
            }
        }
        teleportCounter = 0;
        inside = null;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Lifetime", lifetime);
        tag.putInt("Age", age);
        tag.putFloat("SpreadRadius", spreadRadius);
        if (casterUuid != null) tag.putUUID("Caster", casterUuid);
        if (targetFlame != null) tag.put("TargetFlame", NbtUtils.writeBlockPos(targetFlame));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        lifetime = tag.getInt("Lifetime");
        age = tag.getInt("Age");
        spreadRadius = tag.getFloat("SpreadRadius");
        if (tag.hasUUID("Caster")) casterUuid = tag.getUUID("Caster");
        targetFlame = NbtUtils.readBlockPos(tag, "TargetFlame").orElse(null);
    }

    // fábricas por tipo (registro dos BlockEntityTypes)
    public static MagicFlameBlockEntity wildfire(BlockPos pos, BlockState state) {
        return new MagicFlameBlockEntity(ASBlocks.WILDFIRE_FLAME_BLOCK_ENTITY.get(), Kind.WILDFIRE, pos, state);
    }

    public static MagicFlameBlockEntity teleportation(BlockPos pos, BlockState state) {
        return new MagicFlameBlockEntity(ASBlocks.TELEPORTATION_FLAME_BLOCK_ENTITY.get(), Kind.TELEPORTATION, pos, state);
    }
}
