package eng.cad.auto.axiom.client;

import eng.cad.auto.axiom.registry.FluidRegistry;
import eng.cad.auto.axiom.registry.MenuRegistry;
import eng.cad.auto.axiom.ui.AlembicScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;

public class ClientSetup {
    public static void init(IEventBus modEventBus) {
        modEventBus.addListener(ClientSetup::clientSetup);
    }

    private static void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(MenuRegistry.ALEMBIC_MENU.get(), AlembicScreen::new);
        });
        for (RegistryObject<? extends Fluid> fluid:FluidRegistry.FLUIDLIST) {
            event.enqueueWork(() ->
                    ItemBlockRenderTypes.setRenderLayer(fluid.get(), RenderType.translucent())
            );
        }
    }

}
