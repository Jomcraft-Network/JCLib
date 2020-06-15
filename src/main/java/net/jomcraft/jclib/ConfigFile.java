package net.jomcraft.jclib;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigFile {

	private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

	public static final Common COMMON = new Common(COMMON_BUILDER);

	public static final ForgeConfigSpec COMMON_SPEC = COMMON_BUILDER.build();

	public static class Common {

		public final ForgeConfigSpec.ConfigValue<String> hostIP;
		
		public final ForgeConfigSpec.BooleanValue connect;
		
		public final ForgeConfigSpec.ConfigValue<String> username;
		
		public final ForgeConfigSpec.ConfigValue<String> password;
		
		public final ForgeConfigSpec.ConfigValue<String> port;
		
		public final ForgeConfigSpec.ConfigValue<String> database;

		Common(ForgeConfigSpec.Builder builder) {

			builder.push("Common");

			String desc = "MariaDB server host ip";
			hostIP = builder.comment(desc).define("hostIP", "localhost");
			
			desc = "Should the server connect to the database on startup?";
			connect = builder.comment(desc).define("connect", false);
			
			desc = "MariaDB server username";
			username = builder.comment(desc).define("username", "JCLib");
			
			desc = "MariaDB database password";
			password = builder.comment(desc).define("password", "password");
			
			desc = "MariaDB server port";
			port = builder.comment(desc).define("port", "3306");
			
			desc = "MariaDB database name";
			database = builder.comment(desc).define("database", "JCLib");

			builder.pop();
		}
	}
}