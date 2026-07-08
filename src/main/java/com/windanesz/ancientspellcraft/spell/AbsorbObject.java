package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.internal.CastContext;
import com.koomplo.wizardry.api.content.util.BlockUtil;
import com.windanesz.ancientspellcraft.registry.ASAttachments;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Absorve um BLOCO para o bolso espacial; agachado, solta o bloco guardado no ponto mirado
 * (1.12.2 AbsorbObject; guardado no attachment em vez do NBT do item).
 */
public class AbsorbObject extends WarlockRaySpell {

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        return false;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        if (!(ctx.caster() instanceof Player player) || ctx.world().isClientSide) return true;
        var tag = player.getData(ASAttachments.WARLOCK_DATA);

        if (player.isShiftKeyDown()) {
            if (!tag.contains("ObjectState")) return false;
            var pos = blockHit.getBlockPos().relative(blockHit.getDirection());
            BlockState state = NbtUtils.readBlockState(ctx.world().holderLookup(net.minecraft.core.registries.Registries.BLOCK),
                    tag.getCompound("ObjectState"));
            if (!BlockUtil.canBlockBeReplaced(ctx.world(), pos) || !BlockUtil.canPlaceBlock(player, ctx.world(), pos)) return false;
            ctx.world().setBlockAndUpdate(pos, state);
            tag.remove("ObjectState");
            player.setData(ASAttachments.WARLOCK_DATA, tag);
            return true;
        }

        if (tag.contains("ObjectState")) {
            player.displayClientMessage(Component.translatable("spell.ancientspellcraft.absorb_object.full"), true);
            return false;
        }
        var pos = blockHit.getBlockPos();
        BlockState state = ctx.world().getBlockState(pos);
        if (state.getDestroySpeed(ctx.world(), pos) < 0 || ctx.world().getBlockEntity(pos) != null
                || !BlockUtil.canBreak(player, ctx.world(), pos, false)) {
            return false;
        }
        tag.put("ObjectState", NbtUtils.writeBlockState(state));
        player.setData(ASAttachments.WARLOCK_DATA, tag);
        ctx.world().removeBlock(pos, false);
        player.displayClientMessage(Component.translatable("spell.ancientspellcraft.absorb_object.absorbed",
                state.getBlock().getName()), true);
        return true;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        com.koomplo.wizardry.api.client.ParticleBuilder.create(
                com.koomplo.wizardry.setup.registries.client.EBParticles.DUST).pos(x, y, z).color(0x9d2cf3).spawn(ctx.world());
    }
}
