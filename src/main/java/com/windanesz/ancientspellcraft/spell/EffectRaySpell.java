package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.api.content.spell.internal.CastContext;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperties;
import com.koomplo.wizardry.api.content.util.EntityUtil;
import com.koomplo.wizardry.content.spell.DefaultProperties;
import com.koomplo.wizardry.content.spell.abstr.BuffSpell;
import com.koomplo.wizardry.content.spell.abstr.RaySpell;
import com.koomplo.wizardry.setup.registries.client.EBParticles;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

/** Ray generico que aplica efeitos no alvo (padrao dos rays de curse/debuff do 1.12.2). */
public class EffectRaySpell extends RaySpell {

    /** Particula do feixe: tipo, cor (-1 = sem clr), time base + aleatorio (0 = default). */
    public record ParticleSpec(com.koomplo.wizardry.api.content.DeferredObject<net.minecraft.core.particles.SimpleParticleType> type, int color, int timeBase, int timeRand) {
    }

    private final List<Supplier<Holder<MobEffect>>> effects;
    private final boolean permanent;
    private final boolean useStrength;
    private final List<ParticleSpec> particles;

    public EffectRaySpell(boolean permanent, boolean useStrength,
                          List<Supplier<Holder<MobEffect>>> effects, List<ParticleSpec> particles) {
        this.permanent = permanent;
        this.useStrength = useStrength;
        this.effects = effects;
        this.particles = particles;
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == ASItems.ANCIENT_SPELLCRAFT_SPELL_BOOK.get() || item == ASItems.ANCIENT_SPELLCRAFT_SCROLL.get();
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if (!(entityHit.getEntity() instanceof LivingEntity target)) return false;
        if (!ctx.world().isClientSide) {
            int duration = permanent ? Integer.MAX_VALUE
                    : (int) (property(DefaultProperties.EFFECT_DURATION) * ctx.modifiers().get(SpellModifiers.DURATION));
            int amplifier = (useStrength ? property(DefaultProperties.EFFECT_STRENGTH) : 0)
                    + BuffSpell.getStandardBonusAmplifier(ctx.modifiers().get(SpellModifiers.POTENCY));
            for (Supplier<Holder<MobEffect>> effect : effects) {
                target.addEffect(new MobEffectInstance(effect.get(), duration, amplifier));
            }
        }
        return true;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        return false;
    }

    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        return true;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        for (ParticleSpec spec : particles) {
            ParticleBuilder builder = ParticleBuilder.create(spec.type()).pos(x, y, z);
            if (spec.color() != -1) builder.color(spec.color());
            if (spec.timeBase() > 0) builder.time(spec.timeBase() + ctx.world().random.nextInt(Math.max(1, spec.timeRand())));
            builder.spawn(ctx.world());
        }
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.empty();
    }
}
