package com.windanesz.ancientspellcraft.item;

import com.koomplo.wizardry.api.content.event.SpellCastEvent;
import com.koomplo.wizardry.api.content.spell.Element;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.core.IArtifactEffect;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

/**
 * Fábricas dos efeitos de modificador de cast (1.12.2 ASEventHandler, bloco de SpellCastEvent):
 * orbs elementais (+0.30 no elemento / -0.5 fora), reduções/aumentos de custo e as joias de poder.
 */
public final class ASArtifactEffects {

    /** COST vira {@code multiplier * cost} (ex.: mana orb = 0.85). */
    public static IArtifactEffect costMultiplier(float multiplier) {
        return new IArtifactEffect() {
            @Override
            public void onSpellPreCast(SpellCastEvent.Pre event, ItemStack artifact) {
                event.getModifiers().set(SpellModifiers.COST, multiplier * event.getModifiers().get(SpellModifiers.COST));
            }
        };
    }

    /** +25% de custo e +0.25 no modificador dado (ring_blast/range/duration). */
    public static IArtifactEffect tradeoff(String modifierKey) {
        return new IArtifactEffect() {
            @Override
            public void onSpellPreCast(SpellCastEvent.Pre event, ItemStack artifact) {
                event.getModifiers().set(SpellModifiers.COST, 1.25f * event.getModifiers().get(SpellModifiers.COST));
                event.getModifiers().set(modifierKey, event.getModifiers().get(modifierKey) + 0.25f);
            }
        };
    }

    /** Orb elemental (1.12.2, orb_artefact_potency_bonus=30): +0.30 de potência no elemento, -0.5 fora. */
    public static IArtifactEffect elementOrb(Supplier<Element> element) {
        return new IArtifactEffect() {
            @Override
            public void onSpellPreCast(SpellCastEvent.Pre event, ItemStack artifact) {
                float potency = event.getModifiers().get(SpellModifiers.POTENCY);
                event.getModifiers().set(SpellModifiers.POTENCY,
                        event.getSpell().getElement() == element.get() ? potency + 0.30f : potency - 0.5f);
            }
        };
    }

    /** +potência fixa para spells de fire/ice/lightning (charm_elemental_grimoire). */
    public static IArtifactEffect grimoire(Supplier<Element> a, Supplier<Element> b, Supplier<Element> c) {
        return new IArtifactEffect() {
            @Override
            public void onSpellPreCast(SpellCastEvent.Pre event, ItemStack artifact) {
                var element = event.getSpell().getElement();
                if (element == a.get() || element == b.get() || element == c.get()) {
                    event.getModifiers().set(SpellModifiers.POTENCY,
                            event.getModifiers().get(SpellModifiers.POTENCY) + 0.1f);
                }
            }
        };
    }

    /** Joia de poder: +potência e +custo fixos (ring 0.05, amulet 0.10, orb 0.20). */
    public static IArtifactEffect powerJewel(float bonus) {
        return new IArtifactEffect() {
            @Override
            public void onSpellPreCast(SpellCastEvent.Pre event, ItemStack artifact) {
                event.getModifiers().set(SpellModifiers.POTENCY, event.getModifiers().get(SpellModifiers.POTENCY) + bonus);
                event.getModifiers().set(SpellModifiers.COST, event.getModifiers().get(SpellModifiers.COST) + bonus);
            }
        };
    }

    // ---- onda 2a: defesas de dano recebido (1.12.2 ASEventHandler.onLivingHurtEvent) ----

    /** charm_cryostasis: com a vida em ≤6 (ou prestes a ficar), 25% de castar cryostasis em si. */
    public static IArtifactEffect cryostasis() {
        return new IArtifactEffect() {
            @Override
            public void onPlayerHurt(net.minecraft.world.entity.player.Player player, net.minecraft.world.damagesource.DamageSource source,
                                     com.google.common.util.concurrent.AtomicDouble amount, java.util.concurrent.atomic.AtomicBoolean canceled, ItemStack artifact) {
                if (player.level().isClientSide) return;
                if ((player.getHealth() <= 6 || player.getHealth() - amount.get() <= 6)
                        && player.level().random.nextFloat() < 0.25f) {
                    var spell = com.koomplo.wizardry.core.platform.Services.REGISTRY_UTIL.getSpell(
                            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("ancientspellcraft", "cryostasis"));
                    if (spell != null) {
                        spell.cast(new com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext(
                                player.level(), player, net.minecraft.world.InteractionHand.MAIN_HAND, 0, new SpellModifiers()));
                    }
                }
            }
        };
    }

