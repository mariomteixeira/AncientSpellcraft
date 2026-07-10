package com.windanesz.ancientspellcraft.entity.ai;

import com.windanesz.ancientspellcraft.entity.living.ClassWizard;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

/** Mercenário do battlemage_contract segue o contratante (1.12.2 Covenant follow). */
public class MercenaryFollowGoal extends Goal {

    private final ClassWizard wizard;

    public MercenaryFollowGoal(ClassWizard wizard) {
        this.wizard = wizard;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        Player owner = wizard.getMercenaryOwner();
        return owner != null && wizard.getTarget() == null && wizard.distanceToSqr(owner) > 100;
    }

    @Override
    public boolean canContinueToUse() {
        Player owner = wizard.getMercenaryOwner();
        return owner != null && wizard.getTarget() == null && wizard.distanceToSqr(owner) > 16
                && !wizard.getNavigation().isDone();
    }

    @Override
    public void start() {
        Player owner = wizard.getMercenaryOwner();
        if (owner != null) wizard.getNavigation().moveTo(owner, 0.7);
    }

    @Override
    public void tick() {
        Player owner = wizard.getMercenaryOwner();
        if (owner != null && wizard.tickCount % 20 == 0) {
            wizard.getNavigation().moveTo(owner, 0.7);
        }
    }

    @Override
    public void stop() {
        wizard.getNavigation().stop();
    }
}
