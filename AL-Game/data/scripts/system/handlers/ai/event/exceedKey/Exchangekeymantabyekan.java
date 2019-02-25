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
package ai.event.exceedKey;

import com.aionemu.commons.utils.Rnd;

import com.aionemu.gameserver.ai2.*;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.*;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("exchange_mantab_yekan")
public class Exchangekeymantabyekan extends NpcAI2
{
	@Override
    protected void handleDialogStart(Player player) {
        //손상된 돌파석을 가지고 계시다면 저에게 가져 오십시오.
		//상당한 가격으로 매입하겠습니다.
		if (player.getInventory().getFirstItemByItemId(185000199) != null) { //<[Event] Tournament Key>
            PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 10));
        } else {
            PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
			PacketSendUtility.broadcastPacket(player, new SM_MESSAGE(player,
			"kamu tidak memiliki item [item: 185000199] 5pcs untuk di tukar", ChatType.BRIGHT_YELLOW_CENTER), true);
        }
    }
	
	@Override
    public boolean onDialogSelect(final Player player, int dialogId, int questId, int extendedRewardIndex) {
		if (dialogId == 10000 && player.getInventory().decreaseByItemId(185000199, 5)) { //<[Event] Tournament Key>
		    switch (getNpcId()) {
		        case 833501: //GM.
				    //소유하고 있는 손상된 돌파석이 있다면 저에게 가져오십시오.
					//섭섭치 않은 가격에 매입하겠습니다.
					switch (Rnd.get(1, 46)) {
					    case 1:
						    ItemService.addItem(player, 110551421, 1); //Boundless Chain Armor of Hope
						break;
						case 2:
						    ItemService.addItem(player, 114502008, 1); //Boundless Chain Armor of Hope
						break;
						case 3:
						    ItemService.addItem(player, 110551422, 1); //Boundless Chain Armor of Hope
						break;
						case 4:
						    ItemService.addItem(player, 110551423, 1); //Boundless Chain Armor of Hope
						break;
						case 5:
						    ItemService.addItem(player, 114502010, 1); //Boundless Chain Armor of Hope
						break;
						case 6:
						    ItemService.addItem(player, 111501992, 1); //Boundless Chain Armor of Hope
						break;
						case 7:
						    ItemService.addItem(player, 114502009, 1); //Manastone: +6
						break;
						case 8:
						    ItemService.addItem(player, 113502002, 1); //Manastone: +6
						break;
						case 9:
						    ItemService.addItem(player, 111501990, 1); //Manastone: +6
						break;
						case 10:
						    ItemService.addItem(player, 111501991, 1); //Manastone: +6
						break;
						case 11:
						    ItemService.addItem(player, 113502000, 1); //Manastone: +6
						break;
						case 12:
						    ItemService.addItem(player, 113502001, 1); //Manastone: +6
						break;
						case 13:
						    ItemService.addItem(player, 112501926, 1); //kinah 5m
						break;
						case 14:
						    ItemService.addItem(player, 112501927, 1); //Tempering Solution
						break;
						case 15:
						    ItemService.addItem(player, 112501928, 1); //kinah 2m
						break;
						case 16:
						    ItemService.addItem(player, 110102081, 1); //kinah 3m
						break;
						case 17:
						    ItemService.addItem(player, 114101920, 1); //kinah 4m
						break;
						case 18:
						    ItemService.addItem(player, 111101878, 1); //ms +7
						break;
						case 19:
						    ItemService.addItem(player, 111101879, 1); //ms +7
						break;
						case 20:
						    ItemService.addItem(player, 110102080, 1); //ms +7
						break;
						case 21:
						    ItemService.addItem(player, 113101886, 1); //ms +7
						break;
						case 22:
						    ItemService.addItem(player, 113101885, 1); //ms +7
						break;
						case 23:
						    ItemService.addItem(player, 114101919, 1); //ms +7
						break;
						case 24:
						    ItemService.addItem(player, 112101822, 1); //ms +8
						break;
						case 25:
						    ItemService.addItem(player, 112101823, 1); //ms +8
						break;
						case 26:
						    ItemService.addItem(player, 110302057, 1); //ms +8
						break;
						case 27:
						    ItemService.addItem(player, 114302064, 1); //ms +8
						break;
						case 28:
						    ItemService.addItem(player, 111302001, 1); //ms +8
						break;
						case 29:
						    ItemService.addItem(player, 111302002, 1); //ms +8
						break;
						case 30:
						    ItemService.addItem(player, 113302027, 1); //ms +9
						break;
						case 31:
						    ItemService.addItem(player, 113302026, 1); //ms +8
						break;
						case 32:
						    ItemService.addItem(player, 114302063, 1); //ms +8
						break;
						case 33:
						    ItemService.addItem(player, 112301938, 1); //ms +8
						break;
						case 34:
						    ItemService.addItem(player, 112301939, 1); //ms +8
						break;
						case 35:
						    ItemService.addItem(player, 110302056, 1); //ms +8
						break;
						case 36:
						    ItemService.addItem(player, 114601797, 1); //ms +10
						break;
						case 37:
						    ItemService.addItem(player, 110601845, 1); //ms +8
						break;
						case 38:
						    ItemService.addItem(player, 110601844, 1); //ms +8
						break;
						case 39:
						    ItemService.addItem(player, 114601798, 1); //ms +8
						break;
						case 40:
						    ItemService.addItem(player, 113601791, 1); //ms +8
						break;
						case 41:
						    ItemService.addItem(player, 111601814, 1); //ms +8
						break;
						case 42:
						    ItemService.addItem(player, 111601815, 1); //ms +8
						break;
						case 43:
						    ItemService.addItem(player, 112601795, 1); //ms +8
						break;
						case 44:
						    ItemService.addItem(player, 112601796, 1); //ms +8
						break;
						case 45:
						    ItemService.addItem(player, 115002031, 1); //ms +8
						break;
						case 46:
						    ItemService.addItem(player, 115002030, 1); //ms +8
						break;
					}
				break;
			}
		}
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
		return true;
	}
}