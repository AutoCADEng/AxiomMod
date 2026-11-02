package eng.cad.auto.engine.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.ArrayList;

public class BeamEffectRenderer {
    private static final ArrayList<AbstractBeamEffect> activeBeams = new ArrayList<>();
    private BeamEffectRenderer(){};
    public static void init(IEventBus eventBus) {
        eventBus.addListener(BeamEffectRenderer::onRenderLevel);
    }

    private static void onRenderLevel(final RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;
        if (activeBeams.isEmpty()) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null){
            activeBeams.clear();
            return;
        }

        float currentTime = (int) mc.level.getGameTime() + event.getPartialTick();
        Vec3 camera_pos = event.getCamera().getPosition();
        PoseStack transformations = event.getPoseStack();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();

        for (int idx = activeBeams.size() - 1; idx >= 0; idx--){
            AbstractBeamEffect<?> beam = activeBeams.get(idx);
            float age = currentTime - beam.creationTick;
            if (age>= beam.lifespan){
                activeBeams.remove(idx);
                continue;
            }
            RenderType renderType = BeamRenderTypes.BEAM;
            drawBeam(bufferSource.getBuffer(renderType), transformations.last(), beam, camera_pos, age);
            bufferSource.endBatch(BeamRenderTypes.BEAM);
        }
    }

    public static void addBeam(AbstractBeamEffect<?> beam){
        activeBeams.add(beam);
    }

    private static void drawBeam(VertexConsumer vertexConsumer, PoseStack.Pose pose, AbstractBeamEffect<?> beam, Vec3 camera_pos, float age){
        if (beam.positions.isEmpty()) return;
        Vec3 startPosWorld = beam.positions.get(0);
        Vec3 endPosWorld = beam.positions.get(beam.positions.size() - 1);
        double magnitude = endPosWorld.subtract(startPosWorld).length();
        int segments = Mth.clamp((int) (magnitude * 2.0), beam.minSegments, beam.maxSegments);

        Vec3 initialPositionWorld = new Vec3(beam.shape.calculateBezier(0));
        Vec3 initialPositionCamera = initialPositionWorld.subtract(camera_pos);
        Vec3 nextPositionCamera = initialPositionWorld.subtract(camera_pos);
        Vec3 initialDirection = nextPositionCamera.subtract(initialPositionCamera).normalize();
        Vec3 cameraDirection = new Vec3(Minecraft.getInstance().gameRenderer.getMainCamera().getLookVector());
        Vec3 initialPerpendicular = initialDirection.cross(cameraDirection).normalize();
        drawVertex(vertexConsumer, pose, initialPositionCamera.subtract(initialPerpendicular.scale(beam.evaluateWave(0, age))),  beam.tint);
        drawVertex(vertexConsumer, pose, initialPositionCamera.add(initialPerpendicular.scale(beam.evaluateWave(0, age))), beam.tint);

        for (int idx = 1; idx<= segments; idx++){
            float curvePosition = (float) idx / segments;
            float lastCurvePosition = (float) (idx - 1) / segments;

            Vec3 positionWorld = new Vec3(beam.shape.calculateBezier(curvePosition));

            Vec3 positionCamera = positionWorld.subtract(camera_pos);
            Vec3 lastPositionWorld = new Vec3(beam.shape.calculateBezier(lastCurvePosition));
            Vec3 lastPositionCamera = lastPositionWorld.subtract(camera_pos);
            Vec3 direction = positionCamera.subtract(lastPositionCamera).normalize();
            Vec3 perpendicular = direction.cross(cameraDirection).normalize();

            float fadeMod =  1 - (age/(float) beam.lifespan);
            int fadedColor = ((int) (((beam.tint >> 24) & 0xFF) * fadeMod) << 24) | (beam.tint & 0x00FFFFFF);
            drawVertex(vertexConsumer, pose, positionCamera.subtract(perpendicular.scale(beam.evaluateWave(curvePosition, age))), fadedColor);
            drawVertex(vertexConsumer, pose, positionCamera.add(perpendicular.scale(beam.evaluateWave(curvePosition, age))), fadedColor);
        }
    }

    private static void drawVertex(VertexConsumer c, PoseStack.Pose pose, Vec3 position, int rgb) {
        float a = ((rgb >> 24) & 0xFF) / 255.0F;
        float r = ((rgb >> 16) & 0xFF) / 255.0F;
        float g = ((rgb >> 8)  & 0xFF) / 255.0F;
        float b = ((rgb >> 0) & 0xFF) / 255.0F;
        c.vertex(pose.pose(),
                        (float) position.x,
                        (float) position.y,
                        (float) position.z)
                .color(r, g, b, a)
                .endVertex();
    }


}