package eng.cad.auto.axiology.registry;

import eng.cad.auto.axiology.AxiomMod;
import eng.cad.auto.axiology.modulartools.abilities.custom.*;
import eng.cad.auto.axiology.modulartools.abilities.custom.tools.*;
import eng.cad.auto.axiology.modulartools.components.ModularToolAugment;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class AugmentsRegistry {
    public static final ResourceKey<net.minecraft.core.Registry<ModularToolAugment>> REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(AxiomMod.MODID, "tool_modules"));
    public static final DeferredRegister<ModularToolAugment> MODULES = DeferredRegister.create(REGISTRY_KEY, AxiomMod.MODID);
    public static final Supplier<IForgeRegistry<ModularToolAugment>> REGISTRY = MODULES.makeRegistry(RegistryBuilder::new);

    public static final RegistryObject<ModularToolAugment> BACKPACK = MODULES.register("backpack", BackpackAugment::new);
    public static final RegistryObject<ModularToolAugment> BATTERY = MODULES.register("battery", BatteryAugment::new);
    public static final RegistryObject<ModularToolAugment> SMELT_ITEMS = MODULES.register("smelt_items", SmeltItemsAugment::new);
    public static final RegistryObject<ModularToolAugment> TANK = MODULES.register("tank", TankAugment::new);
    public static final RegistryObject<ModularToolAugment> TELEPORT_ITEMS = MODULES.register("teleport_items", TeleportItemsAugment::new);

    public static final RegistryObject<ModularToolAugment> AXE = MODULES.register("axe", AxeAugment::new);
    public static final RegistryObject<ModularToolAugment> HOE = MODULES.register("hoe", HoeAugment::new);
    public static final RegistryObject<ModularToolAugment> SWORD = MODULES.register("sword", SwordAugment::new);
    public static final RegistryObject<ModularToolAugment> SHOVEL = MODULES.register("shovel", ShovelAugment::new);
    public static final RegistryObject<ModularToolAugment> PICKAXE = MODULES.register("pickaxe", PickaxeAugment::new);
    public static final RegistryObject<ModularToolAugment> SHEARS = MODULES.register("shears", ShearsAugment::new);

    public static final RegistryObject<ModularToolAugment> GOLD = registerMaterialModule("gold", Tiers.GOLD);
    public static final RegistryObject<ModularToolAugment> DIAMOND = registerMaterialModule("diamond", Tiers.DIAMOND);
    public static final RegistryObject<ModularToolAugment> WOOD = registerMaterialModule("wood", Tiers.WOOD);
    public static final RegistryObject<ModularToolAugment> STONE = registerMaterialModule("stone", Tiers.STONE);
    public static final RegistryObject<ModularToolAugment> IRON = registerMaterialModule("iron", Tiers.IRON);
    public static final RegistryObject<ModularToolAugment> NETHERITE = registerMaterialModule("netherite", Tiers.NETHERITE);

    public static final RegistryObject<ModularToolAugment> EFFICIENCY =
            MODULES.register("efficiency", () -> new EnchantmentAugment(Component.translatable("axiology.modulartools.efficiency"))
                    .addEnchantment(Enchantments.BLOCK_EFFICIENCY)
                    .setMaxStack(10)
                    .setColor(ChatFormatting.GREEN)
            );

    public static final RegistryObject<ModularToolAugment> UNBREAKING =
            MODULES.register("unbreaking", () -> new EnchantmentAugment(Component.translatable("axiology.modulartools.unbreaking"))
                    .addEnchantment(Enchantments.UNBREAKING)
                    .setMaxStack(5)
                    .setColor(ChatFormatting.GREEN)
            );

    public static final RegistryObject<ModularToolAugment> FORTUNE =
            MODULES.register("fortune", () -> new EnchantmentAugment(Component.translatable("axiology.modulartools.fortune"))
                    .addEnchantment(Enchantments.BLOCK_FORTUNE)
                    .setMaxStack(5)
                    .setColor(ChatFormatting.DARK_PURPLE)
                    .setCost(4)
            );

    public static final RegistryObject<ModularToolAugment> SHARPNESS =
            MODULES.register("sharpness", () -> new EnchantmentAugment(Component.translatable("axiology.modulartools.sharpness"))
                    .addEnchantment(Enchantments.SHARPNESS)
                    .setMaxStack(9)
                    .setColor(ChatFormatting.DARK_RED)
            );

    public static final RegistryObject<ModularToolAugment> SILK_TOUCH =
            MODULES.register("silk_touch", () -> new EnchantmentAugment(Component.translatable("axiology.modulartools.silk_touch"))
                    .addEnchantment(Enchantments.SILK_TOUCH)
                    .setMaxStack(1)
                    .setColor(ChatFormatting.GOLD)
                    .setCost(8)
            );
    public static final RegistryObject<ModularToolAugment> SMITE =
            MODULES.register("silk_touch", () -> new EnchantmentAugment(Component.translatable("axiology.modulartools.smite"))
                    .addEnchantment(Enchantments.SMITE)
                    .setMaxStack(9)
                    .setColor(ChatFormatting.YELLOW)
            );
    public static final RegistryObject<ModularToolAugment> BANE_OF_ARTHROPODS =
            MODULES.register("bane_of_arthropods", () -> new EnchantmentAugment(Component.translatable("axiology.modulartools.arthropods"))
                    .addEnchantment(Enchantments.BANE_OF_ARTHROPODS)
                    .setMaxStack(9)
                    .setColor(ChatFormatting.DARK_BLUE)
            );
    private static RegistryObject<ModularToolAugment> registerMaterialModule(String name, Tier tier) {
        return MODULES.register(name, () -> new MaterialAugment(Component.translatable("axiology.modulartools.plating." + name), tier));
    }

    public static void init(IEventBus modEventBus) {
        MODULES.register(modEventBus);
    }
}
