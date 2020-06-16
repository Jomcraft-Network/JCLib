package net.jomcraft.jclib;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;

public class EventHandlers {

	@SubscribeEvent
	public void shutdown(FMLServerStoppedEvent event) {
		if(JCLib.shutdownState.size() == 0) {
			MySQL.close();
			JCLib.keepaliveTimer.cancel();
		}
	}
}
