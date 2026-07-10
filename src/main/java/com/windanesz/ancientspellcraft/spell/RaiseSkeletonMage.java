package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.internal.CastContext;
import com.koomplo.wizardry.content.spell.abstr.MinionSpell;
import com.windanesz.ancientspellcraft.entity.living.SkeletonMageEntity;
import com.windanesz.ancientspellcraft.entity.living.SkeletonMageMinion;
import com.windanesz.ancientspellcraft.registry.ASEntities;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.world.item.Item;

/**
 * Ergue um esqueleto mago de elemento aleatorio (1.12.2 RaiseSkeletonMage). Cristal elemental no
 * offhand foca o elemento (10% de quebrar); com amulet_elemental_offense não quebra (desvio: o
 * 1.12.2 usava o cristal encaixado no amuleto — o port não tem slots de artefato).
 */
public class RaiseSkeletonMage extends MinionSpell<SkeletonMageMinion> {

    public RaiseSkeletonMage() {
        super(level -> new SkeletonMageMinion(ASEntities.SKELETON_MAGE_MINION.get(), level));
    }

    @Override
    protected void addMinionExtras(SkeletonMageMinion minion, CastContext ctx, int alreadySpawned) {
        super.addMinionExtras(minion, ctx, alreadySpawned);
        minion.setRare(ctx.world().random.nextFloat() < 0.4f);
        int elementIndex = ctx.world().random.nextInt(SkeletonMageEntity.MAGE_ELEMENTS.length);
        if (ctx.caster() instanceof net.minecraft.world.entity.player.Player player) {
            // amulet_elemental_offense com cristal encaixado: foca o elemento SEM risco (fiel ao 1.12.2)
            if (com.koomplo.wizardry.core.integrations.ArtifactChannel.isEquipped(player, ASItems.AMULET_ELEMENTAL_OFFENSE.get())) {
                for (var artifact : com.koomplo.wizardry.core.integrations.ArtifactChannel.getEquippedArtifacts(player)) {
                    if (!artifact.is(ASItems.AMULET_ELEMENTAL_OFFENSE.get())) continue;
                    var element = com.windanesz.ancientspellcraft.item.ASArtifactEffects.socketedCrystalElement(player, artifact);
                    for (int i = 0; element != null && i < SkeletonMageEntity.MAGE_ELEMENTS.length; i++) {
                        if (SkeletonMageEntity.MAGE_ELEMENTS[i] == element) {
                            minion.setMageElement(i);
                            return;
                        }
                    }
                }
            }
            var offhand = player.getOffhandItem();
            for (int i = 0; i < SkeletonMageEntity.MAGE_ELEMENTS.length; i++) {
                if (offhand.is(com.koomplo.wizardry.api.content.util.RegistryUtils.getCrystal(SkeletonMageEntity.MAGE_ELEMENTS[i]))) {
                    elementIndex = i;
                    boolean amulet = com.koomplo.wizardry.core.integrations.ArtifactChannel.isEquipped(
                            player, ASItems.AMULET_ELEMENTAL_OFFENSE.get());
                    if (!amulet && ctx.world().random.nextInt(100) > 90) {
                        offhand.shrink(1);
                        player.playSound(net.minecraft.sounds.SoundEvents.ITEM_BREAK, 0.9F,
                                1.2F / (ctx.world().random.nextFloat() * 0.2F + 0.9F));
                        if (!ctx.world().isClientSide) {
                            player.displayClientMessage(net.minecraft.network.chat.Component.translatable(
                                    "spell.ancientspellcraft.raise_skeleton_mage.crystal.break"), true);
                        }
                    }
                    break;
                }
            }
        }
        minion.setMageElement(elementIndex);
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == ASItems.ANCIENT_SPELLCRAFT_SPELL_BOOK.get() || item == ASItems.ANCIENT_SPELLCRAFT_SCROLL.get();
    }
}
