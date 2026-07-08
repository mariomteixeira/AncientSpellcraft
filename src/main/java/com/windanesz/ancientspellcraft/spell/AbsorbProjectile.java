package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperties;
import com.koomplo.wizardry.content.item.armor.WizardArmorType;
import com.windanesz.ancientspellcraft.registry.ASAttachments;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

/**
 * Canaliza absorvendo projeteis proximos (ate 10, viram tipos guardados); agachado, RE-DISPARA
 * um projetil guardado na direcao do olhar (1.12.2 AbsorbProjectile; o original serializava o NBT
 * completo do projetil - aqui guardamos o tipo, desvio documentado).
 */
public class AbsorbProjectile extends Spell implements ClassSpell {

    @Override
    public boolean isInstantCast() {
        return false;
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        if (ctx.world().isClientSide) return true;
        var tag = ctx.caster().getData(ASAttachments.WARLOCK_DATA);
        ListTag list = tag.getList("Projectiles", Tag.TAG_STRING);

        if (ctx.caster().isShiftKeyDown()) {
            if (list.isEmpty() || ctx.castingTicks() % 5 != 0) return !list.isEmpty();
            var id = ResourceLocation.tryParse(list.getString(0));
            list.remove(0);
            tag.put("Projectiles", list);
            ctx.caster().setData(ASAttachments.WARLOCK_DATA, tag);
            if (id == null) return true;
            EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(id);
            if (type.create(ctx.world()) instanceof Projectile projectile) {
                var look = ctx.caster().getLookAngle();
                projectile.setPos(ctx.caster().getX(), ctx.caster().getEyeY(), ctx.caster().getZ());
                projectile.setOwner(ctx.caster());
                projectile.shoot(look.x, look.y, look.z, 1.5f, 1.0f);
                ctx.world().addFreshEntity(projectile);
            }
            this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
            return true;
        }

        if (ctx.castingTicks() % 5 != 0 || list.size() >= 10) return true;
        for (var projectile : ctx.world().getEntitiesOfClass(Projectile.class,
                ctx.caster().getBoundingBox().inflate(6))) {
            if (projectile.getOwner() == ctx.caster()) continue;
            list.add(StringTag.valueOf(BuiltInRegistries.ENTITY_TYPE.getKey(projectile.getType()).toString()));
            projectile.discard();
            tag.put("Projectiles", list);
            ctx.caster().setData(ASAttachments.WARLOCK_DATA, tag);
            this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
            break;
        }
        return true;
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
