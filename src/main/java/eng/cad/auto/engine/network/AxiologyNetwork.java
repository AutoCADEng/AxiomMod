package eng.cad.auto.engine.network;

import eng.cad.auto.axiology.AxiomMod;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

public class AxiologyNetwork {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(AxiomMod.MODID, "net.axiology"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private AxiologyNetwork() {}

    private static int packetId = 0;

    public static <T extends AxiologyPacket<T>> void register(Class<T> packetClass, Supplier<T> factory){
        INSTANCE.registerMessage(
                packetId++,
                packetClass,
                AxiologyPacket::encode,
                buffer -> factory.get().decode(buffer),
                AxiologyPacket::handle
        );
    }
    public static void sendToClient(ServerPlayer player, AxiologyPacket<?> packet){
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    public static void sendToClientWithChunk(Level level, BlockPos pos, AxiologyPacket<?> packet){
        if (level.isClientSide()) return;
        INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(pos)), packet);
    }
}
