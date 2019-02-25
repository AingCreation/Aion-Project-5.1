package com.aionemu.gameserver.configs.main;

import com.aionemu.commons.configuration.Property;

public class PvPConfig
{

  @Property(key="gameserver.pvp.chainkill.time.restriction", defaultValue="0")
  public static int CHAIN_KILL_TIME_RESTRICTION;

  @Property(key="gameserver.pvp.chainkill.number.restriction", defaultValue="30")
  public static int CHAIN_KILL_NUMBER_RESTRICTION;

  @Property(key="gameserver.pvp.max.leveldiff.restriction", defaultValue="9")
  public static int MAX_AUTHORIZED_LEVEL_DIFF;

  @Property(key="gameserver.pvp.medal.rewarding.enable", defaultValue="false")
  public static boolean ENABLE_MEDAL_REWARDING;

  @Property(key="gameserver.pvp.medal.reward.chance", defaultValue="10")
  public static float MEDAL_REWARD_CHANCE;

  @Property(key="gameserver.pvp.medal.reward.quantity", defaultValue="1")
  public static int MEDAL_REWARD_QUANTITY;

  @Property(key="gameserver.pvp.toll.rewarding.enable", defaultValue="false")
  public static boolean ENABLE_TOLL_REWARDING;

  @Property(key="gameserver.pvp.toll.reward.chance", defaultValue="50")
  public static float TOLL_REWARD_CHANCE;

  @Property(key="gameserver.pvp.toll.reward.quantity", defaultValue="1")
  public static int TOLL_REWARD_QUANTITY;

  @Property(key="gameserver.pvp.killingspree.enable", defaultValue="false")
  public static boolean ENABLE_KILLING_SPREE_SYSTEM;

  @Property(key="gameserver.pvp.raw.killcount.spree", defaultValue="20")
  public static int SPREE_KILL_COUNT;

  @Property(key="gameserver.pvp.raw.killcount.rampage", defaultValue="35")
  public static int RAMPAGE_KILL_COUNT;

  @Property(key="gameserver.pvp.raw.killcount.genocide", defaultValue="50")
  public static int GENOCIDE_KILL_COUNT;
  
  @Property(key="gameserver.pvp.raw.killcount.goodlike", defaultValue="80")
  public static int GOODLIKE_KILL_COUNT;
  
  @Property(key="gameserver.pvp.raw.killcount.legendary", defaultValue="100")
  public static int LEGENDARY_KILL_COUNT;

  @Property(key="gameserver.pvp.special_reward.type", defaultValue="0")
  public static int GENOCIDE_SPECIAL_REWARDING;

  @Property(key="gameserver.pvp.special_reward.chance", defaultValue="2")
  public static float SPECIAL_REWARD_CHANCE;
  
  // Bandit Reward
  @Property(key="gameserver.pvp.toll.pk.cost", defaultValue="0")
  public static int TOLL_PK_COST;
  
  // War system bonus reward
  /**
	 * War PvP System
	 */
  @Property(key = "gameserver.pvp.war.enable", defaultValue = "false")
	public static boolean WAR_PVP_SYSTEM;
  
  @Property(key = "gameserver.pvp.special.war.ap", defaultValue = "5000")
	public static int WAR_AP_REWARD;
  
  @Property(key = "gameserver.pvp.special.war.gp", defaultValue = "50")
	public static int WAR_GP_REWARD;
  
  @Property(key = "gameserver.pvp.special.war.mapid", defaultValue = "600010000")
  public static int WAR_MAPID;
  /**
	 * Advanced PvP System
	 */
  @Property(key = "gameserver.pvp.adv.enable", defaultValue = "true")
	public static boolean ADVANCED_PVP_SYSTEM;

  @Property(key = "gameserver.pvp.special.adv.ap", defaultValue = "0")
	public static int ADVANCED_AP_REWARD;

  @Property(key = "gameserver.pvp.special.adv.reward", defaultValue = "186000147")
  public static int ADVANCED_ITEM_REWARD;

  @Property(key = "gameserver.pvp.special.adv.count", defaultValue = "1")
  public static int ADVANCED_ITEM_COUNT;
  
  @Property(key = "gameserver.pvp.special.adv.mapid", defaultValue = "600010000")
  public static int ADVANCED_MAPID;
  
  @Property(key = "gameserver.default.custom.portal.worldid", defaultValue = "210040000")
  public static int DEFAULT_CUSTOM_PORTAL_WORLDID;

  @Property(key = "gameserver.default.custom.portal.spawn.asmo", defaultValue = "277.5999,1397.0684,104.25,4")
  public static String DEFAULT_CUSTOM_PORTAL_SPAWN_ASMO;

  @Property(key = "gameserver.default.custom.portal.spawn.ely", defaultValue = "457.18195,1392.9445,115.9263,47")
  public static String DEFAULT_CUSTOM_PORTAL_SPAWN_ELY;
  
  @Property(key = "gameserver.default.custom.portal.worldid.2", defaultValue = "210040000")
  public static int DEFAULT_CUSTOM_PORTAL_WORLDID_2;

  @Property(key = "gameserver.default.custom.portal.spawn.asmo.2", defaultValue = "277.5999,1397.0684,104.25,4")
  public static String DEFAULT_CUSTOM_PORTAL_SPAWN_ASMO_2;

  @Property(key = "gameserver.default.custom.portal.spawn.ely.2", defaultValue = "457.18195,1392.9445,115.9263,47")
  public static String DEFAULT_CUSTOM_PORTAL_SPAWN_ELY_2;
  
}