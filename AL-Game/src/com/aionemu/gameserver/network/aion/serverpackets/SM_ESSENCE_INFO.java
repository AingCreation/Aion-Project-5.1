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
package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.services.dreamergames.model.PlayerCpEntry;
import com.aionemu.gameserver.services.dreamergames.templates.PlayerCpTemplate;

/**
 * @author Ranastic (Encom)
 */

public class SM_ESSENCE_INFO extends AionServerPacket {
	boolean isNew = false;
	Player player;
	private PlayerCpEntry[] cpList;
	
	public SM_ESSENCE_INFO(Player player, PlayerCpEntry[] getAllCps) {
	    this.cpList = player.getCpList().getAllCps();
	    this.player = player;
	}
	
	public SM_ESSENCE_INFO(Player player, PlayerCpEntry cpListEntry, boolean isNew) {
		this.cpList = new PlayerCpEntry[] { cpListEntry };
		this.isNew = isNew;
		this.player = player;
	}
	
	protected void writeImpl(AionConnection con) {
		int size = 0;
		if (this.cpList != null) {
			size = this.cpList.length;
		}
		int playerlevel = this.player.getLevel() + 1;
		if (playerlevel > 75) {
			
		}
		playerlevel = 75;
	    
		PlayerCpTemplate[] playerLevel = DataManager.PLAYER_CP_TABLE.getTemplatesForGroup(playerlevel);
		int NextCp = playerLevel[0].getCpPoint();
		
		writeD(this.player.getCommonData().getMaxCp());
		writeD(NextCp - 1);
		writeH(size);
		if (this.cpList != null) {
			for (PlayerCpEntry entry : this.cpList) {
				writeD(entry.getSlotId());
				writeH(entry.getCpPoint());
			}
		}
	}
}