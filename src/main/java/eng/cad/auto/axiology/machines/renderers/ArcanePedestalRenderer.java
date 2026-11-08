package eng.cad.auto.axiology.machines.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import eng.cad.auto.axiology.machines.multiblock.infusion.ArcanePedestal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

public class ArcanePedestalRenderer implements BlockEntityRenderer<ArcanePedestal> {
    public ArcanePedestalRenderer(BlockEntityRendererProvider.Context context) {

    }
    @Override
    public void render(ArcanePedestal entity, float pTick, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay) {
        if (entity.getLevel() == null) return;
        if (!entity.getLevel().isClientSide) return;
        float t = entity.getLevel().getGameTime() + pTick % 360.0f;
        ItemStack item = entity.getItemSlots().getStackInSlot(0);
        if (item.isEmpty()) return;
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        poseStack.pushPose();
        poseStack.translate(0.5f, 1.3f + 0.1f* Mth.sin(2*Mth.PI*(t/100)), 0.5f);
        poseStack.scale(0.75f,0.75f,0.75f);
        poseStack.mulPose(Axis.YP.rotationDegrees(t));
        if (entity.getDataSlots().get("isItemHidden") == 1) poseStack.scale(0.25f, 0.25f ,0.25f);
        itemRenderer.renderStatic(item, ItemDisplayContext.FIXED, getLightLevel(entity.getLevel(), entity.getBlockPos().above()), OverlayTexture.NO_OVERLAY,poseStack,bufferSource, entity.getLevel(), 1);
        poseStack.popPose();
    }

    private int getLightLevel(Level level, BlockPos pos) {
        int bLight = level.getBrightness(LightLayer.BLOCK, pos);
        int sLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(bLight, sLight);
    }
}
