/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javolution.util.FastMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.main.EventsConfig;
import com.aionemu.gameserver.configs.main.GroupConfig;
import com.aionemu.gameserver.configs.main.LoggingConfig;
import com.aionemu.gameserver.configs.main.PunishmentConfig;
import com.aionemu.gameserver.configs.main.PvPConfig;
import com.aionemu.gameserver.controllers.attack.AggroInfo;
import com.aionemu.gameserver.controllers.attack.KillList;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RewardType;
import com.aionemu.gameserver.model.ingameshop.InGameShopEn;
import com.aionemu.gameserver.model.team2.alliance.PlayerAlliance;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.loginserver.LoginServer;
import com.aionemu.gameserver.network.loginserver.serverpackets.SM_ACCOUNT_TOLL_INFO;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.services.dreamergames.WorldMapHigherGP;
import com.aionemu.gameserver.services.events.CrazyDaevaService;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.audit.AuditLogger;
import com.aionemu.gameserver.utils.stats.AbyssRankEnum;
import com.aionemu.gameserver.utils.stats.StatFunctions;
import com.aionemu.gameserver.world.World;

/**
 * @author Sarynth
 */
public class PvpService {

  private static Logger log = LoggerFactory.getLogger("KILL_LOG");
	public static final PvpService getInstance() {
		return SingletonHolder.instance;
	}

	private FastMap<Integer, KillList> pvpKillLists;

	private PvpService() {
		pvpKillLists = new FastMap<Integer, KillList>();
	}

	/**
	 * @param winnerId
	 * @param victimId
	 * @return
	 */
	private int getKillsFor(int winnerId, int victimId) {
		KillList winnerKillList = pvpKillLists.get(winnerId);

		if (winnerKillList == null)
			return 0;
		return winnerKillList.getKillsFor(victimId);
	}

	/**
	 * @param winnerId
	 * @param victimId
	 */
	private void addKillFor(int winnerId, int victimId) {
		KillList winnerKillList = pvpKillLists.get(winnerId);
		if (winnerKillList == null) {
			winnerKillList = new KillList();
			pvpKillLists.put(winnerId, winnerKillList);
		}
		winnerKillList.addKillFor(victimId);
	}

