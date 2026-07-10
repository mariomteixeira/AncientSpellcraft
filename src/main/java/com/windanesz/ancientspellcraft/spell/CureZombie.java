package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.api.content.spell.internal.CastContext;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperties;
import com.koomplo.wizardry.api.content.util.BlockUtil;
import com.koomplo.wizardry.content.spell.DefaultProperties;
import com.koomplo.wizardry.content.spell.abstr.RaySpell;
import com.koomplo.wizardry.setup.registries.client.EBParticles;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/** Inicia a cura de um zombie villager (1.12.2 CureZombie): delay vanilla de 3-5 min e o caster
 * fica como conversion starter (reputação/gossip) — startConverting aberto por access transformer. */
public class CureZombie extends ASRaySpell {

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if (entityHit.getEntity() instanceof net.minecraft.world.entity.monster.ZombieVillager zombie
                && ctx.caster() instanceof Player player) {
            if (!ctx.world().isClientSide && !zombie.isConverting()) {
                zombie.startConverting(player.getUUID(), ctx.world().random.nextInt(2401) + 3600);
            }
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
        ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z).color(0xf4c542).spawn(ctx.world());
    }
}
