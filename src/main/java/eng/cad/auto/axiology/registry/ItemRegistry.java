package eng.cad.auto.axiology.registry;

import eng.cad.auto.axiology.AxiomMod;
import eng.cad.auto.axiology.items.AxiomShard;
import eng.cad.auto.axiology.items.toolsets.BlazingTools;
import eng.cad.auto.axiology.items.toolsets.CursedTools;
import eng.cad.auto.axiology.items.SuperSoaker;
import eng.cad.auto.axiology.core.item.ToolTiers;
import eng.cad.auto.axiology.items.toolsets.EtchedTools;
import eng.cad.auto.axiology.items.toolsets.FluxedTools;
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
    public static final RegistryObject<Item> INNERT_ARCANE_CORE = registerItem("innert_arcane_core", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ARCANE_CORE = registerItem("arcane_core", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> MUNDANE_MACHINE_FRAME = registerItem("mundane_machine_frame", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ARCANE_MACHINE_FRAME  = registerItem("arcane_machine_frame", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> SUPER_SOAKER = registerItem("super_soaker", () -> new SuperSoaker(new Item.Properties(),4000));

    public static final RegistryObject<CursedTools.Pickaxe> CURSED_PICKAXE = registerItem(CursedTools.name+"_pickaxe", () -> new CursedTools.Pickaxe(ToolTiers.CURSED, 1, -2.8f, new Item.Properties()));
    public static final RegistryObject<CursedTools.Axe> CURSED_AXE = registerItem(CursedTools.name+"_axe", () -> new CursedTools.Axe(ToolTiers.CURSED, 5, -3.0f, new Item.Properties()));
    public static final RegistryObject<CursedTools.Shovel> CURSED_SHOVEL = registerItem(CursedTools.name+"_shovel", () -> new CursedTools.Shovel(ToolTiers.CURSED, 1, -3.0f, new Item.Properties()));
    public static final RegistryObject<CursedTools.Hoe> CURSED_HOE = registerItem(CursedTools.name+"_hoe", () -> new CursedTools.Hoe(ToolTiers.CURSED, -3, 0.0f, new Item.Properties()));
    public static final RegistryObject<CursedTools.Sword> CURSED_SWORD = registerItem(CursedTools.name+"_sword", () -> new CursedTools.Sword(ToolTiers.CURSED, 3, -2.4f, new Item.Properties()));
    public static final RegistryObject<CursedTools.Shears> CURSED_SHEARS = registerItem(CursedTools.name+"_shears", () -> new CursedTools.Shears(new Item.Properties().durability(ToolTiers.CURSED.getUses())));

    public static final RegistryObject<FluxedTools.Pickaxe> FLUXED_PICKAXE = registerItem(FluxedTools.name+"_pickaxe", () -> new FluxedTools.Pickaxe(ToolTiers.FLUXED, 1, -2.8f, new Item.Properties()));
    public static final RegistryObject<FluxedTools.Axe> FLUXED_AXE = registerItem(FluxedTools.name+"_axe", () -> new FluxedTools.Axe(ToolTiers.FLUXED, 5, -3.0f, new Item.Properties()));
    public static final RegistryObject<FluxedTools.Shovel> FLUXED_SHOVEL = registerItem(FluxedTools.name+"_shovel", () -> new FluxedTools.Shovel(ToolTiers.FLUXED, 1, -3.0f, new Item.Properties()));
    public static final RegistryObject<FluxedTools.Hoe> FLUXED_HOE = registerItem(FluxedTools.name+"_hoe", () -> new FluxedTools.Hoe(ToolTiers.FLUXED, -3, 0.0f, new Item.Properties()));
    public static final RegistryObject<FluxedTools.Sword> FLUXED_SWORD = registerItem(FluxedTools.name+"_sword", () -> new FluxedTools.Sword(ToolTiers.FLUXED, 3, -2.4f, new Item.Properties()));
    public static final RegistryObject<FluxedTools.Shears> FLUXED_SHEARS = registerItem(FluxedTools.name+"_shears", () -> new FluxedTools.Shears(new Item.Properties().durability(ToolTiers.FLUXED.getUses())));

    public static final RegistryObject<BlazingTools.Pickaxe> BLAZING_PICKAXE = registerItem(BlazingTools.name+"_pickaxe", () -> new BlazingTools.Pickaxe(ToolTiers.BLAZING, 1, -2.8f, new Item.Properties()));
    public static final RegistryObject<BlazingTools.Axe> BLAZING_AXE = registerItem(BlazingTools.name+"_axe", () -> new BlazingTools.Axe(ToolTiers.BLAZING, 5, -3.0f, new Item.Properties()));
    public static final RegistryObject<BlazingTools.Shovel> BLAZING_SHOVEL = registerItem(BlazingTools.name+"_shovel", () -> new BlazingTools.Shovel(ToolTiers.BLAZING, 1, -3.0f, new Item.Properties()));
    public static final RegistryObject<BlazingTools.Hoe> BLAZING_HOE = registerItem(BlazingTools.name+"_hoe", () -> new BlazingTools.Hoe(ToolTiers.BLAZING, -3, 0.0f, new Item.Properties()));
    public static final RegistryObject<BlazingTools.Sword> BLAZING_SWORD = registerItem(BlazingTools.name+"_sword", () -> new BlazingTools.Sword(ToolTiers.BLAZING, 3, -2.4f, new Item.Properties()));
    public static final RegistryObject<BlazingTools.Shears> BLAZING_SHEARS = registerItem(BlazingTools.name+"_shears", () -> new BlazingTools.Shears(new Item.Properties().durability(ToolTiers.BLAZING.getUses())));

    public static final RegistryObject<EtchedTools.Pickaxe> ETCHED_PICKAXE = registerItem(EtchedTools.name+"_pickaxe", () -> new EtchedTools.Pickaxe(ToolTiers.ETCHED, 1, -2.8f, new Item.Properties()));
    public static final RegistryObject<EtchedTools.Axe> ETCHED_AXE = registerItem(EtchedTools.name+"_axe", () -> new EtchedTools.Axe(ToolTiers.ETCHED, 5, -3.0f, new Item.Properties()));
    public static final RegistryObject<EtchedTools.Shovel> ETCHED_SHOVEL = registerItem(EtchedTools.name+"_shovel", () -> new EtchedTools.Shovel(ToolTiers.ETCHED, 1, -3.0f, new Item.Properties()));
    public static final RegistryObject<EtchedTools.Hoe> ETCHED_HOE = registerItem(EtchedTools.name+"_hoe", () -> new EtchedTools.Hoe(ToolTiers.ETCHED, -3, 0.0f, new Item.Properties()));
    public static final RegistryObject<EtchedTools.Sword> ETCHED_SWORD = registerItem(EtchedTools.name+"_sword", () -> new EtchedTools.Sword(ToolTiers.ETCHED, 3, -2.4f, new Item.Properties()));
    public static final RegistryObject<EtchedTools.Shears> ETCHED_SHEARS = registerItem(EtchedTools.name+"_shears", () -> new EtchedTools.Shears(new Item.Properties().durability(ToolTiers.ETCHED.getUses())));

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