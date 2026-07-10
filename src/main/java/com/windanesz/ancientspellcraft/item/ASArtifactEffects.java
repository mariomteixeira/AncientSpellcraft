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

    private ASArtifactEffects() {}
}
