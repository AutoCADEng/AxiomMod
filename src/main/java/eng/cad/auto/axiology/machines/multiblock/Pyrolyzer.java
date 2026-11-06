package eng.cad.auto.axiology.machines.multiblock;

import eng.cad.auto.axiology.core.DataSlotHandler;
import eng.cad.auto.axiology.core.ISlotData;
import eng.cad.auto.axiology.core.blocks.BaseMachineBlock;
import eng.cad.auto.axiology.core.component.*;
import eng.cad.auto.axiology.core.machine.*;
import eng.cad.auto.axiology.crafting.machines.PyrolyzerRecipe;
import eng.cad.auto.axiology.registry.RecipeRegistry;
import eng.cad.auto.engine.crafting.core.CustomCraftingContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;

public class Pyrolyzer extends BaseMaster<Pyrolyzer> implements IClientTickable, ISlotData, ITickable, IMachineProgress, IRecipeMachine<PyrolyzerRecipe> {
    public static final String name = "pyrolyzer";
    int[] dataSync = new int[0];
    long lastSync = 0;
    boolean firstInit = false;
    public Pyrolyzer(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        this.data = new DataSlotHandler("progress", "valid_shape");
        refreshCapabilities();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Pyrolyzer getInstance() {
        return this;
    }

    @Override
    public DataSlotHandler getDataSlots() {
        return this.data;
    }

    @Override
    public int[] getLastSyncedData() {
        return this.dataSync;
    }

    @Override
    public void setLastSyncedData(int[] data) {
        this.dataSync = data;
    }

    @Override
    public long getLastSyncTime() {
        return this.lastSync;
    }

    @Override
    public void setLastSyncTime(long time) {
        this.lastSync = time;
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if(level.isClientSide()) return;
        if (!(level.getBlockEntity(pos) instanceof Pyrolyzer pyrolyzer)) return;

        if (!firstInit){
            validateStructure(getBlockPos(), level);
            firstInit = true;
        }

        if (!isStructureValid()){
            resetProgress(pyrolyzer);
            return;
        }

        CustomCraftingContainer inventories = new CustomCraftingContainer(pyrolyzer);
        ArrayList<BaseMachine<? extends IMultiblockFollower>> followerEntities = new ArrayList<>();

        for(BaseMachine<? extends IMultiblockFollower> follower:pyrolyzer.getFollowers(level))
        {
            followerEntities.add(follower);
            if (!(follower instanceof IMultiblockFollower followerEntity)) continue;
            inventories = inventories.merge(followerEntity.getCraftingContainer());
        }

        PyrolyzerRecipe activeRecipe = findRecipe(pyrolyzer, level);

        if (activeRecipe == null){
            resetProgress(pyrolyzer);
            return;
        }

        if (!activeRecipe.hasSpace(pyrolyzer, followerEntities, activeRecipe)) {
            resetProgress(pyrolyzer);
            return;
        }

        if (pyrolyzer.progressCompleted(pyrolyzer)) {
            if (activeRecipe.validate(pyrolyzer, followerEntities, activeRecipe)) activeRecipe.craft(pyrolyzer, followerEntities, activeRecipe);
            resetProgress(pyrolyzer);
            level.playSound(null, pos, SoundEvents.DISPENSER_DISPENSE, SoundSource.BLOCKS, 1.0F, 1.0F);
            return;
        }
        if (level.random.nextFloat() < 0.025F) level.playSound(null, pos, SoundEvents.BLASTFURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F);
        incrementProgress(pyrolyzer, 1);
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
    public RecipeType<PyrolyzerRecipe> recipeType() {
        return RecipeRegistry.PYROLYZER.get();
    }

    @Override
    public void clientTick(Level level, BlockPos pos, BlockState state, TickingMachine blockEntity) {
        if (!level.isClientSide()) return;
        if (getDataSlots().get("progress") <= 0) return;
        if (level.random.nextFloat() < 0.33F) {

            double x = pos.getX() + 0.5D + (level.random.nextDouble() - 0.5D) * 0.5D;
            double y = pos.above().getY() + 1.0D;
            double z = pos.getZ() + 0.5D + (level.random.nextDouble() - 0.5D) * 0.5D;

            double xSpeed = 0.0D;
            double ySpeed = 0.05D;
            double zSpeed = 0.0D;

            level.addParticle(ParticleTypes.SMOKE, x, y, z, xSpeed, ySpeed, zSpeed);
        }
    }

    @Override
    public void updateStructureValidity(boolean validity) {
        getDataSlots().set("valid_shape", validity?1:0);
    }

    @Override
    public boolean isStructureValid() {
        return getDataSlots().get("valid_shape") == 1;
    }

    public static class MachineBlock extends BaseMachineBlock {
        public MachineBlock(Properties properties, RegistryObject<BlockEntityType<? extends BaseMachine<?>>> blockEntity) {
            super(properties, blockEntity);
        }
    }
}
