package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.api.content.spell.internal.CastContext;
import com.koomplo.wizardry.content.item.SpellBookItem;
import com.koomplo.wizardry.setup.registries.EBItems;
import com.koomplo.wizardry.setup.registries.client.EBParticles;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/** Revinculo caotico (1.12.2 ChaoticRebinding): explode um livro de spell dropado, deixando um livro arruinado. */
public class ChaoticRebinding extends WarlockRaySpell {

    public ChaoticRebinding() {
        this.ignoreLivingEntities(true);
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if (!(entityHit.getEntity() instanceof ItemEntity item)
                || !(item.getItem().getItem() instanceof SpellBookItem)) return false;
        Vec3 pos = item.position();
        if (!ctx.world().isClientSide) {
            item.setItem(ItemStack.EMPTY);
            item.discard();
            var ruined = new ItemEntity(ctx.world(), pos.x, pos.y, pos.z, new ItemStack(EBItems.RUINED_SPELL_BOOK.get()));
            ctx.world().explode(ruined, pos.x, pos.y, pos.z, 3f, Level.ExplosionInteraction.NONE);
            ctx.world().addFreshEntity(ruined);
        } else {
            ParticleBuilder.create(EBParticles.FLASH).shaded(true).time(40).scale(1.9f)
                    .pos(pos.x, pos.y + 0.5, pos.z).color(0xbf00ee).spawn(ctx.world());
        }
        return true;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        return false;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ParticleBuilder.create(EBParticles.DARK_MAGIC).pos(x, y, z).color(0xbf00ee).spawn(ctx.world());
    }
}
