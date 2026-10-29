package eng.cad.auto.axolotyl.machine.multiblock;

import eng.cad.auto.axiom.AxiomMod;
import eng.cad.auto.axiom.registry.MultiblockRegistry;
import eng.cad.auto.axolotyl.machine.core.GenericMachineEntity;
import eng.cad.auto.axolotyl.machine.core.MachineCore;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class GenericMasterEntity extends GenericMachineEntity {
    MultiblockStructure machineStructure;
    NonNullList<BlockPos> followers = NonNullList.create();
    public GenericMasterEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, MachineCore.Config machineConfig) {
        super(type, pos, state, machineConfig);
    }

    public boolean validateStructure(BlockPos controllerPos){
        if (level == null) return false;
        if (machineStructure == null) {
            var supplier = MultiblockRegistry.Structures.get(ResourceLocation.fromNamespaceAndPath(AxiomMod.MODID, super.machineConfig.name() + "_multiblock"));
            if (supplier == null) return false;
            machineStructure = supplier.get();
        }

        followers.clear();
        Direction facing = level.getBlockState(controllerPos).getValue(GenericFollowerBlock.FACING);
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
                    if (!machineStructure.blockMap.get(buildKey).contains(blockAtPos)){
                        followers.clear();
                        return false;
                    }
                    if (blockAtPos instanceof GenericFollowerBlock followerBlock){
                        followers.add(worldPos); // Seems safer than storing block entities that might become invalid
                    }
                }
            }
        }
        return true;
    }

    public NonNullList<GenericFollowerEntity> getFollowers() {
        NonNullList<GenericFollowerEntity> followerEntities = NonNullList.create();
        if (level == null) return followerEntities;
        for (BlockPos followerPos:followers){
            if ((level.getBlockEntity(followerPos) instanceof GenericFollowerEntity followerEntity)){
                followerEntities.add(followerEntity);
            }
        }
        return followerEntities;
    }
}