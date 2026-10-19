package eng.cad.auto.axiom.common.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.ParametersAreNullableByDefault;

public class CustomBucket extends BucketItem {
    private int color;
    public CustomBucket(RegistryObject<Fluid> fluid, Properties properties, int color) {
        super(fluid, properties);
        this.color = color;
    }

    @Override @ParametersAreNullableByDefault
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
        return new FluidBucketWrapper(stack);
    }

    public int getColor() {
        return color;
    }
}
