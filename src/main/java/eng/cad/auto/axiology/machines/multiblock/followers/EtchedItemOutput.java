package eng.cad.auto.axiology.machines.multiblock.followers;

public class EtchedItemOutput {
//    MachineRegistryObject INSTANCE;
//    public static final String name = "etched_item_output";
//    public EtchedItemOutput() {
//        this.INSTANCE = MachineRegistry.registerMachine(new MachineRegistryObject
//                .Builder(name)
//                .addItemSlots(1)
//                .modifySlot(0, SlotType.ITEM,64, SlotIORestriction.OUTPUT)
//                .addServerLogic(Logic.Server::new)
//                .addClientLogic(new Logic.Client())
//                .rightClickLogic(new Logic.RightClickLogic())
//                .noGui()
//                .isMultiblockFollower(new Logic.Follower())
//                .build());
//    }
//    public static class Logic{
//        public static class Follower implements IFollowerLogic{
//            @Override
//            public CustomCraftingContainer getCraftingContainer(BaseFollower entity) {
//                return new CustomCraftingContainer(entity).addSlot(entity.get().inventory.getStackInSlot(0));
//            }
//        }
//        public static class Server implements ITickable {
//
//            @Override
//            public void serverTick(Level level, BlockPos pos, BlockState state) {
//                if(level.isClientSide()) return;
//                if (!(level.getBlockEntity(pos) instanceof BaseFollower blockEntity)) return;
//
//            }
//
//            @Override
//            public void saveInventory(BaseMachine entity, CompoundTag tag) {
//                if (!(entity instanceof BaseFollower blockEntity)) return;
//                if (blockEntity.get().inventory != null) {
//                    tag.put("Inventory", blockEntity.get().inventory.serializeNBT());
//                }
//            }
//
//            @Override
//            public void loadInventory(BaseMachine entity, CompoundTag tag) {
//                if (!(entity instanceof BaseFollower blockEntity)) return;
//                if (blockEntity.get().inventory != null && tag.contains("Inventory")) {
//                    blockEntity.get().inventory.deserializeNBT(tag.getCompound("Inventory"));
//                }
//
//            }
//
//            @Override
//            public void resetProgress(BaseMachine blockEntity) {
//            }
//
//            @Override
//            public void incrementProgress(BaseMachine blockEntity, int count) {
//            }
//        }
//        public static class Client {
//            @Override
//            public void UIRenderLogic(GuiGraphics guiGraphics, float tick, int mouse_x, int mouse_y, GenericUI.Screen screen) {
//            }
//
//            @Override
//            public void clientTick(Level level, BlockPos pos, BlockState state, BaseMachine blockEntity) {
//                if (!level.isClientSide()) return;
//
//            }
//        }
//        public static class RightClickLogic implements IRightClickable<BaseMachine> {
//
//            @Override
//            public InteractionResult rightClick(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult result) {
//                if (!(level.getBlockEntity(blockPos) instanceof BaseFollower machine)) return InteractionResult.PASS;
//                if (!machine.machineConfig.name().equals(EtchedItemOutput.name)) return InteractionResult.PASS;
//                boolean success = onRightClick(machine, blockState, level, blockPos, player, interactionHand, result);
//                return success? InteractionResult.SUCCESS : InteractionResult.PASS;
//            }
//
//            private boolean onRightClick(BaseMachine blockEntity, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult result) {
//                if (!(blockEntity instanceof BaseFollower machine)) return false;
//                if (player.isShiftKeyDown()) return false;
//                if (interactionHand == InteractionHand.OFF_HAND) return false;
//                return CommonRightClickBehaviour.extractItem(0, blockEntity, blockState, level, blockPos, player, interactionHand, result);
//            }
//        }
//    }
}
