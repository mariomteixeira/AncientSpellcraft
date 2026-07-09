package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperties;
import com.windanesz.ancientspellcraft.registry.ASAttachments;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

/**
 * metamagic_projectile (1.12.2 MetamagicProjectile): arma uma flag no jogador; o PRÓXIMO cast
 * (que não seja ray/projétil/metamagic) é cancelado e vira um projétil que casta a spell no
 * impacto — leitura em ASMetamagicEvents.
 */
public class MetamagicProjectileSpell extends Spell {

    public static final String FLAG = "MetamagicProjectile";

    @Override
    public boolean cast(PlayerCastContext ctx) {
        if (!ctx.world().isClientSide) {
            var tag = ctx.caster().getData(ASAttachments.PLAYER_DATA);
            tag.putBoolean(FLAG, true);
            ctx.caster().setData(ASAttachments.PLAYER_DATA, tag);
        }
        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return true;
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == ASItems.ANCIENT_SPELLCRAFT_SPELL_BOOK.get() || item == ASItems.ANCIENT_SPELLCRAFT_SCROLL.get();
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.empty();
    }
}
