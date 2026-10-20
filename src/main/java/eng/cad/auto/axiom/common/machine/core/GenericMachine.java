package eng.cad.auto.axiom.common.machine.core;

import eng.cad.auto.axiom.AxiomMod;
import eng.cad.auto.axiom.registry.MachineRegistry;
import eng.cad.auto.axiom.registry.BlockRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;

public class GenericMachine {
    public RegistryObject<BlockEntityType<GenericBlockEntity>> blockEntity;
    public RegistryObject<Block> block;
    public final Config config;
    public GenericUI ui;

    private GenericMachine(String name, Config config) {
        this.config = config;
        block = BlockRegistry.registerBlock(
                name,
                () -> new GenericMachineBlock(
                        BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK),
                        blockEntity,
                        config
                )
        );

        blockEntity =
                MachineRegistry.MACHINES.register(name, () ->
                        BlockEntityType.Builder.of(
                                (blockPos, blockState) ->
                                        new GenericBlockEntity(
                                                blockEntity.get(),
                                                blockPos,
                                                blockState,
                                                config),
                                block.get()
                        ).build(null)
                );
        ui = new GenericUI(config, blockEntity);
    }
    //Helper Nested Classes
    public record Config(int itemSlots, int fluidSlots, int dataSlots, IMachineLogic serverLogic, IMachineClientLogic clientLogic, String name) {

    }

    public static class Builder {

        int itemSlots = 0;
        int fluidSlots = 0;
        int dataSlots = 0;
        String name;
        
        IMachineLogic serverLogic = new IMachineLogic() {
            @Override
            public void serverTick(Level level, BlockPos pos, BlockState state) {
                 AxiomMod.LOGGER.warn("No custom behaviour defined for serverTick");
            }
        };

        IMachineClientLogic clientLogic = new IMachineClientLogic() {
            @Override
            public void renderLogic(GuiGraphics guiGraphics, float tick, int mouse_x, int mouse_y, GenericUI.Screen screen) {
                AxiomMod.LOGGER.warn("No custom behaviour defined for renderLogic");
            }
        };

        public Builder (String name){
            this.name = name;
        }
        public Builder addItemSlots(int number) {
            itemSlots += number;
            return this;
        }

        public Builder addFluidSlots(int number) {
            fluidSlots += number;
            return this;
        }

        public Builder addServerLogic(IMachineLogic logic){
            this.serverLogic = logic;
            return this;
        }

        public Builder addClientLogic(IMachineClientLogic logic){
            this.clientLogic = logic;
            return this;
        }

        public Builder addDataSlots(int number){
            dataSlots += number;
            return this;
        }

        public Config buildData(){
            return new Config(itemSlots, fluidSlots, dataSlots, serverLogic, clientLogic, name);
        }

        public GenericMachine build(){
            Config machineData = this.buildData();
            return new GenericMachine(name, machineData);
        }
    }
}
