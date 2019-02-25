package com.aionemu.gameserver.network.aion.clientpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.main.LoggingConfig;
import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.FriendList;
import com.aionemu.gameserver.model.wedding.WeddingService;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
import com.aionemu.gameserver.services.NameRestrictionService;
import com.aionemu.gameserver.utils.ChatUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.world.World;

public class CM_CHAT_MESSAGE_WHISPER extends AionClientPacket
{
	private static final Logger log = LoggerFactory.getLogger("CHAT_LOG");
	private String name;
	private String message;
	
	public CM_CHAT_MESSAGE_WHISPER(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);

	}
	
	@Override
	protected void readImpl() {
		name = readS();
		message = readS();
	}
	
	@Override
	protected void runImpl() {
		name = ChatUtil.getRealAdminName(name);
        if (name.contains("\ue020")) {
            name = WeddingService.getRealWeddingsName(name);
        }
		//name = ChatUtil.getRealAdminName(name);
		String formatname = Util.convertName(name);
		Player sender = getConnection().getActivePlayer();
		Player receiver = World.getInstance().findPlayer(formatname);
		if (LoggingConfig.LOG_CHAT)
			log.info(String.format("[MESSAGE] [%s] Whisper To: %s, Message: %s", sender.getName(), formatname, message));
		if (receiver == null) {
			//%0 is not playing the game.
			sendPacket(SM_SYSTEM_MESSAGE.STR_NO_SUCH_USER(formatname));
		} else if (receiver.getFriendList().getStatus() == FriendList.Status.OFFLINE && sender.getAccessLevel() < AdminConfig.GM_LEVEL) {
            //%0 is not playing the game.
			sendPacket(SM_SYSTEM_MESSAGE.STR_NO_SUCH_USER(formatname));
        } else if (!receiver.isWispable()) {
            //%0 is currently not accepting any Whispers.
			sendPacket(SM_SYSTEM_MESSAGE.STR_WHISPER_REFUSE(formatname));
        } else if (sender.getLevel() < CustomConfig.LEVEL_TO_WHISPER) {
            //Characters under level 10 cannot send whispers.
			sendPacket(SM_SYSTEM_MESSAGE.STR_CANT_WHISPER_LEVEL(String.valueOf(CustomConfig.LEVEL_TO_WHISPER)));
        } else if (receiver.getBlockList().contains(sender.getObjectId())) {
            //%0 has blocked you.
			sendPacket(SM_SYSTEM_MESSAGE.STR_YOU_EXCLUDED(receiver.getName()));
        } else if ((!CustomConfig.SPEAKING_BETWEEN_FACTIONS)
            && (sender.getRace().getRaceId() != receiver.getRace().getRaceId())
            && (sender.getAccessLevel() < AdminConfig.GM_LEVEL) && (receiver.getAccessLevel() < AdminConfig.GM_LEVEL)) {
            //%0 is not playing the game.
			sendPacket(SM_SYSTEM_MESSAGE.STR_NO_SUCH_USER(formatname));
        } else {
            if (RestrictionsManager.canChat(sender)) {
                PacketSendUtility.sendPacket(receiver, new SM_MESSAGE(sender, NameRestrictionService.filterMessage(message), ChatType.WHISPER));
			}
        }
	}
}