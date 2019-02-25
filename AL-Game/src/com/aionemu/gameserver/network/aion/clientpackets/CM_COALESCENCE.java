package com.aionemu.gameserver.network.aion.clientpackets;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.item.CoalescenceService;

/**
 * @author Ranastic
 */
public class CM_COALESCENCE extends AionClientPacket
{
	private int ItemSize;
	private int upgradedItemObjectId;
	private int ItemsObjId;
	private List<Integer> ItemsList = new ArrayList<Integer>();

	public CM_COALESCENCE(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		upgradedItemObjectId = readD();
		ItemSize = readH();
		for (int i = 0; i < ItemSize; i++) {
			ItemsObjId = readD();
			ItemsList.add(ItemsObjId);
		}
	}

	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		if (player != null) {
			CoalescenceService.startMagicMorph(player, upgradedItemObjectId, ItemsList);
		} else {}
	}
}
