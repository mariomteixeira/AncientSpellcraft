package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.api.content.spell.internal.CastContext;
import com.koomplo.wizardry.api.content.util.RegistryUtils;
import com.koomplo.wizardry.content.item.armor.WizardArmorType;
import com.koomplo.wizardry.core.platform.Services;
import com.koomplo.wizardry.setup.registries.Spells;
import com.koomplo.wizardry.setup.registries.client.EBParticles;
import com.windanesz.ancientspellcraft.block.SageLecternBlock;
import com.windanesz.ancientspellcraft.block.SageLecternBlockEntity;
import com.windanesz.ancientspellcraft.item.MysticSpellBookItem;
import com.windanesz.ancientspellcraft.registry.ASAttachments;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Perfect Theory (1.12.2 PerfectTheory, SAGE): canaliza 5s mirando um Sage Lectern com um mystic
 * spell book VAZIO — consome 1 theory point (do experiment) e escreve perfect_theory_spell no
 * livro. Desvio: o efeito engarrafado é o último experimento do PLAYER_DATA na hora do CAST da
 * spell resultante (o 1.12.2 copiava o NBT para o livro/tomo).
 */
public class PerfectTheoryLecternSpell extends ASRaySpell implements ClassSpell {

    private static final int CHANNEL_TICKS = 100;

    @Override
    public boolean isInstantCast() {
        return false;
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        return false;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        if (!(ctx.caster() instanceof Player caster)) return false;
        BlockPos pos = blockHit.getBlockPos();
        if (!(ctx.world().getBlockState(pos).getBlock() instanceof SageLecternBlock)) {
            if (!ctx.world().isClientSide && ctx.castingTicks() == 0) {
                caster.displayClientMessage(Component.translatable("generic.ancientspellcraft.spell_lectern_interact.no_lectern"), true);
            }
            return false;
        }

        if (ctx.castingTicks() < CHANNEL_TICKS) {
            if (ctx.world().isClientSide) {
                for (int i = 0; i < ctx.castingTicks() / 20 + 1; i++) {
                    ParticleBuilder.create(EBParticles.DUST)
                            .pos(pos.getX() + ctx.world().random.nextFloat(), pos.getY() + 1, pos.getZ() + ctx.world().random.nextFloat())
                            .velocity(0, 0.05 + ctx.world().random.nextFloat() * 0.1, 0)
                            .time(40).spawn(ctx.world());
                }
            }
            return true;
        }
        if (ctx.world().isClientSide) return true;

        if (!(ctx.world().getBlockEntity(pos) instanceof SageLecternBlockEntity lectern)) return false;
        ItemStack book = lectern.getBook();
        if (!(book.getItem() instanceof MysticSpellBookItem) || RegistryUtils.getSpell(book) != Spells.NONE) {
            caster.displayClientMessage(Component.translatable("spell.ancientspellcraft.perfect_theory.no_valid_book"), true);
            return false;
        }
        var tag = caster.getData(ASAttachments.PLAYER_DATA);
        if (tag.getInt(ExperimentSpell.THEORY_POINTS_TAG) < 1) {
            caster.displayClientMessage(Component.translatable("generic.ancientspellcraft.spell_lectern_interact.no_theory_points"), true);
            return false;
        }

        var theorySpell = Services.REGISTRY_UTIL.getSpell(
                ResourceLocation.fromNamespaceAndPath("ancientspellcraft", "perfect_theory_spell"));
        if (theorySpell == null) return false;
        RegistryUtils.setSpell(book, theorySpell);
        lectern.setItem(SageLecternBlockEntity.BOOK, book);

        tag.putInt(ExperimentSpell.THEORY_POINTS_TAG, tag.getInt(ExperimentSpell.THEORY_POINTS_TAG) - 1);
        caster.setData(ASAttachments.PLAYER_DATA, tag);
        caster.releaseUsingItem();
        return true;
    }

    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        return false;
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
