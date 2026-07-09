package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.content.entity.construct.BlizzardConstruct;
import com.koomplo.wizardry.content.entity.construct.StormcloudConstruct;
import com.koomplo.wizardry.api.content.spell.internal.CastContext;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.api.content.util.BlockUtil;
import com.koomplo.wizardry.content.item.armor.WizardArmorType;
import com.koomplo.wizardry.content.spell.DefaultProperties;
import com.koomplo.wizardry.content.spell.abstr.ConstructRangedSpell;
import com.koomplo.wizardry.setup.registries.EBEntities;
import com.windanesz.ancientspellcraft.block.TemporaryBlockEntity;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;

/**
 * Tempestade ternaria (1.12.2 TernaryStorm, SAGE master): stormcloud + blizzard no ponto mirado e
 * o chao abaixo vira magma conjurado/bloco de raio temporarios - raio, gelo e fogo de uma vez.
 */
public class TernaryStorm extends ConstructRangedSpell<StormcloudConstruct> implements ClassSpell {

    public TernaryStorm() {
        super(level -> new StormcloudConstruct(EBEntities.STORMCLOUD.get(), level), false);
    }

    @Override
    protected void addConstructExtras(CastContext ctx, StormcloudConstruct construct, Direction side) {
        super.addConstructExtras(ctx, construct, side);
        if (ctx.world().isClientSide) return;

        BlizzardConstruct blizzard = new BlizzardConstruct(ctx.world());
        if (ctx.caster() != null) blizzard.setCaster(ctx.caster());
        blizzard.setPos(construct.getX(), construct.getY(), construct.getZ());
        blizzard.lifetime = 40;
        ctx.world().addFreshEntity(blizzard);

        if (ctx.caster() == null) return;
        BlockPos base = construct.blockPosition().below();
        int radius = (int) (property(DefaultProperties.EFFECT_RADIUS) * ctx.modifiers().get(SpellModifiers.BLAST));
        int blockLifetime = property(DefaultProperties.DURATION);
        for (BlockPos pos : BlockUtil.getBlockSphere(construct.blockPosition(), radius)) {
            if (pos.getY() != base.getY() || ctx.world().isEmptyBlock(pos)) continue;
            if (ctx.world().getBlockState(pos).getDestroySpeed(ctx.world(), pos) < 0) continue;
            var block = ctx.world().random.nextBoolean() ? ASBlocks.CONJURED_MAGMA.get() : ASBlocks.LIGHTNING_BLOCK.get();
            TemporaryBlockEntity.place(ctx.caster(), ctx.world(), block, pos, blockLifetime);
        }
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
