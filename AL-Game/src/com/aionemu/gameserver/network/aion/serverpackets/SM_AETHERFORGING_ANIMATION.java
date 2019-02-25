package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author Ranastic
 */
public class SM_AETHERFORGING_ANIMATION extends AionServerPacket
{
	private int senderObjectId;
	private int action;
	
	public SM_AETHERFORGING_ANIMATION(int senderObjectId, int action) {
		this.senderObjectId = senderObjectId;
		this.action = action;
	}
	
	@Override
	protected void writeImpl(AionConnection client) {
		writeD(senderObjectId);
		writeC(action);
	}
}
