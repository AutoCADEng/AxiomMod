package eng.cad.auto.axiology.machines;

import eng.cad.auto.axiology.machines.util.CommonRightClickBehaviour;
import eng.cad.auto.axiology.registry.MachineRegistry;
import eng.cad.auto.engine.machine.core.GenericMachineEntity;
import eng.cad.auto.engine.machine.core.MachineCore;
import eng.cad.auto.engine.machine.core.interfaces.IMachineLogic;
import eng.cad.auto.engine.machine.core.interfaces.IRightClickBehaviour;
import eng.cad.auto.engine.machine.core.slot.SlotIORestriction;
import eng.cad.auto.engine.machine.core.slot.SlotType;
import eng.cad.auto.engine.machine.multiblock.GenericMasterEntity;
import eng.cad.auto.engine.network.AxiologyNetwork;
import eng.cad.auto.engine.network.InfusionSyncPacket;
import eng.cad.auto.engine.network.KillBeamPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.PacketDistributor;

import java.util.*;

public class ArcaneImbuer {
    MachineCore INSTANCE;
    public static final String name = "arcane_imbuer";
    public ArcaneImbuer(){
        this.INSTANCE = MachineRegistry.registerMachine(new MachineCore.Builder(name)
                .addItemSlots(1)
                .modifySlot(0, SlotType.ITEM,1, SlotIORestriction.IN_OUT)
                .noGui()
                .addDataSlots(2)
                .rightClickLogic(new Logic.RightClickLogic())
                .addServerLogic(Logic.Server::new)
                .build());
    }

    public static class Logic{

        public static int activated = 0;
        public static int PEDESTAL_RANGE = 7;
        public static class Server implements IMachineLogic {
            HashMap<BlockPos, UUID> beamIDs = new HashMap<>();
            HashSet<BlockPos> entities = new HashSet<>();
            @Override
            public void serverTick(Level level, BlockPos pos, BlockState state) {
                if (level == null || level.isClientSide) return;
                if (!(level.getBlockEntity(pos) instanceof GenericMachineEntity machine)) return;
                if (!Objects.equals(machine.machineConfig.name(), ArcaneImbuer.name)) return;
                if (level.getGameTime()%20 == 0) {
                    for(BlockEntity entity:scan(level, pos, PEDESTAL_RANGE)){
                        if (entity instanceof GenericMachineEntity follower){
                            if (!(Objects.equals(follower.machineConfig.name(), ArcanePedestal.name))) continue;
                            entities.add(follower.getBlockPos());
                        }
                    }
                }

                if (machine.get().metadata.get(activated) == 0 && beamIDs.isEmpty()) return;

                for (BlockPos followerPos: entities){
                    if (level.getBlockEntity(followerPos) instanceof GenericMachineEntity follower){
                        if (!(Objects.equals(follower.machineConfig.name(), ArcanePedestal.name))) continue;

                        if (follower.get().inventory.getStackInSlot(0).isEmpty()
                                || machine.get().metadata.get(activated) == 0
                                || follower.get().metadata.get(0) == 1) {
                            if (!beamIDs.containsKey(followerPos)) continue;
                            AxiologyNetwork.INSTANCE.send(PacketDistributor.NEAR.with(() ->
                                    new PacketDistributor.TargetPoint(pos.getX(), pos.getY(), pos.getZ(), 128, level.dimension())),
                                    new KillBeamPacket(beamIDs.get(followerPos)));
                            beamIDs.remove(followerPos);
                            continue;
                        }

                        if (!beamIDs.containsKey(followerPos) && follower.get().metadata.get(0) == 0) {
                            UUID id = UUID.randomUUID();
                            beamIDs.put(followerPos, id);
                            AxiologyNetwork.INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(pos.getX(), pos.getY(), pos.getZ(), 128, level.dimension())),
                            new InfusionSyncPacket(id, null, 0xFFFFFFFF, pos.getCenter().toVector3f(), followerPos.getCenter().toVector3f(), level));
                            continue;
                        }

                        if (follower.get().metadata.get(0) == 0){
                            follower.get().metadata.set(1, follower.get().metadata.get(1) + 1);
                        }

                        if (follower.get().metadata.get(1) > 200){
                            follower.get().metadata.set(1,0);
                            follower.get().metadata.set(0, 1);
                            follower.get().inventory.setStackInSlot(0, ItemStack.EMPTY);
                            follower.setChanged();
                            level.sendBlockUpdated(followerPos, follower.getBlockState(), follower.getBlockState(), 3);
                        }
                    }
                }
            }
            public static ArrayList<BlockEntity> scan(Level level, BlockPos center, int radius) {
                ArrayList<BlockEntity> pedestals = new ArrayList<>();
                for (BlockPos pos : BlockPos.betweenClosed(
                        center.offset(-radius, -radius, -radius),
                        center.offset(radius, radius, radius))) {
                    if (!level.isLoaded(pos)) continue;
                    BlockEntity be = level.getBlockEntity(pos);
                    if (be != null) pedestals.add(be);
                }
                return pedestals;
            }
            @Override
            public void saveInventory(GenericMachineEntity entity, CompoundTag tag) {
                if (!(entity instanceof GenericMasterEntity blockEntity)) return;
                if (blockEntity.get().inventory != null) {
                    tag.put("Inventory", entity.get().inventory.serializeNBT());
                }
                if (blockEntity.get().tanks != null) {
                    tag.put("Tanks", entity.get().tanks.serializeNBT());
                }
                tag.putInt("progress", blockEntity.get().metadata.get(activated));
            }

            @Override
            public void loadInventory(GenericMachineEntity entity, CompoundTag tag) {
                if (!(entity instanceof GenericMasterEntity blockEntity)) return;
                if (blockEntity.get().inventory != null && tag.contains("Inventory")) {
                    blockEntity.get().inventory.deserializeNBT(tag.getCompound("Inventory"));
                }
                if (blockEntity.get().tanks != null && tag.contains("Tanks")) {
                    blockEntity.get().tanks.deserializeNBT(tag.getCompound("Tanks"));
                }

                blockEntity.get().metadata.set(activated, tag.getInt("progress"));
            }

            @Override
            public void resetProgress(GenericMachineEntity blockEntity) {
                blockEntity.get().metadata.set(activated, 0);
            }

            @Override
            public void incrementProgress(GenericMachineEntity blockEntity, int count) {
                blockEntity.get().metadata.set(activated,blockEntity.get().metadata.get(activated) + count);
            }
        }
        public static class RightClickLogic implements IRightClickBehaviour {
            @Override
            public InteractionResult useMachine(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult result) {
                if (!(level.getBlockEntity(blockPos) instanceof GenericMachineEntity machine)) return InteractionResult.PASS;
                if (!machine.machineConfig.name().equals(ArcaneImbuer.name)) return InteractionResult.PASS;
                boolean success = onRightClick(machine, blockState, level, blockPos, player, interactionHand, result);
                return success? InteractionResult.SUCCESS : InteractionResult.PASS;
            }

            private boolean onRightClick(GenericMachineEntity blockEntity, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult result) {
                if (interactionHand == InteractionHand.OFF_HAND) return false;
                if (player.isShiftKeyDown()) {
                    blockEntity.get().metadata.set(activated, blockEntity.get().metadata.get(activated) == 0? 1:0);
                    return true;
                }
                return CommonRightClickBehaviour.itemInHand(0, blockEntity, blockState, level, blockPos, player, interactionHand, result);
            }
        }
    }
}
