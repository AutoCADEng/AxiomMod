package eng.cad.auto.axiom.machines.util;

import eng.cad.auto.axolotyl.item.CustomBucket;
import eng.cad.auto.axolotyl.machine.core.GenericMachineEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import java.util.Optional;

public class CommonRightClickBehaviour {

    public static boolean bucketInHand(int slot, GenericMachineEntity blockEntity, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult result) {
        ItemStack playerHand = player.getItemInHand(interactionHand);
        Optional<IFluidHandlerItem> bucketHandler = playerHand.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve();
        if (bucketHandler.isPresent()) {
            if (bucketHandler.get().getFluidInTank(slot).isEmpty()) {
                return insertFluid(slot,blockEntity,blockState,level,blockPos,player,interactionHand,result);
            } else {
                return extractFluid(slot,blockEntity,blockState,level,blockPos,player,interactionHand,result);
            }
        }
        return false;
    }

    //TODO find a way to make slot useful, this is probably the makings of a bug!
    public static boolean insertFluid(int slot, GenericMachineEntity blockEntity, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult result){
        ItemStack playerHand = player.getItemInHand(interactionHand);
        Optional<IFluidHandlerItem> bucketHandler = playerHand.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve();
        if (bucketHandler.isPresent()) {
            return CustomBucket.fluidToItem(blockEntity, player, interactionHand);
        }
        return false;
    }

    public static boolean extractFluid(int slot, GenericMachineEntity blockEntity, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult result){
        ItemStack playerHand = player.getItemInHand(interactionHand);
        Optional<IFluidHandlerItem> bucketHandler = playerHand.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve();
        if (bucketHandler.isPresent()) {
            return CustomBucket.fluidFromItem(blockEntity, player, interactionHand);
        }
        return false;
    }

    public static boolean itemInHand(int item_slot, GenericMachineEntity blockEntity, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult result) {
        ItemStack playerHand = player.getItemInHand(interactionHand);
        ItemStack machineItem = blockEntity.get().inventory.getStackInSlot(item_slot);
        if (insertItem(item_slot,blockEntity,blockState,level,blockPos,player,interactionHand,result)) return true;
        return extractItem(item_slot,blockEntity,blockState,level,blockPos,player,interactionHand,result);
    }

    public static boolean insertItem(int item_slot, GenericMachineEntity blockEntity, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult result) {
        ItemStack playerHand = player.getItemInHand(interactionHand);
        ItemStack machineItem = blockEntity.get().inventory.getStackInSlot(item_slot);

        if (machineItem.isEmpty()) {
            if (playerHand.isEmpty()) return false;
            ItemStack toInsert = playerHand.copy();
            toInsert.setCount(1);
            ItemStack remainder = blockEntity.get().inventory.insertItem(item_slot, toInsert, false);
            if (remainder.getCount() < toInsert.getCount()) {
                level.playSound(null, blockPos, SoundEvents.COMPOSTER_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                playerHand.shrink(1);
            }
            return true;
        }
        return false;
    }

    public static boolean extractItem(int item_slot, GenericMachineEntity blockEntity, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult result) {
        ItemStack playerHand = player.getItemInHand(interactionHand);
        ItemStack machineItem = blockEntity.get().inventory.getStackInSlot(item_slot);

        if (playerHand.isEmpty() || playerHand.is(machineItem.getItem())) {
            int maxHandLimit = playerHand.isEmpty() ? machineItem.getMaxStackSize() : playerHand.getMaxStackSize();
            int currentHandCount = playerHand.isEmpty() ? 0 : playerHand.getCount();
            int maxTransferable = maxHandLimit - currentHandCount;
            if (maxTransferable <= 0) return false;
            ItemStack simulatedExtract = blockEntity.get().inventory.extractItem(item_slot, maxTransferable, true);
            if (simulatedExtract.isEmpty()) return false;
            ItemStack extracted = blockEntity.get().inventory.extractItem(item_slot, simulatedExtract.getCount(), false);
            level.playSound(null, blockPos, SoundEvents.COMPOSTER_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (playerHand.isEmpty()) {
                player.setItemInHand(interactionHand, extracted);
            } else {
                playerHand.grow(extracted.getCount());
            }
            return true;
        }
        return false;
    }
}
