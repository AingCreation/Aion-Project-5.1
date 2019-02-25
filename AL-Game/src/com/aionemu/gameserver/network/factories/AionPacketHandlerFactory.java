package com.aionemu.gameserver.network.factories;

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.AionPacketHandler;
import com.aionemu.gameserver.network.aion.clientpackets.*;

public class AionPacketHandlerFactory
{
	private AionPacketHandler handler;
	
	public static AionPacketHandlerFactory getInstance() {
		return SingletonHolder.instance;
	}
	
	public AionPacketHandlerFactory() {
		handler = new AionPacketHandler();
		addPacket(new CM_VERSION_CHECK(0x00DA, State.CONNECTED)); //5.1
		addPacket(new CM_TIME_CHECK(0x00E8, State.CONNECTED, State.AUTHED, State.IN_GAME)); //5.1
		addPacket(new CM_MAC_ADDRESS(0x0187, State.CONNECTED, State.AUTHED, State.IN_GAME)); //5.1
		addPacket(new CM_CHARACTER_LIST(0x15C, State.AUTHED)); //5.1
		addPacket(new CM_PING(0x2F6, State.AUTHED, State.IN_GAME)); //5.1
		addPacket(new CM_MAY_LOGIN_INTO_GAME(0x0170, State.AUTHED)); //5.1
		addPacket(new CM_ENTER_WORLD(0x00C2, State.AUTHED)); //5.1
		addPacket(new CM_CHARACTER_PASSKEY(0x01A8, State.AUTHED)); //5.1
		addPacket(new CM_SHOW_BLOCKLIST(0x0164, State.IN_GAME)); //5.1
		addPacket(new CM_L2AUTH_LOGIN_CHECK(0x015F, State.CONNECTED)); //5.1
		addPacket(new CM_INSTANCE_INFO(0x019A, State.IN_GAME)); //5.1
		addPacket(new CM_LEVEL_READY(0x00C3, State.IN_GAME)); //5.1
		addPacket(new CM_EMOTION(0x00E1, State.IN_GAME)); //5.1
		addPacket(new CM_MOVE(0x010A, State.IN_GAME)); //5.1
		addPacket(new CM_ABYSS_LANDING(0x0106, State.IN_GAME)); //5.1
		addPacket(new CM_FRIEND_ADD(0x0135, State.IN_GAME)); //5.1
		addPacket(new CM_PING_REQUEST(0x12D, State.IN_GAME)); //5.1
		addPacket(new CM_PLAYER_SEARCH(0x0165, State.IN_GAME)); //5.1
		addPacket(new CM_FRIEND_STATUS(0x0160, State.IN_GAME)); //5.1
		addPacket(new CM_TARGET_SELECT(0x00E5, State.IN_GAME)); //5.1
		addPacket(new CM_ABYSS_RANKING_PLAYERS(0x0186, State.IN_GAME)); //5.1
		addPacket(new CM_ABYSS_RANKING_LEGIONS(0x013C, State.IN_GAME)); //5.1
		addPacket(new CM_BLOCK_ADD(0x016C, State.IN_GAME)); //5.1
		addPacket(new CM_QUIT(0x00D9, State.AUTHED, State.IN_GAME)); //5.1
		addPacket(new CM_MAY_QUIT(0x00CE, State.AUTHED, State.IN_GAME)); //5.1
		addPacket(new CM_APPEARANCE(0x18F, State.IN_GAME)); //5.1
		addPacket(new CM_CHARACTER_EDIT(0x00CD, State.AUTHED)); //5.1
		addPacket(new CM_CREATE_CHARACTER(0x15D, State.AUTHED)); //5.1
		addPacket(new CM_CHECK_NICKNAME(0x018B, State.AUTHED)); //5.1
		addPacket(new CM_USE_ITEM(0x00EF, State.IN_GAME)); //5.1
		addPacket(new CM_S_REP_WEB_SESSIONKEY(0x0104, State.CONNECTED, State.AUTHED, State.IN_GAME)); //5.1
		addPacket(new CM_SHOW_FRIENDLIST(0x01AC, State.IN_GAME)); //5.1
		addPacket(new CM_MOVE_ITEM(0x0166, State.IN_GAME)); //5.1
		addPacket(new CM_SHOW_DIALOG(0x2FE, State.IN_GAME)); //5.1
		addPacket(new CM_VIEW_PLAYER_DETAILS(0x012E, State.IN_GAME)); //5.1
		addPacket(new CM_CHAT_MESSAGE_PUBLIC(0x00D1, State.IN_GAME)); //5.1
		addPacket(new CM_EQUIP_ITEM(0x00EC, State.IN_GAME)); //5.1
		addPacket(new CM_CASTSPELL(0x00FB, State.IN_GAME)); //5.1
		addPacket(new CM_SET_NOTE(0x02F0, State.IN_GAME)); //5.1
		addPacket(new CM_TITLE_SET(0x141, State.IN_GAME)); //5.1
		addPacket(new CM_BONUS_TITLE(0x1A3, State.IN_GAME)); //5.1
		addPacket(new CM_PLAY_MOVIE_END(0x12B, State.IN_GAME)); //5.1
		addPacket(new CM_ATTACK(0xFA, State.IN_GAME)); //5.1
		addPacket(new CM_TOGGLE_SKILL_DEACTIVATE(0xF8, State.IN_GAME)); //5.1
		addPacket(new CM_PRIVATE_STORE(0x013D, State.IN_GAME)); //5.1
		addPacket(new CM_PRIVATE_STORE_NAME(0x132, State.IN_GAME)); //5.1
		addPacket(new CM_MOTION(0x010D, State.IN_GAME)); //5.1
		addPacket(new CM_DELETE_ITEM(0x013E, State.IN_GAME)); //5.1
		addPacket(new CM_REVIVE(0x00CF, State.IN_GAME)); //5.1
		addPacket(new CM_USE_CHARGE_SKILL(0x01A0, State.IN_GAME)); //5.1
		addPacket(new CM_DELETE_QUEST(0x012A, State.IN_GAME)); //5.1
		addPacket(new CM_MACRO_CREATE(0x175, State.IN_GAME)); //5.1
		addPacket(new CM_GM_BOOKMARK(0x00E3, State.IN_GAME)); //5.1
		addPacket(new CM_CHECK_MAIL_SIZE(0x14F, State.IN_GAME)); //5.1
		addPacket(new CM_CHECK_MAIL_SIZE_2(0x19F, State.IN_GAME)); //5.1
		addPacket(new CM_CLOSE_DIALOG(0x02FF, State.IN_GAME)); //5.1
		addPacket(new CM_HOTSPOT_TELEPORT(0x01BE, State.IN_GAME)); //5.1
		addPacket(new CM_DIALOG_SELECT(0x02FC, State.IN_GAME)); //5.1
		// ==================[MAIL]==================
		addPacket(new CM_SEND_MAIL(0x014E, State.IN_GAME)); //5.1
		addPacket(new CM_DELETE_MAIL(0x0143, State.IN_GAME)); //5.1
		addPacket(new CM_READ_MAIL(0x014C, State.IN_GAME)); //5.1
		addPacket(new CM_GET_MAIL_ATTACHMENT(0x0142, State.IN_GAME)); //5.1
		addPacket(new CM_READ_EXPRESS_MAIL(0x0178, State.IN_GAME)); //5.1
		// ============================================
		// ==================[LEGION]==================
		addPacket(new CM_LEGION_WH_KINAH(0x116, State.IN_GAME)); //5.1
		addPacket(new CM_LEGION_SEND_EMBLEM(0x02F5, State.IN_GAME)); //5.1
		addPacket(new CM_LEGION_SEND_EMBLEM_INFO(0x00EA, State.IN_GAME)); //5.1
		addPacket(new CM_LEGION_UPLOAD_INFO(0x17A, State.IN_GAME)); //5.1
		addPacket(new CM_LEGION_UPLOAD_EMBLEM(0x17B, State.IN_GAME)); //5.1
		addPacket(new CM_LEGION(0x2F7, State.IN_GAME)); //5.1
		addPacket(new CM_LEGION_SEND_EMBLEM_INFO(0x02F5, State.IN_GAME)); //5.1
		addPacket(new CM_LEGION_TABS(0x2FD, State.IN_GAME)); //5.1
		addPacket(new CM_LEGION_MODIFY_EMBLEM(0x2F1, State.IN_GAME)); //5.1
		addPacket(new CM_LEGION_SEARCH(0x1C6, State.IN_GAME)); //5.1
		addPacket(new CM_LEGION_JOIN_REQUEST(0x1C7, State.IN_GAME)); //5.1
		addPacket(new CM_LEGION_JOIN_CANCEL(0x1C4, State.IN_GAME)); //5.1
		// ============================================
		// ==================[EXCHANGE]==================
		addPacket(new CM_EXCHANGE_ADD_KINAH(0x118, State.IN_GAME)); //5.1
		addPacket(new CM_EXCHANGE_REQUEST(0x0105, State.IN_GAME)); //5.1
		addPacket(new CM_EXCHANGE_ADD_ITEM(0x11A, State.IN_GAME)); //5.1
		addPacket(new CM_EXCHANGE_CANCEL(0x10F, State.IN_GAME)); //5.1
		addPacket(new CM_EXCHANGE_LOCK(0x119, State.IN_GAME)); //5.1
		addPacket(new CM_EXCHANGE_OK(0x10E, State.IN_GAME)); //5.1
		// ============================================
		// ==================[GROUP]==================
		addPacket(new CM_INVITE_TO_GROUP(0x013B, State.IN_GAME)); //5.1
		//addPacket(new CM_CHAT_GROUP_INFO(0x11B, State.IN_GAME));
		addPacket(new CM_FIND_GROUP(0x117, State.IN_GAME)); //5.1
		addPacket(new CM_GROUP_DISTRIBUTION(0x136, State.IN_GAME)); //5.1
		addPacket(new CM_GROUP_LOOT(0x172, State.IN_GAME)); //5.1
		//addPacket(new CM_GROUP_DATA_EXCHANGE(0x1C0, State.IN_GAME));
		addPacket(new CM_AUTO_GROUP(0x182, State.IN_GAME)); //5.1
		addPacket(new CM_DISTRIBUTION_SETTINGS(0x173, State.IN_GAME)); //5.1
		addPacket(new CM_PLAYER_STATUS_INFO(0x13A, State.IN_GAME)); //5.1
		addPacket(new CM_SHOW_BRAND(0x17F, State.IN_GAME)); //5.1
		// ============================================
		// ==================[PET]=====================
		addPacket(new CM_PET_EMOTE(0x00DF, State.IN_GAME)); //5.1
		addPacket(new CM_PET(0x00DC, State.IN_GAME)); //5.1
		// ============================================
		// ==================[SUMMON]==================
		addPacket(new CM_SUMMON_EMOTION(0x0180, State.IN_GAME)); //5.1
		addPacket(new CM_SUMMON_ATTACK(0x0181, State.IN_GAME)); //5.1
		addPacket(new CM_SUMMON_CASTSPELL(0x197, State.IN_GAME)); //5.1
		addPacket(new CM_SUMMON_COMMAND(0x0133, State.IN_GAME)); //5.1
		addPacket(new CM_SUMMON_MOVE(0x0183, State.IN_GAME)); //5.1
		// ============================================
		addPacket(new CM_DUEL_REQUEST(0x0148, State.IN_GAME)); //5.1
		addPacket(new CM_BLOCK_DEL(0x16D, State.IN_GAME)); //5.1
		addPacket(new CM_CHAT_MESSAGE_WHISPER(0x00E6, State.IN_GAME)); //5.1
		addPacket(new CM_MACRO_DELETE(0x018A, State.IN_GAME)); //5.1
		addPacket(new CM_QUESTION_RESPONSE(0x0108, State.IN_GAME)); //5.1
		addPacket(new CM_FRIEND_DEL(0x014A, State.IN_GAME)); //5.1
		addPacket(new CM_ENCHANMENT_STONES(0x0100, State.IN_GAME)); //5.1
		addPacket(new CM_ENCHANTMENT_EXTRACTION(0x1CE, State.IN_GAME)); //5.1
		// ==================[BROKER]==================
		addPacket(new CM_BROKER_ADD_ITEM(0x13F, State.IN_GAME)); //5.1
		addPacket(new CM_BROKER_SETTLE_ACCOUNT(0x158, State.IN_GAME)); //5.1
		addPacket(new CM_BROKER_SETTLE_LIST(0x147, State.IN_GAME)); //5.1
		addPacket(new CM_BROKER_LIST(0x0131, State.IN_GAME)); //5.1
		addPacket(new CM_BUY_BROKER_ITEM(0x144, State.IN_GAME)); //5.1
		addPacket(new CM_REGISTER_BROKER_ITEM(0x145, State.IN_GAME)); //5.1
		addPacket(new CM_BROKER_SEARCH(0x0146, State.IN_GAME)); //5.1
		addPacket(new CM_BROKER_REGISTERED(0x147, State.IN_GAME)); //5.1
		addPacket(new CM_BROKER_CANCEL_REGISTERED(0x15A, State.IN_GAME)); //5.1
		addPacket(new CM_BROKER_COLLECT_SOLD_ITEMS(0x15B, State.IN_GAME)); //5.1
		// ============================================
		// ==================[HOUSING]=================
		addPacket(new CM_HOUSE_KICK(0x102, State.IN_GAME)); //5.1
		addPacket(new CM_HOUSE_SETTINGS(0x103, State.IN_GAME)); //5.1
		addPacket(new CM_HOUSE_DECORATE(0x101, State.IN_GAME)); //5.1
		addPacket(new CM_GET_HOUSE_BIDS(0x190, State.IN_GAME)); //5.1
		addPacket(new CM_REGISTER_HOUSE(0x191, State.IN_GAME)); //5.1
		addPacket(new CM_HOUSE_TELEPORT_BACK(0x125, State.IN_GAME));
		addPacket(new CM_HOUSE_TELEPORT(0x1A4, State.IN_GAME)); //5.1
		addPacket(new CM_HOUSE_PAY_RENT(0x1A5, State.IN_GAME)); //5.1
		addPacket(new CM_USE_HOUSE_OBJECT(0x1BA, State.IN_GAME)); //5.1
		addPacket(new CM_HOUSE_OPEN_DOOR(0x1B8, State.IN_GAME)); //5.1
		addPacket(new CM_HOUSE_EDIT(0x128, State.IN_GAME)); //5.1
		addPacket(new CM_PLACE_BID(0x1A7, State.IN_GAME)); //5.1
		addPacket(new CM_HOUSE_SCRIPT(0xE4, State.IN_GAME)); //5.1
		//addPacket(new CM_BUTLER_SALUTE(0x012E, State.IN_GAME)); //To Do 5.1
		// ============================================
		// ==================[TELEPORT]================
		addPacket(new CM_TELEPORT_SELECT(0x15E, State.IN_GAME)); //5.1
		addPacket(new CM_TELEPORT_DONE(0xD5, State.IN_GAME)); //5.1
		addPacket(new CM_MOVE_IN_AIR(0x10B, State.IN_GAME)); //5.1
		// ============================================
		addPacket(new CM_FUSION_WEAPONS(0x0194, State.IN_GAME)); //5.1
		addPacket(new CM_BREAK_WEAPONS(0x0195, State.IN_GAME)); //5.1
		addPacket(new CM_TUNE(0x01A1, State.IN_GAME)); //5.1
		addPacket(new CM_TUNE_RESULT(0x1A8, State.IN_GAME)); //To Do 5.1
		addPacket(new CM_CUBE_EXPAND(0x01B1, State.IN_GAME)); //5.1
		addPacket(new CM_CUSTOM_SETTINGS(0xD6, State.IN_GAME)); //5.1
		addPacket(new CM_MEGAPHONE_MESSAGE(0x1B7, State.IN_GAME)); //5.1
		addPacket(new CM_CHANGE_CHANNEL(0x176, State.IN_GAME)); //5.1
		addPacket(new CM_REPLACE_ITEM(0x188, State.IN_GAME));
		addPacket(new CM_SPLIT_ITEM(0x167, State.IN_GAME)); //5.1
		addPacket(new CM_BLOCK_SET_REASON(0x189, State.IN_GAME)); //5.1
		addPacket(new CM_CHARGE_ITEM(0x114, State.IN_GAME)); //5.1
		addPacket(new CM_ITEM_REMODEL(0x110, State.IN_GAME)); //5.1
		addPacket(new CM_OPEN_STATICDOOR(0xDD, State.IN_GAME)); //5.1
		addPacket(new CM_RECIPE_DELETE(0x113, State.IN_GAME)); //5.1
		addPacket(new CM_OBJECT_SEARCH(0xC1, State.IN_GAME)); //5.1
		addPacket(new CM_STOP_TRAINING(0x11E, State.IN_GAME)); //5.1
		addPacket(new CM_INSTANCE_LEAVE(0x2F4, State.IN_GAME)); //5.1
		addPacket(new CM_MARK_FRIENDLIST(0x134, State.IN_GAME)); //5.1
		addPacket(new CM_CHALLENGE_LIST(0x1A2, State.IN_GAME)); //5.1
		addPacket(new CM_RELEASE_OBJECT(0x1BB, State.IN_GAME)); //5.1
		addPacket(new CM_CLIENT_COMMAND_ROLL(0x121, State.IN_GAME)); //5.1
		addPacket(new CM_BUY_TRADE_IN_TRADE(0x112, State.IN_GAME)); //5.1
		addPacket(new CM_BUY_ITEM(0x109, State.IN_GAME)); //5.1
		addPacket(new CM_WINDSTREAM(0x10C, State.IN_GAME)); //5.1
		addPacket(new CM_CRAFT(0x157, State.IN_GAME)); //5.1
		addPacket(new CM_GATHER(0xE9, State.IN_GAME)); //5.1
		addPacket(new CM_REMOVE_ALTERED_STATE(0xF9, State.IN_GAME)); //5.1
		addPacket(new CM_LOOT_ITEM(0x151, State.IN_GAME)); //5.1
		addPacket(new CM_START_LOOT(0x150, State.IN_GAME)); //5.1
		addPacket(new CM_UPGRADE_ARCADE(0x1BC, State.IN_GAME)); //5.1
		addPacket(new CM_ATREIAN_PASSPORT(0x1B2, State.IN_GAME)); //5.1
		addPacket(new CM_CHAT_AUTH(0x174, State.IN_GAME)); //5.1
		addPacket(new CM_UI_SETTINGS(0xC0, State.IN_GAME)); //5.1
		addPacket(new CM_RESTORE_CHARACTER(0x153, State.AUTHED)); //5.1
		addPacket(new CM_RECONNECT_AUTH(0x17D, State.AUTHED)); //5.1
		addPacket(new CM_DELETE_CHARACTER(0x152, State.AUTHED)); //5.1
		addPacket(new CM_QUEST_SHARE(0x162, State.IN_GAME)); //5.1
		addPacket(new CM_SELECT_ITEM(0x1B6, State.IN_GAME)); //5.1
		addPacket(new CM_REPORT_PLAYER(0x185, State.IN_GAME)); //5.1
		addPacket(new CM_INTRUDER_SCAN(0x018E, State.IN_GAME)); //5.1
		addPacket(new CM_UPDATE_CP(0x012C, State.IN_GAME)); //5.1
		addPacket(new CM_COMPOSITE_STONES(0x1AA, State.IN_GAME)); //5.1
		addPacket(new CM_QUESTIONNAIRE(0x016B, State.IN_GAME)); //5.1
		addPacket(new CM_PURIFICATION_ITEM(0x01BD, State.IN_GAME)); //5.1
		addPacket(new CM_STONESPEAR_SIEGE(0xFC, State.IN_GAME)); //5.1
		addPacket(new CM_SHOW_MAP(0x00E2, State.IN_GAME)); //To Do 5.1 maybe removed by ncsoft
		addPacket(new CM_LUNA_SHOP(0x01CC, State.IN_GAME)); //5.1
		addPacket(new CM_EVENT_DICE(0x01C3, State.IN_GAME)); //5.1
		addPacket(new CM_SELL_TERMINATED_ITEMS(0x01CD, State.IN_GAME)); //5.1
		addPacket(new CM_AETHERFORGING(0x01C1, State.IN_GAME)); //5.1
		addPacket(new CM_COALESCENCE(0x01C0, State.IN_GAME)); //5.1
		addPacket(new CM_COALESCENCE_WINDOW(0x01E7, State.IN_GAME)); //5.1
		
		//addPacket(new CM_SUBZONE_CHANGE(0x0162, State.IN_GAME)); //To Do 5.1 maybe removed by ncsoft
		//addPacket(new CM_A_STATION(0x1BB, State.IN_GAME)); //To Do 5.1 we do not implement fasttrack yet
		//addPacket(new CM_A_STATION_CHECK(0x1BA, State.IN_GAME)); //To Do 5.1 we do not implement fasttrack yet
		//addPacket(new CM_CHAT_PLAYER_INFO(0xD9, State.IN_GAME)); //To Do 5.1
		//addPacket(new CM_UNPACK_ITEM(0x1B2, State.IN_GAME)); //To Do 5.1
	}
	
	public AionPacketHandler getPacketHandler() {
		return handler;
	}
	
	private void addPacket(AionClientPacket prototype) {
		handler.addPacketPrototype(prototype);
	}
	
	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {
		protected static final AionPacketHandlerFactory instance = new AionPacketHandlerFactory();
	}
}