	/**
	 * @param victim
	 */
	public void doReward(Player victim) {
		// winner is the player that receives the kill count
		final Player winner = victim.getAggroList().getMostPlayerDamage();

		int totalDamage = victim.getAggroList().getTotalDamage();

		if (totalDamage == 0 || winner == null) {
			return;
		}

		// Add Player Kill to record.
			// Pvp Kill Reward.
		int reduceap = PunishmentConfig.PUNISHMENT_REDUCEAP;
		if (reduceap < 0)
			reduceap *= -1;
		if (reduceap > 100) {
			reduceap = 100;
		}

		// Announce that player has died.
			
		//Kill-log
		if ((LoggingConfig.LOG_PL) || (reduceap > 0)) {
      String ip1 = winner.getClientConnection().getIP();
      String mac1 = winner.getClientConnection().getMacAddress();
      String ip2 = victim.getClientConnection().getIP();
      String mac2 = victim.getClientConnection().getMacAddress();
      if ((mac1 != null) && (mac2 != null)) {
        if ((ip1.equalsIgnoreCase(ip2)) && (mac1.equalsIgnoreCase(mac2))) {
					AuditLogger.info(winner, "Power Leveling : " + winner.getName() + " with " + victim.getName() + ", They have the sames ip=" + ip1 + " and mac=" + mac1 + ".");
					if (reduceap > 0) {
						int win_ap = winner.getAbyssRank().getAp() * reduceap / 100;
						int vic_ap = victim.getAbyssRank().getAp() * reduceap / 100;
						AbyssPointsService.addAp(winner, -win_ap);
						AbyssPointsService.addAp(victim, -vic_ap);
						PacketSendUtility.sendMessage(winner, "[PL-AP] You lost " + reduceap + "% of your total ap");
						PacketSendUtility.sendMessage(victim, "[PL-AP] You lost " + reduceap + "% of your total ap");
        }
					return;
				}
				if (ip1.equalsIgnoreCase(ip2)) {
					AuditLogger.info(winner, "Possible Power Leveling : " + winner.getName() + " with " + victim.getName() + ", They have the sames ip=" + ip1 + ".");
					AuditLogger.info(winner, "Check if " + winner.getName() + " and " + victim.getName() + " are Brothers-Sisters-Lovers-dogs-cats...");
        }

			}
		}
		if (winner.getLevel() - victim.getLevel() <= PvPConfig.MAX_AUTHORIZED_LEVEL_DIFF) {
			if (getKillsFor(winner.getObjectId(), victim.getObjectId()) < PvPConfig.CHAIN_KILL_NUMBER_RESTRICTION) {
				winner.getAbyssRank().updateKillCounts();
				if (PvPConfig.ENABLE_MEDAL_REWARDING) {
					if (Rnd.get() * 100 < PvPRewardService.getMedalRewardChance(winner, victim)) {
						int medalId = PvPRewardService.getRewardId(winner, victim, false);
						long medalCount = PvPRewardService.getRewardQuantity(winner, victim);
						ItemService.addItem(winner, medalId, medalCount);
						if (winner.getInventory().getItemCountByItemId(medalId) > 0) {
							if (medalCount == 1)
								PacketSendUtility.sendPacket(winner, new SM_SYSTEM_MESSAGE(1390000, new DescriptionId(medalId)));
							else {
								PacketSendUtility.sendPacket(winner, new SM_SYSTEM_MESSAGE(1390005, medalCount, new DescriptionId(medalId)));
							}
						}
					}
				}
				if ((PvPConfig.ENABLE_TOLL_REWARDING) && (Rnd.get() * 100.0F < PvPRewardService.getTollRewardChance(winner, victim))) {
					int qt = PvPRewardService.getTollQuantity(winner, victim);
					InGameShopEn.getInstance().addToll(winner, qt);
					if (qt == 1)
						PacketSendUtility.sendBrightYellowMessage(winner, "You obtained " + qt + " point toll.");
					else {
						PacketSendUtility.sendBrightYellowMessage(winner, "You obtained " + qt + " point toll.");
					}
				}
				
				// Reward Pk Kill
				if (victim.isBandit()) {
					long toll = winner.getClientConnection().getAccount().getToll() + PvPConfig.TOLL_PK_COST;
					
					if (LoginServer.getInstance().sendPacket(new SM_ACCOUNT_TOLL_INFO(toll, winner.getClientConnection().getAccount().getLuna(), winner.getAcountName()))) {
						winner.getClientConnection().getAccount().setToll(toll);
						PacketSendUtility.sendMessage(winner, "You've received " + PvPConfig.TOLL_PK_COST + " tolls from MAD bunny!");
					}
					
					Iterator<Player> iter = World.getInstance().getPlayersIterator();
					String message = victim.getName() +"'s madness ended by " + winner.getName();
					Player target;
					while (iter.hasNext()) {
						target = iter.next();
						PacketSendUtility.sendBrightYellowMessageOnCenter(target, message);
					}
					
				}
				
				// War System
	     		Calendar calendar = Calendar.getInstance();
	     		if (PvPConfig.WAR_PVP_SYSTEM) {
	     		    if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY || Calendar.DAY_OF_WEEK == Calendar.TUESDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
	     		        if (winner.getWorldId() == PvPConfig.WAR_MAPID) {
	     		        	AbyssPointsService.addAp(winner, +PvPConfig.WAR_AP_REWARD);
	     			        AbyssPointsService.addGp(winner, +PvPConfig.WAR_GP_REWARD);
	     			        PacketSendUtility.sendMessage(winner, "[WAR] You kill player" + victim.getName() + "!");
	     			        PacketSendUtility.sendMessage(victim, "[WAR] You killed by" + winner.getName() + "!");
	     		        }
	     		    }
	     	    }
	     		
	     		// PvP Advanced system
	     		Calendar calendar1 = Calendar.getInstance();
	     		if (PvPConfig.ADVANCED_PVP_SYSTEM) {
	     		    if (calendar1.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY || Calendar.DAY_OF_WEEK == Calendar.TUESDAY || calendar1.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY || calendar1.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY || calendar1.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY || calendar1.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || calendar1.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
	     		        if (winner.getWorldId() == PvPConfig.ADVANCED_MAPID) {
	     		        	//AbyssPointsService.addAp(winner, +PvPConfig.WAR_AP_REWARD);
	     			        //AbyssPointsService.addGp(winner, +PvPConfig.WAR_GP_REWARD);
	     		        	ItemService.addItem(winner, PvPConfig.ADVANCED_ITEM_REWARD, PvPConfig.ADVANCED_ITEM_COUNT);
	     			        PacketSendUtility.sendMessage(winner, "[PvP System] You kill player" + victim.getName() + "!");
	     			        PacketSendUtility.sendMessage(victim, "[PvP System] You killed by" + winner.getName() + "!");
	     		        }
	     		    }
	     	    }
				
				if (PvPConfig.GENOCIDE_SPECIAL_REWARDING != 0) {
					switch (PvPConfig.GENOCIDE_SPECIAL_REWARDING) {
						case 1:
							if ((winner.getSpreeLevel() <= 2) || (Rnd.get() * 100 >= PvPConfig.SPECIAL_REWARD_CHANCE))
								break;
							int abyssId = PvPRewardService.getRewardId(winner, victim, true);
							ItemService.addItem(winner, abyssId, 1L);
							log.info("[PvP][Advanced] {Player : " + winner.getName() + "} has won " + abyssId + " for killing {Player : " + victim.getName() + "}");
							break;
						default:
							if ((winner.getSpreeLevel() <= 2) || (Rnd.get() * 100 >= PvPConfig.SPECIAL_REWARD_CHANCE))
								break;
							ItemService.addItem(winner, PvPConfig.GENOCIDE_SPECIAL_REWARDING, 1L);
							log.info("[PvP][Advanced] {Player : " + winner.getName() + "} has won " + PvPConfig.GENOCIDE_SPECIAL_REWARDING + " for killing {Player : " + victim.getName() + "}");
							break;
					}
				}
			}
			else {
				PacketSendUtility.sendMessage(winner, "You will not gain anything by killing the player " + victim.getName() + " for the rest of the day because you kill too many times.");
			}
		}
		int playerDamage = 0;
		boolean success = false;

