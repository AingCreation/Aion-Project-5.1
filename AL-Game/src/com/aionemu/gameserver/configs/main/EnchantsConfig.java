package com.aionemu.gameserver.configs.main;

import com.aionemu.commons.configuration.Property;

public class EnchantsConfig
{
	/**
	 * Enable Cap Enchantment +25
	 */

	@Property(key = "gameserver.supplement.lesser", defaultValue = "1.0")
    public static float LESSER_SUP;
	
    @Property(key = "gameserver.supplement.regular", defaultValue = "1.0")
    public static float REGULAR_SUP;
	
    @Property(key = "gameserver.supplement.greater", defaultValue = "1.0")
    public static float GREATER_SUP;
	
	@Property(key = "gameserver.socket.manastone", defaultValue = "50")
	public static float SOCKET_MANASTONE;
	
	@Property(key = "gameserver.enchant.item", defaultValue = "50")
	public static float ENCHANT_ITEM;
	
	@Property(key = "gameserver.manastone.clean", defaultValue = "false")
	public static boolean CLEAN_STONE;
	
	@Property(key = "gameserver.enchant.cast.speed", defaultValue = "4000")
	public static int ENCHANT_SPEED;

	@Property(key = "gameserver.enchant.skill", defaultValue = "true")
	public static boolean ENCHANT_SKILL_ENABLE;

	@Property(key = "gameserver.enchant.item.broke", defaultValue = "true")
	public static boolean ENCHANT_ITEM_BROKE;

	/***
     * Tempering (Authorize) Rates
     */
    @Property(key = "gameserver.rate.tempering", defaultValue = "5")
    public static float TEMPERING_RATE;
    
    //Destroy Item ArchDaeva
  	@Property(key = "gameserver.archdaeva.item.destroy", defaultValue = "true")
  	public static boolean ENABLE_ARCHDAEVA_DESTROY_ITEM;
	
	/*
     * Enchant Manastone Rates test
     */
	@Property(key="gameserver.enable.manastone.rate.archdaeva", defaultValue="false")
    public static boolean ENABLE_MANASTONE_RATE_ARCHDAEVA;
	 
	@Property(key="gameserver.manastone.rate.slot1", defaultValue="100")
    public static int MANASTONE_RATE_SLOT1;

    @Property(key="gameserver.manastone.rate.slot2", defaultValue="80")
    public static int MANASTONE_RATE_SLOT2;

    @Property(key="gameserver.manastone.rate.slot3", defaultValue="70")
    public static int MANASTONE_RATE_SLOT3;

    @Property(key="gameserver.manastone.rate.slot4", defaultValue="50")
    public static int MANASTONE_RATE_SLOT4;

    @Property(key="gameserver.manastone.rate.slot5", defaultValue="30")
    public static int MANASTONE_RATE_SLOT5;

    @Property(key="gameserver.manastone.rate.slot6", defaultValue="20")
    public static int MANASTONE_RATE_SLOT6;
    
