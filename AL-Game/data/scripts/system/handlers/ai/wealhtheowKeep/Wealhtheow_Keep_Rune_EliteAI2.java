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
package ai.wealhtheowKeep;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.*;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.ingameshop.InGameShopEn;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.model.gameobjects.*;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.*;
import com.aionemu.gameserver.world.*;
import com.aionemu.gameserver.world.knownlist.Visitor;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("rune_elite")
public class Wealhtheow_Keep_Rune_EliteAI2 extends AggressiveNpcAI2
{
	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
	}
	
	@Override
    protected void handleSpawned() {
        super.handleSpawned();
		announceRuneElite();
	}
	
	@Override
	protected void handleDied() {
		switch (getNpcId()) {
			//Wealhtheow's Keep.
			case 251825:
			case 251830:
				treasureChest();
				doRewardBoss();
				ThreadPoolManager.getInstance().schedule(new Runnable() {
			        @Override
			        public void run() {
						spawn(701481, 780.46515f, 288.62924f, 143.18782f, (byte) 45);
                        spawn(701481, 787.1314f, 288.72644f, 143.20233f, (byte) 30);
                        spawn(701481, 793.9525f, 289.05054f, 143.18248f, (byte) 15);
			        }
		        }, 10000);
			break;
		}
		super.handleDied();
	}
	
	private void treasureChest() {
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				//A treasure chest has appeared.
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_IDAbRe_Core_NmdC_BoxSpawn);
			}
		});
	}
	
	public void doRewardBoss() {
        World.getInstance().getWorldMap(WorldMapType.KALDOR.getId()).getMainWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(Player player) {
                if (player.getRace() == Race.ELYOS) {
                    int qt = 10;
                    AbyssPointsService.addGp(player, 1000);
					ItemService.addItem(player, 185000199, 1);
					ItemService.addItem(player, 166030005, 50);
                    InGameShopEn.getInstance().addToll(player, qt);
                    PacketSendUtility.sendBrightYellowMessage(player, "You obtained " + qt + " Web Point.");
					} else {
                    int qt = 10;
                    AbyssPointsService.addGp(player, 1000);
					ItemService.addItem(player, 185000199, 1);
					ItemService.addItem(player, 166030005, 50);
                    InGameShopEn.getInstance().addToll(player, qt);
                    PacketSendUtility.sendBrightYellowMessage(player, "You obtained " + qt + " Web Point.");
                }
            }
        });
    }
	
	private void announceRuneElite() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF5_Fortress_RuneElite);
			}
		});
	}
}