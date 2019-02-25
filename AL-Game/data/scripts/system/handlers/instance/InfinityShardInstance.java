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

import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.services.player.PlayerReviveService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.*;
import com.aionemu.gameserver.world.knownlist.Visitor;
import com.aionemu.gameserver.world.WorldMapInstance;

/**
Author Rinzler (Encom)

12人のディーヴァよ！古代兵器ハイペリオンを破壊せよ！ 
カタラマイズは地下カタラム 6時位置の12のフォース攻略インスタンスダンジョンだ。
かなり難易度の高いダンジョンに一般モンスターはほとんどなく、ダンジョン自体は
レジェンド等級ネームドモンスターであるヒイペリオンを相手にするための場所とすることができる。
処置に成功した場合65レベル神話の評価/ヒーローグレードアイテムを入手することができます。

背景ストーリー
古代のルーン族は強大なティアマト軍に対抗するための手段としてイド エネルギーを使う強力な兵器ヒペリオンを開発した。
しかし団結力が弱かったルーン族はこのヒペリオンを正しく活用する前に内部不和によって発生した爆発により滅亡の道を歩んでしまった。

生き残った一部生存者はティアマトの手からヒペリオンを守るために故意にイド爆発を起こしてよじれた時空間の向こう側にヒペリオンを隠してしまう。
かなり以前からヒペリオンを狙っていたベリトラはティアマトが滅亡した後、ヒペリオンが隠された場所を探し始めてついにヒペリオンが隠されたカタラマイズを発見する。
天族と魔族は途方もない破壊力を持ったヒペリオンがベリトラ軍の手に落ちるのを防ぐために動き出す。

-カタラマイズ入口は地下カタラムの6時方向に存在します。
-別途の入場クエストなしでレベルと入場アイテム条件だけ満たすと入場が可能です。
**/
@InstanceID(300800000)
public class InfinityShardInstance extends GeneralInstanceHandler
{
	private int ideForcefieldGenerator;
	private boolean isInstanceDestroyed = false;
	private FastMap<Integer, VisibleObject> objects = new FastMap<Integer, VisibleObject>();
	
	@Override
    public void onDropRegistered(Npc npc) {
        Set<DropItem> dropItems = DropRegistrationService.getInstance().getCurrentDropMap().get(npc.getObjectId());
		int npcId = npc.getNpcId();
		int index = dropItems.size() + 1;
        switch (npcId) {
            case 231073: //Hyperion.
                for (Player player: instance.getPlayersInside()) {
                    if (player.isOnline()) {
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188053619, 1)); //Ancient Manastone Bundle.
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188053789, 1)); //Major Stigma Support Bundle.
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188053083, 1)); //Tempering Solution Chest.
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188052951, 1)); //[Event] Prestige Supplies.
                    } switch (Rnd.get(1, 4)) {
				        case 1:
				            dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188052387, 1)); //Hyperion's Equipment Box.
				        break;
					    case 2:
				            dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188052718, 1)); //Hyperion's Weapons Chest.
				        break;
						case 3:
				            dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188053005, 1)); //Hyperion's Wing Chest.
				        break;
						case 4:
				            dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188053154, 1)); //Hyperion's Accessory Box.
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
			case 802184: //Infinity Shard Opportunity Bundle.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 186000051, 1)); //Major Ancient Crown.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 186000052, 1)); //Greater Ancient Crown.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 186000236, 50)); //Blood Mark.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 186000237, 50)); //Ancient Coin.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 186000242, 2)); //Ceramium Medal.
			break;
        }
    }
	
	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		SpawnTemplate protectiveShield = SpawnEngine.addNewSingleTimeSpawn(300800000, 284437, 129.26147f, 137.86557f, 110.50481f, (byte) 0);
		protectiveShield.setEntityId(27);
		objects.put(284437, SpawnEngine.spawnObject(protectiveShield, instanceId)); //Protective Shield.
	}
	
	@Override
	public void onDie(Npc npc) {
		Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			case 231096: //Hyperion Defense Combatant.
			case 231097: //Hyperion Defense Scout.
			case 231098: //Hyperion Defense Medic.
			case 231103: //Summoned Ancien Tyrhund.
			case 233297: //Hyperion Defense Assaulter.
			case 233298: //Hyperion Defense Assassin.
			    despawnNpc(npc);
			break;
			case 231074: //Ide Forcefield Generator I.
			case 231078: //Ide Forcefield Generator II.
			case 231082: //Ide Forcefield Generator III.
			case 231086: //Ide Forcefield Generator IV.
				ideForcefieldGenerator++;
				if (ideForcefieldGenerator == 1) {
				} else if (ideForcefieldGenerator == 2) {
				} else if (ideForcefieldGenerator == 3) {
				} else if (ideForcefieldGenerator == 4) {
				    //The Hyperion's shields are down.
					sendMsgByRace(1401796, Race.PC_ALL, 10000);
					deleteNpc(284437); //Protective Shield.
				}
				despawnNpc(npc);
				//The Hyperion's shields are faltering.
				sendMsgByRace(1401795, Race.PC_ALL, 0);
            break;
			case 231073: //Hyperion.
			    sendMsg("[Congratulation]: you finish <Infinity Shard>");
				spawn(730842, 124.669853f, 137.840668f, 113.942917f, (byte) 0); //Infinity Shard Exit.
				spawn(802184, 127.32316f, 131.72421f, 112.17429f, (byte) 25); //Infinity Shard Opportunity Bundle.
			break;
		}
	}
	
	@Override
    public boolean onReviveEvent(Player player) {
		player.getGameStats().updateStatsAndSpeedVisually();
		PlayerReviveService.revive(player, 100, 100, false, 0);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
		PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_INSTANT_DUNGEON_RESURRECT, 0, 0));
        return TeleportService2.teleportTo(player, mapId, instanceId, 118.046104f, 115.37017f, 131.8584f, (byte) 117);
    }
	
	private void deleteNpc(int npcId) {
		if (getNpc(npcId) != null) {
			getNpc(npcId).getController().onDelete();
		}
	}
	
	private void despawnNpc(Npc npc) {
		if (npc != null) {
			npc.getController().onDelete();
		}
	}
	
	@Override
    public void onInstanceDestroy() {
        isInstanceDestroyed = true;
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
	public boolean onDie(final Player player, Creature lastAttacker) {
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()), true);
		PacketSendUtility.sendPacket(player, new SM_DIE(player.haveSelfRezEffect(), player.haveSelfRezItem(), 0, 8));
		return true;
	}
}