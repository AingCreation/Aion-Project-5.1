/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 *
 *  Aion-Lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Aion-Lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details. *
 *  You should have received a copy of the GNU General Public License
 *  along with Aion-Lightning.
 *  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aionemu.gameserver.network.aion.serverpackets;

import java.util.List;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.decomposable.SelectItem;
import com.aionemu.gameserver.model.templates.decomposable.SelectItems;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author Alcapwnd
 */
public class SM_SELECT_ITEM_LIST extends AionServerPacket {

	private int uniqueItemId;
	private List<SelectItem> selsetitems;

	public SM_SELECT_ITEM_LIST(Player player, List<SelectItem> selsetitem, int uniqueItemId) {
		this.uniqueItemId = uniqueItemId;
		this.selsetitems = selsetitem;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeD(uniqueItemId);
		writeD(0x00);
		writeC(selsetitems.size());
		for (int slotCount = 0; slotCount < selsetitems.size(); slotCount++) {
			writeC(slotCount);
			SelectItem rt = selsetitems.get(slotCount);
			ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(rt.getSelectItemId());
			writeD(rt.getSelectItemId());
			writeD(rt.getCount());
			writeC(itemTemplate.getOptionSlotBonus() > 0 ? 255 : 0);
			if (itemTemplate.isArmor() || itemTemplate.isWeapon()) {
				writeH(-1);
			} else {
				writeH(0);
			} if (itemTemplate.isCloth() || itemTemplate.getOptionSlotBonus() > 0) {
				writeC(1);
			} else {
				writeC(0);
			}
		}
	}
}
