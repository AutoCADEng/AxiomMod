package eng.cad.auto.axiom.machines;


import com.mojang.blaze3d.systems.RenderSystem;
import eng.cad.auto.axiom.machines.util.CommonRightClickBehaviour;
import eng.cad.auto.axolotyl.crafting.core.CustomCraftingContainer;
import eng.cad.auto.axolotyl.crafting.core.GenericRecipe;
import eng.cad.auto.axolotyl.crafting.util.CraftingUtil;
import eng.cad.auto.axolotyl.item.CustomBucket;
import eng.cad.auto.axolotyl.machine.core.GenericMachineEntity;
import eng.cad.auto.axolotyl.machine.core.interfaces.IRightClickBehaviour;
import eng.cad.auto.axolotyl.machine.core.slot.SlotIORestriction;
import eng.cad.auto.axolotyl.machine.core.slot.SlotType;
import eng.cad.auto.axolotyl.machine.core.ui.GenericUI;
import eng.cad.auto.axolotyl.client.machine.core.IMachineClientLogic;
import eng.cad.auto.axolotyl.machine.core.interfaces.IMachineLogic;
import eng.cad.auto.axolotyl.machine.core.MachineCore;
import eng.cad.auto.axolotyl.machine.core.interfaces.IMenuSlotLogic;
import eng.cad.auto.axiom.registry.MachineRegistry;
import eng.cad.auto.axiom.registry.RecipeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.SlotItemHandler;
import org.joml.Vector2i;

import java.util.Objects;
import java.util.Optional;

public class FermenterMachine {
    MachineCore INSTANCE;
    public static final String name = "fermenter";
    public FermenterMachine() {
        this.INSTANCE = MachineRegistry.registerMachine(new MachineCore
                .Builder(name)
                .addItemSlots(2)
                .addFluidSlots(2)
                .modifySlot(0, SlotType.ITEM, 64, SlotIORestriction.INPUT)
                .modifySlot(1, SlotType.ITEM, 64, SlotIORestriction.INPUT)
                .modifySlot(0, SlotType.FLUID, 4000, SlotIORestriction.INPUT)
                .modifySlot(1, SlotType.FLUID, 4000, SlotIORestriction.OUTPUT)
                .addDataSlots(1)
                .addServerLogic(new Logic.Server())
                .addClientLogic(new Logic.Client())
                .rightClickLogic(new Logic.RightClickLogic())
                .addMenuSlots(new Logic.MenuInit())
                .build());
    }
    static class Logic {

        static final int Max_Processing_Ticks = 24000/60;

        static final int input_tank = 0;
        static final int output_tank = 1;

        static final int inputSlot0 = 0;
        static final int inputSlot1 = 1;

        static final int progressBar_Data = 0;

