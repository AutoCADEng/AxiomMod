package eng.cad.auto.axiology.machines.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import eng.cad.auto.axiology.machines.SoakingDrum;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class SoakingDrumRenderer implements BlockEntityRenderer<SoakingDrum> {
    public SoakingDrumRenderer(BlockEntityRendererProvider.Context context) {

    }

    private static void quad(VertexConsumer vc, Matrix4f quadPose, Matrix3f normal, Vector3f vertA, Vector3f vertB, Vector3f vertC, Vector3f vertD, Vector3f normVec, float u0, float u1, float v0, float v1, int tint, int light) {
        float a = (tint >> 24 & 0xFF) / 255f;
        float r = (tint >> 16 & 0xFF) / 255f;
        float g = (tint >> 8 & 0xFF) / 255f;
        float b = (tint & 0xFF) / 255f;

        vc.vertex(quadPose, vertA.x, vertA.y, vertA.z).color(r, g, b, a).uv(u0, v1).uv2(light).normal(normal, normVec.x, normVec.y, normVec.z).endVertex();
        vc.vertex(quadPose, vertB.x, vertB.y, vertB.z).color(r, g, b, a).uv(u1, v1).uv2(light).normal(normal, normVec.x, normVec.y, normVec.z).endVertex();
        vc.vertex(quadPose, vertC.x, vertC.y, vertC.z).color(r, g, b, a).uv(u1, v0).uv2(light).normal(normal, normVec.x, normVec.y, normVec.z).endVertex();
        vc.vertex(quadPose, vertD.x, vertD.y, vertD.z).color(r, g, b, a).uv(u0, v0).uv2(light).normal(normal, normVec.x, normVec.y, normVec.z).endVertex();
    }

    @Override
    public void render(@NotNull SoakingDrum soakingDrum, float pTick, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay) {
        if (soakingDrum.getLevel() == null) return;
        if (!soakingDrum.getLevel().isClientSide) return;
        float t = soakingDrum.getLevel().getGameTime() + pTick % 360.0f;
        FluidStack fluid = soakingDrum.getFluidSlots().getFluidInTank(0);
        float fill = (float) fluid.getAmount() / (float) soakingDrum.getFluidSlots().getTankCapacity(0);

        ItemStack item = soakingDrum.getItemSlots().getStackInSlot(0);
        if (!item.isEmpty()) {
            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
            poseStack.pushPose();
            poseStack.translate(0.5f, 0.5f, 0.5f);
            poseStack.scale(0.75f, 0.75f, 0.75f);
            poseStack.translate(0f, -0.3f, 0f);

            if (!fluid.isEmpty()) {
                poseStack.translate(0, fill, 0);
                poseStack.mulPose(Axis.YP.rotationDegrees(t));
            }
            poseStack.mulPose(Axis.XP.rotationDegrees(90));
            poseStack.scale(0.75f, 0.75f, 0.75f);
            itemRenderer.renderStatic(item, ItemDisplayContext.FIXED, getLightLevel(soakingDrum.getLevel(), soakingDrum.getBlockPos().above()), OverlayTexture.NO_OVERLAY, poseStack, bufferSource, soakingDrum.getLevel(), 1);
            poseStack.popPose();

        }
        if (!fluid.isEmpty()) {
            renderFluid(fluid.getFluid(), fill, soakingDrum, pTick, poseStack, bufferSource, getLightLevel(soakingDrum.getLevel(), soakingDrum.getBlockPos().above()), overlay);
        }
    }

    private int getLightLevel(Level level, BlockPos pos) {
        int bLight = level.getBrightness(LightLayer.BLOCK, pos);
        int sLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(bLight, sLight);
    }

    private void renderFluid(Fluid fluid, float fill, SoakingDrum soakingDrum, float partialTick, PoseStack pose, MultiBufferSource buffer, int light, int overlay) {
        ResourceLocation fluidResource = IClientFluidTypeExtensions.of(fluid).getStillTexture();
        int tint = IClientFluidTypeExtensions.of(fluid).getTintColor();
        var sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidResource);

        float x0 = 2 / 16f, x1 = 14 / 16f;
        float z0 = 2 / 16f, z1 = 14 / 16f;
        float y0 = 4 / 16f, y1 = y0 + (12 / 16f) * fill;

        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.translucent());
        Matrix4f quadPose = pose.last().pose();
        Matrix3f normal = pose.last().normal();

        float u0 = sprite.getU0(), u1 = sprite.getU1();
        float v0 = sprite.getV0(), v1 = sprite.getV1();

        //TOP
        quad(vertexConsumer, quadPose, normal,
                new Vector3f(x0, y1, z1),
                new Vector3f(x1, y1, z1),
                new Vector3f(x1, y1, z0),
                new Vector3f(x0, y1, z0),
                new Vector3f(0, 1, 0),
                u0, u1, v0, v1,
                tint, light);

        //EAST
        quad(vertexConsumer, quadPose, normal,
                new Vector3f(x1, y0, z1),
                new Vector3f(x1, y0, z0),
                new Vector3f(x1, y1, z0),
                new Vector3f(x1, y1, z1),
                new Vector3f(1, 0, 0),
                u0, u1, v0, v1,
                tint, light);

        //WEST
        quad(vertexConsumer, quadPose, normal,
                new Vector3f(x0, y0, z0),
                new Vector3f(x0, y0, z1),
                new Vector3f(x0, y1, z1),
                new Vector3f(x0, y1, z0),
                new Vector3f(-1, 0, 0),
                u0, u1, v0, v1,
                tint, light);

        // SOUTH
        quad(vertexConsumer, quadPose, normal,
                new Vector3f(x0, y0, z1),
                new Vector3f(x1, y0, z1),
                new Vector3f(x1, y1, z1),
                new Vector3f(x0, y1, z1),
                new Vector3f(0, 0, 1),
                u0, u1, v0, v1,
                tint, light);

        // NORTH
        quad(vertexConsumer, quadPose, normal,
                new Vector3f(x1, y0, z0),
                new Vector3f(x0, y0, z0),
                new Vector3f(x0, y1, z0),
                new Vector3f(x1, y1, z0),
                new Vector3f(0, 0, -1),
                u0, u1, v0, v1,
                tint, light);
    }
}
