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
import com.aionemu.gameserver.controllers.attack.AggroInfo;
import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.actions.PlayerActions;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.instance.InstanceScoreType;
import com.aionemu.gameserver.model.instance.instancereward.HarmonyArenaReward;
import com.aionemu.gameserver.model.instance.instancereward.InstanceReward;
import com.aionemu.gameserver.model.instance.playerreward.HarmonyGroupReward;
import com.aionemu.gameserver.model.instance.playerreward.PvPArenaPlayerReward;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.AutoGroupService;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.player.PlayerReviveService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.skillengine.model.DispelCategoryType;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

/****/
/** Author Rinzler (Encom)
/****/

public class HarmonyArenaInstance extends GeneralInstanceHandler
{
	protected int killBonus = 1000;
	protected int deathFine = -150;
	protected HarmonyArenaReward instanceReward;
	protected boolean isInstanceDestroyed;
	
	@Override
	public InstanceReward<?> getInstanceReward() {
		return instanceReward;
	}
	
	@Override
	public void onEnterInstance(final Player player) {
		Integer object = player.getObjectId();
		if (!instanceReward.containPlayer(object)) {
			instanceReward.regPlayerReward(object);
			instanceReward.getPlayerReward(object).applyBoostMoraleEffect(player);
			instanceReward.setRndPosition(object);
		} else {
			instanceReward.portToPosition(player);
		}
		sendEnterPacket(player);
	}
	
