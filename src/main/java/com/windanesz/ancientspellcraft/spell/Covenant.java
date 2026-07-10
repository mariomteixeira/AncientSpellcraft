package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.internal.CastContext;
import com.koomplo.wizardry.content.entity.living.Wizard;
import com.windanesz.ancientspellcraft.entity.ai.WizardFollowPlayerGoal;
import com.windanesz.ancientspellcraft.entity.living.ClassWizard;
import com.windanesz.ancientspellcraft.registry.ASAttachments;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Covenant (1.12.2): alia um wizard comum (não class wizard) ao jogador — ele te segue e passa a
 * atacar monstros. Um aliado por vez; re-cast no mesmo wizard desfaz. A aliança re-aplica quando o
 * wizard recarrega (EntityJoinLevelEvent). Desvio: o retargeting usa NearestAttackableTarget de
 * Monster em vez do seletor com whitelist/blacklist de config do 1.12.2.
 */
public class Covenant extends ASRaySpell {

    public static final String ALLIED_WIZARD_TAG = "AlliedWizard";

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if (!(ctx.caster() instanceof Player player)) return false;
        if (!(entityHit.getEntity() instanceof Wizard wizard) || wizard instanceof ClassWizard) return false;
        if (ctx.world().isClientSide) return true;

        var tag = player.getData(ASAttachments.PLAYER_DATA);
        java.util.UUID old = tag.hasUUID(ALLIED_WIZARD_TAG) ? tag.getUUID(ALLIED_WIZARD_TAG) : null;
        boolean sameWizard = wizard.getUUID().equals(old);

        if (old != null && ctx.world() instanceof net.minecraft.server.level.ServerLevel server) {
            if (server.getEntity(old) instanceof Wizard oldWizard && (!sameWizard || isFollowing(oldWizard))) {
                endAlliance(oldWizard);
                player.sendSystemMessage(Component.translatable(
                        "spell.ancientspellcraft.covenant.no_longer_following", oldWizard.getDisplayName()));
            }
        }

        if (sameWizard) {
            // re-cast no mesmo wizard = desfaz a aliança
            tag.remove(ALLIED_WIZARD_TAG);
            player.setData(ASAttachments.PLAYER_DATA, tag);
            return true;
        }

        ally(player, wizard);
        tag.putUUID(ALLIED_WIZARD_TAG, wizard.getUUID());
        player.setData(ASAttachments.PLAYER_DATA, tag);
        player.displayClientMessage(Component.translatable(
                "spell.ancientspellcraft.covenant.following", wizard.getDisplayName()), true);
        return true;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        return false;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        com.koomplo.wizardry.api.client.ParticleBuilder.create(
                        com.koomplo.wizardry.setup.registries.client.EBParticles.SPARKLE)
                .pos(x, y, z).color(0xf3e388).spawn(ctx.world());
    }

    // ===== aliança =====

    /** Marcador do goal de retarget adicionado pelo covenant. */
    public static class CovenantTargetGoal extends NearestAttackableTargetGoal<Monster> {
        public CovenantTargetGoal(Mob mob) {
            super(mob, Monster.class, false);
        }
    }

    public static boolean isFollowing(Mob wizard) {
        return wizard.goalSelector.getAvailableGoals().stream()
                .anyMatch(wrapped -> wrapped.getGoal() instanceof WizardFollowPlayerGoal);
    }

    public static void ally(Player player, Mob wizard) {
        if (isFollowing(wizard)) return;
        wizard.goalSelector.addGoal(2, new WizardFollowPlayerGoal((net.minecraft.world.entity.PathfinderMob) wizard, player.getUUID()));
        wizard.targetSelector.addGoal(0, new CovenantTargetGoal(wizard));
    }

    public static void endAlliance(Mob wizard) {
        wizard.goalSelector.getAvailableGoals().stream()
                .filter(wrapped -> wrapped.getGoal() instanceof WizardFollowPlayerGoal)
                .map(net.minecraft.world.entity.ai.goal.WrappedGoal::getGoal)
                .toList().forEach(wizard.goalSelector::removeGoal);
        wizard.targetSelector.getAvailableGoals().stream()
                .filter(wrapped -> wrapped.getGoal() instanceof CovenantTargetGoal)
                .map(net.minecraft.world.entity.ai.goal.WrappedGoal::getGoal)
                .toList().forEach(wizard.targetSelector::removeGoal);
    }

    /** Re-aplica a aliança quando o wizard aliado (re)carrega (1.12.2 onCheckSpawnEvent). */
    public static void onEntityJoinLevel(net.neoforged.neoforge.event.entity.EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide || !(event.getEntity() instanceof Wizard wizard)
                || wizard instanceof ClassWizard || isFollowing(wizard)) return;
        for (Player player : event.getLevel().players()) {
            var tag = player.getData(ASAttachments.PLAYER_DATA);
            if (tag.hasUUID(ALLIED_WIZARD_TAG) && tag.getUUID(ALLIED_WIZARD_TAG).equals(wizard.getUUID())) {
                ally(player, wizard);
                return;
            }
        }
    }
}
