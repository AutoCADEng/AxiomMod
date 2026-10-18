package eng.cad.auto.axiom.registry.custom;

import eng.cad.auto.axiom.AxiomMod;
import eng.cad.auto.axiom.common.Axiom;
import eng.cad.auto.axiom.registry.ItemRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.*;

import java.util.ArrayList;

public class AxiomRegistry {
    public static final ResourceLocation registryKey = ResourceLocation.fromNamespaceAndPath(AxiomMod.MODID,"axiom_registry");
    public static final DeferredRegister<Axiom> AXIOM_DEFERRED_REGISTER = DeferredRegister.create(registryKey, AxiomMod.MODID);
    public static final ArrayList<RegistryObject<Item>> AXIOMS = new ArrayList<>();
    public static final RegistryObject<Axiom> IGNIS = AxiomRegistry.registerElement("ignis",0xFF0000);
    public static final RegistryObject<Axiom> AER = AxiomRegistry.registerElement("aer",0xFFFF00);
    public static final RegistryObject<Axiom> AQUA = AxiomRegistry.registerElement("aqua",0x0000FF);
    public static final RegistryObject<Axiom> TERRA = AxiomRegistry.registerElement("terra",0x00FF00);

    public static <T extends Axiom> RegistryObject<Axiom> registerElement(String name, int color){
        AxiomMod.LOGGER.info("Registering axioms {}", name);
        AXIOMS.add(ItemRegistry.registerAxiomShard(name,color));
        return AXIOM_DEFERRED_REGISTER.register(name+"_axiom",
        ()-> new Axiom(name+"_axiom", color));
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        event.create(new RegistryBuilder<Axiom>().setName(registryKey));
    }
}
