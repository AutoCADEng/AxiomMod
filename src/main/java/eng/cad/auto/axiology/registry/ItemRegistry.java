package eng.cad.auto.axiology.registry;

import eng.cad.auto.axiology.AxiomMod;
import eng.cad.auto.axiology.items.AxiomShard;
import eng.cad.auto.axiology.items.SuperSoaker;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AxiomMod.MODID);

    public static final RegistryObject<Item> TREATED_FABRIC = registerItem("treated_fabric", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TREATED_BARK = registerItem("treated_bark", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> ETCHED_IRON_INGOT = registerItem("etched_iron_ingot", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ETCHED_IRON_NUGGET = registerItem("etched_iron_nugget", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> FLUXED_INGOT = registerItem("fluxed_ingot", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BLAZING_INGOT = registerItem("blazing_ingot", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CURSED_INGOT = registerItem("cursed_ingot", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> MUNDANE_CORE = registerItem("mundane_core", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ARCANE_CORE = registerItem("arcane_core", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> MUNDANE_MACHINE_FRAME = registerItem("mundane_machine_frame", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ARCANE_MACHINE_FRAME  = registerItem("arcane_machine_frame", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> SUPER_SOAKER = registerItem("super_soaker", () -> new SuperSoaker(new Item.Properties(),4000));
    public static RegistryObject<Item> registerAxiomShard(String name, int color) {
            String itemName = name + "_shard";
            return registerItem(itemName, () -> new AxiomShard(new Item.Properties(), color));
    }

    public static <T extends Item> RegistryObject<T> registerItem(String name, Supplier<T> item){
        return ITEMS.register(name, item);
    }

    public static <T extends Block> RegistryObject<Item> registerBlockItem(String name, Supplier<T> block){
        return registerItem(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }
}