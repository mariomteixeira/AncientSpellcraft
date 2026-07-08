package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperties;
import com.koomplo.wizardry.api.content.util.BlockUtil;
import com.koomplo.wizardry.api.content.util.EntityUtil;
import com.koomplo.wizardry.content.entity.MeteorEntity;
import com.koomplo.wizardry.content.spell.DefaultProperties;
import com.koomplo.wizardry.setup.registries.client.EBParticles;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/** Chuva de meteoros continua ao redor do caster (1.12.2 Armageddon). */
public class Armageddon extends Spell {

    @Override
    public boolean isInstantCast() {
        return false;
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        if (!ctx.world().isClientSide) {
            float radius = property(DefaultProperties.EFFECT_RADIUS) * ctx.modifiers().get(SpellModifiers.BLAST);
            int yPos = ctx.caster().blockPosition().getY() + 16;
            List<BlockPos> ring = BlockUtil.getBlockSphere(ctx.caster().blockPosition().above(6), radius).stream()
                    .filter(p -> p.getY() == yPos && p.distSqr(new BlockPos((int) ctx.caster().getX(), p.getY(), (int) ctx.caster().getZ())) > 9)
                    .toList();
            if (!ring.isEmpty()) {
                BlockPos pos = ring.get(ctx.world().random.nextInt(ring.size()));
                MeteorEntity meteor = new MeteorEntity(ctx.world(), pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                        0.5f, EntityUtil.canDamageBlocks(ctx.caster(), ctx.world()));
                meteor.setDeltaMovement(new Vec3(0, -1, 0));
                ctx.world().addFreshEntity(meteor);
            }
        } else {
            ParticleBuilder.create(EBParticles.MAGIC_FIRE, ctx.caster()).velocity(0, 0.1, 0)
                    .time(40).scale(1.2f).spawn(ctx.world());
        }
        this.playSoundLoop(ctx.world(), ctx.caster(), ctx.castingTicks());
        return true;
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == ASItems.ANCIENT_SPELLCRAFT_SPELL_BOOK.get() || item == ASItems.ANCIENT_SPELLCRAFT_SCROLL.get();
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.empty();
    }
}
