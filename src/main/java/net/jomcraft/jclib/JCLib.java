package net.jomcraft.jclib;

import java.io.InputStream;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.toml.TomlParser;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod("jclib")
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class JCLib {
	public static final String MODID = "jclib";
	public static final Logger log = LogManager.getLogger(JCLib.MODID);
	public static boolean tconstructLoaded = false;
	public static final String VERSION = getModVersion();
	public static JCLib instance;

	public JCLib() {
		instance = this;

		MinecraftForge.EVENT_BUS.register(JCLib.class);
		final ModLoadingContext modLoadingContext = ModLoadingContext.get();
		modLoadingContext.registerConfig(ModConfig.Type.COMMON, ConfigManager.SERVER_SPEC);
	}

	public static JCLib getInstance() {
		return instance;
	}

	@SuppressWarnings("unchecked")
	public static String getModVersion() {
		// Stupid FG 3 workaround
		TomlParser parser = new TomlParser();
		InputStream stream = JCLib.class.getClassLoader().getResourceAsStream("META-INF/mods.toml");
		CommentedConfig file = parser.parse(stream);
		return ((ArrayList<CommentedConfig>) file.get("mods")).get(0).get("version");
	}

	@SubscribeEvent
	public static void commonSetup(final FMLCommonSetupEvent event) {
		tconstructLoaded = ModList.get().isLoaded("tconstruct");
	}
}