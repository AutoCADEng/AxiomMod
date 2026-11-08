package eng.cad.auto.axiology.modulartools.abilities.custom;


import eng.cad.auto.axiology.modulartools.components.IModularDurability;
import eng.cad.auto.axiology.modulartools.components.IModularFluidInventory;
import eng.cad.auto.axiology.modulartools.components.IModularTool;
import eng.cad.auto.axiology.modulartools.components.ModularToolAugment;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;

public class FluidFueledAugment extends ModularToolAugment implements IModularDurability {
    @Override
    public Component getDisplayName() {
        return Component.translatable("axiology.modulartools.fuel.liquid");
    }

    /**
     * Calculates how much burnable fuel is present in a tool tank. Intended flow:
     * Filter for IModularFluidInventories
     * Excludes fluids that are not tagged by "tool_fuel_fluid"
     * Sum up all valid fuel per tank per IModularFluidInventory
     *
     * @param ItemStack of the modular tool being checked
     * @return returns the total burnable fuel in a modular tool
     */
    @Override
    public int getDurability(ItemStack stack) {
        TagKey<Fluid> valid_fuels = TagKey.create(Registries.FLUID, ResourceLocation.parse("axiology:tool_fuel_fluids"));

        int fuel = IModularTool.getAugments(stack).keySet().stream()
                .filter(key -> key instanceof IModularFluidInventory fluidTank)
                .map(key -> (IModularFluidInventory) key)
                .mapToInt(fluidModule -> {
                            int validFuel = 0;
                            for (int idx = 0; idx < fluidModule.getTanks().getTanks(); idx++) {
                                if (!fluidModule.getTanks().getFluidInTank(idx).getFluid().defaultFluidState().is(valid_fuels))
                                    continue;
                                validFuel += fluidModule.getTanks().getFluidInTank(idx).getAmount();
                            }
                            return validFuel;
                        }
                ).sum();

        return fuel;
    }
}
