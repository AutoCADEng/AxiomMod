package eng.cad.auto.axiom.client.core;

import eng.cad.auto.axiom.fluids.*;
import eng.cad.auto.axiom.registry.CreativeTabRegistry;
import eng.cad.auto.axiom.registry.ItemRegistry;
import eng.cad.auto.axolotyl.machine.core.MachineCore;
import eng.cad.auto.axolotyl.machine.core.ui.GenericUI;
import eng.cad.auto.axiom.registry.FluidRegistry;
import eng.cad.auto.axiom.registry.MachineRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
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

        for (MachineCore machine: MachineRegistry.MACHINELIST.values()) {
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
            for (MachineCore machine: MachineRegistry.MACHINELIST.values()){
                event.accept(machine.blockItem);
            }
        }

        if (event.getTab() == CreativeTabRegistry.AXIOLOGY_BUCKETS.get()){
            //Buckets
            event.accept(FluidRegistry.Fluid_Holders.get(Blazewater.name).BUCKET);
            event.accept(FluidRegistry.Fluid_Holders.get(Fluxfluid.name).BUCKET);
            event.accept(FluidRegistry.Fluid_Holders.get(Deathwater.name).BUCKET);

            event.accept(FluidRegistry.Fluid_Holders.get(Ethanol.name).BUCKET);
            event.accept(FluidRegistry.Fluid_Holders.get(Ethanol.Dilute.name).BUCKET);

            event.accept(FluidRegistry.Fluid_Holders.get(NitricAcid.name).BUCKET);
            event.accept(FluidRegistry.Fluid_Holders.get(NitricAcid.Dilute.name).BUCKET);
            event.accept(FluidRegistry.Fluid_Holders.get(NitricAcid.Nital.name).BUCKET);

//            //Axiom Buckets
//            for (FluidHolder axiomFluid: AxiomRegistry.AXIOM_FLUIDS){
//                event.accept(axiomFluid.BUCKET);
//            }
        }

//        if (event.getTab() == CreativeTabRegistry.AXIOLOGY_SHARD.get()){
//            //Axiom Shards
//            for (RegistryObject<Item> shard: AxiomRegistry.AXIOM_CRYSTALS){
//                event.accept(shard);
//            }
//        }
    }
}
