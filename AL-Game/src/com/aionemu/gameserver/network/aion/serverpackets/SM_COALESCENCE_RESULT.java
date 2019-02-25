package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author Ranastic
 */
public class SM_COALESCENCE_RESULT extends AionServerPacket
{
	private int itemObjId;
	
	public SM_COALESCENCE_RESULT(int itemObjId) {
		this.itemObjId = itemObjId;
	}

	@Override
	protected void writeImpl(AionConnection client) {
		writeD(itemObjId);
		writeD(0x00);
	    writeD(0x00);
	    writeD(0x00);
	    writeD(0x00);
	    writeD(0x00);
	}
}
