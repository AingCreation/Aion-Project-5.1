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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.item.ItemUpgradeService;

/**
 * @author Ranastic (Encom)
 */

public class CM_PURIFICATION_ITEM extends AionClientPacket
{
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(CM_PURIFICATION_ITEM.class);
	int playerObjectId;
	int upgradedItemObjectId;
	int resultItemId;
	int requireItemObjectId1;
	int requireItemObjectId2;
	int requireItemObjectId3;
	int requireItemObjectId4;
	int requireItemObjectId5;
	Item baseItem;
	
	public CM_PURIFICATION_ITEM(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		Player player = getConnection().getActivePlayer();
		playerObjectId = readD();
		upgradedItemObjectId = readD();
		resultItemId = readD();
		requireItemObjectId1 = readD();
		requireItemObjectId2 = readD();
		requireItemObjectId3 = readD();
		requireItemObjectId4 = readD();
		requireItemObjectId5 = readD();
		Storage inventory = player.getInventory();
		baseItem = inventory.getItemByObjId(upgradedItemObjectId);
	}
	
	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		if (player == null) {
			return;
		} if (!ItemUpgradeService.checkItemUpgrade(player, baseItem, resultItemId)) {
			return;
		}
		Item resultItem = ItemService.newItem(resultItemId, 1, null, 0, 0, 0);
		ItemService.makeUpgradeItem(player, baseItem, resultItem);
		if (!ItemUpgradeService.decreaseMaterial(player, baseItem, resultItemId)) {
			return;
		}
		ItemService.addItem(player, resultItem);
	}
}