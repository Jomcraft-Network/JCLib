package net.jomcraft.jclib.bygmixin;

import java.io.File;

import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.jomcraft.jclib.ConfigManager;
import potionstudios.byg.world.biome.BYGTerraBlenderRegion;

@Mixin({ BYGTerraBlenderRegion.class })
public abstract class MixinBYGTerraBlenderRegion {

	@Redirect(method = "addBiomes(Lnet/minecraft/core/Registry;Ljava/util/function/Consumer;)V", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;)V"), remap = false)
	private void info(Logger logger, String message) {
		boolean checker = new File("byg_patch.bin").exists();
		if (checker && ConfigManager.SERVER.bygPatch.get())
			logger.debug(message);
		else
			logger.info(message);
	}
}