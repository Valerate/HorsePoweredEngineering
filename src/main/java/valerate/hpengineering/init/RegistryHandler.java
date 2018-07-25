package valerate.hpengineering.init;

import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class RegistryHandler {	
	
	@SubscribeEvent
	public static void onBlockRegister(RegistryEvent.Register<Block> event) {
	}

	
}
