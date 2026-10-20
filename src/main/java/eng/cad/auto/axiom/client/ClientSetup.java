package eng.cad.auto.axiom.client;

import eng.cad.auto.axiom.common.machine.core.GenericBlockEntity;
import eng.cad.auto.axiom.common.machine.core.GenericMachine;
import eng.cad.auto.axiom.common.machine.core.GenericUI;
import eng.cad.auto.axiom.registry.FluidRegistry;
import eng.cad.auto.axiom.registry.MachineRegistry;
import net.minecraft.client.gui.GuiGraphics;
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

        for (GenericMachine machine: MachineRegistry.MACHINELIST.values()) {
            event.enqueueWork(() ->
                    MenuScreens.<GenericUI.Menu, GenericUI.Screen>register(
                        machine.ui.menu.get(),
                        (menu, inventory, title) ->
                                machine.ui.new Screen(
                                        machine.config.name(),
                                        menu,
                                        inventory,
                                        title
                                )
                    )
            );
        }

        for (RegistryObject<? extends Fluid> fluid:FluidRegistry.FLUIDLIST) {
            event.enqueueWork(() ->
                    ItemBlockRenderTypes.setRenderLayer(fluid.get(), RenderType.translucent())
            );
        }
    }

}
