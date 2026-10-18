package eng.cad.auto.axiom.registry;

import eng.cad.auto.axiom.AxiomMod;
import eng.cad.auto.axiom.axiomancy.Axiom;
import eng.cad.auto.axiom.axiomancy.items.AxiomShard;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AxiomMod.MODID);

    public static RegistryObject<Item> registerAxiomShard(String name, int color) {
            String itemName = name+"_shard";
            return registerItem(itemName, () -> new AxiomShard(new Item.Properties(), color));
    }

    public static <T extends Item> RegistryObject<T> registerItem(String name, Supplier<T> item){
        return ITEMS.register(name, item);
    }

    public static <T extends Block> RegistryObject<Item> registerBlockItem(String name, Supplier<T> block){
        return registerItem(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }
}
