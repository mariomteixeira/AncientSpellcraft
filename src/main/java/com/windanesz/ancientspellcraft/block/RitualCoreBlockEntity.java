package com.windanesz.ancientspellcraft.block;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.content.entity.construct.ForcefieldConstruct;
import com.koomplo.wizardry.setup.registries.EBItems;
import com.koomplo.wizardry.setup.registries.client.EBParticles;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.UUID;

/**
 * Estado dos rituais contínuos (1.12.2 TileRune.ritualData):
 * - arcane_barrier: mantém um ForcefieldConstruct (lifetime curto renovado — quebrou o core, expira).
 *   Desvio: raio fixo 4, sem cor/allow_players do original.
 * - condensing: engole shard/cristal jogado no core e o cresce (ciclo 180t: shard +0.75, cristal +0.25;
 *   100% -> shard vira magic_crystal, cristal vira grand). Clique devolve o item.
 * - forest: engole um sapling e planta/cresce árvores desse tipo num raio 3-30 a cada 40t;
 *   expira em 1600t (lifetime do JSON 1.12.2).
 */
public class RitualCoreBlockEntity extends BlockEntity {

    private String ritual = "";
    private ItemStack storedItem = ItemStack.EMPTY;
    private float progress;
    private UUID barrierUuid;
    private int age;

    public float rotation;
    public float rotationPrev;

    public RitualCoreBlockEntity(BlockPos pos, BlockState state) {
        super(ASBlocks.RITUAL_CORE_BLOCK_ENTITY.get(), pos, state);
    }

    public void setRitual(String ritual) {
        this.ritual = ritual;
        setChanged();
    }

    public ItemStack getStoredItem() {
        return storedItem;
    }

    public void popStoredItem() {
        if (storedItem.isEmpty() || level == null) return;
        var drop = new ItemEntity(level, worldPosition.getX() + 0.5, worldPosition.getY() + 0.5,
                worldPosition.getZ() + 0.5, storedItem.copy());
        level.addFreshEntity(drop);
        storedItem = ItemStack.EMPTY;
        progress = 0;
        sync();
    }

    /** setChanged + block update para o storedItem chegar no client (render do item flutuando). */
    private void sync() {
        setChanged();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public net.minecraft.network.protocol.Packet<net.minecraft.network.protocol.game.ClientGamePacketListener> getUpdatePacket() {
        return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);
    }

    public void clientTick() {
        rotationPrev = rotation;
        rotation += 0.04F;
        if (level != null && level.random.nextInt(4) == 0) {
            ParticleBuilder.create(EBParticles.FLASH)
                    .pos(worldPosition.getX() + 0.5, worldPosition.getY() + 0.3, worldPosition.getZ() + 0.5)
                    .color("forest".equals(ritual) ? 0x53ad17 : 0x9d2cf3).time(15).spawn(level);
        }
    }

    public void serverTick() {
        if (!(level instanceof ServerLevel serverLevel)) return;
        age++;
        switch (ritual) {
            case "arcane_barrier" -> tickBarrier(serverLevel);
            case "condensing" -> tickCondensing(serverLevel);
            case "forest" -> tickForest(serverLevel);
            default -> {
            }
        }
    }

    private void tickBarrier(ServerLevel serverLevel) {
        if (age % 20 != 1) return;
        Entity existing = barrierUuid == null ? null : serverLevel.getEntity(barrierUuid);
        if (existing instanceof ForcefieldConstruct barrier && barrier.isAlive()) {
            barrier.lifetime = 60;
        } else {
            ForcefieldConstruct barrier = new ForcefieldConstruct(serverLevel);
            barrier.setPos(worldPosition.getX() + 0.5, worldPosition.getY(), worldPosition.getZ() + 0.5);
            barrier.setRadius(4.0F);
            barrier.lifetime = 60;
            serverLevel.addFreshEntity(barrier);
            barrierUuid = barrier.getUUID();
            setChanged();
        }
    }

    private void tickCondensing(ServerLevel serverLevel) {
        if (storedItem.isEmpty()) {
            for (ItemEntity item : serverLevel.getEntitiesOfClass(ItemEntity.class, new AABB(worldPosition).inflate(1))) {
                var stack = item.getItem();
                if (stack.is(EBItems.MAGIC_CRYSTAL_SHARD.get()) || stack.is(EBItems.MAGIC_CRYSTAL.get())) {
                    storedItem = stack.copyWithCount(1);
                    stack.shrink(1);
                    if (stack.isEmpty()) item.discard();
                    progress = 0;
                    sync();
                    break;
                }
            }
            return;
        }
        if (serverLevel.getGameTime() % 180 != 0) return;
        if (storedItem.is(EBItems.MAGIC_CRYSTAL_SHARD.get())) progress += 0.75F;
        else if (storedItem.is(EBItems.MAGIC_CRYSTAL.get())) progress += 0.25F;
        if (progress >= 100.0F) {
            storedItem = storedItem.is(EBItems.MAGIC_CRYSTAL_SHARD.get())
                    ? new ItemStack(EBItems.MAGIC_CRYSTAL.get())
                    : new ItemStack(EBItems.MAGIC_CRYSTAL_GRAND.get());
            progress = 0;
        }
        setChanged();
    }

    private void tickForest(ServerLevel serverLevel) {
        if (age > 1600) {
            serverLevel.removeBlock(worldPosition, false);
            return;
        }
        if (storedItem.isEmpty()) {
            for (ItemEntity item : serverLevel.getEntitiesOfClass(ItemEntity.class, new AABB(worldPosition).inflate(1))) {
                var stack = item.getItem();
                if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof SaplingBlock) {
                    storedItem = stack.copyWithCount(1);
                    stack.shrink(1);
                    if (stack.isEmpty()) item.discard();
                    sync();
                    break;
                }
            }
            return;
        }
        if (age % 40 != 0) return;
        if (!(storedItem.getItem() instanceof BlockItem blockItem) || !(blockItem.getBlock() instanceof SaplingBlock sapling)) return;

        var random = serverLevel.random;
        int x = worldPosition.getX() + Math.max(random.nextInt(30), 3) * (random.nextBoolean() ? 1 : -1);
        int z = worldPosition.getZ() + Math.max(random.nextInt(30), 3) * (random.nextBoolean() ? 1 : -1);
        for (int i = 0; i < 20; i++) {
            int j = i > 10 ? -(i - 10) : i;
            BlockPos pos = new BlockPos(x, worldPosition.getY() + j, z);
            BlockState state = sapling.defaultBlockState();
            if (serverLevel.getBlockState(pos).is(Blocks.AIR) && state.canSurvive(serverLevel, pos)) {
                serverLevel.setBlockAndUpdate(pos, state);
                sapling.advanceTree(serverLevel, pos, state, random);
                break;
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("Ritual", ritual);
        tag.putFloat("Progress", progress);
        tag.putInt("Age", age);
        if (barrierUuid != null) tag.putUUID("BarrierUuid", barrierUuid);
        if (!storedItem.isEmpty()) tag.put("StoredItem", storedItem.save(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        ritual = tag.getString("Ritual");
        progress = tag.getFloat("Progress");
        age = tag.getInt("Age");
        if (tag.hasUUID("BarrierUuid")) barrierUuid = tag.getUUID("BarrierUuid");
        storedItem = tag.contains("StoredItem") ? ItemStack.parseOptional(registries, tag.getCompound("StoredItem")) : ItemStack.EMPTY;
    }
}
