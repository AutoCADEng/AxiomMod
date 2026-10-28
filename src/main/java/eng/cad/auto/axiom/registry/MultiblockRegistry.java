package eng.cad.auto.axiom.registry;

import eng.cad.auto.axolotyl.machine.multiblock.MultiblockStructure;
import net.minecraft.resources.ResourceLocation;
import java.util.HashMap;
import java.util.function.Supplier;

public class MultiblockRegistry {
    public static HashMap<ResourceLocation, Supplier<? extends MultiblockStructure>> Structures = new HashMap<>();
}
