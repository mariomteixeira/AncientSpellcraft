package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.internal.CastContext;
import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.content.item.armor.WizardArmorType;
import com.koomplo.wizardry.setup.registries.EBMobEffects;
import com.windanesz.ancientspellcraft.misc.ASCameraState;
import com.windanesz.ancientspellcraft.registry.ASAttachments;
import com.windanesz.ancientspellcraft.registry.ASEffects;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Scrying Orb (1.12.2 ScryingOrb, warlock): sneak-cast mira um bloco e grava o ponto de visão
 * (2 blocos afastado da face atingida); cast normal projeta a visão para lá por 6s
 * (astral_projection + sixth_sense); recast desliga. Desvio: a posição gravada no client é uma
 * cópia local (o server guarda no attachment) — relogar exige gravar de novo.
 */
public class ScryingOrbSpell extends ASRaySpell implements ClassSpell {

    private static final String POS_TAG = "ScryingOrbPos";
    private static final String ACTIVE_TAG = "ScryingOrbActive";
    private static final int SCRYING_DURATION = 120;

    @Override
    public boolean cast(PlayerCastContext ctx) {
        Player caster = ctx.caster();

        if (ctx.world().isClientSide) {
            if (ASCameraState.scryingActive) {
                ASCameraState.scryingActive = false;
                return true;
            }
            if (!caster.isShiftKeyDown()) {
                if (ASCameraState.scryingStoredPos != null) ASCameraState.scryingActive = true;
                return true;
            }
            return super.cast(ctx);
        }

        var tag = caster.getData(ASAttachments.PLAYER_DATA);
        if (tag.getBoolean(ACTIVE_TAG)) {
            tag.putBoolean(ACTIVE_TAG, false);
            caster.setData(ASAttachments.PLAYER_DATA, tag);
            caster.removeEffect(ASEffects.ASTRAL_PROJECTION);
            caster.removeEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(EBMobEffects.SIXTH_SENSE.get()));
            return true;
        }
        if (!caster.isShiftKeyDown()) {
            BlockPos stored = NbtUtils.readBlockPos(tag, POS_TAG).orElse(null);
            if (stored != null) {
                caster.addEffect(new MobEffectInstance(ASEffects.ASTRAL_PROJECTION, SCRYING_DURATION, 0));
                caster.addEffect(new MobEffectInstance(
                        BuiltInRegistries.MOB_EFFECT.wrapAsHolder(EBMobEffects.SIXTH_SENSE.get()), SCRYING_DURATION, 0));
                tag.putBoolean(ACTIVE_TAG, true);
                caster.setData(ASAttachments.PLAYER_DATA, tag);
            }
            return true;
        }
        return super.cast(ctx);
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        return false;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        BlockPos pos = blockHit.getBlockPos().relative(blockHit.getDirection(), 2);
        if (ctx.world().isClientSide) {
            ASCameraState.scryingStoredPos = pos;
        } else if (ctx.caster() instanceof Player player) {
            var tag = player.getData(ASAttachments.PLAYER_DATA);
            tag.put(POS_TAG, NbtUtils.writeBlockPos(pos));
            player.setData(ASAttachments.PLAYER_DATA, tag);
        }
        return true;
    }

    @Override
    public WizardArmorType armourClass() {
        return WizardArmorType.WARLOCK;
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == ASItems.FORBIDDEN_TOME.get();
    }
}
