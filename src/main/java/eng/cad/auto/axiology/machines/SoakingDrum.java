package eng.cad.auto.axiology.machines;

import eng.cad.auto.axiology.core.*;
import eng.cad.auto.axiology.core.blocks.BaseMachineBlock;
import eng.cad.auto.axiology.core.machine.BaseMachine;
import eng.cad.auto.axiology.core.machine.TickingMachine;
import eng.cad.auto.axiology.core.machine.component.*;
import eng.cad.auto.axiology.core.slot.SlotConfig;
import eng.cad.auto.axiology.core.slot.SlotIORestriction;
import eng.cad.auto.axiology.crafting.machines.SoakerRecipe;
import eng.cad.auto.axiology.registry.RecipeRegistry;
import eng.cad.auto.axiology.core.fluid.FluidStackHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;

public class SoakingDrum extends TickingMachine<SoakingDrum> implements IRightClickable, IMachineProgress, IRecipeMachine<SoakerRecipe>, ISlotItem, ISlotFluid, ISlotData, IMachinePersistable, ITickable, IClientTickable{
    public static String name = "soaking_drum";
    int[] dataSync = new int[0];
    long lastSync = 0;
    public SoakingDrum(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        this.fluids = new FluidStackHandler(new SlotConfig[]{
                new SlotConfig(SlotIORestriction.IN_OUT, 8000)
        }, this::onFluidContentsChanged);
        this.items = new ExtendedItemStackHandler(new SlotConfig[]{
                new SlotConfig(SlotIORestriction.IN_OUT, 1)
        });
        this.data = new DataSlotHandler("progress");
        refreshCapabilities();
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

        SoakerRecipe activeRecipe = findRecipe(blockEntity, level, null);

        if (activeRecipe == null) {
            resetProgress(blockEntity);
            return;
        }

        if (progressCompleted(blockEntity)) {
            if (activeRecipe.validate(blockEntity, activeRecipe)) {
                activeRecipe.craft(blockEntity, activeRecipe);
            }
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

    public static class MachineBlock extends BaseMachineBlock {
        public MachineBlock(Properties properties, RegistryObject<BlockEntityType<? extends BaseMachine<?>>> blockEntity) {
            super(properties, blockEntity);
        }
    }
}
