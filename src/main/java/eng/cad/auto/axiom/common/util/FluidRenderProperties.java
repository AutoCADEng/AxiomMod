package eng.cad.auto.axiom.common.util;

import com.mojang.blaze3d.systems.RenderSystem;
import eng.cad.auto.axiom.AxiomMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;

public class FluidRenderProperties implements IClientFluidTypeExtensions {
    private final ResourceLocation fluidSource;
    private final ResourceLocation fluidFlowing;
    private final int color;

    public FluidRenderProperties(String sourcePath, String flowingPath){
        this(sourcePath, flowingPath, 0xFFFFFF);
    }
    public FluidRenderProperties(String sourcePath, String flowingPath, int color){
        this.fluidSource = ResourceLocation.fromNamespaceAndPath(AxiomMod.MODID, "block/fluid/" + sourcePath);
        this.fluidFlowing = ResourceLocation.fromNamespaceAndPath(AxiomMod.MODID, "block/fluid/" + flowingPath);
        this.color = color;
    }

    public FluidRenderProperties(int color){
        this.fluidSource = ResourceLocation.fromNamespaceAndPath(AxiomMod.MODID, "block/fluid/generic_flow");
        this.fluidFlowing = ResourceLocation.fromNamespaceAndPath(AxiomMod.MODID, "block/fluid/generic_still");
        this.color = color;
    }

    @Override
    public ResourceLocation getStillTexture() {
        return fluidSource;
    }

    @Override
    public ResourceLocation getFlowingTexture() {
        return fluidFlowing;
    }

    @Override
    public int getTintColor() {
        return color;
    }
}
