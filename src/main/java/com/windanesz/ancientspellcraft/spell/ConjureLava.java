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

/** Conjura lava no ponto mirado (1.12.2 ConjureLava). */
public class ConjureLava extends ASRaySpell {

    public ConjureLava() {
        this.hitLiquids(true);
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        return false;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        if (!(ctx.caster() instanceof Player player)) return false;
        BlockPos pos = blockHit.getBlockPos().relative(blockHit.getDirection());
        if (ctx.world().getBlockState(pos).canBeReplaced() && BlockUtil.canPlaceBlock(player, ctx.world(), pos)) {
            if (!ctx.world().isClientSide) {
                ctx.world().setBlock(pos, net.minecraft.world.level.block.Blocks.LAVA.defaultBlockState(), 2);
            }
            return true;
        }
        return false;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ParticleBuilder.create(EBParticles.MAGIC_FIRE).pos(x, y, z).spawn(ctx.world());
    }
}
