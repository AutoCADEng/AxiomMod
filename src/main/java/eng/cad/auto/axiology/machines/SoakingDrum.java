package eng.cad.auto.axiology.machines;

import eng.cad.auto.axiology.core.*;
import eng.cad.auto.axiology.core.component.*;
import eng.cad.auto.axiology.core.machine.TickingMachine;
import eng.cad.auto.axiology.crafting.machines.SoakerRecipe;
import eng.cad.auto.axiology.machines.util.CommonRightClickBehaviour;
import eng.cad.auto.axiology.registry.RecipeRegistry;
import eng.cad.auto.engine.fluid.FluidStackHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class SoakingDrum extends TickingMachine<SoakingDrum> implements IRightClickable, IMachineProgress, IRecipeMachine<SoakerRecipe>, ISlotItem, ISlotFluid, ISlotData, IMachinePersistable, ITickable, IClientTickable{
    public static String name = "soaking_drum";
    int[] dataSync = new int[0];
    long lastSync = 0;
    public SoakingDrum(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        this.data = new DataSlotHandler("progress");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public SoakingDrum getInstance() {
        return this;
    }

    @Override
    public FluidStackHandler getFluidSlots() {
        return this.fluids;
    }

    @Override
    public ExtendedItemStackHandler getItemSlots() {
        return this.items;
    }

    @Override
    public DataSlotHandler getDataSlots() {
        return this.data;
    }

    @Override
    public int[] getLastSyncedData() {
        return dataSync;
    }

    @Override
    public void setLastSyncedData(int[] data) {
        this.dataSync = data;
    }

    @Override
    public long getLastSyncTime() {
        return lastSync;
    }

    @Override
    public void setLastSyncTime(long time) {
        lastSync = time;
    }

    @Override
    public void clientTick(Level level, BlockPos pos, BlockState state, TickingMachine blockEntity) {
        if (!level.isClientSide()) return;
        if (getInstance().getDataSlots().get("progress") <= 0) return;
        if (level.random.nextFloat() < 0.33F) {

            double x = pos.getX() + 0.5D + (level.random.nextDouble() - 0.5D) * 0.5D;
            double y = pos.getY() + 1.0D;
            double z = pos.getZ() + 0.5D + (level.random.nextDouble() - 0.5D) * 0.5D;

            double xSpeed = 0.0D;
            double ySpeed = 0.05D;
            double zSpeed = 0.0D;

            level.addParticle(ParticleTypes.BUBBLE, x, y, z, xSpeed, ySpeed, zSpeed);
        }
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (!(validateMachine(level, pos, getName()) instanceof SoakingDrum blockEntity)) return;

        SoakerRecipe activeRecipe = findRecipe(blockEntity, level);

        if (activeRecipe == null || !activeRecipe.hasSpace(blockEntity, activeRecipe)) {
            resetProgress(blockEntity);
            return;
        }

        if (progressCompleted(blockEntity)) {
            if (activeRecipe.validate(blockEntity, activeRecipe)) activeRecipe.craft(blockEntity, activeRecipe);
            level.playSound(null, pos, SoundEvents.DISPENSER_DISPENSE, SoundSource.BLOCKS, 1.0F, 1.0F);
            resetProgress(blockEntity);
            return;
        }

        if (level.random.nextFloat() < 0.1F) level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0F, 1.0F);
        incrementProgress(blockEntity,1);
    }

    @Override
    public RecipeType<SoakerRecipe> recipeType() {
        return RecipeRegistry.SOAKING_DRUM.get();
    }

    @Override
    public int progressSlot() {
        return data.indexOf("progress");
    }

    @Override
    public int maxProgress() {
        return 24000 / 60;
    }

    @Override
    public InteractionResult rightClick(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult result) {
        return onRightClick(blockState, level, blockPos, player, interactionHand, result) ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }

    private boolean onRightClick(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult result) {
        if (player.isShiftKeyDown()) return false;
        if (interactionHand == InteractionHand.OFF_HAND) return false;
        if (!(level.getBlockEntity(blockPos) instanceof SoakingDrum soakingDrum)) return false;
        if (CommonRightClickBehaviour.itemInHand(0,soakingDrum, blockState, level, blockPos, player, interactionHand, result)) return true;
        return CommonRightClickBehaviour.bucketInHand(soakingDrum.getFluidSlots().getFluidInTank(0).getAmount(), soakingDrum, blockState, level, blockPos, player, interactionHand, result);
    }

}
