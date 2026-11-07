package eng.cad.auto.axiology.machines;

import com.mojang.blaze3d.systems.RenderSystem;
import eng.cad.auto.axiology.core.DataSlotHandler;
import eng.cad.auto.axiology.core.ISlotData;
import eng.cad.auto.axiology.core.ISlotFluid;
import eng.cad.auto.axiology.core.MachineUI;
import eng.cad.auto.axiology.core.blocks.GuiMachineBlock;
import eng.cad.auto.axiology.core.component.*;
import eng.cad.auto.axiology.core.machine.BaseMachine;
import eng.cad.auto.axiology.core.machine.TickingMachine;
import eng.cad.auto.axiology.core.machine.GuiMachine;
import eng.cad.auto.axiology.core.slot.SlotConfig;
import eng.cad.auto.axiology.core.slot.SlotIORestriction;
import eng.cad.auto.axiology.crafting.machines.ChemicalReactorRecipe;
import eng.cad.auto.axiology.machines.util.CommonRightClickBehaviour;
import eng.cad.auto.axiology.registry.MenuRegistry;
import eng.cad.auto.axiology.registry.RecipeRegistry;
import eng.cad.auto.engine.fluid.FluidStackHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

public class ChemicalReactor extends GuiMachine<ChemicalReactor> implements IMachineProgress, IRecipeMachine<ChemicalReactorRecipe>, IRightClickable, ISlotFluid, ISlotData, IMachinePersistable, ITickable, IGuiGraphics {

    public static final String name = "chemical_reactor";
    int[] dataSync = new int[0];
    long lastSync = 0;

    public ChemicalReactor(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        this.data = new DataSlotHandler("progress");
        this.fluids = new FluidStackHandler(new SlotConfig[]{
                new SlotConfig(SlotIORestriction.INPUT, 4000),
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
    public ChemicalReactor getInstance() {
        return this;
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if(!(validateMachine(level, pos, getName()) instanceof ChemicalReactor blockEntity)) return;

        ChemicalReactorRecipe activeRecipe = findRecipe(blockEntity, level, null);

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
    public RecipeType<ChemicalReactorRecipe> recipeType() {
        return RecipeRegistry.CHEMICAL_REACTOR.get();
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
        return MenuRegistry.CHEMICAL_REACTOR.createMenu(containerId, inventory, this);
    }

    @Override
    public void loadMenuSlots(MachineUI.Menu menu) {
        // No item slots on this machine.
    }

    @Override
    public void UIRenderLogic(GuiGraphics guiGraphics, float tick, int mouse_x, int mouse_y, MachineUI.Screen screen) {
        if (!(screen.getMenu().blockEntity instanceof ChemicalReactor reactor)) return;
        ClientRender.progressBar(guiGraphics, screen, reactor);
        ClientRender.fluids(guiGraphics, screen, reactor);
    }

    @OnlyIn(Dist.CLIENT)
    private static final class ClientRender {
        // Progress bar
        static final Vector2i PROGRESS_COORDS = new Vector2i(62, 9);
        static final Vector2i PROGRESS_TEXTURE_COORDS = new Vector2i(0, 166);
        static final Vector2i PROGRESS_SIZE = new Vector2i(61, 60);

        // Fluid tanks (input, input 2, output)
        static final Vector2i[] TANK_COORDS = {
                new Vector2i(50, 22),
                new Vector2i(69, 22), //niceee
                new Vector2i(110, 22)
        };
        static final Vector2i GLASS_OVERLAY_COORDS = new Vector2i(158, 166);
        static final Vector2i TANK_SIZE = new Vector2i(16, 32);

        static void progressBar(GuiGraphics g, MachineUI.Screen screen, ChemicalReactor reactor) {
            float percentage = (float) reactor.getProgress(reactor) / (float) reactor.maxProgress();
            int width = (int) (PROGRESS_SIZE.x * percentage);
            if (width > 0) {
                g.blit(screen.GUI, screen.getGuiLeft() + PROGRESS_COORDS.x, screen.getGuiTop() + PROGRESS_COORDS.y,
                        PROGRESS_TEXTURE_COORDS.x, PROGRESS_TEXTURE_COORDS.y, width, PROGRESS_SIZE.y);
            }
        }

        static void fluids(GuiGraphics guiGraphics, MachineUI.Screen screen, ChemicalReactor blockEntity) {
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

    public static class MachineBlock extends GuiMachineBlock {
        public MachineBlock(Properties properties, RegistryObject<BlockEntityType<? extends BaseMachine<?>>> blockEntity) {
            super(properties, blockEntity);
        }
    }
}