package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.internal.CastContext;
import com.windanesz.ancientspellcraft.registry.ASEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

/** Amarra um no no tempo: quando o efeito expira, o caster volta a posicao/vida do cast (1.12.2 TimeKnot). */
public class TimeKnot extends ASBuffSpell {

    @SuppressWarnings("unchecked")
    public TimeKnot() {
        super(42 / 255f, 206 / 255f, 88 / 255f, () -> ASEffects.TIME_KNOT);
    }

    @Override
    protected boolean applyEffects(CastContext ctx, LivingEntity target) {
        if (target instanceof ServerPlayer player) {
            CompoundTag tag = new CompoundTag();
            tag.putDouble("X", player.getX());
            tag.putDouble("Y", player.getY());
            tag.putDouble("Z", player.getZ());
            tag.putFloat("Health", player.getHealth());
            tag.putString("Dim", player.level().dimension().location().toString());
            player.setData(com.windanesz.ancientspellcraft.registry.ASAttachments.TIME_KNOT, tag);
        }
        return super.applyEffects(ctx, target);
    }
}
