package net.jomcraft.jclib;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.toml.TomlParser;
import net.minecraftforge.api.distmarker.Dist;
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
	public static final Logger log = LogManager.getLogger(JCLib.MODID);
	public static final String VERSION = getModVersion();
	public static boolean databaseInitialized = false;
	public static Timer keepaliveTimer = new Timer();
	public static boolean keepaliveActivated = false;
	public static MySQL mysql;
	public static JCLib instance;
	
	public JCLib() {
		instance = this;
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigFile.COMMON_SPEC);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::postInit);
		
		final String any = FMLNetworkConstants.IGNORESERVERONLY;
		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> any, (test2, test) -> true));
	}
	
	public void postInit(FMLLoadCompleteEvent event) {
		if (ConfigFile.COMMON.only_server.get()) {
			DistExecutor.safeRunWhenOn(Dist.DEDICATED_SERVER, () -> () -> {
				if (ConfigFile.COMMON.connect.get())
					JCLib.connectMySQL();
			});
		} else {
			if (ConfigFile.COMMON.connect.get())
				JCLib.connectMySQL();
		}
	}
	
	public static boolean connectMySQL() {
		try {
			
			mysql = new MySQL(ConfigFile.COMMON.hostIP.get(), ConfigFile.COMMON.database.get(), ConfigFile.COMMON.username.get(), ConfigFile.COMMON.password.get());
			databaseInitialized = true;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public static String getModVersion() {
		//Stupid FG 3 workaround
		final TomlParser parser = new TomlParser();
		final InputStream stream = JCLib.class.getClassLoader().getResourceAsStream("META-INF/mods.toml");
		final CommentedConfig file = parser.parse(stream);

		return ((ArrayList<CommentedConfig>) file.get("mods")).get(0).get("version");
	}
	
	public static JCLib getInstance() {
		return instance;
	}
	
	public static boolean startKeepAlive(int minutes) {
		if (!keepaliveActivated) {
			keepaliveTimer.scheduleAtFixedRate(new KeepAlive(), minutes * 60 * 1000, minutes * 60 * 1000);
			keepaliveActivated = true;
			return true;
		}
		return false;
	}
	
	public static class KeepAlive extends TimerTask {
		public void run() {
			MySQL.update("SELECT VERSION();");
		}
	}
}