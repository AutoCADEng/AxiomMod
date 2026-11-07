package eng.cad.auto.axiology.machines;

import com.mojang.blaze3d.systems.RenderSystem;
import eng.cad.auto.axiology.core.*;
import eng.cad.auto.axiology.core.blocks.BaseMachineBlock;
import eng.cad.auto.axiology.core.machine.BaseMachine;
import eng.cad.auto.axiology.core.machine.GuiMachine;
import eng.cad.auto.axiology.core.machine.component.*;
import eng.cad.auto.axiology.core.slot.SlotConfig;
import eng.cad.auto.axiology.core.slot.SlotIORestriction;
import eng.cad.auto.axiology.crafting.machines.FermenterRecipe;
import eng.cad.auto.axiology.registry.MenuRegistry;
import eng.cad.auto.axiology.registry.RecipeRegistry;
import eng.cad.auto.axiology.core.fluid.FluidStackHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

public class Fermenter extends GuiMachine<Fermenter> implements IMachineProgress, IRecipeMachine<FermenterRecipe>, IRightClickable, ISlotFluid, ISlotItem, ISlotData, IMachinePersistable, ITickable {

    public static final String name = "fermenter";

    static final int INPUT_TANK = 0;

    int[] dataSync = new int[0];
    long lastSync = 0;

