package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperties;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperty;
import com.koomplo.wizardry.api.content.util.EntityUtil;
import com.koomplo.wizardry.content.item.armor.WizardArmorType;
import com.koomplo.wizardry.content.spell.DefaultProperties;
import com.koomplo.wizardry.setup.registries.EBAttachments;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/** Empoderamento caotico (1.12.2 ChaoticEmpowerment): cada minion seu ou EXPLODE (explosion_chance) ou ganha forca+velocidade. */
public class ChaoticEmpowerment extends Spell implements ClassSpell {

    public static final SpellProperty<Float> EXPLOSION_STRENGTH = SpellProperty.floatProperty("explosion_strength");
    public static final SpellProperty<Float> EXPLOSION_CHANCE = SpellProperty.floatProperty("explosion_chance");

    @Override
    public boolean cast(PlayerCastContext ctx) {
        boolean affected = false;
        double radius = property(DefaultProperties.EFFECT_RADIUS) * ctx.modifiers().get(SpellModifiers.BLAST);
        for (var entity : EntityUtil.getLivingWithinRadius(radius, ctx.caster().getX(), ctx.caster().getY(), ctx.caster().getZ(), ctx.world())) {
            if (!(entity instanceof Mob mob)) continue;
            var data = mob.getData(EBAttachments.MINION_DATA);
            if (!data.isSummoned() || data.getOwner() != ctx.caster()) continue;
            affected = true;
            if (ctx.world().random.nextFloat() < property(EXPLOSION_CHANCE)) {
                if (!ctx.world().isClientSide) {
                    mob.discard();
                    ctx.world().explode(mob, mob.getX(), mob.getY(), mob.getZ(), property(EXPLOSION_STRENGTH),
                            EntityUtil.canDamageBlocks(ctx.caster(), ctx.world())
                                    ? Level.ExplosionInteraction.MOB : Level.ExplosionInteraction.NONE);
                }
            } else if (!ctx.world().isClientSide) {
                int duration = property(DefaultProperties.EFFECT_DURATION);
                mob.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, duration, 0));
                mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, duration, 1));
            }
        }
        if (affected) this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return affected;
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
