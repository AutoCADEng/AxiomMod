package eng.cad.auto.axolotyl.network;

import eng.cad.auto.axiom.AxiomMod;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class FluidNetwork {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(AxiomMod.MODID, "net.fluid"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private FluidNetwork() {}

    private static int packetId = 0;

    public static void register() {
        INSTANCE.registerMessage(
                packetId++,
                FluidSyncPacket.class,
                FluidSyncPacket::encode,
                FluidSyncPacket::decode,
                FluidSyncPacket::handle
        );
    }

    public static void sendToClient(ServerPlayer player, FluidSyncPacket packet){
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    public static void sendToClientWithChunk(Level level, BlockPos pos, FluidSyncPacket packet){
        if (level.isClientSide()) return;
        INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(pos)), packet);
    }
}
