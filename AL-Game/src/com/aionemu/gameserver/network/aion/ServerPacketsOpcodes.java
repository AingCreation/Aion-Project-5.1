package com.aionemu.gameserver.network.aion;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.aionemu.gameserver.network.aion.serverpackets.*;

public class ServerPacketsOpcodes
{
	private static Map<Class<? extends AionServerPacket>, Integer> opcodes = new HashMap<Class<? extends AionServerPacket>, Integer>();
	
	static {
		Set<Integer> idSet = new HashSet<Integer>();
		addPacketOpcode(SM_KEY.class, 0x48, idSet); //5.1
		addPacketOpcode(SM_VERSION_CHECK.class, 0x00, idSet); //5.1
		addPacketOpcode(SM_0x124.class, 0x124, idSet); //5.1
		addPacketOpcode(SM_TIME_CHECK.class, 0x27, idSet); //5.1
		addPacketOpcode(SM_L2AUTH_LOGIN_CHECK.class, 0xc7, idSet); //5.1
		addPacketOpcode(SM_ACCOUNT_PROPERTIES.class, 0xEE, idSet); //5.1
		addPacketOpcode(SM_CHARACTER_LIST.class, 0xC8, idSet); //5.1
		addPacketOpcode(SM_PONG.class, 0x8e, idSet); //5.1
		addPacketOpcode(SM_MAY_LOGIN_INTO_GAME.class, 0x89, idSet); //5.1
		addPacketOpcode(SM_CHARACTER_SELECT.class, 0xb1, idSet); //5.1
		addPacketOpcode(SM_A_STATION.class, 0x96, idSet); //5.1
		addPacketOpcode(SM_ABNORMAL_STATE.class, 0x31, idSet); //5.1
		addPacketOpcode(SM_SKILL_LIST.class, 0x2c, idSet); //5.1
		addPacketOpcode(SM_QUEST_COMPLETED_LIST.class, 0x7b, idSet); //5.1
		addPacketOpcode(SM_QUEST_LIST.class, 0x47, idSet); //5.1
		addPacketOpcode(SM_INVENTORY_UPDATE_ITEM.class, 0x1d, idSet); //5.1
		addPacketOpcode(SM_TITLE_INFO.class, 0xb0, idSet); //5.1
		addPacketOpcode(SM_MOTION.class, 0x94, idSet); //5.1
		addPacketOpcode(SM_ENTER_WORLD_CHECK.class, 0x0d, idSet); //5.1
		addPacketOpcode(SM_MACRO_LIST.class, 0xE7, idSet); //5.1
		addPacketOpcode(SM_UI_SETTINGS.class, 0x1E, idSet); //5.1
		addPacketOpcode(SM_ITEM_COOLDOWN.class, 0x67, idSet); //5.1
		addPacketOpcode(SM_INVENTORY_INFO.class, 0x1A, idSet); //5.1
		addPacketOpcode(SM_CHANNEL_INFO.class, 0xe5, idSet); //5.1
		addPacketOpcode(SM_STATS_INFO.class, 0x01, idSet); //5.1
		addPacketOpcode(SM_BIND_POINT_INFO.class, 0xeb, idSet); //5.1
		addPacketOpcode(SM_PLAYER_SPAWN.class, 0x0f, idSet); //5.1
		addPacketOpcode(SM_PACKAGE_INFO_NOTIFY.class, 0x10A, idSet); //5.1
		addPacketOpcode(SM_GAME_TIME.class, 0x26, idSet); //5.1
		addPacketOpcode(SM_TOWNS_LIST.class, 0xe2, idSet); //5.1
		addPacketOpcode(SM_MOVE.class, 0x37, idSet); //5.1
		addPacketOpcode(SM_ATTACK_STATUS.class, 0x05, idSet); //5.1
		addPacketOpcode(SM_CREATE_CHARACTER.class, 0xc9, idSet); //5.1
		addPacketOpcode(SM_NICKNAME_CHECK_RESPONSE.class, 0xe9, idSet); //5.1
		addPacketOpcode(SM_EMOTION.class, 0x25, idSet); //5.1
		addPacketOpcode(SM_PLAYER_INFO.class, 0x20, idSet); //5.1
		addPacketOpcode(SM_ATTACK.class, 0x36, idSet); //5.1
		addPacketOpcode(SM_DELETE.class, 0x16, idSet); //5.1
		addPacketOpcode(SM_NPC_INFO.class, 0x0e, idSet); //5.1
		addPacketOpcode(SM_EMOTION_NPC.class, 0xc6, idSet); //5.1
		addPacketOpcode(SM_SIEGE_LOCATION_INFO.class, 0xd1, idSet); //5.1
		addPacketOpcode(SM_PRICES.class, 0xfc, idSet); //5.1
		addPacketOpcode(SM_HOTSPOT_TELEPORT.class, 0x128, idSet); //5.1
		addPacketOpcode(SM_SYSTEM_MESSAGE.class, 0x19, idSet); //5.1
		addPacketOpcode(SM_MESSAGE.class, 0x18, idSet); //5.1
		addPacketOpcode(SM_FLY_TIME.class, 0xF4, idSet); //5.1
		addPacketOpcode(SM_INFLUENCE_RATIO.class, 0x55, idSet); //5.1
		addPacketOpcode(SM_EMOTION_LIST.class, 0x4f, idSet); //5.1
		addPacketOpcode(SM_AUTO_GROUP.class, 0x7a, idSet); //5.1
		addPacketOpcode(SM_PET.class, 0x65, idSet); //5.1
		addPacketOpcode(SM_ABYSS_RANK.class, 0xed, idSet); //5.1
		addPacketOpcode(SM_QUIT_RESPONSE.class, 0x62, idSet); //5.1
		addPacketOpcode(SM_ABYSS_RANK_UPDATE.class, 0x88, idSet); //5.1
		addPacketOpcode(SM_TARGET_SELECTED.class, 0x29, idSet); //5.1
		addPacketOpcode(SM_PLAYER_STATE.class, 0x44, idSet); //5.1
		addPacketOpcode(SM_HOUSE_OWNER_INFO.class, 0x107, idSet); //5.1
		addPacketOpcode(SM_ATREIAN_PASSPORT.class, 0x12B, idSet); //5.1
		addPacketOpcode(SM_UPDATE_PLAYER_APPEARANCE.class, 0x24, idSet); //5.1
		addPacketOpcode(SM_GATHERABLE_INFO.class, 0x11, idSet); //5.1
		addPacketOpcode(SM_FRIEND_STATUS.class, 0xe3, idSet); //5.1
		addPacketOpcode(SM_CASTSPELL.class, 0x21, idSet); //5.1
		addPacketOpcode(SM_CASTSPELL_RESULT.class, 0x2b, idSet); //5.1
		addPacketOpcode(SM_DIALOG_WINDOW.class, 0x3c, idSet); //5.1
		addPacketOpcode(SM_FRIEND_LIST.class, 0x84, idSet); //5.1
		addPacketOpcode(SM_WAREHOUSE_ADD_ITEM.class, 0xa9, idSet); //5.1
		addPacketOpcode(SM_LOOKATOBJECT.class, 0x28, idSet); //5.1
		addPacketOpcode(SM_DELETE_WAREHOUSE_ITEM.class, 0xaa, idSet); //5.1
		addPacketOpcode(SM_INVENTORY_ADD_ITEM.class, 0x1b, idSet); //5.1
		addPacketOpcode(SM_DELETE_ITEM.class, 0x1c, idSet); //5.1
		addPacketOpcode(SM_PLAY_MOVIE.class, 0x69, idSet); //5.1
		addPacketOpcode(SM_ITEM_USAGE_ANIMATION.class, 0xb7, idSet); //5.1
		addPacketOpcode(SM_TRADELIST.class, 0xfd, idSet); //5.1
		addPacketOpcode(SM_VIEW_PLAYER_DETAILS.class, 0x41, idSet); //5.1
		addPacketOpcode(SM_PLAYER_SEARCH.class, 0xd3, idSet); //5.1
		addPacketOpcode(SM_BLOCK_LIST.class, 0xe0, idSet); //5.1
		addPacketOpcode(SM_BLOCK_RESPONSE.class, 0xdf, idSet); //5.1
		addPacketOpcode(SM_LEGION_UPDATE_EMBLEM.class, 0xd7, idSet); //5.1
		addPacketOpcode(SM_SERVER_IDS.class, 0x114, idSet); //5.1
		addPacketOpcode(SM_CUBE_UPDATE.class, 0x82, idSet); //5.1
		addPacketOpcode(SM_QUEST_ACTION.class, 0x7C, idSet); //5.1
		addPacketOpcode(SM_LEGION_ADD_MEMBER.class, 0x6F, idSet); //5.1
		addPacketOpcode(SM_SELECT_ITEM_ADD.class, 0x11E, idSet); //5.1
		addPacketOpcode(SM_SELECT_ITEM_LIST.class, 0x11C, idSet); //5.1
		addPacketOpcode(SM_PRIVATE_STORE_NAME.class, 0x91, idSet); //5.1
		addPacketOpcode(SM_MACRO_RESULT.class, 0xe8, idSet); //5.1
		addPacketOpcode(SM_UPDATE_NOTE.class, 0x68, idSet); //5.1
		addPacketOpcode(SM_ABNORMAL_EFFECT.class, 0x32, idSet); //5.1
		addPacketOpcode(SM_FRIEND_RESPONSE.class, 0xDE, idSet); //5.1
		addPacketOpcode(SM_GROUP_INFO.class, 0x5A, idSet); //5.1
		addPacketOpcode(SM_GROUP_MEMBER_INFO.class, 0x5b, idSet); //5.1
		addPacketOpcode(SM_SHOW_BRAND.class, 0xf9, idSet); //5.1
		addPacketOpcode(SM_DIE.class, 0xC1, idSet); //5.1
		addPacketOpcode(SM_LOOT_STATUS.class, 0xCD, idSet); //5.1
		addPacketOpcode(SM_LOOT_ITEMLIST.class, 0xce, idSet); //5.1
		addPacketOpcode(SM_STATUPDATE_EXP.class, 0x08, idSet); //5.1
		addPacketOpcode(SM_STATUPDATE_HP.class, 0x03, idSet); //5.1
		addPacketOpcode(SM_STATUPDATE_MP.class, 0x04, idSet); //5.1
		addPacketOpcode(SM_LEGION_INFO.class, 0x6E, idSet); //5.1
		addPacketOpcode(SM_LEGION_UPDATE_NICKNAME.class, 0x0b, idSet); //5.1
		addPacketOpcode(SM_LEGION_UPDATE_SELF_INTRO.class, 0x77, idSet); //5.1
		addPacketOpcode(SM_LEGION_EDIT.class, 0x9E, idSet); //5.1
		addPacketOpcode(SM_LEGION_LEAVE_MEMBER.class, 0x70, idSet); //5.1
		addPacketOpcode(SM_LEGION_UPDATE_MEMBER.class, 0x71, idSet); //5.1
		addPacketOpcode(SM_LEGION_TABS.class, 0x0c, idSet); //5.1
		addPacketOpcode(SM_LEGION_SEND_EMBLEM.class, 0xD5, idSet); //5.1
		addPacketOpcode(SM_LEGION_MEMBERLIST.class, 0x9D, idSet); //5.1
		addPacketOpcode(SM_LEGION_SEARCH.class, 0x133, idSet); //5.1
		addPacketOpcode(SM_LEGION_REQUEST.class, 0x138, idSet); //5.1
		addPacketOpcode(SM_LEGION_REQUEST_INFO.class, 0x135, idSet); //5.1
		addPacketOpcode(SM_LEGION_REQUEST_LIST.class, 0x136, idSet); //5.1
        addPacketOpcode(SM_LEGION_REQUEST_PLAYER.class, 0x137, idSet); //5.1
		addPacketOpcode(SM_MAIL_SERVICE.class, 0xa1, idSet); //5.1
		addPacketOpcode(SM_RECIPE_LIST.class, 0xcf, idSet); //5.1
		addPacketOpcode(SM_NEARBY_QUESTS.class, 0x7f, idSet); //5.1
		addPacketOpcode(SM_ABYSS_RANKING_PLAYERS.class, 0x8A, idSet); //5.1
		addPacketOpcode(SM_ABYSS_RANKING_LEGIONS.class, 0x8b, idSet); //5.1
		addPacketOpcode(SM_ABYSS_LANDING.class, 0x13B, idSet); //5.1
		addPacketOpcode(SM_RIFT_ANNOUNCE.class, 0xec, idSet); //5.1
		addPacketOpcode(SM_INSTANCE_INFO.class, 0x8d, idSet); //5.1
		addPacketOpcode(SM_SERIAL_KILLER.class, 0x54, idSet); //5.1
		addPacketOpcode(SM_BROKER_SERVICE.class, 0x92, idSet); //5.1
		addPacketOpcode(SM_RESTORE_CHARACTER.class, 0xCB, idSet); //5.1
		addPacketOpcode(SM_DELETE_CHARACTER.class, 0xCA, idSet); //5.1
		addPacketOpcode(SM_TARGET_UPDATE.class, 0x51, idSet); //5.1
		addPacketOpcode(SM_CUSTOM_SETTINGS.class, 0xB8, idSet); //5.1
		addPacketOpcode(SM_PET_EMOTE.class, 0xBB, idSet); //5.1
		addPacketOpcode(SM_TRANSFORM.class, 0x3A, idSet); //5.1
		addPacketOpcode(SM_FRIEND_NOTIFY.class, 0xE1, idSet); //5.1
		addPacketOpcode(SM_DUEL.class, 0xB9, idSet); //5.1
		addPacketOpcode(SM_LEAVE_GROUP_MEMBER.class, 0xF7, idSet); //5.1
		addPacketOpcode(SM_GROUP_LOOT.class, 0x87, idSet); //5.1
		addPacketOpcode(SM_EXCHANGE_REQUEST.class, 0x4A, idSet); //5.1
		addPacketOpcode(SM_EXCHANGE_ADD_ITEM.class, 0x4B, idSet); //5.1
		addPacketOpcode(SM_EXCHANGE_ADD_KINAH.class, 0x4D, idSet); //5.1
		addPacketOpcode(SM_EXCHANGE_CONFIRMATION.class, 0x4E, idSet); //5.1
		addPacketOpcode(SM_PING_RESPONSE.class, 0x80, idSet); //5.1
		addPacketOpcode(SM_SELL_ITEM.class, 0x3E, idSet); //5.1
		addPacketOpcode(SM_LEVEL_UPDATE.class, 0x46, idSet); //5.1
		addPacketOpcode(SM_WAREHOUSE_INFO.class, 0xA8, idSet); //5.1
		addPacketOpcode(SM_WAREHOUSE_UPDATE_ITEM.class, 0xAB, idSet); //5.1
		addPacketOpcode(SM_CUSTOM_PACKET.class, 99999, idSet); //5.1
		addPacketOpcode(SM_CHAT_INIT.class, 0xe6, idSet); //5.1
		addPacketOpcode(SM_RECONNECT_KEY.class, 0xFF, idSet); //5.1
		addPacketOpcode(SM_TELEPORT_MAP.class, 0xC4, idSet); //5.1
		addPacketOpcode(SM_TELEPORT_LOC.class, 0x14, idSet); //5.1
		addPacketOpcode(SM_SHIELD_EFFECT.class, 0xDA, idSet); //5.1
		addPacketOpcode(SM_DISPUTE_LAND.class, 0x11B, idSet); //5.1
		addPacketOpcode(SM_HEADING_UPDATE.class, 0x39, idSet); //5.1
		addPacketOpcode(SM_HOUSE_OBJECTS.class, 0x10E, idSet); //5.1
		addPacketOpcode(SM_INSTANCE_COUNT_INFO.class, 0x93, idSet); //5.1
		addPacketOpcode(SM_WINDSTREAM_ANNOUNCE.class, 0xA4, idSet); //5.1
		addPacketOpcode(SM_ICON_INFO.class, 0xAF, idSet); //5.1
		addPacketOpcode(SM_WEATHER.class, 0x43, idSet); //5.1
		addPacketOpcode(SM_MARK_FRIENDLIST.class, 0x117, idSet); //5.1
		addPacketOpcode(SM_CHALLENGE_LIST.class, 0x118, idSet); //5.1
		addPacketOpcode(SM_QUESTIONNAIRE.class, 0xBF, idSet); //5.1
		addPacketOpcode(SM_PLAYER_MOVE.class, 0x15, idSet); //5.1
		addPacketOpcode(SM_STATUPDATE_DP.class, 0x06, idSet); //5.1
		addPacketOpcode(SM_DP_INFO.class, 0x07, idSet); //5.1
		addPacketOpcode(SM_PLAYER_STANCE.class, 0x1F, idSet); //5.1
		addPacketOpcode(SM_FORTRESS_STATUS.class, 0x56, idSet); //5.1
		addPacketOpcode(SM_FORTRESS_INFO.class, 0xF3, idSet); //5.1
		addPacketOpcode(SM_QUESTION_WINDOW.class, 0x34, idSet); //5.1
		addPacketOpcode(SM_MANTRA_EFFECT.class, 0xD0, idSet); //5.1
		addPacketOpcode(SM_LEARN_RECIPE.class, 0xF1, idSet); //5.1
		addPacketOpcode(SM_TRADE_IN_LIST.class, 0x097, idSet); //5.1
		addPacketOpcode(SM_REPURCHASE.class, 0xA7, idSet); //5.1
		addPacketOpcode(SM_TARGET_IMMOBILIZE.class, 0xCC, idSet); //5.1
		addPacketOpcode(SM_SKILL_CANCEL.class, 0x2A, idSet); //5.1
		addPacketOpcode(SM_CAPTCHA.class, 0x57, idSet); //5.1
		addPacketOpcode(SM_SIEGE_LOCATION_STATE.class, 0xD2, idSet); //5.1
		addPacketOpcode(SM_SUMMON_PANEL.class, 0x99, idSet); //5.1
		addPacketOpcode(SM_SUMMON_UPDATE.class, 0x9B, idSet); //5.1
		addPacketOpcode(SM_ABYSS_ARTIFACT_INFO3.class, 0xDC, idSet); //5.1
		addPacketOpcode(SM_INSTANCE_SCORE.class, 0x79, idSet); //5.1
		addPacketOpcode(SM_INSTANCE_STAGE_INFO.class, 0x8C, idSet); //5.1
		addPacketOpcode(SM_USE_OBJECT.class, 0xC5, idSet); //5.1
		addPacketOpcode(SM_FORCED_MOVE.class, 0xC3, idSet); //5.1
		addPacketOpcode(SM_SUMMON_PANEL_REMOVE.class, 0x49, idSet); //5.1
		addPacketOpcode(SM_SUMMON_OWNER_REMOVE.class, 0x9A, idSet); //5.1
		addPacketOpcode(SM_SKILL_REMOVE.class, 0x2D, idSet); //5.1
		addPacketOpcode(SM_SHOW_NPC_ON_MAP.class, 0x59, idSet); //5.1
		addPacketOpcode(SM_SUMMON_USESKILL.class, 0xA2, idSet); //5.1
		addPacketOpcode(SM_SKILL_COOLDOWN.class, 0x33, idSet); //5.1
		addPacketOpcode(SM_LEGION_SEND_EMBLEM_DATA.class, 0xD6, idSet); //5.1
		addPacketOpcode(SM_SKILL_ACTIVATION.class, 0x2E, idSet); //5.1
		addPacketOpcode(SM_GATHER_UPDATE.class, 0x23, idSet); //5.1
		addPacketOpcode(SM_FIND_GROUP.class, 0xA6, idSet); //5.1
		addPacketOpcode(SM_ALLIANCE_INFO.class, 0xF5, idSet); //5.1
		addPacketOpcode(SM_ALLIANCE_MEMBER_INFO.class, 0xF6, idSet); //5.1
		addPacketOpcode(SM_ALLIANCE_READY_CHECK.class, 0xFA, idSet); //5.1 5.1
		addPacketOpcode(SM_RENAME.class, 0x58, idSet); //5.1
		addPacketOpcode(SM_PLASTIC_SURGERY.class, 0x53, idSet); //5.1
		addPacketOpcode(SM_PLAYER_REGION.class, 0xD9, idSet); //5.1
		addPacketOpcode(SM_KISK_UPDATE.class, 0x90, idSet); //5.1
		addPacketOpcode(SM_GATHER_STATUS.class, 0x22, idSet); //5.1
		addPacketOpcode(SM_ABYSS_ARTIFACT_INFO.class, 0x60, idSet); //5.1
		addPacketOpcode(SM_LEGION_UPDATE_TITLE.class, 0x72, idSet); //5.1
		addPacketOpcode(SM_CRAFT_ANIMATION.class, 0xB4, idSet); //5.1
		addPacketOpcode(SM_NPC_ASSEMBLER.class, 0x0A, idSet); //5.1
		addPacketOpcode(SM_RESURRECT.class, 0xC2, idSet); //5.1
		addPacketOpcode(SM_LOGIN_QUEUE.class, 0x17, idSet); //5.1
		addPacketOpcode(SM_CHAT_WINDOW.class, 0x63, idSet); //5.1
		addPacketOpcode(SM_PRIVATE_STORE.class, 0x86, idSet); //5.1
		addPacketOpcode(SM_TRANSFORM_IN_SUMMON.class, 0x9C, idSet); //5.1
		addPacketOpcode(SM_WINDSTREAM.class, 0xA3, idSet); //5.1
		addPacketOpcode(SM_CRAFT_UPDATE.class, 0xB5, idSet); //5.1
		addPacketOpcode(SM_ASCENSION_MORPH.class, 0xB6, idSet); //5.1
		addPacketOpcode(SM_PETITION.class, 0xEF, idSet); //5.1
		addPacketOpcode(SM_FRIEND_UPDATE.class, 0xF0, idSet); //5.1
		addPacketOpcode(SM_RECIPE_DELETE.class, 0xF2, idSet); //5.1
		addPacketOpcode(SM_RECEIVE_BIDS.class, 0x103, idSet); //5.1
		addPacketOpcode(SM_OBJECT_USE_UPDATE.class, 0x108, idSet); //5.1
		//addPacketOpcode(SM_GROUP_DATA_EXCHANGE.class, 0xB2, idSet); //5.1
		addPacketOpcode(SM_HOUSE_EDIT.class, 0x52, idSet); //5.1
		addPacketOpcode(SM_HOUSE_UPDATE.class, 0x3D, idSet); //5.1
		addPacketOpcode(SM_HOUSE_OBJECT.class, 0x10C, idSet); //5.1
		addPacketOpcode(SM_HOUSE_BIDS.class, 0x100, idSet); //5.1
		addPacketOpcode(SM_HOUSE_PAY_RENT.class, 0x106, idSet); //5.1
		addPacketOpcode(SM_DELETE_HOUSE_OBJECT.class, 0x10D, idSet); //5.1
		addPacketOpcode(SM_HOUSE_RENDER.class, 0x10F, idSet); //5.1
		addPacketOpcode(SM_HOUSE_ACQUIRE.class, 0x113, idSet); //5.1
		addPacketOpcode(SM_DELETE_HOUSE.class, 0x110, idSet); //5.1
		addPacketOpcode(SM_HOUSE_REGISTRY.class, 0x74, idSet); //5.1
		addPacketOpcode(SM_HOUSE_SCRIPTS.class, 0x83, idSet); //5.1
		addPacketOpcode(SM_HOUSE_TELEPORT.class, 0xDD, idSet); //5.1
		addPacketOpcode(SM_BUTLER_SALUTE.class, 0xB2, idSet); //5.1
		addPacketOpcode(SM_USE_ROBOT.class, 0x5C, idSet); //5.1
		addPacketOpcode(SM_CASH_BUFF.class, 0xFB, idSet); //5.1
		addPacketOpcode(SM_UPGRADE_ARCADE.class, 0x12A, idSet); //5.1
		addPacketOpcode(SM_ESSENCE_INFO.class, 0x5D, idSet); //5.1
		addPacketOpcode(SM_ESSENCE_LIST.class, 0x5E, idSet); //5.1
		addPacketOpcode(SM_STONESPEAR_SIEGE.class, 0x12E, idSet); //5.1
		addPacketOpcode(SM_TERRITORY_LIST.class, 0x12F, idSet); //5.1
		addPacketOpcode(SM_LUNA_SHOP.class, 0x148, idSet); //5.1
		addPacketOpcode(SM_LUNA_SHOP_LIST.class, 0x147, idSet); //5.1
		addPacketOpcode(SM_EVENT_DICE.class, 0x149, idSet); //5.1
		addPacketOpcode(SM_TOLL_INFO.class, 0x9F, idSet); //5.1
		addPacketOpcode(SM_MEGAPHONE_MESSAGE.class, 0x123, idSet); //5.1
		addPacketOpcode(SM_TUNE_RESULT.class, 0x122, idSet); // 5.1 TODO
		addPacketOpcode(SM_AETHERFORGING_ANIMATION.class, 0x14D, idSet); //5.1
		addPacketOpcode(SM_AETHERFORGING_PLAYER.class, 0x14A, idSet); //5.1
		addPacketOpcode(SM_COALESCENCE_RESULT.class, 0x14B, idSet); //5.1
		addPacketOpcode(SM_COALESCENCE_WINDOW.class, 0x15B, idSet); //5.1
		addPacketOpcode(SM_BOOST_EVENTS.class, 0x146, idSet); //5.1
	}
	
	static int getOpcode(Class<? extends AionServerPacket> packetClass) {
		Integer opcode = opcodes.get(packetClass);
		if (opcode == null) {
			throw new IllegalArgumentException("There is no opcode for " + packetClass + " defined.");
		}
		return opcode;
	}
	
	private static void addPacketOpcode(Class<? extends AionServerPacket> packetClass, int opcode, Set<Integer> idSet) {
		if (opcode < 0) {
			return;
		} if (idSet.contains(opcode)) {
			throw new IllegalArgumentException(String.format("There already exists another packet with id 0x%02X", opcode));
		}
		idSet.add(opcode);
		opcodes.put(packetClass, opcode);
	}
}