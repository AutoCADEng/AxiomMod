package eng.cad.auto.axiom;

import com.mojang.logging.LogUtils;
import eng.cad.auto.axiom.client.colors.ClientItemColorer;
import eng.cad.auto.axiom.client.ClientSetup;
import eng.cad.auto.axiom.registry.*;
import eng.cad.auto.axiom.registry.custom.AxiomList;
import eng.cad.auto.axiom.registry.custom.AxiomRegistry;
import eng.cad.auto.axiom.registry.recipes.AlembicRecipeRegistry;
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
        AxiomRegistry.AXIOM_DEFERRED_REGISTER.register(modEventBus);
        AxiomList.init();
        BlockRegistry.BLOCKS.register(modEventBus);
        BlockEntityRegistry.BLOCKENTITYTYPES.register(modEventBus);
        ItemRegistry.ITEMS.register(modEventBus);
        MenuRegistry.MENUS.register(modEventBus);
        FluidRegistry.FLUIDS.register(modEventBus);
        FluidRegistry.FLUIDTYPES.register(modEventBus);
        AlembicRecipeRegistry.SERIALIZER.register(modEventBus);
        //Client Event Hooks
        ClientSetup.init(modEventBus);
        ClientItemColorer.init(modEventBus);

    }
}
