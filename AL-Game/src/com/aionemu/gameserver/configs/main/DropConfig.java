package com.aionemu.gameserver.configs.main;

import com.aionemu.commons.configuration.Property;

public class DropConfig
{
	/**
	 * Enable announce when a player get Epic item / Mythic item from chest.
	 */
	@Property(key = "gameserver.unique.chest.drop.announce.enable", defaultValue = "false")
	public static boolean ENABLE_UNIQUE_CHEST_DROP_ANNOUNCE;
	
	/**
	 * Other's
	 */
	@Property(key = "gameserver.drop.reduction.disable", defaultValue = "false")
	public static boolean DISABLE_DROP_REDUCTION;
	
	@Property(key = "gameserver.unique.drop.announce.enable", defaultValue = "true")
	public static boolean ENABLE_UNIQUE_DROP_ANNOUNCE;
	
	@Property(key = "gameserver.drop.noreduction", defaultValue = "0")
	public static String DISABLE_DROP_REDUCTION_IN_ZONES;
}