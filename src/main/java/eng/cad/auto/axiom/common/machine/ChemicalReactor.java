package eng.cad.auto.axiom.common.machine;


import com.mojang.blaze3d.systems.RenderSystem;
import eng.cad.auto.axiom.common.crafting.core.CustomCraftingContainer;
import eng.cad.auto.axiom.common.crafting.core.GenericRecipe;
import eng.cad.auto.axiom.common.crafting.util.CraftingUtil;
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
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.items.SlotItemHandler;
import org.joml.Vector2i;

import java.util.Objects;

public class ChemicalReactor {
    MachineHolder INSTANCE;
    public static final String name = "chemical_reactor";
    public ChemicalReactor() {
        this.INSTANCE = MachineRegistry.registerMachine(new MachineHolder
                .Builder(name)
                .addFluidSlots(3)
                .modifySlot(0, SlotType.FLUID, 4000, SlotIORestriction.INPUT)
                .modifySlot(1, SlotType.FLUID, 4000, SlotIORestriction.INPUT)
                .modifySlot(2, SlotType.FLUID, 4000, SlotIORestriction.OUTPUT)
                .addDataSlots(1)
                .addServerLogic(new Logic.Server())
                .addClientLogic(new Logic.Client())
                .build());
    }
    static class Logic {

        static final int Max_Processing_Ticks = 24000/60;

        static final int input_tank = 0;
        static final int input_tank_2 = 1;
        static final int output_tank = 2;


        static final int progressBar_Data = 0;

        static class Server implements IMachineLogic {
            @Override
            public void serverTick(Level level, BlockPos pos, BlockState state) {
                if(level.isClientSide()) return;
                if (!(level.getBlockEntity(pos) instanceof GenericBlockEntity blockEntity)) return;
                if (!(Objects.equals(blockEntity.machineConfig.name(), name))) return;
                GenericRecipe activeRecipe = CraftingUtil.getActiveRecipe(
                        new CustomCraftingContainer(blockEntity)
                                .addTank(blockEntity.machine.tanks.getFluidInTank(input_tank))
                                .addTank(blockEntity.machine.tanks.getFluidInTank(input_tank_2))
                                .addTank(blockEntity.machine.tanks.getFluidInTank(output_tank)),
                        RecipeRegistry.CHEMICAL_REACTOR.get(),
                        level,
                        blockEntity);

                if (activeRecipe == null){
                    resetProgress(blockEntity);
                    return;
                }

                if (!CraftingUtil.hasSpace(blockEntity, activeRecipe)) {
                    resetProgress(blockEntity);
                    return;
                }

                if (blockEntity.machine.metadata.get(progressBar_Data) >= Max_Processing_Ticks) {
                    if (CraftingUtil.validate(blockEntity, activeRecipe)) CraftingUtil.craft(blockEntity, activeRecipe);
                    resetProgress(blockEntity);
                    return;
                }

                incrementProgress(blockEntity,1);
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
            public Vector2i progress_coords = new Vector2i(62,9);
            public Vector2i progress_texture_coords = new Vector2i(0,166);
            public Vector2i progress_size = new Vector2i(61,60);


            //Fluid Tanks
            public Vector2i input_tank_coords = new Vector2i(50,22);
            public Vector2i input_tank_2_coords = new Vector2i(69,22); //niceee
            public Vector2i output_tank_coords = new Vector2i(110,22);
            public Vector2i glass_overlay_coords = new Vector2i(158, 166);
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

                float inputTank = (float) screen.getMenu().tanks.getFluidInTank(input_tank).getAmount() / (float) screen.getMenu().blockEntity.machineConfig.fluidSlotConfig().get(input_tank).capacity();
                if (inputTank>0){
                    FluidType inputType = screen.getMenu().tanks.getFluidInTank(input_tank).getFluid().getFluidType();
                    ResourceLocation inputTankResource = IClientFluidTypeExtensions.of(inputType).getStillTexture();
                    renderFluidTank(screen, guiGraphics, inputTankResource, input_tank_coords, tank_size, IClientFluidTypeExtensions.of(inputType).getTintColor(), inputTank);
                }

                float inputTank2 = (float) screen.getMenu().tanks.getFluidInTank(input_tank_2).getAmount() / (float) screen.getMenu().blockEntity.machineConfig.fluidSlotConfig().get(input_tank_2).capacity();
                if (inputTank2>0){
                    FluidType inputType2 = screen.getMenu().tanks.getFluidInTank(input_tank_2).getFluid().getFluidType();
                    ResourceLocation inputTankResource2 = IClientFluidTypeExtensions.of(inputType2).getStillTexture();
                    renderFluidTank(screen, guiGraphics, inputTankResource2, input_tank_2_coords, tank_size, IClientFluidTypeExtensions.of(inputType2).getTintColor(), inputTank2);
                }

                float outputTank = (float) screen.getMenu().tanks.getFluidInTank(output_tank).getAmount() / (float) screen.getMenu().blockEntity.machineConfig.fluidSlotConfig().get(output_tank).capacity();
                if (outputTank>0){
                    FluidType outputType = screen.getMenu().tanks.getFluidInTank(output_tank).getFluid().getFluidType();
                    ResourceLocation outputTankResource = IClientFluidTypeExtensions.of(outputType).getStillTexture();
                    renderFluidTank(screen, guiGraphics, outputTankResource, output_tank_coords, tank_size, IClientFluidTypeExtensions.of(outputType).getTintColor(), outputTank);
                }
                //Glass Overlays
                guiGraphics.blit(screen.GUI, screen.getGuiLeft() + input_tank_coords.x, screen.getGuiTop() + input_tank_coords.y,
                        glass_overlay_coords.x, glass_overlay_coords.y, glass_overlay_size.x, glass_overlay_size.y);
                guiGraphics.blit(screen.GUI, screen.getGuiLeft() + input_tank_2_coords.x, screen.getGuiTop() + input_tank_2_coords.y,
                        glass_overlay_coords.x, glass_overlay_coords.y, glass_overlay_size.x, glass_overlay_size.y);
                guiGraphics.blit(screen.GUI, screen.getGuiLeft() + output_tank_coords.x, screen.getGuiTop() + output_tank_coords.y,
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

    }
}
