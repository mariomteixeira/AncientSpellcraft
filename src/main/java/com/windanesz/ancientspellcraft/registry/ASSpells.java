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
    }

    private static java.util.function.Supplier<Holder<net.minecraft.world.effect.MobEffect>> holder(Holder<net.minecraft.world.effect.MobEffect> h) {
        return () -> h;
    }

    private ASSpells() {
    }
}
