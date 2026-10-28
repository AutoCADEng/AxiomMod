package eng.cad.auto.axiom;

import com.mojang.logging.LogUtils;
import eng.cad.auto.axolotyl.client.colors.ClientItemColorer;
import eng.cad.auto.axiom.client.core.ClientSetup;
import eng.cad.auto.axolotyl.machine.multiblock.MultiblockStructure;
import eng.cad.auto.axolotyl.network.FluidNetwork;
import eng.cad.auto.axiom.registry.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(AxiomMod.MODID)
public class AxiomMod {
    public static final String MODID = "axiology";
    public static final Logger LOGGER = LogUtils.getLogger();

    public AxiomMod(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        //Registries
        FluidRegistry.FLUIDS.register(modEventBus);
        FluidRegistry.init();
        FluidRegistry.FLUIDTYPES.register(modEventBus);
        BlockRegistry.BLOCKS.register(modEventBus);
        FluidNetwork.register();
        MachineRegistry.MACHINES.register(modEventBus);
        MachineRegistry.init();
        ItemRegistry.ITEMS.register(modEventBus);
        CreativeTabRegistry.CREATIVE_TABS.register(modEventBus);
        MenuRegistry.MENUS.register(modEventBus);
        TagRegistry.register(modEventBus);
        RecipeRegistry.RECIPE_TYPES.register(modEventBus);
        RecipeRegistry.RECIPE_SERIALIZERS.register(modEventBus);
        MinecraftForge.EVENT_BUS.addListener(AxiomMod::onAddReloadListeners);
        //Client Event Hooks
        ClientSetup.init(modEventBus);
        ClientItemColorer.init(modEventBus);

    }

    private static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(MultiblockStructure.JsonListener.getInstance());
    }
}
