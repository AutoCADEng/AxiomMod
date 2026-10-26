package eng.cad.auto.axiom.common.machine;


import com.mojang.blaze3d.systems.RenderSystem;
import eng.cad.auto.axiom.common.crafting.core.CraftingIngredient;
import eng.cad.auto.axiom.common.crafting.core.CustomCraftingContainer;
import eng.cad.auto.axiom.common.crafting.core.GenericRecipe;
import eng.cad.auto.axiom.common.machine.core.GenericBlockEntity;
import eng.cad.auto.axiom.common.machine.core.slot.SlotIORestriction;
import eng.cad.auto.axiom.common.machine.core.slot.SlotType;
import eng.cad.auto.axiom.common.machine.ui.GenericUI;
import eng.cad.auto.axiom.client.machine.core.IMachineClientLogic;
import eng.cad.auto.axiom.common.machine.interfaces.IMachineLogic;
import eng.cad.auto.axiom.common.machine.core.MachineHolder;
import eng.cad.auto.axiom.common.machine.interfaces.IMenuSlotLogic;
import eng.cad.auto.axiom.registry.MachineRegistry;
import eng.cad.auto.axiom.registry.RecipeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.joml.Vector2i;

import java.util.Objects;

public class FermenterMachine {
    MachineHolder INSTANCE;
    public static final String name = "fermenter";
    public FermenterMachine() {
        this.INSTANCE = MachineRegistry.registerMachine(new MachineHolder
                .Builder(name)
                .addItemSlots(2)
                .modifySlot(0, SlotType.ITEM, 64, SlotIORestriction.INPUT)
                .modifySlot(1, SlotType.ITEM, 64, SlotIORestriction.INPUT)
                .modifySlot(0, SlotType.FLUID, 4000, SlotIORestriction.INPUT)
                .modifySlot(1, SlotType.FLUID, 4000, SlotIORestriction.OUTPUT)
                .addDataSlots(1)
                .addFluidSlots(2)
                .addServerLogic(new Logic.Server())
                .addClientLogic(new Logic.Client())
                .addMenuSlots(new Logic.MenuInit())
                .build());
    }
    static class Logic {

        static final int Max_Processing_Ticks = 24000/60;

        static final int waterTank = 0;
        static final int ethanolTank = 1;

        static final int inputSlot0 = 0;
        static final int inputSlot1 = 1;

        static final int progressBar_Data = 0;

        static class Server implements IMachineLogic {
            @Override
            public void serverTick(Level level, BlockPos pos, BlockState state) {
                if(level.isClientSide()) return;
                if (!(level.getBlockEntity(pos) instanceof GenericBlockEntity blockEntity)) return;
                if (!(Objects.equals(blockEntity.machineConfig.name(), name))) return;
                GenericRecipe activeRecipe = GenericRecipe.getActiveRecipe(
                        new CustomCraftingContainer(blockEntity)
                        .addTank(blockEntity.machine.tanks.getFluidInTank(waterTank))
                        .addTank(blockEntity.machine.tanks.getFluidInTank(ethanolTank))
                        .addSlot(blockEntity.machine.inventory.getStackInSlot(inputSlot0))
                        .addSlot(blockEntity.machine.inventory.getStackInSlot(inputSlot1)),
                        RecipeRegistry.FERMENTATION.get(),
                        level,
                        blockEntity);

                if (activeRecipe == null){
                    resetProgress(blockEntity);
                    return;
                }

                if (!hasSpace(blockEntity, ethanolTank, activeRecipe.getCraftingOutput().get(0).getFluid())){
                    resetProgress(blockEntity);
                    return;
                }

                if (blockEntity.machine.metadata.get(progressBar_Data) >= Max_Processing_Ticks) {
                    if (activeRecipe.validateCraft(blockEntity)) craftRecipe(blockEntity, activeRecipe, false);
                    resetProgress(blockEntity);
                    return;
                }
                incrementProgress(blockEntity,1);
            }

