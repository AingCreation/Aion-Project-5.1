/*
 * This file is part of Encom. **ENCOM FUCK OTHER SVN**
 *
 *  Encom is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Encom is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser Public License
 *  along with Encom.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.configs.main;

import com.aionemu.commons.configuration.Property;

public class CustomConfig
{
	@Property(key = "gameserver.premium.notify", defaultValue = "false")
	public static boolean PREMIUM_NOTIFY;
	@Property(key = "gameserver.enchant.announce.enable", defaultValue = "true")
	public static boolean ENABLE_ENCHANT_ANNOUNCE;
	@Property(key = "gameserver.chat.factions.enable", defaultValue = "false")
	public static boolean SPEAKING_BETWEEN_FACTIONS;
	@Property(key = "gameserver.chat.whisper.level", defaultValue = "10")
	public static int LEVEL_TO_WHISPER;
	@Property(key = "gameserver.search.factions.mode", defaultValue = "false")
	public static boolean FACTIONS_SEARCH_MODE;
	@Property(key = "gameserver.search.gm.list", defaultValue = "false")
	public static boolean SEARCH_GM_LIST;
	@Property(key = "gameserver.cross.faction.binding", defaultValue = "false")
	public static boolean ENABLE_CROSS_FACTION_BINDING;
	@Property(key = "gameserver.simple.secondclass.enable", defaultValue = "false")
	public static boolean ENABLE_SIMPLE_2NDCLASS;
	@Property(key = "gameserver.skill.chain.triggerrate", defaultValue = "true")
	public static boolean SKILL_CHAIN_TRIGGERRATE;
	@Property(key = "gameserver.unstuck.delay", defaultValue = "3600")
	public static int UNSTUCK_DELAY;
	@Property(key = "gameserver.admin.dye.price", defaultValue = "1000000")
	public static int DYE_PRICE;
	@Property(key = "gameserver.base.flytime", defaultValue = "60")
	public static int BASE_FLYTIME;
	@Property(key = "gameserver.oldnames.coupon.disable", defaultValue = "false")
	public static boolean OLD_NAMES_COUPON_DISABLED;
	@Property(key = "gameserver.oldnames.command.disable", defaultValue = "true")
	public static boolean OLD_NAMES_COMMAND_DISABLED;
	@Property(key = "gameserver.friendlist.size", defaultValue = "90")
	public static int FRIENDLIST_SIZE;
	@Property(key = "gameserver.basic.questsize.limit", defaultValue = "40")
	public static int BASIC_QUEST_SIZE_LIMIT;
	@Property(key = "gameserver.instances.enable", defaultValue = "true")
	public static boolean ENABLE_INSTANCES;
	@Property(key = "gameserver.instances.mob.aggro", defaultValue = "300030000,300040000,300050000,300060000,300070000,300080000,300090000,300100000,300110000,300120000,300130000,300140000,300150000,300160000,300170000,300190000,300200000,300210000,300220000,300230000,300240000,300250000,300260000,300270000,300280000,300300000,300310000,300320000,300380000,300440000,300460000,300470000,300510000,300520000,300530000,300540000,300560000,300580000,300590000,300600000,300700000,300800000,300900000,301010000,301020000,301030000,301040000,301050000,301110000,301120000,301130000,301140000,301170000,301180000,301190000,301210000,310050000,310080000,310090000,310100000,310110000,320050000,320080000,320090000,320100000,320110000,320120000,320130000,320150000")
	public static String INSTANCES_MOB_AGGRO;
	@Property(key = "gameserver.instances.cooldown.filter", defaultValue = "0")
	public static String INSTANCES_COOL_DOWN_FILTER;
	@Property(key = "gameserver.instances.cooldown.rate", defaultValue = "1")
	public static int INSTANCES_RATE;
	@Property(key = "gameserver.enable.kinah.cap", defaultValue = "false")
	public static boolean ENABLE_KINAH_CAP;
	@Property(key = "gameserver.kinah.cap.value", defaultValue = "1000000000")
	public static long KINAH_CAP_VALUE;
	@Property(key = "gameserver.noap.mentor.group", defaultValue = "false")
	public static boolean MENTOR_GROUP_AP;
	@Property(key = "gameserver.faction.free", defaultValue = "true")
	public static boolean FACTION_FREE_USE;
	@Property(key = "gameserver.faction.prices", defaultValue = "10000")
	public static int FACTION_USE_PRICE;
	@Property(key = "gameserver.faction.cmdchannel", defaultValue = "true")
	public static boolean FACTION_CMD_CHANNEL;
	@Property(key = "gameserver.dialog.show.id", defaultValue = "true")
	public static boolean ENABLE_SHOW_DIALOG_ID;
	@Property(key = "gameserver.reward.service.enable", defaultValue = "false")
	public static boolean ENABLE_REWARD_SERVICE;
	@Property(key = "gameserver.limits.enable", defaultValue = "true")
	public static boolean LIMITS_ENABLED;
	@Property(key = "gameserver.limits.update", defaultValue = "0 0 0 ? * *")
	public static String LIMITS_UPDATE;
    @Property(key = "gameserver.limits.rate", defaultValue="1")
    public static int LIMITS_RATE;
    @Property(key = "gameserver.chat.text.length", defaultValue="150")
    public static int MAX_CHAT_TEXT_LENGHT;
    @Property(key = "gameserver.abyssxform.afterlogout", defaultValue="false")
    public static boolean ABYSSXFORM_LOGOUT;
    @Property(key = "gameserver.instance.duel.enable", defaultValue="true")
    public static boolean INSTANCE_DUEL_ENABLE;
    @Property(key =" gameserver.ride.restriction.enable", defaultValue="true")
    public static boolean ENABLE_RIDE_RESTRICTION;
	@Property(key = "gameserver.challenge.tasks.enabled", defaultValue = "false")
	public static boolean CHALLENGE_TASKS_ENABLED;
	@Property(key = "gameserver.commands.admin.dot.enable", defaultValue = "false")
	public static boolean ENABLE_ADMIN_DOT_COMMANDS;
	@Property(key = "gameserver.rift.enable", defaultValue = "true")
	public static boolean RIFT_ENABLED;
	@Property(key = "gameserver.rift.duration", defaultValue = "1")
	public static int RIFT_DURATION;
	@Property(key = "gameserver.rift.appear.chance", defaultValue = "50")
    public static int RIFT_APPEAR_CHANCE;
	@Property(key = "gameserver.auto.kinah.enabled", defaultValue = "false")
	public static boolean AUTO_KINAH_ENABLED;
	
	//Basic Quest limit size
	@Property(key = "gameserver.basic.cubesize.limit", defaultValue = "9")
	public static int BASIC_CUBE_SIZE_LIMIT;
	
	//Exp Progressive.
	@Property(key = "gameserver.enable.exp.progressive.ap.player", defaultValue = "false")
	public static boolean ENABLE_EXP_PROGRESSIVE_AP_PLAYER;
	@Property(key = "gameserver.enable.exp.progressive.ap.npc", defaultValue = "false")
	public static boolean ENABLE_EXP_PROGRESSIVE_AP_NPC;
	@Property(key = "gameserver.enable.exp.progressive.hunting", defaultValue = "false")
	public static boolean ENABLE_EXP_PROGRESSIVE_HUNTING;
	@Property(key = "gameserver.enable.exp.progressive.group.hunting", defaultValue = "false")
	public static boolean ENABLE_EXP_PROGRESSIVE_GROUP_HUNTING;
	@Property(key = "gameserver.enable.exp.progressive.quest", defaultValue = "false")
	public static boolean ENABLE_EXP_PROGRESSIVE_QUEST;
	
	//Vortex 3.9
	@Property(key = "gameserver.vortex.enable", defaultValue = "true")
	public static boolean VORTEX_ENABLED;
	@Property(key = "gameserver.vortex.duration", defaultValue = "2")
	public static int VORTEX_DURATION;
	
	//Dispute Land 3.0
	@Property(key = "gameserver.dispute.land.enable", defaultValue = "true")
	public static boolean DISPUTE_LAND_ENABLED;
	@Property(key = "gameserver.dispute.land.schedule", defaultValue = "0 0 2 ? * *")
	public static String DISPUTE_LAND_SCHEDULE;
	@Property(key = "gameserver.dispute.land.duration", defaultValue = "2")
	public static int DISPUTE_LAND_DURATION;
	
	//Beritra Invasion 4.7
	@Property(key = "gameserver.beritra.enable", defaultValue = "true")
	public static boolean BERITRA_ENABLED;
	@Property(key = "gameserver.beritra.duration", defaultValue = "2")
	public static int BERITRA_DURATION;
	
	//Agent Fight 4.7
	@Property(key = "gameserver.agent.enable", defaultValue = "true")
	public static boolean AGENT_ENABLED;
	@Property(key = "gameserver.agent.duration", defaultValue = "2")
	public static int AGENT_DURATION;
	
	//Berserk Anoha 4.7
	@Property(key = "gameserver.anoha.enable", defaultValue = "true")
	public static boolean ANOHA_ENABLED;
	@Property(key = "gameserver.anoha.duration", defaultValue = "1")
	public static int ANOHA_DURATION;
	
	//Panesterra 4.7
	@Property(key = "gameserver.svs.enable", defaultValue = "true")
	public static boolean SVS_ENABLED;
	@Property(key = "gameserver.svs.duration", defaultValue = "1")
	public static int SVS_DURATION;
	
	//R.v.R 4.9
	@Property(key = "gameserver.rvr.enable", defaultValue = "true")
	public static boolean RVR_ENABLED;
	@Property(key = "gameserver.rvr.duration", defaultValue = "1")
	public static int RVR_DURATION;
	
	//Moltenus
	@Property(key = "gameserver.moltenus.enable", defaultValue = "true")
	public static boolean MOLTENUS_ENABLED;
	@Property(key = "gameserver.moltenus.duration", defaultValue = "1")
	public static int MOLTENUS_DURATION;
	
	//Dynamic Rift
	//Shugo Merchant League
	//Tower Of Eternity
	@Property(key = "gameserver.dynamic.rift.enable", defaultValue = "true")
	public static boolean DYNAMIC_RIFT_ENABLED;
	@Property(key = "gameserver.dynamic.rift.dragon.schedule", defaultValue = "0 0 2 ? * *")
	public static String DYNAMIC_RIFT_DRAGON_SCHEDULE;
	@Property(key = "gameserver.dynamic.rift.indratoo.schedule", defaultValue = "0 0 2 ? * *")
	public static String DYNAMIC_RIFT_INDRATOO_SCHEDULE;
	@Property(key = "gameserver.shugo.merchant.league.schedule", defaultValue = "0 0 2 ? * *")
	public static String SHUGO_MERCHANT_LEAGUE_SCHEDULE;
	@Property(key = "gameserver.tower.of.eternity.schedule", defaultValue = "0 0 2 ? * *")
	public static String TOWER_OF_ETERNITY_SCHEDULE;
	@Property(key = "gameserver.dynamic.rift.duration", defaultValue = "1")
	public static int DYNAMIC_RIFT_DURATION;
	
	//Zorshiv Dredgion 2.1
	@Property(key = "gameserver.zorshiv.dredgion.enable", defaultValue = "true")
	public static boolean ZORSHIV_DREDGION_ENABLED;
	@Property(key = "gameserver.zorshiv.dredgion.duration", defaultValue = "1")
	public static int ZORSHIV_DREDGION_DURATION;
	
	//Live Party Concert Hall 4.3/4.8
	@Property(key = "gameserver.iu.enable", defaultValue = "true")
	public static boolean IU_ENABLED;
	@Property(key = "gameserver.iu.schedule", defaultValue = "0 0 2 ? * *")
	public static String IU_SCHEDULE;
	@Property(key = "gameserver.iu.duration", defaultValue = "1")
	public static int IU_DURATION;
	
	//Nightmare Circus 4.3
	@Property(key = "gameserver.nightmare.circus.enable", defaultValue = "true")
	public static boolean NIGHTMARE_CIRCUS_ENABLE;
	@Property(key = "gameserver.nightmare.circus.duration", defaultValue = "1")
	public static int NIGHTMARE_CIRCUS_DURATION;
	
	//Conquest/Offering 4.8
	@Property(key = "gameserver.conquest.enable", defaultValue = "true")
	public static boolean CONQUEST_ENABLED;
	@Property(key = "gameserver.conquest.duration", defaultValue = "1")
	public static int CONQUEST_DURATION;
	
	//Idian Depths 4.8
	//Source JAP: http://acb.blog69.fc2.com/blog-entry-1021.html?no=1024
	@Property(key = "gameserver.idian.depths.enable", defaultValue = "true")
	public static boolean IDIAN_DEPTHS_ENABLED;
	@Property(key = "gameserver.idian.depths.schedule", defaultValue = "0 0 6 ? * *")
    public static String IDIAN_DEPTHS_SCHEDULE;
	@Property(key = "gameserver.idian.depths.duration", defaultValue = "1")
	public static int IDIAN_DEPTHS_DURATION;
	
	//Instance Rift 4.9
	//Source KOR: http://aion.power.plaync.com/wiki/%EA%B0%95%EC%B2%A0%EC%9D%98+%EB%AC%B4%EB%8D%A4+%EB%B9%84%EB%B0%80%EC%B0%BD%EA%B3%A0
	@Property(key = "gameserver.instance.rift.enable", defaultValue = "true")
	public static boolean INSTANCE_RIFT_ENABLED;
	@Property(key = "gameserver.instance.rift.duration", defaultValue = "24")
	public static int INSTANCE_RIFT_DURATION;
	
   /**
	* On official server "KOR/JAP/NA" every time a player disconnect from
	* server, after reconnect is he always teleport to "Bind Point"
	*/
	@Property(key = "gameserver.reconnect.to.bind.point", defaultValue = "true")
	public static boolean ENABLE_RECONNECT_TO_BIND_POINT;
	
	//Base Rewards
	@Property(key = "gameserver.base.rewards.enable", defaultValue = "true")
	public static boolean ENABLE_BASE_REWARDS;
	
	//Protector/Conqueror 4.8
	@Property(key = "gameserver.protector.conqueror.enable", defaultValue = "true")
	public static boolean PROTECTOR_CONQUEROR_ENABLE;
	@Property(key = "gameserver.protector.conqueror.handled.worlds", defaultValue = "")
	public static String PROTECTOR_CONQUEROR_WORLDS = "";
	@Property(key = "gameserver.protector.conqueror.kills.refresh", defaultValue = "5")
	public static int PROTECTOR_CONQUEROR_REFRESH;
	@Property(key = "gameserver.protector.conqueror.kills.decrease", defaultValue = "1")
	public static int PROTECTOR_CONQUEROR_DECREASE;
	@Property(key = "gameserver.protector.conqueror.level.diff", defaultValue = "10")
	public static int PROTECTOR_CONQUEROR_LEVEL_DIFF;
	@Property(key = "gameserver.protector.conqueror.1st.rank.kills", defaultValue = "5")
	public static int PROTECTOR_CONQUEROR_1ST_RANK_KILLS;
	@Property(key = "gameserver.protector.conqueror.2nd.rank.kills", defaultValue = "10")
	public static int PROTECTOR_CONQUEROR_2ND_RANK_KILLS;
	
	//Illusion Godstones.
	@Property(key = "gameserver.break.illusion.godstones", defaultValue = "1.0")
	public static float ILLUSION_GODSTONE_BREAK_RATE;
	
	//Energy Of Respose.
	@Property(key = "gameserver.energy.of.repose.enable", defaultValue = "false")
	public static boolean ENERGY_OF_REPOSE_ENABLE;
	
	@Property(key = "gameserver.item.not.for.archdaeva.enable", defaultValue = "false")
	public static boolean ITEM_NOT_FOR_ARCHDAEVA_ENABLE;
	
	//Luna Shop.
	@Property(key = "gameserver.enable.luna.cap", defaultValue = "false")
	public static boolean ENABLE_LUNA_CAP;
	@Property(key = "gameserver.luna.cap.value", defaultValue = "9999999")
	public static long LUNA_CAP_VALUE;
	
	//Bind Point
	@Property(key = "gameserver.storebindpoint.enable", defaultValue = "false")
    public static boolean ENABLE_STORE_BINDPOINT;
	
	//MAX CP LIMIT
	@Property(key = "gameserver.max.cp.limit", defaultValue = "1000")
	public static int CP_LIMIT_MAX;
	
	// World Channel Cost
	@Property(key = "gameserver.worldchannel.costs", defaultValue = "50000")
	public static int WORLD_CHANNEL_AP_COSTS;
	
	// Auto Powershard
	@Property(key = "gameserver.enable.auto.powershard", defaultValue = "false")
	public static boolean ENABLE_AUTO_POWERSHARD;
	
	/**
     * Rank Item Limit
     */
    @Property(key="gameserver.allow.rank.item.limit", defaultValue="false")
    public static boolean RANK_ITEM_LIMIT;
    
    @Property(key="gameserver.rank.min.item", defaultValue="15")
    public static int RANK_LIMIT;
    
    /**
     * Custom Enchant Skills
     */
    @Property(key="gameserver.enable.breakthrough.skill", defaultValue="true")
	public static boolean ENABLE_BREAKTHOUGH_SKILL;
	
	/**
     * PvP GP Config
     */
    @Property(key = "gameserver.enable.gp.reward", defaultValue = "true")
    public static boolean ENABLE_GP_REWARD;

    @Property(key = "gameserver.gp.rate.modifier", defaultValue = "2.0")
    public static float GP_RATE_MODIFIER;

    @Property(key = "gameserver.gp.rate.lost.minus.rank.player", defaultValue = "2.0")
    public static float GP_RATE_LOST_MINUS_RANK;

    @Property(key = "gameserver.gp.rate.lost.equal.higher.rank.player", defaultValue = "2.0")
    public static float GP_RATE_LOST_EQUAL_HIGHER_RANK;

    @Property(key = "gameserver.gp.rate.gain.minus.rank.player", defaultValue = "2.0")
    public static float GP_RATE_GAIN_MINUS_RANK;

    @Property(key = "gameserver.gp.rate.gain.equal.higher.rank.player", defaultValue = "2.0")
    public static float GP_RATE_GAIN_EQUAL_HIGHER_RANK;

    @Property(key = "gameserver.member.gp.gain", defaultValue = "0.7")
    public static float MEMBER_GP_GAIN;

    @Property(key = "gameserver.alliance.gp.gain", defaultValue = "0.7")
    public static float ALLIANCE_GP_GAIN;

    @Property(key = "gameserver.min.player.gp.reward", defaultValue = "70")
    public static int REWARD_GP_MIN_LEVEL;

    @Property(key = "gameserver.gp.gain.aggro.attacker", defaultValue = "0.1")
    public static float GP_GAIN_AGGRO_ATTACKER;

    @Property(key = "gameserver.enable.gp.lose", defaultValue = "false")
    public static boolean ENABLE_GP_LOSE;
	
	/**
	 * Custom BossSpawnService
	 */
	@Property(key = "gameserver.custombosses.enable", defaultValue = "true")
	public static boolean CUSTOMBOSSES_ENABLE;
	
	@Property(key = "gameserver.custombosses.delay", defaultValue = "60")
    public static int CUSTOMBOSSES_DELAY;
}