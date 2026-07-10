package com.windanesz.ancientspellcraft.entity.ai;

import com.koomplo.wizardry.api.content.spell.Element;
import com.koomplo.wizardry.content.item.armor.WizardArmorType;
import com.windanesz.ancientspellcraft.item.BattlemageSwordItem;
import com.windanesz.ancientspellcraft.item.ElementalSwordEffects;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Melee do class wizard battlemage (1.12.2 EntityAIBattlemageMelee): golpeia com a espada aplicando
 * os efeitos elementais. Desvio: ativa quando o alvo está a até 5 blocos (o 1.12.2 usava o cooldown
 * da spell como gatilho — perto = espada, longe = spells dá o mesmo comportamento na prática).
 */
public class BattlemageMeleeGoal extends MeleeAttackGoal {

    private final Supplier<WizardArmorType> armourClass;
    private final Supplier<Element> element;

    public BattlemageMeleeGoal(PathfinderMob mob, Supplier<WizardArmorType> armourClass, Supplier<Element> element) {
        super(mob, 1.0, true);
        this.armourClass = armourClass;
        this.element = element;
    }

    private boolean valid() {
        return armourClass.get() == WizardArmorType.BATTLEMAGE
                && mob.getMainHandItem().getItem() instanceof BattlemageSwordItem
                && mob.getTarget() != null
                && mob.distanceToSqr(mob.getTarget()) <= 25;
    }

    @Override
    public boolean canUse() {
        return valid() && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return valid() && super.canContinueToUse();
    }

    @Override
    protected void checkAndPerformAttack(@NotNull LivingEntity target) {
        if (canPerformAttack(target)) {
            resetAttackCooldown();
            mob.swing(InteractionHand.MAIN_HAND);
            mob.doHurtTarget(target);
            Element el = element.get();
            if (el != null) {
                ElementalSwordEffects.hit(el, mob.getMainHandItem(), target, mob, false);
            }
        }
    }
}
