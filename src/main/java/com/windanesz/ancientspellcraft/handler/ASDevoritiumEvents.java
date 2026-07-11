package com.windanesz.ancientspellcraft.handler;

import com.koomplo.wizardry.api.content.entity.construct.MagicConstructEntity;
import com.windanesz.ancientspellcraft.material.IDevoritium;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

/** Construct de magia que entra no mundo sobre bloco de devoritium expira em 20t (1.12.2 IDevoritium). */
public final class ASDevoritiumEvents {

    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof MagicConstructEntity construct
                && event.getLevel().getBlockState(construct.blockPosition().below()).getBlock() instanceof IDevoritium) {
            construct.lifetime = 20;
        }
    }

    private ASDevoritiumEvents() {
    }
}
