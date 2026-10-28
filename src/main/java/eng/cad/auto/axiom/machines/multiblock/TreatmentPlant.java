package eng.cad.auto.axiom.machines.multiblock;


import eng.cad.auto.axiom.AxiomMod;
import eng.cad.auto.axiom.registry.MachineRegistry;
import eng.cad.auto.axolotyl.client.machine.core.IMachineClientLogic;
import eng.cad.auto.axolotyl.machine.core.GenericMachineEntity;
import eng.cad.auto.axolotyl.machine.core.MachineCore;
import eng.cad.auto.axolotyl.machine.core.interfaces.IMachineLogic;
import eng.cad.auto.axolotyl.machine.core.slot.SlotIORestriction;
import eng.cad.auto.axolotyl.machine.core.slot.SlotType;
import eng.cad.auto.axolotyl.machine.core.ui.GenericUI;
import eng.cad.auto.axolotyl.machine.multiblock.GenericMasterEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class TreatmentPlant {
    MachineCore INSTANCE;
    public static final String name = "treatment_plant";
    public TreatmentPlant() {
        this.INSTANCE = MachineRegistry.registerMachine(new MachineCore
                .Builder(name)
                .addFluidSlots(1)
                .addItemSlots(2)
                .modifySlot(0, SlotType.FLUID, 4000, SlotIORestriction.INPUT)
                .modifySlot(1, SlotType.FLUID, 4000, SlotIORestriction.INPUT)
                .modifySlot(2, SlotType.FLUID, 4000, SlotIORestriction.OUTPUT)
                .addDataSlots(1)
                .addServerLogic(new Logic.Server())
                .addClientLogic(new Logic.Client())
                .isMultiblockController()
                .build());
    }
    static class Logic {

        static final int Max_Processing_Ticks = 24000/60;


        static class Server implements IMachineLogic {
            @Override
            public void serverTick(Level level, BlockPos pos, BlockState state) {
                if(level.isClientSide()) return;
                if (!(level.getBlockEntity(pos) instanceof GenericMasterEntity blockEntity)) return;
                AxiomMod.LOGGER.info("==== START OF STRUCTURE CHECK ====");
                blockEntity.validateStructure(pos);
                AxiomMod.LOGGER.info("==== END OF STRUCTURE CHECK ====");
            }

            @Override
            public void saveInventory(GenericMachineEntity entity, CompoundTag tag) {
                if (!(entity instanceof GenericMasterEntity blockEntity)) return;
                if (blockEntity.getMachine().inventory != null) {
                    tag.put("Inventory", entity.getMachine().inventory.serializeNBT());
                }
                if (blockEntity.getMachine().tanks != null) {
                    tag.put("Tanks", entity.getMachine().tanks.serializeNBT());
                }
            }

            @Override
            public void loadInventory(GenericMachineEntity entity, CompoundTag tag) {
                if (!(entity instanceof GenericMasterEntity blockEntity)) return;
                if (blockEntity.getMachine().inventory != null && tag.contains("Inventory")) {
                    blockEntity.getMachine().inventory.deserializeNBT(tag.getCompound("Inventory"));
                }
                if (blockEntity.getMachine().tanks != null && tag.contains("Tanks")) {
                    blockEntity.getMachine().tanks.deserializeNBT(tag.getCompound("Tanks"));
                }
            }

            @Override
            public void resetProgress(GenericMachineEntity blockEntity) {
                if (!(blockEntity instanceof GenericMasterEntity machine)) return;

            }
            @Override
            public void incrementProgress(GenericMachineEntity blockEntity, int value) {
                if (!(blockEntity instanceof GenericMasterEntity machine)) return;

            }

        }
        static class Client implements IMachineClientLogic {

            @Override
            public void renderLogic(GuiGraphics guiGraphics, float tick, int mouse_x, int mouse_y, GenericUI.Screen screen) {
                //Let's get the menu open first ...
            }

            @Override
            public void clientTick(Level level, BlockPos pos, BlockState state, GenericMachineEntity blockEntity) {
                //Do nothing yet
            }
        }
    }
}
