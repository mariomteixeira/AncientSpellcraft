package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.internal.CastContext;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.windanesz.ancientspellcraft.block.MagicFlameBlockEntity;
import com.windanesz.ancientspellcraft.registry.ASAttachments;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Enchant Fireplace (1.12.2 EnchantFireplace): mira FOGO numa lareira (>=3 lados sólidos) e o
 * transforma em chama de teleporte; sneak-cast numa chama guarda a origem, cast normal em outra
 * LINKA as duas (bidirecional — desvio: o original era unidirecional). Ficar 20t na chama viaja.
 */
public class EnchantFireplaceSpell extends ASRaySpell {

    private static final String LAST_FLAME_TAG = "LastEnchantedFlame";

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        return false;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        if (ctx.world().isClientSide) return true;
        if (!(ctx.caster() instanceof Player caster)) return false;
        BlockPos pos = blockHit.getBlockPos();
        var state = ctx.world().getBlockState(pos);

        if (state.is(ASBlocks.TELEPORTATION_FLAME.get())) {
            var tag = caster.getData(ASAttachments.PLAYER_DATA);
            if (caster.isShiftKeyDown()) {
                tag.put(LAST_FLAME_TAG, NbtUtils.writeBlockPos(pos));
                caster.setData(ASAttachments.PLAYER_DATA, tag);
                caster.displayClientMessage(Component.translatable("spell.ancientspellcraft.teleportation_flame.stored"), true);
                return true;
            }
            BlockPos stored = NbtUtils.readBlockPos(tag, LAST_FLAME_TAG).orElse(null);
            if (stored == null || stored.equals(pos)) {
                caster.displayClientMessage(Component.translatable("spell.ancientspellcraft.teleportation_flame.cannot_link"), true);
                return false;
            }
            if (ctx.world().getBlockEntity(pos) instanceof MagicFlameBlockEntity here
                    && ctx.world().getBlockEntity(stored) instanceof MagicFlameBlockEntity there) {
                here.setTargetFlame(stored);
                there.setTargetFlame(pos);
                caster.displayClientMessage(Component.translatable("spell.ancientspellcraft.teleportation_flame.link_success"), true);
                return true;
            }
            return false;
        }

        if (state.is(Blocks.FIRE) || state.is(Blocks.CAMPFIRE)) {
            int solidSides = 0;
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                if (ctx.world().getBlockState(pos.relative(direction)).isSolid()) solidSides++;
            }
            if (solidSides < 3) {
                caster.displayClientMessage(Component.translatable("spell.ancientspellcraft.teleportation_flame.no_fireplace"), true);
                return false;
            }
            ctx.world().setBlockAndUpdate(pos, ASBlocks.TELEPORTATION_FLAME.get().defaultBlockState());
            if (ctx.world().getBlockEntity(pos) instanceof MagicFlameBlockEntity flame) {
                flame.setLifetime((int) (this.property(com.koomplo.wizardry.content.spell.DefaultProperties.EFFECT_DURATION)
                        * ctx.modifiers().get(SpellModifiers.DURATION)));
                flame.setCaster(caster);
            }
            return true;
        }
        caster.displayClientMessage(Component.translatable("spell.ancientspellcraft.teleportation_flame.not_looking_at_flame"), true);
        return false;
    }

    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        return false;
    }
}
