package eng.cad.auto.axiom;

import com.mojang.logging.LogUtils;
import eng.cad.auto.axiom.client.colors.ClientItemColorer;
import eng.cad.auto.axiom.client.ClientSetup;
import eng.cad.auto.axiom.network.FluidNetwork;
import eng.cad.auto.axiom.registry.*;
import eng.cad.auto.axiom.registry.custom.AxiomRegistry;
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
        //Registry Event Hooks
        modEventBus.register(AxiomRegistry.class);
        //Registries
        FluidRegistry.FLUIDS.register(modEventBus);
        FluidRegistry.FLUIDTYPES.register(modEventBus);
        AxiomRegistry.AXIOM_DEFERRED_REGISTER.register(modEventBus);
        AxiomList.init();
        BlockRegistry.BLOCKS.register(modEventBus);
        FluidNetwork.register();
        MachineRegistry.MACHINES.register(modEventBus);
        MachineRegistry.init();
        ItemRegistry.ITEMS.register(modEventBus);
        MenuRegistry.MENUS.register(modEventBus);
        TagRegistry.register(modEventBus);
        RecipeRegistry.RECIPE_TYPES.register(modEventBus);
        RecipeRegistry.RECIPE_SERIALIZERS.register(modEventBus);
        //Client Event Hooks
        ClientSetup.init(modEventBus);
        ClientItemColorer.init(modEventBus);

    }
}