            private boolean hasSpace(GenericBlockEntity blockEntity, int slot, FluidStack fluid) {
                FluidStack tank = blockEntity.machine.tanks.getFluidInTank(slot);
                if (!tank.isFluidEqual(fluid) && !tank.isEmpty()) return false;
                return !(tank.getAmount() + fluid.getAmount() > blockEntity.machineConfig.fluidSlotConfig().get(slot).capacity());
            }

            @Override
            public void craftRecipe(GenericBlockEntity entity, GenericRecipe recipe, boolean simulate) {
                for (CraftingIngredient ingredient:recipe.getCraftingIngredients()){
                    recipe.performCraft(ingredient, entity, simulate);
                    entity.machine.tanks.fill(recipe.getCraftingOutput().get(0).getFluid(), simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE);
                }
            }

            @Override
            public void saveInventory(GenericBlockEntity entity, CompoundTag tag) {
                if (entity.machine.inventory != null) {
                    tag.put("Inventory", entity.machine.inventory.serializeNBT());
                }
                if (entity.machine.tanks != null) {
                    tag.put("Tanks", entity.machine.tanks.serializeNBT());
                }
                tag.putInt("Progress", entity.machine.metadata.get(progressBar_Data));
            }

            @Override
            public void loadInventory(GenericBlockEntity entity, CompoundTag tag) {
                if (entity.machine.inventory != null && tag.contains("Inventory")) {
                    entity.machine.inventory.deserializeNBT(tag.getCompound("Inventory"));
                }
                if (entity.machine.tanks != null && tag.contains("Tanks")) {
                    entity.machine.tanks.deserializeNBT(tag.getCompound("Tanks"));
                }
                if (tag.contains("Progress")) entity.machine.metadata.set(progressBar_Data, tag.getInt("Progress"));
            }

            @Override
            public void resetProgress(GenericBlockEntity blockEntity) {
                blockEntity.machine.metadata.set(progressBar_Data, 0);
            }
            @Override
            public void incrementProgress(GenericBlockEntity blockEntity, int value) {
                blockEntity.machine.metadata.set(progressBar_Data, Math.min(Max_Processing_Ticks, blockEntity.machine.metadata.get(progressBar_Data) + value));
            }

        }
        static class Client implements IMachineClientLogic {

            //Progress Bar
            public Vector2i progress_coords = new Vector2i(61,17);
            public Vector2i progress_texture_coords = new Vector2i(0,166);
            public Vector2i progress_size = new Vector2i(61,39);


            //Fluid Tanks
            public Vector2i water_tank_coords = new Vector2i(55,23);
            public Vector2i ethanol_tank_coords = new Vector2i(126,23);
            public Vector2i glass_overlay_coords = new Vector2i(61, 166);
            public Vector2i glass_overlay_size = new Vector2i(16,32);
            public Vector2i tank_size = new Vector2i(16, 32);

            @Override
            public void renderLogic(GuiGraphics guiGraphics, float tick, int mouse_x, int mouse_y, GenericUI.Screen screen) {

                doProgressBar(guiGraphics, screen, mouse_x, mouse_y, tick);
                doRenderFluids(guiGraphics, screen, mouse_x, mouse_y, tick);
            }

            @Override
            public void clientTick(Level level, BlockPos pos, BlockState state, GenericBlockEntity blockEntity) {
                //Do nothing yet
            }

