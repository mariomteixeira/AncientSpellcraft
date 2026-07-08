package com.windanesz.ancientspellcraft.registry;

import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.core.registry.EBRegistries;
import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.spell.ASBuffSpell;
import com.windanesz.ancientspellcraft.spell.AreaBuffSpell;
import com.windanesz.ancientspellcraft.spell.LightBuffSpell;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ASSpells {

    public static final DeferredRegister<Spell> SPELLS = DeferredRegister.create(EBRegistries.SPELL, AncientSpellcraft.MODID);

    static {
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
        SPELLS.register("vanish", () -> new com.windanesz.ancientspellcraft.spell.EffectRaySpell(false, false,
                java.util.List.of(() -> net.minecraft.core.Holder.direct(MobEffects.INVISIBILITY.value())),
                java.util.List.of(new com.windanesz.ancientspellcraft.spell.EffectRaySpell.ParticleSpec(com.koomplo.wizardry.setup.registries.client.EBParticles.DARK_MAGIC, 0x571e65, 0, 0), new com.windanesz.ancientspellcraft.spell.EffectRaySpell.ParticleSpec(com.koomplo.wizardry.setup.registries.client.EBParticles.DARK_MAGIC, 0x251609, 0, 0), new com.windanesz.ancientspellcraft.spell.EffectRaySpell.ParticleSpec(com.koomplo.wizardry.setup.registries.client.EBParticles.SPARKLE, 0x0b4b40, 12, 8))));
        SPELLS.register("permashrink", () -> new com.windanesz.ancientspellcraft.spell.EffectRaySpell(true, true,
                java.util.List.of(() -> ASEffects.SHRINKAGE),
                java.util.List.of(new com.windanesz.ancientspellcraft.spell.EffectRaySpell.ParticleSpec(com.koomplo.wizardry.setup.registries.client.EBParticles.DARK_MAGIC, -1, 0, 0), new com.windanesz.ancientspellcraft.spell.EffectRaySpell.ParticleSpec(com.koomplo.wizardry.setup.registries.client.EBParticles.DARK_MAGIC, -1, 0, 0), new com.windanesz.ancientspellcraft.spell.EffectRaySpell.ParticleSpec(com.koomplo.wizardry.setup.registries.client.EBParticles.SPARKLE, -1, 12, 8))));
        SPELLS.register("permagrowth", () -> new com.windanesz.ancientspellcraft.spell.EffectRaySpell(true, true,
                java.util.List.of(() -> ASEffects.GROWTH),
                java.util.List.of(new com.windanesz.ancientspellcraft.spell.EffectRaySpell.ParticleSpec(com.koomplo.wizardry.setup.registries.client.EBParticles.DARK_MAGIC, -1, 0, 0), new com.windanesz.ancientspellcraft.spell.EffectRaySpell.ParticleSpec(com.koomplo.wizardry.setup.registries.client.EBParticles.DARK_MAGIC, -1, 0, 0), new com.windanesz.ancientspellcraft.spell.EffectRaySpell.ParticleSpec(com.koomplo.wizardry.setup.registries.client.EBParticles.SPARKLE, -1, 12, 8))));
    }

    private static java.util.function.Supplier<Holder<net.minecraft.world.effect.MobEffect>> holder(Holder<net.minecraft.world.effect.MobEffect> h) {
        return () -> h;
    }

    private ASSpells() {
    }
}
