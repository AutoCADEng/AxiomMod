package eng.cad.auto.axiology.tests;

import eng.cad.auto.axiology.AxiomMod;
import eng.cad.auto.axiology.machines.multiblock.Pyrolyzer;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.items.IItemHandler;

@GameTestHolder(AxiomMod.MODID)
@PrefixGameTestTemplate(false)
public class TestPyrolyzer{
    @GameTest(template = "run_pyrolyzer_charcoal", timeoutTicks = Pyrolyzer.Logic.Max_Processing_Ticks + 50)
    public static void charcoalWorks(GameTestHelper helper) {
        helper.pullLever(0,3,3);
        helper.runAfterDelay(Pyrolyzer.Logic.Max_Processing_Ticks + 5,
        ()->{
            BlockEntity in_chest_be = helper.getBlockEntity(new BlockPos(0,2,4));
            BlockEntity in_tank_be = helper.getBlockEntity(new BlockPos(0,4,4));
            BlockEntity out_chest_be = helper.getBlockEntity(new BlockPos(4,2,4));
            BlockEntity out_tank_be = helper.getBlockEntity(new BlockPos(4,4,4));

            IFluidHandler tank_in = in_tank_be.getCapability(ForgeCapabilities.FLUID_HANDLER).orElseThrow(() -> new AssertionError("No fluid handler at tank position"));
            IFluidHandler tank_out = out_tank_be.getCapability(ForgeCapabilities.FLUID_HANDLER).orElseThrow(() -> new AssertionError("No fluid handler at tank position"));
            IItemHandler chest_in = in_chest_be.getCapability(ForgeCapabilities.ITEM_HANDLER).orElseThrow(() -> new AssertionError("No fluid handler at tank position"));
            IItemHandler chest_out = out_chest_be.getCapability(ForgeCapabilities.ITEM_HANDLER).orElseThrow(() -> new AssertionError("No fluid handler at tank position"));

            helper.assertTrue(chest_in.getStackInSlot(0).isEmpty(), "Input chest is not empty");
            helper.assertTrue(
                    chest_out.getStackInSlot(0).is(Items.CHARCOAL) && chest_out.getStackInSlot(0).getCount() == 2,
                    "Output chest does not contain 2 charcoal, got " + chest_out.getStackInSlot(0));
            FluidStack lava = tank_out.getFluidInTank(0);
            helper.assertTrue(
                    lava.getFluid().isSame(Fluids.LAVA) && lava.getAmount() >= 5,
                    "Expected at least 5 mB lava, got " + lava.getAmount() + " of " + lava.getFluid());
            helper.succeed();
        });
    }
}
