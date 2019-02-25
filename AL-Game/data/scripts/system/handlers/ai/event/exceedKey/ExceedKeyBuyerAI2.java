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

@AIName("exceed_key_buyer")
public class ExceedKeyBuyerAI2 extends NpcAI2
{
	@Override
    protected void handleDialogStart(Player player) {
        //손상된 돌파석을 가지고 계시다면 저에게 가져 오십시오.
		//상당한 가격으로 매입하겠습니다.
		if (player.getInventory().getFirstItemByItemId(182007137) != null) { //The Stolen Letter Card D.
            PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 10));
        } else {
            PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
			PacketSendUtility.broadcastPacket(player, new SM_MESSAGE(player,
			"kamu tidak memiliki item [item: 182007137] untuk di tukar", ChatType.BRIGHT_YELLOW_CENTER), true);
        }
    }
	
	@Override
    public boolean onDialogSelect(final Player player, int dialogId, int questId, int extendedRewardIndex) {
		if (dialogId == 10000 && player.getInventory().decreaseByItemId(182007137, 1)) { //The Stolen Letter Card D.
		    switch (getNpcId()) {
		        case 805716: //Arados.
				case 805717: //Pochuren.
				    //소유하고 있는 손상된 돌파석이 있다면 저에게 가져오십시오.
					//섭섭치 않은 가격에 매입하겠습니다.
					switch (Rnd.get(1, 41)) {
					    case 1:
						    ItemService.addItem(player, 167010282, 5); //Manastone: Agility +5
						break;
						case 2:
						    ItemService.addItem(player, 167010281, 5); //Manastone: +5
						break;
						case 3:
						    ItemService.addItem(player, 167010284, 5); //Manastone: +5
						break;
						case 4:
						    ItemService.addItem(player, 167010280, 5); //Manastone: +5
						break;
						case 5:
						    ItemService.addItem(player, 167010283, 5); //Manastone: +5
						break;
						case 6:
						    ItemService.addItem(player, 167010285, 5); //Manastone: +5
						break;
						case 7:
						    ItemService.addItem(player, 167010288, 5); //Manastone: +6
						break;
						case 8:
						    ItemService.addItem(player, 167010287, 5); //Manastone: +6
						break;
						case 9:
						    ItemService.addItem(player, 167010290, 5); //Manastone: +6
						break;
						case 10:
						    ItemService.addItem(player, 167010286, 5); //Manastone: +6
						break;
						case 11:
						    ItemService.addItem(player, 167010289, 5); //Manastone: +6
						break;
						case 12:
						    ItemService.addItem(player, 167010291, 5); //Manastone: +6
						break;
						case 13:
						    ItemService.addItem(player, 182400001, 500000000); //kinah 5m
						break;
						case 14:
						    ItemService.addItem(player, 166030005, 10); //Tempering Solution
						break;
						case 15:
						    ItemService.addItem(player, 182400001, 200000000); //kinah 2m
						break;
						case 16:
						    ItemService.addItem(player, 182400001, 300000000); //kinah 3m
						break;
						case 17:
						    ItemService.addItem(player, 182400001, 400000000); //kinah 4m
						break;
						case 18:
						    ItemService.addItem(player, 167010294, 5); //ms +7
						break;
						case 19:
						    ItemService.addItem(player, 167010293, 5); //ms +7
						break;
						case 20:
						    ItemService.addItem(player, 167010296, 5); //ms +7
						break;
						case 21:
						    ItemService.addItem(player, 167010292, 5); //ms +7
						break;
						case 22:
						    ItemService.addItem(player, 167010295, 5); //ms +7
						break;
						case 23:
						    ItemService.addItem(player, 167010297, 5); //ms +7
						break;
						case 24:
						    ItemService.addItem(player, 167010300, 5); //ms +8
						break;
						case 25:
						    ItemService.addItem(player, 167010299, 5); //ms +8
						break;
						case 26:
						    ItemService.addItem(player, 167010302, 5); //ms +8
						break;
						case 27:
						    ItemService.addItem(player, 167010298, 5); //ms +8
						break;
						case 28:
						    ItemService.addItem(player, 167010301, 5); //ms +8
						break;
						case 29:
						    ItemService.addItem(player, 167010303, 5); //ms +8
						break;
						case 30:
						    ItemService.addItem(player, 167010306, 5); //ms +9
						break;
						case 31:
						    ItemService.addItem(player, 167010305, 5); //ms +8
						break;
						case 32:
						    ItemService.addItem(player, 167010308, 5); //ms +8
						break;
						case 33:
						    ItemService.addItem(player, 167010304, 5); //ms +8
						break;
						case 34:
						    ItemService.addItem(player, 167010307, 5); //ms +8
						break;
						case 35:
						    ItemService.addItem(player, 167010309, 5); //ms +8
						break;
						case 36:
						    ItemService.addItem(player, 167010312, 5); //ms +10
						break;
						case 37:
						    ItemService.addItem(player, 167010311, 5); //ms +8
						break;
						case 38:
						    ItemService.addItem(player, 167010314, 5); //ms +8
						break;
						case 39:
						    ItemService.addItem(player, 167010310, 5); //ms +8
						break;
						case 40:
						    ItemService.addItem(player, 167010313, 5); //ms +8
						break;
						case 41:
						    ItemService.addItem(player, 167010315, 5); //ms +8
						break;
					}
				break;
			}
		}
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
		return true;
	}
}