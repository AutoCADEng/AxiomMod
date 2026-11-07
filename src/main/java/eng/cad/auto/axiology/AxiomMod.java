package eng.cad.auto.axiology;

import com.mojang.logging.LogUtils;
import eng.cad.auto.axiology.core.events.BlockBreakListeners;
import eng.cad.auto.axiology.core.events.BlockPlaceAreaListeners;
import eng.cad.auto.axiology.items.util.RangedFluidBehaviours;
import eng.cad.auto.axiology.machines.SoakingDrum;
import eng.cad.auto.axiology.machines.multiblock.infusion.ArcaneAltar;
import eng.cad.auto.axiology.machines.multiblock.infusion.ArcanePedestal;
import eng.cad.auto.axiology.machines.renderers.ArcaneAltarRenderer;
import eng.cad.auto.axiology.machines.renderers.ArcanePedestalRenderer;
import eng.cad.auto.axiology.machines.renderers.SoakingDrumRenderer;
import eng.cad.auto.axiology.registry.render.BeamRegistry;
import eng.cad.auto.axiology.client.colors.ClientItemColorer;
import eng.cad.auto.axiology.client.core.ClientSetup;
import eng.cad.auto.axiology.client.render.BeamEffectRenderer;
import eng.cad.auto.axiology.core.machine.MultiblockStructure;
import eng.cad.auto.axiology.core.network.AxiologyNetwork;
import eng.cad.auto.axiology.registry.*;
import eng.cad.auto.axiology.core.network.packet.InfusionSyncPacket;
import eng.cad.auto.axiology.core.network.packet.FluidBeamSyncPacket;
import eng.cad.auto.axiology.core.network.packet.KillBeamPacket;
import eng.cad.auto.axiology.core.network.packet.MachineFluidSyncPacket;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(AxiomMod.MODID)
public class AxiomMod {
    public static final String MODID = "axiology";
    public static final Logger LOGGER = LogUtils.getLogger();

    public AxiomMod(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        ForgeMod.enableMilkFluid();
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::entityRendererSetup);
        modEventBus.addListener(BeamRegistry::createRegistry);
        MinecraftForge.EVENT_BUS.addListener(BlockBreakListeners::onBlockBreak);
        MinecraftForge.EVENT_BUS.addListener(BlockPlaceAreaListeners::onBlockPlacedArea);
        MinecraftForge.EVENT_BUS.addListener(AxiomMod::onAddReloadListeners);
        //Registries
        FluidRegistry.FLUIDS.register(modEventBus);
        FluidRegistry.init();
        FluidRegistry.FLUIDTYPES.register(modEventBus);
        BlockRegistry.BLOCKS.register(modEventBus);
        MachineRegistry.MACHINES.register(modEventBus);
        ItemRegistry.ITEMS.register(modEventBus);
        CreativeTabRegistry.CREATIVE_TABS.register(modEventBus);
        MenuRegistry.MENUS.register(modEventBus);
        TagRegistry.register(modEventBus);
        RecipeRegistry.RECIPE_TYPES.register(modEventBus);
        RecipeRegistry.RECIPE_SERIALIZERS.register(modEventBus);
        BeamRegistry.BEAMS.register(modEventBus);

        //Client Event Hooks
        ClientSetup.init(modEventBus);
        ClientItemColorer.init(modEventBus);
        BeamEffectRenderer.init(MinecraftForge.EVENT_BUS);
        //Networking
        AxiologyNetwork.register(MachineFluidSyncPacket.class, MachineFluidSyncPacket::new);
        AxiologyNetwork.register(FluidBeamSyncPacket.class, FluidBeamSyncPacket::new);
        AxiologyNetwork.register(InfusionSyncPacket.class, InfusionSyncPacket::new);
        AxiologyNetwork.register(KillBeamPacket.class, KillBeamPacket::new);
    }

    private static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(MultiblockStructure.JsonListener.getInstance());
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(RangedFluidBehaviours::init);
    }

    @SuppressWarnings("unchecked")
    private void entityRendererSetup(EntityRenderersEvent.RegisterRenderers event) {
        event.<SoakingDrum>registerBlockEntityRenderer((BlockEntityType<SoakingDrum>) MachineRegistry.MACHINELIST.get(SoakingDrum.name).blockEntity.get(), SoakingDrumRenderer::new);
        event.<ArcanePedestal>registerBlockEntityRenderer((BlockEntityType<ArcanePedestal>) MachineRegistry.MACHINELIST.get(ArcanePedestal.name).blockEntity.get(), ArcanePedestalRenderer::new);
        event.<ArcaneAltar>registerBlockEntityRenderer((BlockEntityType<ArcaneAltar>) MachineRegistry.MACHINELIST.get(ArcaneAltar.name).blockEntity.get(), ArcaneAltarRenderer::new);
    }
}