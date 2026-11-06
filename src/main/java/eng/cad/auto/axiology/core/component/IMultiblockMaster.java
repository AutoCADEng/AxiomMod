package eng.cad.auto.axiology.core.component;

import eng.cad.auto.axiology.AxiomMod;
import eng.cad.auto.axiology.core.blocks.BaseMachineBlock;
import eng.cad.auto.axiology.core.machine.BaseMachine;
import eng.cad.auto.axiology.core.machine.TickingMachine;
import eng.cad.auto.axiology.registry.MultiblockRegistry;
import eng.cad.auto.axiology.core.machine.MultiblockStructure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraftforge.event.level.BlockEvent;
import java.util.HashSet;

public interface IMultiblockMaster {

    HashSet<BlockPos> getFollowerPositions();

    default boolean validateStructure(BlockPos controllerPos, Level level){
        if (level == null) return false;
        var machineStructure = getStructure(level, controllerPos);

        Direction facing = level.getBlockState(controllerPos).getValue(BaseMachineBlock.FACING);
        int height = machineStructure.shapeList.size();
        int rows = machineStructure.shapeList.get(0).length;
        int cols = machineStructure.shapeList.get(0)[0].length();
        for (int layer = 0; layer < height; layer++) {
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {

                    Rotation rotation = switch (facing) {
                        case EAST -> Rotation.CLOCKWISE_90;
                        case SOUTH -> Rotation.CLOCKWISE_180;
                        case WEST -> Rotation.COUNTERCLOCKWISE_90;
                        default -> Rotation.NONE;
                    };

                    BlockPos structurePos = new BlockPos(col, -layer, row);
                    BlockPos relativePos = structurePos.subtract(machineStructure.rootPos);
                    BlockPos rotatedPos = relativePos.rotate(rotation);
                    BlockPos worldPos = controllerPos.offset(rotatedPos);

                    Block blockAtPos = level.getBlockState(worldPos).getBlock();

                    char buildKey = machineStructure.shapeList.get(layer)[row].charAt(col);
                    if (!machineStructure.blockMap.get(buildKey).contains(blockAtPos) && (!machineStructure.blockMap.get(buildKey).get(0).defaultBlockState().isAir())){
                        getFollowerPositions().clear();
                        return false;
                    }
                    if (blockAtPos instanceof BaseMachineBlock followerBlock){
                        getFollowerPositions().add(worldPos); // Seems safer than storing block entities that might become invalid
                    }
                }
            }
        }
        return true;
    }

    default MultiblockStructure getStructure(Level level, BlockPos pos){
        if (!(level.getBlockEntity(pos) instanceof BaseMachine<?> machine)) return null;
        var supplier = MultiblockRegistry.Structures.get(ResourceLocation.fromNamespaceAndPath(AxiomMod.MODID, machine.getName() + "_multiblock"));
        if (supplier == null) return null;
        MultiblockStructure machineStructure = supplier.get();
        return machineStructure;
    }

    default NonNullList<BaseMachine<? extends IMultiblockFollower>> getFollowers(Level level) {
        NonNullList<BaseMachine<? extends IMultiblockFollower>> followerEntities = NonNullList.create();
        if (level == null) return followerEntities;
        for (BlockPos followerPos:getFollowerPositions()){
            if ((level.getBlockEntity(followerPos) instanceof IMultiblockFollower followerEntity)){
                if (!(level.getBlockEntity(followerPos) instanceof TickingMachine<?> follower)) return null;
                followerEntities.add((TickingMachine<? extends IMultiblockFollower>) follower); // Fighting with the compiler here >:( Feel free to replace
            }
        }
        return followerEntities;
    }

    default void dropFollower(BaseMachine<? extends IMultiblockFollower> follower, BlockEvent.BreakEvent event){
        getFollowerPositions().remove(follower.getBlockPos());
    }
}
