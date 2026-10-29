package eng.cad.auto.axolotyl.machine.core.ui;

import eng.cad.auto.axiom.AxiomMod;
import eng.cad.auto.axolotyl.fluid.FluidStackHandler;
import eng.cad.auto.axolotyl.machine.core.GenericMachineEntity;
import eng.cad.auto.axolotyl.machine.core.MachineCore;
import eng.cad.auto.axolotyl.network.FluidNetwork;
import eng.cad.auto.axolotyl.network.FluidSyncPacket;
import eng.cad.auto.axiom.registry.MachineRegistry;
import eng.cad.auto.axiom.registry.MenuRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
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

public class GenericUI {
    public RegistryObject<MenuType<Menu>> menu;
    public GenericUI(MachineCore.Config config, RegistryObject<BlockEntityType<? extends GenericMachineEntity>> blockEntity) {
        menu = MenuRegistry.registerMenu(config.name(), Menu::new);
    }

    public class Menu extends AbstractContainerMenu {
        public final GenericMachineEntity blockEntity;
        public final Level level;
        public final ContainerData data;
        public final FluidStackHandler tanks;

        public Menu(int containerID, Inventory inventory, FriendlyByteBuf byteBuffer) {
            this(containerID, inventory, (GenericMachineEntity) inventory.player.level().getBlockEntity(byteBuffer.readBlockPos()));
        }

        public Menu(int containerID, Inventory inventory, GenericMachineEntity blockEntity) {
            super(menu.get(), containerID);
            this.level = inventory.player.level();
            this.blockEntity = blockEntity;
            this.tanks = blockEntity.get().tanks;

            if (inventory.player instanceof ServerPlayer player
                    && this.tanks != null) {
                for (int idx = 0; idx < this.tanks.getTanks(); idx++) {
                    FluidNetwork.sendToClient(
                            player,
                            new FluidSyncPacket(
                                    blockEntity.getBlockPos(),
                                    idx,
                                    this.tanks.getFluidInTank(idx)
                            )
                    );
                }
            }
            this.data = blockEntity.get().metadata;
            addDataSlots(data);
            checkContainerSize(inventory, blockEntity.machineConfig.itemSlots());

            blockEntity.machineConfig.menuSlots().loadMenuSlots(this);
            AddPlayerInventory(inventory);
            AddPlayerHotbar(inventory);
        }

        @Override
        public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
            AxiomMod.LOGGER.warn("Failed to overwrite quickMoveStack in {}", blockEntity.machineConfig.name());
            return ItemStack.EMPTY;
        }

        @Override
        public boolean stillValid(Player player) {
            return stillValid(ContainerLevelAccess.create(player.level(), blockEntity.getBlockPos()), player,
                    MachineRegistry.MACHINELIST.get(blockEntity.machineConfig.name()).block.get());
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
            guiGraphics.blit(GUI, leftPos, topPos, 0, 0, imageWidth, imageHeight);
            this.getMenu().blockEntity.machineConfig.clientLogic().renderLogic(guiGraphics, tick, mouse_x, mouse_y, this);

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