package eng.cad.auto.axiology.registry.render;

import eng.cad.auto.axiology.AxiomMod;
import eng.cad.auto.axiology.client.effects.FluidBeamEffect;
import eng.cad.auto.axiology.client.effects.InfusionBeamEffect;
import eng.cad.auto.engine.client.render.AbstractBeamEffect;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;


public class BeamRegistry {

    public static final ResourceKey<Registry<AbstractBeamEffect<?>>> BEAM_REGISTRY_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(AxiomMod.MODID, "beam_registry"));

    public static final DeferredRegister<AbstractBeamEffect<?>> BEAMS = DeferredRegister.create(BEAM_REGISTRY_KEY, AxiomMod.MODID);

    public static final RegistryObject<InfusionBeamEffect> INFUSION = BEAMS.register("infusion_effect", InfusionBeamEffect::new);
    public static final RegistryObject<FluidBeamEffect> FLUID_RAY = BEAMS.register("fluid_beam_effect", FluidBeamEffect::new);

    public static void createRegistry(NewRegistryEvent event) {
        event.create(
                new RegistryBuilder<AbstractBeamEffect<?>>()
                        .setName(BEAM_REGISTRY_KEY.location())
                        .disableSaving()
        );
    }
}