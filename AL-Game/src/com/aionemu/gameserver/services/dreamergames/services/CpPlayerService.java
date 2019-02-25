package com.aionemu.gameserver.services.dreamergames.services;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ESSENCE_INFO;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class CpPlayerService {
	public static void onPlayerLogin(Player player) {
		player.getCommonData().setMaxCp(player.getLevel());
	    PacketSendUtility.sendPacket(player, new SM_ESSENCE_INFO(player, player.getCpList().getAllCps()));
	}
	
	public static void onPlayerLogout(Player player) {
		player.getCommonData().setMaxCp(player.getLevel());
	    PacketSendUtility.sendPacket(player, new SM_ESSENCE_INFO(player, player.getCpList().getAllCps()));
	}
}