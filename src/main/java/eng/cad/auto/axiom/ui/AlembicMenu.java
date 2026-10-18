package eng.cad.auto.axiom.ui;

import eng.cad.auto.axiom.axiomancy.blockentitiy.AlembicBlockEntity;
import eng.cad.auto.axiom.registry.BlockRegistry;
import eng.cad.auto.axiom.registry.MenuRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;


import javax.annotation.ParametersAreNonnullByDefault;

public class AlembicMenu extends AbstractContainerMenu{
    public final AlembicBlockEntity blockEntity;
    public final Level level;
    public final ContainerData data;
    public static int inputSlotsX = 35;
    public static int inputSlotsY = 30;
    public static int outputSlotsX = 110;
    public static int outputSlotsY = 17;
    public AlembicMenu(int containerID, Inventory inventory, FriendlyByteBuf byteBuffer) {
        this(containerID, inventory, inventory.player.level().getBlockEntity(byteBuffer.readBlockPos()), new SimpleContainerData(2));
    }

    public AlembicMenu(int containerID, Inventory inventory, BlockEntity blockEntity, ContainerData containerData) {
        super(MenuRegistry.ALEMBIC_MENU.get(), containerID);

        checkContainerDataCount(containerData, 2);
        addDataSlots(containerData);

        checkContainerSize(inventory,5);
        this.blockEntity = ((AlembicBlockEntity) blockEntity);
        this.level = inventory.player.level();
        this.data = containerData;
        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent( itemHandler ->{
            this.addSlot(new SlotItemHandler(itemHandler, 0, inputSlotsX,inputSlotsY));
            for (int idx = 0; idx < 4; idx++) {
                int x = outputSlotsX + (idx % 2) * 18;
                int y = outputSlotsY + (idx / 2) * 18;
                this.addSlot(new SlotItemHandler(itemHandler, 1 + idx, x, y));
            }
        });
        AddPlayerInventory(inventory);
        AddPlayerHotbar(inventory);

    }

    public boolean isCrafting() {
        return data.get(0) > 0;
    }

    @Override @ParametersAreNonnullByDefault
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        if (slotIndex < 0 || slotIndex >= this.slots.size()) {
            return ItemStack.EMPTY;
        }

        Slot slot = this.slots.get(slotIndex);

        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        if (slotIndex < 5) {
            if (!this.moveItemStackTo(stack, 10, 36 + 5, true)) {
                return ItemStack.EMPTY;
            }
        }

        else {
            if (!this.moveItemStackTo(stack, 0, 5, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.getCount() == original.getCount()) {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        slot.onTake(player, stack);
        return original;
    }

    @Override @ParametersAreNonnullByDefault
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(player.level(), blockEntity.getBlockPos()), player, BlockRegistry.ALEMBIC.get());
    }

    private void AddPlayerInventory(Inventory playerInventory){
        for (int i = 0; i<3; i++){
            for (int j=0; j<9;j++){
                this.addSlot(new Slot(playerInventory,j+i*9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
    }

    private void AddPlayerHotbar(Inventory playerInventory){
        for (int i = 0; i<9; i++){
                this.addSlot(new Slot(playerInventory,i, 8 + i * 18, 142));
        }
    }
}

