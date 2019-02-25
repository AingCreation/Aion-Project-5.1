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
import javolution.util.FastList;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import com.aionemu.commons.utils.Rnd;

import com.aionemu.gameserver.ai2.*;
import com.aionemu.gameserver.ai2.manager.*;
import com.aionemu.gameserver.instance.handlers.*;
import com.aionemu.gameserver.controllers.effect.*;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.*;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.model.instance.InstanceScoreType;
import com.aionemu.gameserver.model.instance.instancereward.*;
import com.aionemu.gameserver.model.instance.playerreward.*;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.model.team2.group.PlayerGroupService;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.player.PlayerReviveService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.utils.*;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
Author Rinzler (Encom)
//http://aion.power.plaync.com/wiki/%EC%B2%A0%EB%B2%BD%EC%9D%98+%EB%B3%B4%EB%A3%A8+-+%EC%A7%84%ED%96%89+%EC%A0%95%EB%B3%B4#hd144945

24人の精鋭で行われるミニ守城型インスタンス ダンジョン！
鉄壁の砦は、南部カタラムの南側、ベリトラ軍の陸上浸透路を監視する監視基地であり、最後の関門である。 
ベリトラ軍が関門を通れないように、押し寄せてくる敵を倒し、最後まで生き残り、要塞を守らなければならない。

-入場制限は65レベル以上
-南部カタラムにある第81基地を占領すると、基地内部に入場するためのNPCが登場する。
-24人が参加するフォースIDに集まってくる敵を倒してディフェンスする。
-最後のウェーブで登場する最終ボスを倒せば、それまでにかかった時間と点数に応じてランクがつけられる。
-毎週水曜日午前9時に初期化 
-入場時に血戦の証3個を消費

-基本報酬として、フォースメンバー全員に、スコアに応じて、アビスポイントが付与される。
-ランクに応じた追加報酬として、古代の遺物やセラミウムの功績勲章、ヒーローおよびミシックグレードのアイテムを獲得することができる。

背景ストーリー
鉄壁の砦は、ルーン族がベリトラ領地と南部カタラムとの間に造られた関門であり、一時期は、ベリトラ軍がカタラムに進出する際の主な通路として使われ
たが、天族・魔族がカタラムへ進出してからは、彼らに占領されたまま現在に至っている。
ベリトラ軍は、この重要な関門を取り戻すために、パシッド部隊をつくり、ドレドギオンと大量の物資を動員し、鉄壁の砦を攻略する準備を行っていた。ベリト
ラ軍による大規模な攻撃がまもなく始まるとの噂を聞いた天族・魔族の司令部は、
増員されたベリトラ軍の兵士がカタラムに進出することを防ぐために、勇猛なディーヴァに助けを求め、それに応じてくれたディーヴァと一緒に鉄壁の砦へ向かった。

鉄壁の砦の関門の城壁には、天族・魔族が守城のために設置した大砲を含む設備が整っており、近隣の地域にはパシッド部隊が設置した様々な前進
基地が造られている。鉄壁の砦に進入したディーヴァは、
ベリトラ軍の一斉攻勢を防ぐのはもちろんベリトラ軍の攻城施設を破壊し、パシッド部隊の司令官である総指揮官パシッドを撃退しなければならない。

場所
鉄壁の砦の入口は、南部カタラムにある第81基地の中にある。
自分の種族が基地を占領した場合のみ入場することができ、入場時は、血戦の証3枚が必要となる。
-天族入場NPC: ダイタロス
-魔族入場NPC: シュバンヒルト

獲得アイテム
鉄壁の砦を攻略すると、次のような内容で様々な報酬アイテムが付与される。
また最終的に獲得したランクによって報酬のクオリティが大きく異なり、高いランクに達成した際は、ミシック級の装備アイテムも入手することができる。

1.ランク達成基本報酬
ランクに応じて、フォースメンバー全員が平等に受け取る報酬
アビスポイントやセラミウムの功績勲章、高級製作材料などがこれに当たる。

2.ランク宝箱報酬
ランクに応じて大きさが異なる宝箱が登場し、その中から様々な報酬を入手することができる。
ほとんどの報酬が数量に限りがあるため、他の参加者とサイコロ対決をし、勝った場合のみ獲得することができる。

3.拠点ネームド報酬
一種の追加報酬であり、ID内部の各拠点にあるネームド モンスターを倒せば、ベリトラ軍団の補給箱を入手することができる。
この箱を開けると、ランダムで複合魔石を入手することができる。

ランク: S
92,000点
アビスポイント35,000
セラミウムの功績勲章4個
最上級材料箱
最上級材料支援袋
リジッド スティール ウォール セット-武器or防具 × 1
スティール ウォール セット-武器 × 1
スティール ウォール セット-防具 × 1
関門の戦闘補給箱 × 4
関門 補給魔力付与の袋 × 1
要塞戦の報酬スティグマの袋 × 5
パシッドの戦利品袋 × 1
最上級遺物箱 × 1(フォース ドロップ)

ランク: A
84,000点
アビスポイント25,000
セラミウムの功績勲章2個
最上級材料箱
上級材料支援袋
スティール ウォール セット-武器 × 1
スティール ウォール セット-防具 × 1
関門の戦闘補給箱 × 3
要塞戦の報酬スティグマの袋 × 5
パシッドの戦利品の袋 × 1
上級遺物箱 × 1(フォース ドロップ)

ランク: B
76,000点
アビスポイント15,000
セラミウムの功績勲章1個
上級材料箱
下級材料支援袋
スティール ウォール セット-防具 × 1
関門の戦闘補給箱 × 2
要塞戦の報酬スティグマの袋 × 4
中級遺物箱 × 1(フォース ドロップ)

ランク: C
50,000点
アビスポイント11,000
下級材料支援袋
関門の戦闘補給箱 × 2
下級遺物箱 × 1(フォース ドロップ)

ランク: D
10,000点
アビスポイント7,000 	
関門の戦闘補給箱 × 2
最下級遺物箱 × 1(フォース ドロップ)