    /** ring_protector: vida baixa, 50% de castar de graça uma spell de minion de uma wand da hotbar
     * (desvio: primeira encontrada, sem mexer em seleção/cooldowns da wand). */
    public static IArtifactEffect protector() {
        return new IArtifactEffect() {
            @Override
            public void onPlayerHurt(net.minecraft.world.entity.player.Player player, net.minecraft.world.damagesource.DamageSource source,
                                     com.google.common.util.concurrent.AtomicDouble amount, java.util.concurrent.atomic.AtomicBoolean canceled, ItemStack artifact) {
                if (player.level().isClientSide) return;
                if (!(player.getHealth() <= 8 || player.getHealth() - amount.get() <= 6)
                        || player.level().random.nextFloat() >= 0.5f) return;
                for (int i = 0; i < 9; i++) {
                    ItemStack stack = player.getInventory().getItem(i);
                    if (!(stack.getItem() instanceof com.koomplo.wizardry.api.content.item.ICastItem)) continue;
                    for (var spell : com.koomplo.wizardry.api.content.util.CastItemDataHelper.getSpells(stack)) {
                        if (spell instanceof com.koomplo.wizardry.content.spell.abstr.MinionSpell<?>) {
                            if (spell.cast(new com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext(
                                    player.level(), player, net.minecraft.world.InteractionHand.MAIN_HAND, 0, new SpellModifiers()))) {
                                return;
                            }
                        }
                    }
                }
            }
        };
    }

    /** Buff defensivo com gatilho de vida baixa (ring_berserker: STRENGTH 15s). */
    public static IArtifactEffect lowHealthBuff(Supplier<net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect>> effect, int duration, int amplifier) {
        return new IArtifactEffect() {
            @Override
            public void onPlayerHurt(net.minecraft.world.entity.player.Player player, net.minecraft.world.damagesource.DamageSource source,
                                     com.google.common.util.concurrent.AtomicDouble amount, java.util.concurrent.atomic.AtomicBoolean canceled, ItemStack artifact) {
                if (player.level().isClientSide) return;
                if ((player.getHealth() <= 6 || player.getHealth() - amount.get() <= 6)
                        && !player.hasEffect(effect.get())) {
                    player.addEffect(new net.minecraft.world.effect.MobEffectInstance(effect.get(), duration, amplifier));
                }
            }
        };
    }

