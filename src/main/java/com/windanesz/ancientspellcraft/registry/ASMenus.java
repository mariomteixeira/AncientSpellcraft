package com.windanesz.ancientspellcraft.registry;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.client.SphereCognizanceMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ASMenus {

    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, AncientSpellcraft.MODID);

    public static final Supplier<MenuType<SphereCognizanceMenu>> SPHERE_COGNIZANCE = MENUS.register("sphere_cognizance",
            () -> IMenuTypeExtension.create((id, inv, buf) -> new SphereCognizanceMenu(id, inv)));

    public static final Supplier<MenuType<com.windanesz.ancientspellcraft.client.ScribingDeskMenu>> SCRIBING_DESK = MENUS.register("scribing_desk",
            () -> IMenuTypeExtension.create((id, inv, buf) -> new com.windanesz.ancientspellcraft.client.ScribingDeskMenu(id, inv)));

    public static final Supplier<MenuType<com.windanesz.ancientspellcraft.client.ArcaneAnvilMenu>> ARCANE_ANVIL = MENUS.register("arcane_anvil",
            () -> IMenuTypeExtension.create((id, inv, buf) -> new com.windanesz.ancientspellcraft.client.ArcaneAnvilMenu(id, inv)));

    public static final Supplier<MenuType<com.windanesz.ancientspellcraft.client.SageLecternMenu>> SAGE_LECTERN = MENUS.register("sage_lectern",
            () -> IMenuTypeExtension.create((id, inv, buf) -> new com.windanesz.ancientspellcraft.client.SageLecternMenu(id, inv)));

    private ASMenus() {
    }
}
