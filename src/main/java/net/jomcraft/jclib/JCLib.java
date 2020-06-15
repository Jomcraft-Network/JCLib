package net.jomcraft.jclib;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
	public static final String VERSION = "1.0.0";
	public static MySQL mysql;
	public static JCLib instance;
	
	public JCLib() {
		instance = this;
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigFile.COMMON_SPEC);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::postInit);
		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> "ANY", (remote, isServer) -> true));
		
	}
	
	public void postInit(FMLLoadCompleteEvent event) {
		JCLib.connectMySQL();
	}
	
	public static void connectMySQL() {
		try {
			
			mysql = new MySQL(ConfigFile.COMMON.hostIP.get(), ConfigFile.COMMON.database.get(), ConfigFile.COMMON.username.get(), ConfigFile.COMMON.password.get());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public static JCLib getInstance() {
		return instance;
	}
}