		// Distribute AP to groups and players that had damage.
		for (AggroInfo aggro : victim.getAggroList().getFinalDamageList(true)) {
			if (aggro.getAttacker() instanceof Player) {
				success = rewardPlayer(victim, totalDamage, aggro);
			}
			else if (aggro.getAttacker() instanceof PlayerGroup) {
				success = rewardPlayerGroup(victim, totalDamage, aggro);
			}
			else if (aggro.getAttacker() instanceof PlayerAlliance) {
				success = rewardPlayerAlliance(victim, totalDamage, aggro);
			}

			// Add damage last, so we don't include damage from same race. (Duels, Arena)
			if (success)
				playerDamage += aggro.getDamage();
		}
		ProtectorConquerorService.getInstance().updateRanks(winner, victim);

		// Apply lost AP to defeated player
		final int apLost = StatFunctions.calculatePvPApLost(victim, winner);
		final int apActuallyLost = (int) (apLost * playerDamage / totalDamage);

		if (apActuallyLost > 0) {
			AbyssPointsService.addAp(victim, -apActuallyLost);

		}
		if (CustomConfig.ENABLE_GP_LOSE && winner.getLevel() >= CustomConfig.REWARD_GP_MIN_LEVEL) {

            float worldGainedGP = 1;
            if (victim.getPosition().getMapId() == WorldMapHigherGP.getInstance().getMapId()) {
                worldGainedGP = WorldMapHigherGP.getInstance().getRateIncrease();
            } else if (victim.getPosition().getMapId() == WorldMapHigherGP.getInstance().getMapId2()) {
                worldGainedGP = WorldMapHigherGP.getInstance().getRateIncrease2();
            } else if (victim.getPosition().getMapId() == WorldMapHigherGP.getInstance().getMapId3()) {
                worldGainedGP = WorldMapHigherGP.getInstance().getRateIncrease3();
            } else if (victim.getPosition().getMapId() == WorldMapHigherGP.getInstance().getMapId4()) {
                worldGainedGP = WorldMapHigherGP.getInstance().getRateIncrease4();
            } else if (victim.getPosition().getMapId() == WorldMapHigherGP.getInstance().getMapId5()) {
                worldGainedGP = WorldMapHigherGP.getInstance().getRateIncrease5();
            }

            int rankDiff = victim.getAbyssRank().getRank().getDailyReduceGp() - winner.getAbyssRank().getRank().getDailyReduceGp();
            float gpLostRateModifier;
            float gpLostRate = CustomConfig.GP_RATE_MODIFIER;
            float gpActuallyLost;
            float membershipLostRate = 1; // victim.getRates().getGpLostPlayerRate();
            if (rankDiff >= 0) {
                gpLostRateModifier = CustomConfig.GP_RATE_LOST_EQUAL_HIGHER_RANK;
            } else {
                gpLostRateModifier = CustomConfig.GP_RATE_LOST_MINUS_RANK;
            }
            gpActuallyLost = Math.abs((((victim.getAbyssRank().getRank().getDailyReduceGp() + winner.getAbyssRank().getRank().getDailyReduceGp()) / gpLostRate) * worldGainedGP) * gpLostRateModifier);

            if (gpActuallyLost <= 0) {
                gpActuallyLost = Math.abs(2 * worldGainedGP) * gpLostRateModifier;
            }

            AbyssPointsService.addGp(victim, (int) (-gpActuallyLost * membershipLostRate));

        }

