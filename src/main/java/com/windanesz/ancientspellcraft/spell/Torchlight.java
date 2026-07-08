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

/** Coloca uma tocha do inventario no ponto mirado (1.12.2 Torchlight). */
public class Torchlight extends ASRaySpell {

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        return false;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        if (!(ctx.caster() instanceof Player player)) return false;
        var torch = new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.TORCH);
        if (!player.getInventory().contains(torch)) {
            if (!ctx.world().isClientSide) {
                player.displayClientMessage(net.minecraft.network.chat.Component.translatable(
                        "spell.ancientspellcraft.torchlight.no_torch"), true);
            }
            return false;
        }
        BlockPos pos = blockHit.getBlockPos().relative(blockHit.getDirection());
        if (BlockUtil.canPlaceBlock(player, ctx.world(), pos) && ctx.world().getBlockState(pos).canBeReplaced()
                && blockHit.getDirection() != net.minecraft.core.Direction.DOWN) {
            if (!ctx.world().isClientSide) {
                var state = blockHit.getDirection().getAxis().isHorizontal()
                        ? net.minecraft.world.level.block.Blocks.WALL_TORCH.defaultBlockState()
                                .setValue(net.minecraft.world.level.block.WallTorchBlock.FACING, blockHit.getDirection())
                        : net.minecraft.world.level.block.Blocks.TORCH.defaultBlockState();
                ctx.world().setBlockAndUpdate(pos, state);
                if (!player.isCreative()) {
                    player.getInventory().removeItem(player.getInventory().findSlotMatchingItem(torch) >= 0
                            ? player.getInventory().getItem(player.getInventory().findSlotMatchingItem(torch)).split(1)
                            : net.minecraft.world.item.ItemStack.EMPTY);
                }
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