        static class Server implements IMachineLogic {
            @Override
            public void serverTick(Level level, BlockPos pos, BlockState state) {
                if(level.isClientSide()) return;
                if (!(level.getBlockEntity(pos) instanceof GenericMachineEntity blockEntity)) return;
                if (!(Objects.equals(blockEntity.machineConfig.name(), name))) return;
                GenericRecipe activeRecipe = CraftingUtil.getActiveRecipe(
                        new CustomCraftingContainer(blockEntity)
                        .addTank(blockEntity.get().tanks.getFluidInTank(input_tank))
                        .addTank(blockEntity.get().tanks.getFluidInTank(output_tank))
                        .addSlot(blockEntity.get().inventory.getStackInSlot(inputSlot0))
                        .addSlot(blockEntity.get().inventory.getStackInSlot(inputSlot1)),
                        RecipeRegistry.FERMENTATION.get(),
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

                if (blockEntity.get().metadata.get(progressBar_Data) >= Max_Processing_Ticks) {
                    if (CraftingUtil.validate(blockEntity, activeRecipe)) CraftingUtil.craft(blockEntity, activeRecipe);
                    resetProgress(blockEntity);
                    return;
                }
                incrementProgress(blockEntity,1);
            }

            @Override
            public void saveInventory(GenericMachineEntity entity, CompoundTag tag) {
                if (entity.get().inventory != null) {
                    tag.put("Inventory", entity.get().inventory.serializeNBT());
                }
                if (entity.get().tanks != null) {
                    tag.put("Tanks", entity.get().tanks.serializeNBT());
                }
                tag.putInt("Progress", entity.get().metadata.get(progressBar_Data));
            }

            @Override
            public void loadInventory(GenericMachineEntity entity, CompoundTag tag) {
                if (entity.get().inventory != null && tag.contains("Inventory")) {
                    entity.get().inventory.deserializeNBT(tag.getCompound("Inventory"));
                }
                if (entity.get().tanks != null && tag.contains("Tanks")) {
                    entity.get().tanks.deserializeNBT(tag.getCompound("Tanks"));
                }
                if (tag.contains("Progress")) entity.get().metadata.set(progressBar_Data, tag.getInt("Progress"));
            }

            @Override
            public void resetProgress(GenericMachineEntity blockEntity) {
                blockEntity.get().metadata.set(progressBar_Data, 0);
            }
            @Override
            public void incrementProgress(GenericMachineEntity blockEntity, int value) {
                blockEntity.get().metadata.set(progressBar_Data, Math.min(Max_Processing_Ticks, blockEntity.get().metadata.get(progressBar_Data) + value));
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
            public void clientTick(Level level, BlockPos pos, BlockState state, GenericMachineEntity blockEntity) {
                //Do nothing yet
            }

            private void doRenderFluids(GuiGraphics guiGraphics, GenericUI.Screen screen, int mouseX, int mouseY, float tick) {

                float waterTankLevel = (float) screen.getMenu().tanks.getFluidInTank(input_tank).getAmount() / (float) screen.getMenu().blockEntity.machineConfig.fluidSlotConfig().get(input_tank).capacity();
                if (waterTankLevel>0){
                    FluidType waterType = screen.getMenu().tanks.getFluidInTank(input_tank).getFluid().getFluidType();
                    ResourceLocation waterTankResource = IClientFluidTypeExtensions.of(waterType).getStillTexture();
                    renderFluidTank(screen, guiGraphics, waterTankResource, water_tank_coords, tank_size, IClientFluidTypeExtensions.of(waterType).getTintColor(), waterTankLevel);
                }

                float ethanolTankLevel = (float) screen.getMenu().tanks.getFluidInTank(output_tank).getAmount() / (float)  screen.getMenu().blockEntity.machineConfig.fluidSlotConfig().get(output_tank).capacity();
                if (ethanolTankLevel>0) {
                    FluidType ethanolType = screen.getMenu().tanks.getFluidInTank(output_tank).getFluid().getFluidType();
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
                ui.addMachineSlot(new SlotItemHandler( ui.blockEntity.get().inventory,0, 34,22));
                ui.addMachineSlot(new SlotItemHandler( ui.blockEntity.get().inventory,1, 34,40));
            }
        }
        static class RightClickLogic implements IRightClickBehaviour {

            @Override
            public InteractionResult useMachine(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult result) {
                if (!(level.getBlockEntity(blockPos) instanceof GenericMachineEntity machine)) return InteractionResult.PASS;
                if (!machine.machineConfig.name().equals(SoakingDrum.name)) return InteractionResult.PASS;
                boolean success = onRightClick(machine, blockState, level, blockPos, player, interactionHand, result);
                return success? InteractionResult.SUCCESS : InteractionResult.PASS;
            }

            private boolean onRightClick(GenericMachineEntity blockEntity, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult result) {
                if (player.isShiftKeyDown()) return false;
                if (interactionHand == InteractionHand.OFF_HAND) return false;
                return CommonRightClickBehaviour.bucketInHand(0, blockEntity, blockState, level, blockPos, player, interactionHand, result);
            }

        }
    }
}
