package com.windanesz.ancientspellcraft.potion;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.api.content.DeferredObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Coloca um bloco de luz temporario acima do jogador e mantém a partícula sobre a cabeça
 * (1.12.2 PotionMageLight: FLASH branca; PotionCandleLight: MAGIC_FIRE alaranjada).
 */
public class LightSourceEffect extends ASMobEffect {

    private final Supplier<Block> lightBlock;
    private final DeferredObject<SimpleParticleType> particle;
    private final int red, green, blue, greenJitter;
    private final float scale;
    private final double velocityDivisor;

    public LightSourceEffect(int color, Supplier<Block> lightBlock, DeferredObject<SimpleParticleType> particle,
                             int red, int green, int blue, int greenJitter, float scale, double velocityDivisor) {
        super(MobEffectCategory.BENEFICIAL, color);
        this.lightBlock = lightBlock;
        this.particle = particle;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.greenJitter = greenJitter;
        this.scale = scale;
        this.velocityDivisor = velocityDivisor;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (!(entity instanceof Player)) return true;

        if (entity.level().isClientSide) {
            var rand = entity.level().random;
            ParticleBuilder.create(particle).entity(entity).pos(0, 2.8, 0)
                    .time(6 + rand.nextInt(5))
                    .velocity(rand.nextGaussian() / velocityDivisor, rand.nextDouble() / velocityDivisor,
                            rand.nextGaussian() / velocityDivisor)
                    .color(red, green + (greenJitter > 0 ? rand.nextInt(greenJitter + 1) : 0), blue)
                    .collide(false).scale(scale).spawn(entity.level());
        } else if (entity.tickCount % 15 == 0) {
            BlockPos above = entity.blockPosition().above();
            if (entity.level().isEmptyBlock(above)) {
                entity.level().setBlockAndUpdate(above, lightBlock.get().defaultBlockState());
            }
        }
        return true;
    }
}
