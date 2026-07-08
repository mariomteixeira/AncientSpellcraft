package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperties;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperty;
import com.koomplo.wizardry.api.content.util.BlockUtil;
import com.koomplo.wizardry.api.content.util.EntityUtil;
import com.koomplo.wizardry.api.content.util.MagicDamageSource;
import com.koomplo.wizardry.content.spell.DefaultProperties;
import com.koomplo.wizardry.core.AllyDesignation;
import com.koomplo.wizardry.setup.registries.EBDamageSources;
import com.koomplo.wizardry.setup.registries.client.EBParticles;
import com.windanesz.ancientspellcraft.block.TemporaryBlockEntity;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Carrega por 2s e explode em estilhacos de gelo radiais (1.12.2 FrostNova). */
public class FrostNova extends Spell {

    @Override
    public boolean applicableForItem(Item item) {
        return item == ASItems.ANCIENT_SPELLCRAFT_SPELL_BOOK.get() || item == ASItems.ANCIENT_SPELLCRAFT_SCROLL.get();
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.empty();
    }

    public static final SpellProperty<Integer> SHARD_COUNT = SpellProperty.intProperty("shard_count");

    @Override
    public boolean isInstantCast() {
        return false;
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        this.playSoundLoop(ctx.world(), ctx.caster(), ctx.castingTicks());
        if (ctx.castingTicks() < 40) {
            if (ctx.world().isClientSide) {
                ParticleBuilder.create(EBParticles.SNOW, ctx.caster()).spawn(ctx.world());
            }
            return true;
        }
        if (ctx.castingTicks() > 40) return false;
        if (!ctx.world().isClientSide) {
            for (int i = 0; i < property(SHARD_COUNT); i++) {
                double dx = ctx.world().random.nextDouble() - 0.5;
                double dz = ctx.world().random.nextDouble() - 0.5;
                var shard = new com.windanesz.ancientspellcraft.entity.projectile.SafeIceShardEntity(
                        com.windanesz.ancientspellcraft.registry.ASEntities.SAFE_ICE_SHARD.get(), ctx.world());
                shard.setPos(ctx.caster().getX() + dx, ctx.caster().getY() + 1, ctx.caster().getZ() + dz);
                shard.setDeltaMovement(dx * 2.5, 0.2, dz * 2.5);
                shard.setOwner(ctx.caster());
                ctx.world().addFreshEntity(shard);
            }
        }
        return true;
    }
}
