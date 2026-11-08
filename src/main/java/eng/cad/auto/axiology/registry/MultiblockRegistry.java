package eng.cad.auto.axiology.registry;

import eng.cad.auto.axiology.core.machine.MultiblockStructure;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.function.Supplier;

public class MultiblockRegistry {
    public static HashMap<ResourceLocation, Supplier<? extends MultiblockStructure>> Structures = new HashMap<>();
}
