package com.windanesz.ancientspellcraft.entity.projectile;

import com.koomplo.wizardry.content.entity.projectile.IceShardEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

/** Ice shard que nao acerta o proprio caster (1.12.2 EntitySafeIceShard). */
public class SafeIceShardEntity extends IceShardEntity {

    public SafeIceShardEntity(EntityType<? extends AbstractArrow> type, Level world) {
        super(type, world);
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        if (getOwner() != null && getOwner() == result.getEntity()) return;
        super.onHitEntity(result);
    }
}
