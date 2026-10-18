package eng.cad.auto.axiom.client.subscribers;

import eng.cad.auto.axiom.registry.MenuRegistry;
import eng.cad.auto.axiom.ui.AlembicScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientSetup {
    public static void init(IEventBus modEventBus) {
        modEventBus.addListener(ClientSetup::clientSetup);
    }

    private static void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(MenuRegistry.ALEMBIC_MENU.get(), AlembicScreen::new);
        });
    }
}
