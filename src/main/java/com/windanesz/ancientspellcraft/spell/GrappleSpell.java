package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperties;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperty;
import com.koomplo.wizardry.api.content.util.RayTracer;
import com.koomplo.wizardry.setup.registries.client.EBParticles;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Grapple (1.12.2 Grapple): cipó mágico contínuo — estende até o alvo; bloco puxa VOCÊ,
 * entidade é puxada ATÉ você (para a 3 blocos); estica até 1.5x o alcance antes de arrebentar.
 * Física 1:1 (aceleração 0.3). Desvios documentados: charm_abseiling (descer pela corda) fica
 * com artefatos; corda = partículas LEAF espaçadas (sem a partícula VINE contínua do 1.12.2).
 */
public class GrappleSpell extends Spell {

    private static final SpellProperty<Float> RANGE = SpellProperty.floatProperty("range", 12.0f);
    private static final SpellProperty<Float> EXTENSION_SPEED = SpellProperty.floatProperty("extension_speed", 1.5f);
    private static final SpellProperty<Float> REEL_SPEED = SpellProperty.floatProperty("reel_speed", 0.8f);

    private static final double MINIMUM_REEL_DISTANCE = 3;
    private static final double REEL_ACCELERATION = 0.3;
    private static final double STRETCH_LIMIT = 1.5;
    private static final double Y_OFFSET = 0.25;

    private static final Map<UUID, HitResult> TARGETS = new HashMap<>();

    @Override
    public boolean isInstantCast() {
        return false;
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        Player caster = ctx.caster();
        Vec3 origin = caster.getEyePosition();
        float extensionSpeed = this.property(EXTENSION_SPEED) * ctx.modifiers().get(SpellModifiers.POTENCY);
        float range = this.property(RANGE) * ctx.modifiers().get(SpellModifiers.RANGE);

        HitResult hit = ctx.castingTicks() <= 1 ? null : TARGETS.get(caster.getUUID());
        if (hit == null) {
            Vec3 end = origin.add(caster.getLookAngle().scale(range));
            hit = RayTracer.rayTrace(ctx.world(), caster, origin, end, 0.3f, false,
                    LivingEntity.class, RayTracer.ignoreEntityFilter(caster));
            if (hit == null) hit = ctx.world().clip(new net.minecraft.world.level.ClipContext(origin, end,
                    net.minecraft.world.level.ClipContext.Block.COLLIDER, net.minecraft.world.level.ClipContext.Fluid.NONE, caster));
            TARGETS.put(caster.getUUID(), hit);
            caster.swing(ctx.hand());
        }

        Vec3 target = hit.getLocation();
        Entity hitEntity = hit instanceof EntityHitResult entityHit ? entityHit.getEntity() : null;
        if (hitEntity != null) target = hitEntity.position().add(0, hitEntity.getBbHeight() / 2, 0);

        double distance = origin.distanceTo(target);
        Vec3 direction = target.subtract(origin).normalize();
        double maxLength = range * STRETCH_LIMIT;
        if (distance > maxLength || hit.getType() == HitResult.Type.MISS && ctx.castingTicks() * extensionSpeed >= distance) {
            TARGETS.remove(caster.getUUID());
            return false;
        }

        boolean extending = ctx.castingTicks() * extensionSpeed < distance;
        if (ctx.world().isClientSide) {
            double drawn = extending ? ctx.castingTicks() * extensionSpeed : distance;
            for (double d = 0; d < drawn; d += 1.5) {
                Vec3 pos = origin.subtract(0, Y_OFFSET, 0).add(direction.scale(d));
                ParticleBuilder.create(EBParticles.LEAF).pos(pos.x, pos.y, pos.z).time(3).spawn(ctx.world());
            }
        }
        if (extending) {
            if (ctx.castingTicks() == 1) this.playSound(ctx.world(), caster, ctx.castingTicks(), -1);
            return true;
        }

        Vec3 velocity = direction.scale(this.property(REEL_SPEED) * ctx.modifiers().get(SpellModifiers.POTENCY));
        if (hitEntity != null) {
            if (distance > MINIMUM_REEL_DISTANCE) {
                Vec3 motion = hitEntity.getDeltaMovement();
                hitEntity.push((-velocity.x - motion.x) * REEL_ACCELERATION,
                        (-velocity.y - motion.y) * REEL_ACCELERATION,
                        (-velocity.z - motion.z) * REEL_ACCELERATION);
                if (hitEntity instanceof ServerPlayer serverPlayer) {
                    serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(hitEntity));
                }
            }
        } else if (hit instanceof BlockHitResult) {
            Vec3 motion = caster.getDeltaMovement();
            caster.push((velocity.x - motion.x) * REEL_ACCELERATION,
                    (velocity.y - motion.y) * REEL_ACCELERATION,
                    (velocity.z - motion.z) * REEL_ACCELERATION);
            if (caster.getDeltaMovement().y > 0) caster.fallDistance = 0;
        } else {
            TARGETS.remove(caster.getUUID());
            return false;
        }
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
