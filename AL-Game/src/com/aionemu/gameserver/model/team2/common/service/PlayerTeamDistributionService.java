package com.aionemu.gameserver.model.team2.common.service;

import java.util.*;

import com.google.common.base.Predicate;

import com.aionemu.commons.utils.Rnd;

import com.aionemu.gameserver.configs.main.*;
import com.aionemu.gameserver.controllers.attack.AggroInfo;
import com.aionemu.gameserver.controllers.attack.AggroList;
import com.aionemu.gameserver.model.ingameshop.InGameShopEn;
import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RewardType;
import com.aionemu.gameserver.model.gameobjects.player.XPCape;
import com.aionemu.gameserver.model.team2.TemporaryPlayerTeam;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.player.*;
import com.aionemu.gameserver.services.abyss.*;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.*;
import com.aionemu.gameserver.world.*;
import com.aionemu.gameserver.utils.stats.StatFunctions;

public class PlayerTeamDistributionService
{
	public static void doReward(TemporaryPlayerTeam<?> team, float damagePercent, Npc owner, AionObject winner) {
		if (team == null || owner == null) {
			return;
		}
		PlayerTeamRewardStats filteredStats = new PlayerTeamRewardStats(owner);
		team.applyOnMembers(filteredStats);
		if (filteredStats.players.isEmpty() || !filteredStats.hasLivingPlayer) {
			return;
		}
		long expReward;
		int kinahCount = 0;
		if (filteredStats.players.size() + filteredStats.mentorCount == 1) {
			expReward = (long) (StatFunctions.calculateSoloExperienceReward(filteredStats.players.get(0), owner));
		} else {
			expReward = (long) (StatFunctions.calculateGroupExperienceReward(filteredStats.highestLevel, owner));
		}
		//Party Bonus:
		//2 Members 10%
		int size = filteredStats.players.size();
		int bonus = 100;
		if (size > 1) {
			bonus = 150 + (size - 2) * 10;
		} for (Player member: filteredStats.players) {
			if (member.isMentor() || member.getLifeStats().isAlreadyDead()) {
				continue;
			}
			//Aura Of Growth + Berdin's Star.
			if (member.getWorldId() == WorldMapType.ILUMA.getId() ||
			    member.getWorldId() == WorldMapType.NORSVOLD.getId()) {
				if (Rnd.chance(RateConfig.AURA_OF_GROWTH)) {
					member.getCommonData().addGrowthEnergy(1060000 * 8);
					PacketSendUtility.sendPacket(member, new SM_STATS_INFO(member));
				}
			}
			//Auto Drop Kinah.
     		if (CustomConfig.AUTO_KINAH_ENABLED) {
				switch (member.getWorldId()) {
					case 210010000: //Poeta.
					case 220010000: //Ishalgen.
						if (member.getLevel() < owner.getLevel() + 5) {
							kinahCount = Rnd.get(100, 1500) * member.getLevel();
						} else if (member.getLevel() > owner.getLevel() + 5) {
							kinahCount = 1000;
						}
					break;
					case 210030000: //Verteron.
					case 220030000: //Altgard.
						if (member.getLevel() < owner.getLevel() + 5) {
							kinahCount = Rnd.get(100, 3000) * member.getLevel();
						} else if (member.getLevel() > owner.getLevel() + 5) {
							kinahCount = 1000;
						}
					break;
					case 210020000: //Eltnen.
					case 220020000: //Morheim.
						if (member.getLevel() < owner.getLevel() + 5) {
							kinahCount = Rnd.get(100, 4500) * member.getLevel();
						} else if (member.getLevel() > owner.getLevel() + 5) {
							kinahCount = 1000;
						}
					break;
					case 210040000: //Heiron.
					case 220040000: //Beluslan.
						if (member.getLevel() < owner.getLevel() + 5) {
							kinahCount = Rnd.get(100, 5000) * member.getLevel();
						} else if (member.getLevel() > owner.getLevel() + 5) {
							kinahCount = 1000;
						}
					break;
					case 210060000: //Theobomos.
					case 220050000: //Brushtonin.
						if (member.getLevel() < owner.getLevel() + 5) {
							kinahCount = Rnd.get(100, 5500) * member.getLevel();
						} else if (member.getLevel() > owner.getLevel() + 5) {
							kinahCount = 1000;
						}
					break;
					case 210050000: //Inggison.
					case 220070000: //Gelkmaros.
						if (member.getLevel() < owner.getLevel() + 5) {
							kinahCount = Rnd.get(100, 6500) * member.getLevel();
						} else if (member.getLevel() > owner.getLevel() + 5) {
							kinahCount = 1000;
						}
					break;
					case 210070000: //Cygnea.
					case 220080000: //Enshar.
						if (member.getLevel() < owner.getLevel() + 5) {
							kinahCount = Rnd.get(100, 8000) * member.getLevel();
						} else if (member.getLevel() > owner.getLevel() + 5) {
							kinahCount = 1000;
						}
					break;
					case 400010000: //Reshanta.
					case 400020000: //Belus.
					case 400040000: //Aspida.
					case 400050000: //Atanatos.
					case 400060000: //Disillon.
						if (member.getLevel() < owner.getLevel() + 5) {
							kinahCount = Rnd.get(100, 10000) * member.getLevel();
						} else if (member.getLevel() > owner.getLevel() + 5) {
							kinahCount = 1000;
						}
					break;
					case 600090000: //Kaldor.
					case 600100000: //Levinshor.
						if (member.getLevel() < owner.getLevel() + 5) {
							kinahCount = Rnd.get(100, 15000) * member.getLevel();
						} else if (member.getLevel() > owner.getLevel() + 5) {
							kinahCount = 1000;
						}
					break;
					case 210100000: //Iluma.
					case 220110000: //Norsvold.
						if (member.getLevel() < owner.getLevel() + 5) {
							kinahCount = Rnd.get(100, 17500) * member.getLevel();
						} else if (member.getLevel() > owner.getLevel() + 5) {
							kinahCount = 1000;
						}
					break;
					case 210090000: //Idian Depths E.
					case 220100000: //Idian Depths A.
						if (member.getLevel() < owner.getLevel() + 5) {
							kinahCount = Rnd.get(100, 20000) * member.getLevel();
						} else if (member.getLevel() > owner.getLevel() + 5) {
							kinahCount = 1000;
						}
					break;
					default:
						kinahCount = 0;
					break;
				} if (member.isInInstance() && member.getLevel() < owner.getLevel() + 5) {
					kinahCount = Rnd.get(100, 1000) * member.getLevel();
				} else if (member.isInInstance() && member.getLevel() > owner.getLevel() + 5) {
					kinahCount = 1000;
				}
				member.getInventory().increaseKinah(kinahCount);
     		}
			long rewardXp = (long) (expReward * bonus * member.getLevel()) / (filteredStats.partyLvlSum * 100);
			int rewardDp = StatFunctions.calculateGroupDPReward(member, owner);
			float rewardAp = 1;
			//Players 10 levels below highest member get 0 reward.
			if (filteredStats.highestLevel - member.getLevel() >= 10) {
				rewardXp = 0;
				rewardDp = 0;
			} else if (filteredStats.mentorCount > 0) {
				int cape = XPCape.values()[(int) member.getLevel()].value();
				if (cape < rewardXp) {
					rewardXp = cape;
				}
			}
			rewardXp *= damagePercent;
			rewardDp *= damagePercent;
			rewardAp *= damagePercent;
			//Reward XP Group (New system, Exp Retail NA)
			switch (member.getWorldId()) {
				case 301540000: //Archives Of Eternity.
				case 301550000: //Cradle Of Eternity.
				case 301600000: //Adma's Fall.
				case 301610000: //Theobomos Test Chamber.
				case 301620000: //Drakenseer's Lair.
				case 301650000: //Ashunatal Dredgion.
				case 301660000: //Fallen Poeta.
					member.getCommonData().addExp(Rnd.get(480000, 550000), RewardType.GROUP_HUNTING, owner.getObjectTemplate().getNameId());
				break;
				case 210100000: //Iluma.
				case 220110000: //Norsvold.
					AbyssPointsService.addAp(member, owner, Rnd.get(60, 100));
					member.getCommonData().addExp(Rnd.get(480000, 550000), RewardType.GROUP_HUNTING, owner.getObjectTemplate().getNameId());
				break;
				case 600090000: //Kaldor.
				case 600100000: //Levinshor.
					member.getCommonData().addExp(Rnd.get(50000, 100000), RewardType.GROUP_HUNTING, owner.getObjectTemplate().getNameId());
				break;
				default:
					member.getCommonData().addExp(rewardXp, RewardType.GROUP_HUNTING, owner.getObjectTemplate().getNameId());
				break;
			}
			member.getCommonData().addDp(rewardDp);
			if (owner.isRewardAP() && !(filteredStats.mentorCount > 0 && CustomConfig.MENTOR_GROUP_AP)) {
				rewardAp *= StatFunctions.calculatePvEApGained(member, owner);
				int ap = (int) rewardAp / filteredStats.players.size();
				if (ap >= 1) {
					AbyssPointsService.addAp(member, owner, ap);
				}
			}
		}
		Player mostDamagePlayer = owner.getAggroList().getMostPlayerDamageOfMembers(team.getMembers(), filteredStats.highestLevel);
		if (mostDamagePlayer == null) {
			return;
		} if (winner.equals(team) && (!owner.getAi2().getName().equals("chest") || filteredStats.mentorCount == 0)) {
			DropRegistrationService.getInstance().registerDrop(owner, mostDamagePlayer, filteredStats.highestLevel, filteredStats.players);
		}
	}
	
	private static class PlayerTeamRewardStats implements Predicate<Player> {
		final List<Player> players = new ArrayList<Player>();
		int partyLvlSum = 0;
		int highestLevel = 0;
		int mentorCount = 0;
		boolean hasLivingPlayer = false;
		Npc owner;
		public PlayerTeamRewardStats(Npc owner) {
			this.owner = owner;
		}
		@Override
		public boolean apply(Player member) {
			if (member.isOnline()) {
				if (MathUtil.isIn3dRange(member, owner, GroupConfig.GROUP_MAX_DISTANCE)) {
					QuestEngine.getInstance().onKill(new QuestEnv(owner, member, 0, 0));
					if (member.isMentor()) {
						mentorCount++;
						return true;
					}
					if (!hasLivingPlayer && !member.getLifeStats().isAlreadyDead())
						hasLivingPlayer = true;
					players.add(member);
					partyLvlSum += member.getLevel();
					if (member.getLevel() > highestLevel)
						highestLevel = member.getLevel();
				}
			}
			return true;
		}
	}
}