package eng.cad.auto.axiology.machines;

import eng.cad.auto.axiology.core.*;
import eng.cad.auto.axiology.core.blocks.BaseMachineBlock;
import eng.cad.auto.axiology.core.component.*;
import eng.cad.auto.axiology.core.machine.TickingMachine;
import eng.cad.auto.axiology.core.slot.SlotConfig;
import eng.cad.auto.axiology.core.slot.SlotIORestriction;
import eng.cad.auto.axiology.crafting.machines.AlembicRecipe;
import eng.cad.auto.axiology.machines.util.CommonRightClickBehaviour;
import eng.cad.auto.engine.fluid.FluidStackHandler;
import eng.cad.auto.axiology.registry.RecipeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

public class Alembic extends TickingMachine<Alembic> implements IHeatedMachine, IMachineProgress, IRecipeMachine<AlembicRecipe>, IRightClickable, ISlotFluid, ISlotData, IMachinePersistable, ITickable, IClientTickable{
    public static final String name = "alembic";
    int[] dataSync = new int[0];
    long lastSync = 0;
    public Alembic(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);

        this.fluids = new FluidStackHandler(new SlotConfig[]{
                new SlotConfig(SlotIORestriction.IN_OUT, 1000)
        });

        this.data = new DataSlotHandler("heating", "progress");
        refreshCapabilities();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Alembic getInstance() {
        return this;
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if(!(validateMachine(level, pos, getName()) instanceof Alembic blockEntity)) return;
        updateHeatState(level, pos, blockEntity);
        AlembicRecipe activeRecipe = findRecipe(blockEntity, level);

        if (activeRecipe == null || !isHeated(blockEntity)){
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
    public void clientTick(Level level, BlockPos pos, BlockState state, TickingMachine blockEntity) {
        if (!level.isClientSide()) return;
        if (getProgress(blockEntity) <= 0) return;
        if (level.random.nextFloat() < 0.33F) {

            double x = pos.getX() + 0.5D + (level.random.nextDouble() - 0.5D) * 0.5D;
            double y = pos.getY() + 1.0D;
            double z = pos.getZ() + 0.5D + (level.random.nextDouble() - 0.5D) * 0.5D;

            double xSpeed = 0.0D;
            double ySpeed = 0.05D;
            double zSpeed = 0.0D;

            level.addParticle(ParticleTypes.SMOKE, x, y, z, xSpeed, ySpeed, zSpeed);
        }
    }

    @Override
    public InteractionResult rightClick(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult result) {
        boolean success = onRightClick(blockState, level, blockPos, player, interactionHand, result);
        return success? InteractionResult.SUCCESS:InteractionResult.CONSUME;
    }

    private boolean onRightClick(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult result) {
        if (player.isShiftKeyDown()) return false;
        if (interactionHand == InteractionHand.OFF_HAND) return false;
        if (!(level.getBlockEntity(blockPos) instanceof Alembic alembic)) return false;
        return CommonRightClickBehaviour.bucketInHand(0, alembic, blockState, level, blockPos, player, interactionHand, result);
    }

    @Override
    public RecipeType<AlembicRecipe> recipeType() {
        return RecipeRegistry.ALEMBIC.get();
    }

    @Override
    public int progressSlot() {
        return getDataSlots().indexOf("progress");
    }

    @Override
    public int maxProgress() {
        return 24000/60;
    }

    @Override
    public int heatSlot() {
        return getDataSlots().indexOf("heating");
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
    public FluidStackHandler getFluidSlots() {
        return this.fluids;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
    }
    public static class MachineBlock extends BaseMachineBlock {
        public MachineBlock(Properties properties, RegistryObject<BlockEntityType<? extends TickingMachine>> blockEntity) {
            super(properties, blockEntity);
        }
    }
}
