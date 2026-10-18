package eng.cad.auto.axiom.registry;

import eng.cad.auto.axiom.AxiomMod;
import eng.cad.auto.axiom.common.block.AlembicBlock;
import net.minecraft.world.level.block.Block;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class BlockRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, AxiomMod.MODID);
    public static final RegistryObject<Block> ALEMBIC = registerBlock("alembic",
            () -> new AlembicBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<LiquidBlock> MORTIS_FLUID_BLOCK = registerBlockNoItem("mortis",
            () -> new LiquidBlock(() -> (FlowingFluid) FluidRegistry.MORTIS.get(), fluidProperties(MapColor.COLOR_BLACK)));

    public static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block){
        RegistryObject<T> blockObject = BLOCKS.register(name, block);
        ItemRegistry.registerBlockItem(name, blockObject);
        return blockObject;
    }
    public static <T extends Block> RegistryObject<T> registerBlockNoItem(String name, Supplier<T> block){
        RegistryObject<T> blockObject = BLOCKS.register(name, block);
        return blockObject;
    }

    private static Block.Properties fluidProperties(MapColor color) {
        return Block.Properties.of().mapColor(color).noCollission().strength(100f).noLootTable().replaceable().liquid().pushReaction(PushReaction.DESTROY);
    }
}
