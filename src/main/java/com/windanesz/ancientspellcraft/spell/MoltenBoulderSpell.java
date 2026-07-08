package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.internal.CastContext;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperty;
import com.koomplo.wizardry.content.item.armor.WizardArmorType;
import com.koomplo.wizardry.content.spell.abstr.ConstructSpell;
import com.windanesz.ancientspellcraft.entity.construct.MoltenBoulderConstruct;
import com.windanesz.ancientspellcraft.registry.ASEntities;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;

/** Pedregulho fundido (1.12.2 MoltenBoulder). */
public class MoltenBoulderSpell extends ConstructSpell<MoltenBoulderConstruct> implements ClassSpell {

    public static final SpellProperty<Float> SPEED = SpellProperty.floatProperty("speed");

    public MoltenBoulderSpell() {
        super(level -> new MoltenBoulderConstruct(ASEntities.MOLTEN_BOULDER.get(), level), false);
    }

    @Override
    protected void addConstructExtras(CastContext ctx, MoltenBoulderConstruct construct, net.minecraft.core.Direction side) {
        super.addConstructExtras(ctx, construct, side);
        float speed = property(SPEED);
        Vec3 look = ctx.caster().getLookAngle();
        Vec3 direction = new Vec3(look.x, 0, look.z).normalize();
        construct.setHorizontalVelocity(direction.x * speed, direction.z * speed);
        construct.setYRot(ctx.caster().getYRot());
        construct.setPos(construct.getX() + direction.x, construct.getY() + 1.6, construct.getZ() + direction.z);
    }

    @Override
    public WizardArmorType armourClass() {
        return WizardArmorType.SAGE;
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == ASItems.MYSTIC_SPELL_BOOK.get();
    }
}
