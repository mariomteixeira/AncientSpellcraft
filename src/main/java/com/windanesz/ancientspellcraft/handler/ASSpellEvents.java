package com.windanesz.ancientspellcraft.handler;

import com.koomplo.wizardry.api.content.util.EntityUtil;
import com.windanesz.ancientspellcraft.registry.ASSpells;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public final class ASSpellEvents {

    /** Guarda do burrow: se o cast parou, o jogador nao pode continuar atravessando o chao. */
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.noPhysics && !player.isSpectator()
                && !EntityUtil.isCasting(player, ASSpells.BURROW.get())) {
            player.noPhysics = false;
        }
        if (!player.level().isClientSide) {
            tickMasterBoltPull(player);
            tickSpringCharge(player);
            tickPhaseJump(player);
            // amulet_celerity: remove o modifier de velocidade quando o amuleto sai
            if (player.tickCount % 20 == 0) {
                var attribute = player.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED);
                if (attribute != null
                        && attribute.hasModifier(com.windanesz.ancientspellcraft.item.ASArtifactEffects.CELERITY_MODIFIER)
                        && !com.koomplo.wizardry.core.integrations.ArtifactChannel.isEquipped(player,
                        com.windanesz.ancientspellcraft.registry.ASItems.AMULET_CELERITY.get())) {
                    attribute.removeModifier(com.windanesz.ancientspellcraft.item.ASArtifactEffects.CELERITY_MODIFIER);
                }
            }
        }
    }

    /** Release do phase_jump: parou de canalizar -> teleporta min..max (x blast) + extra/segundo (Banish do Redux). */
    private static void tickPhaseJump(Player player) {
        var data = com.windanesz.ancientspellcraft.spell.PhaseJumpSpell.CHANNELING.get(player.getUUID());
        if (data == null) return;
        var spell = com.koomplo.wizardry.core.platform.Services.REGISTRY_UTIL.getSpell(
                net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("ancientspellcraft", "phase_jump"));
        if (!(spell instanceof com.windanesz.ancientspellcraft.spell.PhaseJumpSpell phaseJump)) return;
        if (EntityUtil.isCasting(player, phaseJump)) {
            return; // ainda canalizando
        }
        com.windanesz.ancientspellcraft.spell.PhaseJumpSpell.CHANNELING.remove(player.getUUID());
        double min = phaseJump.property(com.windanesz.ancientspellcraft.spell.PhaseJumpSpell.MINIMUM_TELEPORT_DISTANCE);
        double max = phaseJump.property(com.windanesz.ancientspellcraft.spell.PhaseJumpSpell.MAXIMUM_TELEPORT_DISTANCE);
        double bonus = (data[0] / 20f) * phaseJump.property(com.windanesz.ancientspellcraft.spell.PhaseJumpSpell.EXTRA_DISTANCE_PER_SECOND);
        double radius = (min + player.level().random.nextDouble() * (max - min)) * data[1] + bonus;
        var banish = com.koomplo.wizardry.core.platform.Services.REGISTRY_UTIL.getSpell(
                net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("ebwizardry", "banish"));
        if (banish instanceof com.koomplo.wizardry.content.spell.necromancy.Banish b) {
            b.teleport(player, player.level(), radius);
        }
    }

    /** Lançamento do spring_charge: parou de castar com carga no chão -> salto (fórmula 1.12.2). */
    private static void tickSpringCharge(Player player) {
        var data = com.windanesz.ancientspellcraft.spell.SpringChargeSpell.CHARGING.get(player.getUUID());
        if (data == null) return;
        var spell = com.koomplo.wizardry.core.platform.Services.REGISTRY_UTIL.getSpell(
                net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("ancientspellcraft", "spring_charge"));
        if (!(spell instanceof com.windanesz.ancientspellcraft.spell.SpringChargeSpell spring)) return;
        if (EntityUtil.isCasting(player, spring)) {
            return; // ainda carregando
        }
        com.windanesz.ancientspellcraft.spell.SpringChargeSpell.CHARGING.remove(player.getUUID());
        if (!player.onGround() || data[0] < 4) return;
        float ticks = Math.min(40, data[0]);
        double vertical = spring.property(com.windanesz.ancientspellcraft.spell.SpringChargeSpell.VERTICAL_SPEED) * data[1] * ticks;
        double horizontal = spring.property(com.windanesz.ancientspellcraft.spell.SpringChargeSpell.HORIZONTAL_SPEED) * ticks;
        var look = player.getLookAngle();
        player.setDeltaMovement(player.getDeltaMovement().x + look.x * horizontal, vertical,
                player.getDeltaMovement().z + look.z * horizontal);
        player.hurtMarked = true; // sync do motion pro client
        player.fallDistance = 0;
    }

    /**
     * Puxão do master_bolt (1.12.2 MasterBolt.update): countdown de 20t movendo o jogador em
     * passos até o bloco, deixando um rastro de lightning_block temporário; na chegada, explosão
     * proporcional à queda + esfera de raios + devolve o item. Desvio: sem o efeito static_aura.
     */
    private static void tickMasterBoltPull(Player player) {
        var tag = player.getData(com.windanesz.ancientspellcraft.registry.ASAttachments.PLAYER_DATA);
        int countdown = tag.getInt(com.windanesz.ancientspellcraft.spell.MasterBoltSpell.COUNTDOWN_TAG);
        if (countdown <= 0) return;
        var destOpt = net.minecraft.nbt.NbtUtils.readBlockPos(tag, com.windanesz.ancientspellcraft.spell.MasterBoltSpell.LOCATION_TAG);
        if (destOpt.isEmpty()) {
            tag.remove(com.windanesz.ancientspellcraft.spell.MasterBoltSpell.COUNTDOWN_TAG);
            player.setData(com.windanesz.ancientspellcraft.registry.ASAttachments.PLAYER_DATA, tag);
            return;
        }
        var dest = destOpt.get();
        var level = player.level();
        if (level.getBlockState(dest).is(com.windanesz.ancientspellcraft.registry.ASBlocks.MASTER_BOLT.get())) {
            level.removeBlock(dest, false);
        }

        boolean arrived = false;
        if (countdown > 1 && player.distanceToSqr(dest.getX() + 0.5, dest.getY(), dest.getZ() + 0.5) > 2.0) {
            double stepX = (dest.getX() + 0.5 - player.getX()) / countdown;
            double stepY = (dest.getY() - player.getY()) / countdown;
            double stepZ = (dest.getZ() + 0.5 - player.getZ()) / countdown;
            for (int i = 0; i < 10 && !arrived; i++) {
                player.teleportTo(player.getX() + stepX, player.getY() + stepY, player.getZ() + stepZ);
                var pos = player.blockPosition();
                if (level.getBlockState(pos).isAir()) {
                    com.windanesz.ancientspellcraft.block.TemporaryBlockEntity.place(player, level,
                            com.windanesz.ancientspellcraft.registry.ASBlocks.LIGHTNING_BLOCK.get(), pos, 60);
                }
                arrived = player.distanceToSqr(dest.getX() + 0.5, dest.getY(), dest.getZ() + 0.5) < 2.0;
            }
        } else {
            arrived = true;
        }

        if (arrived) {
            level.explode(player, player.getX(), player.getY(), player.getZ(),
                    Math.min(2.0F, player.fallDistance * 0.05F), net.minecraft.world.level.Level.ExplosionInteraction.MOB);
            float radius = Math.max(1.0F, Math.min(3.0F, player.fallDistance * 0.2F));
            var center = player.blockPosition();
            for (var pos : net.minecraft.core.BlockPos.betweenClosed(center.offset((int) -radius, (int) -radius, (int) -radius),
                    center.offset((int) radius, (int) radius, (int) radius))) {
                if (pos.distSqr(center) <= radius * radius && level.getBlockState(pos).isAir()) {
                    com.windanesz.ancientspellcraft.block.TemporaryBlockEntity.place(player, level,
                            com.windanesz.ancientspellcraft.registry.ASBlocks.LIGHTNING_BLOCK.get(), pos.immutable(), 90);
                }
            }
            player.getInventory().placeItemBackInInventory(
                    new net.minecraft.world.item.ItemStack(com.windanesz.ancientspellcraft.registry.ASItems.MASTER_BOLT.get()));
            player.fallDistance = 0;
            tag.remove(com.windanesz.ancientspellcraft.spell.MasterBoltSpell.LOCATION_TAG);
            tag.remove(com.windanesz.ancientspellcraft.spell.MasterBoltSpell.DIMENSION_TAG);
            tag.remove(com.windanesz.ancientspellcraft.spell.MasterBoltSpell.COUNTDOWN_TAG);
        } else {
            tag.putInt(com.windanesz.ancientspellcraft.spell.MasterBoltSpell.COUNTDOWN_TAG, countdown - 1);
        }
        player.setData(com.windanesz.ancientspellcraft.registry.ASAttachments.PLAYER_DATA, tag);
    }

    /** static_charge (1.12.2 ASEventHandler): espada carregada dá +2 de dano por nível até expirar. */
    public static void onIncomingDamage(net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent event) {
        if (event.getEntity().level().isClientSide) return;
        if (!(event.getSource().getDirectEntity() instanceof net.minecraft.world.entity.LivingEntity attacker)) return;
        var stack = attacker.getMainHandItem();
        if (!(stack.getItem() instanceof net.minecraft.world.item.SwordItem)) return;
        var tag = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.EMPTY).copyTag();
        int level = tag.getInt(com.windanesz.ancientspellcraft.spell.StaticChargeSpell.LEVEL_TAG);
        if (level <= 0) return;
        if (tag.getLong(com.windanesz.ancientspellcraft.spell.StaticChargeSpell.EXPIRY_TAG) < event.getEntity().level().getGameTime()) {
            net.minecraft.world.item.component.CustomData.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA, stack, t -> {
                t.remove(com.windanesz.ancientspellcraft.spell.StaticChargeSpell.LEVEL_TAG);
                t.remove(com.windanesz.ancientspellcraft.spell.StaticChargeSpell.EXPIRY_TAG);
            });
            return;
        }
        event.setAmount(event.getAmount() + level * 2);
    }

    /** Gate das class spells (1.12.2 IClassSpell.onSpellCastPreEvent): set completo + warlock attunement. */
    public static void onClassSpellCastPre(com.koomplo.wizardry.api.content.event.SpellCastEvent.Pre event) {
        if (!(event.getSpell() instanceof com.windanesz.ancientspellcraft.spell.ClassSpell classSpell)) return;
        if (!(event.getCaster() instanceof Player player)) return;
        if (classSpell.armourClass() == com.koomplo.wizardry.content.item.armor.WizardArmorType.WARLOCK
                && !ASWarlockEvents.isWarlockAttuned(player)) {
            player.displayClientMessage(net.minecraft.network.chat.Component.translatable(
                    "message.ancientspellcraft.not_warlock_attuned"), true);
            event.setCanceled(true);
            return;
        }
        // 1.12.2: quem fez o ritual de attunement só casta class spell de warlock
        if (classSpell.armourClass() != com.koomplo.wizardry.content.item.armor.WizardArmorType.WARLOCK
                && ASWarlockEvents.isWarlockAttuned(player)) {
            player.displayClientMessage(net.minecraft.network.chat.Component.translatable(
                    "message.ancientspellcraft.warlock_attunement_prevents_spell_cast"), true);
            event.setCanceled(true);
            return;
        }
        if (!isWearingFullSet(player, classSpell.armourClass())) {
            player.displayClientMessage(net.minecraft.network.chat.Component.translatable(
                    "message.ancientspellcraft.must_have_full_matching_set",
                    net.minecraft.network.chat.Component.translatable(
                            "wizard_armour_class." + classSpell.armourClass().name().toLowerCase())), false);
            event.setCanceled(true);
        }
    }

    /** ring_poison_arrow (1.12.2): flecha do jogador acerta — 20% de envenenar por 5s. */
    public static void onProjectileImpact(net.neoforged.neoforge.event.entity.ProjectileImpactEvent event) {
        if (event.getProjectile().level().isClientSide) return;
        if (!(event.getProjectile() instanceof net.minecraft.world.entity.projectile.AbstractArrow arrow)) return;
        if (!(arrow.getOwner() instanceof Player player)) return;
        if (!(event.getRayTraceResult() instanceof net.minecraft.world.phys.EntityHitResult entityHit)
                || !(entityHit.getEntity() instanceof net.minecraft.world.entity.LivingEntity target)) return;
        if (player.level().random.nextFloat() < 0.2f
                && com.koomplo.wizardry.core.integrations.ArtifactChannel.isEquipped(player,
                com.windanesz.ancientspellcraft.registry.ASItems.RING_POISON_ARROW.get())
                && !target.hasEffect(net.minecraft.world.effect.MobEffects.POISON)) {
            target.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.POISON, 100));
        }
    }

    /** Baterias de mana (1.12.2): cast por WAND com mana insuficiente puxa o custo de um anel de mana equipado. */
    public static void onManaBatteryTransfer(com.koomplo.wizardry.api.content.event.SpellCastEvent.Pre event) {
        if (!(event.getCaster() instanceof Player player) || player.level().isClientSide) return;
        if (event.getSource() != com.koomplo.wizardry.api.content.event.SpellCastEvent.Source.WAND) return;
        int cost = (int) (event.getSpell().getCost() * event.getModifiers().get(
                com.koomplo.wizardry.api.content.spell.internal.SpellModifiers.COST) + 0.1f);
        if (cost <= 0) return;
        var wand = player.getMainHandItem().getItem() instanceof com.koomplo.wizardry.api.content.item.ICastItem
                ? player.getMainHandItem() : player.getOffhandItem();
        if (!(wand.getItem() instanceof com.koomplo.wizardry.api.content.item.IManaItem wandMana)) return;
        if (wandMana.getMana(wand) > cost) return;
        for (var artifact : com.koomplo.wizardry.core.integrations.ArtifactChannel.getEquippedArtifacts(player)) {
            if (artifact.getItem() instanceof com.windanesz.ancientspellcraft.item.ManaArtifactItem battery
                    && !(artifact.getItem() instanceof com.windanesz.ancientspellcraft.item.WizardTankardItem)
                    && !(artifact.getItem() instanceof com.windanesz.ancientspellcraft.item.CubePhasingItem)
                    && !(artifact.getItem() instanceof com.windanesz.ancientspellcraft.item.ResistanceAmuletItem)
                    && battery.getMana(artifact) >= cost) {
                battery.consumeMana(artifact, cost, player);
                wandMana.rechargeMana(wand, cost);
                break;
            }
        }
    }

    /** Set das joias de poder (1.12.2): 2+ de ring/amulet/charm_power equipadas = +5% potência por peça além da 1ª. */
    public static void onJewelSetBonus(com.koomplo.wizardry.api.content.event.SpellCastEvent.Pre event) {
        if (!(event.getCaster() instanceof Player player)) return;
        int jewels = 0;
        if (com.koomplo.wizardry.core.integrations.ArtifactChannel.isEquipped(player, com.windanesz.ancientspellcraft.registry.ASItems.RING_POWER.get())) jewels++;
        if (com.koomplo.wizardry.core.integrations.ArtifactChannel.isEquipped(player, com.windanesz.ancientspellcraft.registry.ASItems.AMULET_POWER.get())) jewels++;
        if (com.koomplo.wizardry.core.integrations.ArtifactChannel.isEquipped(player, com.windanesz.ancientspellcraft.registry.ASItems.CHARM_POWER_ORB.get())) jewels++;
        if (jewels > 1) {
            float potency = event.getModifiers().get(com.koomplo.wizardry.api.content.spell.internal.SpellModifiers.POTENCY);
            event.getModifiers().set(com.koomplo.wizardry.api.content.spell.internal.SpellModifiers.POTENCY,
                    potency * (1 + (jewels - 1) * 0.05f));
        }
    }

    /** charm_infernal_stone (1.12.2): quente, spells de FOGO custam -25% de mana e consomem 10 de calor. */
    public static void onInfernalStone(com.koomplo.wizardry.api.content.event.SpellCastEvent.Pre event) {
        if (!(event.getCaster() instanceof Player player) || player.level().isClientSide) return;
        if (!"fire".equals(event.getSpell().getElement().getName())) return;
        for (var artifact : com.koomplo.wizardry.core.integrations.ArtifactChannel.getEquippedArtifacts(player)) {
            if (artifact.getItem() instanceof com.windanesz.ancientspellcraft.item.InfernalStoneItem
                    && com.windanesz.ancientspellcraft.item.InfernalStoneItem.getHeat(artifact) > 0) {
                event.getModifiers().set(com.koomplo.wizardry.api.content.spell.internal.SpellModifiers.COST,
                        event.getModifiers().get(com.koomplo.wizardry.api.content.spell.internal.SpellModifiers.COST) * 0.75f);
                com.windanesz.ancientspellcraft.item.InfernalStoneItem.removeHeat(artifact, 10);
                return;
            }
        }
    }

    /** Castar com a espada de battlemage na mão carrega a lâmina (1.12.2: +20 por cast). */
    public static void onSpellCastPost(com.koomplo.wizardry.api.content.event.SpellCastEvent.Post event) {
        if (event.getCaster() instanceof Player player && !player.level().isClientSide
                && player.getMainHandItem().getItem() instanceof com.windanesz.ancientspellcraft.item.BattlemageSwordItem) {
            com.windanesz.ancientspellcraft.item.BattlemageSwordItem.addCharge(player.getMainHandItem(),
                    com.windanesz.ancientspellcraft.ASServerConfig.SPELLBLADE_CHARGE_GAIN_PER_SPELLCAST.get());
        }
    }

    /**
     * Bloqueio do battlemage_shield (1.12.2: mana = durabilidade): o bloqueio não desgasta o item,
     * consome a mana do escudo (regra vanilla: dano >= 3 gasta 1 + floor(dano)); mana vazia derruba
     * a guarda.
     */
    public static void onShieldBlock(net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        var shield = player.getUseItem();
        if (!(shield.getItem() instanceof com.windanesz.ancientspellcraft.item.BattlemageShieldItem item)) return;
        if (!event.getBlocked()) return;
        event.setShieldDamage(0);
        if (!player.level().isClientSide) {
            float blocked = event.getBlockedDamage();
            if (blocked >= 3.0f) {
                item.consumeMana(shield, 1 + net.minecraft.util.Mth.floor(blocked), player);
            }
            if (item.isManaEmpty(shield)) player.stopUsingItem();
        }
    }

    public static boolean isWearingFullSet(Player player, com.koomplo.wizardry.content.item.armor.WizardArmorType type) {
        for (var stack : player.getArmorSlots()) {
            if (!(stack.getItem() instanceof com.koomplo.wizardry.content.item.armor.WizardArmorItem armor)
                    || armor.getWizardArmorType() != type) {
                return false;
            }
        }
        return true;
    }

    private ASSpellEvents() {
    }
}
