package com.windanesz.ancientspellcraft.potion;

import com.windanesz.ancientspellcraft.registry.ASBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/** Coloca um bloco de luz temporario acima do jogador (1.12.2 PotionMageLight/PotionCandleLight). */
public class LightSourceEffect extends ASMobEffect {

    private final Supplier<Block> lightBlock;

    public LightSourceEffect(int color, Supplier<Block> lightBlock) {
        super(MobEffectCategory.BENEFICIAL, color);
        this.lightBlock = lightBlock;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity instanceof Player && !entity.level().isClientSide && entity.tickCount % 15 == 0) {
            BlockPos above = entity.blockPosition().above();
            if (entity.level().isEmptyBlock(above)) {
                entity.level().setBlockAndUpdate(above, lightBlock.get().defaultBlockState());
            }
        }
        // TODO particulas FLASH/MAGIC_FIRE do 1.12.2 (client-side; exige DistExecutor ou client handler)
        return true;
    }
}