            private void doRenderFluids(GuiGraphics guiGraphics, GenericUI.Screen screen, int mouseX, int mouseY, float tick) {

                float waterTankLevel = (float) screen.getMenu().tanks.getFluidInTank(waterTank).getAmount() / (float) screen.getMenu().blockEntity.machineConfig.fluidSlotConfig().get(waterTank).capacity();
                if (waterTankLevel>0){
                    FluidType waterType = screen.getMenu().tanks.getFluidInTank(waterTank).getFluid().getFluidType();
                    ResourceLocation waterTankResource = IClientFluidTypeExtensions.of(waterType).getStillTexture();
                    renderFluidTank(screen, guiGraphics, waterTankResource, water_tank_coords, tank_size, IClientFluidTypeExtensions.of(waterType).getTintColor(), waterTankLevel);
                }

                float ethanolTankLevel = (float) screen.getMenu().tanks.getFluidInTank(ethanolTank).getAmount() / (float)  screen.getMenu().blockEntity.machineConfig.fluidSlotConfig().get(ethanolTank).capacity();
                if (ethanolTankLevel>0) {
                    FluidType ethanolType = screen.getMenu().tanks.getFluidInTank(ethanolTank).getFluid().getFluidType();
                    ResourceLocation ethanolTankResource = IClientFluidTypeExtensions.of(ethanolType).getStillTexture();
                    renderFluidTank(screen, guiGraphics, ethanolTankResource,  ethanol_tank_coords, tank_size, IClientFluidTypeExtensions.of(ethanolType).getTintColor(), ethanolTankLevel);
                }

                //Glass Overlays
                guiGraphics.blit(screen.GUI, screen.getGuiLeft() + water_tank_coords.x, screen.getGuiTop() + water_tank_coords.y,
                        glass_overlay_coords.x, glass_overlay_coords.y, glass_overlay_size.x, glass_overlay_size.y);
                guiGraphics.blit(screen.GUI, screen.getGuiLeft() + ethanol_tank_coords.x, screen.getGuiTop() + ethanol_tank_coords.y,
                        glass_overlay_coords.x, glass_overlay_coords.y, glass_overlay_size.x, glass_overlay_size.y);
            }

            private void doProgressBar(GuiGraphics guiGraphics, GenericUI.Screen screen, int mouseX, int mouseY, float tick) {
                float progressPercentage = (float) screen.getMenu().data.get(progressBar_Data) / (float) Max_Processing_Ticks;
                int progressWidth = (int) (progress_size.x * progressPercentage);

                if (progressWidth > 0) {
                    guiGraphics.blit(screen.GUI, screen.getGuiLeft() + progress_coords.x, screen.getGuiTop() + progress_coords.y, progress_texture_coords.x, progress_texture_coords.y, progressWidth, progress_size.y);
                }
            }



            private void renderFluidTank(GenericUI.Screen screen, GuiGraphics guiGraphics, ResourceLocation fluidResource, Vector2i tank_pos, Vector2i tank_size, int tint, float tankLevel) {
                var sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(fluidResource);
                float u0 = sprite.getU0();
                float u1 = sprite.getU1();
                float v0 = sprite.getV0();
                float v1 = sprite.getV1();

                setUITint(tint);
                guiGraphics.blit(
                        screen.getGuiLeft()+tank_pos.x,
                        screen.getGuiTop() + tank_pos.y + (int) ((float) tank_size.y * (1.0f-tankLevel)) ,
                        0,
                        tank_size.x,
                        (int) ((float)tank_size.y * tankLevel),
                        sprite
                );
                setUITint();
            }

            private ResourceLocation CheckEmpty(ResourceLocation resource, ResourceLocation defaultResource) {
                if (resource.toString().equals("minecraft:empty")) return defaultResource;
                return resource;
            }
            public void setUITint(int color){
                int alpha = (color & 0xFF000000) >>> 24;
                int red = (color & 0x00FF0000) >>> 16;
                int green = (color & 0x0000FF00) >>> 8;
                int blue = (color & 0x000000FF);
                RenderSystem.setShaderColor(red/255f, green/255f, blue/255f, alpha/255f);
            }

            public void setUITint(){
                setUITint(0xFFFFFFFF);
            }

        }
        static class MenuInit implements IMenuSlotLogic{

            @Override
            public void loadMenuSlots(GenericUI.Menu ui) {
                ui.addMachineSlot(new SlotItemHandler( ui.blockEntity.machine.inventory,0, 34,22));
                ui.addMachineSlot(new SlotItemHandler( ui.blockEntity.machine.inventory,1, 34,40));
            }
        }
    }
}
