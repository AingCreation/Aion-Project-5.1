package com.aionemu.gameserver.configs.main;

import com.aionemu.commons.configuration.Property;

public class MembershipConfig
{
	@Property(key = "gameserver.membership.additional.chest.drop", defaultValue = "false")
	public static boolean ADD_CHEST_DROP;
	@Property(key = "gameserver.membership.additional.chest.drop.announce", defaultValue = "false")
	public static boolean ADD_CHEST_DROP_ANNOUNCE;
	@Property(key = "gameserver.instances.title.requirement", defaultValue = "10")
	public static byte INSTANCES_TITLE_REQ;
	@Property(key = "gameserver.instances.race.requirement", defaultValue = "10")
	public static byte INSTANCES_RACE_REQ;
	@Property(key = "gameserver.instances.level.requirement", defaultValue = "10")
	public static byte INSTANCES_LEVEL_REQ;
	@Property(key = "gameserver.instances.group.requirement", defaultValue = "10")
	public static byte INSTANCES_GROUP_REQ;
	@Property(key = "gameserver.instances.quest.requirement", defaultValue = "10")
	public static byte INSTANCES_QUEST_REQ;
	@Property(key = "gameserver.instances.cooldown", defaultValue = "10")
	public static byte INSTANCES_COOLDOWN;
	@Property(key = "gameserver.instances.item.requirement", defaultValue = "10")
	public static byte INSTANCES_ITEM_REQ;
	@Property(key = "gameserver.store.wh.all", defaultValue = "10")
	public static byte STORE_WH_ALL;
	@Property(key = "gameserver.store.accountwh.all", defaultValue = "10")
	public static byte STORE_AWH_ALL;
	@Property(key = "gameserver.store.legionwh.all", defaultValue = "10")
	public static byte STORE_LWH_ALL;
	@Property(key = "gameserver.trade.all", defaultValue = "10")
	public static byte TRADE_ALL;
	@Property(key = "gameserver.disable.soulbind", defaultValue = "10")
	public static byte DISABLE_SOULBIND;
	@Property(key = "gameserver.remodel.all", defaultValue = "10")
	public static byte REMODEL_ALL;
	@Property(key = "gameserver.emotions.all", defaultValue = "10")
	public static byte EMOTIONS_ALL;
	@Property(key = "gameserver.quest.stigma.slot", defaultValue = "10")
	public static byte STIGMA_SLOT_QUEST;
	@Property(key = "gameserver.soulsickness.disable", defaultValue = "10")
	public static byte DISABLE_SOULSICKNESS;
	@Property(key = "gameserver.autolearn.stigma", defaultValue = "10")
	public static byte STIGMA_AUTOLEARN;
	@Property(key = "gameserver.character.additional.enable", defaultValue = "10")
	public static byte CHARACTER_ADDITIONAL_ENABLE;
	@Property(key = "gameserver.advanced.friendlist.enable", defaultValue = "10")
	public static byte ADVANCED_FRIENDLIST_ENABLE;
	@Property(key = "gameserver.character.additional.count", defaultValue = "8")
	public static byte CHARACTER_ADDITIONAL_COUNT;
	@Property(key = "gameserver.advanced.friendlist.size", defaultValue = "90")
	public static int ADVANCED_FRIENDLIST_SIZE;
	
	// TITLE ADDITIONAL
	@Property(key = "gameserver.titles.additional.enable", defaultValue = "10")
    public static byte TITLES_ADDITIONAL_ENABLE;
	
	/**
     * Membership Tags
     */
    @Property(key = "gameserver.membership.tag.display", defaultValue = "true")
    public static boolean PREMIUM_TAG_DISPLAY;
    @Property(key = "gameserver.membership.tag.vip", defaultValue = "\uE02E %s")
    public static String TAG_VIP;
    @Property(key = "gameserver.membership.tag.premium", defaultValue = "\uE02D %s")
    public static String TAG_PREMIUM;
	
	//TITLE SPECIAL MEMBER.
	@Property(key = "gameserver.player.tag.30", defaultValue="")
    public static String PLAYER_TAG_30;
	@Property(key = "gameserver.player.tag.34", defaultValue="")
    public static String PLAYER_TAG_34;
	
	/**
	 * Toll Online Bonus
	 */
	@Property(key = "gameserver.toll.onlinebonus.enable", defaultValue = "true")
    public static boolean TOLL_ONLINE_BONUS_ENABLE;
	
    @Property(key = "gameserver.toll.onlinebonus.time", defaultValue = "60")
    public static int TOLL_ONLINE_BONUS_TIME;
	
    @Property(key = "gameserver.toll.onlinebonus.count", defaultValue = "1")
    public static int TOLL_ONLINE_BONUS_COUNT;
}