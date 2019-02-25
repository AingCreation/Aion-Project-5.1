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
package com.aionemu.gameserver.network.aion.clientpackets;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.craft.AetherForging;
import com.aionemu.gameserver.services.craft.CraftService;

/**
 * @author Ranastic
 */

public class CM_AETHERFORGING extends AionClientPacket
{
	List<Integer> itemList = new ArrayList<Integer>();
	private int action;
	@SuppressWarnings("unused")
	private int targetTemplateId;
	private int recipeId;
	@SuppressWarnings("unused")
	private int targetObjId;
	private int materialsCount;
	private int craftType;
	
	public CM_AETHERFORGING(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		action = readC();
		switch (action) {
			case 0: // Cancel MagicCraft
				targetTemplateId = readD();
				recipeId = readD();
				targetObjId = readD();
				materialsCount = readH();
				craftType = readC();
				break;
			case 1: // Start MagicCraft
				if (this.itemList != null) {
					this.itemList.clear();
				}
				targetTemplateId = readD(); // TODO
				recipeId = readD();
				targetObjId = readD(); // TODO
				materialsCount = readH();
				craftType = readC(); // TODO
				for (int i = 0; i < materialsCount; i++) {
					this.itemList.add(readD()); // materialId
					readQ(); // materialCount
				}
		}
	}
	
	@Override
	protected void runImpl() {
		final Player player = getConnection().getActivePlayer();

		if (player == null || !player.isSpawned()) {
			return;
		}
		if (player.getController().isInShutdownProgress()) {
			return;
		}

		switch (action) {
			case 0: { // cancel
				AetherForging.sendCancelForgingCraft(player); // TODO (NullPointer)
				break;
			}
			case 1: { // start
				AetherForging.startForgingCraft(player, recipeId, craftType);
				break;
			}
		}
	}
}