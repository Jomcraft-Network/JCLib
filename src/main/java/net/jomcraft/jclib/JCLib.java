package net.jomcraft.jclib;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.toml.TomlParser;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
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
	private static boolean databaseInitialized = false;
	static Timer keepaliveTimer = new Timer();
	static HashMap<String, Boolean> shutdownState = new HashMap<String, Boolean>();
	private static boolean keepaliveActivated = false;
	public static MySQL mysql;
	private static JCLib instance;
	
	public JCLib() {
		instance = this;
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigFile.COMMON_SPEC);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::postInit);
		MinecraftForge.EVENT_BUS.register(new EventHandlers());
		final String any = FMLNetworkConstants.IGNORESERVERONLY;
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
		
		JCLib.getLog().info("MySQL service shut down");
		MySQL.close();
		JCLib.keepaliveTimer.cancel();
	}
	
	@SuppressWarnings("deprecation")
	public void postInit(FMLLoadCompleteEvent event) {
		if (ConfigFile.COMMON.only_server.get()) {
			DistExecutor.runWhenOn(Dist.DEDICATED_SERVER, () -> () -> {
				if (ConfigFile.COMMON.connect.get())
					JCLib.connectMySQL();
			});
		} else {
			if (ConfigFile.COMMON.connect.get())
				JCLib.connectMySQL();
		}
	}
	
	public static boolean connectMySQL() {
		if (!databaseInitialized) {
			try {
				JCLib.getLog().info("Attempting to connect to the MySQL database");
				mysql = new MySQL(ConfigFile.COMMON.hostIP.get(), ConfigFile.COMMON.database.get(), ConfigFile.COMMON.username.get(), ConfigFile.COMMON.password.get());
				databaseInitialized = true;
				return true;
			} catch (Exception e) {
				JCLib.getLog().error("Couldn't connect to the MySQL database: ", e);
			}
			return false;
		} else {
			return true;
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
			try {
				MySQL.update("SELECT VERSION();");
			} catch (ClassNotFoundException | SQLException e) {
				JCLib.getLog().error("Couldn't keep-alive: ", e);
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
	
	public static JCLib getInstance() {
		return instance;
	}
	
	public static Logger getLog() {
		return log;
	}
	
	/**
     * @deprecated Using this method is no longer necessary.
     */
	public static boolean databaseInitialized() {
		return databaseInitialized;
	}
}