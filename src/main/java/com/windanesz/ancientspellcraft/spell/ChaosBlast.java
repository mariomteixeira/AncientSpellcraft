package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.api.content.spell.Element;
import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperties;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperty;
import com.koomplo.wizardry.api.content.util.EntityUtil;
import com.koomplo.wizardry.api.content.util.MagicDamageSource;
import com.koomplo.wizardry.content.item.armor.WizardArmorType;
import com.koomplo.wizardry.content.spell.DefaultProperties;
import com.koomplo.wizardry.setup.registries.EBDamageSources;
import com.koomplo.wizardry.setup.registries.client.EBParticles;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/**
 * Rajada do caos (1.12.2 ChaosBlast): carrega charging_time e dispara UM raio elemental devastador
 * (dano MAGIC + efeito elemental do attunement). TODO ring_chaos_blast_multitarget (2o alvo a 60%).
 */
public class ChaosBlast extends Spell implements ClassSpell {

    public static final SpellProperty<Integer> CHARGING_TIME = SpellProperty.intProperty("charging_time");

    @Override
    public boolean isInstantCast() {
        return false;
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        int chargeup = property(CHARGING_TIME);
        Element element = elementOrMagic(ctx.caster());
        int[] colours = WarlockSpellEffects.colours(element);

        if (ctx.castingTicks() < chargeup) {
            if (ctx.world().isClientSide) {
                for (int i = 0; i < 10; i++) {
                    ParticleBuilder.create(EBParticles.FLASH, ctx.caster())
                            .scale(Math.min(1.0f, 2 / (Math.max(1.2f, ctx.castingTicks() * 0.2f) * 0.7f)))
                            .color(colours[ctx.world().random.nextInt(2)])
                            .pos(0, ctx.world().random.nextFloat() + 0.2f, 0).time(40).spawn(ctx.world());
                }
            }
            return true;
        }
        if (ctx.castingTicks() > chargeup + 1) return false;

        double range = property(DefaultProperties.RANGE) * ctx.modifiers().get(SpellModifiers.RANGE);
        Vec3 origin = ctx.caster().getEyePosition();
        Vec3 look = ctx.caster().getLookAngle();
        Vec3 endpoint = origin.add(look.scale(range));

        var blockHit = ctx.world().clip(new net.minecraft.world.level.ClipContext(origin, endpoint,
                net.minecraft.world.level.ClipContext.Block.COLLIDER, net.minecraft.world.level.ClipContext.Fluid.NONE, ctx.caster()));
        if (blockHit.getType() != net.minecraft.world.phys.HitResult.Type.MISS) endpoint = blockHit.getLocation();

        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(ctx.world(), ctx.caster(), origin, endpoint,
                ctx.caster().getBoundingBox().expandTowards(look.scale(range)).inflate(1.0),
                e -> e instanceof LivingEntity && e != ctx.caster());
        if (entityHit != null) endpoint = entityHit.getLocation();

        if (!ctx.world().isClientSide) {
            if (entityHit != null && entityHit.getEntity() instanceof LivingEntity target) {
                float damage = property(DefaultProperties.DAMAGE) * ctx.modifiers().get(SpellModifiers.POTENCY);
                EntityUtil.attackEntityWithoutKnockback(target,
                        MagicDamageSource.causeDirectMagicDamage(ctx.caster(), EBDamageSources.MAGIC), damage);
                WarlockSpellEffects.affectEntity(target, element, ctx.caster(), true);
                // ring_chaos_blast_multitarget: o raio segue e atinge um 2º alvo a 60% (1.12.2)
                if (com.koomplo.wizardry.core.integrations.ArtifactChannel.isEquipped(ctx.caster(),
                        ASItems.RING_CHAOS_BLAST_MULTITARGET.get())) {
                    Vec3 firstHit = entityHit.getLocation();
                    Vec3 rayEnd = origin.add(look.scale(range));
                    EntityHitResult second = ProjectileUtil.getEntityHitResult(ctx.world(), ctx.caster(), firstHit, rayEnd,
                            target.getBoundingBox().expandTowards(look.scale(range)).inflate(1.0),
                            e -> e instanceof LivingEntity && e != ctx.caster() && e != target);
                    if (second != null && second.getEntity() instanceof LivingEntity secondTarget) {
                        EntityUtil.attackEntityWithoutKnockback(secondTarget,
                                MagicDamageSource.causeDirectMagicDamage(ctx.caster(), EBDamageSources.MAGIC), damage * 0.6f);
                        WarlockSpellEffects.affectEntity(secondTarget, element, ctx.caster(), true);
                    }
                }
            }
        } else {
            double distance = origin.distanceTo(endpoint);
            for (double d = 1; d < distance; d += 0.5) {
                Vec3 pos = origin.add(look.scale(d));
                ParticleBuilder.create(WarlockSpellEffects.particle(element))
                        .pos(pos.x, pos.y, pos.z).color(colours[ctx.world().random.nextInt(2)])
                        .scale(1.5f).time(15).spawn(ctx.world());
            }
            ParticleBuilder.create(EBParticles.FLASH).pos(endpoint.x, endpoint.y, endpoint.z)
                    .scale(3f).color(colours[0]).time(30).spawn(ctx.world());
        }
        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
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

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.empty();
    }
}
