package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.entity.living.ISpellCaster;
import com.koomplo.wizardry.api.content.item.ICastItem;
import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.spell.internal.CastContext;
import com.koomplo.wizardry.api.content.util.CastItemDataHelper;
import com.koomplo.wizardry.setup.registries.Spells;
import com.windanesz.ancientspellcraft.registry.ASEffects;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Interrompe o cast do alvo (1.12.2 Counterspell): player alvo ganha o cooldown cheio da spell
 * atual da wand; NPC conjurador ganha exaustão mágica V por 4s.
 */
public class Counterspell extends SageRaySpell {

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if (!(entityHit.getEntity() instanceof LivingEntity target)) return false;
        if (ctx.world().isClientSide) return true;

        if (target.isUsingItem()) target.stopUsingItem();

        if (target instanceof Player targetPlayer) {
            for (InteractionHand hand : InteractionHand.values()) {
                ItemStack stack = targetPlayer.getItemInHand(hand);
                if (stack.getItem() instanceof ICastItem) {
                    Spell current = CastItemDataHelper.getCurrentSpell(stack);
                    if (current != Spells.NONE) {
                        CastItemDataHelper.setCurrentCooldown(stack, current.getCooldown(), ctx.world().getGameTime());
                    }
                    break;
                }
            }
        } else if (target instanceof ISpellCaster) {
            target.addEffect(new MobEffectInstance(ASEffects.MAGICAL_EXHAUSTION, 80, 4));
        }
        return true;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        return false;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        com.koomplo.wizardry.api.client.ParticleBuilder.create(
                com.koomplo.wizardry.setup.registries.client.EBParticles.FLASH).pos(x, y, z).color(0xd94444).spawn(ctx.world());
    }
}
