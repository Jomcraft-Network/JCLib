package net.jomcraft.jclib;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;

public class EventHandlers {

	@SubscribeEvent
	public void shutdown(FMLServerStoppedEvent event) {
		if(JCLib.shutdownState.size() == 0) {
			JCLib.getLog().info("Sent shutdown request to child handlers");
			JCLib.getRequestHandlers().values().forEach(db -> db.shutdown());
			JCLib.getLog().info("MySQL service shut down");
			JCLib.keepaliveTimer.cancel();
		}
	}
}