    /** amulet_rabbit: 25% ao levar dano — SPEED 10s + WEAKNESS II 10s. */
    public static IArtifactEffect rabbit() {
        return new IArtifactEffect() {
            @Override
            public void onPlayerHurt(net.minecraft.world.entity.player.Player player, net.minecraft.world.damagesource.DamageSource source,
                                     com.google.common.util.concurrent.AtomicDouble amount, java.util.concurrent.atomic.AtomicBoolean canceled, ItemStack artifact) {
                if (player.level().isClientSide || player.level().random.nextFloat() >= 0.25f) return;
                if (!player.hasEffect(net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED)) {
                    player.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED, 200));
                }
                if (!player.hasEffect(net.minecraft.world.effect.MobEffects.WEAKNESS)) {
                    player.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.WEAKNESS, 200, 1));
                }
            }
        };
    }

    /** amulet_shield: dano >1 — wizard_shield XVI por 5s, cooldown de 3 min no amuleto. */
    public static IArtifactEffect shieldAmulet() {
        return new IArtifactEffect() {
            @Override
            public void onPlayerHurt(net.minecraft.world.entity.player.Player player, net.minecraft.world.damagesource.DamageSource source,
                                     com.google.common.util.concurrent.AtomicDouble amount, java.util.concurrent.atomic.AtomicBoolean canceled, ItemStack artifact) {
                if (player.level().isClientSide || amount.get() <= 1) return;
                if (player.getCooldowns().isOnCooldown(artifact.getItem())) return;
                player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                        com.windanesz.ancientspellcraft.registry.ASEffects.WIZARD_SHIELD, 100, 15));
                player.getCooldowns().addCooldown(artifact.getItem(), 3600);
            }
        };
    }

    /** belt_soul_scorch: o atacante direto recebe soul_scorch por 3s. */
    public static IArtifactEffect soulScorch() {
        return new IArtifactEffect() {
            @Override
            public void onPlayerHurt(net.minecraft.world.entity.player.Player player, net.minecraft.world.damagesource.DamageSource source,
                                     com.google.common.util.concurrent.AtomicDouble amount, java.util.concurrent.atomic.AtomicBoolean canceled, ItemStack artifact) {
                if (player.level().isClientSide) return;
                if (source.getDirectEntity() instanceof net.minecraft.world.entity.LivingEntity attacker) {
                    attacker.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                            com.windanesz.ancientspellcraft.registry.ASEffects.SOUL_SCORCH, 60));
                }
            }
        };
    }

    // ---- onda 2b: morte e tempo (1.12.2 onLivingHurtEvent/onLivingDeathEvent) ----

    /** ring_undeath: dano letal — recebe a maldição de morto-vivo permanente, cura 50% e sobrevive (cd 5 min). */
    public static IArtifactEffect undeath() {
        return new IArtifactEffect() {
            @Override
            public void onPlayerHurt(net.minecraft.world.entity.player.Player player, net.minecraft.world.damagesource.DamageSource source,
                                     com.google.common.util.concurrent.AtomicDouble amount, java.util.concurrent.atomic.AtomicBoolean canceled, ItemStack artifact) {
                if (player.level().isClientSide) return;
                var curse = net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(
                        com.koomplo.wizardry.setup.registries.EBMobEffects.CURSE_OF_UNDEATH.get());
                if (player.hasEffect(curse)) return;
                if (player.getHealth() - amount.get() > 0 || player.getCooldowns().isOnCooldown(artifact.getItem())) return;
                player.addEffect(new net.minecraft.world.effect.MobEffectInstance(curse, Integer.MAX_VALUE, 0));
                player.heal(player.getMaxHealth() * 0.5f);
                player.displayClientMessage(net.minecraft.network.chat.Component.translatable(
                        "item.ancientspellcraft.ring_undeath.resurrect"), true);
                player.getCooldowns().addCooldown(artifact.getItem(), 6000);
                amount.set(0);
            }
        };
    }

    /** amulet_time_knot: dano letal com o nó do tempo ativo — volta ao ponto gravado em vez de morrer (cd 5 min). */
    public static IArtifactEffect timeKnot() {
        return new IArtifactEffect() {
            @Override
            public void onPlayerHurt(net.minecraft.world.entity.player.Player player, net.minecraft.world.damagesource.DamageSource source,
                                     com.google.common.util.concurrent.AtomicDouble amount, java.util.concurrent.atomic.AtomicBoolean canceled, ItemStack artifact) {
                if (!(player instanceof net.minecraft.server.level.ServerPlayer serverPlayer)) return;
                if (!player.hasEffect(com.windanesz.ancientspellcraft.registry.ASEffects.TIME_KNOT)
                        || player.getHealth() - amount.get() > 0
                        || player.getCooldowns().isOnCooldown(artifact.getItem())) return;
                serverPlayer.removeEffect(com.windanesz.ancientspellcraft.registry.ASEffects.TIME_KNOT);
                if (com.windanesz.ancientspellcraft.handler.ASPotionEvents.loopPlayer(serverPlayer)) {
                    canceled.set(true);
                    serverPlayer.clearFire();
                    serverPlayer.getCooldowns().addCooldown(artifact.getItem(), 6000);
                }
            }
        };
    }

    /** amulet_time_slow: vida baixa — slow_time do Redux por 6s (cd 8 min). */
    public static IArtifactEffect timeSlow() {
        return new IArtifactEffect() {
            @Override
            public void onPlayerHurt(net.minecraft.world.entity.player.Player player, net.minecraft.world.damagesource.DamageSource source,
                                     com.google.common.util.concurrent.AtomicDouble amount, java.util.concurrent.atomic.AtomicBoolean canceled, ItemStack artifact) {
                if (player.level().isClientSide || player.getCooldowns().isOnCooldown(artifact.getItem())) return;
                if (player.getHealth() <= 6 || player.getHealth() - amount.get() <= 6) {
                    player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                            net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(
                                    com.koomplo.wizardry.setup.registries.EBMobEffects.SLOW_TIME.get()), 120));
                    player.getCooldowns().addCooldown(artifact.getItem(), 9600);
                }
            }
        };
    }

    /** charm_reanimation: matar esqueleto/zumbi — 15% de erguer um esqueleto minion por 30s (arco se o morto usava). */
    public static IArtifactEffect reanimation() {
        return new IArtifactEffect() {
            @Override
            public void onKillEntity(net.minecraft.world.entity.player.Player player, net.minecraft.world.entity.LivingEntity deadEntity,
                                     net.minecraft.world.damagesource.DamageSource source, ItemStack artifact) {
                if (!(player.level() instanceof net.minecraft.server.level.ServerLevel serverLevel)) return;
                if (!deadEntity.getType().is(net.minecraft.tags.EntityTypeTags.UNDEAD)) return;
                if (!(deadEntity instanceof net.minecraft.world.entity.monster.AbstractSkeleton
                        || deadEntity instanceof net.minecraft.world.entity.monster.Zombie)) return;
                if (serverLevel.random.nextFloat() >= 0.15f) return;

                var skeleton = new net.minecraft.world.entity.monster.Skeleton(net.minecraft.world.entity.EntityType.SKELETON, serverLevel);
                skeleton.setPos(deadEntity.getX(), deadEntity.getY(), deadEntity.getZ());
                boolean archer = deadEntity.getMainHandItem().getItem() instanceof net.minecraft.world.item.BowItem
                        || deadEntity.getOffhandItem().getItem() instanceof net.minecraft.world.item.BowItem;
                skeleton.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,
                        new ItemStack(archer ? net.minecraft.world.item.Items.BOW : net.minecraft.world.item.Items.WOODEN_SWORD));
                skeleton.setDropChance(net.minecraft.world.entity.EquipmentSlot.MAINHAND, 0.0F);
                var data = com.koomplo.wizardry.core.platform.Services.OBJECT_DATA.getMinionData(skeleton);
                data.setSummoned(true);
                data.setOwnerUUID(player.getUUID());
                data.setLifetime(600);
                data.updateGoals();
                serverLevel.addFreshEntity(skeleton);
            }
        };
    }

    /** charm_plunderers_mark: matar um evil wizard dropa um astral diamond shard. */
    public static IArtifactEffect plunderersMark() {
        return new IArtifactEffect() {
            @Override
            public void onKillEntity(net.minecraft.world.entity.player.Player player, net.minecraft.world.entity.LivingEntity deadEntity,
                                     net.minecraft.world.damagesource.DamageSource source, ItemStack artifact) {
                if (deadEntity.level().isClientSide) return;
                if (deadEntity instanceof com.koomplo.wizardry.content.entity.living.EvilWizard) {
                    deadEntity.spawnAtLocation(new ItemStack(
                            com.windanesz.ancientspellcraft.registry.ASItems.ASTRAL_DIAMOND_SHARD.get()));
                }
            }
        };
    }

    // ---- onda 2c: tick ----

    /** head_mask_of_silence: a cada 1s, conjuradores num raio de 8 ganham exaustão mágica I por 2s. */
    public static IArtifactEffect maskOfSilence() {
        return new IArtifactEffect() {
            @Override
            public void onTick(net.minecraft.world.entity.player.Player player, net.minecraft.world.level.Level level, ItemStack artifact) {
                if (level.isClientSide || player.tickCount % 20 != 0) return;
                for (var nearby : com.koomplo.wizardry.api.content.util.EntityUtil.getLivingWithinRadius(
                        8, player.getX(), player.getY(), player.getZ(), level)) {
                    if (nearby == player) continue;
                    if (nearby instanceof net.minecraft.world.entity.player.Player
                            || nearby instanceof com.koomplo.wizardry.api.content.entity.living.ISpellCaster) {
                        nearby.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                                com.windanesz.ancientspellcraft.registry.ASEffects.MAGICAL_EXHAUSTION, 40, 0));
                    }
                }
            }
        };
    }

    /** ring_prismarine: pegando fogo — apaga e dá fire resistance 6s (cd 60s; desvio: sem castar
     * extinguish, spell não portada). */
    public static IArtifactEffect prismarine() {
        return new IArtifactEffect() {
            @Override
            public void onTick(net.minecraft.world.entity.player.Player player, net.minecraft.world.level.Level level, ItemStack artifact) {
                if (level.isClientSide || !player.isOnFire()) return;
                if (player.getCooldowns().isOnCooldown(artifact.getItem())) return;
                player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                        net.minecraft.world.effect.MobEffects.FIRE_RESISTANCE, 120));
                player.clearFire();
                player.getCooldowns().addCooldown(artifact.getItem(), 1200);
            }
        };
    }

    // ---- onda 3a: buffs de tick ----

    /** amulet_celerity: +10% de velocidade enquanto equipado (modifier transiente; limpeza no ASSpellEvents). */
    public static final net.minecraft.resources.ResourceLocation CELERITY_MODIFIER =
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("ancientspellcraft", "amulet_celerity");

    public static IArtifactEffect celerity() {
        return new IArtifactEffect() {
            @Override
            public void onTick(net.minecraft.world.entity.player.Player player, net.minecraft.world.level.Level level, ItemStack artifact) {
                if (level.isClientSide) return;
                var attribute = player.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED);
                if (attribute != null && !attribute.hasModifier(CELERITY_MODIFIER)) {
                    attribute.addTransientModifier(new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                            CELERITY_MODIFIER, 0.1D, net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                }
            }
        };
    }

    /** amulet_oakflesh: com oakflesh ativo em floresta, regeneração contínua (1.12.2). */
    public static IArtifactEffect oakflesh() {
        return new IArtifactEffect() {
            @Override
            public void onTick(net.minecraft.world.entity.player.Player player, net.minecraft.world.level.Level level, ItemStack artifact) {
                if (level.isClientSide || player.tickCount % 10 != 0) return;
                if (!player.hasEffect(net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(
                        com.koomplo.wizardry.setup.registries.EBMobEffects.OAKFLESH.get()))) return;
                var biome = level.getBiome(player.blockPosition()).unwrapKey().orElse(null);
                if (biome == null) return;
                String path = biome.location().getPath();
                if (path.contains("forest") || path.contains("wood")) {
                    player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                            net.minecraft.world.effect.MobEffects.REGENERATION, 40, 0));
                }
            }
        };
    }

    /** amulet_holy_aura: a cada 2s, mortos-vivos num raio de 6 pegam fogo por 5s. */
    public static IArtifactEffect holyAura() {
        return new IArtifactEffect() {
            @Override
            public void onTick(net.minecraft.world.entity.player.Player player, net.minecraft.world.level.Level level, ItemStack artifact) {
                if (level.isClientSide || level.getGameTime() % 40 != 0) return;
                for (var entity : com.koomplo.wizardry.api.content.util.EntityUtil.getLivingWithinRadius(
                        6, player.getX(), player.getY(), player.getZ(), level)) {
                    if (entity != player && entity.getType().is(net.minecraft.tags.EntityTypeTags.UNDEAD)) {
                        entity.igniteForSeconds(5);
                    }
                }
            }
        };
    }

    /** amulet_healing_mushroom: ferido, a cada 6s há 50% de brotar cogumelos de cura por perto (cd 45s). */
    public static IArtifactEffect healingMushroom() {
        return new IArtifactEffect() {
            @Override
            public void onTick(net.minecraft.world.entity.player.Player player, net.minecraft.world.level.Level level, ItemStack artifact) {
                if (level.isClientSide || player.tickCount % 120 != 0 || !level.random.nextBoolean()) return;
                if (player.getHealth() >= player.getMaxHealth()) return;
                if (player.getCooldowns().isOnCooldown(artifact.getItem())) return;
                for (int i = 0; i < level.random.nextInt(3); i++) {
                    var pos = com.koomplo.wizardry.api.content.util.BlockUtil.findNearbyFloorSpace(
                            level, player.blockPosition(), 7, 7, false, player);
                    if (pos != null && com.windanesz.ancientspellcraft.block.MagicMushroomBlock.tryPlace(
                            level, pos, player, com.windanesz.ancientspellcraft.registry.ASBlocks.MUSHROOMS.get("mushroom_healing").get(), 700, 1.0f)) {
                        player.getCooldowns().addCooldown(artifact.getItem(), 900);
                    }
                }
            }
        };
    }

    /** amulet_cursed_pendant: à meia-noite, 50% de invocar um evil class wizard vingativo (cd 15 min). */
    public static IArtifactEffect cursedPendant() {
        return new IArtifactEffect() {
            @Override
            public void onTick(net.minecraft.world.entity.player.Player player, net.minecraft.world.level.Level level, ItemStack artifact) {
                if (!(level instanceof net.minecraft.server.level.ServerLevel serverLevel)) return;
                if (level.getDayTime() % 24000 != 18000 || level.random.nextFloat() > 0.5f) return;
                if (player.getCooldowns().isOnCooldown(artifact.getItem())) return;
                var pos = com.koomplo.wizardry.api.content.util.BlockUtil.findNearbyFloorSpace(
                        level, player.blockPosition(), 8, 8, false, player);
                if (pos == null) return;
                var wizard = new com.windanesz.ancientspellcraft.entity.living.EvilClassWizard(
                        com.windanesz.ancientspellcraft.registry.ASEntities.EVIL_CLASS_WIZARD.get(), level);
                wizard.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                wizard.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(pos),
                        net.minecraft.world.entity.MobSpawnType.EVENT, null);
                wizard.setLastHurtByMob(player);
                serverLevel.addFreshEntity(wizard);
                player.displayClientMessage(net.minecraft.network.chat.Component.translatable(
                        "item.ancientspellcraft.amulet_cursed_pendant.summoned_wizard"), false);
                player.getCooldowns().addCooldown(artifact.getItem(), 18000);
            }
        };
    }

    // ---- onda 3b: glyphs do battlemage ----

    private static boolean holdingBattlemageSword(net.minecraft.world.entity.player.Player player) {
        return player.getMainHandItem().getItem() instanceof com.windanesz.ancientspellcraft.item.BattlemageSwordItem;
    }

    /** charm_glyph_leeching: 30% no golpe da lâmina de ROUBAR um buff do alvo (dur ≤10min, amp <III). */
    public static IArtifactEffect glyphLeeching() {
        return new IArtifactEffect() {
            @Override
            public void onHurtEntity(net.minecraft.world.entity.player.Player player, net.minecraft.world.entity.LivingEntity target,
                                     net.minecraft.world.damagesource.DamageSource source, com.google.common.util.concurrent.AtomicDouble amount,
                                     java.util.concurrent.atomic.AtomicBoolean canceled, ItemStack artifact) {
                if (player.level().isClientSide || !holdingBattlemageSword(player)) return;
                if (player.level().random.nextDouble() >= 0.3) return;
                var candidates = target.getActiveEffects().stream()
                        .filter(e -> e.getEffect().value().isBeneficial())
                        .filter(e -> e.getDuration() <= 12000 && e.getAmplifier() < 3)
                        .toList();
                if (candidates.isEmpty()) return;
                var stolen = candidates.get(player.level().random.nextInt(candidates.size()));
                player.addEffect(new net.minecraft.world.effect.MobEffectInstance(stolen));
                target.removeEffect(stolen.getEffect());
            }
        };
    }

    /** charm_glyph_antigravity: lâmina leve — jump boost + speed segurando a espada, mas dano x0.7. */
    public static IArtifactEffect glyphAntigravity() {
        return new IArtifactEffect() {
            @Override
            public void onTick(net.minecraft.world.entity.player.Player player, net.minecraft.world.level.Level level, ItemStack artifact) {
                if (level.isClientSide || player.tickCount % 15 != 0 || !holdingBattlemageSword(player)) return;
                player.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.JUMP, 40, 0));
                player.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED, 40, 0));
            }

            @Override
            public void onHurtEntity(net.minecraft.world.entity.player.Player player, net.minecraft.world.entity.LivingEntity target,
                                     net.minecraft.world.damagesource.DamageSource source, com.google.common.util.concurrent.AtomicDouble amount,
                                     java.util.concurrent.atomic.AtomicBoolean canceled, ItemStack artifact) {
                if (holdingBattlemageSword(player)) amount.set(amount.get() * 0.7);
            }
        };
    }

    /** charm_glyph_might: dano da lâmina x1.2. */
    public static IArtifactEffect glyphMight() {
        return new IArtifactEffect() {
            @Override
            public void onHurtEntity(net.minecraft.world.entity.player.Player player, net.minecraft.world.entity.LivingEntity target,
                                     net.minecraft.world.damagesource.DamageSource source, com.google.common.util.concurrent.AtomicDouble amount,
                                     java.util.concurrent.atomic.AtomicBoolean canceled, ItemStack artifact) {
                if (holdingBattlemageSword(player)) amount.set(amount.get() * 1.2);
            }
        };
    }

    // ---- onda 3c: auras do battlemage (1.12.2 ItemGlyphAuraArtefact: tick %10, raio 10, exige set completo) ----

    /** Aplica se o alvo não tem o efeito (ou tem amplifier menor); damaging dá 0.01 de dano para puxar agro. */
    public static void applyAuraEffect(net.minecraft.world.entity.player.Player player, net.minecraft.world.entity.LivingEntity target,
                                       net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> effect, int duration, int amplifier, boolean damaging) {
        var current = target.getEffect(effect);
        if (current != null && current.getAmplifier() >= amplifier) return;
        if (damaging && target.getLastHurtByMob() != player) {
            com.koomplo.wizardry.api.content.util.EntityUtil.attackEntityWithoutKnockback(target,
                    com.koomplo.wizardry.api.content.util.MagicDamageSource.causeDirectMagicDamage(player,
                            com.koomplo.wizardry.setup.registries.EBDamageSources.MAGIC), 0.01f);
        }
        target.addEffect(new net.minecraft.world.effect.MobEffectInstance(effect, duration, amplifier));
    }

    public interface AuraTick {
        void apply(net.minecraft.world.entity.player.Player player, java.util.List<net.minecraft.world.entity.LivingEntity> nearby);
    }

    /** Base das auras: tick a cada 0.5s com o set completo de battlemage; nearby = raio 10 (sem o wearer). */
    public static IArtifactEffect battlemageAura(AuraTick aura) {
        return new IArtifactEffect() {
            @Override
            public void onTick(net.minecraft.world.entity.player.Player player, net.minecraft.world.level.Level level, ItemStack artifact) {
                if (level.isClientSide || player.tickCount % 10 != 0) return;
                if (!com.windanesz.ancientspellcraft.handler.ASSpellEvents.isWearingFullSet(player,
                        com.koomplo.wizardry.content.item.armor.WizardArmorType.BATTLEMAGE)) return;
                var nearby = com.koomplo.wizardry.api.content.util.EntityUtil.getLivingWithinRadius(
                                10, player.getX(), player.getY(), player.getZ(), level).stream()
                        .filter(e -> e != player).toList();
                aura.apply(player, nearby);
            }
        };
    }

    public static boolean isAuraAlly(net.minecraft.world.entity.player.Player player, net.minecraft.world.entity.LivingEntity entity) {
        return com.koomplo.wizardry.core.AllyDesignation.isAllied(player, entity);
    }

    public static boolean isAuraEnemy(net.minecraft.world.entity.player.Player player, net.minecraft.world.entity.LivingEntity entity) {
        return !(entity instanceof net.minecraft.world.entity.animal.Animal)
                && !com.koomplo.wizardry.core.AllyDesignation.isAllied(player, entity);
    }

    // ---- onda 3d: tickables especiais ----

    /** charm_specterlight_torch: a cada 1s revela invisíveis num raio de 10 (remove invisibility/muffle). */
    public static IArtifactEffect specterlightTorch() {
        return new IArtifactEffect() {
            @Override
            public void onTick(net.minecraft.world.entity.player.Player player, net.minecraft.world.level.Level level, ItemStack artifact) {
                if (level.isClientSide || player.tickCount % 20 != 0) return;
                for (var entity : com.koomplo.wizardry.api.content.util.EntityUtil.getLivingWithinRadius(
                        10, player.getX(), player.getY(), player.getZ(), level)) {
                    if (entity == player || !entity.isInvisible()) continue;
                    entity.removeEffect(net.minecraft.world.effect.MobEffects.INVISIBILITY);
                    entity.removeEffect(net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(
                            com.koomplo.wizardry.setup.registries.EBMobEffects.MUFFLE.get()));
                }
            }
        };
    }

    /** belt_enchanted_whetstone: repara 1 de durabilidade da espada/machado na mão a cada 5s. */
    public static IArtifactEffect enchantedWhetstone() {
        return new IArtifactEffect() {
            @Override
            public void onTick(net.minecraft.world.entity.player.Player player, net.minecraft.world.level.Level level, ItemStack artifact) {
                if (level.isClientSide || player.tickCount % 100 != 0) return;
                for (var hand : net.minecraft.world.InteractionHand.values()) {
                    ItemStack tool = player.getItemInHand(hand);
                    if ((tool.getItem() instanceof net.minecraft.world.item.SwordItem
                            || tool.getItem() instanceof net.minecraft.world.item.AxeItem) && tool.isDamaged()) {
                        tool.setDamageValue(tool.getDamageValue() - 1);
                        break;
                    }
                }
            }
        };
    }

    /** amulet_searing_skin: com fireskin do Redux ativo, regeneração contínua. */
    public static IArtifactEffect searingSkin() {
        return new IArtifactEffect() {
            @Override
            public void onTick(net.minecraft.world.entity.player.Player player, net.minecraft.world.level.Level level, ItemStack artifact) {
                if (level.isClientSide || player.tickCount % 10 != 0) return;
                if (player.hasEffect(net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(
                        com.koomplo.wizardry.setup.registries.EBMobEffects.FIRESKIN.get()))) {
                    player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                            net.minecraft.world.effect.MobEffects.REGENERATION, 40));
                }
            }
        };
    }

    /** head_ardor_crown: seus minions num raio de 8 ganham speed II + strength (pulso de 0.5s). */
    public static IArtifactEffect ardorCrown() {
        return new IArtifactEffect() {
            @Override
            public void onTick(net.minecraft.world.entity.player.Player player, net.minecraft.world.level.Level level, ItemStack artifact) {
                if (level.isClientSide || player.tickCount % 10 != 0) return;
                for (var entity : com.koomplo.wizardry.api.content.util.EntityUtil.getLivingWithinRadius(
                        8, player.getX(), player.getY(), player.getZ(), level)) {
                    if (!(entity instanceof net.minecraft.world.entity.Mob mob) || entity == player) continue;
                    var data = mob.getData(com.koomplo.wizardry.setup.registries.EBAttachments.MINION_DATA);
                    if (!data.isSummoned() || data.getOwner() != player) continue;
                    entity.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                            net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED, 40, 1, false, false));
                    entity.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                            net.minecraft.world.effect.MobEffects.DAMAGE_BOOST, 40, 0, false, false));
                }
            }
        };
    }

    /** head_minion_circle: minions ociosos num raio de 20 orbitam você num círculo de raio 3
     * (desvio: sem remover a IA de perambular — só navegação). */
    public static IArtifactEffect minionCircle() {
        return new IArtifactEffect() {
            @Override
            public void onTick(net.minecraft.world.entity.player.Player player, net.minecraft.world.level.Level level, ItemStack artifact) {
                if (level.isClientSide || player.tickCount % 5 != 0) return;
                var minions = com.koomplo.wizardry.api.content.util.EntityUtil.getLivingWithinRadius(
                                20, player.getX(), player.getY(), player.getZ(), level).stream()
                        .filter(e -> e instanceof net.minecraft.world.entity.Mob mob
                                && mob.getData(com.koomplo.wizardry.setup.registries.EBAttachments.MINION_DATA).isSummoned()
                                && mob.getData(com.koomplo.wizardry.setup.registries.EBAttachments.MINION_DATA).getOwner() == player
                                && mob.getTarget() == null)
                        .map(e -> (net.minecraft.world.entity.Mob) e)
                        .sorted(java.util.Comparator.comparingInt(net.minecraft.world.entity.Entity::getId))
                        .toList();
                if (minions.isEmpty()) return;
                double angleIncrement = 2 * Math.PI / minions.size();
                for (int i = 0; i < minions.size(); i++) {
                    var minion = minions.get(i);
                    double targetX = player.getX() + 3.0 * Math.cos(i * angleIncrement);
                    double targetZ = player.getZ() + 3.0 * Math.sin(i * angleIncrement);
                    if (minion.position().distanceTo(new net.minecraft.world.phys.Vec3(targetX, minion.getY(), targetZ)) > 0.5) {
                        minion.getNavigation().moveTo(targetX, minion.getY(), targetZ, 1.2);
                    }
                }
            }
        };
    }

    /** charm_guardian_blade: vida ≤33% — invoca uma lâmina espectral animada por 15s (dano x6, cd 30s). */
    public static IArtifactEffect guardianBlade() {
        return new IArtifactEffect() {
            @Override
            public void onTick(net.minecraft.world.entity.player.Player player, net.minecraft.world.level.Level level, ItemStack artifact) {
                if (!(level instanceof net.minecraft.server.level.ServerLevel serverLevel)) return;
                if (level.getGameTime() % 20 != 0 || player.getHealth() > player.getMaxHealth() * 0.33f) return;
                if (player.getCooldowns().isOnCooldown(artifact.getItem())) return;
                var pos = com.koomplo.wizardry.api.content.util.BlockUtil.findNearbyFloorSpace(
                        level, player.blockPosition(), 4, 8, false, player);
                if (pos == null) return;
                var minion = new com.windanesz.ancientspellcraft.entity.living.AnimatedItemEntity(
                        com.windanesz.ancientspellcraft.registry.ASEntities.ANIMATED_ITEM.get(), level);
                minion.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                minion.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,
                        new ItemStack(com.windanesz.ancientspellcraft.registry.ASItems.CHARM_GUARDIAN_BLADE.get()));
                minion.setDropChance(net.minecraft.world.entity.EquipmentSlot.MAINHAND, 0.0F);
                var attack = minion.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE);
                if (attack != null) {
                    attack.addTransientModifier(new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("ancientspellcraft", "guardian_blade"),
                            6, net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
                }
                var data = com.koomplo.wizardry.core.platform.Services.OBJECT_DATA.getMinionData(minion);
                data.setSummoned(true);
                data.setOwnerUUID(player.getUUID());
                data.setLifetime(300);
                data.updateGoals();
                serverLevel.addFreshEntity(minion);
                player.getCooldowns().addCooldown(artifact.getItem(), 600);
            }
        };
    }

    /** amulet_elemental_aura: com static_aura/fireskin/ice_shroud ativo, 1/3 a cada 1s de descarregar
     * o elemento em inimigos num raio de 5 (dano SHOCK / fogo / frost — valores das spells do Redux). */
    public static IArtifactEffect elementalAura() {
        return new IArtifactEffect() {
            @Override
            public void onTick(net.minecraft.world.entity.player.Player player, net.minecraft.world.level.Level level, ItemStack artifact) {
                if (level.isClientSide || player.tickCount % 20 != 0 || level.random.nextInt(3) != 0) return;
                var registryUtil = com.koomplo.wizardry.core.platform.Services.REGISTRY_UTIL;
                int mode = player.hasEffect(net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(
                        com.koomplo.wizardry.setup.registries.EBMobEffects.STATIC_AURA.get())) ? 1
                        : player.hasEffect(net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(
                        com.koomplo.wizardry.setup.registries.EBMobEffects.FIRESKIN.get())) ? 2
                        : player.hasEffect(net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(
                        com.koomplo.wizardry.setup.registries.EBMobEffects.ICE_SHROUD.get())) ? 3 : 0;
                if (mode == 0) return;
                for (var target : com.koomplo.wizardry.api.content.util.EntityUtil.getLivingWithinRadius(
                        5, player.getX(), player.getY(), player.getZ(), level)) {
                    if (target == player || com.koomplo.wizardry.core.AllyDesignation.isAllied(player, target)) continue;
                    if (mode == 1) {
                        var spell = registryUtil.getSpell(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("ebwizardry", "static_aura"));
                        float damage = spell != null ? spell.property(com.koomplo.wizardry.content.spell.DefaultProperties.DAMAGE) : 4;
                        com.koomplo.wizardry.api.content.util.EntityUtil.attackEntityWithoutKnockback(target,
                                com.koomplo.wizardry.api.content.util.MagicDamageSource.causeDirectMagicDamage(player,
                                        com.koomplo.wizardry.setup.registries.EBDamageSources.SHOCK), damage);
                    } else if (mode == 2) {
                        // 1.12.2: burn_duration do fire_breath x5 (no Redux a propriedade equivalente é effect_duration)
                        var spell = registryUtil.getSpell(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("ebwizardry", "fire_breath"));
                        int burn = spell != null ? spell.property(com.koomplo.wizardry.content.spell.DefaultProperties.EFFECT_DURATION) : 10;
                        target.igniteForSeconds(burn * 5);
                    } else {
                        var spell = registryUtil.getSpell(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("ebwizardry", "ice_shroud"));
                        int duration = spell != null ? spell.property(com.koomplo.wizardry.content.spell.DefaultProperties.EFFECT_DURATION) : 200;
                        int strength = spell != null ? spell.property(com.koomplo.wizardry.content.spell.DefaultProperties.EFFECT_STRENGTH) : 0;
                        target.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                                net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(
                                        com.koomplo.wizardry.setup.registries.EBMobEffects.FROST.get()), duration, strength));
                    }
                }
            }
        };
    }

    private ASArtifactEffects() {}
}
