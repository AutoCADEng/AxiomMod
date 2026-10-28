package eng.cad.auto.axolotyl.client.colors;

import eng.cad.auto.axiom.items.AxiomShard;
import eng.cad.auto.axiom.registry.FluidRegistry;
import eng.cad.auto.axolotyl.item.CustomBucket;
import eng.cad.auto.axolotyl.fluid.FluidHolder;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;

public class ClientItemColorer {
    public static void init(IEventBus modEventBus) {
        modEventBus.addListener(ClientItemColorer::setup);
    }
    private static void setup(RegisterColorHandlersEvent.Item event) {
        //Shard Shader
//        event.register(
//                (stack, tintIndex) -> {
//
//                    if (stack.getItem() instanceof AxiomShard shard) {
//                        return shard.getColor();
//                    }
//
//                    return 0xFFFFFF;
//                }, AxiomRegistry.AXIOM_CRYSTALS.stream().map(RegistryObject::get).toArray(ItemLike[]::new)
//        );

        //Bucket Shader
        event.register(
                (stack, tintIndex) -> {

                    if (stack.getItem() instanceof CustomBucket bucket) {
                        if (tintIndex == 0) return 0xFFFFFF;
                        if (tintIndex == 1) return bucket.getColor();
                    }
                    return 0xFFFFFF;
                }, FluidRegistry.Fluid_Holders.values().stream().map(FluidHolder::getBucket).map(RegistryObject::get).toArray(ItemLike[]::new)
        );
//        event.register(
//                (stack, tintIndex) -> {
//
//                    if (stack.getItem() instanceof CustomBucket bucket) {
//                        if (tintIndex == 0) return 0xFFFFFF;
//                        if (tintIndex == 1) return bucket.getColor();
//                    }
//                    return 0xFFFFFF;
//                }, AxiomRegistry.AXIOM_FLUIDS.stream().map(FluidHolder::getBucket).map(RegistryObject::get).toArray(ItemLike[]::new)
//        );
    }
}
