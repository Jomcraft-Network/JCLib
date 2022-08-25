package net.jomcraft.jclib;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {

	public ForgeConfigSpec.ConfigValue<Boolean> bygPatch;
	public ForgeConfigSpec.ConfigValue<Boolean> swordPatch;

	public Config(final ForgeConfigSpec.Builder builder) {
		builder.push("General");
		this.bygPatch = builder.comment("Enables BYG console spam patch").translation("BYG-Patch").define("BYG-Patch", false);

		this.swordPatch = builder.comment("Stops swords from breaking grass/bush blocks").translation("Sword-Patch").define("Sword-Patch", false);
		builder.pop();
	}
}