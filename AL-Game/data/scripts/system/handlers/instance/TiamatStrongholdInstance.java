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

import com.aionemu.commons.utils.Rnd;
import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AbstractAI;
import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.services.player.PlayerReviveService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;
import com.aionemu.gameserver.world.zone.ZoneInstance;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** @author Rinzler, Ranastic (Encom)
/****/

@InstanceID(300510000)
public class TiamatStrongholdInstance extends GeneralInstanceHandler
{
	private int kills;
	private int protectorate;
	private boolean startSuramaEvent;
	private Map<Integer, StaticDoor> doors;
	protected boolean isInstanceDestroyed = false;
	private FastMap<Integer, VisibleObject> objects = new FastMap<Integer, VisibleObject>();
	
	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		//Kahrun [Reian Leader]
		spawn(800456, 1577.6334f, 1055.3899f, 492.0681f, (byte) 26);
		//Laksyaka Colonel I
		spawn(219363, 763.0683f, 1446.5114f, 495.65198f, (byte) 90);
        spawn(219363, 763.0781f, 1191.3202f, 495.64584f, (byte) 30);
		spawn(219363, 893.29297f, 1190.8209f, 495.6457f, (byte) 30);
		spawn(219363, 893.3504f, 1446.1143f, 495.64215f, (byte) 91);
		SpawnTemplate FileLadderCGF = SpawnEngine.addNewSingleTimeSpawn(300510000, 730612, 1544.2832f, 1068.4409f, 489.32297f, (byte) 0);
		FileLadderCGF.setEntityId(55);
		objects.put(730612, SpawnEngine.spawnObject(FileLadderCGF, instanceId));
		//**//
		SpawnTemplate AionFXPostGlow = SpawnEngine.addNewSingleTimeSpawn(300510000, 730694, 1542.26f, 1068.59f, 491.32999f, (byte) 0);
		AionFXPostGlow.setEntityId(35);
		objects.put(730694, SpawnEngine.spawnObject(AionFXPostGlow, instanceId));
		doors = instance.getDoors();
		switch (Rnd.get(1, 4)) {
			case 1:
				deleteNpc(219363);
				spawn(219364, 763.0683f, 1446.5114f, 495.65198f, (byte) 90);
			break;
			case 2:
				deleteNpc(219363);
				spawn(219364, 763.0781f, 1191.3202f, 495.64584f, (byte) 30);
			break;
			case 3:
				deleteNpc(219363);
				spawn(219364, 893.29297f, 1190.8209f, 495.6457f, (byte) 30);
			break;
			case 4:
				deleteNpc(219363);
				spawn(219364, 893.3504f, 1446.1143f, 495.64215f, (byte) 91);
			break;
		}
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				protectorateFirstWave();
			}
		}, 60000);
	}
	
	public void onDropRegistered(Npc npc) {
		Set<DropItem> dropItems = DropRegistrationService.getInstance().getCurrentDropMap().get(npc.getObjectId());
		int npcId = npc.getNpcId();
		int index = dropItems.size() + 1;
		switch (npcId) {
			case 219352: //Invincible Shabokan.
			case 219353: //Brigade General Chantra.
			case 219355: //Traitor Kumbanda.
			case 219356: //Brigade General Laksyaka.
			case 219357: //Adjudant Anuhart.
			case 701541: //Brigade General Tahabata Chest.
			    for (Player player: instance.getPlayersInside()) {
				    if (player.isOnline()) {
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188053619, 1)); //Ancient Manastone Bundle.
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188053208, 1)); //Tiamat Guard's Eternal Armor Chest.
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188053083, 1)); //Tempering Solution Chest.
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188052951, 1)); //[Event] Prestige Supplies.
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 166500000, 1)); //Amplification Stone.
					}
				}
			break;
			case 219354: //Brigade General Terath.
			    for (Player player: instance.getPlayersInside()) {
				    if (player.isOnline()) {
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188053619, 1)); //Ancient Manastone Bundle.
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188053208, 1)); //Tiamat Guard's Eternal Armor Chest.
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188053083, 1)); //Tempering Solution Chest.
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188052951, 1)); //[Event] Prestige Supplies.
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 166500000, 1)); //Amplification Stone.
					} switch (Rnd.get(1, 3)) {
				        case 1:
				            dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 100001398, 1)); //Terath's Sword.
				        break;
						case 2:
				            dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 100501087, 1)); //Terath's Orb.
				        break;
					    case 3:
				            dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 101301030, 1)); //Terath's Polearm.
				        break;
					}
				}
			break;
			case 219392: //Laksyaka Elite Guard Captain.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000120, 1)); //Military Ward Key.
			break;
			case 219364: //Laksyaka Colonel.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000121, 1)); //Eternal Prison Key.
			break;
			case 702658: //Abbey Box.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 188053579, 1)); //[Event] Abbey Bundle.
		    break;
			case 702659: //Noble Abbey Box.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 188053580, 1)); //[Event] Noble Abbey Bundle.
		    break;
			case 701501: //Balaur Medal Box.
			case 701527: //Stronghold Treasure Chest.
				for (Player player: instance.getPlayersInside()) {
				    if (player.isOnline()) {
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 186000201, 5)); //Protectorate Coin.
					}
				}
			break;
			case 802179: //Tiamat Stronghold Opportunity Bundle.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 186000051, 1)); //Major Ancient Crown.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 186000052, 1)); //Greater Ancient Crown.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 186000236, 50)); //Blood Mark.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 186000237, 50)); //Ancient Coin.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 186000242, 2)); //Ceramium Medal.
			break;
		}
	}
	
	@Override
	public void handleUseItemFinish(Player player, Npc npc) {
		switch (npc.getNpcId()) {
			case 701523: //Research Center Acces Controler.
				doors.get(22).setOpen(true);
			break;
			case 701495:
			break;
		}
	}
	
	@Override
	public void onDie(Npc npc) {
		Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			case 219369: //Protectorate Elite Fighter.
			case 219370: //Protectorate Elite Infantryman.
			case 219371: //Protectorate Elite Scout.
			case 219372: //Protectorate Elite Healer.
			case 219373: //Protectorate Elite Mounted Officer.
				protectorate++;
				if (protectorate == 4) {
				    protectorateSecondWave();
				} else if (protectorate == 8) {
				   protectorateThirdWave();
				}
			break;
		    case 219352: //Invincible Shabokan.
				//A Balaur Medal Chest appeared in the Noble's Garden.
			    sendMsgByRace(1401614, Race.PC_ALL, 2000);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						doors.get(48).setOpen(true);
						doors.get(369).setOpen(true);
						doors.get(706).setOpen(true);
						spawnStrongholdGatewaySecure();
				    }
			    }, 5000);
		    break;
		    case 219355: //Traitor Kumbanda.
				//A Balaur Medal Chest appeared in the Noble's Garden.
				sendMsgByRace(1401614, Race.PC_ALL, 2000);
		    break;
			case 219356: //Brigade General Laksyaka.
				SpawnTemplate CastShadowPLSM = SpawnEngine.addNewSingleTimeSpawn(300510000, 730622, 644.15436f, 1319.6982f, 487.51764f, (byte) 0);
			    CastShadowPLSM.setEntityId(15);
			    objects.put(730622, SpawnEngine.spawnObject(CastShadowPLSM, instanceId));
			break;
		    case 219357: //Adjudant Anuhart.
				deleteNpc(283099); //Blade Storm.
				doors.get(37).setOpen(true);
			    doors.get(610).setOpen(true);
				//A Balaur Medal Chest appeared in the Noble's Garden.
				sendMsgByRace(1401614, Race.PC_ALL, 2000);
		    break;
		    case 219358: //Brigade General Tahabata.
			    switch (Rnd.get(1, 2)) {
		            case 1:
				        spawn(702658, 678.0355f, 1071.9641f, 497.75186f, (byte) 85); //Abbey Box.
					break;
					case 2:
					    spawn(702659, 678.0355f, 1071.9641f, 497.75186f, (byte) 85); //Noble Abbey Box.
					break;
				}
				//A Balaur Medal Chest appeared in the Noble's Garden.
				sendMsgByRace(1401614, Race.PC_ALL, 2000);
			    SpawnTemplate CastShadowPSSM = SpawnEngine.addNewSingleTimeSpawn(300510000, 730622, 652.39172f, 1068.9602f, 497.58624f, (byte) 0);
			    CastShadowPSSM.setEntityId(82);
			    objects.put(730622, SpawnEngine.spawnObject(CastShadowPSSM, instanceId));
		    break;
		    case 219353: //Brigade General Chantra.
			    doors.get(711).setOpen(true);
				//A Balaur Medal Chest appeared in the Noble's Garden.
				sendMsgByRace(1401614, Race.PC_ALL, 2000);
		    break;
		    case 219354: //Brigade General Terath.
			    spawnBalaurMedalBox();
				spawnStrongholdTreasureChest();
				//A Balaur Medal Chest appeared in the Noble's Garden.
				sendMsgByRace(1401614, Race.PC_ALL, 2000);
				sendMsg("Congratulation]: you finish <Tiamat Stronghold>");
				spawn(283177, 1029.9412f, 266.9634f, 416.4337f, (byte) 28); //Tiamat Eye.
				spawn(800464, 1119.9684f, 1069.8219f, 496.86157f, (byte) 0); //Reian Sorcerer.
				spawn(800464, 1120.9542f, 1068.1592f, 496.86157f, (byte) 15); //Reian Sorcerer.
				spawn(800465, 1120.6633f, 1071.6091f, 496.86157f, (byte) 112); //Reian Sorcerer.
				spawn(802179, 1030.0757f, 271.60504f, 409.08588f, (byte) 30); //Tiamat Stronghold Opportunity Bundle.
				//**//
				SpawnTemplate RecvShadowPLSM = SpawnEngine.addNewSingleTimeSpawn(300510000, 730629, 1121.5267f, 1069.9308f, 500.24982f, (byte) 0);
			    RecvShadowPLSM.setEntityId(555);
			    objects.put(730629, SpawnEngine.spawnObject(RecvShadowPLSM, instanceId));
		    break;
		}
	}
	
	private void spawnStrongholdTreasureChest() {
		spawn(701527, 1074.0504f, 1068.9313f, 785.9529f, (byte) 118);
	}
	
	private void spawnBalaurMedalBox() {
		spawn(701501, 1063.5973f, 1092.7402f, 787.685f, (byte) 107);
		spawn(701501, 1071.5909f, 1040.6797f, 787.685f, (byte) 23);
		spawn(701501, 1075.4409f, 1078.5071f, 787.685f, (byte) 16);
		spawn(701501, 1077.1716f, 1058.1995f, 787.685f, (byte) 61);
		spawn(701501, 1086.274f, 1098.3997f, 787.685f, (byte) 90);
		spawn(701501, 1099.8691f, 1047.1895f, 787.685f, (byte) 64);
	}
	
	private void raidProtectorate(final Npc npc, float x, float y, float z, boolean despawn) {
		((AbstractAI) npc.getAi2()).setStateIfNot(AIState.WALKING);
		npc.setState(1);
		npc.getMoveController().moveToPoint(x, y, z);
		PacketSendUtility.broadcastPacket(npc, new SM_EMOTION(npc, EmotionType.START_EMOTE2, 0, npc.getObjectId()));
	}
	
	private void protectorateFirstWave() {
		raidProtectorate((Npc)spawn(219369, 1509.0016f, 1051.2535f, 491.42322f, (byte) 5), 1556.6959f, 1063.507f, 492.16028f, false); //Protectorate Elite Fighter.
		raidProtectorate((Npc)spawn(219370, 1508.4454f, 1085.6877f, 491.35217f, (byte) 118), 1556.8972f, 1072.9952f, 492.16028f, false); //Protectorate Elite Infantryman.
		raidProtectorate((Npc)spawn(219371, 1508.9395f, 1061.8333f, 491.48456f, (byte) 119), 1555.9574f, 1066.3362f, 492.16028f, false); //Protectorate Elite Scout.
		raidProtectorate((Npc)spawn(219372, 1508.3008f, 1073.0205f, 491.48438f, (byte) 3), 1555.0793f, 1070.4535f, 492.16028f, false); //Protectorate Elite Healer.
	}
	
	private void protectorateSecondWave() {
		raidProtectorate((Npc)spawn(219369, 1509.0016f, 1051.2535f, 491.42322f, (byte) 5), 1556.6959f, 1063.507f, 492.16028f, false); //Protectorate Elite Fighter.
		raidProtectorate((Npc)spawn(219370, 1508.4454f, 1085.6877f, 491.35217f, (byte) 118), 1556.8972f, 1072.9952f, 492.16028f, false); //Protectorate Elite Infantryman.
		raidProtectorate((Npc)spawn(219371, 1508.9395f, 1061.8333f, 491.48456f, (byte) 119), 1555.9574f, 1066.3362f, 492.16028f, false); //Protectorate Elite Scout.
		raidProtectorate((Npc)spawn(219372, 1508.3008f, 1073.0205f, 491.48438f, (byte) 3), 1555.0793f, 1070.4535f, 492.16028f, false); //Protectorate Elite Healer.
	}
	
	private void protectorateThirdWave() {
		raidProtectorate((Npc)spawn(219370, 1509.0016f, 1051.2535f, 491.42322f, (byte) 5), 1556.6959f, 1063.507f, 492.16028f, false); //Protectorate Elite Infantryman.
		raidProtectorate((Npc)spawn(219372, 1508.4454f, 1085.6877f, 491.35217f, (byte) 118), 1556.8972f, 1072.9952f, 492.16028f, false); //Protectorate Elite Healer.
		raidProtectorate((Npc)spawn(219373, 1508.9395f, 1061.8333f, 491.48456f, (byte) 119), 1555.9574f, 1066.3362f, 492.16028f, false); //Protectorate Elite Mounted Officer.
		raidProtectorate((Npc)spawn(219373, 1508.3008f, 1073.0205f, 491.48438f, (byte) 3), 1555.0793f, 1070.4535f, 492.16028f, false); //Protectorate Elite Mounted Officer.
	}
	
	private void moveToForward(final Npc npc, float x, float y, float z, boolean despawn) {
		((AbstractAI) npc.getAi2()).setStateIfNot(AIState.WALKING);
		npc.setState(1);
		npc.getMoveController().moveToPoint(x, y, z);
		PacketSendUtility.broadcastPacket(npc, new SM_EMOTION(npc, EmotionType.START_EMOTE2, 0, npc.getObjectId()));
		if (despawn) {
		    ThreadPoolManager.getInstance().schedule(new Runnable() {
			    @Override
			    public void run() {
			  	    if (npc.getNpcId() == 800336) {
			  		    spawn(800338, 1104f, 1069f, 497f, (byte) 61);
			  		    Npc kahrun = getNpc(800338);
						Npc garnon = getNpc(800347);
						//The Reian Soldiers will secure this area. You must press on. Hurry!
					    NpcShoutsService.getInstance().sendMsg(kahrun, 1500597, kahrun.getObjectId(), 0, 5000);
					    //I'm impressed! I thought the gatekeeper would be a greater challenge.
						//I see we chose our champions well.
						NpcShoutsService.getInstance().sendMsg(kahrun, 1500598, kahrun.getObjectId(), 0, 10000);
						//There are reinforcements further in. We must deal with them.
						NpcShoutsService.getInstance().sendMsg(kahrun, 1500599, kahrun.getObjectId(), 0, 15000);
					    //What are you waiting for? Get going!
						NpcShoutsService.getInstance().sendMsg(kahrun, 1500600, kahrun.getObjectId(), 0, 20000);
						//This makes no sense. There must be a way to get to Tiamat's throne from here!
						NpcShoutsService.getInstance().sendMsg(kahrun, 1500601, kahrun.getObjectId(), 0, 25000);
						//Garnon? How did you get here?
						NpcShoutsService.getInstance().sendMsg(kahrun, 1500602, kahrun.getObjectId(), 0, 30000);
						//You've found it? Where in the world was it?
						NpcShoutsService.getInstance().sendMsg(kahrun, 1500603, kahrun.getObjectId(), 0, 35000);
						//Let's not waste time. The final battle is upon us.
						NpcShoutsService.getInstance().sendMsg(kahrun, 1500604, kahrun.getObjectId(), 0, 40000);
						//Lord Kahrun!
						NpcShoutsService.getInstance().sendMsg(garnon, 1500605, garnon.getObjectId(), 0, 45000);
						//We've found the entrance to the Dragon Lord's Refuge!
						NpcShoutsService.getInstance().sendMsg(garnon, 1500606, garnon.getObjectId(), 0, 50000);
						//The scouts we sent to Tiamaranta's Eye are the ones you should be asking.
						NpcShoutsService.getInstance().sendMsg(garnon, 1500607, garnon.getObjectId(), 0, 55000);
						//Tiamat. Hand over Siel's Relics and leave Tiamaranta.
						NpcShoutsService.getInstance().sendMsg(garnon, 1500608, garnon.getObjectId(), 0, 60000);
						//My forces are on their way.
						//Do you truly wish to face us in all-out battle? Such a choice will lead to tragedy for us all.
						NpcShoutsService.getInstance().sendMsg(kahrun, 1500609, kahrun.getObjectId(), 0, 65000);
						//Well then, you've chosen the path of destruction. Now you will see the true power of the Reian Tribe
						NpcShoutsService.getInstance().sendMsg(kahrun, 1500610, kahrun.getObjectId(), 0, 70000);
						//Aarrghh! Si...Siel's Relics...
						//They are more powerful than we ever imagined! Run! You must not try to face this alone.
						NpcShoutsService.getInstance().sendMsg(kahrun, 1500611, kahrun.getObjectId(), 0, 75000);
			  	    }
				    npc.getController().onDelete();
			    }
		    }, 13000);
		}
	}
	
	private void spawnStrongholdGatewaySecure() {
		moveToForward((Npc)spawn(800463, 1201.272f, 1072.5137f, 491f, (byte) 61), 1130, 1072, 497.3f, false);
		moveToForward((Npc)spawn(800463, 1192.8656f, 1071.1085f, 491f, (byte) 61), 1112, 1070, 497, false);
		moveToForward((Npc)spawn(800463, 1208.4175f, 1071.1797f, 491f, (byte) 61), 1133, 1072.5f, 497.3f, false);
		moveToForward((Npc)spawn(800463, 1192.8656f, 1068.3411f, 491f, (byte) 61), 1114, 1067, 496.7f, false);
		moveToForward((Npc)spawn(800463, 1208.4175f, 1068.3979f, 491f, (byte) 61), 1133.32f, 1066.47f, 497.3f, false);
		moveToForward((Npc)spawn(800463, 1201.272f, 1066.2085f, 491f, (byte) 61), 1128.8f, 1067, 497.3f, false);
		moveToForward((Npc)spawn(800380, 1190.323f, 1068.1558f, 491.03488f, (byte) 61), 1108, 1066, 497.3f, false);
		moveToForward((Npc)spawn(800374, 1188.4259f, 1066.4757f, 491.55029f, (byte) 61), 1094, 1064, 497.4f, true);
		moveToForward((Npc)spawn(800374, 1188.2158f, 1074.2047f, 491.55029f, (byte) 61), 1092.5f, 1074.6f, 497.4f, true);
		moveToForward((Npc)spawn(800376, 1190.3859f, 1071.6548f, 491.03488f, (byte) 61), 1109, 1073, 497.2f, false);
		moveToForward((Npc)spawn(800461, 1184.7582f, 1068.6f, 491.03488f, (byte) 61), 1111, 1068.6f, 497.33f, false);
		moveToForward((Npc)spawn(800347, 1178.0425f, 1072.28f, 491.02545f, (byte) 61), 1106, 1072, 497.2f, false);
		moveToForward((Npc)spawn(800336, 1178.0559f, 1069.6f, 491.02545f, (byte) 61), 1104, 1069, 497, true);
	}
	
	@Override
	public void onEnterZone(Player player, ZoneInstance zone) {
		if (zone.getAreaTemplate().getZoneName() == ZoneName.get("LAKSYAKA_LEGION_HQ_300510000")) {
			if (!startSuramaEvent) {
				startSuramaEvent = true;
				spawn(800433, 725.93f, 1319.9f, 490.7f, (byte) 61);
			}
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
	
	protected Npc getNpc(int npcId) {
		if (!isInstanceDestroyed) {
			return instance.getNpc(npcId);
		}
		return null;
	}
	
	@Override
    public boolean onReviveEvent(Player player) {
		player.getGameStats().updateStatsAndSpeedVisually();
		PlayerReviveService.revive(player, 100, 100, false, 0);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
		PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_INSTANT_DUNGEON_RESURRECT, 0, 0));
        return TeleportService2.teleportTo(player, mapId, instanceId, 1581.6545f, 1068.9741f, 492.00992f, (byte) 61);
    }
	
	@Override
	public boolean onDie(final Player player, Creature lastAttacker) {
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()), true);
		PacketSendUtility.sendPacket(player, new SM_DIE(player.haveSelfRezEffect(), player.haveSelfRezItem(), 0, 8));
		return true;
	}
	
	private boolean isDead(Npc npc) {
		return (npc == null || npc.getLifeStats().isAlreadyDead());
	}
	
	private void deleteNpc(int npcId) {
		if (getNpc(npcId) != null) {
			getNpc(npcId).getController().onDelete();
		}
	}
	
	@Override
	public void onInstanceDestroy() {
		doors.clear();
		isInstanceDestroyed = true;
	}
}