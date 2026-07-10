package com.windanesz.ancientspellcraft.entity.ai;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;
import java.util.UUID;

/** Wizard aliado pelo covenant segue o jogador (1.12.2 EntityAIWizardFollowPlayer). */
public class WizardFollowPlayerGoal extends net.minecraft.world.entity.ai.goal.Goal {

    private final PathfinderMob wizard;
    private final UUID ownerUUID;

    public WizardFollowPlayerGoal(PathfinderMob wizard, UUID ownerUUID) {
        this.wizard = wizard;
        this.ownerUUID = ownerUUID;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    private Player owner() {
        return wizard.level().getPlayerByUUID(ownerUUID);
    }

    @Override
    public boolean canUse() {
        Player owner = owner();
        return owner != null && wizard.getTarget() == null && wizard.distanceToSqr(owner) > 100;
    }

    @Override
    public boolean canContinueToUse() {
        Player owner = owner();
        return owner != null && wizard.getTarget() == null && wizard.distanceToSqr(owner) > 36
                && !wizard.getNavigation().isDone();
    }

    @Override
    public void start() {
        Player owner = owner();
        if (owner != null) wizard.getNavigation().moveTo(owner, 1.0);
    }

    @Override
    public void tick() {
        Player owner = owner();
        if (owner != null && wizard.tickCount % 20 == 0) {
            wizard.getNavigation().moveTo(owner, 1.0);
        }
    }

    @Override
    public void stop() {
        wizard.getNavigation().stop();
    }
}