		PacketSendUtility.broadcastPacketAndReceive(victim, SM_SYSTEM_MESSAGE.STR_MSG_COMBAT_FRIENDLY_DEATH_TO_B(victim.getName(), winner.getName()));
		if (LoggingConfig.LOG_KILL)
			log.info("[KILL] Player [" + winner.getName() + "] killed [" + victim.getName() + "]");
	}
	/**
	 * @param victim
	 * @param totalDamage
	 * @param aggro
	 * @return true if group is not same race
	 */
	private boolean rewardPlayerGroup(Player victim, int totalDamage, AggroInfo aggro) {
        // Reward Group
        PlayerGroup group = ((PlayerGroup) aggro.getAttacker());
        Player winner = victim.getAggroList().getMostPlayerDamage();
        // Don't Reward Player of Same Faction.
        if (group == null) {
            return false;
        }
        // Don't Reward Player of Same Faction.
     	if (group.getRace() == victim.getRace() && !victim.isBandit())
     		return false;
        // Find group members in range
        List<Player> players = new ArrayList<>();
        // Find highest rank and level in local group
        int maxRank = AbyssRankEnum.GRADE9_SOLDIER.getId();
        int maxLevel = 0;
        for (Player member : group.getMembers()) {
            if (MathUtil.isIn3dRange(member, victim, GroupConfig.GROUP_MAX_DISTANCE)) {
                // Don't distribute AP to a dead player!
                if (!member.getLifeStats().isAlreadyDead()) {
                    players.add(member);
                    if (member.getLevel() > maxLevel) {
                        maxLevel = member.getLevel();
                    }
                    if (member.getAbyssRank().getRank().getId() > maxRank) {
                        maxRank = member.getAbyssRank().getRank().getId();
                    }
                }
            }
        }
        // They are all dead or out of range.
        if (players.isEmpty()) {
            return false;
        }
        int baseApReward = StatFunctions.calculatePvpApGained(victim, maxRank, maxLevel);
        int baseXpReward = StatFunctions.calculatePvpXpGained(victim, maxRank, maxLevel);
        int baseDpReward = StatFunctions.calculatePvpDpGained(victim, maxRank, maxLevel);

        float groupPercentage = (float) aggro.getDamage() / totalDamage;
        int apRewardPerMember = Math.round(baseApReward * groupPercentage / players.size());
        int xpRewardPerMember = Math.round(baseXpReward * groupPercentage / players.size());
        int dpRewardPerMember = Math.round(baseDpReward * groupPercentage / players.size());

        float gpPlayerReward;
        float worldGainedGP = 1;
        if (victim.getPosition().getMapId() == WorldMapHigherGP.getInstance().getMapId()) {
            worldGainedGP = WorldMapHigherGP.getInstance().getRateIncrease();
        } else if (victim.getPosition().getMapId() == WorldMapHigherGP.getInstance().getMapId2()) {
            worldGainedGP = WorldMapHigherGP.getInstance().getRateIncrease2();
        } else if (victim.getPosition().getMapId() == WorldMapHigherGP.getInstance().getMapId3()) {
            worldGainedGP = WorldMapHigherGP.getInstance().getRateIncrease3();
        } else if (victim.getPosition().getMapId() == WorldMapHigherGP.getInstance().getMapId4()) {
            worldGainedGP = WorldMapHigherGP.getInstance().getRateIncrease4();
        } else if (victim.getPosition().getMapId() == WorldMapHigherGP.getInstance().getMapId5()) {
            worldGainedGP = WorldMapHigherGP.getInstance().getRateIncrease5();
        }

        final int rankDiff = winner.getAbyssRank().getRank().getDailyReduceGp() - victim.getAbyssRank().getRank().getDailyReduceGp();
        float gpGaintRateModifier;
        float gpRate = CustomConfig.GP_RATE_MODIFIER;

        if (rankDiff >= 0) {
            gpGaintRateModifier = CustomConfig.GP_RATE_GAIN_EQUAL_HIGHER_RANK;
        } else {
            gpGaintRateModifier = CustomConfig.GP_RATE_GAIN_MINUS_RANK;
        }
        gpPlayerReward = (((winner.getAbyssRank().getRank().getDailyReduceGp() + victim.getAbyssRank().getRank().getDailyReduceGp()) / gpRate) * worldGainedGP) * gpGaintRateModifier;

        if (gpPlayerReward == 0 && rankDiff == 0) {
            gpPlayerReward = (5 * worldGainedGP) * gpGaintRateModifier;
        }
        for (Player member : players) {
            int memberApGain = 1;
            int memberXpGain = 1;
            int memberDpGain = 1;

            if (this.getKillsFor(member.getObjectId(), victim.getObjectId()) < PvPConfig.CHAIN_KILL_NUMBER_RESTRICTION) {
                if (apRewardPerMember > 0) {
                    memberApGain = Math.round(RewardType.AP_PLAYER.calcReward(member, apRewardPerMember));
                }
                if (xpRewardPerMember > 0) {
                    memberXpGain = Math.round(xpRewardPerMember * member.getRates().getXpPlayerGainRate());
                }
                if (dpRewardPerMember > 0) {
                    memberDpGain = Math.round(StatFunctions.adjustPvpDpGained(dpRewardPerMember, victim.getLevel(), member.getLevel()) * member.getRates().getDpPlayerRate());
                }
                if (PvPConfig.ENABLE_KILLING_SPREE_SYSTEM) {
    				Player winner1 = victim.getAggroList().getMostPlayerDamage();
    				PvPSpreeService.increaseRawKillCount(winner1);
                }

            }

            if (member.getWedding() != null) {
                Player partner = member.getWedding().getPartner();
                if (member.isMarried() && member.getPlayerGroup2().getMembers() == partner && member.getPlayerGroup2().getMembers().size() == 2) {
                    AbyssPointsService.addAp(member, victim, memberApGain + (memberApGain * 20 / 100)); //20% more AP for weddings
                } else {
                    AbyssPointsService.addAp(member, victim, memberApGain);
                }
            } else {
                AbyssPointsService.addAp(member, victim, memberApGain);
            }


            if (CustomConfig.ENABLE_GP_REWARD && victim.getLevel() >= CustomConfig.REWARD_GP_MIN_LEVEL) {
                try {
                    if (winner == member && winner.getLevel() >= CustomConfig.REWARD_GP_MIN_LEVEL) {
                        AbyssPointsService.addGp(member, victim, (int) (gpPlayerReward ));
                    } else {
                        if (member.getLevel() >= CustomConfig.REWARD_GP_MIN_LEVEL) {
                            AbyssPointsService.addGp(member, victim, (int) ((gpPlayerReward * CustomConfig.MEMBER_GP_GAIN) ));
                        }
                    }
                } catch (NullPointerException e) {
                    log.warn("Null data caught on reward group. " + e.getMessage(), e);
                }
            }

            member.getCommonData().addExp(memberXpGain, RewardType.PVP_KILL, victim.getName());
            member.getCommonData().addDp(memberDpGain);
            this.addKillFor(member.getObjectId(), victim.getObjectId());
          //notify Kill-Quests
			int worldId = member.getWorldId();
			QuestEngine.getInstance().onKillInWorld(new QuestEnv(victim, member, 0, 0), worldId);
			QuestEngine.getInstance().onKillRanked(new QuestEnv(victim, member, 0, 0), victim.getAbyssRank().getRank());
        }
        return true;
    }

	/**
	 * @param victim
	 * @param totalDamage
	 * @param aggro
	 * @return true if group is not same race
	 */
	private boolean rewardPlayerAlliance(Player victim, int totalDamage, AggroInfo aggro) {
        // Reward Alliance
        PlayerAlliance alliance = ((PlayerAlliance) aggro.getAttacker());
        Player winner = victim.getAggroList().getMostPlayerDamage();
        // Don't Reward Player of Same Faction.
        if (alliance.getLeaderObject().getRace() == victim.getRace()) {
            return false;
        }
        // Find group members in range
        List<Player> players = new ArrayList<>();
        // Find highest rank and level in local group
        int maxRank = AbyssRankEnum.GRADE9_SOLDIER.getId();
        int maxLevel = 0;

        for (Player member : alliance.getMembers()) {
            if (!member.isOnline()) {
                continue;
            }
            if (MathUtil.isIn3dRange(member, victim, GroupConfig.GROUP_MAX_DISTANCE)) {
                // Don't distribute AP to a dead player!
                if (!member.getLifeStats().isAlreadyDead()) {
                    players.add(member);
                    if (member.getLevel() > maxLevel) {
                        maxLevel = member.getLevel();
                    }
                    if (member.getAbyssRank().getRank().getId() > maxRank) {
                        maxRank = member.getAbyssRank().getRank().getId();
                    }
                }
            }
        }

        // They are all dead or out of range.
        if (players.isEmpty()) {
            return false;
        }

        int baseApReward = StatFunctions.calculatePvpApGained(victim, maxRank, maxLevel);
        int baseXpReward = StatFunctions.calculatePvpXpGained(victim, maxRank, maxLevel);
        int baseDpReward = StatFunctions.calculatePvpDpGained(victim, maxRank, maxLevel);

        float groupPercentage = (float) aggro.getDamage() / totalDamage;
        int apRewardPerMember = Math.round(baseApReward * groupPercentage / players.size());
        int xpRewardPerMember = Math.round(baseXpReward * groupPercentage / players.size());
        int dpRewardPerMember = Math.round(baseDpReward * groupPercentage / players.size());

        float gpPlayerReward;
        float worldGainedGP = 1;
        if (victim.getPosition().getMapId() == WorldMapHigherGP.getInstance().getMapId()) {
            worldGainedGP = WorldMapHigherGP.getInstance().getRateIncrease();
        } else if (victim.getPosition().getMapId() == WorldMapHigherGP.getInstance().getMapId2()) {
            worldGainedGP = WorldMapHigherGP.getInstance().getRateIncrease2();
        } else if (victim.getPosition().getMapId() == WorldMapHigherGP.getInstance().getMapId3()) {
            worldGainedGP = WorldMapHigherGP.getInstance().getRateIncrease3();
        } else if (victim.getPosition().getMapId() == WorldMapHigherGP.getInstance().getMapId4()) {
            worldGainedGP = WorldMapHigherGP.getInstance().getRateIncrease4();
        } else if (victim.getPosition().getMapId() == WorldMapHigherGP.getInstance().getMapId5()) {
            worldGainedGP = WorldMapHigherGP.getInstance().getRateIncrease5();
        }
        final int rankDiff = winner.getAbyssRank().getRank().getDailyReduceGp() - victim.getAbyssRank().getRank().getDailyReduceGp();
        float gpGaintRateModifier;
        float gpRate = CustomConfig.GP_RATE_MODIFIER;


        if (rankDiff >= 0) {
            gpGaintRateModifier = CustomConfig.GP_RATE_GAIN_EQUAL_HIGHER_RANK;
        } else {
            gpGaintRateModifier = CustomConfig.GP_RATE_GAIN_MINUS_RANK;
        }
        gpPlayerReward = (((winner.getAbyssRank().getRank().getDailyReduceGp() + victim.getAbyssRank().getRank().getDailyReduceGp()) / gpRate) * worldGainedGP) * gpGaintRateModifier;
        if (gpPlayerReward == 0 && rankDiff == 0) {
            gpPlayerReward = (5 * worldGainedGP) * gpGaintRateModifier;
        }

        for (Player member : players) {
            int memberApGain = 1;
            int memberXpGain = 1;
            int memberDpGain = 1;
            if (this.getKillsFor(member.getObjectId(), victim.getObjectId()) < PvPConfig.CHAIN_KILL_NUMBER_RESTRICTION) {
                if (apRewardPerMember > 0) {
                    memberApGain = Math.round(RewardType.AP_PLAYER.calcReward(member, apRewardPerMember));
                }
                if (xpRewardPerMember > 0) {
                    memberXpGain = Math.round(xpRewardPerMember * member.getRates().getXpPlayerGainRate());
                }
                if (dpRewardPerMember > 0) {
                    memberDpGain = Math.round(StatFunctions.adjustPvpDpGained(dpRewardPerMember, victim.getLevel(), member.getLevel()) * member.getRates().getDpPlayerRate());
                }
                if (PvPConfig.ENABLE_KILLING_SPREE_SYSTEM) {
					Player winner1 = victim.getAggroList().getMostPlayerDamage();
					PvPSpreeService.increaseRawKillCount(winner1);
				}
            }
            AbyssPointsService.addAp(member, victim, memberApGain);

            if (CustomConfig.ENABLE_GP_REWARD) {
                try {
                    if (winner == member && winner.getLevel() >= CustomConfig.REWARD_GP_MIN_LEVEL) {
                        AbyssPointsService.addGp(winner, victim, (int) (gpPlayerReward));
                    } else {
                        if (member.getLevel() >= CustomConfig.REWARD_GP_MIN_LEVEL) {
                            AbyssPointsService.addGp(member, victim, (int) ((gpPlayerReward * CustomConfig.ALLIANCE_GP_GAIN)));
                        }
                    }
                } catch (NullPointerException e) {
                    log.warn("Null data caught on Alliance Reward.", e);
                }
            }


            member.getCommonData().addExp(memberXpGain, RewardType.PVP_KILL, victim.getName());
            member.getCommonData().addDp(memberDpGain);

            this.addKillFor(member.getObjectId(), victim.getObjectId());
          //notify Kill-Quests
			int worldId = member.getWorldId();
			QuestEngine.getInstance().onKillInWorld(new QuestEnv(victim, member, 0, 0), worldId);
			QuestEngine.getInstance().onKillRanked(new QuestEnv(victim, member, 0, 0), victim.getAbyssRank().getRank());
        }
        return true;
    }

	/**
	 * @param victim
	 * @param totalDamage
	 * @param aggro
	 * @return true if player is not same race
	 */
	private boolean rewardPlayer(Player victim, int totalDamage, AggroInfo aggro) {
		// Reward Player
		Player winner = ((Player) aggro.getAttacker());
		Player mostDamagePlayer = victim.getAggroList().getMostPlayerDamage();
		
		if ((winner.getRace() == victim.getRace() && !victim.isBandit() && !winner.isBandit()) || (!MathUtil.isIn3dRange(winner, victim, GroupConfig.GROUP_MAX_DISTANCE)) || (winner.getLifeStats().isAlreadyDead())) {
			return false;
		}
		int baseApReward = 1;
		//int baseGpReward = 1;
		int baseXpReward = 1;
		int baseDpReward = 1;

		if (this.getKillsFor(winner.getObjectId(), victim.getObjectId()) < PvPConfig.CHAIN_KILL_NUMBER_RESTRICTION) {
			baseApReward = StatFunctions.calculatePvpApGained(victim, winner.getAbyssRank().getRank().getId(), winner.getLevel());
			//baseGpReward = StatFunctions.calculatePvpGpGained(victim, winner.getAbyssRank().getRank().getId(), winner.getLevel());
			baseXpReward = StatFunctions.calculatePvpXpGained(victim, winner.getAbyssRank().getRank().getId(), winner.getLevel());
			baseDpReward = StatFunctions.calculatePvpDpGained(victim, winner.getAbyssRank().getRank().getId(), winner.getLevel());
			if (PvPConfig.ENABLE_KILLING_SPREE_SYSTEM) {
				Player winners = victim.getAggroList().getMostPlayerDamage();
				PvPSpreeService.increaseRawKillCount(winners);
			}
			if (EventsConfig.ENABLE_CRAZY) {
				if (winner.getRace() != victim.getRace())
					CrazyDaevaService.getInstance().increaseRawKillCount(winner);
			}
		}

		int apPlayerReward = Math.round(baseApReward * winner.getRates().getApPlayerGainRate() * aggro.getDamage() / totalDamage);
		//int gpPlayerReward = Math.round(baseGpReward * aggro.getDamage() / totalDamage);
		//gpPlayerReward = (int) RewardType.GP_PLAYER.calcReward(winner, gpPlayerReward);
		int xpPlayerReward = Math.round(baseXpReward * winner.getRates().getXpPlayerGainRate() * aggro.getDamage() / totalDamage);
		int dpPlayerReward = Math.round(baseDpReward * winner.getRates().getDpPlayerRate() * aggro.getDamage() / totalDamage);

		AbyssPointsService.addAp(winner, victim, apPlayerReward);
		//AbyssPointsService.addGp(winner, victim, gpPlayerReward);
		if (CustomConfig.ENABLE_GP_REWARD) {
            float gpPlayerReward = 0;
            try {
                float worldGainedGP = 1;
                if (victim.getPosition().getMapId() == WorldMapHigherGP.getInstance().getMapId()) {
                    worldGainedGP = WorldMapHigherGP.getInstance().getRateIncrease();
                } else if (victim.getPosition().getMapId() == WorldMapHigherGP.getInstance().getMapId2()) {
                    worldGainedGP = WorldMapHigherGP.getInstance().getRateIncrease2();
                } else if (victim.getPosition().getMapId() == WorldMapHigherGP.getInstance().getMapId3()) {
                    worldGainedGP = WorldMapHigherGP.getInstance().getRateIncrease3();
                } else if (victim.getPosition().getMapId() == WorldMapHigherGP.getInstance().getMapId4()) {
                    worldGainedGP = WorldMapHigherGP.getInstance().getRateIncrease4();
                } else if (victim.getPosition().getMapId() == WorldMapHigherGP.getInstance().getMapId5()) {
                    worldGainedGP = WorldMapHigherGP.getInstance().getRateIncrease5();
                }

                final int rankDiff = winner.getAbyssRank().getRank().getDailyReduceGp() - victim.getAbyssRank().getRank().getDailyReduceGp();
                float gpGaintRateModifier;
                float gpRate = CustomConfig.GP_RATE_MODIFIER;


                if (rankDiff >= 0) {
                    gpGaintRateModifier = CustomConfig.GP_RATE_GAIN_EQUAL_HIGHER_RANK;
                } else {
                    gpGaintRateModifier = CustomConfig.GP_RATE_GAIN_MINUS_RANK;
                }
                gpPlayerReward = (((winner.getAbyssRank().getRank().getDailyReduceGp() + victim.getAbyssRank().getRank().getDailyReduceGp()) / gpRate) * worldGainedGP) * gpGaintRateModifier;

                if (gpPlayerReward == 0) {
                    gpPlayerReward = (5 * worldGainedGP) * gpGaintRateModifier;
                }
                String ip1 = winner.getClientConnection().getIP();
                String ip2 = victim.getClientConnection().getIP();

                if (ip1 != null || ip2 != null) {
                    if (ip1.equalsIgnoreCase(ip2)) {
                        if (winner == mostDamagePlayer) {
                            AbyssPointsService.addGp(winner, victim, (int) (gpPlayerReward));
                        } else {
                            AbyssPointsService.addGp(winner, victim, (int) ((gpPlayerReward ) * CustomConfig.GP_GAIN_AGGRO_ATTACKER));
                        }
                    } else {
                        if (winner == mostDamagePlayer) {
                            AbyssPointsService.addGp(winner, victim, (int) (gpPlayerReward ));
                        } else {
                            AbyssPointsService.addGp(winner, victim, (int) ((gpPlayerReward) * CustomConfig.GP_GAIN_AGGRO_ATTACKER));
                        }
                    }
                } else { ;
                    if (winner == mostDamagePlayer) {
                        AbyssPointsService.addGp(winner, victim, (int) (gpPlayerReward ));
                    } else {
                        AbyssPointsService.addGp(winner, victim, (int) ((gpPlayerReward) * CustomConfig.GP_GAIN_AGGRO_ATTACKER));
                    }
                }
            } catch (NullPointerException e) {
                if (winner == mostDamagePlayer) {
                    AbyssPointsService.addGp(winner, victim, (int) (gpPlayerReward ));
                } else {
                    AbyssPointsService.addGp(winner, victim, (int) ((gpPlayerReward ) * CustomConfig.GP_GAIN_AGGRO_ATTACKER));
                }
            }
        }
		
		winner.getCommonData().addExp(xpPlayerReward, RewardType.PVP_KILL, victim.getName());
		winner.getCommonData().addDp(dpPlayerReward);
		this.addKillFor(winner.getObjectId(), victim.getObjectId());
		//notify Kill-Quests
		int worldId = winner.getWorldId();
		QuestEngine.getInstance().onKillInWorld(new QuestEnv(victim, winner, 0, 0), worldId);
		QuestEngine.getInstance().onKillRanked(new QuestEnv(victim, winner, 0, 0), victim.getAbyssRank().getRank());

		return true;
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {

		protected static final PvpService instance = new PvpService();
	}
}
