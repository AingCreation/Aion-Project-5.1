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

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AbstractAI;
import com.aionemu.gameserver.controllers.effect.PlayerEffectController;
import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.actions.NpcActions;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.player.PlayerReviveService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.*;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(300520000)
public class DragonLordRefugeInstance extends GeneralInstanceHandler
{
	private int tiamatBuff;
	protected boolean isInstanceDestroyed = false;
	private List<Integer> movies = new ArrayList<Integer>();
	private FastMap<Integer, VisibleObject> objects = new FastMap<Integer, VisibleObject>();
	
	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		spawn(800429, 496.42648f, 516.493f, 240.26653f, (byte) 0); //Kahrun (Reian Leader).
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				spawnTiamatWomanForm();
			}
		}, 180000);
	}
	
	public void onDropRegistered(Npc npc) {
		Set<DropItem> dropItems = DropRegistrationService.getInstance().getCurrentDropMap().get(npc.getObjectId());
		int npcId = npc.getNpcId();
		int index = dropItems.size() + 1;
		switch (npcId) {
			case 702658: //Abbey Box.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 188053579, 1)); //[Event] Abbey Bundle.
		    break;
			case 702659: //Noble Abbey Box.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 188053580, 1)); //[Event] Noble Abbey Bundle.
		    break;
			case 701542: //Tiamat's Huge Treasure Crate.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 166500000, 1)); //Amplification Stone.
				for (Player player: instance.getPlayersInside()) {
				    if (player.isOnline()) {
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188053619, 1)); //Ancient Manastone Bundle.
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188052084, 1)); //Dragon Lord Tiamat Treasure Chest.
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 188052383, 1)); //Distorted Dragon Lord's Weapon Box.
					}
				}
			break;
			case 802182: //Dragon Lord's Refuge Opportunity Bundle.
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
		Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
		    case 219532: //Noble Drakan Figther.
			case 219533: //Noble Drakan Wizard.
			case 219534: //Noble Drakan Sorcerer.
			case 219535: //Noble Drakan Clerc.
			case 219536: //Sardha Drakan Figther.
			case 219537: //Sardha Drakan Wizard.
			case 219538: //Sardha Drakan Sorcerer.
			case 219539: //Sardha Drakan Clerc.
			    despawnNpc(npc);
			break;
			case 283163: //Balaur Spiritualist.
			case 283164: //Balaur Spiritualist.
			case 283165: //Balaur Spiritualist.
			case 283166: //Balaur Spiritualist.
			    despawnNpc(npc);
				//The Empyrean Lord absorbed the Balaur Spiritualist's mental energy!
				sendMsgByRace(1401551, Race.PC_ALL, 0);
			break;
		    case 219359: //Calindi Flamelord.
			    despawnNpc(npc);
				deleteNpc(730695); //Surkana.
				deleteNpc(283130); //Blaze Engraving.
				deleteNpc(283132); //Blaze Engraving.
				if (getNpcs(219359).isEmpty()) { //Calindi Flamelord.
				    spawnTiamatTrueForm();
			    } if (player != null) {
				    switch (player.getRace()) {
					    case ELYOS:
						    spawnFissurefang();
							sendMovie(player, 882);
							//Enter the Internal Passage and destroy Tiamat's Incarnations while Kaisinel is dealing with Tiamat.
							sendMsgByRace(1401531, Race.ELYOS, 0);
							//The battle with Tiamat will automatically end in 30 minutes.
							sendMsgByRace(1401547, Race.ELYOS, 10000);
							//Empyrean Lord Kaisinel is attacking with all his might.
							sendMsgByRace(1401538, Race.ELYOS, 15000);
							//Eliminate the Balaur Spiritualist to grant a beneficial effect to the Empyrean Lord.
							sendMsgByRace(1401550, Race.ELYOS, 25000);
							ThreadPoolManager.getInstance().schedule(new Runnable() {
						        @Override
						        public void run() {
									startGodKaisinelEvent();
									spawn(283175, 551.78796f, 514.75494f, 417.40436f, (byte) 60); //Kaisinel Teleport.
								}
						    }, 15000);
						    ThreadPoolManager.getInstance().schedule(new Runnable() {
					            @Override
					            public void run() {
						            startRushWalkEvent1();
						            spawnInternalPassageEnter1();
						            spawn(283166, 463f, 461f, 417.405f, (byte) 17); //Balaur Spiritualist.
				                }
			                }, 25000);
						break;
						case ASMODIANS:
							spawnFissurefang();
							sendMovie(player, 884);
							//Enter the Internal Passage and destroy Tiamat's Incarnations while Kaisinel is dealing with Tiamat.
							sendMsgByRace(1401532, Race.ASMODIANS, 0);
							//The battle with Tiamat will automatically end in 30 minutes.
							sendMsgByRace(1401547, Race.ASMODIANS, 10000);
							//Empyrean Lord Marchutan is attacking with all his might.
							sendMsgByRace(1401538, Race.ASMODIANS, 15000);
							//Eliminate the Balaur Spiritualist to grant a beneficial effect to the Empyrean Lord.
							sendMsgByRace(1401550, Race.ASMODIANS, 25000);
							ThreadPoolManager.getInstance().schedule(new Runnable() {
						        @Override
						        public void run() {
									startGodMarchutanEvent();
									spawn(283176, 551.78796f, 514.75494f, 417.40436f, (byte) 60); //Marchutan Teleport.
								}
						    }, 15000);
							ThreadPoolManager.getInstance().schedule(new Runnable() {
					            @Override
					            public void run() {
						            startRushWalkEvent1();
						            spawnInternalPassageEnter1();
						            spawn(283166, 463f, 461f, 417.405f, (byte) 17); //Balaur Spiritualist.
					            }
				            }, 25000);
				        break;
					}
			    }
			    instance.doOnAllPlayers(new Visitor<Player>() {
				    @Override
				    public void visit(Player player) {
						//Dragon Lord Tiamat used its Death Roar to defeat the Empyrean Lord.
						sendMsgByRace(1401542, Race.PC_ALL, 0);
						SkillEngine.getInstance().applyEffectDirectly(20920, player, player, 30000); //Dragon Lord's Roar.
				    }
			    });
			    ThreadPoolManager.getInstance().schedule(new Runnable() {
				    @Override
				    public void run() {
					    instance.doOnAllPlayers(new Visitor<Player>() {
						    @Override
						    public void visit(Player player) {
							    player.getEffectController().removeEffect(20920); //Dragon Lord's Roar.
						    }
					    });
				    }
			    }, 10000);
			break;
		    case 219365: //Fissurefang.
			    despawnNpc(npc);
				spawnGraviwing();
				Npc tiamatTrue1 = instance.getNpc(219361); //Tiamat.
				tiamatBuff++;
				if (tiamatTrue1 != null) {
				    if (tiamatBuff == 1) {
					    tiamatTrue1.getEffectController().removeEffect(20975); //Fissure Incarnate.
				    }
				}
			    //Fissure Incarnate has collapsed.
				sendMsgByRace(1401533, Race.PC_ALL, 0);
			    despawnNpc(getNpc(730673)); //Internal Passage In 1.
			    ThreadPoolManager.getInstance().schedule(new Runnable() {
				    @Override
				    public void run() {
					    startRushWalkEvent2();
					    spawnInternalPassageEnter2();
					    spawn(283165, 545f, 461f, 417.405f, (byte) 46); //Balaur Spiritualist.
				    }
			    }, 15000);
		    break;
		    case 219366: //Graviwing.
			    despawnNpc(npc);
				spawnWrathclaw();
				Npc tiamatTrue2 = instance.getNpc(219361); //Tiamat.
				tiamatBuff++;
				if (tiamatTrue2 != null) {
				    if (tiamatBuff == 2) {
					    tiamatTrue2.getEffectController().removeEffect(20977); //Gravity Incarnate.
				    }
				}
			    //Gravity Incarnate has collapsed.
				sendMsgByRace(1401535, Race.PC_ALL, 0);
			    despawnNpc(getNpc(730674)); //Internal Passage In 2.
			    ThreadPoolManager.getInstance().schedule(new Runnable() {
				    @Override
				    public void run() {
					    startRushWalkEvent3();
					    spawnInternalPassageEnter3();
					    spawn(283164, 463f, 568f, 417.405f, (byte) 105); //Balaur Spiritualist.
				    }
			    }, 15000);
		    break;
		    case 219367: //Wrathclaw.
			    despawnNpc(npc);
				spawnPetriscale();
				Npc tiamatTrue3 = instance.getNpc(219361); //Tiamat.
				tiamatBuff++;
				if (tiamatTrue3 != null) {
				    if (tiamatBuff == 3) {
					    tiamatTrue3.getEffectController().removeEffect(20976); //Wrath Incarnate.
				    }
				}
				//Wrath Incarnate has collapsed.
				sendMsgByRace(1401534, Race.PC_ALL, 0);
			    despawnNpc(getNpc(730675)); //Internal Passage In 3.
			    ThreadPoolManager.getInstance().schedule(new Runnable() {
				    @Override
				    public void run() {
					    startRushWalkEvent4();
					    spawnInternalPassageEnter4();
					    spawn(283163, 545f, 568f, 417.405f, (byte) 78); //Balaur Spiritualist.
				    }
			    }, 15000);
		    break;
			case 219368: //Petriscale.
			    despawnNpc(npc);
				despawnNpc(getNpc(219361)); //Tiamat.
				despawnNpc(getNpc(219488)); //God Kaisinel.
				despawnNpc(getNpc(219491)); //God Marchutan.
				despawnNpc(getNpc(730676)); //Internal Passage In 4.
				Npc tiamatTrue4 = instance.getNpc(219361); //Tiamat.
				tiamatBuff++;
				if (tiamatTrue4 != null) {
				    if (tiamatBuff == 4) {
					    tiamatTrue4.getEffectController().removeEffect(20978); //Petrification Incarnate.
						tiamatTrue4.getEffectController().removeEffect(20984); //Unbreakable Wing.
				    }
				}
				//Gravity Incarnate has collapsed.
				sendMsgByRace(1401536, Race.PC_ALL, 0);
				if (player != null) {
				    switch (player.getRace()) {
				        case ELYOS:
					        kaisinelLight();
							//All of Tiamat's Incarnations have collapsed.
							sendMsgByRace(1401537, Race.ELYOS, 2000);
							ThreadPoolManager.getInstance().schedule(new Runnable() {
						        @Override
						        public void run() {
									spawnTiamatDying();
									spawnGodKaisinelGroggy();
									//Empyrean Lord Kaisinel is exhausted. You must take over the fight against Tiamat!
									sendMsgByRace(1401540, Race.ELYOS, 5000);
									
								}
						    }, 5000);
						break;
						case ASMODIANS:
					        marchutanGrace();
							//All of Tiamat's Incarnations have collapsed.
							sendMsgByRace(1401537, Race.ASMODIANS, 2000);
							ThreadPoolManager.getInstance().schedule(new Runnable() {
						        @Override
						        public void run() {
									spawnTiamatDying();
									spawnGodMarchutanGroggy();
									//Empyrean Lord Marchutan is exhausted. You must take over the fight against Tiamat!
									sendMsgByRace(1401541, Race.ASMODIANS, 5000);
								}
						    }, 5000);
				        break;
					}
				}
			break;
		    case 219362: //Tiamat Dying.
			    despawnNpc(npc);
				spawn(283134, 458.36316f, 514.46686f, 417.40436f, (byte) 0);
				if (player != null) {
				    switch (player.getRace()) {
			            case ELYOS:
				            sendMovie(player, 883);
							spawn(800350, 504.4801f, 515.12964f, 417.40436f, (byte) 60); //Kaisinel.
				        break;
			            case ASMODIANS:
				            sendMovie(player, 885);
				            spawn(800356, 504.4801f, 515.12964f, 417.40436f, (byte) 60); //Marchutan.
				        break;
					}
			    }
				spawnAbbeyNobleBox();
				spawnTiamatHugeTreasureCrate();
			    despawnNpc(getNpc(701502)); //Siel's Relic.
				despawnNpc(getNpc(219489)); //God Kaisinel Tired.
				despawnNpc(getNpc(219492)); //God Marchutan Tired.
			    despawnNpc(getNpc(730694)); //Tiamat Aetheric Field.
			    spawn(800430, 500.61713f, 507.2179f, 417.40436f, (byte) 0); //Kahrun (Reian Leader).
			    spawn(800464, 546.452f, 516.3783f, 417.40436f, (byte) 111);  //Reian Sorcerer.
			    spawn(800465, 546.79755f, 512.78314f, 417.40436f, (byte) 10); //Reian Sorcerer.
				spawn(802182, 486.26587f, 509.8968f, 417.40436f, (byte) 6); //Dragon Lord's Refuge Opportunity Bundle.
			    SpawnTemplate SelfShadowing = SpawnEngine.addNewSingleTimeSpawn(300520000, 730630, 548.29999f, 514.59998f, 420.04001f, (byte) 0);
			    SelfShadowing.setEntityId(23);
			    objects.put(730630, SpawnEngine.spawnObject(SelfShadowing, instanceId));
				SpawnTemplate FUpdateRadius = SpawnEngine.addNewSingleTimeSpawn(300520000, 730704, 437.54105f, 513.48688f, 415.82394f, (byte) 0);
			    FUpdateRadius.setEntityId(17);
			    objects.put(730704, SpawnEngine.spawnObject(FUpdateRadius, instanceId));
		    break;
			case 219488: //God Kaisinel.
			    if (!getNpcs(219361).isEmpty()) //Tiamat.
				    despawnNpc(getNpc(219361));
			    if (!getNpcs(219365).isEmpty()) //Fissurefang.
				    despawnNpc(getNpc(219365));
			    if (!getNpcs(219366).isEmpty()) //Graviwing.
				    despawnNpc(getNpc(219366));
			    if (!getNpcs(219367).isEmpty()) //Wrathclaw.
				    despawnNpc(getNpc(219367));
			    if (!getNpcs(219368).isEmpty()) //Petriscale.
				    despawnNpc(getNpc(219368));
			    instance.doOnAllPlayers(new Visitor<Player>() {
				    @Override
				    public void visit(Player player) {
					    PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.IDTIAMAT_TIAMAT_COUNTDOWN_OVER);
				    }
			    });
			    ThreadPoolManager.getInstance().schedule(new Runnable() {
				    @Override
				    public void run() {
					    instance.doOnAllPlayers(new Visitor<Player>() {
						    @Override
						    public void visit(Player player) {
							    onExitInstance(player);
						    }
					    });
					    onInstanceDestroy();
				    }
			    }, 10000);
			break;
		    case 219491: //God Marchutan.
			    if (!getNpcs(219361).isEmpty()) //Tiamat.
				    despawnNpc(getNpc(219361));
			    if (!getNpcs(219365).isEmpty()) //Fissurefang.
				    despawnNpc(getNpc(219365));
			    if (!getNpcs(219366).isEmpty()) //Graviwing.
				    despawnNpc(getNpc(219366));
			    if (!getNpcs(219367).isEmpty()) //Wrathclaw.
				    despawnNpc(getNpc(219367));
			    if (!getNpcs(219368).isEmpty()) //Petriscale.
				    despawnNpc(getNpc(219368));
			    instance.doOnAllPlayers(new Visitor<Player>() {
				    @Override
				    public void visit(Player player) {
					    PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.IDTIAMAT_TIAMAT_COUNTDOWN_OVER);
				    }
			    });
			    ThreadPoolManager.getInstance().schedule(new Runnable() {
				    @Override
				    public void run() {
					    instance.doOnAllPlayers(new Visitor<Player>() {
						    @Override
						    public void visit(Player player) {
							    onExitInstance(player);
						    }
					    });
					    onInstanceDestroy();
				    }
			    }, 10000);
		    break;
	    }
	}
	
	@Override
	public void onPlayerLogOut(Player player) {
		removeEffects(player);
	}
	
	@Override
	public void onLeaveInstance(Player player) {
		removeEffects(player);
	}
	
	private void removeEffects(Player player) {
		PlayerEffectController effectController = player.getEffectController();
		effectController.removeEffect(20932); //Kaisinel's Light.
		effectController.removeEffect(20936); //Marchutan's Grace.
	}
	
	//Kaisinel's Light.
	private void kaisinelLight() {
		for (Player p: instance.getPlayersInside()) {
			SkillTemplate st =  DataManager.SKILL_DATA.getSkillTemplate(20932); //Kaisinel's Light.
			Effect e = new Effect(p, p, st, 1, st.getEffectsDuration(9));
			e.initialize();
			e.applyEffect();
		}
	}
	
	//Marchutan's Grace.
	private void marchutanGrace() {
		for (Player p: instance.getPlayersInside()) {
			SkillTemplate st =  DataManager.SKILL_DATA.getSkillTemplate(20936); //Marchutan's Grace.
			Effect e = new Effect(p, p, st, 1, st.getEffectsDuration(9));
			e.initialize();
			e.applyEffect();
		}
	}
	
	//PHASE TIAMAT.
	private void spawnTiamatWomanForm() {
		spawn(219360, 470.5909f, 515.02856f, 417.40436f, (byte) 119); //Tiamat.
	}
	private void spawnTiamatTrueForm() {
		spawn(219361, 457.7215f, 514.4464f, 417.53998f, (byte) 0); //Tiamat True.
	}
	private void spawnTiamatDying() {
		spawn(219362, 458.36316f, 514.46686f, 417.40436f, (byte) 0); //Tiamat Dying.
	}
	private void spawnTiamatHugeTreasureCrate() {
		spawn(701542, 485.7635f, 514.60126f, 417.40436f, (byte) 0); //Tiamat's Huge Treasure Crate.
	}
	private void spawnAbbeyNobleBox() {
		switch (Rnd.get(1, 2)) {
		    case 1:
				spawn(702658, 488.25827f, 505.1509f, 417.40436f, (byte) 11); //Abbey Box.
			break;
			case 2:
				spawn(702659, 488.25827f, 505.1509f, 417.40436f, (byte) 11); //Noble Abbey Box.
			break;
		}
	}
	
	private void eventGodAttack(final Npc npc, float x, float y, float z, boolean despawn) {
		((AbstractAI) npc.getAi2()).setStateIfNot(AIState.WALKING);
		npc.setState(1);
		npc.getMoveController().moveToPoint(x, y, z);
		PacketSendUtility.broadcastPacket(npc, new SM_EMOTION(npc, EmotionType.START_EMOTE2, 0, npc.getObjectId()));
	}
	
	//PHASE GOD KASINEL.
	private void startGodKaisinelEvent() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				eventGodAttack((Npc)spawn(219488, 551.78796f, 514.75494f, 417.40436f, (byte) 60), 480.363f, 514.3989f, 417.40436f, false); //God Kaisinel.
			}
		}, 1000);
	}
	private void spawnGodKaisinelGroggy() {
		spawn(219489, 507.17175f, 513.7484f, 417.40436f, (byte) 59); //God Kaisinel Tired.
	}
	
	//PHASE GOD MARCHUTAN.
	private void startGodMarchutanEvent() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
		        eventGodAttack((Npc)spawn(219491, 551.78796f, 514.75494f, 417.40436f, (byte) 60), 480.363f, 514.3989f, 417.40436f, false); //God Marchutan.
			}
		}, 1000);
	}
	private void spawnGodMarchutanGroggy() {
		spawn(219492, 507.17175f, 513.7484f, 417.40436f, (byte) 59); //God Marchutan Tired.
	}
	
	//PHASE 4 DRAGON.
	private void spawnFissurefang() {
		spawn(219365, 196.67767f, 176.11638f, 246.07117f, (byte) 8); //Fissurefang.
	}
	private void spawnGraviwing() {
		spawn(219366, 799.8529f, 176.94928f, 246.07117f, (byte) 39); //Graviwing.
	}
	private void spawnWrathclaw() {
		spawn(219367, 199.11307f, 848.60956f, 246.07117f, (byte) 110); //Wrathclaw.
	}
	private void spawnPetriscale() {
		spawn(219368, 796.535f, 849.48615f, 246.07117f, (byte) 72); //Petriscale.
	}
	
	private void spawnInternalPassageEnter1() {
		SpawnTemplate NEAXEnvironment = SpawnEngine.addNewSingleTimeSpawn(300520000, 730673, 461.24423f, 458.91919f, 416.62f, (byte) 0);
		NEAXEnvironment.setEntityId(35);
		objects.put(730673, SpawnEngine.spawnObject(NEAXEnvironment, instanceId));
	}
	
	private void spawnInternalPassageEnter2() {
		SpawnTemplate FileLadderCGF = SpawnEngine.addNewSingleTimeSpawn(300520000, 730674, 546.12146f, 459.33582f, 416.62f, (byte) 0);
		FileLadderCGF.setEntityId(33);
		objects.put(730674, SpawnEngine.spawnObject(FileLadderCGF, instanceId));
	}
	
	private void spawnInternalPassageEnter3() {
		SpawnTemplate CustomEquipColor2 = SpawnEngine.addNewSingleTimeSpawn(300520000, 730675, 461.45767f, 570.08691f, 416.61667f, (byte) 0);
		CustomEquipColor2.setEntityId(31);
		objects.put(730675, SpawnEngine.spawnObject(CustomEquipColor2, instanceId));
	}
	
	private void spawnInternalPassageEnter4() {
		SpawnTemplate BCastPointShadow = SpawnEngine.addNewSingleTimeSpawn(300520000, 730676, 546.47882f, 570.13873f, 416.62f, (byte) 0);
		BCastPointShadow.setEntityId(32);
		objects.put(730676, SpawnEngine.spawnObject(BCastPointShadow, instanceId));
	}
	
	//PHASE RUSH.
	private void rushWalk(final Npc npc) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (!isInstanceDestroyed) {
					for (Player player: instance.getPlayersInside()) {
						npc.setTarget(player);
						((AbstractAI) npc.getAi2()).setStateIfNot(AIState.WALKING);
						npc.setState(1);
						npc.getMoveController().moveToTargetObject();
						PacketSendUtility.broadcastPacket(npc, new SM_EMOTION(npc, EmotionType.START_EMOTE2, 0, npc.getObjectId()));
					}
				}
			}
		}, 1000);
	}
	
	public void startRushWalkEvent1() {
		rushWalk((Npc)spawn(219538, 468.89908f, 463.28857f, 417.40436f, (byte) 16)); //Sardha Drakan Sorcerer.
		rushWalk((Npc)spawn(219539, 467.41974f, 466.10922f, 417.40436f, (byte) 13)); //Sardha Drakan Clerc.
		rushWalk((Npc)spawn(219533, 544.04144f, 469.6464f, 417.40436f, (byte) 52)); //Noble Drakan Wizard.
	}
	
	public void startRushWalkEvent2() {
		rushWalk((Npc)spawn(219532, 540.9507f, 466.07214f, 417.40436f, (byte) 42)); //Noble Drakan Figther.
		rushWalk((Npc)spawn(219533, 544.04144f, 469.6464f, 417.40436f, (byte) 52)); //Noble Drakan Wizard.
		rushWalk((Npc)spawn(219534, 536.7774f, 463.96362f, 417.40436f, (byte) 33)); //Noble Drakan Sorcerer.
	}
	
	private void startRushWalkEvent3() {
		rushWalk((Npc)spawn(219535, 462.77353f, 562.71106f, 417.40436f, (byte) 77)); //Noble Drakan Clerc.
		rushWalk((Npc)spawn(219536, 467.94543f, 567.6658f, 417.40436f, (byte) 85)); //Sardha Drakan Figther.
		rushWalk((Npc)spawn(219537, 464.2729f, 566.56067f, 417.40436f, (byte) 67)); //Sardha Drakan Wizard.
	}
	
	public void startRushWalkEvent4() {
		rushWalk((Npc)spawn(219535, 542.7636f, 565.65045f, 417.40436f, (byte) 77)); //Noble Drakan Clerc.
		rushWalk((Npc)spawn(219536, 538.6315f, 566.12714f, 417.40436f, (byte) 85)); //Sardha Drakan Figther.
		rushWalk((Npc)spawn(219537, 544.4505f, 561.9321f, 417.40436f, (byte) 67)); //Sardha Drakan Wizard.
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
	public boolean onDie(final Player player, Creature lastAttacker) {
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()), true);
		PacketSendUtility.sendPacket(player, new SM_DIE(false, false, 0, 8));
		return true;
	}
	
	@Override
	public boolean onReviveEvent(Player player) {
		player.getGameStats().updateStatsAndSpeedVisually();
		PlayerReviveService.revive(player, 100, 100, false, 0);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
		PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_INSTANT_DUNGEON_RESURRECT, 0, 0));
		TeleportService2.teleportTo(player, mapId, instanceId, 510.2436f, 512.10333f, 417.40436f, (byte) 49);
		return true;
	}
	
	@Override
	public void onInstanceDestroy() {
		isInstanceDestroyed = true;
		movies.clear();
	}
	
	public void onExitInstance(Player player) {
		TeleportService2.moveToInstanceExit(player, mapId, player.getRace());
	}
}