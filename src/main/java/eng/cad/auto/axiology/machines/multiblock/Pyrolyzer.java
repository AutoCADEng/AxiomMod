package eng.cad.auto.axiology.machines.multiblock;

public class Pyrolyzer {
//    MachineRegistryObject INSTANCE;
//    public static final String name = "pyrolyzer";
//    public Pyrolyzer() {
//        this.INSTANCE = MachineRegistry.registerMachine(new MachineRegistryObject
//                .Builder(name)
//                .addDataSlots(2)
//                .addServerLogic(Logic.Server::new)
//                .addClientLogic(new Logic.Client())
//                .isMultiblockController()
//                .build());
//    }
//    public static class Logic {
//
//        public static final int Max_Processing_Ticks = 24000/60;
//        static final int isValid = 0;
//        static final int progress = 1;
//        static class Server implements ITickable {
//            int counter = 0;
//            @Override
//            public void serverTick(Level level, BlockPos pos, BlockState state) {
//                if(level.isClientSide()) return;
//                if (!(level.getBlockEntity(pos) instanceof BaseMaster blockEntity)) return;
//                counter++;
//
//                if (counter >= 10){
//                    counter = 0;
//                    blockEntity.get().metadata.set(isValid, blockEntity.validateStructure(pos)?1:0); // lazy implementation for now, we will look into events when I got more time.
//                }
//
//                if (blockEntity.get().metadata.get(isValid) == 0){
//                    resetProgress(blockEntity);
//                    return;
//                }
//
//                if (!(Objects.equals(blockEntity.machineConfig.name(), name))) return;
//
//                CustomCraftingContainer inventories = new CustomCraftingContainer(blockEntity);
//                ArrayList<BaseFollower> followerEntities = new ArrayList<>();
//
//                for(BaseFollower follower:blockEntity.getFollowers())
//                {
//                    followerEntities.add(follower);
//                    inventories = inventories.merge(follower.getCraftingContainer(follower));
//                }
//
//                PyrolyzerRecipe activeRecipe = PyrolyzerRecipe.getActiveRecipe(
//                        inventories,
//                        RecipeRegistry.PYROLYZER.get(),
//                        level,
//                        blockEntity);
//
//                if (activeRecipe == null){
//                    resetProgress(blockEntity);
//                    return;
//                }
//                if (!activeRecipe.hasSpace(blockEntity, followerEntities, activeRecipe)) {
//                    resetProgress(blockEntity);
//                    return;
//                }
//
//                if (blockEntity.get().metadata.get(progress) >= Max_Processing_Ticks) {
//                    if (activeRecipe.validate(blockEntity, followerEntities, activeRecipe)) activeRecipe.craft(blockEntity, followerEntities, activeRecipe);
//                    resetProgress(blockEntity);
//                    level.playSound(null, pos, SoundEvents.DISPENSER_DISPENSE, SoundSource.BLOCKS, 1.0F, 1.0F);
//                    return;
//                }
//                if (level.random.nextFloat() < 0.025F) level.playSound(null, pos, SoundEvents.BLASTFURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F);
//                incrementProgress(blockEntity, 1);
//            }
//
//            //TODO Test if this machine keeps it inventory
//            @Override
//            public void saveInventory(BaseMachine entity, CompoundTag tag) {
//                if (!(entity instanceof BaseMaster blockEntity)) return;
//                if (blockEntity.get().inventory != null) {
//                    tag.put("Inventory", entity.get().inventory.serializeNBT());
//                }
//                if (blockEntity.get().tanks != null) {
//                    tag.put("Tanks", entity.get().tanks.serializeNBT());
//                }
//
//                tag.putInt("isValid", blockEntity.get().metadata.get(isValid));
//                tag.putInt("progress", blockEntity.get().metadata.get(progress));
//            }
//
//            @Override
//            public void loadInventory(BaseMachine entity, CompoundTag tag) {
//                if (!(entity instanceof BaseMaster blockEntity)) return;
//                if (blockEntity.get().inventory != null && tag.contains("Inventory")) {
//                    blockEntity.get().inventory.deserializeNBT(tag.getCompound("Inventory"));
//                }
//                if (blockEntity.get().tanks != null && tag.contains("Tanks")) {
//                    blockEntity.get().tanks.deserializeNBT(tag.getCompound("Tanks"));
//                }
//
//                blockEntity.get().metadata.set(isValid, tag.getInt("isValid"));
//                blockEntity.get().metadata.set(progress, tag.getInt("progress"));
//            }
//
//            @Override
//            public void resetProgress(BaseMachine blockEntity) {
//                if (!(blockEntity instanceof BaseMaster machine)) return;
//                machine.get().metadata.set(progress, 0);
//            }
//            @Override
//            public void incrementProgress(BaseMachine blockEntity, int value) {
//                if (!(blockEntity instanceof BaseMaster machine)) return;
//                machine.get().metadata.set(progress, machine.get().metadata.get(progress) + value);
//            }
//
//        }
//        static class Client {
//
//            @Override
//            public void UIRenderLogic(GuiGraphics guiGraphics, float tick, int mouse_x, int mouse_y, GenericUI.Screen screen) {
//                //Let's get the menu open first ...
//            }
//
//            @Override
//            public void clientTick(Level level, BlockPos pos, BlockState state, BaseMachine blockEntity) {
//                if (!level.isClientSide()) return;
//                if (blockEntity.get().metadata.get(progress) <= 0) return;
//                if (level.random.nextFloat() < 0.33F) {
//
//                    double x = pos.getX() + 0.5D + (level.random.nextDouble() - 0.5D) * 0.5D;
//                    double y = pos.above().getY() + 1.0D;
//                    double z = pos.getZ() + 0.5D + (level.random.nextDouble() - 0.5D) * 0.5D;
//
//                    double xSpeed = 0.0D;
//                    double ySpeed = 0.05D;
//                    double zSpeed = 0.0D;
//
//                    level.addParticle(ParticleTypes.SMOKE, x, y, z, xSpeed, ySpeed, zSpeed);
//                }
//            }
//        }
//    }
}
