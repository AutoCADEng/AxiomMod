package eng.cad.auto.axolotyl.item;

import eng.cad.auto.axiom.AxiomMod;
import eng.cad.auto.axolotyl.machine.core.GenericMachineEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;
import net.minecraftforge.registries.RegistryObject;


import javax.annotation.ParametersAreNullableByDefault;
import java.util.Optional;


public class CustomBucket extends BucketItem {
    private int color;
    public CustomBucket(RegistryObject<Fluid> fluid, Properties properties, int color) {
        super(fluid, properties);
        this.color = color;
    }

    @Override @ParametersAreNullableByDefault
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
        AxiomMod.LOGGER.info("Initialized capability for {}", stack.getDisplayName());
        return new FluidBucketWrapper(stack);
    }


    public int getColor() {
        return color;
    }

    //Move to a dedicated fluid helper class at some point?

    public static boolean fluidToItem(GenericMachineEntity blockEntity, Player player, InteractionHand interactionHand) {
        Optional<IFluidHandlerItem> bucketHandler = player.getItemInHand(interactionHand).getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve();
        FluidActionResult result = FluidUtil.tryFillContainerAndStow(
                player.getItemInHand(interactionHand),
                blockEntity.getMachine().tanks,
                new PlayerInvWrapper(player.getInventory()),
                1000,
                player,
                true
        );

        if (result.isSuccess()){
            player.setItemInHand(interactionHand, result.result);
        }

        return result.isSuccess();
    }

    public static boolean fluidFromItem(GenericMachineEntity blockEntity, Player player, InteractionHand interactionHand) {
        Optional<IFluidHandlerItem> bucketHandler = player.getItemInHand(interactionHand).getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve();

        FluidActionResult result = FluidUtil.tryEmptyContainerAndStow(
                player.getItemInHand(interactionHand),
                blockEntity.getMachine().tanks,
                new PlayerInvWrapper(player.getInventory()),
                1000,
                player,
                true
        );
        if (result.isSuccess()) {
            player.setItemInHand(interactionHand, result.getResult());
        }
        return result.isSuccess();
    }
}
