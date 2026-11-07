package eng.cad.auto.axiology.core.events;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.Event;

public class BlockDropEvent extends Event {
    private final Level level;
    private final BlockPos pos;
    private final Entity entity;
    private final Player miner;

    public BlockDropEvent(Level level, BlockPos pos, ItemEntity entity, Player miner) {
        this.level = level;
        this.pos = pos;
        this.entity = entity;
        this.miner = miner;
    }

    public Level getLevel() { return level; }
    public BlockPos getPos() { return pos; }
    public Entity getEntity() { return entity; }
    public Player getMiner() { return miner; }
}
