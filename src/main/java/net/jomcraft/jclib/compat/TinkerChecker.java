package net.jomcraft.jclib.compat;

import net.minecraft.world.item.Item;
import slimeknights.tconstruct.tools.item.ModifiableSwordItem;

public class TinkerChecker {

	public static boolean isInstanceOf(Item item) {
		return item instanceof ModifiableSwordItem;
	}

}