	private void sendEnterPacket(final Player player) {
		final Integer object = player.getObjectId();
		final HarmonyGroupReward group = instanceReward.getHarmonyGroupReward(object);
		if (group == null) {
			return;
		}
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player opponent) {
				if (!group.containPlayer(opponent.getObjectId())) {
					PacketSendUtility.sendPacket(opponent, new SM_INSTANCE_SCORE(10, getTime(), getInstanceReward(), object));
					PacketSendUtility.sendPacket(player, new SM_INSTANCE_SCORE(10, getTime(), getInstanceReward(), opponent.getObjectId()));
					PacketSendUtility.sendPacket(opponent, new SM_INSTANCE_SCORE(3, getTime(), getInstanceReward(), object));
				} else {
					PacketSendUtility.sendPacket(opponent, new SM_INSTANCE_SCORE(10, getTime(), getInstanceReward(), opponent.getObjectId()));
					if (object != opponent.getObjectId()) {
						PacketSendUtility.sendPacket(opponent, new SM_INSTANCE_SCORE(3, getTime(), getInstanceReward(), object));
					}
				}
			}
		});
		Integer NullObject = null;
		PacketSendUtility.sendPacket(player, new SM_INSTANCE_SCORE(6, getTime(), getInstanceReward(), null));
		instanceReward.sendPacket(4, object);
	}
	
	private void updatePoints(Creature victim) {
		if (!instanceReward.isStartProgress()) {
			return;
		}
		int bonus = 0;
		int rank = 0;
		if (victim instanceof Player) {
			final HarmonyGroupReward victimGroup = instanceReward.getHarmonyGroupReward(victim.getObjectId());
			victimGroup.addPoints(deathFine);
			bonus = killBonus;
			rank = instanceReward.getRank(victimGroup.getPoints());
			instanceReward.sendPacket(10, victim.getObjectId());
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
				instanceReward.getHarmonyGroupReward(attaker.getObjectId()).addPoints(rewardPoints);
				sendSystemMsg(attaker, victim, rewardPoints);
				instanceReward.sendPacket(10, attaker.getObjectId());
			}
		} if (instanceReward.hasCapPoints()) {
			instanceReward.setInstanceScoreType(InstanceScoreType.END_PROGRESS);
			reward();
			instanceReward.sendPacket(5, null);
		}
	}
	
	@Override
	public void onDie(Npc npc) {
		if (npc.getAggroList().getMostPlayerDamage() == null) {
			return;
		}
		updatePoints(npc);
	}
	
	protected void sendSystemMsg(Player player, Creature creature, int rewardPoints) {
		int nameId = creature.getObjectTemplate().getNameId();
		DescriptionId name = new DescriptionId(nameId * 2 + 1);
		PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400237, nameId == 0 ? creature.getName() : name, rewardPoints));
	}
	
	private int getNpcBonus(int npcId) {
		switch (npcId) {
			case 207102:
			case 207116:
			case 243678: //Roaming Volcanic Petrahulk.
				return 400;
			case 207099:
				return 200;
			case 243679: //Heated Negotiator Grangvolkan.
				return 100;
			case 219328: //Plaza Wall.	
			case 243680: //Lurking Fangwing.
				return 50;
			default:
				return 0;
		}
	}
	
	private int getTime() {
		return instanceReward.getTime();
	}
	
	@Override
	public void onPlayerLogin(Player player) {
		sendEnterPacket(player);
	}
	
	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		instanceReward = new HarmonyArenaReward(mapId, instanceId, instance);
		instanceReward.setInstanceScoreType(InstanceScoreType.PREPARING);
		instanceReward.setInstanceStartTime();
		spawnRings();
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (!isInstanceDestroyed && !instanceReward.isRewarded() && canStart()) {
					openDoors();
					//The member recruitment window has passed. You cannot recruit any more members.
					sendMsgByRace(1401181, Race.PC_ALL, 0);
					instanceReward.setInstanceScoreType(InstanceScoreType.START_PROGRESS);
					instanceReward.sendPacket(10, null);
					instanceReward.sendPacket(2, null);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							if (!isInstanceDestroyed && !instanceReward.isRewarded()) {
								instanceReward.setRound(2);
								instanceReward.setRndZone();
								instanceReward.sendPacket(10, null);
								instanceReward.sendPacket(2, null);
								changeZone();
								//If you defeat a higher rank group in this round, you can earn additional points.
								sendMsgByRace(1401491, Race.PC_ALL, 2000);
								ThreadPoolManager.getInstance().schedule(new Runnable() {
									@Override
									public void run() {
										if (!isInstanceDestroyed && !instanceReward.isRewarded()) {
											instanceReward.setRound(3);
											instanceReward.setRndZone();
											instanceReward.sendPacket(10, null);
											instanceReward.sendPacket(2, null);
											changeZone();
											//If you defeat a higher rank group in this round, you can earn additional points.
											sendMsgByRace(1401491, Race.PC_ALL, 2000);
											ThreadPoolManager.getInstance().schedule(new Runnable() {
												@Override
												public void run() {
													if (!isInstanceDestroyed && !instanceReward.isRewarded()) {
														instanceReward.setInstanceScoreType(InstanceScoreType.END_PROGRESS);
														reward();
														instanceReward.sendPacket(5, null);
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
	
	protected void spawnRings() {
	}
	
	private boolean canStart() {
		if (instance.getPlayersInside().size() < 2) {
			onInstanceDestroy();
			//Unavailable to use when you're alone.
			sendMsgByRace(1403045, Race.PC_ALL, 0);
			instanceReward.setInstanceScoreType(InstanceScoreType.END_PROGRESS);
			reward();
			instanceReward.sendPacket(5, null);
			return false;
		}
		return true;
	}
	
	private void changeZone() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				for (Player player : instance.getPlayersInside()) {
					instanceReward.portToPosition(player);
					instanceReward.sendPacket(4, player.getObjectId());
				}
			}
		}, 1000);
	}
	
	private void sendPacket(final AionServerPacket packet) {
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				PacketSendUtility.sendPacket(player, packet);
			}
		});
	}
	
	@Override
	public void onLeaveInstance(Player player) {
		clearDebuffs(player);
		PvPArenaPlayerReward playerReward = instanceReward.getPlayerReward(player.getObjectId());
		if (playerReward != null) {
			playerReward.endBoostMoraleEffect(player);
			instanceReward.clearPosition(playerReward.getPosition(), Boolean.FALSE);
			instanceReward.removePlayerReward(playerReward);
		}
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
	
	protected void reward() {
		if (instanceReward.canRewarded()) {
			for (Player player : instance.getPlayersInside()) {
				HarmonyGroupReward group = instanceReward.getHarmonyGroupReward(player.getObjectId());
				float playerRate = player.getRates().getGloryRewardRate();
				//<Abyss Points>
				AbyssPointsService.addAp(player, group.getBasicAP() + group.getRankingAP() + (int) (group.getScoreAP() * playerRate));
				//<Glory Points>
				//AbyssPointsService.addGp(player, group.getBasicGP() + group.getRankingGP() + (int) (group.getScoreGP() * playerRate));
				int courage = group.getBasicCourage() + group.getRankingCourage() + (int) (group.getScoreCourage()* playerRate);
				if (courage != 0) {
					ItemService.addItem(player, 186000137, courage);
				}
				int gloryTicket = group.getGloryTicket();
				if (gloryTicket != 0) {
					ItemService.addItem(player, 186000185, gloryTicket);
				}
				int infinity = group.getBasicInfinity() + group.getRankingInfinity() + (int) (group.getScoreInfinity()* playerRate);
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
						if (PlayerActions.isAlreadyDead(player)) {
							PlayerReviveService.duelRevive(player);
						}
						onExitInstance(player);
					}
					AutoGroupService.getInstance().unRegisterInstance(instanceId);
				}
			}
		}, 10000);
	}
	
	@Override
	public boolean onDie(Player player, Creature lastAttacker) {
		PvPArenaPlayerReward ownerReward = instanceReward.getPlayerReward(player.getObjectId());
		ownerReward.endBoostMoraleEffect(player);
		ownerReward.applyBoostMoraleEffect(player);
		instanceReward.sendPacket(4, player.getObjectId());
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()), true);
		PacketSendUtility.sendPacket(player, new SM_DIE(false, false, 0, 8));
		if (lastAttacker != null && lastAttacker != player) {
			if (lastAttacker instanceof Player) {
				Player winner = (Player) lastAttacker;
				Integer winnerObj = winner.getObjectId();
				instanceReward.getHarmonyGroupReward(winnerObj).addPvPKillToPlayer();
				int worldId = winner.getWorldId();
				QuestEngine.getInstance().onKillInWorld(new QuestEnv(player, winner, 0, 0), worldId);
			}
		}
		updatePoints(player);
		return true;
	}
	
	@Override
	public void handleUseItemFinish(Player player, Npc npc) {
		final Integer object = player.getObjectId();
		final HarmonyGroupReward group = instanceReward.getHarmonyGroupReward(object);
		if (!instanceReward.isStartProgress() || group == null) {
			return;
		}
		int rewardetPoints = getNpcBonus(npc.getNpcId());
		int skill = instanceReward.getNpcBonusSkill(npc.getNpcId());
		if (skill != 0) {
			useSkill(npc, player, skill >> 8, skill & 0xFF);
		}
		group.addPoints(rewardetPoints);
		sendSystemMsg(player, npc, rewardetPoints);
		instanceReward.sendPacket(10, object);
	}
	
	protected void useSkill(Npc npc, Player player, int skillId, int level) {
		SkillEngine.getInstance().getSkill(npc, skillId, level, player).useNoAnimationSkill();
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
	public void onInstanceDestroy() {
		isInstanceDestroyed = true;
		instanceReward.clear();
	}
}