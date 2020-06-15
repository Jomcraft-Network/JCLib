package net.jomcraft.jclib;

import java.io.InputStream;
import java.util.ArrayList;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.toml.TomlParser;

import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(value = JCLib.MODID)
public class JCLib {

	public static final String MODID = "jclib";
	public static final Logger log = LogManager.getLogger(JCLib.MODID);
	public static final String VERSION = getModVersion();
	public static boolean databaseInitialized = false;
	public static MySQL mysql;
	public static JCLib instance;
	
	public JCLib() {
		instance = this;
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigFile.COMMON_SPEC);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::postInit);
		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> "ANY", (remote, isServer) -> true));
		
	}
	
	public void postInit(FMLLoadCompleteEvent event) {
		if(ConfigFile.COMMON.connect.get())
			JCLib.connectMySQL();
	}
	
	public static void connectMySQL() {
		try {
			
			mysql = new MySQL(ConfigFile.COMMON.hostIP.get(), ConfigFile.COMMON.database.get(), ConfigFile.COMMON.username.get(), ConfigFile.COMMON.password.get());
			databaseInitialized = true;
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	@SuppressWarnings("unchecked")
	public static String getModVersion() {
		//Stupid FG 3 workaround
		TomlParser parser = new TomlParser();
		InputStream stream = JCLib.class.getClassLoader().getResourceAsStream("META-INF/mods.toml");
		CommentedConfig file = parser.parse(stream);

		return ((ArrayList<CommentedConfig>) file.get("mods")).get(0).get("version");
	}
	
	public static JCLib getInstance() {
		return instance;
	}
}