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
package instance;

import java.util.*;
import javolution.util.FastMap;
import java.util.concurrent.Future;

import com.aionemu.commons.utils.Rnd;

import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.player.PlayerReviveService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.utils.*;
import com.aionemu.gameserver.world.knownlist.Visitor;
import com.aionemu.gameserver.world.WorldMapInstance;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(320130000)
public class AdmaStrongholdInstance extends GeneralInstanceHandler
{
	private boolean isStartTimer = false;
	private Future<?> suspiciousPotTask;
	private List<Npc> suspiciousPot = new ArrayList<Npc>();
	private FastMap<Integer, VisibleObject> objects = new FastMap<Integer, VisibleObject>();
	
	public void onDropRegistered(Npc npc) {
		Set<DropItem> dropItems = DropRegistrationService.getInstance().getCurrentDropMap().get(npc.getObjectId());
		int npcId = npc.getNpcId();
		int index = dropItems.size() + 1;
		switch (npcId) {
			case 237148: //Captain Mundirve.
			case 237149: //Butler Luitart.
			case 237150: //Chief Maid Miladi.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000032, 1)); //Observation Post Passage Key.
		    break;
			case 237155: //Bard Guionbark.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000027, 1)); //Library Key.
		    break;
			case 237240: //Enthralled Gutorum.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000026, 1)); //Inner Chamber Key.
		    break;
			case 702658: //Abbey Box.
				//dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 188053579, 1)); //[Event] Abbey Bundle.
		    break;
			case 702659: //Noble Abbey Box.
				//dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 188053580, 1)); //[Event] Noble Abbey Bundle.
		    break;
			case 237241: //Enthralled Karemiwen.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 170175031, 1)); //[Souvenir] Karemiwen's Teddy Bear.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000028, 1)); //Main Hall Key.
				for (Player player: instance.getPlayersInside()) {
				    if (player.isOnline()) {
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188053083, 1)); //Tempering Solution Chest.
					} switch (Rnd.get(1, 2)) {
				        case 1:
						    dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 123000927, 1)); //Karemiwen's Band.
						break;
						case 2:
						    dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 123000928, 1)); //Karemiwen's Leather Belt.
						break;
					}
				}
			break;
			case 237242: //Enthralled Taliesin.
			    switch (Rnd.get(1, 3)) {
				    case 1:
				        dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000029, 1)); //Great Dining Hall Key.
				    break;
				    case 2:
				        dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000030, 1)); //Lannok Treasury Key.
				    break;
				    case 3:
				        dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000031, 1)); //Servants Quarters Key.
				    break;
			    }
		    break;
			case 237239: //Death Reaper.
			    for (Player player: instance.getPlayersInside()) {
				    if (player.isOnline()) {
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188053788, 1)); //Greater Stigma Support Bundle.
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188053083, 1)); //Tempering Solution Chest.
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188052951, 1)); //[Event] Prestige Supplies.
					} switch (Rnd.get(1, 2)) {
				        case 1:
				            dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188054177, 1)); //Master Lanmark's Weapon Box.
					    break;
					    case 2:
				            dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188054181, 1)); //Master Accessory Treasure Box.
					    break;
					}
				}
			break;
		}
	}
	
	@Override
	public void handleUseItemFinish(Player player, Npc npc) {
		switch (npc.getNpcId()) {
			case 700396: //Ntuamu's Teddy Bear.
				player.getEffectController().removeEffect(18462); //Deep Wound.
			break;
			case 700397: //Tarnished Incense Burner.
				player.getEffectController().removeEffect(18463); //Mental Tremor.
			break;
		}
	}
	
	@Override
    public void onInstanceCreate(WorldMapInstance instance) {
        super.onInstanceCreate(instance);
		spawn(237244, 606.483f, 745.0968f, 197.72092f, (byte) 61); //Enthralled Lannok.
		switch (Rnd.get(1, 2)) {
		    case 1:
				spawn(237240, 378.346f, 222.61f, 164.007f, (byte) 65); //Enthralled Gutorum.
			break;
			case 2:
				spawn(237240, 525.4f, 222.724f, 164.007f, (byte) 88); //Enthralled Gutorum.
			break;
		}
    }
	
	@Override
	public void onEnterInstance(final Player player) {
		super.onInstanceCreate(instance);
		//The Suspicious Pot will disappear in 3 minutes.
		sendMsgByRace(1403059, Race.PC_ALL, 2000);
		if (!isStartTimer) {
			isStartTimer = true;
			System.currentTimeMillis();
			suspiciousPot.add((Npc) spawn(237245, 451.54147f, 276.3691f, 170.08488f, (byte) 90)); //Suspicious Pot.
			suspiciousPotTask = ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					//The Suspicious Pot has disappeared.
					sendMsg(1403060);
					suspiciousPot.get(0).getController().onDelete();
				}
			}, 180000);
		}
	}
	
    @Override
    public void onDie(Npc npc) {
        Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			case 237245: //Suspicious Pot.
				suspiciousPotTask.cancel(true);
				//Destroy the Shadow Specters in the proper order or they will retain their power.
				sendMsgByRace(1403038, Race.PC_ALL, 4000);
				//The Shadow Specter is moving towards the storehouse, to look for Warehouse Manager Gutorum.
				sendMsgByRace(1403014, Race.PC_ALL, 6000);
			break;
			case 237240: //Enthralled Gutorum.
				//The Shadow Specter is moving towards Karemiwen's Bedroom, to look for Princess Karemiwen.
				sendMsgByRace(1403015, Race.PC_ALL, 2000);
			break;
			case 237241: //Enthralled Karemiwen.
				//A dark shadow is slithering towards the 2nd floor Main Hall.
				sendMsgByRace(1403016, Race.PC_ALL, 2000);
			break;
			case 237242: //Enthralled Taliesin.
				//The Shadow Specter is moving towards the Underground Stable, to look for Stable Keeper Zeeturun.
				sendMsgByRace(1403017, Race.PC_ALL, 2000);
			break;
			case 237243: //Enthralled Zeeturun.
				//The Shadow Specter is moving towards the Collapsed Observation Post, to look for Lord Lanmark.
				sendMsgByRace(1403018, Race.PC_ALL, 2000);
			break;
			case 237148: //Captain Mundirve.
				spawn(237159, 346.57733f, 534.69476f, 181.204f, (byte) 40);
                spawn(237159, 345.45975f, 544.2697f, 182.18115f, (byte) 71);
                spawn(237160, 359.008f, 557.2267f, 181.3445f, (byte) 79);
                spawn(237160, 349.17578f, 519.81287f, 181.27892f, (byte) 15);
                spawn(237161, 344.26273f, 525.73566f, 180.68095f, (byte) 69);
                spawn(237161, 356.2736f, 556.973f, 180.74712f, (byte) 12);
            break;
			case 237244: //Enthralled Lannok.
				despawnNpc(npc);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						//The Death Reaper appeared at the Collapsed Observation Post.
						sendMsgByRace(1403019, Race.PC_ALL, 0);
						spawn(237239, 606.483f, 745.0968f, 197.72092f, (byte) 61); //Death Reaper.
				    }
			    }, 3000);
            break;
			case 237239: //Death Reaper.
				sendMsg("[Congratulation]: you finish <Adma Stronghold 4.9>");
				switch (Rnd.get(1, 2)) {
		            case 1:
				        spawn(702658, 614.9905f, 745.60156f, 198.75998f, (byte) 60); //Abbey Box.
					break;
					case 2:
					    spawn(702659, 614.9905f, 745.60156f, 198.75998f, (byte) 60); //Noble Abbey Box.
					break;
				}
				SpawnTemplate wreckOfUnstableExit = SpawnEngine.addNewSingleTimeSpawn(320130000, 730176, 627.72888f, 745.44885f, 199.8019f, (byte) 0);
			    wreckOfUnstableExit.setEntityId(66);
			    objects.put(730176, SpawnEngine.spawnObject(wreckOfUnstableExit, instanceId));
            break;
		}
    }
	
	public void removeItems(Player player) {
        Storage storage = player.getInventory();
        storage.decreaseByItemId(185000026, storage.getItemCountByItemId(185000026)); //Inner Chamber Key.
		storage.decreaseByItemId(185000027, storage.getItemCountByItemId(185000027)); //Library Key.
        storage.decreaseByItemId(185000028, storage.getItemCountByItemId(185000028)); //Main Hall Key.
		storage.decreaseByItemId(185000029, storage.getItemCountByItemId(185000029)); //Great Dining Hall Key.
		storage.decreaseByItemId(185000030, storage.getItemCountByItemId(185000030)); //Lannok Treasury Key.
		storage.decreaseByItemId(185000031, storage.getItemCountByItemId(185000031)); //Servants Quarters Key.
		storage.decreaseByItemId(185000032, storage.getItemCountByItemId(185000032)); //Observation Post Passage Key.
    }
	
	private void despawnNpc(Npc npc) {
		if (npc != null) {
			npc.getController().onDelete();
		}
	}
	
	private void sendMsg(final String str) {
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				PacketSendUtility.sendMessage(player, str);
			}
		});
	}
	
	protected void sendMsgByRace(final int msg, final Race race, int time) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						if (player.getRace().equals(race) || race.equals(Race.PC_ALL)) {
							PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(msg));
						}
					}
				});
			}
		}, time);
	}
	
	@Override
	public void onPlayerLogOut(Player player) {
		removeItems(player);
	}
	
	@Override
	public void onLeaveInstance(Player player) {
		removeItems(player);
	}
	
	@Override
    public boolean onReviveEvent(Player player) {
		player.getGameStats().updateStatsAndSpeedVisually();
		PlayerReviveService.revive(player, 100, 100, false, 0);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
		PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_INSTANT_DUNGEON_RESURRECT, 0, 0));
        return TeleportService2.teleportTo(player, mapId, instanceId, 476.61665f, 832.25867f, 165.14975f, (byte) 0);
    }
	
	@Override
	public boolean onDie(final Player player, Creature lastAttacker) {
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()), true);
		PacketSendUtility.sendPacket(player, new SM_DIE(player.haveSelfRezEffect(), player.haveSelfRezItem(), 0, 8));
		return true;
	}
}