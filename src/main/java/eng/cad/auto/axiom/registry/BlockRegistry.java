package eng.cad.auto.axiom.registry;

import eng.cad.auto.axiom.AxiomMod;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class BlockRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, AxiomMod.MODID);

    public static final RegistryObject<Block> ETCHED_IRON_BLOCK = registerBlock("etched_iron_block", ()-> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));

    public static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block){
        RegistryObject<T> blockObject = BLOCKS.register(name, block);
        ItemRegistry.registerBlockItem(name, blockObject);
        return blockObject;
    }

}