ランク: F
10,000点未満

装備アイテムについて
Bランク以上で攻略すると、宝箱の中から装備アイテムが出てくる。
ID攻略用としての使用に最適化されたオプションが付いており、ヒーロー級アイテムも魔石スロット6個を持っている。

1.堅固な鉄壁セット
-Sランクを達成した場合に宝箱から武器/防具のいずれかが出てくる。

2.鉄壁セット
-Aランク以上を達成した場合に宝箱から武器の一つが出てくる。
-Bランク以上を達成した場合に宝箱から防具の一つが出てくる。

補給箱について
鉄壁の砦では2種類の補給箱から魔石袋を入手することができる。
高級オプション付きの複合魔石や古代魔石などが入っている袋であるため、魅力的な報酬と言える。

1.関門戦闘補給箱
-ランクに応じて、宝箱からいくつかの関門の戦闘補給箱が出てくる。
-この箱を開けると、高級オプション付きの複合魔石と古代魔石のいずれかを入手することができる。
-この箱から入手できる古代魔石の中にはPvPオプションが付いているものもある。

2.ベリトラ軍の補給箱
-一種の追加報酬であり、各拠点にあるネームド モンスターを倒せば、ベリトラ軍団の補給箱を入手することができる。
-この箱を開けると、ランダムで複合魔石を入手することができる。

その他セット報酬
他にも、ランクの結果に応じて、以下のような様々な報酬が付与される。

1.関門 補給魔力付与の袋
-Sランクに達成した場合のみ追加報酬として入手できる袋
-袋を開けると「祝福された上級魔力付与:2段階」を入手することができる。

2.要塞戦の報酬スティグマの袋
-カタラム要塞戦の報酬として入手できるスティグマを鉄壁の砦でも入手することができる。
-Bランク以上を達成した場合に何個か出てくる。

3.パシッドの戦利品の袋
-ヒーロークエストである天族総司令官の足の爪、魔族総司令官の角の進行に必要なクエスト アイテム.
-Aランク以上を達成した場合に入手することができる。
**/
@InstanceID(300540000)
public class TheEternalBastionInstance extends GeneralInstanceHandler
{
	private int rank;
	private Race spawnRace;
	private long instanceTime;
	private int dredgionSignalTower;
	private Future<?> instanceTimer;
	private boolean isInstanceDestroyed;
	private Map<Integer, StaticDoor> doors;
	private EternalBastionReward instanceReward;
	private final FastList<Future<?>> bastionTask = FastList.newInstance();
	
	protected EternalBastionPlayerReward getPlayerReward(Integer object) {
		return (EternalBastionPlayerReward) instanceReward.getPlayerReward(object);
	}
	
	@SuppressWarnings("unchecked")
	protected void addPlayerReward(Player player) {
		instanceReward.addPlayerReward(new EternalBastionPlayerReward(player.getObjectId()));
	}
	
	private boolean containPlayer(Integer object) {
		return instanceReward.containPlayer(object);
	}
	
	@Override
	public InstanceReward<?> getInstanceReward() {
		return instanceReward;
	}
	
