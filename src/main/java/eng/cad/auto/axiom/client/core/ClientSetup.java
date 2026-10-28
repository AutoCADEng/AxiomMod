package eng.cad.auto.axiom.client.core;

import eng.cad.auto.axiom.registry.CreativeTabRegistry;
import eng.cad.auto.axiom.registry.ItemRegistry;
import eng.cad.auto.axiom.registry.custom.AxiomRegistry;
import eng.cad.auto.axolotyl.fluid.FluidHolder;
import eng.cad.auto.axolotyl.machine.core.MachineHolder;
import eng.cad.auto.axolotyl.machine.core.ui.GenericUI;
import eng.cad.auto.axiom.registry.FluidRegistry;
import eng.cad.auto.axiom.registry.MachineRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;

public class ClientSetup {
    public static void init(IEventBus modEventBus) {
        modEventBus.addListener(ClientSetup::clientSetup);
        modEventBus.addListener(ClientSetup::creativeTabSetup);
    }

    private static void clientSetup(final FMLClientSetupEvent event) {

        for (MachineHolder machine: MachineRegistry.MACHINELIST.values()) {
            if (machine.config.noGui()) continue;
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

        for (RegistryObject<? extends Fluid> fluid:FluidRegistry.FLUIDLIST.values()) {
            event.enqueueWork(() ->
                    ItemBlockRenderTypes.setRenderLayer(fluid.get(), RenderType.translucent())
            );
        }
    }

    private static void creativeTabSetup(final BuildCreativeModeTabContentsEvent event){
        if (event.getTab() == CreativeTabRegistry.AXIOLOGY_TAB.get()){
            //Crafting items
            event.accept(ItemRegistry.BLAZING_INGOT);
            event.accept(ItemRegistry.FLUXED_INGOT);
            event.accept(ItemRegistry.ETCHED_IRON_INGOT);
            event.accept(ItemRegistry.TREATED_BARK);
            event.accept(ItemRegistry.ETCHED_IRON_NUGGET);

            //Machine Blocks
            for (MachineHolder machine: MachineRegistry.MACHINELIST.values()){
                event.accept(machine.blockItem);
            }
        }

        if (event.getTab() == CreativeTabRegistry.AXIOLOGY_BUCKETS.get()){
            //Buckets
            event.accept(FluidRegistry.BLAZEWATER.BUCKET);
            event.accept(FluidRegistry.DEATHWATER.BUCKET);
            event.accept(FluidRegistry.FLUXFLUID.BUCKET);

            event.accept(FluidRegistry.ETHANOL.BUCKET);
            event.accept(FluidRegistry.DILUTE_ETHANOL.BUCKET);

            event.accept(FluidRegistry.NITRIC_ACID.BUCKET);
            event.accept(FluidRegistry.DILUTE_NITRIC_ACID.BUCKET);
            event.accept(FluidRegistry.NITAL.BUCKET);

            //Axiom Buckets
            for (FluidHolder axiomFluid: AxiomRegistry.AXIOM_FLUIDS){
                event.accept(axiomFluid.BUCKET);
            }
        }

        if (event.getTab() == CreativeTabRegistry.AXIOLOGY_SHARD.get()){
            //Axiom Shards
            for (RegistryObject<Item> shard: AxiomRegistry.AXIOM_CRYSTALS){
                event.accept(shard);
            }
        }
    }
}
