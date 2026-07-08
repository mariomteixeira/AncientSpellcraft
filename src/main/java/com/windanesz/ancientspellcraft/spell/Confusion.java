package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.internal.CastContext;
import com.koomplo.wizardry.api.content.util.EntityUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collections;

/** Confusao (1.12.2 Confusion): player tem maos trocadas + hotbar embaralhada + cegueira; mob ataca alvo aleatorio. */
public class Confusion extends WarlockRaySpell {

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if (ctx.world().isClientSide) return true;
        if (entityHit.getEntity() instanceof Player player) {
            if (!player.getOffhandItem().isEmpty()) {
                ItemStack main = player.getMainHandItem().copy();
                player.setItemInHand(InteractionHand.MAIN_HAND, player.getOffhandItem());
                player.setItemInHand(InteractionHand.OFF_HAND, main);
            }
            var hotbar = new ArrayList<ItemStack>();
            for (int i = 0; i < 9; i++) hotbar.add(player.getInventory().getItem(i));
            Collections.shuffle(hotbar);
            for (int i = 0; i < 9; i++) player.getInventory().setItem(i, hotbar.get(i));
            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 3));
            return true;
        }
        if (entityHit.getEntity() instanceof Mob mob) {
            var nearby = EntityUtil.getLivingWithinRadius(8, mob.getX(), mob.getY(), mob.getZ(), ctx.world());
            nearby.remove(mob);
            nearby.remove(ctx.caster());
            if (!nearby.isEmpty()) {
                mob.setTarget(nearby.get(ctx.world().random.nextInt(nearby.size())));
            }
            mob.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0));
            return true;
        }
        return false;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        return false;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        com.koomplo.wizardry.api.client.ParticleBuilder.create(
                com.koomplo.wizardry.setup.registries.client.EBParticles.DARK_MAGIC).pos(x, y, z).color(0x9932cc).spawn(ctx.world());
    }
}
