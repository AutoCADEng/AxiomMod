package eng.cad.auto.axiology.core.machine.component;

import eng.cad.auto.axiology.AxiomMod;
import eng.cad.auto.axiology.core.blocks.BaseMachineBlock;
import eng.cad.auto.axiology.core.machine.BaseMachine;
import eng.cad.auto.axiology.core.machine.MultiblockStructure;
import eng.cad.auto.axiology.registry.MultiblockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;

import java.util.HashSet;

public interface IMultiblockMaster {

    HashSet<BlockPos> getFollowerPositions();

    default void validateStructure(BlockPos masterPos, Level level) {
        if (level == null) return;
        var machineStructure = getStructure(level, masterPos);
        if (machineStructure == null) {
            updateStructureValidity(false);
            return;
        }

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
                    if (!validateBlock(new BlockPos(col, layer, row), machineStructure, level, rotation, masterPos)) {
                        updateStructureValidity(false);
                        clearFollowers(level);
                        return;
                    }

                }
            }
        }
        updateStructureValidity(true);
    }

    default boolean validateBlock(BlockPos relativeFollowerPos, MultiblockStructure structure, Level level, Rotation rotation, BlockPos masterPos) {
        BlockPos structurePos = new BlockPos(relativeFollowerPos.getX(), -relativeFollowerPos.getY(), relativeFollowerPos.getZ());
        BlockPos relativePos = structurePos.subtract(structure.rootPos);
        BlockPos rotatedPos = relativePos.rotate(rotation);
        BlockPos worldPos = masterPos.offset(rotatedPos);
        Block blockAtPos = level.getBlockState(worldPos).getBlock();

        char buildKey = structure.shapeList.get(relativeFollowerPos.getY())[relativeFollowerPos.getZ()].charAt(relativeFollowerPos.getX());
        if (!structure.blockMap.get(buildKey).contains(blockAtPos)) {
            return false;
        }
        getFollowerPositions().add(worldPos);
        return true;
    }

    default MultiblockStructure getStructure(Level level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof BaseMachine<?> machine)) return null;
        var supplier = MultiblockRegistry.Structures.get(ResourceLocation.fromNamespaceAndPath(AxiomMod.MODID, machine.getName() + "_multiblock"));
        if (supplier == null) return null;
        MultiblockStructure machineStructure = supplier.get();
        return machineStructure;
    }

    default NonNullList<BaseMachine<? extends IMultiblockFollower>> getFollowers(Level level) {
        NonNullList<BaseMachine<? extends IMultiblockFollower>> followerEntities = NonNullList.create();
        if (level == null) return followerEntities;
        for (BlockPos followerPos : getFollowerPositions()) {
            if ((level.getBlockEntity(followerPos) instanceof IMultiblockFollower followerEntity)) {
                if (!(level.getBlockEntity(followerPos) instanceof BaseMachine<?> follower)) continue;
                followerEntities.add((BaseMachine<? extends IMultiblockFollower>) follower); // Fighting with the compiler here >:( Feel free to replace
            }
        }
        return followerEntities;
    }

    default void clearFollowers(Level level) {
        for (BaseMachine<? extends IMultiblockFollower> follower : getFollowers(level)) {
            if (!(follower instanceof IMultiblockFollower followerEntity)) return;
            followerEntity.clearMaster();
        }
        getFollowerPositions().clear();
    }

    void updateStructureValidity(boolean validity);

    boolean isStructureValid();
}