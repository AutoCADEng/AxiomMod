package eng.cad.auto.axiom.machines.multiblock;

import eng.cad.auto.axiom.registry.MachineRegistry;
import eng.cad.auto.axolotyl.client.machine.core.IMachineClientLogic;
import eng.cad.auto.axolotyl.crafting.core.CustomCraftingContainer;
import eng.cad.auto.axolotyl.item.CustomBucket;
import eng.cad.auto.axolotyl.machine.core.GenericMachineEntity;
import eng.cad.auto.axolotyl.machine.core.MachineCore;
import eng.cad.auto.axolotyl.machine.core.interfaces.IMachineLogic;
import eng.cad.auto.axolotyl.machine.core.interfaces.IRightClickBehaviour;
import eng.cad.auto.axolotyl.machine.core.slot.SlotIORestriction;
import eng.cad.auto.axolotyl.machine.core.slot.SlotType;
import eng.cad.auto.axolotyl.machine.core.ui.GenericUI;
import eng.cad.auto.axolotyl.machine.multiblock.GenericFollowerEntity;
import eng.cad.auto.axolotyl.machine.multiblock.IFollowerLogic;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import java.util.Optional;

public class EtchedItemInput {
    MachineCore INSTANCE;
    public static final String name = "etched_item_input";
    public EtchedItemInput() {
        this.INSTANCE = MachineRegistry.registerMachine(new MachineCore
                .Builder(name)
                .addItemSlots(1)
                .modifySlot(0, SlotType.ITEM,64, SlotIORestriction.INPUT)
                .addServerLogic(new Logic.Server())
                .addClientLogic(new Logic.Client())
                .rightClickLogic(new Logic.RightClickLogic())
                .noGui()
                .isMultiblockFollower(new Logic.Follower())
                .build());
    }
    public static class Logic{
        public static class Follower implements IFollowerLogic{
            @Override
            public CustomCraftingContainer getCraftingContainer(GenericFollowerEntity entity) {
                return new CustomCraftingContainer(entity).addSlot(entity.get().inventory.getStackInSlot(0));
            }
        }
        public static class Server implements IMachineLogic {

            @Override
            public void serverTick(Level level, BlockPos pos, BlockState state) {
                if(level.isClientSide()) return;
                if (!(level.getBlockEntity(pos) instanceof GenericFollowerEntity blockEntity)) return;

            }

            @Override
            public void saveInventory(GenericMachineEntity entity, CompoundTag tag) {
                if (!(entity instanceof GenericFollowerEntity blockEntity)) return;
                if (blockEntity.get().inventory != null) {
                    tag.put("Inventory", blockEntity.get().inventory.serializeNBT());
                }
            }

            @Override
            public void loadInventory(GenericMachineEntity entity, CompoundTag tag) {
                if (!(entity instanceof GenericFollowerEntity blockEntity)) return;
                if (blockEntity.get().inventory != null && tag.contains("Inventory")) {
                    blockEntity.get().inventory.deserializeNBT(tag.getCompound("Inventory"));
                }

            }

            @Override
            public void resetProgress(GenericMachineEntity blockEntity) {
            }

            @Override
            public void incrementProgress(GenericMachineEntity blockEntity, int count) {
            }
        }
        public static class Client implements IMachineClientLogic {
            @Override
            public void renderLogic(GuiGraphics guiGraphics, float tick, int mouse_x, int mouse_y, GenericUI.Screen screen) {
            }

            @Override
            public void clientTick(Level level, BlockPos pos, BlockState state, GenericMachineEntity blockEntity) {
                if (!level.isClientSide()) return;

            }
        }
        public static class RightClickLogic implements IRightClickBehaviour {

            @Override
            public InteractionResult useMachine(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult result) {
                if (!(level.getBlockEntity(blockPos) instanceof GenericFollowerEntity machine)) return InteractionResult.PASS;
                if (!machine.machineConfig.name().equals(EtchedItemInput.name)) return InteractionResult.PASS;
                boolean success = onRightClick(machine, blockState, level, blockPos, player, interactionHand, result);
                return success? InteractionResult.SUCCESS : InteractionResult.PASS;
            }

            private boolean onRightClick(GenericMachineEntity blockEntity, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult result) {
                if (!(blockEntity instanceof GenericFollowerEntity machine)) return false;
                if (player.isShiftKeyDown()) return false;
                if (interactionHand == InteractionHand.OFF_HAND) return false;
                return bucketInHand(machine, blockState, level, blockPos, player, interactionHand, result);
            }

            private boolean bucketInHand(GenericMachineEntity blockEntity, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult result) {
                if (!(blockEntity instanceof GenericFollowerEntity machine)) return false;
                ItemStack playerHand = player.getItemInHand(interactionHand);
                Optional<IFluidHandlerItem> bucketHandler = playerHand.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve();
                if (bucketHandler.isPresent()) {
                    if (bucketHandler.get().getFluidInTank(0).isEmpty()) {
                        CustomBucket.fluidToItem(machine, player, interactionHand);
                    } else {
                        CustomBucket.fluidFromItem(machine, player, interactionHand);
                    }
                    return true;
                }
                return false;
            }
        }
    }
}
