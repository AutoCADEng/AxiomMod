package eng.cad.auto.axiology.core.machine;

import eng.cad.auto.axiology.core.blocks.BaseMachineBlock;
import eng.cad.auto.axiology.core.machine.component.IMultiblockFollower;
import eng.cad.auto.axiology.core.machine.component.IMultiblockMaster;
import eng.cad.auto.axiology.core.machine.component.ISubscribeBlockBreak;
import eng.cad.auto.axiology.core.machine.component.ISubscribeBlockPlacedInArea;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;

import java.util.HashSet;

public abstract class BaseMaster<H extends BaseMaster<H>> extends TickingMachine<H> implements ISubscribeBlockBreak, ISubscribeBlockPlacedInArea, IMultiblockMaster {
    HashSet<BlockPos> followers = new HashSet<>();

    public BaseMaster(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    public void fireBlockBreak(BlockEvent.BreakEvent event) {
        if (level == null) return;
        getFollowers(level).forEach(follower -> {
            if (!(follower instanceof IMultiblockFollower multiblockFollower)) return;
            multiblockFollower.clearMaster();
            followers.clear();
        });
    }

    @Override
    public HashSet<BlockPos> getFollowerPositions() {
        return this.followers;
    }

    @Override
    public void fireBlockPlaceEvent(BlockEvent.EntityPlaceEvent event) {
        if (level == null) return;
        MultiblockStructure structure = getStructure(level, this.getInstance().getBlockPos());
        int y = structure.shapeList.size();
        int x = structure.shapeList.get(0)[0].length();
        int z = structure.shapeList.get(0).length;
        Vec3i offset = structure.rootPos;

        Vec3i positive = new Vec3i(x, y, z).subtract(offset);
        Vec3i negative = new Vec3i(x, y, z).subtract(offset.multiply(-1));

        Vec3i eventPos = event.getPos();

        if (eventPos.getX() > positive.getX() || eventPos.getX() < negative.getX()) return;
        if (eventPos.getY() > positive.getY() || eventPos.getY() < negative.getY()) return;
        if (eventPos.getZ() > positive.getZ() || eventPos.getZ() < negative.getZ()) return;

        if (!(level.getBlockEntity(event.getPos()) instanceof BaseFollower<?> follower)) return;
        if (follower.getMaster() != null) return;
        Direction facing = level.getBlockState(this.getBlockPos()).getValue(BaseMachineBlock.FACING);

        Rotation rotation = switch (facing) {
            case EAST -> Rotation.CLOCKWISE_90;
            case SOUTH -> Rotation.CLOCKWISE_180;
            case WEST -> Rotation.COUNTERCLOCKWISE_90;
            default -> Rotation.NONE;
        };

        if (!validateBlock(event.getPos(), structure, level, rotation, this.getBlockPos())) return;
        follower.setMaster(this);
        followers.add(event.getPos());
    }

}