    /**
     * Custom Enchantments
     */
    @Property(key="gameserver.charged.stigma.max", defaultValue="5")
	public static int MAX_CHARGED_STIGMA;
	@Property(key="gameserver.max.cap.authorized.plume", defaultValue="20")
	public static int MAX_CAP_AUTHORIZED_PLUME;
	@Property(key="gameserver.max.cap.authorized.armor", defaultValue="20")
	public static int MAX_CAP_AUTHORIZED_ARMOR;
	@Property(key="gameserver.max.cap.authorized.weapon", defaultValue="20")
	public static int MAX_CAP_AUTHORIZED_WEAPON;
	@Property(key="gameserver.max.cap.authorized.accesories", defaultValue="20")
	public static int MAX_CAP_AUTHORIZED_ACCESORIES;
	@Property(key="gameserver.max.cap.authorized.wing", defaultValue="20")
	public static int MAX_CAP_AUTHORIZED_WING;
	@Property(key="gameserver.max.cap.enchant.armor1", defaultValue="20")
	public static int MAX_CAP_ENCHANT_ARMOR1;
	@Property(key="gameserver.max.cap.enchant.weapon1", defaultValue="20")
	public static int MAX_CAP_ENCHANT_WEAPON1;
	@Property(key="gameserver.max.cap.enchant.wing1", defaultValue="20")
	public static int MAX_CAP_ENCHANT_WING1;
	@Property(key="gameserver.max.cap.enchant.armor2", defaultValue="20")
	public static int MAX_CAP_ENCHANT_ARMOR2;
	@Property(key="gameserver.max.cap.enchant.weapon2", defaultValue="20")
	public static int MAX_CAP_ENCHANT_WEAPON2;
	@Property(key="gameserver.max.cap.enchant.wing2", defaultValue="20")
	public static int MAX_CAP_ENCHANT_WING2;
	@Property(key="gameserver.enable.enchant.alwayssuccess", defaultValue="true")
	public static boolean ENCHANT_ALWAYS_SUCCESS;
	@Property(key="gameserver.enable.temperance.alwayssuccess", defaultValue="true")
	public static boolean TEMPERANCE_ALWAYS_SUCCESS;
	@Property(key="gameserver.breakthrough.skill.min.level.type1", defaultValue="20")
	public static int BREAKTHROUGH_SKILL_MINLEVEL_TYPE1;
	@Property(key="gameserver.breakthrough.skill.min.level.type1_2", defaultValue="20")
	public static int BREAKTHROUGH_SKILL_MINLEVEL_TYPE1_2;
	@Property(key="gameserver.breakthrough.skill.min.level.type1_3", defaultValue="20")
	public static int BREAKTHROUGH_SKILL_MINLEVEL_TYPE1_3;
	@Property(key="gameserver.breakthrough.skill.min.level.type2", defaultValue="25")
	public static int BREAKTHROUGH_SKILL_MINLEVEL_TYPE2;
	@Property(key="gameserver.breakthrough.skill.min.level.type2_2", defaultValue="25")
	public static int BREAKTHROUGH_SKILL_MINLEVEL_TYPE2_2;
	@Property(key="gameserver.breakthrough.skill.min.level.type2_3", defaultValue="25")
	public static int BREAKTHROUGH_SKILL_MINLEVEL_TYPE2_3;
	@Property(key="gameserver.breakthrough.skill.reset.onmax.level", defaultValue="true")
	public static boolean BREAKTHROUGH_SKILL_RESET_ONMAX_LEVEL;
	@Property(key="gameserver.breakthrough.skill.fail.decresed.max", defaultValue="true")
	public static boolean BREAKTHROUGH_SKILL_FAIL_DECREASE_MAX;
	@Property(key="gameserver.skill.boost.max.level", defaultValue="10")
	public static int SKILL_BOOST_MAX_LEVEL;
	@Property(key="gameserver.destroy.item.when.fail.enchant", defaultValue="true")
	public static boolean DESTROY_ITEM_WHEN_FAIL_ENCHANT;
	@Property(key="gameserver.destroy.item.when.fail.temper", defaultValue="true")
	public static boolean DESTROY_ITEM_WHEN_FAIL_TEMPER;
	@Property(key="gameserver.destroy.essence.core.when.fail", defaultValue="true")
	public static boolean DESTROY_ESSENCE_CORE;
	@Property(key="gameserver.destroy.stigma.when.fail", defaultValue="true")
	public static boolean DESTROY_STIGMA_WHEN_FAIL;
	@Property(key="gameserver.destroy.archdaeva.item.onlevel", defaultValue="15")
	public static int DESTROY_ARCHDAEVA_ITEM_ONLEVEL;
	@Property(key="gameserver.destroy.archdaeva.item.percentage", defaultValue="50")
	public static int DESTROY_ARCHDAEVA_ITEM_PERCENTAGE;
}