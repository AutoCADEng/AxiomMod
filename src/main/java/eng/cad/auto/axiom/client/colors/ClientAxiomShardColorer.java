package eng.cad.auto.axiom.client.colors;

import eng.cad.auto.axiom.axiomancy.items.AxiomShard;
import eng.cad.auto.axiom.registry.custom.AxiomRegistry;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;

public class ClientAxiomShardColorer {
    public static void init(IEventBus modEventBus) {
        modEventBus.addListener(ClientAxiomShardColorer::setup);
    }
    private static void setup(RegisterColorHandlersEvent.Item event) {
        event.register(
                (stack, tintIndex) -> {
                    if (tintIndex != 0) {
                        return 0xFFFFFF;
                    }

                    if (stack.getItem() instanceof AxiomShard shard) {
                        return shard.getColor();
                    }

                    return 0xFFFFFF;
                }, AxiomRegistry.AXIOMS.stream().map(RegistryObject::get).toArray(ItemLike[]::new)
        );
    }
}
