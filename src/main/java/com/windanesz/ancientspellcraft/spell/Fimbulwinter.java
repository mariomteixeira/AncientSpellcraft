package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperties;
import com.koomplo.wizardry.api.content.util.BlockUtil;
import com.koomplo.wizardry.content.entity.projectile.IceChargeEntity;
import com.koomplo.wizardry.content.spell.DefaultProperties;
import com.koomplo.wizardry.setup.registries.EBEntities;
import com.koomplo.wizardry.setup.registries.client.EBParticles;
import com.windanesz.ancientspellcraft.entity.projectile.SafeIceShardEntity;
import com.windanesz.ancientspellcraft.registry.ASEntities;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/** O inverno dos invernos: chuva continua de estilhacos e cargas de gelo (1.12.2 Fimbulwinter). */
public class Fimbulwinter extends Spell {

    @Override
    public boolean isInstantCast() {
        return false;
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        if (!ctx.world().isClientSide) {
            int radius = (int) (property(DefaultProperties.EFFECT_RADIUS) * ctx.modifiers().get(SpellModifiers.BLAST));
            int yPos = ctx.caster().blockPosition().getY() + 10;
            List<BlockPos> ring = BlockUtil.getBlockSphere(ctx.caster().blockPosition().above(12), radius).stream()
                    .filter(p -> p.getY() == yPos && p.distSqr(new BlockPos((int) ctx.caster().getX(), p.getY(), (int) ctx.caster().getZ())) > 12)
                    .toList();
            if (!ring.isEmpty()) {
                for (int i = 0; i < 3; i++) {
                    BlockPos pos = ring.get(ctx.world().random.nextInt(ring.size()));
                    SafeIceShardEntity shard = new SafeIceShardEntity(ASEntities.SAFE_ICE_SHARD.get(), ctx.world());
                    shard.setOwner(ctx.caster());
                    shard.setPos(pos.getX() + ctx.world().random.nextFloat(), pos.getY() + ctx.world().random.nextFloat(),
                            pos.getZ() + ctx.world().random.nextFloat());
                    shard.setDeltaMovement(0, -0.5, 0);
                    ctx.world().addFreshEntity(shard);
                }
                BlockPos pos = ring.get(ctx.world().random.nextInt(ring.size()));
                IceChargeEntity charge = new IceChargeEntity(EBEntities.ICE_CHARGE.get(), ctx.world());
                charge.setOwner(ctx.caster());
                charge.setPos(pos.getX(), pos.getY(), pos.getZ());
                charge.setDeltaMovement(0, ctx.world().random.nextDouble() * 0.2, 0);
                ctx.world().addFreshEntity(charge);
            }
        } else {
            ParticleBuilder.create(EBParticles.SNOW, ctx.caster()).velocity(0, 0.1, 0).time(40).scale(1.2f).spawn(ctx.world());
            for (int i = 0; i < 6; i++) {
                ParticleBuilder.create(EBParticles.SNOW)
                        .pos(ctx.caster().getX(), ctx.caster().getY() + ctx.world().random.nextDouble() * 18, ctx.caster().getZ())
                        .time(100).scale(2).shaded(true).spawn(ctx.world());
            }
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
