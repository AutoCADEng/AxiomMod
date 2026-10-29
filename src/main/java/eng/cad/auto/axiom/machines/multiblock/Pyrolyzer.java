package eng.cad.auto.axiom.machines.multiblock;


import eng.cad.auto.axiom.registry.MachineRegistry;
import eng.cad.auto.axiom.registry.RecipeRegistry;
import eng.cad.auto.axolotyl.client.machine.core.IMachineClientLogic;
import eng.cad.auto.axolotyl.crafting.core.CustomCraftingContainer;
import eng.cad.auto.axolotyl.crafting.core.GenericRecipe;
import eng.cad.auto.axolotyl.crafting.util.CraftingUtil;
import eng.cad.auto.axolotyl.machine.core.GenericMachineEntity;
import eng.cad.auto.axolotyl.machine.core.MachineCore;
import eng.cad.auto.axolotyl.machine.core.interfaces.IMachineLogic;
import eng.cad.auto.axolotyl.machine.core.slot.SlotIORestriction;
import eng.cad.auto.axolotyl.machine.core.slot.SlotType;
import eng.cad.auto.axolotyl.machine.core.ui.GenericUI;
import eng.cad.auto.axolotyl.machine.multiblock.GenericFollowerEntity;
import eng.cad.auto.axolotyl.machine.multiblock.GenericMasterEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Objects;

public class Pyrolyzer {
    MachineCore INSTANCE;
    public static final String name = "pyrolyzer";
    public Pyrolyzer() {
        this.INSTANCE = MachineRegistry.registerMachine(new MachineCore
                .Builder(name)
                .addDataSlots(2)
                .addServerLogic(new Logic.Server())
                .addClientLogic(new Logic.Client())
                .isMultiblockController()
                .build());
    }
    static class Logic {

        static final int Max_Processing_Ticks = 24000/60;
        static final int isValid = 0;
        static final int progress = 1;
        static class Server implements IMachineLogic {
            int counter = 0;
            @Override
            public void serverTick(Level level, BlockPos pos, BlockState state) {
                if(level.isClientSide()) return;
                if (!(level.getBlockEntity(pos) instanceof GenericMasterEntity blockEntity)) return;
                counter++;

                if (counter >= 10){
                    counter = 0;
                    blockEntity.get().metadata.set(isValid, blockEntity.validateStructure(pos)?1:0); // lazy implementation for now, we will look into events when I got more time.
                }

                if (blockEntity.get().metadata.get(isValid) == 0){
                    resetProgress(blockEntity);
                    return;
                }

                if (!(Objects.equals(blockEntity.machineConfig.name(), name))) return;

                CustomCraftingContainer inventories = new CustomCraftingContainer(blockEntity);
                ArrayList<GenericFollowerEntity> followerEntities = new ArrayList<>();

                for(GenericFollowerEntity follower:blockEntity.getFollowers())
                {
                    followerEntities.add(follower);
                    inventories = inventories.merge(follower.getCraftingContainer(follower));
                }

                GenericRecipe activeRecipe = CraftingUtil.getActiveRecipe(
                        inventories,
                        RecipeRegistry.PYROLYZER.get(),
                        level,
                        blockEntity);

                if (activeRecipe == null){
                    resetProgress(blockEntity);
                    return;
                }
                if (!CraftingUtil.hasSpace(blockEntity, followerEntities, activeRecipe)) {
                    resetProgress(blockEntity);
                    return;
                }

                if (blockEntity.get().metadata.get(progress) >= Max_Processing_Ticks) {
                    if (CraftingUtil.validate(blockEntity, followerEntities, activeRecipe)) CraftingUtil.craft(blockEntity, followerEntities, activeRecipe);
                    resetProgress(blockEntity);
                    return;
                }
                incrementProgress(blockEntity, 1);
            }

            @Override
            public void saveInventory(GenericMachineEntity entity, CompoundTag tag) {
                if (!(entity instanceof GenericMasterEntity blockEntity)) return;
                if (blockEntity.get().inventory != null) {
                    tag.put("Inventory", entity.get().inventory.serializeNBT());
                }
                if (blockEntity.get().tanks != null) {
                    tag.put("Tanks", entity.get().tanks.serializeNBT());
                }

                tag.putInt("isValid", blockEntity.get().metadata.get(isValid));
                tag.putInt("progress", blockEntity.get().metadata.get(progress));
            }

            @Override
            public void loadInventory(GenericMachineEntity entity, CompoundTag tag) {
                if (!(entity instanceof GenericMasterEntity blockEntity)) return;
                if (blockEntity.get().inventory != null && tag.contains("Inventory")) {
                    blockEntity.get().inventory.deserializeNBT(tag.getCompound("Inventory"));
                }
                if (blockEntity.get().tanks != null && tag.contains("Tanks")) {
                    blockEntity.get().tanks.deserializeNBT(tag.getCompound("Tanks"));
                }

                blockEntity.get().metadata.set(isValid, tag.getInt("isValid"));
                blockEntity.get().metadata.set(progress, tag.getInt("progress"));
            }

            @Override
            public void resetProgress(GenericMachineEntity blockEntity) {
                if (!(blockEntity instanceof GenericMasterEntity machine)) return;
                machine.get().metadata.set(progress, 0);
            }
            @Override
            public void incrementProgress(GenericMachineEntity blockEntity, int value) {
                if (!(blockEntity instanceof GenericMasterEntity machine)) return;
                machine.get().metadata.set(progress, machine.get().metadata.get(progress) + value);
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
