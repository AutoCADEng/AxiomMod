package eng.cad.auto.axiology.machines.multiblock.followers;

import eng.cad.auto.axiology.core.ExtendedItemStackHandler;
import eng.cad.auto.axiology.core.ISlotItem;
import eng.cad.auto.axiology.core.blocks.BaseMachineBlock;
import eng.cad.auto.axiology.core.component.IRightClickable;
import eng.cad.auto.axiology.core.machine.BaseMachine;
import eng.cad.auto.axiology.core.slot.SlotConfig;
import eng.cad.auto.axiology.core.slot.SlotIORestriction;
import eng.cad.auto.axiology.crafting.core.CustomCraftingContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;

public class ItemInputHatch extends Hatch<ItemInputHatch> implements ISlotItem, IRightClickable {
    public static String name = "item_input_hatch";
    public ItemInputHatch(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        this.items = new ExtendedItemStackHandler(new SlotConfig[]{
                new SlotConfig(SlotIORestriction.INPUT, 64)
        });
        refreshCapabilities();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Hatch<ItemInputHatch> getInstance() {
        return this;
    }

    @Override
    public CustomCraftingContainer getCraftingContainer() {
        return new CustomCraftingContainer(this)
                .addSlot(getItemSlots().getStackInSlot(0));
    }

    @Override
    public ExtendedItemStackHandler getItemSlots() {
        return this.items;
    }

    public static class MachineBlock extends BaseMachineBlock {
        public MachineBlock(Properties properties, RegistryObject<BlockEntityType<? extends BaseMachine<?>>> blockEntity) {
            super(properties, blockEntity);
        }
    }
}
