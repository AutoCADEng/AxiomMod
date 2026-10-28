package eng.cad.auto.axolotyl.machine.core;

import eng.cad.auto.axiom.AxiomMod;
import eng.cad.auto.axiom.machines.ChemicalReactor;
import eng.cad.auto.axiom.registry.ItemRegistry;
import eng.cad.auto.axolotyl.client.machine.core.IMachineClientLogic;
import eng.cad.auto.axolotyl.machine.core.slot.SlotConfig;
import eng.cad.auto.axolotyl.machine.core.slot.SlotIORestriction;
import eng.cad.auto.axolotyl.machine.core.slot.SlotType;
import eng.cad.auto.axolotyl.machine.core.interfaces.IMachineLogic;
import eng.cad.auto.axolotyl.machine.core.interfaces.IRightClickBehaviour;
import eng.cad.auto.axolotyl.machine.core.ui.GenericUI;
import eng.cad.auto.axolotyl.machine.core.interfaces.IMenuSlotLogic;
import eng.cad.auto.axiom.registry.MachineRegistry;
import eng.cad.auto.axiom.registry.BlockRegistry;
import eng.cad.auto.axolotyl.machine.multiblock.GenericFollowerBlock;
import eng.cad.auto.axolotyl.machine.multiblock.GenericFollowerEntity;
import eng.cad.auto.axolotyl.machine.multiblock.GenericMasterBlock;
import eng.cad.auto.axolotyl.machine.multiblock.GenericMasterEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;

public class MachineCore {
    public RegistryObject<BlockEntityType<? extends GenericMachineEntity>> blockEntity;
    public RegistryObject<Item> blockItem;
    public RegistryObject<Block> block;
    public final Config config;
    public GenericUI ui;

