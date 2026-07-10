package com.windanesz.ancientspellcraft.block;

import com.windanesz.ancientspellcraft.registry.ASBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/** BE compartilhado dos blocos temporarios do AS: caster + lifetime (ITemporaryBlock do 1.12.2). */
public class TemporaryBlockEntity extends BlockEntity {

    private int lifetime = -1;
    private UUID casterUUID;
    private float damage;

    public TemporaryBlockEntity(BlockPos pos, BlockState state) {
        super(ASBlocks.TEMPORARY_BE.get(), pos, state);
    }

    public void setLifetime(int lifetime) {
        this.lifetime = lifetime;
    }

    /** Sem lifetime = colocado por estrutura/worldgen, não por spell (gate do runeword_sealbreaker). */
    public boolean isPermanent() {
        return lifetime <= 0;
    }

    /**
     * Dissolve o conjunto conectado de blocos PERMANENTES do tipo dado (muros de estrutura),
     * a partir de start. Usado pelo runeword_sealbreaker e pelo unsealing_scroll.
     *
     * @return quantos blocos foram removidos
     */
    public static int dissolveConnectedPermanent(Level level, net.minecraft.world.level.block.Block block,
                                                 BlockPos start, int cap) {
        var queue = new java.util.ArrayDeque<BlockPos>();
        var seen = new java.util.HashSet<BlockPos>();
        queue.add(start);
        seen.add(start);
        int removed = 0;
        while (!queue.isEmpty() && removed < cap) {
            BlockPos p = queue.poll();
            if (!level.getBlockState(p).is(block)) continue;
            if (!(level.getBlockEntity(p) instanceof TemporaryBlockEntity be) || !be.isPermanent()) continue;
            level.removeBlock(p, false);
            removed++;
            for (var d : net.minecraft.core.Direction.values()) {
                BlockPos n = p.relative(d);
                if (seen.add(n)) queue.add(n);
            }
        }
        return removed;
    }

    public void setCaster(LivingEntity caster) {
        this.casterUUID = caster == null ? null : caster.getUUID();
    }

    public Player getCaster() {
        return casterUUID == null || level == null ? null : level.getPlayerByUUID(casterUUID);
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getDamage() {
        return damage;
    }

    /** Coloca um bloco temporario com caster e lifetime (ITemporaryBlock.placeTemporaryBlock do 1.12.2). */
    public static boolean place(LivingEntity caster, Level level, net.minecraft.world.level.block.Block block, BlockPos pos, int lifetime) {
        if (!level.getBlockState(pos).canBeReplaced()) return false;
        level.setBlockAndUpdate(pos, block.defaultBlockState());
        if (level.getBlockEntity(pos) instanceof TemporaryBlockEntity be) {
            be.setCaster(caster);
            be.setLifetime(lifetime);
        }
        return true;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TemporaryBlockEntity be) {
        if (be.lifetime > 0 && --be.lifetime == 0) {
            level.removeBlock(pos, false);
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Lifetime", lifetime);
        tag.putFloat("Damage", damage);
        if (casterUUID != null) tag.putUUID("Caster", casterUUID);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        lifetime = tag.getInt("Lifetime");
        damage = tag.getFloat("Damage");
        if (tag.hasUUID("Caster")) casterUUID = tag.getUUID("Caster");
    }
}
