package com.aionemu.gameserver.configs.main;

import com.aionemu.commons.configuration.Property;

/**
 * @author Idhacker542
 */
public class WarConfig {
	
	/**
	 * War System
	 */
	 @Property(key="gameserver.war.enable", defaultValue = "true")
	 public static boolean WAR_ENABLE;
	
	/**
	 * War Schedule
	 */
	@Property(key="gameserver.war.schedule.time", defaultValue = "0 0 1 ? * *")
    public static String WAR_SYSTEM_SCHEDULE;
	@Property(key="gameserver.war.announce", defaultValue = "50")
    public static int WAR_ANNOUNCE;
	@Property(key="gameserver.war.runtime", defaultValue = "1")
    public static int WAR_RUNTIME;
	
	/**
	 * War Reward
	 */
	@Property(key="gameserver.war.gp.reward", defaultValue = "500")
	public static int WAR_GP_REWARD;
	@Property(key="gameserver.war.item.reward", defaultValue = "186000242")
	public static int WAR_ITEM_REWARD;
	@Property(key="gameserver.war.item.amount", defaultValue = "5")
	public static int WAR_ITEM_AMOUNT;
	@Property(key="gameserver.war.item.reward.1", defaultValue = "186000242")
	public static int WAR_ITEM_REWARD_1;
	@Property(key="gameserver.war.item.amount.1", defaultValue = "5")
	public static int WAR_ITEM_AMOUNT_1;
	@Property(key="gameserver.war.item.reward.2", defaultValue = "186000242")
	public static int WAR_ITEM_REWARD_2;
	@Property(key="gameserver.war.item.amount.2", defaultValue = "5")
	public static int WAR_ITEM_AMOUNT_2;
	@Property(key="gameserver.war.item.reward.3", defaultValue = "186000242")
	public static int WAR_ITEM_REWARD_3;
	@Property(key="gameserver.war.item.amount.3", defaultValue = "5")
	public static int WAR_ITEM_AMOUNT_3;
	
	@Property(key="gameserver.war.item.amount.loss", defaultValue = "5")
	public static int WAR_ITEM_AMOUNT_LOSS;
	@Property(key="gameserver.war.item.amount.1.loss", defaultValue = "5")
	public static int WAR_ITEM_AMOUNT_1_LOSS;
	@Property(key="gameserver.war.item.amount.2.loss", defaultValue = "5")
	public static int WAR_ITEM_AMOUNT_2_LOSS;
	@Property(key="gameserver.war.item.amount.3.loss", defaultValue = "5")
	public static int WAR_ITEM_AMOUNT_3_LOSS;
	
	/**
	 * War Reward Loss
	 */
	@Property(key="gameserver.war.gp.reward.loss", defaultValue = "50")
	public static int WAR_GP_REWARD_LOSS;
	@Property(key="gameserver.war.item.reward.loss", defaultValue = "186000242")
	public static int WAR_ITEM_REWARD_LOSS;

}
