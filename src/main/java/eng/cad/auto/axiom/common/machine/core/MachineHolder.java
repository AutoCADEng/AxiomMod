package eng.cad.auto.axiom.common.machine.core;

import eng.cad.auto.axiom.AxiomMod;
import eng.cad.auto.axiom.client.machine.core.IMachineClientLogic;
import eng.cad.auto.axiom.common.crafting.core.GenericRecipe;
import eng.cad.auto.axiom.common.machine.interfaces.IMachineLogic;
import eng.cad.auto.axiom.common.machine.interfaces.IRightClickBehaviour;
import eng.cad.auto.axiom.common.machine.ui.GenericUI;
import eng.cad.auto.axiom.common.machine.interfaces.IMenuSlotLogic;
import eng.cad.auto.axiom.registry.MachineRegistry;
import eng.cad.auto.axiom.registry.BlockRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.RegistryObject;

public class MachineHolder {
    public RegistryObject<BlockEntityType<GenericBlockEntity>> blockEntity;
    public RegistryObject<Block> block;
    public final Config config;
    public GenericUI ui;

    private MachineHolder(String name, Config config) {
        AxiomMod.LOGGER.info("Registering new machine of "+name);
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
        if (!config.noGui) {
            ui = new GenericUI(config, blockEntity);
        }
    }
    //Helper Nested Classes
    public record Config(boolean noGui, int itemSlots, int fluidSlots, int dataSlots, IMachineLogic serverLogic, IMachineClientLogic clientLogic, IMenuSlotLogic menuSlots,
                         IRightClickBehaviour rightClickLogic, String name) {

    }

    public static class Builder {

        int itemSlots = 0;
        int fluidSlots = 0;
        int dataSlots = 0;
        boolean noGui = false;
        String name;
        IMenuSlotLogic menuSlot = new IMenuSlotLogic() {
            @Override
            public void loadMenuSlots(GenericUI.Menu menu) {
                AxiomMod.LOGGER.warn("No custom behaviour defined for loadMenuSlots");
            }
        };

        IMachineLogic serverLogic = new IMachineLogic() {
            @Override
            public void serverTick(Level level, BlockPos pos, BlockState state) {
                 AxiomMod.LOGGER.warn("No custom behaviour defined for serverTick");
            }

            @Override
            public void craftRecipe(GenericBlockEntity entity, GenericRecipe recipe, boolean simulateFirst) {
                AxiomMod.LOGGER.warn("No custom behaviour defined for craftRecipe");
            }

            @Override
            public void saveInventory(GenericBlockEntity blockEntity, CompoundTag tag) {
                AxiomMod.LOGGER.warn("No custom behaviour defined for saveInventory, data will NOT PERSIST");
            }

            @Override
            public void loadInventory(GenericBlockEntity blockEntity, CompoundTag tag) {
                AxiomMod.LOGGER.warn("No custom behaviour defined for loadInventory, data will NOT PERSIST");
            }

            @Override
            public void resetProgress(GenericBlockEntity blockEntity) {
                AxiomMod.LOGGER.warn("No custom behaviour defined for resetProgress");
            }

            @Override
            public void incrementProgress(GenericBlockEntity blockEntity, int count) {
                AxiomMod.LOGGER.warn("No custom behaviour defined for incrementProgress");
            }
        };

        IMachineClientLogic clientLogic = new IMachineClientLogic() {
            @Override
            public void renderLogic(GuiGraphics guiGraphics, float tick, int mouse_x, int mouse_y, GenericUI.Screen screen) {
                AxiomMod.LOGGER.warn("No custom behaviour defined for renderLogic");
            }

            @Override
            public void clientTick(Level level, BlockPos pos, BlockState state, GenericBlockEntity blockEntity) {
                AxiomMod.LOGGER.warn("No custom behaviour defined for clientTick");
            }
        };

        IRightClickBehaviour rightClickLogic = new IRightClickBehaviour() {
            @Override
            public InteractionResult useMachine(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult result) {
                if (level.isClientSide()) return InteractionResult.SUCCESS;
                if (player.isShiftKeyDown()) return InteractionResult.CONSUME;
                if (level.getBlockEntity(blockPos) instanceof GenericBlockEntity machine){
                    if (!machine.machineConfig.name().equals(name)) return InteractionResult.CONSUME;
                    if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResult.CONSUME;
                    NetworkHooks.openScreen(serverPlayer, machine, blockPos);
                }
                return InteractionResult.CONSUME;
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
        public Builder addMenuSlots(IMenuSlotLogic menuSlot) {
            this.menuSlot = menuSlot;
            return this;
        }
        public Builder noGui(IRightClickBehaviour rightClickLogic){
            this.rightClickLogic = rightClickLogic;
            noGui = true;
            return this;
        }
        public Config buildData(){
            return new Config(
                    noGui,
                    itemSlots,
                    fluidSlots,
                    dataSlots,
                    serverLogic,
                    clientLogic,
                    menuSlot,
                    rightClickLogic,
                    name);
        }

        public MachineHolder build(){
            Config machineData = this.buildData();
            return new MachineHolder(name, machineData);
        }


    }
}
