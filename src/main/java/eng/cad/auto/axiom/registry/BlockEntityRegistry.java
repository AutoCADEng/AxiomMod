package eng.cad.auto.axiom.registry;

import eng.cad.auto.axiom.AxiomMod;
import eng.cad.auto.axiom.common.blockentitiy.AlembicBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockEntityRegistry
{
    public static final DeferredRegister<BlockEntityType<?>> BLOCKENTITYTYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, AxiomMod.MODID);

    public static final RegistryObject<BlockEntityType<AlembicBlockEntity>> ALEMBIC =
            BLOCKENTITYTYPES.register("alembic",
                    () -> BlockEntityType.Builder.of(AlembicBlockEntity::new, BlockRegistry.ALEMBIC.get()).build(null));
    
}
