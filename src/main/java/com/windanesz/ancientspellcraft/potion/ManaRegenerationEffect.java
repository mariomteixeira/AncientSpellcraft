package com.windanesz.ancientspellcraft.potion;

import com.koomplo.wizardry.api.content.item.IManaItem;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/** Recarrega mana da wand na mao (1.12.2 PotionManaRegeneration; cadencia 60 >> amplifier). */
public class ManaRegenerationEffect extends ASMobEffect {

    public ManaRegenerationEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xc558d6);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        int k = 60 >> amplifier;
        return k <= 0 || duration % k == 0;
    }

    @Override
    public boolean applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity instanceof Player player) {
            if (!refill(player.getMainHandItem())) refill(player.getOffhandItem());
        }
        return true;
    }

    private boolean refill(ItemStack stack) {
        if (stack.getItem() instanceof IManaItem manaItem && !manaItem.isManaFull(stack)) {
            manaItem.rechargeMana(stack, 1);
            return true;
        }
        return false;
    }
}
