package eng.cad.auto.axiology.machines.multiblock.followers;

import eng.cad.auto.axiology.core.ISlotFluid;
import eng.cad.auto.axiology.core.blocks.BaseMachineBlock;
import eng.cad.auto.axiology.core.machine.component.IRightClickable;
import eng.cad.auto.axiology.core.machine.BaseMachine;
import eng.cad.auto.axiology.core.slot.SlotConfig;
import eng.cad.auto.axiology.core.slot.SlotIORestriction;
import eng.cad.auto.axiology.crafting.core.CustomCraftingContainer;
import eng.cad.auto.axiology.core.fluid.FluidStackHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;

public class FluidInputHatch extends Hatch<FluidInputHatch> implements ISlotFluid, IRightClickable {
    public static String name = "fluid_input_hatch";
    public FluidInputHatch(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        this.fluids = new FluidStackHandler(new SlotConfig[]{
                new SlotConfig(SlotIORestriction.INPUT, 1000)
        }, this::onFluidContentsChanged);
        refreshCapabilities();
    }

    @Override
    public FluidStackHandler getFluidSlots() {
        return this.fluids;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Hatch<FluidInputHatch> getInstance() {
        return this;
    }

    @Override
    public CustomCraftingContainer getCraftingContainer() {
        return new CustomCraftingContainer(this)
                .addTank(getFluidSlots().getFluidInTank(0));
    }

    public static class MachineBlock extends BaseMachineBlock {
        public MachineBlock(Properties properties, RegistryObject<BlockEntityType<? extends BaseMachine<?>>> blockEntity) {
            super(properties, blockEntity);
        }
    }
}
