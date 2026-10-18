package eng.cad.auto.axiom.common.util;

import eng.cad.auto.axiom.common.fluid.GenericFluid;
import eng.cad.auto.axiom.common.items.CustomBucket;
import eng.cad.auto.axiom.registry.FluidRegistry;
import eng.cad.auto.axiom.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;

public class FluidHelper {
    public RegistryObject<Fluid> SOURCE;
    public RegistryObject<Fluid> FLOWING;
    public RegistryObject<FluidType> FLUIDTYPE;
    public RegistryObject<LiquidBlock> BLOCK;
    public RegistryObject<Item> BUCKET;
    public int color;
    public MapColor mapColor;
    private final GenericFluid.FluidBehaviour entityBehaviour;

    public FluidHelper(String name, int color, MapColor mapColor){
        this(name,color,mapColor, (a,b,c) -> {});
    }

    public FluidHelper(String name, int color, MapColor mapColor, GenericFluid.FluidBehaviour entitiyBehaviour){
        this.color = color;
        this.mapColor = mapColor;
        this.entityBehaviour = entitiyBehaviour;

        this.SOURCE = registerSource(name);
        this.FLOWING = registerFlowing(name);
        this.FLUIDTYPE = registerFluidType(name);
        this.BLOCK = registerBlock(name);
        this.BUCKET = registerBucket(name);
    }

    private ForgeFlowingFluid.Properties fluidProps() {
        return new ForgeFlowingFluid.Properties(
                () -> FLUIDTYPE.get(),
                () -> (ForgeFlowingFluid) SOURCE.get(),
                () -> (ForgeFlowingFluid) FLOWING.get())
                .block(BLOCK)
                .bucket(BUCKET)
                .tickRate(2);
    }
    private FluidType.Properties fluidTypeProps(int density, int viscosity) {
        return FluidType.Properties.create()
                .density(density)
                .viscosity(viscosity)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH);
    }
    private LiquidBlock.Properties blockProps(MapColor color) {
        return LiquidBlock.Properties.of().mapColor(color).noCollission().strength(100f).noLootTable().replaceable().liquid().pushReaction(PushReaction.DESTROY);
    }
    private RegistryObject<Fluid> registerSource(String name) {
        return FluidRegistry.registerFluid(name, () -> new GenericFluid.Source(fluidProps()));
    }

    private RegistryObject<Fluid> registerFlowing(String name) {
        return FluidRegistry.registerFluid(name+"_flowing", () -> new GenericFluid.Flowing(fluidProps()));
    }

    private RegistryObject<FluidType> registerFluidType(String name) {
        return FluidRegistry.FLUIDTYPES.register(name, () -> new FluidType(fluidTypeProps(1000,1000)) {
            @Override
            public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                consumer.accept(GenericFluid.getFluidRenderer(color));
            }
        });
    }

    private RegistryObject<LiquidBlock> registerBlock(String name){
        return FluidRegistry.registerFluidBlock(name, () -> new LiquidBlock(() -> (FlowingFluid) SOURCE.get(), blockProps(mapColor))
            {
                @Override
                public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
                    super.entityInside(blockState, level, blockPos, entity);
                    entityBehaviour.onEntityInside(level, blockPos, entity);
                }
            }
        );
    }

    private RegistryObject<Item> registerBucket(String name){
        return ItemRegistry.registerItem(name+"_bucket", ()-> new CustomBucket(SOURCE, new Item.Properties()));
    }

}
