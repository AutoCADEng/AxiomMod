package eng.cad.auto.axiology.magic.rebounds.core;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class InfusionContext {
    public Level level;
    public BlockPos alterPos;
    public @Nullable ServerPlayer player;

    public InfusionContext(Level level, BlockPos alterPos, @Nullable ServerPlayer getPlayer) {
        this.level = level;
        this.alterPos = alterPos;
        this.player = getPlayer;
    }
}
