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
package instance.pvparenas;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;

import com.aionemu.gameserver.controllers.attack.AggroInfo;
import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.actions.PlayerActions;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.instance.InstanceScoreType;
import com.aionemu.gameserver.model.instance.instancereward.InstanceReward;
import com.aionemu.gameserver.model.instance.instancereward.PvPArenaReward;
import com.aionemu.gameserver.model.instance.playerreward.PvPArenaPlayerReward;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.AutoGroupService;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.player.PlayerReviveService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.model.DispelCategoryType;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

/****/
/** Author Rinzler (Encom)
/****/

public class PvPArenaInstance extends GeneralInstanceHandler
{
	protected int killBonus;
	protected int deathFine;
	private boolean isInstanceDestroyed;
	protected PvPArenaReward instanceReward;
	
	@Override
	public boolean onDie(Player player, Creature lastAttacker) {
		PvPArenaPlayerReward ownerReward = getPlayerReward(player.getObjectId());
		ownerReward.endBoostMoraleEffect(player);
		ownerReward.applyBoostMoraleEffect(player);
		sendPacket();
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()), true);
		PacketSendUtility.sendPacket(player, new SM_DIE(false, false, 0, 8));
		if (lastAttacker != null && lastAttacker != player) {
			if (lastAttacker instanceof Player) {
				Player winner = (Player) lastAttacker;
				PvPArenaPlayerReward reward = getPlayerReward(winner.getObjectId());
				reward.addPvPKillToPlayer();
				int worldId = winner.getWorldId();
				QuestEngine.getInstance().onKillInWorld(new QuestEnv(player, winner, 0, 0), worldId);
			}
		}
		updatePoints(player);
		return true;
	}
	
	private void updatePoints(Creature victim) {
		if (!instanceReward.isStartProgress()) {
			return;
		}
		int bonus = 0;
		int rank = 0;
		if (victim instanceof Player) {
			PvPArenaPlayerReward victimFine = getPlayerReward(victim.getObjectId());
			victimFine.addPoints(deathFine);
			bonus = killBonus;
			rank = instanceReward.getRank(victimFine.getPoints());
		} else {
			bonus = getNpcBonus(((Npc) victim).getNpcId());
		} if (bonus == 0) {
			return;
		}
		for (AggroInfo damager : victim.getAggroList().getList()) {
			if (!(damager.getAttacker() instanceof Creature)) {
				continue;
			}
			Creature master = ((Creature) damager.getAttacker()).getMaster();
			if (master == null) {
				continue;
			} if (master instanceof Player) {
				Player attaker = (Player) master;
				int rewardPoints = (victim instanceof Player && instanceReward.getRound() == 3 && rank == 0 ? bonus * 3 : bonus) * damager.getDamage() / victim.getAggroList().getTotalDamage();
				getPlayerReward(attaker.getObjectId()).addPoints(rewardPoints);
				sendSystemMsg(attaker, victim, rewardPoints);
			}
		} if (instanceReward.hasCapPoints()) {
			instanceReward.setInstanceScoreType(InstanceScoreType.END_PROGRESS);
			reward();
		}
		sendPacket();
	}
	
	protected void sendSystemMsg(Player player, Creature creature, int rewardPoints) {
		int nameId = creature.getObjectTemplate().getNameId();
		DescriptionId name = new DescriptionId(nameId * 2 + 1);
		PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400237, nameId == 0 ? creature.getName() : name, rewardPoints));
	}
	
	@Override
	public void onDie(Npc npc) {
		if (npc.getAggroList().getMostPlayerDamage() == null) {
			return;
		}
		updatePoints(npc);
		final int npcId = npc.getNpcId();
		if (npcId == 701173 || //Blessed Relics.
			npcId == 701187) { //Blessed Relics.
			spawnBlessedRelics(30000);
		} if (npcId == 701174 || //Cursed Relics.
			npcId == 701188) { //Cursed Relics.
			spawnCursedRelics(30000);
		}
	}
	
	@Override
	public void onEnterInstance(Player player) {
		Integer object = player.getObjectId();
		if (!containPlayer(object)) {
			instanceReward.regPlayerReward(object);
			getPlayerReward(object).applyBoostMoraleEffect(player);
			instanceReward.setRndPosition(object);
		} else {
			instanceReward.portToPosition(player);
		}
		sendPacket();
	}
	
	private void sendPacket(final AionServerPacket packet) {
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				PacketSendUtility.sendPacket(player, packet);
			}
		});
	}
	
	private void spawnBlessedRelics(int time) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (!isInstanceDestroyed && !instanceReward.isRewarded()) {
					spawn(Rnd.get(1, 2) == 1 ? 701173 : 701187, 1841.951f, 1733.968f, 300.242f, (byte) 0);
				}
			}
		}, time);
	}
	
	private void spawnCursedRelics(int time) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (!isInstanceDestroyed && !instanceReward.isRewarded()) {
					spawn(Rnd.get(1, 2) == 1 ? 701174 : 701188, 674.517f, 1778.428f, 204.693f, (byte) 0);
				}
			}
		}, time);
	}
	
	private int getNpcBonus(int npcId) {
		switch (npcId) {
			case 243666: //Black Claw Scratcher.
			case 243675: //Red Sand Brax.
			case 243676: //Red Sand Tog.
			case 243667: //Mutated Drakan Fighter.
			    return 100;
			case 243681: //Casus Manor Chief Maid.
			    return 400;
			case 243671: //Casus Manor Butler.
			    return 650;
			case 243672: //Casus Manor Noble.
			    return 750;
			case 243665: //Mumu Rake Gatherer.
			    return 1250;
			case 243673: //Pale Carmina.
			case 243674: //Corrupt Casus.
				return 1500;
			//Blessed Relics/Cursed Relics
			case 701173:
			case 701174:
			case 701187:
			case 701188:
			case 701201:
			case 701202:
			case 701834:
			case 701835:
			    return 1750;
			default:
				return 0;
		}
	}
	
	@Override
	public InstanceReward<?> getInstanceReward() {
		return instanceReward;
	}
	
	@Override
	public void onPlayerLogOut(Player player) {
		getPlayerReward(player.getObjectId()).updateLogOutTime();
	}
	
	@Override
	public void onPlayerLogin(Player player) {
		getPlayerReward(player.getObjectId()).updateBonusTime();
	}
	
	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		instanceReward = new PvPArenaReward(mapId, instanceId, instance);
		instanceReward.setInstanceScoreType(InstanceScoreType.PREPARING);
		spawnRings();
		if (!instanceReward.isSoloArena()) {
			spawnCursedRelics(0);
			spawnBlessedRelics(0);
		}
		instanceReward.setInstanceStartTime();
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (!isInstanceDestroyed && !instanceReward.isRewarded() && canStart()) {
					openDoors();
					//The member recruitment window has passed. You cannot recruit any more members.
					sendMsgByRace(1401181, Race.PC_ALL, 0);
					instanceReward.setInstanceScoreType(InstanceScoreType.START_PROGRESS);
					sendPacket();
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							if (!isInstanceDestroyed && !instanceReward.isRewarded()) {
								instanceReward.setRound(2);
								instanceReward.setRndZone();
								sendPacket();
								changeZone();
								//If you defeat a higher rank group in this round, you can earn additional points.
								sendMsgByRace(1401491, Race.PC_ALL, 2000);
								ThreadPoolManager.getInstance().schedule(new Runnable() {
									@Override
									public void run() {
										if (!isInstanceDestroyed && !instanceReward.isRewarded()) {
											instanceReward.setRound(3);
											instanceReward.setRndZone();
											sendPacket();
											changeZone();
											//If you defeat a higher rank group in this round, you can earn additional points.
											sendMsgByRace(1401491, Race.PC_ALL, 2000);
											ThreadPoolManager.getInstance().schedule(new Runnable() {
												@Override
												public void run() {
													if (!isInstanceDestroyed && !instanceReward.isRewarded()) {
                                                        instanceReward.setInstanceScoreType(InstanceScoreType.END_PROGRESS);
                                                        reward();
														sendPacket();
                                                    }
                                                }
                                            }, 180000);
                                        }
                                    }
                                }, 180000);
                            }
                        }
                    }, 180000);
                }
            }
        }, 120000);
    }
	
	private boolean canStart() {
		if (instance.getPlayersInside().size() < 2) {
			onInstanceDestroy();
			//Unavailable to use when you're alone.
			sendMsgByRace(1403045, Race.PC_ALL, 0);
			instanceReward.setInstanceScoreType(InstanceScoreType.END_PROGRESS);
			reward();
			sendPacket();
			return false;
		}
		return true;
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
	public void onExitInstance(Player player) {
		TeleportService2.moveToInstanceExit(player, mapId, player.getRace());
	}
	
	private void openDoors() {
		for (StaticDoor door : instance.getDoors().values()) {
			if (door != null) {
				door.setOpen(true);
			}
		}
	}
	
	private boolean containPlayer(Integer object) {
		return instanceReward.containPlayer(object);
	}
	
	protected PvPArenaPlayerReward getPlayerReward(Integer object) {
		instanceReward.regPlayerReward(object);
		return (PvPArenaPlayerReward) instanceReward.getPlayerReward(object);
	}
	
	@Override
	public boolean onReviveEvent(Player player) {
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
		PlayerReviveService.revive(player, 100, 100, false, 0);
		player.getGameStats().updateStatsAndSpeedVisually();
		if (!isInstanceDestroyed) {
			instanceReward.portToPosition(player);
		}
		return true;
	}
	
	@Override
	public void onLeaveInstance(Player player) {
		clearDebuffs(player);
		PvPArenaPlayerReward playerReward = getPlayerReward(player.getObjectId());
		if (playerReward != null) {
			playerReward.endBoostMoraleEffect(player);
			instanceReward.clearPosition(playerReward.getPosition(), Boolean.FALSE);
			instanceReward.removePlayerReward(playerReward);
		}
	}
	
	private void clearDebuffs(Player player) {
		for (Effect ef : player.getEffectController().getAbnormalEffects()) {
			DispelCategoryType category = ef.getSkillTemplate().getDispelCategory();
			if (category == DispelCategoryType.DEBUFF
				|| category == DispelCategoryType.DEBUFF_MENTAL
				|| category == DispelCategoryType.DEBUFF_PHYSICAL
				|| category == DispelCategoryType.ALL) {
				ef.endEffect();
				player.getEffectController().clearEffect(ef);
			}
		}
	}
	
	protected void sendPacket() {
		instanceReward.sendPacket();
	}
	
	private void despawnNpc(Npc npc) {
		if (npc != null) {
			npc.getController().onDelete();
		}
	}
	
	@Override
	public void onInstanceDestroy() {
		isInstanceDestroyed = true;
		instanceReward.clear();
	}
	
	private void changeZone() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				for (Player player : instance.getPlayersInside()) {
					instanceReward.portToPosition(player);
				}
				sendPacket();
			}
		}, 1000);
	}
	
	protected void reward() {
		for (Player player : instance.getPlayersInside()) {
			if (PlayerActions.isAlreadyDead(player))
				PlayerReviveService.duelRevive(player);
			    PvPArenaPlayerReward reward = getPlayerReward(player.getObjectId());
			if (!reward.isRewarded()) {
				reward.setRewarded();
				//<Abyss Points>
				AbyssPointsService.addAp(player, reward.getBasicAP() + reward.getRankingAP() + reward.getScoreAP());
				//<Glory Points>
				//AbyssPointsService.addGp(player, reward.getBasicGP() + reward.getRankingGP() + reward.getScoreGP());
				int courage = reward.getBasicCourage() + reward.getRankingCourage() + reward.getScoreCourage();
				if (courage != 0) {
					ItemService.addItem(player, 186000137, courage);
				}
				int crucible = reward.getBasicCrucible() + reward.getRankingCrucible() + reward.getScoreCrucible();
				if (crucible != 0) {
					ItemService.addItem(player, 186000130, crucible);
				}
				int opportunity = reward.getOpportunity();
				if (opportunity != 0) {
					ItemService.addItem(player, 186000165, opportunity);
				}
				int gloryTicket = reward.getGloryTicket();
				if (gloryTicket != 0) {
					ItemService.addItem(player, 186000185, gloryTicket);
				}
				int mithrilMedal = reward.getMithrilMedal();
				if (mithrilMedal != 0) {
					ItemService.addItem(player, 186000147, mithrilMedal); 
				}
				int platinumMedal = reward.getPlatinumMedal();
				if (platinumMedal != 0) {
					ItemService.addItem(player, 186000096, platinumMedal);
				}
				int gloriousInsignia = reward.getGloriousInsignia();
				if (gloriousInsignia != 0) {
					ItemService.addItem(player, 182213259, gloriousInsignia);
				}
				int lifeSerum = reward.getLifeSerum();
				if (lifeSerum != 0) {
					ItemService.addItem(player, 162000077, lifeSerum);
				}
				int infinity = reward.getBasicInfinity() + reward.getRankingInfinity() + reward.getScoreInfinity();
				if (infinity != 0) {
					ItemService.addItem(player, 186000442, infinity);
				}
			}
		}
		for (Npc npc : instance.getNpcs()) {
			npc.getController().onDelete();
		}
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (!isInstanceDestroyed) {
					for (Player player : instance.getPlayersInside()) {
						onExitInstance(player);
					}
					AutoGroupService.getInstance().unRegisterInstance(instanceId);
				}
			}
		}, 10000);
	}
	
	protected void spawnRings() {
	}
	
	protected Npc getNpc(float x, float y, float z) {
		if (!isInstanceDestroyed) {
			for (Npc npc : instance.getNpcs()) {
				SpawnTemplate st = npc.getSpawn();
				if (st.getX() == x && st.getY() == y && st.getZ() == z) {
					return npc;
				}
			}
		}
		return null;
	}
	
	@Override
	public void handleUseItemFinish(Player player, Npc npc) {
		switch(npc.getNpcId()) {
			case 701169: //Plaza Flame Thrower.
			    despawnNpc(npc);
				spawn(702405, 1798.8951f, 1727.2413f, 302.81836f, (byte) 62);
				spawn(702405, 1808.9938f, 1703.7997f, 302.73233f, (byte) 74);
			break;
			case 701170: //Plaza Flame Thrower.
			    despawnNpc(npc);
				spawn(702405, 1848.1892f, 1689.1056f, 302.74982f, (byte) 92);
				spawn(702405, 1871.4725f, 1699.5228f, 303.0393f, (byte) 104);
			break;
			case 701171: //Plaza Flame Thrower.
			    despawnNpc(npc);
				spawn(702405, 1886.8333f, 1738.3987f, 302.5374f, (byte) 3);
				spawn(702405, 1876.5596f, 1761.9902f, 302.6582f, (byte) 14);
			break;
			case 701172: //Plaza Flame Thrower.
			    despawnNpc(npc);
				spawn(702405, 1837.242f, 1776.3717f, 302.7615f, (byte) 32);
				spawn(702405, 1814.1249f, 1766.2068f, 302.61606f, (byte) 43);
			break;
			case 207102: //Recovery Relics.
				player.getLifeStats().increaseHp(SM_ATTACK_STATUS.TYPE.HP, 10000);
				player.getLifeStats().increaseMp(SM_ATTACK_STATUS.TYPE.MP, 10000);
			break;
		   /**
			* Treasure Box
			* [Arena Of Chaos/Chaos Training Grounds]
			*/
			case 218784:
			case 218785:
			case 218786:
			case 218787:
			case 218788:
			case 218789:
		   /**
			* Treasure Box
			* [Arena Of Discipline/Discipline Training Grounds]
			*/
			case 218791:
			case 218792:
			case 218793:
			case 218794:
			case 218795:
				if (player.getInventory().isFull()) {
					sendMsgByRace(1390149, Race.PC_ALL, 0);
				} switch (Rnd.get(1, 7)) {
					case 1:
					    ItemService.addItem(player, 186000096, 10); //Platinum Medal.
					break;
					case 2:
					    ItemService.addItem(player, 186000130, 10); //Crucible Insignia.
					break;
					case 3:
					    ItemService.addItem(player, 186000137, 10); //Courage Insignia.
					break;
					case 4:
					    ItemService.addItem(player, 186000147, 10); //Mithril Medal.
					break;
					case 5:
					    ItemService.addItem(player, 186000165, 10); //Opportunity Token.
					break;
					case 6:
					    ItemService.addItem(player, 186000442, 10); //Valor Insignia.
					break;
					case 7:
					    ItemService.addItem(player, 182213259, 10); //Glorious Insignia.
					break;
				}
				despawnNpc(npc);
			break;
		} if (!instanceReward.isStartProgress()) {
			return;
		}
		int rewardetPoints = getNpcBonus(npc.getNpcId());
		int skill = instanceReward.getNpcBonusSkill(npc.getNpcId());
		if (skill != 0) {
			useSkill(npc, player, skill >> 8, skill & 0xFF);
		}
		getPlayerReward(player.getObjectId()).addPoints(rewardetPoints);
		sendSystemMsg(player, npc, rewardetPoints);
		sendPacket();
	}
	
	protected void useSkill(Npc npc, Player player, int skillId, int level) {
		SkillEngine.getInstance().getSkill(npc, skillId, level, player).useNoAnimationSkill();
	}
}