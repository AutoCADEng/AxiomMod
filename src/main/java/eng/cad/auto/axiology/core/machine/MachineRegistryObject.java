package eng.cad.auto.axiology.core.machine;

import eng.cad.auto.axiology.AxiomMod;
import eng.cad.auto.axiology.core.blocks.BaseMachineBlock;
import eng.cad.auto.axiology.registry.BlockRegistry;
import eng.cad.auto.axiology.registry.ItemRegistry;
import eng.cad.auto.axiology.registry.MachineRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class MachineRegistryObject {
    public RegistryObject<BlockEntityType<? extends BaseMachine<?>>> blockEntity;
    public RegistryObject<Item> blockItem;
    public RegistryObject<Block> block;

    public final String name;

    public MachineRegistryObject(String name, BaseMachine.MachineFactory factory, BlockFactory blockFactory) {
        this(name, factory, blockFactory, () -> BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion());
    }

    public MachineRegistryObject(String name, BaseMachine.MachineFactory factory, BlockFactory blockFactory, Supplier<BlockBehaviour.Properties> props) {
        AxiomMod.LOGGER.info("Registering new machine of {}", name);
        this.name = name;

        block = BlockRegistry.registerMachineBlock(name, () -> blockFactory.create(props.get(), blockEntity));

        blockItem = ItemRegistry.registerBlockItem(name, block);

        blockEntity = MachineRegistry.MACHINES.register(name, () -> BlockEntityType.Builder.<BaseMachine<?>>of((pos, state) -> factory.create(blockEntity.get(), pos, state), block.get()).build(null));

    }

    @FunctionalInterface
    public interface BlockFactory {
        BaseMachineBlock create(BlockBehaviour.Properties props, RegistryObject<BlockEntityType<? extends BaseMachine<?>>> entity);
    }
}