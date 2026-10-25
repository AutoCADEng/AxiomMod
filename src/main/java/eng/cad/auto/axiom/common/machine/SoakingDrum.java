package eng.cad.auto.axiom.common.machine;

import eng.cad.auto.axiom.client.machine.core.IMachineClientLogic;
import eng.cad.auto.axiom.common.crafting.core.CraftingIngredient;
import eng.cad.auto.axiom.common.crafting.core.CustomCraftingContainer;
import eng.cad.auto.axiom.common.crafting.core.FluidStackHandler;
import eng.cad.auto.axiom.common.crafting.core.GenericRecipe;
import eng.cad.auto.axiom.common.machine.core.GenericBlockEntity;
import eng.cad.auto.axiom.common.machine.core.MachineHolder;
import eng.cad.auto.axiom.common.machine.interfaces.IMachineLogic;
import eng.cad.auto.axiom.common.machine.interfaces.IRightClickBehaviour;
import eng.cad.auto.axiom.common.machine.ui.GenericUI;
import eng.cad.auto.axiom.registry.MachineRegistry;
import eng.cad.auto.axiom.registry.RecipeRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import java.util.Objects;
import java.util.Optional;

public class SoakingDrum {
    MachineHolder INSTANCE;
    public static final String name = "soaking_drum";
    static final int maxFluidCapacity = 16000;
    public SoakingDrum() {
        this.INSTANCE = MachineRegistry.registerMachine(new MachineHolder
                .Builder(name)
                .addItemSlots(1)
                .addDataSlots(2)
                .addFluidSlots(1)
                .addServerLogic(new FermenterMachine.Logic.Server())
                .addClientLogic(new FermenterMachine.Logic.Client())
                .noGui(new Logic.RightClickLogic())
                .build());
    }
    public static class Logic{

        public static final int progress = 0;
        public static final int ready_to_harvest = 1;
        public static final int item_slot = 0;
        public static final int fluid_tank = 0;
        public static final int MAX_PROGRESS = 24000;

        public static class Server implements IMachineLogic {

            @Override
            public void serverTick(Level level, BlockPos pos, BlockState state) {
                if (!(level.getBlockEntity(pos) instanceof GenericBlockEntity blockEntity)) return;
                if (!(Objects.equals(blockEntity.machineConfig.name(), SoakingDrum.name))) return;
                if (blockEntity.machine.metadata.get(ready_to_harvest) == 1) return;

                GenericRecipe activeRecipe = GenericRecipe.getActiveRecipe(
                        new CustomCraftingContainer(blockEntity)
                                .addTank(blockEntity.machine.tanks.getFluidInTank(fluid_tank))
                                .addSlot(blockEntity.machine.inventory.getStackInSlot(item_slot)),
                        RecipeRegistry.SOAKING_DRUM.get(),
                        level,
                        blockEntity);

                if (activeRecipe == null){
                    resetProgress(blockEntity);
                    return;
                }

                if (blockEntity.machine.metadata.get(progress)>=MAX_PROGRESS){
                    if (activeRecipe.validateCraft(blockEntity)){
                        for (CraftingIngredient ingredient:activeRecipe.getCraftingIngredients()){
                            activeRecipe.performCraft(ingredient,blockEntity,false);
                        }
                    }
                    resetProgress(blockEntity);
                }
            }

            @Override
            public void craftRecipe(GenericBlockEntity entity, GenericRecipe recipe, boolean simulate) {
                for (CraftingIngredient ingredient:recipe.getCraftingIngredients()) {
                    recipe.performCraft(ingredient, entity, simulate);
                }
            }

            @Override
            public void saveInventory(GenericBlockEntity entity, CompoundTag tag) {
                if (entity.machine.inventory != null) {
                    tag.put("Inventory", entity.machine.inventory.serializeNBT());
                }
                if (entity.machine.tanks != null) {
                    tag.put("Tanks", entity.machine.tanks.serializeNBT());
                }
                tag.putInt("Progress", entity.machine.metadata.get(progress));
            }

            @Override
            public void loadInventory(GenericBlockEntity entity, CompoundTag tag) {
                if (entity.machine.inventory != null && tag.contains("Inventory")) {
                    entity.machine.inventory.deserializeNBT(tag.getCompound("Inventory"));
                }
                if (entity.machine.tanks != null && tag.contains("Tanks")) {
                    entity.machine.tanks.deserializeNBT(tag.getCompound("Tanks"));
                }
                if (tag.contains("Progress")) entity.machine.metadata.set(progress, tag.getInt("Progress"));
            }

            @Override
            public void resetProgress(GenericBlockEntity blockEntity) {
                blockEntity.machine.metadata.set(progress, 0);
            }

