package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperties;
import com.windanesz.ancientspellcraft.registry.ASAttachments;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

/**
 * Contingency (1.12.2 Contingency, 8 variantes): castar arma o "listener"; a PRÓXIMA spell castada
 * é capturada (não casta) e fica guardada; quando o gatilho acontece (fogo, queda, dano, vida
 * crítica, morte, afogamento...), a spell dispara sozinha. Leitura em ASMetamagicEvents
 * (captura) e ASContingencyEvents (gatilhos). Desvios documentados: gatilhos immobility e
 * hostile_spellcast TODO; ring_eternal_contingency (re-uso) fica com artefatos; o disparo
 * automático não cobra mana.
 */
public class ContingencySpell extends Spell {

    public static final String LISTENER_TAG = "ContingencyListener";
    public static final String STORED_PREFIX = "Contingency_";

    public enum Type {
        FIRE("contingency_fire"),
        FALL("contingency_fall"),
        DAMAGE("contingency_damage"),
        CRITICAL_HEALTH("contingency_critical_health"),
        DEATH("contingency_death"),
        DROWNING("contingency_drowning"),
        HOSTILE_SPELLCAST("contingency_hostile_spellcast"),
        IMMOBILITY("contingency_immobility");

        public final String spellName;

        Type(String spellName) {
            this.spellName = spellName;
        }
    }

    private final Type type;

    public ContingencySpell(Type type) {
        this.type = type;
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        if (!ctx.world().isClientSide) {
            CompoundTag tag = ctx.caster().getData(ASAttachments.PLAYER_DATA);
            tag.putString(LISTENER_TAG, type.name());
            ctx.caster().setData(ASAttachments.PLAYER_DATA, tag);
            ctx.caster().displayClientMessage(
                    Component.translatable("spell.ancientspellcraft.contingency.armed"), true);
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
