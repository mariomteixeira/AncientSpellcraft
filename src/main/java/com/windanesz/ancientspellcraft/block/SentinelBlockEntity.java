package com.windanesz.ancientspellcraft.block;

import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.spell.internal.EntityCastContext;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.setup.registries.Spells;
import com.windanesz.ancientspellcraft.entity.living.SpellCasterEntity;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.registry.ASEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.UUID;

/**
 * Torreta das vaults (1.12.2 TileSentinel): casta magic_missile em players a até 5 blocos
 * (sentinelas de estrutura não têm dono - atacam jogadores). A vida fica num proxy invisível
 * atacável ({@link SpellCasterEntity}); proxy morto -> bloco some. Iron = 5 de vida, diamond = 20.
 * Desvio documentado: sem variantes gold/large_iron (templates só usam estas duas) e som ambiente
 * vanilla (beacon) no lugar do som próprio do pack.
 */
public class SentinelBlockEntity extends BlockEntity {

    private static final double ATTACK_RANGE = 5.0;
    private static final int CAST_FREQUENCY = 60;

    private int ticksUntilNextSpell = CAST_FREQUENCY;
    private float casterHealth = -1; // -1 = ainda não inicializada pelo estado do bloco
    private UUID proxyUuid;
    private SpellCasterEntity proxy;

    public float crystalRotation;
    public float crystalRotationPrev;

    public SentinelBlockEntity(BlockPos pos, BlockState state) {
        super(ASBlocks.SENTINEL_BLOCK_ENTITY.get(), pos, state);
    }

    public boolean isProxy(SpellCasterEntity entity) {
        return entity == proxy;
    }

    private float maxHealth() {
        return getBlockState().getBlock() == ASBlocks.SENTINEL_BLOCK_DIAMOND.get() ? 20.0F : 5.0F;
    }

    public void clientTick() {
        crystalRotationPrev = crystalRotation;
        crystalRotation += 0.05F;
    }

    public void serverTick() {
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (casterHealth < 0) casterHealth = maxHealth();

        if (serverLevel.getGameTime() % 32 == 0) {
            serverLevel.playSound(null, worldPosition, SoundEvents.BEACON_AMBIENT, SoundSource.NEUTRAL, 0.6F, 1.4F);
        }

        if (proxy == null || !proxy.isAlive()) {
            if (casterHealth <= 0) {
                serverLevel.destroyBlock(worldPosition, false);
                return;
            }
            Entity existing = proxyUuid == null ? null : serverLevel.getEntity(proxyUuid);
            if (existing instanceof SpellCasterEntity caster && caster.isAlive()) {
                proxy = caster;
            } else {
                spawnProxy(serverLevel);
            }
        } else {
            casterHealth = proxy.getHealth();
        }

        if (ticksUntilNextSpell > 0) {
            ticksUntilNextSpell--;
            return;
        }
        if (proxy == null) return;

        List<Player> targets = serverLevel.getEntitiesOfClass(Player.class,
                new AABB(worldPosition).inflate(ATTACK_RANGE),
                p -> p.isAlive() && !p.isCreative() && !p.isSpectator());
        if (targets.isEmpty()) return;

        Spell spell = Spells.MAGIC_MISSILE;
        if (spell.cast(new EntityCastContext(serverLevel, proxy, InteractionHand.MAIN_HAND, 0, targets.get(0), new SpellModifiers()))) {
            ticksUntilNextSpell = CAST_FREQUENCY;
        }
    }

    private void spawnProxy(ServerLevel serverLevel) {
        SpellCasterEntity caster = ASEntities.SPELL_CASTER.get().create(serverLevel);
        if (caster == null) return;
        caster.setPos(worldPosition.getX() + 0.5, worldPosition.getY(), worldPosition.getZ() + 0.5);
        var attribute = caster.getAttribute(Attributes.MAX_HEALTH);
        if (attribute != null) attribute.setBaseValue(maxHealth());
        caster.setHealth(casterHealth);
        serverLevel.addFreshEntity(caster);
        proxy = caster;
        proxyUuid = caster.getUUID();
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("TicksUntilNextSpell", ticksUntilNextSpell);
        tag.putFloat("CasterHealth", casterHealth);
        if (proxyUuid != null) tag.putUUID("ProxyUuid", proxyUuid);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        ticksUntilNextSpell = tag.getInt("TicksUntilNextSpell");
        if (tag.contains("CasterHealth")) casterHealth = tag.getFloat("CasterHealth");
        if (tag.hasUUID("ProxyUuid")) proxyUuid = tag.getUUID("ProxyUuid");
    }
}
