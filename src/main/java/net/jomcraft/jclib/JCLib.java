package net.jomcraft.jclib;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.toml.TomlParser;
import com.google.common.eventbus.EventBus;
import net.jomcraft.jclib.events.DBConnectEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;

@Mod(value = JCLib.MODID)
public class JCLib {

	public static final String MODID = "jclib";
	private static final Logger log = LogManager.getLogger(JCLib.MODID);
	public static final String VERSION = getModVersion();
	static Timer keepaliveTimer = new Timer();
	static HashMap<String, Boolean> shutdownState = new HashMap<String, Boolean>();
	private static boolean keepaliveActivated = false;
	public static EventBus eventBus = new EventBus();
	private static HashMap<String, ConnectionRequest> connectionRequests = new HashMap<String, ConnectionRequest>();
	private static HashMap<String, DBRequestHandler> requestHandlers = new HashMap<String, DBRequestHandler>();
	static MySQL mysql;
	private static JCLib instance;
	
	public JCLib() {
		instance = this;
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigFile.COMMON_SPEC);
		MinecraftForge.EVENT_BUS.register(new EventHandlers());
		final String any = FMLNetworkConstants.IGNORESERVERONLY;
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::postInit);
		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> any, (test2, test) -> true));
	}
	
	public static void communicateLogin(String modid) {
		JCLib.getLog().info(modid + " has been added to the tracking system");
		shutdownState.put(modid, false);
	}
	
	public static void readyForShutdown(String modid) {
		shutdownState.put(modid, true);
		
		for(boolean state : shutdownState.values()) {
			if(state == false)
				return;
		}
		JCLib.getLog().info("Sent shutdown request to child handlers");
		requestHandlers.values().forEach(db -> db.shutdown());
		JCLib.getLog().info("MySQL service shut down");
		JCLib.keepaliveTimer.cancel();
	}
	
	@SuppressWarnings("deprecation")
	public void postInit(FMLLoadCompleteEvent event) {
		if (ConfigFile.COMMON.only_server.get()) {
			DistExecutor.runWhenOn(Dist.DEDICATED_SERVER, () -> () -> {
				connectionRequests.put(JCLib.MODID, new ConnectionRequest("JCLib", new JCLibConnectionRequest()));
				for(Entry<String, ConnectionRequest> db : connectionRequests.entrySet()) {
					String modid = db.getKey();
					ConnectionRequest request = db.getValue();
					MySQL connection = connectMySQL(request.getDbName());
					if(connection != null) {
						DBRequestHandler handler = request.getHandler().establishCon(connection);
						requestHandlers.put(modid, handler);
					}
				}
				connectionRequests.clear();
			});
		} else {
			connectionRequests.put(JCLib.MODID, new ConnectionRequest("JCLib", new JCLibConnectionRequest()));
			for(Entry<String, ConnectionRequest> db : connectionRequests.entrySet()) {
				String modid = db.getKey();
				ConnectionRequest request = db.getValue();
				MySQL connection = connectMySQL(request.getDbName());
				if(connection != null) {
					DBRequestHandler handler = request.getHandler().establishCon(connection);
					requestHandlers.put(modid, handler);
				}
			}
			connectionRequests.clear();
		}
	}
	
	public static MySQL connectMySQL(String dbName) {
		try {
			JCLib.getLog().info("Attempting to connect to the MySQL database: " + dbName);
			MySQL connection = new MySQL(ConfigFile.COMMON.hostIP.get(), dbName, ConfigFile.COMMON.username.get(), ConfigFile.COMMON.password.get());
			JCLib.eventBus.post(new DBConnectEvent(dbName, Event.Result.ALLOW));
			
			return connection;
		} catch (Exception e) {
			JCLib.getLog().error("Couldn't connect to the MySQL database " + dbName + ": ", e);
			JCLib.eventBus.post(new DBConnectEvent(dbName, Event.Result.DENY));
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static String getModVersion() {
		//Stupid FG 3 workaround
		final TomlParser parser = new TomlParser();
		final InputStream stream = JCLib.class.getClassLoader().getResourceAsStream("META-INF/mods.toml");
		final CommentedConfig file = parser.parse(stream);

		return ((ArrayList<CommentedConfig>) file.get("mods")).get(0).get("version");
	}

	public static class KeepAlive extends TimerTask {
		public void run() {
			if(requestHandlers.containsKey(JCLib.MODID)) {
				requestHandlers.get(JCLib.MODID).sendVoidQuery("SELECT VERSION();");
			} else if(requestHandlers.size() > 0) {
				requestHandlers.values().iterator().next().sendVoidQuery("SELECT VERSION();");
			}
		}
	}
	
	public static boolean startKeepAlive(int minutes) {
		if (!keepaliveActivated) {
			JCLib.getLog().info("Activated keep-alive task");
			keepaliveTimer.scheduleAtFixedRate(new KeepAlive(), minutes * 60 * 1000, minutes * 60 * 1000);
			keepaliveActivated = true;
			return true;
		}
		return false;
	}
	
	public static void putConnectionRequest(String modid, ConnectionRequest request) {
		if(!connectionRequests.containsKey(modid))
			connectionRequests.put(modid, request);
	}
	
	public static HashMap<String, DBRequestHandler> getRequestHandlers() {
		return requestHandlers;
	}
	
	public static JCLib getInstance() {
		return instance;
	}
	
	public static Logger getLog() {
		return log;
	}
}