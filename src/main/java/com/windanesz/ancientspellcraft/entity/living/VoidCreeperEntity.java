package com.windanesz.ancientspellcraft.entity.living;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;

/**
 * Void Creeper (1.12.2 EntityVoidCreeper): clone do creeper vanilla (mesma IA/fuse/explosão),
 * diferencial é a skin e a loot própria (magic crystal / astral diamond shard / grand crystal).
 * Loot table resolve pelo id do tipo: ancientspellcraft:entities/void_creeper.
 */
public class VoidCreeperEntity extends Creeper {

    public VoidCreeperEntity(EntityType<? extends Creeper> type, Level level) {
        super(type, level);
    }
}