            @Override
            public void incrementProgress(GenericBlockEntity blockEntity, int count) {
                blockEntity.machine.metadata.set(progress, blockEntity.machine.metadata.get(progress)>= MAX_PROGRESS? MAX_PROGRESS : blockEntity.machine.metadata.get(progress) + count);
            }
        }
        public static class Client implements IMachineClientLogic {
            @Override
            public void renderLogic(GuiGraphics guiGraphics, float tick, int mouse_x, int mouse_y, GenericUI.Screen screen) {
                // This does nothing as the machine does not have a UI
            }

            @Override
            public void clientTick(Level level, BlockPos pos, BlockState state, GenericBlockEntity blockEntity) {
                if (blockEntity.machine.metadata.get(ready_to_harvest) == 1) return;
                if (blockEntity.machine.metadata.get(progress)<= 0) return;
                if (level.random.nextFloat() < 0.25F) {

                    double x = pos.getX() + 0.5D + (level.random.nextDouble() - 0.5D) * 0.5D;
                    double y = pos.getY() + 1.0D;
                    double z = pos.getZ() + 0.5D + (level.random.nextDouble() - 0.5D) * 0.5D;

                    double xSpeed = 0.0D;
                    double ySpeed = 0.05D;
                    double zSpeed = 0.0D;

                    level.addParticle(ParticleTypes.BUBBLE_POP, x, y, z, xSpeed, ySpeed, zSpeed);
                }
            }
        }
        public static class RightClickLogic implements IRightClickBehaviour {

            @Override
            public InteractionResult useMachine(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult result) {
                if (!(level.getBlockEntity(blockPos) instanceof GenericBlockEntity machine))
                    return InteractionResult.CONSUME;
                ;
                if (!machine.machineConfig.name().equals(SoakingDrum.name)) return InteractionResult.CONSUME;

                onRightClick(machine, blockState, level, blockPos, player, interactionHand, result);

                return InteractionResult.CONSUME;
            }

            private void onRightClick(GenericBlockEntity blockEntity, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult result) {
                if (player.isShiftKeyDown()) return;
                if (interactionHand == InteractionHand.OFF_HAND) return;
                if (bucketInHand(blockEntity, blockState, level, blockPos, player, interactionHand, result)) return;
                ItemStack playerHand = player.getItemInHand(interactionHand);
                ItemStack machineItem = blockEntity.machine.inventory.getStackInSlot(item_slot);
                if (machineItem.isEmpty()) {
                    if (playerHand.isEmpty()) return;
                    ItemStack toInsert = playerHand.copy();
                    toInsert.setCount(1);
                    ItemStack remainder = blockEntity.machine.inventory.insertItem(item_slot, toInsert, false);
                    if (remainder.getCount() < toInsert.getCount()) {
                        level.playSound(null, blockPos, SoundEvents.COMPOSTER_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                        playerHand.shrink(1);
                    }
                    return;
                }

                if (playerHand.isEmpty() || playerHand.is(machineItem.getItem())) {
                    int maxHandLimit = playerHand.isEmpty() ? machineItem.getMaxStackSize() : playerHand.getMaxStackSize();
                    int currentHandCount = playerHand.isEmpty() ? 0 : playerHand.getCount();
                    int maxTransferable = maxHandLimit - currentHandCount;
                    if (maxTransferable <= 0) return;
                    ItemStack simulatedExtract = blockEntity.machine.inventory.extractItem(item_slot, maxTransferable, true);
                    if (simulatedExtract.isEmpty()) return;
                    ItemStack extracted = blockEntity.machine.inventory.extractItem(item_slot, simulatedExtract.getCount(), false);
                    level.playSound(null, blockPos, SoundEvents.COMPOSTER_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                    if (playerHand.isEmpty()) {
                        player.setItemInHand(interactionHand, extracted);
                    } else {
                        playerHand.grow(extracted.getCount());
                    }
                }
            }

            private boolean bucketInHand(GenericBlockEntity blockEntity, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult result) {
                ItemStack playerHand = player.getItemInHand(interactionHand);
                Optional<IFluidHandlerItem> bucketHandler = playerHand.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve();
                if (bucketHandler.isPresent()) {
                    NonNullList<ItemStack> returnedItems = NonNullList.create();
                    if (bucketHandler.get().getFluidInTank(0).isEmpty()) {
                        if (FluidStackHandler.fluidToItem(returnedItems, blockEntity.machine.tanks, playerHand))
                            level.playSound(null, blockPos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                    } else {
                        if (FluidStackHandler.fluidFromItem(returnedItems, blockEntity.machine.tanks, playerHand))
                            level.playSound(null, blockPos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                    }
                    for (ItemStack item:returnedItems){
                        player.addItem(item);
                    }
                    return true;
                }
                return false;
            }

        }
    }
}
