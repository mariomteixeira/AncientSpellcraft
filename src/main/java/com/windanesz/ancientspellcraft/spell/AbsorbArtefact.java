package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.item.ArtifactItem;
import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperty;
import com.windanesz.ancientspellcraft.registry.ASAttachments;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import java.util.List;

/**
 * Absorve um artefato do offhand (1.12.2 AbsorbArtefact): body_power_gem acumula PowerGems
 * (+1% de potência por gema no cast, máx 30 — hook no ASWarlockEvents); outros artefatos ficam
 * guardados em AbsorbedArtefact SEM efeito — fiel ao 1.12.2 shipped, que tinha a ativação
 * comentada no ArtefactCheckEvent. Blacklist com os defaults do 1.12.2 (config no marco 7).
 */
public class AbsorbArtefact extends WarlockChannelSpell {

    public static final SpellProperty<Integer> TIER_LIMIT = SpellProperty.intProperty("tier_limit", 2);
    public static final int MAX_POWER_GEMS = 30; // gem_of_power_max_absorb_amount (1.12.2)

    private static final List<ResourceLocation> BLACKLIST = List.of(
            ResourceLocation.fromNamespaceAndPath("ancientspellcraft", "charm_philosophers_stone"),
            ResourceLocation.fromNamespaceAndPath("ancientspellcraft", "cornucopia"),
            ResourceLocation.fromNamespaceAndPath("ancientspellcraft", "charm_bucket_coal"),
            ResourceLocation.fromNamespaceAndPath("ancientspellcraft", "charm_evergrowing_crystal"),
            ResourceLocation.fromNamespaceAndPath("ancientspellcraft", "charm_gold_bag"));

    @Override
    protected boolean isValidOffhand(net.minecraft.world.entity.player.Player caster, ItemStack stack) {
        if (!(stack.getItem() instanceof ArtifactItem)) return false;
        if (BLACKLIST.contains(BuiltInRegistries.ITEM.getKey(stack.getItem()))) return false;
        // 1.12.2: só as raridades conhecidas são limitadas (UNCOMMON=0, RARE=1, EPIC=2)
        int ordinal = switch (stack.getRarity()) {
            case UNCOMMON -> 0;
            case RARE -> 1;
            case EPIC -> 2;
            default -> -1;
        };
        return ordinal < 0 || ordinal <= property(TIER_LIMIT);
    }

    @Override
    protected String invalidMessage() {
        return "spell.ancientspellcraft.absorb_artefact.no_artefact";
    }

    @Override
    protected boolean absorb(PlayerCastContext ctx, ItemStack offhand) {
        var tag = ctx.caster().getData(ASAttachments.WARLOCK_DATA);
        if (offhand.is(ASItems.BODY_POWER_GEM.get())) {
            int gems = tag.getInt("PowerGems");
            if (gems > MAX_POWER_GEMS) return false;
            tag.putInt("PowerGems", gems + 1);
        } else {
            tag.putString("AbsorbedArtefact", BuiltInRegistries.ITEM.getKey(offhand.getItem()).toString());
        }
        ctx.caster().setData(ASAttachments.WARLOCK_DATA, tag);
        ctx.caster().displayClientMessage(Component.translatable("spell.ancientspellcraft.absorb_spell.absorbed",
                offhand.getHoverName()), true);
        offhand.shrink(1);
        return true;
    }
}