    public Fermenter(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        this.data = new DataSlotHandler("progress");
        this.items = new ExtendedItemStackHandler(new SlotConfig[]{
                new SlotConfig(SlotIORestriction.INPUT, 64),
                new SlotConfig(SlotIORestriction.INPUT, 64)}
        );

        this.fluids = new FluidStackHandler(new SlotConfig[]{
                new SlotConfig(SlotIORestriction.INPUT, 4000),
                new SlotConfig(SlotIORestriction.OUTPUT, 4000)
        }, this::onFluidContentsChanged);

        refreshCapabilities();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Fermenter getInstance() {
        return this;
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (!(validateMachine(level, pos, getName()) instanceof Fermenter blockEntity)) return;

        FermenterRecipe activeRecipe = findRecipe(blockEntity, level, null);

        if (activeRecipe == null || !activeRecipe.hasSpace(blockEntity, activeRecipe)) {
            resetProgress(blockEntity);
            return;
        }

        if (progressCompleted(blockEntity)) {
            if (activeRecipe.validate(blockEntity, activeRecipe)) activeRecipe.craft(blockEntity, activeRecipe);
            resetProgress(blockEntity);
            return;
        }

        incrementProgress(blockEntity, 1);
    }

    @Override
    public RecipeType<FermenterRecipe> recipeType() {
        return RecipeRegistry.FERMENTATION.get();
    }

    @Override
    public int progressSlot() {
        return getDataSlots().indexOf("progress");
    }

    @Override
    public int maxProgress() {
        return 24000 / 60;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.axiology." + name);
    }

    @Override
    public String getScreenName() {
        return name;
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, @NotNull Inventory inventory, @NotNull Player player) {
        return MenuRegistry.FERMENTER.createMenu(containerId, inventory, this);
    }

    @Override
    public void loadMenuSlots(MachineUI.Menu menu) {
        menu.addMachineSlot(new SlotItemHandler(this.items, 0, 34, 22));
        menu.addMachineSlot(new SlotItemHandler(this.items, 1, 34, 40));
    }

    @Override
    public void UIRenderLogic(GuiGraphics guiGraphics, float tick, int mouse_x, int mouse_y, MachineUI.Screen screen) {
        if (!(screen.getMenu().blockEntity instanceof Fermenter fermenter)) return;
        ClientRender.progressBar(guiGraphics, screen, fermenter);
        ClientRender.fluids(guiGraphics, screen, fermenter);
    }

    @OnlyIn(Dist.CLIENT)
    private static final class ClientRender {
        static final Vector2i PROGRESS_COORDS = new Vector2i(61, 17);
        static final Vector2i PROGRESS_TEXTURE_COORDS = new Vector2i(0, 166);
        static final Vector2i PROGRESS_SIZE = new Vector2i(61, 39);

        static final Vector2i[] TANK_COORDS = {
                new Vector2i(55, 23),
                new Vector2i(126, 23)
        };
        static final Vector2i GLASS_OVERLAY_COORDS = new Vector2i(61, 166);
        static final Vector2i TANK_SIZE = new Vector2i(16, 32);

        static void progressBar(GuiGraphics g, MachineUI.Screen screen, Fermenter fermenter) {
            float percentage = (float) fermenter.getProgress(fermenter) / (float) fermenter.maxProgress();
            int width = (int) (PROGRESS_SIZE.x * percentage);
            if (width > 0) {
                g.blit(screen.GUI, screen.getGuiLeft() + PROGRESS_COORDS.x, screen.getGuiTop() + PROGRESS_COORDS.y,
                        PROGRESS_TEXTURE_COORDS.x, PROGRESS_TEXTURE_COORDS.y, width, PROGRESS_SIZE.y);
            }
        }

        static void fluids(GuiGraphics guiGraphics, MachineUI.Screen screen, Fermenter blockEntity) {
            FluidStackHandler tanks = blockEntity.getFluidSlots();
            for (int idx = 0; idx < TANK_COORDS.length; idx++) {
                FluidStack stack = tanks.getFluidInTank(idx);
                float level = (float) stack.getAmount() / (float) tanks.getTankCapacity(idx);
                if (level <= 0) continue;
                IClientFluidTypeExtensions ext = IClientFluidTypeExtensions.of(stack.getFluid().getFluidType());
                renderTank(screen, guiGraphics, ext.getStillTexture(), TANK_COORDS[idx], ext.getTintColor(), level);
            }

            for (Vector2i coords : TANK_COORDS) {
                guiGraphics.blit(screen.GUI, screen.getGuiLeft() + coords.x, screen.getGuiTop() + coords.y,
                        GLASS_OVERLAY_COORDS.x, GLASS_OVERLAY_COORDS.y, TANK_SIZE.x, TANK_SIZE.y);
            }
        }

        static void renderTank(MachineUI.Screen screen, GuiGraphics g, ResourceLocation fluidTexture, Vector2i pos, int tint, float level) {
            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(fluidTexture);
            setTint(tint);
            g.blit(
                    screen.getGuiLeft() + pos.x,
                    screen.getGuiTop() + pos.y + (int) (TANK_SIZE.y * (1.0f - level)),
                    0,
                    TANK_SIZE.x,
                    (int) (TANK_SIZE.y * level),
                    sprite
            );
            setTint(0xFFFFFFFF);
        }

        static void setTint(int color) {
            int alpha = (color & 0xFF000000) >>> 24;
            int red = (color & 0x00FF0000) >>> 16;
            int green = (color & 0x0000FF00) >>> 8;
            int blue = (color & 0x000000FF);
            RenderSystem.setShaderColor(red / 255f, green / 255f, blue / 255f, alpha / 255f);
        }
    }

    @Override
    public FluidStackHandler getFluidSlots() {
        return this.fluids;
    }

    @Override
    public ExtendedItemStackHandler getItemSlots() {
        return this.items;
    }

    @Override
    public DataSlotHandler getDataSlots() {
        return this.data;
    }

    @Override
    public int[] getLastSyncedData() {
        return dataSync;
    }

    @Override
    public void setLastSyncedData(int[] data) {
        this.dataSync = data;
    }

    @Override
    public long getLastSyncTime() {
        return lastSync;
    }

    @Override
    public void setLastSyncTime(long time) {
        lastSync = time;
    }

    public static class MachineBlock extends BaseMachineBlock {
        public MachineBlock(Properties properties, RegistryObject<BlockEntityType<? extends BaseMachine<?>>> blockEntity) {
            super(properties, blockEntity);
        }
    }
}