    protected MachineCore(String name, Config config) {
        AxiomMod.LOGGER.info("Registering new machine of "+name);
        this.config = config;

        block = BlockRegistry.registerMachineBlock(
                name,
                switch(config.machineType){
                case MACHINE -> () -> new GenericMachineBlock(
                                BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK),
                                blockEntity,
                                config);
                case CONTROLLER -> () -> new GenericMasterBlock(
                        BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK),
                        blockEntity,
                        config);
                case FOLLOWER -> () -> new GenericFollowerBlock(
                        BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK),
                        blockEntity,
                        config);
                }
        );

        blockItem = ItemRegistry.registerBlockItem(name, block);

        blockEntity = MachineRegistry.MACHINES.register(name, switch(config.machineType){
                    case MACHINE -> () ->
                        BlockEntityType.Builder.of(
                        (blockPos, blockState) ->
                            new GenericMachineEntity(
                                blockEntity.get(),
                                blockPos,
                                blockState,
                                config),
                        block.get()
                        ).build(null);
                            case CONTROLLER -> () ->
                                    BlockEntityType.Builder.of(
                                            (blockPos, blockState) ->
                                                    new GenericMasterEntity(
                                                            blockEntity.get(),
                                                            blockPos,
                                                            blockState,
                                                            config),
                                            block.get()
                                    ).build(null);
                            case FOLLOWER -> () ->
                                    BlockEntityType.Builder.of(
                                            (blockPos, blockState) ->
                                                    new GenericFollowerEntity(
                                                            blockEntity.get(),
                                                            blockPos,
                                                            blockState,
                                                            config),
                                            block.get()
                                    ).build(null);
                        }
                );
        if (!config.noGui) {
            ui = new GenericUI(config, blockEntity);
        }

    }


    //This is getting comically long :(
    public record Config(boolean noGui,
                         int itemSlots,
                         int fluidSlots,
                         int dataSlots,
                         IMachineLogic serverLogic,
                         IMachineClientLogic clientLogic,
                         IMenuSlotLogic menuSlots,
                         IRightClickBehaviour rightClickLogic,
                         String name,
                         HashMap<Integer, SlotConfig> itemSlotConfig,
                         HashMap<Integer, SlotConfig> fluidSlotConfig,
                         Builder.MACHINETYPE machineType) {
    }

    public static class Builder {
        // This only runs once per machine so it probably actually doesn't matter that I hate this data structure :<
        // Feel free to replace it with something pretty!
        public HashMap<SlotType, HashMap<Integer, SlotConfig>> slotConfig = new HashMap<>();

        int itemSlots = 0;
        int fluidSlots = 0;
        int dataSlots = 0;

        boolean noGui = false;
        public String name;

        MACHINETYPE machineType = MACHINETYPE.MACHINE;

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
            public void saveInventory(GenericMachineEntity blockEntity, CompoundTag tag) {
                AxiomMod.LOGGER.warn("No custom behaviour defined for saveInventory, data will NOT PERSIST");
            }

            @Override
            public void loadInventory(GenericMachineEntity blockEntity, CompoundTag tag) {
                AxiomMod.LOGGER.warn("No custom behaviour defined for loadInventory, data will NOT PERSIST");
            }

            @Override
            public void resetProgress(GenericMachineEntity blockEntity) {
                AxiomMod.LOGGER.warn("No custom behaviour defined for resetProgress");
            }

            @Override
            public void incrementProgress(GenericMachineEntity blockEntity, int count) {
                AxiomMod.LOGGER.warn("No custom behaviour defined for incrementProgress");
            }
        };

        IMachineClientLogic clientLogic = new IMachineClientLogic() {
            @Override
            public void renderLogic(GuiGraphics guiGraphics, float tick, int mouse_x, int mouse_y, GenericUI.Screen screen) {
                AxiomMod.LOGGER.warn("No custom behaviour defined for renderLogic");
            }

            @Override
            public void clientTick(Level level, BlockPos pos, BlockState state, GenericMachineEntity blockEntity) {
                AxiomMod.LOGGER.warn("No custom behaviour defined for clientTick");
            }
        };

        IRightClickBehaviour rightClickLogic = new IRightClickBehaviour() {
            @Override
            public InteractionResult useMachine(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult result) {
                if (level.isClientSide()) return InteractionResult.SUCCESS;
                if (player.isShiftKeyDown()) return InteractionResult.CONSUME;
                if (level.getBlockEntity(blockPos) instanceof GenericMachineEntity machine){
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


        public Builder modifySlot(int slot, SlotType slotType, int limit, SlotIORestriction restriction){
            if (!slotConfig.containsKey(slotType)) return this;
            slotConfig.get(slotType).put(slot, new SlotConfig(slotType, slot, restriction, limit));
            return this;
        }

        public Builder addItemSlots(int number) {
            if (!slotConfig.containsKey(SlotType.ITEM)) slotConfig.put(SlotType.ITEM, new HashMap<Integer, SlotConfig>());
            for (int idx = itemSlots; idx<(itemSlots+number); idx++){
                slotConfig.get(SlotType.ITEM).put(idx, new SlotConfig(SlotType.ITEM, idx, SlotIORestriction.IN_OUT, 64));
            }
            itemSlots += number;
            return this;
        }

        public Builder addFluidSlots(int number) {

            if (!slotConfig.containsKey(SlotType.FLUID)) slotConfig.put(SlotType.FLUID, new HashMap<Integer, SlotConfig>());

            for (int idx = fluidSlots; idx<(fluidSlots + number); idx++){
                slotConfig.get(SlotType.FLUID).put(idx, new SlotConfig(SlotType.FLUID, idx, SlotIORestriction.IN_OUT, 16000));
            }
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
        public Builder noGui(){
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
                    name,
                    slotConfig.get(SlotType.ITEM),
                    slotConfig.get(SlotType.FLUID),
                    machineType
                    );
        }

        public MachineCore build() {
            Config machineData = this.buildData();
            return new MachineCore(name, machineData);
        }

        public Builder isMultiblockController() {
            machineType = MACHINETYPE.CONTROLLER;
            return this;
        }

        public Builder isMultiblockFollower() {
            machineType = MACHINETYPE.FOLLOWER;
            return this;
        }

        public Builder rightClickLogic(IRightClickBehaviour rightClickLogic) {
            this.rightClickLogic = rightClickLogic;
            return this;
        }

        public enum MACHINETYPE {
            MACHINE, CONTROLLER, FOLLOWER;
        }
    }
}
