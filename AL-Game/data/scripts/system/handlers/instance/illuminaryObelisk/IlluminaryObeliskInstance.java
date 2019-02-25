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
package instance.illuminaryObelisk;

import java.util.*;
import javolution.util.FastMap;
import java.util.concurrent.Future;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.*;
import com.aionemu.gameserver.controllers.effect.PlayerEffectController;
import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.player.PlayerReviveService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.*;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
Author Rinzler (Encom)

You start at the top of the instance so you need to move down as fast as you can.
If you die inside you will restart at the entrance of the instance.  
The tower has two floors, bridges on east and west are connected to the second floor and north and south bridges are connected to the first floor.  
An elevator in the center connects both floors.  
Four Shield Units must be defended from monsters, and each shield needs to charge 3 phases, when all shields are at third phase the final boss will spawn.  
Cannons are located around the shield units, enter them with the right item and make wide area damage to the monsters.

Special Features:
Successfully defend the towers and the final boss will appear.
You will need to collect the items, defend the towers and then the "Boss" will appear.
Updrafts are placed around the map so you can travel faster between floors and bridges, if you fall outside the updraft it will instant kill you.
The rolls of the party members is important, designate a person to collect the items, another for the cannons, and to defend the shield units.

Starting Point:
You will start at the top of the instance.
At the starting point you can obtain quests from the NPC.
A timer of 30 minutes will start once you glide down.
If you die inside the instance, you will resurrect at the entrance.

Shield Units:
Its a good idea to start powering the shields once you have 6 ide shield items.
Help collect the remaining pieces together in the other bridges.
Once you charge a shield with one of the items, a wave of monster will appear, help that person and kill the mobs.
Protect the shield units from monsters while you charge them up to the 3rd phase.
Once all shields are at the 3rd phase no more monsters will spawn.

Activate The Seal:
When all shield units have been charged up to the 3rd phase, you can activate the passage to the final boss.
When you activate the seal the final boss will appear and the fight will begin.
**/

