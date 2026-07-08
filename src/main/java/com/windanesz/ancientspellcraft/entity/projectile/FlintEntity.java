package com.windanesz.ancientspellcraft.entity.projectile;

import com.koomplo.wizardry.api.content.entity.projectile.MagicArrowEntity;
import com.windanesz.ancientspellcraft.registry.ASSpells;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;

/** Estilhaco de silex (1.12.2 EntityFlint). */
public class FlintEntity extends MagicArrowEntity {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("ancientspellcraft", "textures/entity/flint_shard.png");

    public FlintEntity(EntityType<? extends AbstractArrow> type, Level world) {
        super(type, world);
    }

    @Override
    public double getDamage() {
        return ASSpells.FLINT_SHARD.get().property(com.koomplo.wizardry.content.spell.DefaultProperties.DAMAGE);
    }

    @Override
    public int getLifetime() {
        return -1;
    }

    @Override
    public ResourceLocation getTexture() {
        return TEXTURE;
    }
}
