package eng.cad.auto.axiology;

import eng.cad.auto.axiology.tests.TestPyrolyzer;
import net.minecraftforge.event.RegisterGameTestsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

//This stuff don't work and idk why
@Mod.EventBusSubscriber(modid = AxiomMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TestRegistry {
    @SubscribeEvent
    public static void register(RegisterGameTestsEvent event) {
        event.register(TestPyrolyzer.class);
    }
}