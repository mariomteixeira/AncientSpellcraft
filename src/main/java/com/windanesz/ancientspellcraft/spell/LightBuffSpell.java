package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.internal.CastContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

/** magelight/candlelight: aplica o efeito, remove o irmao e ja acende a luz (1.12.2 Magelight/Candlelight). */
public class LightBuffSpell extends ASBuffSpell {

    private final Supplier<Holder<MobEffect>> sibling;
    private final Supplier<Block> lightBlock;

    @SafeVarargs
    public LightBuffSpell(float r, float g, float b, Supplier<Holder<MobEffect>> sibling,
                          Supplier<Block> lightBlock, Supplier<Holder<MobEffect>>... effects) {
        super(r, g, b, effects);
        this.sibling = sibling;
        this.lightBlock = lightBlock;
    }

    @Override
    protected boolean applyEffects(CastContext ctx, LivingEntity target) {
        if (!target.level().isClientSide && target.hasEffect(sibling.get())) {
            target.removeEffect(sibling.get());
        }
        boolean result = super.applyEffects(ctx, target);
        if (!target.level().isClientSide) {
            BlockPos above = target.blockPosition().above();
            if (target.level().isEmptyBlock(above)) {
                target.level().setBlockAndUpdate(above, lightBlock.get().defaultBlockState());
            }
        }
        return result;
    }
}
