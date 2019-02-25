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
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.player.PlayerReviveService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.*;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
Author Rinzler (Encom)

第1区域:軍需基地の衛兵所>奪われたルーンの会堂
■ 第1区域の進め方
1.大量のモンスターが密集している軍需基地の衛兵所を通り抜けて建物の内に入る。
2.1番のネームドモンスター警備隊長ロフカを倒すと、閉まっているドアが開く。
3.2番のネームドモンスター砲兵隊長クルマタを倒すと、1番 or 2番の休憩室の鍵を入手することができる。
4.入手した鍵を応じて、奪われたルーンの会堂内の休憩室に入る。内にいる精鋭モンスターを倒すと、2区域につながっているドアが開く。

第2区域:殺伐としたルーンの回廊>軍需基地の実験洞窟
■ 第2区域の進め方
1.寂冥のルーンの庭園で、3番のネームドモンスター闇の捕食者テラカナクを処置。(スキップしても構わない)
2.軍需基地の実験洞窟に入って、入口を塞いでいる精鋭モンスターを倒すと通路が開かれる。
3.4番のネームドモンスター研究家テセリクを処置。忘れ去られたルーンの聖水につながるドアが開く。

※3番のネームドモンスターは、選択型ネームドである。パスするのも可能！
一方、4番のネームドモンスターは倒さないと、次の通路を開けることができない。

ボーナス モンスター
-軍需基地の実験洞窟にある5つの部屋から素早い軍需基地盗掘屋がランダムに登場する。
-倒すと、外見変更用アイテム、古代コイン、古代魔石などが獲得できる。

第3区域:眠るルーンの橋>軍需基地の武器庫
■ 第3区域の進め方
1.眠るルーンの橋の中心部に5番のネームドモンスター守門長スラートが潜伏している。倒すと、次の通路が開かれる。
2.軍需基地の武器庫では6番のネームドモンスター兵站指揮官ラノディムに会うことができる。倒すと、赤/青/緑の鍵の中から2つがドロップされる。
3.入手した鍵でその色に合ったドアを開けることができる。また、各部屋からルーンの石室の鍵を入手することができる。
4.最終ボス選択のために必要な数のルーンの石室の鍵が集まったか確認しよう。
5.7,8番のネームドモンスターである監察将校オバヌカと監察将校サヤフムを倒そう。(この二つはスキップしても構わない)

第4区域:参謀長室
■ 第4区域の進め方
1.参謀長室の入口にいる精鋭モンスターを倒すとドアが開く。
2.9番のネームドモンスター参謀長モリアタを倒そう。
3.モリアタを倒すと、「最終ボスの選択」に進むための隠されたルーンの石室の通路が活性化する。
通路をクリックし、1～5段階のボスの中から難易度を選択しよう。

最終ボスの選択
■最終ボスの進め方
1.最終ボスは1～5段階の難易度で構成されており、段階に応じて必要なルーンの石室の鍵が一つずつ増える。(例:第5段階で必要な鍵の数は、5個)
2.ボスを選択すると、最終ボスがいる場所に連れて行ってくれるポータルが生成されるが、このポータルは5分後には消えてしまう。
3.最終ボスは、戦闘開始後5分が経過すると次第に強くなる。

場所
-サウロの軍需基地の入口は、南部カタルラムの第83基地の内部にある。
-自分の種族が基地を占領した場合にのみ入場可能で、入場時は血戦の証3枚が必要となる。
特徴
1.ネームドモンスター
-ネームドモンスターは、全部で9種類が登場し、いずれもミシック級・ヒーロー級・ユニーク級のアイテムをドロップする。
-1,2,4,5,6,9番のネームドモンスターは、必ず倒す必要があり、もし倒せなかった場合は次に進むことができない。
-3,7,8番のネームドモンスターは、倒さなくても次に進むことは可能だが、ミシック級アクセサリーや複合魔石など、より上質なアイテムが入手できなくなる。

2.秘密の鍵の箱
-ダンジョンの各地に秘密の鍵の箱が数多く配置されている。
-秘密の鍵の箱を開けると、ルーンの石室の鍵が獲得できる。なお、この鍵は、最終ボスを選ぶときに使われる。
-必要な数は最大5個であるため、すべての鍵箱を開ける必要はない。
-鍵箱の中にはモンスターに変わるものもある。

