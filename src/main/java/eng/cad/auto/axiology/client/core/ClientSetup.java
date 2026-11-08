package eng.cad.auto.axiology.client.core;

import eng.cad.auto.axiology.core.MachineUI;
import eng.cad.auto.axiology.core.machine.MachineRegistryObject;
import eng.cad.auto.axiology.fluids.*;
import eng.cad.auto.axiology.machines.ChemicalReactor;
import eng.cad.auto.axiology.machines.Fermenter;
import eng.cad.auto.axiology.registry.*;
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
        //Machine UIs
        event.enqueueWork(() -> MenuScreens.register(MenuRegistry.CHEMICAL_REACTOR.MENU_TYPE.get(), (menu, inv, title) -> MenuRegistry.CHEMICAL_REACTOR.createScreen(ChemicalReactor.name, menu, inv, title)));
        event.enqueueWork(() -> MenuScreens.register(MenuRegistry.FERMENTER.MENU_TYPE.get(), (menu, inv, title) -> MenuRegistry.FERMENTER.createScreen(Fermenter.name, menu, inv, title)));

        // Fluid Renderers
        for (RegistryObject<? extends Fluid> fluid : FluidRegistry.FLUIDLIST.values()) {
            event.enqueueWork(() ->
                    ItemBlockRenderTypes.setRenderLayer(fluid.get(), RenderType.translucent())
            );
        }
    }

    private static void creativeTabSetup(final BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == CreativeTabRegistry.AXIOLOGY_TAB.get()) {
            //Crafting items
            event.accept(ItemRegistry.BLAZING_INGOT);
            event.accept(ItemRegistry.FLUXED_INGOT);
            event.accept(ItemRegistry.ETCHED_IRON_INGOT);
            event.accept(ItemRegistry.TREATED_BARK);
            event.accept(ItemRegistry.ETCHED_IRON_NUGGET);
            event.accept(ItemRegistry.TREATED_FABRIC);
            event.accept(ItemRegistry.INNERT_ARCANE_CORE);
            event.accept(ItemRegistry.ARCANE_CORE);
            event.accept(ItemRegistry.ARCANE_MACHINE_FRAME);
            event.accept(ItemRegistry.MUNDANE_CORE);
            event.accept(ItemRegistry.MUNDANE_MACHINE_FRAME);
            event.accept(ItemRegistry.CURSED_INGOT);

            event.accept(ItemRegistry.SUPER_SOAKER);

            event.accept(ItemRegistry.CURSED_PICKAXE);
            event.accept(ItemRegistry.CURSED_AXE);
            event.accept(ItemRegistry.CURSED_SHOVEL);
            event.accept(ItemRegistry.CURSED_HOE);
            event.accept(ItemRegistry.CURSED_SWORD);
            event.accept(ItemRegistry.CURSED_SHEARS);

            event.accept(ItemRegistry.FLUXED_PICKAXE);
            event.accept(ItemRegistry.FLUXED_AXE);
            event.accept(ItemRegistry.FLUXED_SHOVEL);
            event.accept(ItemRegistry.FLUXED_HOE);
            event.accept(ItemRegistry.FLUXED_SWORD);
            event.accept(ItemRegistry.FLUXED_SHEARS);

            event.accept(ItemRegistry.BLAZING_PICKAXE);
            event.accept(ItemRegistry.BLAZING_AXE);
            event.accept(ItemRegistry.BLAZING_SHOVEL);
            event.accept(ItemRegistry.BLAZING_HOE);
            event.accept(ItemRegistry.BLAZING_SWORD);
            event.accept(ItemRegistry.BLAZING_SHEARS);

            event.accept(ItemRegistry.ETCHED_PICKAXE);
            event.accept(ItemRegistry.ETCHED_AXE);
            event.accept(ItemRegistry.ETCHED_SHOVEL);
            event.accept(ItemRegistry.ETCHED_HOE);
            event.accept(ItemRegistry.ETCHED_SWORD);
            event.accept(ItemRegistry.ETCHED_SHEARS);

            //Machine Blocks
            for (MachineRegistryObject machine : MachineRegistry.MACHINELIST.values()) {
                event.accept(machine.blockItem);
            }
        }

        if (event.getTab() == CreativeTabRegistry.AXIOLOGY_BUCKETS.get()) {
            //Buckets
            event.accept(FluidRegistry.Fluid_Holders.get(Blazewater.name).BUCKET);
            event.accept(FluidRegistry.Fluid_Holders.get(Fluxfluid.name).BUCKET);
            event.accept(FluidRegistry.Fluid_Holders.get(Cursewater.name).BUCKET);

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
