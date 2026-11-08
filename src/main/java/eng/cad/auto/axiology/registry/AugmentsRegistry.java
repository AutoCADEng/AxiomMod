package eng.cad.auto.axiology.registry;

import eng.cad.auto.axiology.AxiomMod;
import eng.cad.auto.axiology.modulartools.abilities.custom.*;
import eng.cad.auto.axiology.modulartools.abilities.custom.tiers.MaterialAugment;
import eng.cad.auto.axiology.modulartools.abilities.custom.tools.*;
import eng.cad.auto.axiology.modulartools.abilities.enchants.*;
import eng.cad.auto.axiology.modulartools.components.ModularToolAugment;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
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

    public static final RegistryObject<ModularToolAugment> EFFICIENCY = MODULES.register("efficiency", EfficiencyAugment::new);
    public static final RegistryObject<ModularToolAugment> UNBREAKING = MODULES.register("unbreaking", UnbreakingAugment::new);
    public static final RegistryObject<ModularToolAugment> FORTUNE = MODULES.register("fortune", FortuneAugment::new);
    public static final RegistryObject<ModularToolAugment> SHARPNESS = MODULES.register("sharpness", SharpnessAugment::new);
    public static final RegistryObject<ModularToolAugment> SILK_TOUCH = MODULES.register("silk_touch", SilktouchAugment::new);
    public static final RegistryObject<ModularToolAugment> SMITE = MODULES.register("smite", SmiteAugment::new);
    public static final RegistryObject<ModularToolAugment> BANE_OF_ARTHROPODS = MODULES.register("bane_of_arthropods", BaneOfArthropodsAugment::new);
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

    private static RegistryObject<ModularToolAugment> registerMaterialModule(String name, Tier tier) {
        return MODULES.register(name, () -> new MaterialAugment(Component.translatable("axiology.modulartools.plating." + name), tier));
    }

    public static RegistryObject<ModularToolAugment> registerMaterialModule(Component component, String name, Tier tier) {
        return MODULES.register(name, () -> new MaterialAugment(component, tier));
    }
    public static void init(IEventBus modEventBus) {
        MODULES.register(modEventBus);
    }
}
