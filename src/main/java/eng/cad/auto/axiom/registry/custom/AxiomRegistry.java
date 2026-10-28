package eng.cad.auto.axiom.registry.custom;

import eng.cad.auto.axiom.AxiomMod;
import eng.cad.auto.axiom.Axiom;
import eng.cad.auto.axolotyl.fluid.FluidHolder;
import eng.cad.auto.axiom.registry.ItemRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.*;

import java.util.ArrayList;
import java.util.HashMap;

public class AxiomRegistry {
    public static final ResourceLocation registryKey = ResourceLocation.fromNamespaceAndPath(AxiomMod.MODID,"axiom_registry");
    public static final DeferredRegister<Axiom> AXIOM_DEFERRED_REGISTER = DeferredRegister.create(registryKey, AxiomMod.MODID);
    public static final ArrayList<RegistryObject<Item>> AXIOM_CRYSTALS = new ArrayList<>();
    public static final ArrayList<FluidHolder> AXIOM_FLUIDS = new ArrayList<>();
    public static HashMap<String, Axiom> axiomList = new HashMap<>();
    public static <T extends Axiom> RegistryObject<Axiom> registerElement(Axiom axiom){
        AxiomMod.LOGGER.info("Registering axioms {}", axiom.name());
        AXIOM_CRYSTALS.add(ItemRegistry.registerAxiomShard(axiom.name(), axiom.color()));
        AXIOM_FLUIDS.add(new FluidHolder(axiom.name(), axiom.color(), MapColor.COLOR_BLACK));
        return AXIOM_DEFERRED_REGISTER.register(axiom.name()+"_axiom",
        ()-> new Axiom(axiom.name()+"_axiom", axiom.color(), axiom.type()));
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        event.create(new RegistryBuilder<Axiom>().setName(registryKey));
    }
}
