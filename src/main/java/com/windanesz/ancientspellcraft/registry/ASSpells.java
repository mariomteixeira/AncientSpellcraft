package com.windanesz.ancientspellcraft.registry;

import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.core.registry.EBRegistries;
import com.windanesz.ancientspellcraft.registry.ASEntities;
import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.spell.ASBuffSpell;
import com.windanesz.ancientspellcraft.spell.AreaBuffSpell;
import com.windanesz.ancientspellcraft.spell.LightBuffSpell;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ASSpells {

    public static net.neoforged.neoforge.registries.DeferredHolder<Spell, Spell> FLINT_SHARD;
    public static net.neoforged.neoforge.registries.DeferredHolder<Spell, Spell> BURROW;

    public static final DeferredRegister<Spell> SPELLS = DeferredRegister.create(EBRegistries.SPELL, AncientSpellcraft.MODID);

    static {
        // Metamagic (AS-9a): buffs que alteram os modifiers do próximo cast (read-hooks em ASMetamagicEvents)
        SPELLS.register("arcane_augmentation", () -> new com.windanesz.ancientspellcraft.spell.MetamagicBuffSpell(() -> ASEffects.ARCANE_AUGMENTATION));
        SPELLS.register("intensifying_focus", () -> new com.windanesz.ancientspellcraft.spell.MetamagicBuffSpell(() -> ASEffects.INTENSIFYING_FOCUS));
        SPELLS.register("continuity_charm", () -> new com.windanesz.ancientspellcraft.spell.MetamagicBuffSpell(() -> ASEffects.CONTINUITY_CHARM));
        SPELLS.register("metamagic_projectile", com.windanesz.ancientspellcraft.spell.MetamagicProjectileSpell::new);

        // Cores 1.12.2 (r,g,b) normalizadas /255 onde eram int
        SPELLS.register("aquatic_agility", () -> new ASBuffSpell(0f, 0.4f, 0.8f, () -> ASEffects.AQUATIC_AGILITY));
        SPELLS.register("aspect_hunter", () -> new ASBuffSpell(22 / 255f, 102 / 255f, 48 / 255f, () -> ASEffects.FORTIFIED_ARCHERY));
        SPELLS.register("curse_ward", () -> new ASBuffSpell(252 / 255f, 253 / 255f, 228 / 255f, () -> ASEffects.CURSE_WARD));
        SPELLS.register("feather_fall", () -> new ASBuffSpell(230 / 255f, 230 / 255f, 255 / 255f, () -> ASEffects.FEATHER_FALL));
        SPELLS.register("lava_vision", () -> new ASBuffSpell(245 / 255f, 70 / 255f, 1 / 255f, () -> ASEffects.LAVA_VISION));
        SPELLS.register("magma_strider", () -> new ASBuffSpell(216 / 255f, 26 / 255f, 11 / 255f, () -> ASEffects.MAGMA_STRIDER));
        SPELLS.register("might_and_magic", () -> new ASBuffSpell(219 / 255f, 0f, 18 / 255f,
                () -> ASEffects.MAGICAL_EXHAUSTION, holder(MobEffects.HEALTH_BOOST), holder(MobEffects.DAMAGE_BOOST)));
        SPELLS.register("magelight", () -> new LightBuffSpell(216 / 255f, 26 / 255f, 11 / 255f,
                () -> ASEffects.CANDLELIGHT, () -> ASBlocks.MAGELIGHT.get(), () -> ASEffects.MAGELIGHT));
        SPELLS.register("candlelight", () -> new LightBuffSpell(216 / 255f, 26 / 255f, 11 / 255f,
                () -> ASEffects.MAGELIGHT, () -> ASBlocks.CANDLELIGHT.get(), () -> ASEffects.CANDLELIGHT));
        SPELLS.register("celerity", () -> new AreaBuffSpell(157 / 255f, 168 / 255f, 249 / 255f, true, holder(MobEffects.MOVEMENT_SPEED)));
        SPELLS.register("water_walking", () -> new AreaBuffSpell(58 / 255f, 147 / 255f, 254 / 255f, false, () -> ASEffects.WATER_WALKING));
        SPELLS.register("cryostasis", com.windanesz.ancientspellcraft.spell.Cryostasis::new);
        SPELLS.register("time_knot", com.windanesz.ancientspellcraft.spell.TimeKnot::new);
        SPELLS.register("projectile_ward", () -> new com.windanesz.ancientspellcraft.spell.WardBuffSpell(() -> ASEffects.PROJECTILE_WARD));
        SPELLS.register("bulwark", () -> new com.windanesz.ancientspellcraft.spell.WardBuffSpell(() -> ASEffects.BULWARK));
        SPELLS.register("arcane_aegis", () -> new com.windanesz.ancientspellcraft.spell.WardBuffSpell(() -> ASEffects.ARCANE_AEGIS));
        SPELLS.register("dispel_item_curse", com.windanesz.ancientspellcraft.spell.DispelItemCurse::new);
        SPELLS.register("regrowth", com.windanesz.ancientspellcraft.spell.Regrowth::new);
        SPELLS.register("shrink_self", () -> new com.windanesz.ancientspellcraft.spell.ResizeSelfSpell(147 / 255f, 112 / 255f, 219 / 255f, () -> ASEffects.SHRINKAGE));
        SPELLS.register("grow_self", () -> new com.windanesz.ancientspellcraft.spell.ResizeSelfSpell(139 / 255f, 69 / 255f, 19 / 255f, () -> ASEffects.GROWTH));

        SPELLS.register("curse_of_ender", () -> new com.windanesz.ancientspellcraft.spell.EffectRaySpell(true, true,
                java.util.List.of(() -> ASEffects.CURSE_OF_ENDER),
                java.util.List.of(new com.windanesz.ancientspellcraft.spell.EffectRaySpell.ParticleSpec(com.koomplo.wizardry.setup.registries.client.EBParticles.DARK_MAGIC, 0x571e65, 0, 0), new com.windanesz.ancientspellcraft.spell.EffectRaySpell.ParticleSpec(com.koomplo.wizardry.setup.registries.client.EBParticles.DARK_MAGIC, 0x251609, 0, 0), new com.windanesz.ancientspellcraft.spell.EffectRaySpell.ParticleSpec(com.koomplo.wizardry.setup.registries.client.EBParticles.SPARKLE, 0x0b4b40, 12, 8))));
        SPELLS.register("curse_of_gills", () -> new com.windanesz.ancientspellcraft.spell.EffectRaySpell(true, true,
                java.util.List.of(() -> ASEffects.CURSE_OF_GILLS),
                java.util.List.of(new com.windanesz.ancientspellcraft.spell.EffectRaySpell.ParticleSpec(com.koomplo.wizardry.setup.registries.client.EBParticles.DARK_MAGIC, 0x4287f5, 0, 0), new com.windanesz.ancientspellcraft.spell.EffectRaySpell.ParticleSpec(com.koomplo.wizardry.setup.registries.client.EBParticles.DARK_MAGIC, 0x0748b0, 0, 0), new com.windanesz.ancientspellcraft.spell.EffectRaySpell.ParticleSpec(com.koomplo.wizardry.setup.registries.client.EBParticles.SPARKLE, 0x01183d, 12, 8))));
        SPELLS.register("curse_of_umbra", () -> new com.windanesz.ancientspellcraft.spell.EffectRaySpell(true, true,
                java.util.List.of(() -> ASEffects.CURSE_OF_UMBRA),
                java.util.List.of(new com.windanesz.ancientspellcraft.spell.EffectRaySpell.ParticleSpec(com.koomplo.wizardry.setup.registries.client.EBParticles.DARK_MAGIC, 0x000000, 0, 0), new com.windanesz.ancientspellcraft.spell.EffectRaySpell.ParticleSpec(com.koomplo.wizardry.setup.registries.client.EBParticles.DARK_MAGIC, 0x000000, 0, 0), new com.windanesz.ancientspellcraft.spell.EffectRaySpell.ParticleSpec(com.koomplo.wizardry.setup.registries.client.EBParticles.SPARKLE, 0x000000, 12, 8))));
        SPELLS.register("curse_of_insomnia", () -> new com.windanesz.ancientspellcraft.spell.EffectRaySpell(true, false,
                java.util.List.of(() -> ASEffects.CURSE_OF_INSOMNIA),
                java.util.List.of(new com.windanesz.ancientspellcraft.spell.EffectRaySpell.ParticleSpec(com.koomplo.wizardry.setup.registries.client.EBParticles.DUST, -1, 0, 0), new com.windanesz.ancientspellcraft.spell.EffectRaySpell.ParticleSpec(com.koomplo.wizardry.setup.registries.client.EBParticles.DARK_MAGIC, -1, 0, 0))));
        SPELLS.register("curse_of_eternal_combustion", () -> new com.windanesz.ancientspellcraft.spell.EffectRaySpell(true, true,
                java.util.List.of(() -> ASEffects.CURSE_OF_ETERNAL_COMBUSTION),
                java.util.List.of(new com.windanesz.ancientspellcraft.spell.EffectRaySpell.ParticleSpec(com.koomplo.wizardry.setup.registries.client.EBParticles.MAGIC_FIRE, -1, 0, 0), new com.windanesz.ancientspellcraft.spell.EffectRaySpell.ParticleSpec(com.koomplo.wizardry.setup.registries.client.EBParticles.DUST, -1, 0, 0))));
        SPELLS.register("curse_of_eternal_frost", () -> new com.windanesz.ancientspellcraft.spell.EffectRaySpell(true, true,
                java.util.List.of(() -> ASEffects.CURSE_OF_ETERNAL_FROST),
                java.util.List.of(new com.windanesz.ancientspellcraft.spell.EffectRaySpell.ParticleSpec(com.koomplo.wizardry.setup.registries.client.EBParticles.SPARKLE, -1, 12, 8), new com.windanesz.ancientspellcraft.spell.EffectRaySpell.ParticleSpec(com.koomplo.wizardry.setup.registries.client.EBParticles.SNOW, -1, 0, 0))));
        SPELLS.register("curse_of_eternal_tempest", () -> new com.windanesz.ancientspellcraft.spell.EffectRaySpell(true, false,
                java.util.List.of(() -> ASEffects.CURSE_OF_ETERNAL_TEMPEST),
                java.util.List.of(new com.windanesz.ancientspellcraft.spell.EffectRaySpell.ParticleSpec(com.koomplo.wizardry.setup.registries.client.EBParticles.CLOUD, -1, 0, 0), new com.windanesz.ancientspellcraft.spell.EffectRaySpell.ParticleSpec(com.koomplo.wizardry.setup.registries.client.EBParticles.LIGHTNING, -1, 0, 0))));
        SPELLS.register("weakness", () -> new com.windanesz.ancientspellcraft.spell.EffectRaySpell(false, true,
                java.util.List.of(() -> net.minecraft.core.Holder.direct(MobEffects.WEAKNESS.value())),
                java.util.List.of(new com.windanesz.ancientspellcraft.spell.EffectRaySpell.ParticleSpec(com.koomplo.wizardry.setup.registries.client.EBParticles.DARK_MAGIC, 0x571e65, 0, 0), new com.windanesz.ancientspellcraft.spell.EffectRaySpell.ParticleSpec(com.koomplo.wizardry.setup.registries.client.EBParticles.DARK_MAGIC, 0x251609, 0, 0), new com.windanesz.ancientspellcraft.spell.EffectRaySpell.ParticleSpec(com.koomplo.wizardry.setup.registries.client.EBParticles.SPARKLE, 0x0b4b40, 12, 8))));
        SPELLS.register("hunger", () -> new com.windanesz.ancientspellcraft.spell.EffectRaySpell(false, true,
                java.util.List.of(() -> net.minecraft.core.Holder.direct(MobEffects.HUNGER.value()), () -> net.minecraft.core.Holder.direct(MobEffects.WEAKNESS.value())),
                java.util.List.of()) {
            @Override
            protected void spawnParticle(com.koomplo.wizardry.api.content.spell.internal.CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
                ctx.world().addParticle(net.minecraft.core.particles.ParticleTypes.WITCH, x, y, z, 0, 0, 0);
            }
        });
        SPELLS.register("soul_scorch", () -> new com.windanesz.ancientspellcraft.spell.EffectRaySpell(false, true,
                java.util.List.of(() -> ASEffects.SOUL_SCORCH),
                java.util.List.of(new com.windanesz.ancientspellcraft.spell.EffectRaySpell.ParticleSpec(com.koomplo.wizardry.setup.registries.client.EBParticles.DARK_MAGIC, 0xe35f00, 0, 0), new com.windanesz.ancientspellcraft.spell.EffectRaySpell.ParticleSpec(com.koomplo.wizardry.setup.registries.client.EBParticles.DARK_MAGIC, 0x6e2f01, 0, 0), new com.windanesz.ancientspellcraft.spell.EffectRaySpell.ParticleSpec(com.koomplo.wizardry.setup.registries.client.EBParticles.SPARKLE, 0xcf1100, 12, 8))));
        SPELLS.register("vanish", () -> new com.windanesz.ancientspellcraft.spell.SageEffectRaySpell(false, false,
                java.util.List.of(() -> net.minecraft.core.Holder.direct(MobEffects.INVISIBILITY.value())),
                java.util.List.of(new com.windanesz.ancientspellcraft.spell.EffectRaySpell.ParticleSpec(com.koomplo.wizardry.setup.registries.client.EBParticles.DARK_MAGIC, 0x571e65, 0, 0), new com.windanesz.ancientspellcraft.spell.EffectRaySpell.ParticleSpec(com.koomplo.wizardry.setup.registries.client.EBParticles.DARK_MAGIC, 0x251609, 0, 0), new com.windanesz.ancientspellcraft.spell.EffectRaySpell.ParticleSpec(com.koomplo.wizardry.setup.registries.client.EBParticles.SPARKLE, 0x0b4b40, 12, 8))));
        SPELLS.register("permashrink", () -> new com.windanesz.ancientspellcraft.spell.EffectRaySpell(true, true,
                java.util.List.of(() -> ASEffects.SHRINKAGE),
                java.util.List.of(new com.windanesz.ancientspellcraft.spell.EffectRaySpell.ParticleSpec(com.koomplo.wizardry.setup.registries.client.EBParticles.DARK_MAGIC, -1, 0, 0), new com.windanesz.ancientspellcraft.spell.EffectRaySpell.ParticleSpec(com.koomplo.wizardry.setup.registries.client.EBParticles.DARK_MAGIC, -1, 0, 0), new com.windanesz.ancientspellcraft.spell.EffectRaySpell.ParticleSpec(com.koomplo.wizardry.setup.registries.client.EBParticles.SPARKLE, -1, 12, 8))));
        SPELLS.register("permagrowth", () -> new com.windanesz.ancientspellcraft.spell.EffectRaySpell(true, true,
                java.util.List.of(() -> ASEffects.GROWTH),
                java.util.List.of(new com.windanesz.ancientspellcraft.spell.EffectRaySpell.ParticleSpec(com.koomplo.wizardry.setup.registries.client.EBParticles.DARK_MAGIC, -1, 0, 0), new com.windanesz.ancientspellcraft.spell.EffectRaySpell.ParticleSpec(com.koomplo.wizardry.setup.registries.client.EBParticles.DARK_MAGIC, -1, 0, 0), new com.windanesz.ancientspellcraft.spell.EffectRaySpell.ParticleSpec(com.koomplo.wizardry.setup.registries.client.EBParticles.SPARKLE, -1, 12, 8))));
        SPELLS.register("fluorescence", () -> new com.windanesz.ancientspellcraft.spell.EffectRaySpell(false, false,
                java.util.List.of(() -> net.minecraft.core.Holder.direct(MobEffects.GLOWING.value())),
                java.util.List.of(new com.windanesz.ancientspellcraft.spell.EffectRaySpell.ParticleSpec(com.koomplo.wizardry.setup.registries.client.EBParticles.SPARKLE, 0xccffcc, 12, 8))));
        SPELLS.register("starve", com.windanesz.ancientspellcraft.spell.Starve::new);
        SPELLS.register("sufferance", com.windanesz.ancientspellcraft.spell.Sufferance::new);
        SPELLS.register("cursed_touch", com.windanesz.ancientspellcraft.spell.CursedTouch::new);
        SPELLS.register("power_siphon", com.windanesz.ancientspellcraft.spell.PowerSiphon::new);
        SPELLS.register("zombification", com.windanesz.ancientspellcraft.spell.Zombification::new);
        SPELLS.register("cure_zombie", com.windanesz.ancientspellcraft.spell.CureZombie::new);
        SPELLS.register("snow_block", com.windanesz.ancientspellcraft.spell.SnowBlock::new);
        SPELLS.register("conjure_lava", com.windanesz.ancientspellcraft.spell.ConjureLava::new);
        SPELLS.register("conjure_cake", com.windanesz.ancientspellcraft.spell.ConjureCake::new);
        SPELLS.register("torchlight", com.windanesz.ancientspellcraft.spell.Torchlight::new);
        SPELLS.register("singe", com.windanesz.ancientspellcraft.spell.Singe::new);
        SPELLS.register("summon_boat", com.windanesz.ancientspellcraft.spell.SummonBoat::new);
        SPELLS.register("lily_pad", com.windanesz.ancientspellcraft.spell.LilyPad::new);
        SPELLS.register("suppression", () -> new com.windanesz.ancientspellcraft.spell.EffectRaySpell(false, true,
                java.util.List.of(() -> ASEffects.MAGICAL_EXHAUSTION),
                java.util.List.of(new com.windanesz.ancientspellcraft.spell.EffectRaySpell.ParticleSpec(com.koomplo.wizardry.setup.registries.client.EBParticles.DARK_MAGIC, 0x635a63, 0, 0),
                        new com.windanesz.ancientspellcraft.spell.EffectRaySpell.ParticleSpec(com.koomplo.wizardry.setup.registries.client.EBParticles.SPARKLE, 0x9a8fa3, 12, 8))));
        SPELLS.register("summon_spider", com.windanesz.ancientspellcraft.spell.SummonSpider::new);
        SPELLS.register("summon_skeleton_horse", com.windanesz.ancientspellcraft.spell.SummonSkeletonHorse::new);
        SPELLS.register("summon_fire_ant", com.windanesz.ancientspellcraft.spell.SummonFireAnt::new);
        SPELLS.register("fire_ant_swarm", com.windanesz.ancientspellcraft.spell.SummonVolcano::new);
        SPELLS.register("summon_remnant", com.windanesz.ancientspellcraft.spell.SummonRemnant::new);
        SPELLS.register("raise_skeleton_mage", com.windanesz.ancientspellcraft.spell.RaiseSkeletonMage::new);
        SPELLS.register("unholy_alliance", com.windanesz.ancientspellcraft.spell.UnholyAlliance::new);
        SPELLS.register("animate_weapon", () -> new com.windanesz.ancientspellcraft.spell.AnimateSpell("animate_weapon", true));
        SPELLS.register("animate_item", () -> new com.windanesz.ancientspellcraft.spell.AnimateSpell("animate_item", false));
        SPELLS.register("conjure_shadow_blade", () -> new com.windanesz.ancientspellcraft.spell.ASConjureItemSpell(ASItems.SHADOW_BLADE.get()));
        SPELLS.register("conjure_fishing_rod", () -> new com.windanesz.ancientspellcraft.spell.ASConjureItemSpell(ASItems.SPECTRAL_FISHING_ROD.get()));
        SPELLS.register("ice_cream", () -> new com.windanesz.ancientspellcraft.spell.ASConjureItemSpell(ASItems.ICE_CREAM.get()));
        FLINT_SHARD = SPELLS.register("flint_shard", com.windanesz.ancientspellcraft.spell.FlintShard::new);
        SPELLS.register("armageddon", com.windanesz.ancientspellcraft.spell.Armageddon::new);
        SPELLS.register("conflagration", com.windanesz.ancientspellcraft.spell.Conflagration::new);
        SPELLS.register("fimbulwinter", com.windanesz.ancientspellcraft.spell.Fimbulwinter::new);
        SPELLS.register("stone_punch", () -> new com.windanesz.ancientspellcraft.spell.StoneFistSpell("stone_punch", ASItems.STONE_FIST.get()));
        SPELLS.register("stone_fist", () -> new com.windanesz.ancientspellcraft.spell.StoneFistSpell("stone_fist", ASItems.ADVANCED_STONE_FIST.get()));
        SPELLS.register("reveal_undead", com.windanesz.ancientspellcraft.spell.RevealUndead::new);
        SPELLS.register("metabolism_overdrive", com.windanesz.ancientspellcraft.spell.MetabolismOverdrive::new);
        SPELLS.register("wizard_shield", com.windanesz.ancientspellcraft.spell.WizardShieldSpell::new);
        SPELLS.register("withdraw_life", com.windanesz.ancientspellcraft.spell.WithdrawLife::new);
        SPELLS.register("cauterize", com.windanesz.ancientspellcraft.spell.Cauterize::new);
        SPELLS.register("eye_of_the_storm", com.windanesz.ancientspellcraft.spell.EyeOfTheStorm::new);
        SPELLS.register("harvest", com.windanesz.ancientspellcraft.spell.Harvest::new);
        SPELLS.register("mass_pyrokinesis", com.windanesz.ancientspellcraft.spell.MassPyrokinesis::new);
        SPELLS.register("summon_anchor", com.windanesz.ancientspellcraft.spell.SummonAnchor::new);
        SPELLS.register("forcefend", com.windanesz.ancientspellcraft.spell.Forcefend::new);
        SPELLS.register("arcane_magnetism", com.windanesz.ancientspellcraft.spell.ArcaneMagnetism::new);
        BURROW = SPELLS.register("burrow", com.windanesz.ancientspellcraft.spell.Burrow::new);
        SPELLS.register("call_of_the_pack", com.windanesz.ancientspellcraft.spell.CallOfThePack::new);
        SPELLS.register("summon_spirit_bear", com.windanesz.ancientspellcraft.spell.SummonSpiritBear::new);
        SPELLS.register("molten_earth", com.windanesz.ancientspellcraft.spell.MoltenEarth::new);
        SPELLS.register("summon_quicksand", com.windanesz.ancientspellcraft.spell.SummonQuicksand::new);
        SPELLS.register("firewall", com.windanesz.ancientspellcraft.spell.FireWall::new);
        SPELLS.register("electrify", com.windanesz.ancientspellcraft.spell.Electrify::new);
        SPELLS.register("shock_zone", com.windanesz.ancientspellcraft.spell.ShockZone::new);
        SPELLS.register("magma_shell", com.windanesz.ancientspellcraft.spell.MagmaShell::new);
        SPELLS.register("static_dome", com.windanesz.ancientspellcraft.spell.StaticDome::new);
        SPELLS.register("quicksand_ring", com.windanesz.ancientspellcraft.spell.QuicksandRing::new);
        SPELLS.register("create_igloo", com.windanesz.ancientspellcraft.spell.CreateIgloo::new);
        SPELLS.register("frost_nova", com.windanesz.ancientspellcraft.spell.FrostNova::new);
        SPELLS.register("wild_sporeling", com.windanesz.ancientspellcraft.spell.WildSporeling::new);
        SPELLS.register("sporelings_aid", com.windanesz.ancientspellcraft.spell.SporelingsAid::new);
        SPELLS.register("fairy_ring", com.windanesz.ancientspellcraft.spell.FairyRing::new);
        SPELLS.register("ice_workbench", com.windanesz.ancientspellcraft.spell.IceWorkbench::new);
        SPELLS.register("conjure_creeper", () -> new com.windanesz.ancientspellcraft.spell.SageMinionSpell<>(
                level -> new com.windanesz.ancientspellcraft.entity.living.CreeperMinion(ASEntities.CREEPER_MINION.get(), level)));
        SPELLS.register("nether_guard", () -> new com.windanesz.ancientspellcraft.spell.SageMinionSpell<>(
                level -> new com.windanesz.ancientspellcraft.entity.living.PigZombieMinion(ASEntities.PIG_ZOMBIE_MINION.get(), level)));
        SPELLS.register("summon_zombie_pigman", () -> new com.windanesz.ancientspellcraft.spell.SageMinionSpell<>(
                level -> new com.windanesz.ancientspellcraft.entity.living.PigZombieMinion(ASEntities.PIG_ZOMBIE_MINION.get(), level)));
        SPELLS.register("magic_sparks", com.windanesz.ancientspellcraft.spell.MagicSparks::new);
        SPELLS.register("conjure_ink", com.windanesz.ancientspellcraft.spell.ConjureInk::new);
        SPELLS.register("unveil", com.windanesz.ancientspellcraft.spell.Unveil::new);
        SPELLS.register("poison_spray", com.windanesz.ancientspellcraft.spell.PoisonSpray::new);
        SPELLS.register("transplace", com.windanesz.ancientspellcraft.spell.Transplace::new);
        SPELLS.register("extension", com.windanesz.ancientspellcraft.spell.Extension::new);
        SPELLS.register("counterspell", com.windanesz.ancientspellcraft.spell.Counterspell::new);
        SPELLS.register("ray_of_enfeeblement", () -> new com.windanesz.ancientspellcraft.spell.SageEffectRaySpell(false, true,
                java.util.List.of(() -> net.minecraft.core.Holder.direct(MobEffects.WEAKNESS.value()),
                        () -> net.minecraft.core.Holder.direct(MobEffects.MOVEMENT_SLOWDOWN.value()),
                        () -> net.minecraft.core.Holder.direct(MobEffects.DIG_SLOWDOWN.value()),
                        () -> net.minecraft.core.Holder.direct(MobEffects.HUNGER.value())),
                java.util.List.of(new com.windanesz.ancientspellcraft.spell.EffectRaySpell.ParticleSpec(
                        com.koomplo.wizardry.setup.registries.client.EBParticles.DARK_MAGIC, 0x3d2f4f, 0, 0))));
        SPELLS.register("chaos_touch", com.windanesz.ancientspellcraft.spell.ChaosTouch::new);
        SPELLS.register("confusion", com.windanesz.ancientspellcraft.spell.Confusion::new);
        SPELLS.register("chaotic_empowerment", com.windanesz.ancientspellcraft.spell.ChaoticEmpowerment::new);
        SPELLS.register("chaotic_rebinding", com.windanesz.ancientspellcraft.spell.ChaoticRebinding::new);
        SPELLS.register("chaos_vortex", com.windanesz.ancientspellcraft.spell.ChaosVortex::new);
        SPELLS.register("obliteration", com.windanesz.ancientspellcraft.spell.Obliteration::new);
        SPELLS.register("chaos_orb", com.windanesz.ancientspellcraft.spell.ChaosOrbSpell::new);
        SPELLS.register("chaos_field", com.windanesz.ancientspellcraft.spell.ChaosFieldSpell::new);
        SPELLS.register("absorb_crystal", com.windanesz.ancientspellcraft.spell.AbsorbCrystal::new);
        SPELLS.register("absorb_spell", com.windanesz.ancientspellcraft.spell.AbsorbSpell::new);
        SPELLS.register("absorb_potion", com.windanesz.ancientspellcraft.spell.AbsorbPotion::new);
        SPELLS.register("chaos_blast", com.windanesz.ancientspellcraft.spell.ChaosBlast::new);
        SPELLS.register("absorb_object", com.windanesz.ancientspellcraft.spell.AbsorbObject::new);
        SPELLS.register("absorb_projectile", com.windanesz.ancientspellcraft.spell.AbsorbProjectile::new);
        SPELLS.register("alter_potion", com.windanesz.ancientspellcraft.spell.AlterPotion::new);
        SPELLS.register("spectral_wall", com.windanesz.ancientspellcraft.spell.SpectralWall::new);
        SPELLS.register("spectral_floor", com.windanesz.ancientspellcraft.spell.SpectralFloor::new);
        SPELLS.register("teleport_object", com.windanesz.ancientspellcraft.spell.TeleportObject::new);
        SPELLS.register("molten_boulder", com.windanesz.ancientspellcraft.spell.MoltenBoulderSpell::new);
        SPELLS.register("prismatic_spray", com.windanesz.ancientspellcraft.spell.PrismaticSpray::new);
        SPELLS.register("ternary_storm", com.windanesz.ancientspellcraft.spell.TernaryStorm::new);
        SPELLS.register("arcane_wall", com.windanesz.ancientspellcraft.spell.ArcaneWall::new);
        SPELLS.register("orb_space", com.windanesz.ancientspellcraft.spell.OrbSpace::new);
    }

    private static java.util.function.Supplier<Holder<net.minecraft.world.effect.MobEffect>> holder(Holder<net.minecraft.world.effect.MobEffect> h) {
        return () -> h;
    }

    private ASSpells() {
    }
}
