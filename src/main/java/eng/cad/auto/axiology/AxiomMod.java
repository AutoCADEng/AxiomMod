package eng.cad.auto.axiology;

import com.mojang.logging.LogUtils;
import eng.cad.auto.axiology.items.util.RangedFluidBehaviours;
import eng.cad.auto.axiology.registry.render.BeamRegistry;
import eng.cad.auto.engine.client.colors.ClientItemColorer;
import eng.cad.auto.axiology.client.core.ClientSetup;
import eng.cad.auto.engine.client.render.BeamEffectRenderer;
import eng.cad.auto.engine.machine.multiblock.MultiblockStructure;
import eng.cad.auto.engine.network.AxiologyNetwork;
import eng.cad.auto.axiology.registry.*;
import eng.cad.auto.engine.network.BeamSyncPacket;
import eng.cad.auto.engine.network.FluidSyncPacket;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(AxiomMod.MODID)
public class AxiomMod {
    public static final String MODID = "axiology";
    public static final Logger LOGGER = LogUtils.getLogger();

    public AxiomMod(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        ForgeMod.enableMilkFluid();
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(BeamRegistry::createRegistry);
        MinecraftForge.EVENT_BUS.addListener(AxiomMod::onAddReloadListeners);
        //Registries
        FluidRegistry.FLUIDS.register(modEventBus);
        FluidRegistry.init();
        FluidRegistry.FLUIDTYPES.register(modEventBus);
        BlockRegistry.BLOCKS.register(modEventBus);
        MachineRegistry.MACHINES.register(modEventBus);
        MachineRegistry.init();
        ItemRegistry.ITEMS.register(modEventBus);
        CreativeTabRegistry.CREATIVE_TABS.register(modEventBus);
        MenuRegistry.MENUS.register(modEventBus);
        TagRegistry.register(modEventBus);
        RecipeRegistry.RECIPE_TYPES.register(modEventBus);
        RecipeRegistry.RECIPE_SERIALIZERS.register(modEventBus);
        BeamRegistry.BEAMS.register(modEventBus);

        //Client Event Hooks
        ClientSetup.init(modEventBus);
        ClientItemColorer.init(modEventBus);
        BeamEffectRenderer.init(MinecraftForge.EVENT_BUS);
        //Networking
        AxiologyNetwork.register(FluidSyncPacket.class, FluidSyncPacket::new);
        AxiologyNetwork.register(BeamSyncPacket.class, BeamSyncPacket::new);
    }

    private static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(MultiblockStructure.JsonListener.getInstance());
    }
    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(RangedFluidBehaviours::init);
    }

}
