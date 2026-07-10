package com.windanesz.ancientspellcraft.entity.projectile;

import com.koomplo.wizardry.api.content.entity.projectile.MagicArrowEntity;
import com.koomplo.wizardry.setup.registries.EBDamageSources;
import com.windanesz.ancientspellcraft.block.MasterBoltBlock;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.spell.MasterBoltSpell;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Raio-projétil do master_bolt (1.12.2 EntityMasterBolt): voa reto (sem gravidade, atravessa
 * mobs com dano SHOCK) e ao acertar bloco vira o BLOCO master_bolt, gravando a localização
 * para o puxão do re-cast.
 */
public class MasterBoltEntity extends MagicArrowEntity {

    public MasterBoltEntity(EntityType<? extends MagicArrowEntity> type, Level level) {
        super(type, level);
        setNoGravity(true);
    }

    @Override
    public double getDamage() {
        return 12.0; // spell property "damage" (JSON 1.12.2) lida pela spell; fallback aqui
    }

    @Override
    public int getLifetime() {
        return -1;
    }

    @Override
    public ResourceKey<DamageType> getDamageType() {
        return EBDamageSources.SHOCK;
    }

    @Override
    public boolean doDeceleration() {
        return false;
    }

    @Override
    public boolean doOverpenetration() {
        return true;
    }

    @Override
    public ResourceLocation getTexture() {
        return ResourceLocation.fromNamespaceAndPath("ebwizardry", "textures/entity/lightning_arrow.png");
    }

    @Override
    protected void onHitBlock(BlockHitResult hit) {
        super.onHitBlock(hit);
        if (level().isClientSide) return;
        BlockPos pos = hit.getBlockPos().relative(hit.getDirection());
        if (level().getBlockState(pos).canBeReplaced() && getOwner() instanceof Player player) {
            Direction facing = getDirection().getAxis().isHorizontal() ? getDirection() : Direction.NORTH;
            level().setBlockAndUpdate(pos, ASBlocks.MASTER_BOLT.get().defaultBlockState()
                    .setValue(MasterBoltBlock.FACING, facing));
            MasterBoltSpell.storeLocation(player, pos);
        }
        discard();
    }
}