	@Override
    public void onDropRegistered(Npc npc) {
        Set<DropItem> dropItems = DropRegistrationService.getInstance().getCurrentDropMap().get(npc.getObjectId());
		int npcId = npc.getNpcId();
		int index = dropItems.size() + 1;
        switch (npcId) {
			case 701913: //Bastion's Eternal Treasure S.
			case 701914: //Bastion's Eternal Treasure A.
			case 701915: //Bastion's Eternal Treasure B.
			case 701916: //Bastion's Eternal Treasure C.
			case 701917: //Bastion's Eternal Treasure D.
			    for (Player player: instance.getPlayersInside()) {
				    if (player.isOnline()) {
					    switch (Rnd.get(1, 3)) {
				            case 1:
				                dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188052591, 1)); //Weapon Box Of The Iron Wall Fighter.
				            break;
							case 2:
							    dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188052592, 1)); //Armor Box Of The Iron Wall Fighter.
							break;
							case 3:
							    dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188052593, 1)); //Firm Equipment Support Box Of Iron Wall.
							break;
						}
				    }
				}
			break;
			//Bastion's Eternal Treasure Chest.
			case 801268:
			case 801269:
				for (Player player: instance.getPlayersInside()) {
				    if (player.isOnline()) {
					    dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188052582, 1)); //Dragon's Conquerer Mark Box.
					    dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 166500000, 1)); //Amplification Stone.
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
			case 831328: //Cannon Supplies Box.
			case 831329: //Ammo Box.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 182006996, 10)); //Case Shot.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 182006997, 10)); //Armor-Piercing Shot.
			break;
			case 831330: //Siege Mine.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000137, 1)); //Mobile Turret Key.
			break;
			case 802185: //The Eternal Bastion Opportunity Bundle.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 186000051, 1)); //Major Ancient Crown.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 186000052, 1)); //Greater Ancient Crown.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 186000236, 50)); //Blood Mark.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 186000237, 50)); //Ancient Coin.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 186000242, 2)); //Ceramium Medal.
			break;
        }
    }
	
	@Override
	public void onDie(Npc npc) {
		int points = 0;
		int npcId = npc.getNpcId();
		Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			case 233313: //Pashid Siege Combatant.
			    points = 20;
				despawnNpc(npc);
			break;
			case 231115: //Pashid Siege Soldier.
			case 231116: //Pashid Siege Mage.
			case 233309: //Pashid Siege Ambusher.
			    points = 33;
				despawnNpc(npc);
			break;
			case 233312: //Pashid Siege Healer.
			case 233314: //Pashid Elite Siege Defender.
			case 233315: //Pashid Elite Siege Gunner.
			    points = 36;
				despawnNpc(npc);
			break;
			case 231105: //Pashid Advance Protector.
			case 231106: //Pashid Advance Ambusher.
			case 231107: //Pashid Advance Magus.
			case 231108: //Pashid Advance Gunner.
			case 231109: //Pashid Advance Medic.
			case 231110: //Pashid Strike Fencer.
			case 231111: //Pashid Strike Spellcaster.
			case 231112: //Pashid Strike Marksman.
			case 231113: //Pashid Strike Cavalry.
			case 231114: //Pashid Combat Striker.
			case 231117: //Pashid Elite Siege Combatant.
			case 231118: //Pashid Elite Siege Protector.
			case 231119: //Pashid Elite Siege Ambusher.
			case 231120: //Pashid Elite Siege Troublemaker.
			case 231121: //Pashid Elite Siege Scout.
			case 231122: //Pashid Elite Siege Marksman.
			case 231123: //Pashid Elite Siege Rampager.
			case 231124: //Pashid Elite Siege Magus.
			case 231125: //Pashid Elite Siege Summoner.
			case 231126: //Pashid Elite Siege Cavalry.
			case 231127: //Pashid Elite Siege Striker.
			case 231128: //Pashid Elite Siege Medic.
			case 233310: //Pashid Siege Cavalry.
			case 233311: //Pashid Siege Engineer.
			    points = 42;
				despawnNpc(npc);
			break;
			case 230782: //Pashid Army Barricade.
			case 231149: //Pashid Army Barricade.
			case 231181: //Pashid Army Barricade.
			    points = 266;
				despawnNpc(npc);
			break;
			case 230784: //Pashid Snare Turret.
			case 230785: //Pashid Assault Flamethrower.
			case 231137: //Pashid Danuar Turret.
			case 231138: //Pashid Danuar Turret.
			case 231140: //Pashid Assault Pod.
			case 231141: //Pashid Siege Drop Pod.
			case 231143: //Pashid Siege Tower.
			case 231144: //Pashid Siege Cannon.
			case 231151: //Pashid Siege Flamethrower.
			case 231152: //Pashid Siege Tower.
			case 231153: //Pashid Siege Tower.
			case 231154: //Pashid Siege Tower.
			case 231155: //Pashid Siege Tower.
			case 231156: //Pashid Assault Pod.
			case 231157: //Pashid Assault Pod.
			case 231158: //Pashid Assault Pod.
			case 231159: //Pashid Assault Pod.
			case 231160: //Pashid Assault Pod.
			case 231161: //Pashid Assault Pod.
			case 231162: //Pashid Assault Pod.
			case 231148: //Dredgion Signal Tower I.
			case 231180: //Dredgion Signal Tower II.
			    points = 334;
				despawnNpc(npc);
			break;
			case 230744: //Pashid Assault Tribuni Combatant.
			case 230745: //Pashid Assault Tribuni Protector.
			case 230746: //Pashid Assault Tribuni Sentry.
			case 230749: //Pashid Assault Tribuni Marksman
			case 230753: //Pashid Assault Rider.
			case 230754: //Pashid Assault Gunner.
			case 230756: //Pashid Assault Supply Officer.
			case 230757: //Pashid Assault Dragon.
			case 231131: //Pashid Siege Dragon.
			case 231132: //Pashid Siege Dragon.
			case 231133: //Pashid Siege Dragon.
			case 231134: //Pashid Siege Dragon.
			case 231135: //Pashid Siege Dragon.
			    points = 1002;
				despawnNpc(npc);
			break;
			case 231174: //Pashid Artillery Commander Murat.
			case 231175: //Pashid Artillery Commander Kaimdu.
			case 231176: //Pashid Infantry Commander Nirta.
			    points = 1880;
				despawnNpc(npc);
				startPashidSiegeTower();
			break;
			case 231168: //Pashid Scout Commander Azute.
			case 231169: //Pashid Scout Commander Zest.
			case 231170: //Pashid Scout Commander Sartas.
			case 231171: //Pashid Infantry Commander Matuk.
			case 231172: //Pashid Assault Commander Badute.
			case 231173: //Pashid Assault Commander Katsu.
			    points = 1880;
				despawnNpc(npc);
			break;
			case 231177: //Deathbringer Tariksha.
			case 231178: //Commander Hakunta.
			case 231179: //Commander Rakunta.
			    points = 1880;
				despawnNpc(npc);
			break;
			case 231130: //Grand Commander Pashid.
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
					    instance.doOnAllPlayers(new Visitor<Player>() {
						    @Override
						    public void visit(Player player) {
							    stopInstance(player);
						    }
					    });
					}
				}, 5000);
				points = 24000;
				despawnNpc(npc);
				if (checkRank(instanceReward.getPoints()) == 1) {
                    spawn(701913, 744.06085f, 293.31564f, 233.70102f, (byte) 104); //Bastion's Eternal Treasure Chest S.
					spawn(802185, 749.53143f, 294.72095f, 233.88902f, (byte) 5); //The Eternal Bastion Opportunity Bundle.
					spawn(801268, 745.52191f, 329.83740f, 233.81584f, (byte) 0, 330); //Supply Box.
					spawn(801269, 748.98627f, 331.62048f, 233.79378f, (byte) 0, 331); //Supply Box.
                } else if (checkRank(instanceReward.getPoints()) == 2) {
                    spawn(701914, 744.06085f, 293.31564f, 233.70102f, (byte) 104); //Bastion's Eternal Treasure Chest A.
					spawn(802185, 749.53143f, 294.72095f, 233.88902f, (byte) 5); //The Eternal Bastion Opportunity Bundle.
					spawn(801268, 745.52191f, 329.83740f, 233.81584f, (byte) 0, 330); //Supply Box.
					spawn(801269, 748.98627f, 331.62048f, 233.79378f, (byte) 0, 331); //Supply Box.
                } else if (checkRank(instanceReward.getPoints()) == 3) {
                    spawn(701915, 744.06085f, 293.31564f, 233.70102f, (byte) 104); //Bastion's Eternal Treasure Chest B.
                } else if (checkRank(instanceReward.getPoints()) == 4) {
                    spawn(701916, 744.06085f, 293.31564f, 233.70102f, (byte) 104); //Bastion's Eternal Treasure Chest C.
                } else if (checkRank(instanceReward.getPoints()) == 5) {
                    spawn(701917, 744.06085f, 293.31564f, 233.70102f, (byte) 104); //Bastion's Eternal Treasure Chest D.
                }
				final int bastionExit2 = spawnRace == Race.ASMODIANS ? 730882 : 730871;
				spawn(bastionExit2, 767.1659f, 264.31552f, 233.49748f, (byte) 43); //The Eternal Bastion Exit.
			break;
		} if (instanceReward.getInstanceScoreType().isStartProgress()) {
			instanceReward.addNpcKill();
			instanceReward.addPoints(points);
			sendPacket(npc.getObjectTemplate().getNameId(), points);
		} switch (npcId) {
			case 209554: //Lysander's Disciple.
            case 209555: //Lysander's Disciple.
            case 209556: //Granir's Disciple.
            case 209557: //Granir's Disciple.
                despawnNpc(npc);
				instanceReward.addPoints(-50);
			break;
			case 831332: //Right Castle Gate.
			case 831333: //Left Castle Gate.
			case 831334: //Outer Water Gate.
			    //The Bastion has been breached. The Pashid Legion is flooding through the hole.
				sendMsgByRace(1401826, Race.PC_ALL, 0);
			    despawnNpc(npc);
				instanceReward.addPoints(-150);
			break;
			case 831335: //Inner Water Gate.
			    //The Pashid Legion has destroyed the gate at the underground wateray.
				sendMsgByRace(1401824, Race.PC_ALL, 0);
                despawnNpc(npc);
				instanceReward.addPoints(-150);
            break;
			case 209516: //Commander Lysander.
            case 209517: //Commander Granir.
				despawnNpc(npc);
				stopInstance(player);
				instanceReward.addPoints(-90000);
				final int bastionExit1 = spawnRace == Race.ASMODIANS ? 730882 : 730871;
                spawn(bastionExit1, 767.1659f, 264.31552f, 233.49748f, (byte) 43); //The Eternal Bastion Exit.
			break;
		}
	}
	
	private void removeItems(Player player) {
		Storage storage = player.getInventory();
		storage.decreaseByItemId(185000137, storage.getItemCountByItemId(185000137)); //Aetheric Power Crystal.
		storage.decreaseByItemId(182006996, storage.getItemCountByItemId(182006996)); //Case Shot.
		storage.decreaseByItemId(182006997, storage.getItemCountByItemId(182006997)); //Armor-Piercing Shot.
	}
	
	@Override
	public void handleUseItemFinish(Player player, Npc npc) {
		switch (npc.getNpcId()) {
			case 701625: //Lysander's Siege Cannon.
				despawnNpc(npc);
				SkillEngine.getInstance().getSkill(npc, 21065, 60, player).useNoAnimationSkill();
			break;
			case 701922: //Granir's Siege Cannon.
				despawnNpc(npc);
				SkillEngine.getInstance().getSkill(npc, 21066, 60, player).useNoAnimationSkill();
			break;
		}
	}
	
	protected void startInstanceTask() {
	    instanceTime = System.currentTimeMillis();
        instanceReward.setInstanceStartTime();
		bastionTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
			    startAssaultPod2();
				startRaidBastion1();
            }
        }, 120000)); //2 Minutes.
		bastionTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
			    //The Pashid Legion's 2nd Siege Troop is attacking the Bastion's gates.
				sendMsgByRace(1401816, Race.PC_ALL, 2000);
				//Another assault machine has been hit and will crash within the Bastion's wall.
				sendMsgByRace(1401821, Race.PC_ALL, 5000);
				startRaidBastion2();
            }
        }, 300000)); //5 Minutes.
		bastionTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
			    //The Pashid Legion's 3rd Siege Troop is attacking the Bastion's gates.
				sendMsgByRace(1401817, Race.PC_ALL, 2000);
				//Another assault machine has been hit and will crash within the Bastion's wall.
				sendMsgByRace(1401822, Race.PC_ALL, 5000);
				startAssaultPod3();
				startRaidBastion3();
            }
        }, 480000)); //8 Minutes.
		bastionTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
				//The Pashid Legion's Sheban Siege Troop is attacking the Bastion's gates.
				sendMsgByRace(1401818, Race.PC_ALL, 2000);
				startAssaultPod4();
				startRaidBastion4();
            }
        }, 660000)); //11 Minutes.
		bastionTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
				//The Eternal Bastion defenders have withdrawn in preparation of Pashid's assault.
				sendMsgByRace(1401939, Race.PC_ALL, 2000);
				//The commander of the garrison has been killed. The assault force is no longer coordinated and is in retreat.
				sendMsgByRace(1401940, Race.PC_ALL, 5000);
				startRaidBastion6();
            }
        }, 840000)); //14 Minutes.
		bastionTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
				startRaidBastion7();
            }
        }, 1020000)); //17 Minutes.
		bastionTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
				//Grand Commander Pashid has arrived with the Guard to assault the fortress.
				sendMsgByRace(1401819, Race.PC_ALL, 0);
				//The Governor is under attack.
				sendMsgByRace(1401827, Race.PC_ALL, 5000);
				spawn(231130, 744.06085f, 293.31564f, 233.70102f, (byte) 104); //Grand Commander Pashid.
            }
        }, 1200000)); //20 Minutes.
	}
	
	private void SpawnRaceInstance() {
		//** Siege Weapon **//
		final int siegeweapon1 = spawnRace == Race.ASMODIANS ? 701610 : 701596;
        final int siegeweapon2 = spawnRace == Race.ASMODIANS ? 701611 : 701597;
        final int siegeweapon3 = spawnRace == Race.ASMODIANS ? 701612 : 701598;
        final int siegeweapon4 = spawnRace == Race.ASMODIANS ? 701613 : 701599;
        final int siegeweapon5 = spawnRace == Race.ASMODIANS ? 701614 : 701600;
        final int siegeweapon6 = spawnRace == Race.ASMODIANS ? 701615 : 701601;
		final int siegeweapon7 = spawnRace == Race.ASMODIANS ? 701616 : 701602;
		final int siegeweapon8 = spawnRace == Race.ASMODIANS ? 701617 : 701603;
		final int siegeweapon9 = spawnRace == Race.ASMODIANS ? 701618 : 701604;
		final int siegeweapon10 = spawnRace == Race.ASMODIANS ? 701619 : 701605;
		final int siegeweapon11 = spawnRace == Race.ASMODIANS ? 701620 : 701606;
		final int siegeweapon12 = spawnRace == Race.ASMODIANS ? 701621 : 701607;
		final int siegeweapon13 = spawnRace == Race.ASMODIANS ? 702922 : 701625;
		spawn(siegeweapon1, 617.95416f, 248.32031f, 235.74449f, (byte) 63);
		spawn(siegeweapon2, 613.11914f, 275.30057f, 235.74294f, (byte) 64);
		spawn(siegeweapon3, 616.4774f, 313.85846f, 235.74289f, (byte) 52);
		spawn(siegeweapon4, 625.97675f, 339.55414f, 235.7432f, (byte) 54);
		spawn(siegeweapon5, 651.3247f, 373.3068f, 238.60867f, (byte) 44);
		spawn(siegeweapon6, 678.08124f, 396.04736f, 238.63474f, (byte) 43);
		spawn(siegeweapon7, 710.27765f, 409.9322f, 241.02042f, (byte) 31);
		spawn(siegeweapon8, 737.3579f, 413.3636f, 241.02278f, (byte) 33);
		spawn(siegeweapon9, 772.7887f, 410.0723f, 241.02089f, (byte) 6);
		spawn(siegeweapon10, 798.2277f, 400.5876f, 241.02304f, (byte) 38);
		spawn(siegeweapon11, 709.54443f, 313.67133f, 254.21622f, (byte) 103);
		spawn(siegeweapon12, 726.6982f, 328.01038f, 254.21628f, (byte) 103);
		spawn(siegeweapon13, 640.8445f, 412.9476f, 243.93938f, (byte) 103);
		//** Lysander's/Granir's Disciple **//
		final int disciple1 = spawnRace == Race.ASMODIANS ? 209556 : 209554;
        final int disciple2 = spawnRace == Race.ASMODIANS ? 209557 : 209555;
		spawn(disciple1, 687.8377f, 350.53018f, 244.65965f, (byte) 43);
		spawn(disciple2, 694.3164f, 355.77338f, 244.68953f, (byte) 43);
		//** Beritran Chariot **//
		final int beritranChariot = spawnRace == Race.ASMODIANS ? 702589 : 701624;
		spawn(beritranChariot, 414.05554f, 616.8139f, 214.52452f, (byte) 31);
        spawn(beritranChariot, 410.86f, 640.4919f, 214.52452f, (byte) 92);
        spawn(beritranChariot, 422.98706f, 641.44116f, 214.52452f, (byte) 92);
        spawn(beritranChariot, 426.4476f, 617.95264f, 214.52452f, (byte) 32);
		//** Commander **//
		final int commander = spawnRace == Race.ASMODIANS ? 209517 : 209516;
        spawn(commander, 748.7025f, 287.65768f, 233.81223f, (byte) 44);
    }
	
	private void moveToForward(final Npc npc, float x, float y, float z, boolean despawn) {
		((AbstractAI) npc.getAi2()).setStateIfNot(AIState.WALKING);
		npc.setState(1);
		npc.getMoveController().moveToPoint(x, y, z);
		PacketSendUtility.broadcastPacket(npc, new SM_EMOTION(npc, EmotionType.START_EMOTE2, 0, npc.getObjectId()));
	}
	
   /**
	* Assault Pod.
	*/
	private void startAssaultPod1() {
		//The Pashid Legion's 1st Siege Troop is attacking the Bastion's gates.
		sendMsgByRace(1401815, Race.PC_ALL, 0);
		//One of the assault machines is faltering and will collapse within the Eternal Bastion.
		sendMsgByRace(1401820, Race.PC_ALL, 2000);
		//The Pashid Legion is attacking the underground waterway.
		sendMsgByRace(1401823, Race.PC_ALL, 6000);
		spawn(231156, 753.4488f, 296.26138f, 233.75148f, (byte) 67);
        spawn(231156, 741.1732f, 302.49472f, 233.75148f, (byte) 96);
		spawn(231105, 750.00336f, 295.08008f, 233.88875f, (byte) 69);
		spawn(231106, 742.04126f, 299.33875f, 233.85815f, (byte) 96);
		//FXMon_Smoke.
		spawn(297352, 753.4488f, 296.26138f, 233.75148f, (byte) 67);
		spawn(297352, 741.1732f, 302.49472f, 233.75148f, (byte) 96);
	}
	private void startAssaultPod2() {
		spawn(231157, 706.7695f, 261.6263f, 253.43394f, (byte) 40);
		//FXMon_Smoke.
		spawn(297352, 706.7695f, 261.6263f, 253.43394f, (byte) 40);
	}
	private void startAssaultPod3() {
		spawn(231158, 699.1482f, 308.8426f, 249.30322f, (byte) 103);
		//FXMon_Smoke.
		spawn(297352, 699.1482f, 308.8426f, 249.30322f, (byte) 103);
	}
	private void startAssaultPod4() {
		spawn(231159, 626.0661f, 294.24414f, 238.0753f, (byte) 23);
		spawn(231160, 754.409f, 400.14343f, 243.35422f, (byte) 63);
		//FXMon_Smoke.
		spawn(297352, 626.0661f, 294.24414f, 238.0753f, (byte) 23);
		spawn(297352, 754.409f, 400.14343f, 243.35422f, (byte) 63);
	}
	
   /**
	* Pashid Raid Siege Tower.
	*/
	public void startPashidSiegeTower() {
	    spawn(231143, 628.07947f, 350.28632f, 226.0f, (byte) 113);
        spawn(231143, 610.7189f, 303.6627f, 226.375f, (byte) 113);
        spawn(231143, 698.67365f, 410.3066f, 231.0f, (byte) 93);
        spawn(231143, 668.7155f, 401.9532f, 228.46445f, (byte) 73);
        spawn(231143, 647.66833f, 384.26685f, 228.50421f, (byte) 13);
		spawn(231110, 661.85333f, 397.61002f, 240.22342f, (byte) 84);
		spawn(231111, 650.91345f, 391.36047f, 240.22342f, (byte) 113);
		spawn(231112, 636.0446f, 345.1452f, 238.07559f, (byte) 7);
		spawn(231113, 620.0381f, 301.13055f, 238.07529f, (byte) 8);
		spawn(231114, 701.09595f, 399.9889f, 243.35422f, (byte) 82);
	}
	
   /**
	* Raid Assault.
	*/
	public void startRaidBastion1() {
	    moveToForward((Npc)spawn(231105, 706.402f, 265.68735f, 253.43398f, (byte) 39), 698.8748f, 287.8586f, 253.42f, false);
		moveToForward((Npc)spawn(231106, 706.402f, 265.68735f, 253.43398f, (byte) 39), 698.8748f, 287.8586f, 253.42f, false);
		moveToForward((Npc)spawn(231107, 706.402f, 265.68735f, 253.43398f, (byte) 39), 698.8748f, 287.8586f, 253.42f, false);
		moveToForward((Npc)spawn(231108, 706.402f, 265.68735f, 253.43398f, (byte) 39), 698.8748f, 287.8586f, 253.42f, false);
		moveToForward((Npc)spawn(231109, 706.402f, 265.68735f, 253.43398f, (byte) 39), 698.8748f, 287.8586f, 253.42f, false);
	}
	public void startRaidBastion2() {
	    moveToForward((Npc)spawn(231110, 810.351f, 348.20648f, 230.98207f, (byte) 73), 744.66473f, 293.50308f, 233.7125f, false);
		moveToForward((Npc)spawn(231111, 810.351f, 348.20648f, 230.98207f, (byte) 73), 744.66473f, 293.50308f, 233.7125f, false);
		moveToForward((Npc)spawn(231112, 810.351f, 348.20648f, 230.98207f, (byte) 73), 744.66473f, 293.50308f, 233.7125f, false);
		moveToForward((Npc)spawn(231113, 810.351f, 348.20648f, 230.98207f, (byte) 73), 744.66473f, 293.50308f, 233.7125f, false);
		moveToForward((Npc)spawn(231114, 810.351f, 348.20648f, 230.98207f, (byte) 73), 744.66473f, 293.50308f, 233.7125f, false);
		switch (Rnd.get(1, 3)) {
		    case 1:
		        moveToForward((Npc)spawn(231168, 810.351f, 348.20648f, 230.98207f, (byte) 73), 744.66473f, 293.50308f, 233.7125f, false); //Pashid Scout Commander Azute.
		    break;
			case 2:
				moveToForward((Npc)spawn(231169, 810.351f, 348.20648f, 230.98207f, (byte) 73), 744.66473f, 293.50308f, 233.7125f, false); //Pashid Scout Commander Zest.
		    break;
			case 3:
			    moveToForward((Npc)spawn(231170, 810.351f, 348.20648f, 230.98207f, (byte) 73), 744.66473f, 293.50308f, 233.7125f, false); //Pashid Scout Commander Sartas.
		    break;
		}
	}
	public void startRaidBastion3() {
	    moveToForward((Npc)spawn(231105, 701.2387f, 305.8705f, 249.30322f, (byte) 103), 713.0358f, 288.13544f, 249.28407f, false);
		moveToForward((Npc)spawn(231106, 701.2387f, 305.8705f, 249.30322f, (byte) 103), 713.0358f, 288.13544f, 249.28407f, false);
		moveToForward((Npc)spawn(231107, 701.2387f, 305.8705f, 249.30322f, (byte) 103), 713.0358f, 288.13544f, 249.28407f, false);
		moveToForward((Npc)spawn(231108, 701.2387f, 305.8705f, 249.30322f, (byte) 103), 713.0358f, 288.13544f, 249.28407f, false);
		moveToForward((Npc)spawn(231109, 701.2387f, 305.8705f, 249.30322f, (byte) 103), 713.0358f, 288.13544f, 249.28407f, false);
		switch (Rnd.get(1, 3)) {
		    case 1:
		        moveToForward((Npc)spawn(231171, 701.2387f, 305.8705f, 249.30322f, (byte) 103), 713.0358f, 288.13544f, 249.28407f, false); //Pashid Infantry Commander Matuk.
		    break;
			case 2:
				moveToForward((Npc)spawn(231172, 701.2387f, 305.8705f, 249.30322f, (byte) 103), 713.0358f, 288.13544f, 249.28407f, false); //Pashid Assault Commander Badute.
		    break;
			case 3:
			    moveToForward((Npc)spawn(231173, 701.2387f, 305.8705f, 249.30322f, (byte) 103), 713.0358f, 288.13544f, 249.28407f, false); //Pashid Assault Commander Katsu.
		    break;
		}
	}
	public void startRaidBastion4() {
	    moveToForward((Npc)spawn(231110, 626.0661f, 294.24414f, 238.0753f, (byte) 23), 673.7405f, 372.1047f, 241.59521f, false);
		moveToForward((Npc)spawn(231111, 626.0661f, 294.24414f, 238.0753f, (byte) 23), 673.7405f, 372.1047f, 241.59521f, false);
		moveToForward((Npc)spawn(231112, 626.0661f, 294.24414f, 238.0753f, (byte) 23), 673.7405f, 372.1047f, 241.59521f, false);
		moveToForward((Npc)spawn(231113, 626.0661f, 294.24414f, 238.0753f, (byte) 23), 673.7405f, 372.1047f, 241.59521f, false);
		moveToForward((Npc)spawn(231114, 626.0661f, 294.24414f, 238.0753f, (byte) 23), 673.7405f, 372.1047f, 241.59521f, false);
		moveToForward((Npc)spawn(231110, 750.2154f, 399.5554f, 243.35422f, (byte) 62), 673.7405f, 372.1047f, 241.59521f, false);
		moveToForward((Npc)spawn(231111, 750.2154f, 399.5554f, 243.35422f, (byte) 62), 673.7405f, 372.1047f, 241.59521f, false);
		moveToForward((Npc)spawn(231112, 750.2154f, 399.5554f, 243.35422f, (byte) 62), 673.7405f, 372.1047f, 241.59521f, false);
		moveToForward((Npc)spawn(231113, 750.2154f, 399.5554f, 243.35422f, (byte) 62), 673.7405f, 372.1047f, 241.59521f, false);
		moveToForward((Npc)spawn(231114, 750.2154f, 399.5554f, 243.35422f, (byte) 62), 673.7405f, 372.1047f, 241.59521f, false);
		switch (Rnd.get(1, 3)) {
		    case 1:
		        moveToForward((Npc)spawn(231174, 626.0661f, 294.24414f, 238.0753f, (byte) 23), 673.7405f, 372.1047f, 241.59521f, false); //Pashid Artillery Commander Murat.
		    break;
			case 2:
				moveToForward((Npc)spawn(231175, 626.0661f, 294.24414f, 238.0753f, (byte) 23), 673.7405f, 372.1047f, 241.59521f, false); //Pashid Artillery Commander Kaimdu.
		    break;
			case 3:
			    moveToForward((Npc)spawn(231176, 626.0661f, 294.24414f, 238.0753f, (byte) 23), 673.7405f, 372.1047f, 241.59521f, false); //Pashid Infantry Commander Nirta.
		    break;
		}
	}
	public void startRaidBastion6() {
	    moveToForward((Npc)spawn(231110, 810.351f, 348.20648f, 230.98207f, (byte) 73), 744.66473f, 293.50308f, 233.7125f, false);
		moveToForward((Npc)spawn(231111, 810.351f, 348.20648f, 230.98207f, (byte) 73), 744.66473f, 293.50308f, 233.7125f, false);
		moveToForward((Npc)spawn(231112, 810.351f, 348.20648f, 230.98207f, (byte) 73), 744.66473f, 293.50308f, 233.7125f, false);
		moveToForward((Npc)spawn(231113, 810.351f, 348.20648f, 230.98207f, (byte) 73), 744.66473f, 293.50308f, 233.7125f, false);
		moveToForward((Npc)spawn(231114, 810.351f, 348.20648f, 230.98207f, (byte) 73), 744.66473f, 293.50308f, 233.7125f, false);
	}
	public void startRaidBastion7() {
	    moveToForward((Npc)spawn(231110, 810.351f, 348.20648f, 230.98207f, (byte) 73), 744.66473f, 293.50308f, 233.7125f, false);
		moveToForward((Npc)spawn(231111, 810.351f, 348.20648f, 230.98207f, (byte) 73), 744.66473f, 293.50308f, 233.7125f, false);
		moveToForward((Npc)spawn(231112, 810.351f, 348.20648f, 230.98207f, (byte) 73), 744.66473f, 293.50308f, 233.7125f, false);
		moveToForward((Npc)spawn(231113, 810.351f, 348.20648f, 230.98207f, (byte) 73), 744.66473f, 293.50308f, 233.7125f, false);
		moveToForward((Npc)spawn(231114, 810.351f, 348.20648f, 230.98207f, (byte) 73), 744.66473f, 293.50308f, 233.7125f, false);
	}
	
	private void removeEffects(Player player) {
		PlayerEffectController effectController = player.getEffectController();
		effectController.removeEffect(21065);
		effectController.removeEffect(21066);
		effectController.removeEffect(21141);
	}
	
	@Override
	public void onLeaveInstance(Player player) {
		stopInstanceTask();
		removeItems(player);
		removeEffects(player);
		if (player.isInGroup2()) {
            PlayerGroupService.removePlayer(player);
        }
		//"Player Name" has left the battle.
		PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400255, player.getName()));
	}
	
	@Override
	public void onPlayerLogOut(Player player) {
		removeItems(player);
		removeEffects(player);
	}
	
	private int getTime() {
		long result = System.currentTimeMillis() - instanceTime;
		if (result < 60000) { //60 Secondes.
			return (int) (60000 - result);
		} else if (result < 1800000) { //30 Minutes.
			return (int) (1800000 - (result - 60000)); //60 Secondes.
		}
		return 0;
	}
	
	private void sendPacket(final int nameId, final int point) {
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (nameId != 0) {
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400237, new DescriptionId(nameId * 2 + 1), point));
				}
				PacketSendUtility.sendPacket(player, new SM_INSTANCE_SCORE(getTime(), instanceReward, null));
			}
		});
	}
	
	private int checkRank(int totalPoints) {
		if (totalPoints > 90000) { //Rank S.
			rank = 1;
		} else if (totalPoints > 82000) { //Rank A.
			rank = 2;
		} else if (totalPoints > 60000) { //Rank B.
			rank = 3;
		} else if (totalPoints > 30000) { //Rank C.
			rank = 4;
		} else if (totalPoints > 5000) { //Rank D.
			rank = 5;
		} else if (totalPoints >= 2000) { //Rank F.
			rank = 8;
		} else {
			rank = 8;
		}
	    return rank;
	}
	
	protected void stopInstance(Player player) {
		stopInstanceTask();
        instanceReward.setRank(6);
		instanceReward.setRank(checkRank(instanceReward.getPoints()));
		instanceReward.setInstanceScoreType(InstanceScoreType.END_PROGRESS);
		doReward(player);
		sendMsg("[Congratulation]: you finish <The Eternal Bastion>");
		sendPacket(0, 0);
	}
	
	private void stopInstanceTask() {
        for (FastList.Node<Future<?>> n = bastionTask.head(), end = bastionTask.tail(); (n = n.getNext()) != end; ) {
            if (n.getValue() != null) {
                n.getValue().cancel(true);
            }
        }
    }
	
	private void rewardGroup() {
		for (Player p: instance.getPlayersInside()) {
			doReward(p);
		}
	}
	
	@Override
	public void doReward(Player player) {
		EternalBastionPlayerReward playerReward = getPlayerReward(player.getObjectId());
		if (!playerReward.isRewarded()) {
			playerReward.setRewarded();
			int bastionRank = instanceReward.getRank();
			switch (bastionRank) {
				case 1: //Rank S
					playerReward.setScoreAP(35000);
					playerReward.setCeramium(4);
					ItemService.addItem(player, 186000242, 4);
					playerReward.setHighestGradeMaterialBox(1);
					ItemService.addItem(player, 188052594, 1);
					playerReward.setHighestGradeMaterialSupportBundle(1);
					ItemService.addItem(player, 188052596, 1);
				break;
				case 2: //Rank A
					playerReward.setScoreAP(25000);
					playerReward.setCeramium(2);
					ItemService.addItem(player, 186000242, 2);
					playerReward.setHighestGradeMaterialBox(1);
					ItemService.addItem(player, 188052594, 1);
					playerReward.setHighGradeMaterialSupportBundle(1);
					ItemService.addItem(player, 188052597, 1);
				break;
				case 3: //Rank B
					playerReward.setScoreAP(15000);
					playerReward.setCeramium(1);
					ItemService.addItem(player, 186000242, 1);
					playerReward.setHighGradeMaterialBox(1);
					ItemService.addItem(player, 188052595, 1);
					playerReward.setLowGradeMaterialSupportBundle(1);
					ItemService.addItem(player, 188052598, 1);
				break;
				case 4: //Rank C
					playerReward.setScoreAP(11000);
					playerReward.setLowGradeMaterialSupportBundle(1);
					ItemService.addItem(player, 188052598, 1);
				break;
				case 5: //Rank D
				    playerReward.setScoreAP(7000);
				break;
				case 6: //Rank F
				break;
			}
			AbyssPointsService.addAp(player, playerReward.getScoreAP());
		}
	}
	
	private boolean canStart() {
		if (instance.getPlayersInside().size() < 2) {
			onInstanceDestroy();
			//Unavailable to use when you're alone.
			sendMsgByRace(1403045, Race.PC_ALL, 0);
		    instanceReward.setInstanceScoreType(InstanceScoreType.END_PROGRESS);
		    sendPacket(0, 0);
			return false;
		}
		return true;
	}
	
	@Override
	public void onEnterInstance(final Player player) {
		if (!instanceReward.containPlayer(player.getObjectId())) {
			addPlayerReward(player);
		}
		EternalBastionPlayerReward playerReward = getPlayerReward(player.getObjectId());
		if (playerReward.isRewarded()) {
			doReward(player);
		} if (instanceTimer == null) {
			instanceTime = System.currentTimeMillis();
			instanceTimer = ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					if (!instanceReward.isRewarded() && canStart()) {
					    deleteNpc(831334); //Outer Water Gate.
						startAssaultPod1();
					    doors.get(311).setOpen(true);
						instanceReward.addPoints(20000);
					    //The member recruitment window has passed. You cannot recruit any more members.
					    sendMsgByRace(1401181, Race.PC_ALL, 5000);
					    instanceReward.setInstanceScoreType(InstanceScoreType.START_PROGRESS);
					    sendPacket(0, 0);
					}
				}
			}, 60000); //60 Secondes.
		} if (spawnRace == null) {
			spawnRace = player.getRace();
			SpawnRaceInstance();
		}
		sendPacket(0, 0);
	}
	
	protected void despawnNpc(Npc npc) {
        if (npc != null) {
            npc.getController().onDelete();
        }
    }
	
	private void deleteNpc(int npcId) {
		if (getNpc(npcId) != null) {
			getNpc(npcId).getController().onDelete();
		}
	}
	
	@Override
	public void onInstanceDestroy() {
		if (instanceTimer != null) {
			instanceTimer.cancel(false);
		}
		isInstanceDestroyed = true;
		stopInstanceTask();
		doors.clear();
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
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		instanceReward = new EternalBastionReward(mapId, instanceId);
		instanceReward.setInstanceScoreType(InstanceScoreType.PREPARING);
		doors = instance.getDoors();
		startInstanceTask();
		switch (Rnd.get(1, 2)) {
			case 1:
				spawn(230746, 552.5082f, 414.074f, 222.75688f, (byte) 17); //Pashid Assault Tribuni Sentry.
				spawn(231177, 820.55133f, 606.02814f, 239.70607f, (byte) 20); //Deathbringer Tariksha.
			break;
			case 2:
			    spawn(231177, 552.5082f, 414.074f, 222.75688f, (byte) 17); //Deathbringer Tariksha.
				spawn(230746, 820.55133f, 606.02814f, 239.70607f, (byte) 20); //Pashid Assault Tribuni Sentry.
			break;
		}
	}
	
	@Override
    public boolean onReviveEvent(Player player) {
		player.getGameStats().updateStatsAndSpeedVisually();
		PlayerReviveService.revive(player, 100, 100, false, 0);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
		PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_INSTANT_DUNGEON_RESURRECT, 0, 0));
        return TeleportService2.teleportTo(player, mapId, instanceId, 743.9309f, 293.51474f, 233.71062f, (byte) 105);
    }
	
	@Override
	public boolean onDie(final Player player, Creature lastAttacker) {
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()), true);
		PacketSendUtility.sendPacket(player, new SM_DIE(player.haveSelfRezEffect(), player.haveSelfRezItem(), 0, 8));
		return true;
	}
}