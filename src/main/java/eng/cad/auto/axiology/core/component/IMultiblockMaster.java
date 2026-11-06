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

    default void validateStructure(BlockPos masterPos, Level level){
        if (level == null) return;
        var machineStructure = getStructure(level, masterPos);

        int y = machineStructure.shapeList.size();
        int z = machineStructure.shapeList.get(0).length;
        int x = machineStructure.shapeList.get(0)[0].length();

        Direction facing = level.getBlockState(masterPos).getValue(BaseMachineBlock.FACING);

        Rotation rotation = switch (facing) {
            case EAST -> Rotation.CLOCKWISE_90;
            case SOUTH -> Rotation.CLOCKWISE_180;
            case WEST -> Rotation.COUNTERCLOCKWISE_90;
            default -> Rotation.NONE;
        };

        for (int layer = 0; layer < y; layer++) {
            for (int row = 0; row < z; row++) {
                for (int col = 0; col < x; col++) {
                    if (!validateBlock(new BlockPos(col, layer, row), machineStructure, level, rotation, masterPos)){
                        updateStructureValidity(false);
                        return;
                    }
                }
            }
        }
        updateStructureValidity(true);
    }

    default boolean validateBlock(BlockPos relativeFollowerPos, MultiblockStructure structure, Level level, Rotation rotation, BlockPos masterPos){
        BlockPos structurePos = new BlockPos(relativeFollowerPos.getX(), -relativeFollowerPos.getY(), relativeFollowerPos.getZ());
        BlockPos relativePos = structurePos.subtract(structure.rootPos);
        BlockPos rotatedPos = relativePos.rotate(rotation);
        BlockPos worldPos = masterPos.offset(rotatedPos);
        Block blockAtPos = level.getBlockState(worldPos).getBlock();

        char buildKey = structure.shapeList.get(relativeFollowerPos.getY())[relativeFollowerPos.getX()].charAt(relativeFollowerPos.getZ());
        if (!structure.blockMap.get(buildKey).contains(blockAtPos) && (!structure.blockMap.get(buildKey).get(0).defaultBlockState().isAir())){
            return false;
        }
        if (blockAtPos instanceof BaseMachineBlock followerBlock){
            return true;
        }
        return false;
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

    void updateStructureValidity(boolean validity);

    boolean isStructureValid();
}
