package com.windanesz.ancientspellcraft.material;

import com.koomplo.wizardry.api.content.util.EntityUtil;
import com.koomplo.wizardry.content.entity.living.Remnant;
import com.koomplo.wizardry.setup.registries.EBAttachments;
import com.windanesz.ancientspellcraft.registry.ASEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Delegates anti-magia do devoritium (1.12.2 IDevoritium): pisar/segurar/ser atingido pelo metal
 * aplica exaustão mágica; summons tomam dano; Remnants são repelidos. Summons no port são mobs
 * com MINION_DATA.isSummoned() (o Redux não tem o par ISummonedCreature do 1.12.2).
 */
public interface IDevoritium {

    int DEFAULT_SUMMON_DAMAGE = 3;

    default void onEntityWalkDelegate(Level level, BlockPos pos, Entity entity) {
        if (!level.isClientSide && entity instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(ASEffects.MAGICAL_EXHAUSTION, 40, 2));
            damageSummonedCreature(entity, 1);
        }
    }

    default void damageSummonedCreature(Entity entity, float multiplier) {
        if (!entity.level().isClientSide && entity instanceof Mob mob
                && mob.getData(EBAttachments.MINION_DATA).isSummoned()) {
            EntityUtil.attackEntityWithoutKnockback(mob, entity.level().damageSources().generic(),
                    multiplier > 0 ? DEFAULT_SUMMON_DAMAGE * multiplier : DEFAULT_SUMMON_DAMAGE);
        }
    }

    default void onEntityCollisionDelegate(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (entity instanceof Remnant) {
            entity.push(0, 1, 0);
        }
    }

    default void hitEntityDelegate(Entity attacker, Entity target) {
        hitEntityDelegate(attacker, target, 0, 0);
    }

    /** Escalada do 1.12.2: sem efeito → 30t amp 0; amp 0/1/2 → 45t amp+1; amp 3+ não sobe. */
    default void hitEntityDelegate(Entity attacker, Entity target, int bonusAmplifier, int bonusDuration) {
        if (target.level().isClientSide || !(target instanceof LivingEntity living)) return;

        MobEffectInstance current = living.getEffect(ASEffects.MAGICAL_EXHAUSTION);
        if (current == null) {
            living.addEffect(new MobEffectInstance(ASEffects.MAGICAL_EXHAUSTION, 30 + bonusDuration, bonusAmplifier));
        } else if (current.getAmplifier() <= 2) {
            living.addEffect(new MobEffectInstance(ASEffects.MAGICAL_EXHAUSTION, 45 + bonusDuration,
                    current.getAmplifier() + 1 + bonusAmplifier));
        }
    }

    /** Carregar devoritium no inventário: exaustão I a cada 20t; II se selecionado/offhand (1.12.2). */
    default void onUpdateDelegate(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        applyCarryPenalty(stack, level, entity, selected);
    }

    static void applyCarryPenalty(ItemStack stack, Level level, Entity entity, boolean selected) {
        if (!level.isClientSide && level.getGameTime() % 20 == 0 && entity instanceof Player player) {
            boolean held = selected || player.getOffhandItem() == stack;
            player.addEffect(new MobEffectInstance(ASEffects.MAGICAL_EXHAUSTION, 40, held ? 2 : 1));
        }
    }
}