3.最終ボスモンスター
-最終ボスは、1～5段階の中から選択できる。
-4段階以上からミシック級アイテムをドロップする。(第4段階:防具、第5段階:武器/防具)
-すべての最終ボスは、戦闘開始から5分が経過すると次第に強くなる。

ドロップアイテム
サウロの軍需基地ではミシック級・ヒーロー級・ユニーク級の武器/防具/アクセサリーを入手することができる。
特に、これらアイテムは追加オプションや強化の制限値、魔石スロットがランダムであり、調整と再調整が可能である。
**/
@InstanceID(301130000)
public class SauroSupplyBaseInstance extends GeneralInstanceHandler
{
	private Map<Integer, StaticDoor> doors;
	private FastMap<Integer, VisibleObject> objects = new FastMap<Integer, VisibleObject>();
	
	public void onDropRegistered(Npc npc) {
		Set<DropItem> dropItems = DropRegistrationService.getInstance().getCurrentDropMap().get(npc.getObjectId());
		int npcId = npc.getNpcId();
		int index = dropItems.size() + 1;
		switch (npcId) {
			case 233258: //Deranak The Reaver.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 188053219, 1)); //[Event] Sauro Commander's Accessory Box.
			break;
			case 230846: //Sauro Base Grave Robber.
				for (Player player: instance.getPlayersInside()) {
				    if (player.isOnline()) {
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188052578, 1)); //Looted Sauro Supplies.
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 186000237, 20)); //Ancient Coin.
					}
				}
			break;
			case 230847: //Mystery Box Key.
				//Be careful in your selection. The key cannot be changed once it is chosen.
				sendMsgByRace(1401946, Race.PC_ALL, 0);
				for (Player player: instance.getPlayersInside()) {
				    if (player.isOnline()) {
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 185000179, 1)); //Danuar Omphanium Key.
					}
				}
			break;
			case 230852: //Commander Ranodim.
				for (Player player: instance.getPlayersInside()) {
				    if (player.isOnline()) {
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188052582, 1)); //Dragon's Conquerer Mark Box.
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 166500000, 1)); //Amplification Stone.
					} switch (Rnd.get(1, 3)) {
				        case 1:
				            dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000176, 1)); //Red Storeroom Key.
				        break;
					    case 2:
				            dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000177, 1)); //Blue Storeroom Key.
				        break;
					    case 3:
				            dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000178, 1)); //Green Storeroom Key.
				        break;
					/*} switch (Rnd.get(1, 2)) {
				        case 1:
				            dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 188053623, 1)); //Fire Dragon King's Weapon Bundle [Mythic].
				        break;
					    case 2:
				            dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 188054244, 1)); //Dreaming Nether Water Dragon King's Weapon Chest [Mythic].
				        break;*/
					}
				}
			break;
			case 230849: //Guard Captain Rohuka.
			case 230850: //Research Teselik.
			case 230851: //Chief Gunner Kurmata.
			case 230853: //Chief Of Staff Moriata.
				for (Player player: instance.getPlayersInside()) {
				    if (player.isOnline()) {
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188052582, 1)); //Dragon's Conquerer Mark Box.
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 166500000, 1)); //Amplification Stone.
					/*} switch (Rnd.get(1, 2)) {
				        case 1:
				            dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 188053623, 1)); //Fire Dragon King's Weapon Bundle [Mythic].
				        break;
					    case 2:
				            dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 188054244, 1)); //Dreaming Nether Water Dragon King's Weapon Chest [Mythic].
				        break;*/
					}
				}
			break;
			case 230857: //Guard Captain Ahuradim.
			case 230858: //Brigade General Sheba.
				for (Player player: instance.getPlayersInside()) {
				    if (player.isOnline()) {
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188053619, 1)); //Ancient Manastone Bundle.
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188053789, 1)); //Major Stigma Support Bundle.
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188052582, 1)); //Dragon's Conquerer Mark Box.
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188053083, 1)); //Tempering Solution Chest.
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188052951, 1)); //[Event] Prestige Supplies.
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 166500000, 1)); //Amplification Stone.
					} switch (Rnd.get(1, 2)) {
				        case 1:
				            dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188053211, 1)); //[Event] Sauro Commander's Weapon Box.
				        break;
					    case 2:
				            dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188053219, 1)); //[Event] Sauro Commander's Accessory Box.
				        break;
					/*} switch (Rnd.get(1, 2)) {
				        case 1:
				            dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 188053623, 1)); //Fire Dragon King's Weapon Bundle [Mythic].
				        break;
					    case 2:
				            dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 188054244, 1)); //Dreaming Nether Water Dragon King's Weapon Chest [Mythic].
				        break;*/
					}
				}
			break;
			case 702658: //Abbey Box.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 188053579, 1)); //[Event] Abbey Bundle.
		    break;
			case 702659: //Noble Abbey Box.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 188053580, 1)); //[Event] Noble Abbey Bundle.
		    break;
			case 802181: //Sauro Supply Base Opportunity Bundle.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 186000051, 1)); //Major Ancient Crown.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 186000052, 1)); //Greater Ancient Crown.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 186000236, 50)); //Blood Mark.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 186000237, 50)); //Ancient Coin.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 186000242, 2)); //Ceramium Medal.
			break;
		}
	}
	
	/**
	 * Bonus Monster: "Sauro Base Grave Robber"
	 * They can appear in "5 different rooms" and give:
	 * Ancient Coins.
	 * Ancient Manastones.
	 * Skins.
     */
	@Override
    public void onInstanceCreate(WorldMapInstance instance) {
        super.onInstanceCreate(instance);
		doors = instance.getDoors();
		//An intruder alarm has sounded. The Sauro Elite Protectorate are gathering.
		sendMsgByRace(1401810, Race.PC_ALL, 10000);
		//The Sauro Elite Protectorate has assembled.
		sendMsgByRace(1401811, Race.PC_ALL, 30000);
		//The Sauro Elite Protectorate approaches.
		sendMsgByRace(1401812, Race.PC_ALL, 50000);
		//The Sauro Elite Protectorate is one minute out.
		sendMsgByRace(1401813, Race.PC_ALL, 70000);
		//The Sauro Elite Protectorate is upon you.
		sendMsgByRace(1401814, Race.PC_ALL, 130000);
		switch (Rnd.get(1, 5)) {
		    case 1:
				spawn(230846, 464.07788f, 401.3575f, 182.15321f, (byte) 10);
			break;
			case 2:
				spawn(230846, 496.30792f, 412.814f, 182.13792f, (byte) 73);
			break;
			case 3:
				spawn(230846, 497.15717f, 392.34656f, 182.14955f, (byte) 75);
			break;
			case 4:
				spawn(230846, 496.2902f, 358.0765f, 182.14955f, (byte) 48);
			break;
			case 5:
				spawn(230846, 464.15985f, 389.7157f, 182.15321f, (byte) 109);
			break;
		}
    }
	
    @Override
    public void onDie(Npc npc) {
        Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
		   /**
			* Area 1: Guardroom And Rune Hall.
		    */
			case 230849: //Guard Captain Rohuka.
				doors.get(383).setOpen(true);
				//The door to the Defiled Danuar Temple has opened.
				sendMsgByRace(1401914, Race.PC_ALL, 0);
			break;
			case 230851: //Chief Gunner Kurmata.
				doors.get(59).setOpen(true);
				//The door to the Danuar Meditation Garden has opened.
				sendMsgByRace(1401915, Race.PC_ALL, 0);
				switch (Rnd.get(1, 2)) {
				    case 1:
				        doors.get(382).setOpen(true);
						//With the gatekeeper down, the door on the left is open!
						sendMsgByRace(1401229, Race.PC_ALL, 5000);
						spawn(230797, 610.7328f, 518.80884f, 191.2776f, (byte) 75); //Sheban Legion Elite Ambusher.
					break;
			        case 2:
						doors.get(387).setOpen(true);
						//With the gatekeeper down, the door on the right is open!
						sendMsgByRace(1401230, Race.PC_ALL, 5000);
						spawn(230797, 611.1872f, 452.91882f, 191.2776f, (byte) 39); //Sheban Legion Elite Ambusher.
					break;
				}
			break;
			
		   /**
			* Area 2: Rune Cloister And Logistic Base.
		    */
			case 230818: //Sheban Legion Elite Gunner.
				doors.get(372).setOpen(true);
				//The door to the Head Researcher's Office has opened.
				sendMsgByRace(1401916, Race.PC_ALL, 0);
			break;
			case 230850: //Research Teselik.
				doors.get(375).setOpen(true);
				//The door to the Lost Tree of Devotion has opened.
				sendMsgByRace(1401917, Race.PC_ALL, 0);
			break;
			
		   /**
			* Area 3: Rune Bridge And Logistic Base Arsenal.
		    */
			case 233255: //Gatekeeper Stranir.
				doors.get(378).setOpen(true);
				//The door to the Sauro Armory has opened.
				sendMsgByRace(1401918, Race.PC_ALL, 0);
			break;
			case 230852: //Commander Ranodim.
				doors.get(388).setOpen(true);
				//The door to the Heavy Storage Area has opened.
				sendMsgByRace(1401919, Race.PC_ALL, 0);
			break;
			
			/**
			 * Area 4: Chiefs Chamber.
		     */
			case 230791: //Sheban Legion Elite Assaulter.
				doors.get(376).setOpen(true);
				//The door to Moriata's Quarters has opened.
				sendMsgByRace(1401920, Race.PC_ALL, 0);
			break;
			case 230853: //Chief Of Staff Moriata.
				//A device leading to the Danuar Omphanium has been activated.
				sendMsgByRace(1401921, Race.PC_ALL, 0);
				//The passage to the Danuar Omphanium will be open for five minutes.
				sendMsgByRace(1401922, Race.PC_ALL, 5000);
				spawn(730872, 127.77696f, 432.75684f, 151.69659f, (byte) 0, 3);
			break;
			
			/**
			 * Area 5: Final Boss.
		     */
			case 230857: //Guard Captain Ahuradim.
				switch (Rnd.get(1, 2)) {
		            case 1:
				        spawn(702658, 703.3344f, 883.07666f, 411.5939f, (byte) 90); //Abbey Box.
			        break;
			        case 2:
				        spawn(702659, 703.3344f, 883.07666f, 411.5939f, (byte) 90); //Noble Abbey Box.
					break;
				}
				spawn(801967, 708.9197f, 884.59625f, 411.57986f, (byte) 45); //Sauro Supply Base Exit.
				spawn(802181, 710.25726f, 889.6806f, 411.59103f, (byte) 0); //Sauro Supply Base Opportunity Bundle.
				sendMsg("[Congratulation]: you finish <Sauro Supply Base>");
			break;
			case 230858: //Brigade General Sheba.
				switch (Rnd.get(1, 2)) {
		            case 1:
				        spawn(702658, 900.2497f, 896.3568f, 411.3568f, (byte) 30); //Abbey Box.
			        break;
			        case 2:
				        spawn(702659, 900.2497f, 896.3568f, 411.3568f, (byte) 30); //Noble Abbey Box.
					break;
				}
				spawn(801967, 905.3781f, 895.2461f, 411.57785f, (byte) 75); //Sauro Supply Base Exit.
				spawn(802181, 906.9721f, 889.6604f, 411.59854f, (byte) 0); //Sauro Supply Base Opportunity Bundle.
				sendMsg("[Congratulation]: you finish <Sauro Supply Base>");
			break;
		}
    }
	
	public void removeItems(Player player) {
        Storage storage = player.getInventory();
        storage.decreaseByItemId(185000176, storage.getItemCountByItemId(185000176)); //Red Storeroom Key.
		storage.decreaseByItemId(185000177, storage.getItemCountByItemId(185000177)); //Blue Storeroom Key.
        storage.decreaseByItemId(185000178, storage.getItemCountByItemId(185000178)); //Green Storeroom Key.
		storage.decreaseByItemId(185000179, storage.getItemCountByItemId(185000179)); //Danuar Stone Room Key.
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
    public boolean onReviveEvent(Player player) {
		player.getGameStats().updateStatsAndSpeedVisually();
		PlayerReviveService.revive(player, 100, 100, false, 0);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
		PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_INSTANT_DUNGEON_RESURRECT, 0, 0));
        return TeleportService2.teleportTo(player, mapId, instanceId, 641.5419f, 176.81075f, 195.65363f, (byte) 28);
    }
	
	@Override
	public boolean onDie(final Player player, Creature lastAttacker) {
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()), true);
		PacketSendUtility.sendPacket(player, new SM_DIE(player.haveSelfRezEffect(), player.haveSelfRezItem(), 0, 8));
		return true;
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
    public void onInstanceDestroy() {
        doors.clear();
    }
}