package eng.cad.auto.axiology.core;

import eng.cad.auto.axiology.AxiomMod;
import eng.cad.auto.axiology.core.machine.component.IGuiGraphics;
import eng.cad.auto.axiology.core.machine.BaseMachine;
import eng.cad.auto.axiology.core.machine.GuiMachine;
import eng.cad.auto.axiology.core.network.AxiologyNetwork;
import eng.cad.auto.axiology.core.network.packet.MachineFluidSyncPacket;
import eng.cad.auto.axiology.registry.MenuRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.ParametersAreNonnullByDefault;

public class MachineUI {
    public final RegistryObject<MenuType<Menu>> MENU_TYPE;
    public MachineUI(String name, RegistryObject<BlockEntityType<? extends BaseMachine<?>>> blockEntity) {
        MENU_TYPE = MenuRegistry.registerMenu(name, Menu::new);
    }

    public Menu createMenu(int containerID, Inventory inventory, BaseMachine<?> blockEntity){
        return new Menu(containerID, inventory, blockEntity);
    }

    public Screen createScreen(String name, Menu menu, Inventory inventory, Component component){
        return new Screen(name, menu, inventory, component);
    }

    public class Menu extends AbstractContainerMenu {
        public final BaseMachine<?> blockEntity;
        public final Level level;


        public Menu(int containerID, Inventory inventory, FriendlyByteBuf byteBuffer) {
                this(containerID, inventory, (BaseMachine<?>) inventory.player.level().getBlockEntity(byteBuffer.readBlockPos()));
        }

        public Menu(int containerID, Inventory inventory, BaseMachine<?> blockEntity) {
            super(MENU_TYPE.get(), containerID);
            this.level = inventory.player.level();
            this.blockEntity = blockEntity;

            if (!(blockEntity instanceof IGuiGraphics graphics)) {
                AxiomMod.LOGGER.error("IGuiGraphics was absent");
            } else {
                graphics.loadMenuSlots(this);
            }

            if (blockEntity instanceof ISlotFluid tankEntity){
                if (inventory.player instanceof ServerPlayer player) {
                    for (int idx = 0; idx < tankEntity.getFluidSlots().getTanks(); idx++) {
                        AxiologyNetwork.sendToClient(player, new MachineFluidSyncPacket(blockEntity.getBlockPos(), idx, tankEntity.getFluidSlots().getFluidInTank(idx)));
                    }
                }
            }
            if (blockEntity instanceof ISlotData dataEntity) {
                if (dataEntity.getDataSlots().getCount()>0){
                    addDataSlots(dataEntity.getDataSlots());
                }
            }

            if (blockEntity instanceof ISlotItem itemEntity) {
                checkContainerSize(inventory, itemEntity.getItemSlots().getSlots());
            }

            AddPlayerInventory(inventory);
            AddPlayerHotbar(inventory);
        }

        @Override
        public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
            return ItemStack.EMPTY;
        }

        @Override
        public boolean stillValid(Player player) {
            if (!(blockEntity instanceof GuiMachine guiMachine)) return false;
            if (guiMachine.isRemoved()) return false;
            BlockPos pos = guiMachine.getBlockPos();
            return player.level().getBlockEntity(pos) == guiMachine && player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0;
        }

        private void AddPlayerInventory(Inventory playerInventory){
            int row_len = 9;
            int col_len = 3;
            int inventory_start_pos_x = 8;
            int inventory_start_pos_y = 84;
            int grid_size = 18;

            for (int row = 0; row<col_len; row++){
                for (int column=0; column<row_len;column++){
                    this.addSlot(new Slot(playerInventory,
                            column+row*row_len + row_len,
                            inventory_start_pos_x + column * grid_size,
                            inventory_start_pos_y + row * grid_size));
                }
            }
        }

        private void AddPlayerHotbar(Inventory playerInventory){
            int row_len = 9;
            int hotbar_start_pos_x = 8;
            int hotbar_start_pos_y = 142;
            int grid_size = 18;
            for (int col = 0; col<row_len; col++){
                this.addSlot(new Slot(playerInventory,
                        col,
                        hotbar_start_pos_x + col * grid_size,
                        hotbar_start_pos_y));
            }
        }

        public void addMachineSlot(Slot slot) {
            addSlot(slot);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public class Screen extends AbstractContainerScreen<Menu> {
        public final ResourceLocation GUI;
        public Screen(String name, Menu menu, Inventory inventory, Component component) {
            super(menu, inventory, component);
            GUI = ResourceLocation.fromNamespaceAndPath(AxiomMod.MODID, "menu/"+name+"_menu.png");
        }

        @Override
        protected void renderBg(GuiGraphics guiGraphics, float tick, int mouse_x, int mouse_y) {
            if (!(this.getMenu().blockEntity instanceof IGuiGraphics guiMachine)) return;
            guiGraphics.blit(GUI, leftPos, topPos, 0, 0, imageWidth, imageHeight);
            guiMachine.UIRenderLogic(guiGraphics, tick, mouse_x, mouse_y, this);
        }

        @Override @ParametersAreNonnullByDefault
        public void render(GuiGraphics guiGraphics, int mouse_x, int mouse_y, float tick) {
            renderBackground(guiGraphics);
            super.render(guiGraphics, mouse_x, mouse_y, tick);
            renderTooltip(guiGraphics, mouse_x, mouse_y);
        }
        @Override
        protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
            guiGraphics.drawString(
                    this.font,
                    Component.translatable("container.inventory"),
                    8,
                    72,
                    0x404040,
                    false
            );
        }
        @Override
        public void init(){
            super.init();
        }
    }
}