@InstanceID(301230000)
public class IlluminaryObeliskInstance extends GeneralInstanceHandler
{
	private long startTime;
	private Future<?> instanceTimer;
	private Map<Integer, StaticDoor> doors;
	private boolean isDoneEasternRaid = false;
	private boolean isDoneWesternRaid = false;
	private boolean isDoneSouthernRaid = false;
	private boolean isDoneNorthernRaid = false;
	protected boolean isInstanceDestroyed = false;
	private List<Integer> movies = new ArrayList<Integer>();
	
	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		doors = instance.getDoors();
	}
	
   /**
	* Reward:
	* After a successful capture of the boss you will get a small chance of obtaining mythical wings, and a variety of items.
	* Boxes are for all the members and the wings only for one person in the group.
	*/
	public void onDropRegistered(Npc npc) {
		Set<DropItem> dropItems = DropRegistrationService.getInstance().getCurrentDropMap().get(npc.getObjectId());
		int npcId = npc.getNpcId();
		int index = dropItems.size() + 1;
		switch (npcId) {
			case 702018: //Test Weapon Dynatoum.
				for (Player player: instance.getPlayersInside()) {
				    if (player.isOnline()) {
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188053619, 1)); //Ancient Manastone Bundle.
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188053789, 1)); //Major Stigma Support Bundle.
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188053083, 1)); //Tempering Solution Chest.
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188053100, 1)); //Pure Dynatoum's Equipment Crux Box.
					} switch (Rnd.get(1, 2)) {
				        case 1:
				            dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188052830, 1)); //Dynatoum's Brazen Weapon Box.
				        break;
					    case 2:
				            dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188052831, 1)); //Dynatoum's Brazen Armor Box.
				        break;
					} switch (Rnd.get(1, 2)) {
				        case 1:
				            dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 188053623, 1)); //Fire Dragon King's Weapon Bundle [Mythic].
				        break;
					    case 2:
				            dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 188054244, 1)); //Dreaming Nether Water Dragon King's Weapon Chest [Mythic].
				        break;
					}
				}
			break;
			case 702658: //Abbey Box.
				//dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 188053579, 1)); //[Event] Abbey Bundle.
		    break;
			case 702659: //Noble Abbey Box.
				//dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 188053580, 1)); //[Event] Noble Abbey Bundle.
		    break;
			
		   /**
			* Collect Items:
			* Each "Shield Generator" unit needs 3 ide items, 12 items in total, you can find them all around the instance.
			* Bombs to use the cannons appear in chests around the instance in a different place every time, collect them too.
			*/
			case 730884: //Flourishing Idium.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 164000289, 3));
			break;
			case 730885: //Danuar Cannonballs.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 164000290, 3));
			break;
		}
	}
	
	@Override
	public void onEnterInstance(final Player player) {
		super.onInstanceCreate(instance);
		if (instanceTimer == null) {
			startTime = System.currentTimeMillis();
			//The weakened protective shield will disappear in 30 minutes.
			sendMsgByRace(1402129, Race.PC_ALL, 10000);
			//The protective shield covering the Illuminary Obelisk has disappeared.
			//The Pashid Destruction Unit's intense bombing commences.
			sendMsgByRace(1402236, Race.PC_ALL, 60000);
			///////////////////////////////////////////
			//The weakened protective shield will disappear in 25 minutes.
			sendMsgByRace(1402130, Race.PC_ALL, 300000);
			//The weakened protective shield will disappear in 20 minutes.
			sendMsgByRace(1402131, Race.PC_ALL, 600000);
			//The weakened protective shield will disappear in 15 minutes.
			sendMsgByRace(1402132, Race.PC_ALL, 900000);
			//The weakened protective shield will disappear in 10 minutes.
			sendMsgByRace(1402133, Race.PC_ALL, 1200000);
			//The weakened protective shield will disappear in 5 minutes.
			sendMsgByRace(1402134, Race.PC_ALL, 1500000);
			//The weakened protective shield will disappear in 1 minute.
			sendMsgByRace(1402135, Race.PC_ALL, 1740000);
			instanceTimer = ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					openFirstDoors();
				}
			}, 60000);
		} switch (player.getRace()) {
		    case ELYOS:
			    sendMovie(player, 894);
			break;
			case ASMODIANS:
			    sendMovie(player, 895);
			break;
		}
	}
	
   /**
	* Defense Cannons:
	* Each Shield Unit has a defense cannon that can be used.
	* This cannons do powerful wide area damage attacks.
	* In order to use them you need to have Bomb items.
	* When a shield is charged completely a cannon will spawn to help in the defense of the area.
	* Determining a person to use the cannon and positioning before the mobs come is a recommended.
	* Bombs to use the cannons appear in chests around the instance in a different place every time, collect them too.
	*/
	@Override
	public void handleUseItemFinish(Player player, Npc npc) {
		switch (npc.getNpcId()) {
			case 702009: //Danuar Cannon.
			case 702021: //Danuar Cannon.
			case 702022: //Danuar Cannon.
			case 702023: //Danuar Cannon.
			    despawnNpc(npc);
				SkillEngine.getInstance().getSkill(npc, 21511, 60, player).useNoAnimationSkill();
			break;
		}
	}
	
   /**
	* If a "Shield" is destroyed, you must start again from the 1st phase
	* You can heal the shield with a restoration skill.
	*/
	@Override
    public void onDie(Npc npc) {
        Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			case 284851: //Beritra Ranger.
			case 284853: //Beritra Songweaver.
			case 284855: //Beritra Immobilizer.
			case 284857: //Beritra Healer.
				startSCRT();
				despawnNpc(npc);
			break;
			case 702010: //Eastern Shield Generator.
		        despawnNpc(npc);
				deleteNpc(233723);
			    deleteNpc(233724);
			    deleteNpc(233725);
				deleteNpc(233734);
				deleteNpc(233735);
				deleteNpc(233857);
				deleteNpc(284841);
			    deleteNpc(284842);
			    deleteNpc(284843);
				deleteNpc(284850);
				deleteNpc(284851);
				deleteNpc(702218);
				deleteNpc(702219);
				deleteNpc(702220);
				sendMsgByRace(1402139, Race.PC_ALL, 0);
			    ThreadPoolManager.getInstance().schedule(new Runnable() {
				    @Override
				    public void run() {
						spawn(702010, 255.47392f, 293.56177f, 321.18497f, (byte) 89); //Eastern Shield Generator.
				    }
			    }, 10000);
			break;
		    case 702011: //Western Shield Generator.
		        despawnNpc(npc);
				deleteNpc(233726);
				deleteNpc(233727);
				deleteNpc(233728);
				deleteNpc(233736);
				deleteNpc(233737);
				deleteNpc(233858);
				deleteNpc(284844);
				deleteNpc(284845);
				deleteNpc(284846);
				deleteNpc(284852);
				deleteNpc(284853);
				deleteNpc(702221);
				deleteNpc(702222);
				deleteNpc(702223);
				sendMsgByRace(1402140, Race.PC_ALL, 0);
			    ThreadPoolManager.getInstance().schedule(new Runnable() {
				    @Override
				    public void run() {
						spawn(702011, 255.55742f, 216.03549f, 321.21344f, (byte) 30); //Western Shield Generator.
				    }
			    }, 10000);
			break;
		    case 702012: //Southern Shield Generator.
		        despawnNpc(npc);
				deleteNpc(233729);
				deleteNpc(233730);
				deleteNpc(233731);
				deleteNpc(233738);
				deleteNpc(233739);
				deleteNpc(233880);
				deleteNpc(284847);
				deleteNpc(284848);
				deleteNpc(284849);
				deleteNpc(284854);
				deleteNpc(284855);
				deleteNpc(702224);
				deleteNpc(702225);
				deleteNpc(702226);
				sendMsgByRace(1402141, Race.PC_ALL, 0);
			    ThreadPoolManager.getInstance().schedule(new Runnable() {
				    @Override
				    public void run() {
						spawn(702012, 294.20718f, 254.60352f, 295.7729f, (byte) 60); //Southern Shield Generator.
				    }
			    }, 10000);
			break;
		    case 702013: //Northern Shield Generator.
		        despawnNpc(npc);
				deleteNpc(233720);
				deleteNpc(233721);
				deleteNpc(233722);
				deleteNpc(233732);
				deleteNpc(233733);
				deleteNpc(233881);
				deleteNpc(284838);
				deleteNpc(284839);
				deleteNpc(284840);
				deleteNpc(284856);
				deleteNpc(284857);
				deleteNpc(702227);
				deleteNpc(702228);
				deleteNpc(702229);
				sendMsgByRace(1402142, Race.PC_ALL, 0);
			    ThreadPoolManager.getInstance().schedule(new Runnable() {
				    @Override
				    public void run() {
						spawn(702013, 216.97739f, 254.4616f, 295.77353f, (byte) 0); //Northern Shield Generator.
				    }
			    }, 10000);
			break;
			
		   /****************************
			* Eastern Shield Generator *
			****************************/
			case 233723:
			case 233724:
			case 233725:
				despawnNpc(npc);
				if (getNpcs(233723).isEmpty() &&
				    getNpcs(233724).isEmpty() &&
					getNpcs(233725).isEmpty()) {
				    sendMsgByRace(1402194, Race.PC_ALL, 2000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
				        @Override
				        public void run() {
							spawn(702218, 255.56438f, 297.59488f, 321.39154f, (byte) 29);
				        }
			        }, 2000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
				        @Override
				        public void run() {
					        startWaveEasternShieldGenerator2();
				        }
			        }, 8000);
				}
			break;
			case 284841:
			case 284842:
			case 284843:
				despawnNpc(npc);
				if (getNpcs(284841).isEmpty() &&
				    getNpcs(284842).isEmpty() &&
					getNpcs(284843).isEmpty()) {
				    sendMsgByRace(1402194, Race.PC_ALL, 2000);
					sendMsgByRace(1402198, Race.PC_ALL, 10000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
				        @Override
				        public void run() {
							spawn(702219, 255.56438f, 297.59488f, 321.39154f, (byte) 29);
				        }
			        }, 2000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
				        @Override
				        public void run() {
					        startWaveEasternShieldGenerator3();
				        }
			        }, 8000);
				}
			break;
			case 233734:
			case 233735:
			case 284850:
				despawnNpc(npc);
				if (getNpcs(233734).isEmpty() &&
				    getNpcs(233735).isEmpty() &&
					getNpcs(284850).isEmpty()) {
				    sendMsgByRace(1402194, Race.PC_ALL, 2000);
					sendMsgByRace(1402203, Race.PC_ALL, 20000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
				        @Override
				        public void run() {
							isDoneEasternRaid = true;
							spawn(702220, 255.56438f, 297.59488f, 321.39154f, (byte) 29);
				        }
			        }, 2000);
				}
			break;
			
		   /****************************
			* Western Shield Generator *
			****************************/
			case 233726:
			case 233727:
			case 233728:
				despawnNpc(npc);
				if (getNpcs(233726).isEmpty() &&
				    getNpcs(233727).isEmpty() &&
					getNpcs(233728).isEmpty()) {
				    sendMsgByRace(1402195, Race.PC_ALL, 2000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
				        @Override
				        public void run() {
							spawn(702221, 255.38777f, 212.00926f, 321.37292f, (byte) 90);
				        }
			        }, 2000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
				        @Override
				        public void run() {
					        startWaveWesternShieldGenerator2();
				        }
			        }, 8000);
				}
			break;
			case 284844:
			case 284845:
			case 284846:
				despawnNpc(npc);
				if (getNpcs(284844).isEmpty() &&
				    getNpcs(284845).isEmpty() &&
					getNpcs(284846).isEmpty()) {
					sendMsgByRace(1402195, Race.PC_ALL, 2000);
					sendMsgByRace(1402199, Race.PC_ALL, 10000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
				        @Override
				        public void run() {
							spawn(702222, 255.38777f, 212.00926f, 321.37292f, (byte) 90);
				        }
			        }, 2000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
				        @Override
				        public void run() {
					        startWaveWesternShieldGenerator3();
				        }
			        }, 8000);
				}
			break;
			case 233736:
			case 233737:
			case 284852:
				despawnNpc(npc);
				if (getNpcs(233736).isEmpty() &&
				    getNpcs(233737).isEmpty() &&
					getNpcs(284852).isEmpty()) {
				    sendMsgByRace(1402195, Race.PC_ALL, 2000);
					sendMsgByRace(1402203, Race.PC_ALL, 20000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
				        @Override
				        public void run() {
							isDoneWesternRaid = true;
							spawn(702223, 255.38777f, 212.00926f, 321.37292f, (byte) 90);
				        }
			        }, 2000);
				}
			break;
			
		   /*****************************
			* Southern Shield Generator *
			*****************************/
			case 233729:
			case 233730:
			case 233731:
				despawnNpc(npc);
				if (getNpcs(233729).isEmpty() &&
				    getNpcs(233730).isEmpty() &&
					getNpcs(233731).isEmpty()) {
				    sendMsgByRace(1402196, Race.PC_ALL, 2000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
				        @Override
				        public void run() {
							spawn(702224, 298.13452f, 254.48087f, 295.93027f, (byte) 119);
				        }
			        }, 2000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
				        @Override
				        public void run() {
					        startWaveSouthernShieldGenerator2();
				        }
			        }, 8000);
				}
			break;
			case 284847:
			case 284848:
			case 284849:
				despawnNpc(npc);
				if (getNpcs(284847).isEmpty() &&
				    getNpcs(284848).isEmpty() &&
					getNpcs(284849).isEmpty()) {
				    sendMsgByRace(1402196, Race.PC_ALL, 2000);
					sendMsgByRace(1402200, Race.PC_ALL, 10000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
				        @Override
				        public void run() {
							spawn(702225, 298.13452f, 254.48087f, 295.93027f, (byte) 119);
				        }
			        }, 2000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
				        @Override
				        public void run() {
					        startWaveSouthernShieldGenerator3();
				        }
			        }, 8000);
				}
			break;
			case 233738:
			case 233739:
			case 284854:
				despawnNpc(npc);
				if (getNpcs(233738).isEmpty() &&
				    getNpcs(233739).isEmpty() &&
					getNpcs(284854).isEmpty()) {
				    sendMsgByRace(1402196, Race.PC_ALL, 2000);
					sendMsgByRace(1402203, Race.PC_ALL, 20000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
				        @Override
				        public void run() {
							isDoneSouthernRaid = true;
							spawn(702226, 298.13452f, 254.48087f, 295.93027f, (byte) 119);
				        }
			        }, 2000);
				}
			break;
			
		   /*****************************
			* Northern Shield Generator *
			*****************************/
			case 233720:
			case 233721:
			case 233722:
				despawnNpc(npc);
				if (getNpcs(233720).isEmpty() &&
				    getNpcs(233721).isEmpty() &&
					getNpcs(233722).isEmpty()) {
					sendMsgByRace(1402197, Race.PC_ALL, 2000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
				        @Override
				        public void run() {
							spawn(702227, 212.96484f, 254.4526f, 295.90784f, (byte) 60);
				        }
			        }, 2000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
				        @Override
				        public void run() {
					        startWaveNorthernShieldGenerator2();
				        }
			        }, 8000);
				}
			break;
			case 284838:
			case 284839:
			case 284840:
				despawnNpc(npc);
				if (getNpcs(284838).isEmpty() &&
				    getNpcs(284839).isEmpty() &&
					getNpcs(284840).isEmpty()) {
				    sendMsgByRace(1402197, Race.PC_ALL, 2000);
					sendMsgByRace(1402201, Race.PC_ALL, 10000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
				        @Override
				        public void run() {
							spawn(702228, 212.96484f, 254.4526f, 295.90784f, (byte) 60);
				        }
			        }, 2000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
				        @Override
				        public void run() {
					        startWaveNorthernShieldGenerator3();
				        }
			        }, 8000);
				}
			break;
			case 233732:
			case 233733:
			case 284856:
				despawnNpc(npc);
				if (getNpcs(233732).isEmpty() &&
				    getNpcs(233733).isEmpty() &&
					getNpcs(284856).isEmpty()) {
				    sendMsgByRace(1402197, Race.PC_ALL, 2000);
					sendMsgByRace(1402203, Race.PC_ALL, 20000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
				        @Override
				        public void run() {
							isDoneNorthernRaid = true;
							spawn(702229, 212.96484f, 254.4526f, 295.90784f, (byte) 60);
				        }
			        }, 2000);
				}
			break;
			
		   /**
			* Final Boss.
			*/
			case 233740: //Test Weapon Dynatoum.
				despawnNpc(npc);
				sendMsg("[Congratulation]: you finish <Illuminary Obelisk>");
				spawn(702018, 258.888f, 251.14882f, 455.12198f, (byte) 105); //Supply Box.
				spawn(730905, 255.5597f, 254.49713f, 455.12012f, (byte) 104); //Illuminary Obelisk Exit.
				switch (Rnd.get(1, 2)) {
		            case 1:
				        spawn(702658, 259.3773f, 250.84984f, 455.1222f, (byte) 45); //Abbey Box.
					break;
					case 2:
					    spawn(702659, 259.3773f, 250.84984f, 455.1222f, (byte) 45); //Noble Abbey Box.
					break;
				}
			break;
		}
    }
	
	private void removeEffects(Player player) {
		PlayerEffectController effectController = player.getEffectController();
		effectController.removeEffect(0);
	}
	
	public void removeItems(Player player) {
        Storage storage = player.getInventory();
        storage.decreaseByItemId(164000289, storage.getItemCountByItemId(164000289));
		storage.decreaseByItemId(164000290, storage.getItemCountByItemId(164000290));
    }
	
	private void attackGenerator(final Npc npc, float x, float y, float z, boolean despawn) {
		((AbstractAI) npc.getAi2()).setStateIfNot(AIState.WALKING);
		npc.setState(1);
		npc.getMoveController().moveToPoint(x, y, z);
		PacketSendUtility.broadcastPacket(npc, new SM_EMOTION(npc, EmotionType.START_EMOTE2, 0, npc.getObjectId()));
	}
	
   /**
	* The higher the phase of the charge will spawn more difficult monsters, in the 3rd phase elite monsters will spawn.
	* Charging a shield to the 3rd phase continuously can be hard because of all the mobs you will have to handle.
	* A few easy monsters will spawn after a certain time if you leave the shield unit alone.
	* After all units have been charged to the 3rd phase, defeat the remaining monsters.
	****************************
	* Eastern Shield Generator *
	****************************/
	private void startWaveEasternShieldGenerator2() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				attackGenerator((Npc)spawn(284842, 252.68709f, 333.483f, 325.59268f, (byte) 90), 252.4865f, 296.63016f, 321.30084f, false);
				attackGenerator((Npc)spawn(284841, 255.74022f, 333.2762f, 325.49332f, (byte) 90), 255.46408f, 297.3457f, 321.3599f, false);
				attackGenerator((Npc)spawn(284843, 258.72256f, 333.27713f, 325.58722f, (byte) 90), 258.93884f, 295.81204f, 321.29742f, false);
			}
		}, 1000);
	}
	private void startWaveEasternShieldGenerator3() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				attackGenerator((Npc)spawn(233734, 252.68709f, 333.483f, 325.59268f, (byte) 90), 252.4865f, 296.63016f, 321.30084f, false);
				attackGenerator((Npc)spawn(233735, 255.74022f, 333.2762f, 325.49332f, (byte) 90), 255.46408f, 297.3457f, 321.3599f, false);
				attackGenerator((Npc)spawn(284850, 258.72256f, 333.27713f, 325.58722f, (byte) 90), 258.93884f, 295.81204f, 321.29742f, false);
			}
		}, 6000);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				attackGenerator((Npc)spawn(233857, 252.68709f, 333.483f, 325.59268f, (byte) 90), 252.4865f, 296.63016f, 321.30084f, false);
				attackGenerator((Npc)spawn(284851, 255.74022f, 333.2762f, 325.49332f, (byte) 90), 255.46408f, 297.3457f, 321.3599f, false);
				attackGenerator((Npc)spawn(233857, 258.72256f, 333.27713f, 325.58722f, (byte) 90), 258.93884f, 295.81204f, 321.29742f, false);
			}
		}, 23000);
	}
	
   /****************************
	* Western Shield Generator *
	****************************/
	private void startWaveWesternShieldGenerator2() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				attackGenerator((Npc)spawn(284844, 258.37912f, 176.03621f, 325.59268f, (byte) 30), 258.4031f, 212.42247f, 321.33325f, false);
				attackGenerator((Npc)spawn(284845, 255.55922f, 176.17963f, 325.49332f, (byte) 29), 255.8037f, 212.23003f, 321.34384f, false);
				attackGenerator((Npc)spawn(284846, 252.49738f, 176.27466f, 325.52942f, (byte) 29), 253.00607f, 213.30444f, 321.28207f, false);
			}
		}, 1000);
	}
	private void startWaveWesternShieldGenerator3() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				attackGenerator((Npc)spawn(233736, 258.37912f, 176.03621f, 325.59268f, (byte) 30), 258.4031f, 212.42247f, 321.33325f, false);
				attackGenerator((Npc)spawn(233737, 255.55922f, 176.17963f, 325.49332f, (byte) 29), 255.8037f, 212.23003f, 321.34384f, false);
				attackGenerator((Npc)spawn(284852, 252.49738f, 176.27466f, 325.52942f, (byte) 29), 253.00607f, 213.30444f, 321.28207f, false);
			}
		}, 6000);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				attackGenerator((Npc)spawn(233858, 258.37912f, 176.03621f, 325.59268f, (byte) 30), 258.4031f, 212.42247f, 321.33325f, false);
				attackGenerator((Npc)spawn(284853, 255.55922f, 176.17963f, 325.49332f, (byte) 29), 255.8037f, 212.23003f, 321.34384f, false);
				attackGenerator((Npc)spawn(233858, 252.49738f, 176.27466f, 325.52942f, (byte) 29), 253.00607f, 213.30444f, 321.28207f, false);
			}
		}, 23000);
	}
	
   /*****************************
	* Southern Shield Generator *
	*****************************/
	private void startWaveSouthernShieldGenerator2() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				attackGenerator((Npc)spawn(284847, 337.93338f, 257.88702f, 292.43845f, (byte) 60), 298.06833f, 257.82462f, 295.92047f, false);
				attackGenerator((Npc)spawn(284848, 338.05304f, 254.6424f, 292.3325f, (byte) 60), 298.272f, 254.66296f, 295.94693f, false);
				attackGenerator((Npc)spawn(284849, 338.13315f, 251.34738f, 292.48932f, (byte) 59), 297.2915f, 252.06613f, 295.854f, false);
			}
		}, 1000);
	}
	private void startWaveSouthernShieldGenerator3() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				attackGenerator((Npc)spawn(233738, 337.93338f, 257.88702f, 292.43845f, (byte) 60), 298.06833f, 257.82462f, 295.92047f, false);
				attackGenerator((Npc)spawn(233739, 338.05304f, 254.6424f, 292.3325f, (byte) 60), 298.272f, 254.66296f, 295.94693f, false);
				attackGenerator((Npc)spawn(284854, 338.13315f, 251.34738f, 292.48932f, (byte) 59), 297.2915f, 252.06613f, 295.854f, false);
			}
		}, 6000);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				attackGenerator((Npc)spawn(233880, 337.93338f, 257.88702f, 292.43845f, (byte) 60), 298.06833f, 257.82462f, 295.92047f, false);
				attackGenerator((Npc)spawn(284855, 338.05304f, 254.6424f, 292.3325f, (byte) 60), 298.272f, 254.66296f, 295.94693f, false);
				attackGenerator((Npc)spawn(233880, 338.13315f, 251.34738f, 292.48932f, (byte) 59), 297.2915f, 252.06613f, 295.854f, false);
			}
		}, 23000);
	}
	
   /*****************************
	* Northern Shield Generator *
	*****************************/
	private void startWaveNorthernShieldGenerator2() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				attackGenerator((Npc)spawn(284838, 174.50981f, 251.38982f, 292.43088f, (byte) 0), 211.6748f, 252.11331f, 295.82132f, false);
				attackGenerator((Npc)spawn(284839, 174.9973f, 254.4739f, 292.3325f, (byte) 0), 211.53903f, 254.39848f, 295.99915f, false);
				attackGenerator((Npc)spawn(284840, 174.84029f, 257.80832f, 292.4389f, (byte) 0), 211.44466f, 257.45963f, 295.74582f, false);
			}
		}, 1000);
	}
	private void startWaveNorthernShieldGenerator3() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				attackGenerator((Npc)spawn(233732, 174.50981f, 251.38982f, 292.43088f, (byte) 0), 211.6748f, 252.11331f, 295.82132f, false);
				attackGenerator((Npc)spawn(233733, 174.9973f, 254.4739f, 292.3325f, (byte) 0), 211.53903f, 254.39848f, 295.99915f, false);
				attackGenerator((Npc)spawn(284856, 174.84029f, 257.80832f, 292.4389f, (byte) 0), 211.44466f, 257.45963f, 295.74582f, false);
			}
		}, 6000);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				attackGenerator((Npc)spawn(233881, 174.50981f, 251.38982f, 292.43088f, (byte) 0), 211.6748f, 252.11331f, 295.82132f, false);
				attackGenerator((Npc)spawn(284857, 174.9973f, 254.4739f, 292.3325f, (byte) 0), 211.53903f, 254.39848f, 295.99915f, false);
				attackGenerator((Npc)spawn(233881, 174.84029f, 257.80832f, 292.4389f, (byte) 0), 211.44466f, 257.45963f, 295.74582f, false);
			}
		}, 23000);
	}
	
   /**********************************
	* Shield Control Room Teleporter *
	**********************************/
	private void startSCRT() {
		if (isDoneEasternRaid &&
		    isDoneWesternRaid &&
		    isDoneSouthernRaid &&
			isDoneNorthernRaid) {
		    spawnSCRT();
		}
	}
	
	private void spawnSCRT() {
		deleteNpc(702010);
		deleteNpc(702011);
		deleteNpc(702012);
		deleteNpc(702013);
		//The shield is activated and the Pashid Destruction Unit is retreating.
		//The Shield Control Room Teleporter has appeared.
		sendMsgByRace(1402202, Race.PC_ALL, 0);
		spawn(730886, 255.47392f, 293.56177f, 321.18497f, (byte) 89);
		spawn(730886, 255.55742f, 216.03549f, 321.21344f, (byte) 30);
		spawn(730886, 294.20718f, 254.60352f, 295.7729f, (byte) 60);
		spawn(730886, 216.97739f, 254.4616f, 295.77353f, (byte) 0);
	}
	
	protected void openFirstDoors() {
	    openDoor(129);
    }
	
	protected void openDoor(int doorId) {
        StaticDoor door = doors.get(doorId);
        if (door != null) {
            door.setOpen(true);
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
	
	private void sendMovie(Player player, int movie) {
        if (!movies.contains(movie)) {
             movies.add(movie);
             PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, movie));
        }
    }
	
	@Override
	public void onPlayerLogOut(Player player) {
		removeItems(player);
		removeEffects(player);
	}
	
	@Override
	public void onLeaveInstance(Player player) {
		removeItems(player);
		removeEffects(player);
	}
	
	@Override
	public void onInstanceDestroy() {
		isInstanceDestroyed = true;
		doors.clear();
		movies.clear();
	}
	
	public void onExitInstance(Player player) {
		TeleportService2.moveToInstanceExit(player, mapId, player.getRace());
	}
	
	private void deleteNpc(int npcId) {
		if (getNpc(npcId) != null) {
			getNpc(npcId).getController().onDelete();
		}
	}
	
	protected void despawnNpc(Npc npc) {
		if (npc != null) {
			npc.getController().onDelete();
		}
	}
	
	protected void despawnNpcs(List<Npc> npcs) {
		for (Npc npc: npcs) {
			npc.getController().onDelete();
		}
	}
	
	protected Npc getNpc(int npcId) {
		if (!isInstanceDestroyed) {
			return instance.getNpc(npcId);
		}
		return null;
	}
	
	protected List<Npc> getNpcs(int npcId) {
		if (!isInstanceDestroyed) {
			return instance.getNpcs(npcId);
		}
		return null;
	}
	
	@Override
    public boolean onReviveEvent(Player player) {
		player.getGameStats().updateStatsAndSpeedVisually();
		PlayerReviveService.revive(player, 100, 100, false, 0);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
		PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_INSTANT_DUNGEON_RESURRECT, 0, 0));
        return TeleportService2.teleportTo(player, mapId, instanceId, 321.9613f, 323.62772f, 405.49997f, (byte) 35);
    }
	
	@Override
	public boolean onDie(final Player player, Creature lastAttacker) {
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()), true);
		PacketSendUtility.sendPacket(player, new SM_DIE(player.haveSelfRezEffect(), player.haveSelfRezItem(), 0, 8));
		return true;
	}
}