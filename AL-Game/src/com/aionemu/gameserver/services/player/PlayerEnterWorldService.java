/*
 * This file is part of aion-lightning <aion-lightning.com>.
 *
 *  aion-lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-lightning.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.services.player;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

import javolution.util.FastList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.cache.HTMLCache;
import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.configs.main.AStationConfig;
import com.aionemu.gameserver.configs.main.AutoGroupConfig;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.main.EventsConfig;
import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.configs.main.HTMLConfig;
import com.aionemu.gameserver.configs.main.PeriodicSaveConfig;
import com.aionemu.gameserver.configs.main.PvPConfig;
import com.aionemu.gameserver.configs.main.SecurityConfig;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.dao.PlayerPasskeyDAO;
import com.aionemu.gameserver.dao.PlayerPunishmentsDAO;
import com.aionemu.gameserver.model.wedding.WeddingService;
import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.account.Account;
import com.aionemu.gameserver.model.account.CharacterBanInfo;
import com.aionemu.gameserver.model.account.CharacterPasskey.ConnectType;
import com.aionemu.gameserver.model.account.PlayerAccountData;
import com.aionemu.gameserver.model.bonus_service.PlayersBonus;
import com.aionemu.gameserver.model.bonus_service.ServiceBuff;
import com.aionemu.gameserver.model.gameobjects.HouseObject;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.gameobjects.player.emotion.Emotion;
import com.aionemu.gameserver.model.gameobjects.player.motion.Motion;
import com.aionemu.gameserver.model.gameobjects.player.title.Title;
import com.aionemu.gameserver.model.gameobjects.state.CreatureSeeState;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.gameobjects.state.CreatureVisualState;
import com.aionemu.gameserver.model.items.storage.IStorage;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.items.storage.StorageType;
import com.aionemu.gameserver.model.skill.PlayerSkillEntry;
import com.aionemu.gameserver.model.team2.alliance.PlayerAllianceService;
import com.aionemu.gameserver.model.team2.group.PlayerGroupService;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ABYSS_RANK;
import com.aionemu.gameserver.network.aion.serverpackets.SM_A_STATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_BOOST_EVENTS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CHANNEL_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CHARACTER_SELECT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CUBE_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ENTER_WORLD_CHECK;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ESSENCE_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FRIEND_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_GAME_TIME;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INSTANCE_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_COOLDOWN;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_REQUEST_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MACRO_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_SPAWN;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_STATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PRICES;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_COMPLETED_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUIT_RESPONSE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_RECIPE_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_COOLDOWN;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TITLE_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UI_SETTINGS;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.AStationService;
import com.aionemu.gameserver.services.AtreianPassportService;
import com.aionemu.gameserver.services.AutoGroupService;
import com.aionemu.gameserver.services.BrokerService;
import com.aionemu.gameserver.services.ClassChangeService;
import com.aionemu.gameserver.services.ColorChat;
import com.aionemu.gameserver.services.DisputeLandService;
import com.aionemu.gameserver.services.EnchantService;
import com.aionemu.gameserver.services.EventService;
import com.aionemu.gameserver.services.F2pService;
import com.aionemu.gameserver.services.HTMLService;
import com.aionemu.gameserver.services.HousingService;
import com.aionemu.gameserver.services.KiskService;
import com.aionemu.gameserver.services.LegionService;
import com.aionemu.gameserver.services.NewbieGuideService;
import com.aionemu.gameserver.services.PetitionService;
import com.aionemu.gameserver.services.ProtectorConquerorService;
import com.aionemu.gameserver.services.PunishmentService;
import com.aionemu.gameserver.services.PunishmentService.PunishmentType;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.services.SkillLearnService;
import com.aionemu.gameserver.services.StigmaService;
import com.aionemu.gameserver.services.SurveyService;
import com.aionemu.gameserver.services.VortexService;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.services.abyss.AbyssSkillService;
import com.aionemu.gameserver.services.craft.RelinquishCraftStatus;
import com.aionemu.gameserver.services.dreamergames.services.CombatPointService;
import com.aionemu.gameserver.services.dreamergames.services.CpPlayerService;
import com.aionemu.gameserver.services.events.RollDiceEventService;
import com.aionemu.gameserver.services.gmservice.GmSpecialSkills;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.mail.MailService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.territory.TerritoryService;
import com.aionemu.gameserver.services.toypet.PetService;
import com.aionemu.gameserver.services.transfers.PlayerTransferService;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.taskmanager.tasks.ExpireTimerTask;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.audit.AuditLogger;
import com.aionemu.gameserver.utils.audit.GMService;
import com.aionemu.gameserver.utils.collections.ListSplitter;
import com.aionemu.gameserver.utils.rates.Rates;
import com.aionemu.gameserver.utils.stats.AbyssRankEnum;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;

public final class PlayerEnterWorldService {
	private static final Logger log = LoggerFactory.getLogger("GAMECONNECTION_LOG");
	private static final String ServerName = ColorChat.colorChat("Welcome to Unity Aion", "1 0.5 0");
	private static final String ServerTut1 = "use .help for see another player commands.";
	private static final String ServerLine = "=======================";
	private static final String ServerNotice = "Happy Aion & Fair Play";
	private static final String Copyright = "2018 @ Unity Aion.";
	private static final String DevTeam = "* Unity Team *";
	private static final Set<Integer> pendingEnterWorld = new HashSet<Integer>();
	private static ServiceBuff serviceBuff;
	private static PlayersBonus playersBonus;
	static ScheduledFuture<?> adv = null;

	public static final void startEnterWorld(final int objectId, final AionConnection client) {
		PlayerAccountData playerAccData = client.getAccount().getPlayerAccountData(objectId);
		Timestamp lastOnline = playerAccData.getPlayerCommonData().getLastOnline();
		if (lastOnline != null && client.getAccount().getAccessLevel() < AdminConfig.GM_LEVEL) {
			if (System.currentTimeMillis() - lastOnline.getTime() < (GSConfig.CHARACTER_REENTRY_TIME * 1000)) {
				client.sendPacket(new SM_ENTER_WORLD_CHECK((byte) 6)); // 20 sec time
				return;
			}
		}
		CharacterBanInfo cbi = client.getAccount().getPlayerAccountData(objectId).getCharBanInfo();
		if (cbi != null) {
			if (cbi.getEnd() > System.currentTimeMillis() / 1000) {
				client.close(new SM_QUIT_RESPONSE(), false);
				return;
			} else {
				DAOManager.getDAO(PlayerPunishmentsDAO.class).unpunishPlayer(objectId, PunishmentType.CHARBAN);
			}
		} if (SecurityConfig.PASSKEY_ENABLE && !client.getAccount().getCharacterPasskey().isPass()) {
			showPasskey(objectId, client);
		} else {
			validateAndEnterWorld(objectId, client);
		}
	}

	private static final void showPasskey(final int objectId, final AionConnection client) {
		client.getAccount().getCharacterPasskey().setConnectType(ConnectType.ENTER);
		client.getAccount().getCharacterPasskey().setObjectId(objectId);
		boolean isExistPasskey = DAOManager.getDAO(PlayerPasskeyDAO.class).existCheckPlayerPasskey(client.getAccount().getId());
		if (!isExistPasskey) {
			client.sendPacket(new SM_CHARACTER_SELECT(0));
		} else {
			client.sendPacket(new SM_CHARACTER_SELECT(1));
		}
	}

	private static final void validateAndEnterWorld(final int objectId, final AionConnection client) {
		synchronized (pendingEnterWorld) {
			if (pendingEnterWorld.contains(objectId)) {
				log.warn("Skipping enter world " + objectId);
				return;
			}
			pendingEnterWorld.add(objectId);
		}
		int delay = 0;
		if (World.getInstance().findPlayer(objectId) != null) {
			delay = 15000;
			log.warn("Postponed enter world " + objectId);
		}
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				try {
					Player player = World.getInstance().findPlayer(objectId);
					if (player != null) {
						AuditLogger.info(player, "Duplicate player in world");
						client.close(new SM_QUIT_RESPONSE(), false);
						return;
					}
					enterWorld(client, objectId);
				}
				catch (Throwable ex) {
					log.error("Error during enter world " + objectId, ex);
				}
				finally {
					synchronized (pendingEnterWorld) {
						pendingEnterWorld.remove(objectId);
					}
				}
			}
		}, delay);
	}

	public static final void enterWorld(AionConnection client, int objectId) {
		Account account = client.getAccount();
		PlayerAccountData playerAccData = client.getAccount().getPlayerAccountData(objectId);
		if (playerAccData == null) {
			return;
		}
		final Player player = PlayerService.getPlayer(objectId, account);
		if (player != null && client.setActivePlayer(player)) {
			player.setClientConnection(client);
			log.info("[MAC_AUDIT] Player " + player.getName() + " (account " + account.getName() + ") has entered world with " + client.getMacAddress() + " MAC.");
			World.getInstance().storeObject(player);
			StigmaService.onPlayerLogin(player);
			CpPlayerService.onPlayerLogin(player);
			if (playerAccData.getPlayerCommonData().getLastOnline() != null) {
				long lastOnline = playerAccData.getPlayerCommonData().getLastOnline().getTime();
				PlayerCommonData pcd = player.getCommonData();
				long secondsOffline = (System.currentTimeMillis() / 1000) - lastOnline / 1000;
				pcd.setMaxCp(player.getLevel());
				if (pcd.isReadyForSalvationPoints()) {
					//The level of Energy of Salvation will now be maintained up to 1 hour after disconnect.
					if (secondsOffline > 60 * 60) {
						player.getCommonData().resetSalvationPoints();
					}
				} if (pcd.isReadyForGoldenStarEnergy()) {
					//The level of Berdin's Star will now be maintained up to 4 hour after disconnect.
					if (secondsOffline > 240 * 60) {
						pcd.checkGoldenStarPercent();
						player.getCommonData().setGoldenStarEnergy(0);
					}
				} if (pcd.isReadyForGrowthEnergy()) {
					pcd.updateMaxGrowthEnergy();
				} if (pcd.isReadyForReposteEnergy()){
					pcd.updateMaxReposte();
					if (secondsOffline > 14400){
						double hours = secondsOffline / 3600.0;
						long maxRespose = player.getCommonData().getMaxReposteEnergy();
						if (hours > 24.0) {
							hours = 24.0;
						}
						long addResposeEnergy = (long) (hours / 24.0 * maxRespose);
						if (player.getHouseOwnerId() / 10000 * 10000 == player.getWorldId()) {
							switch (player.getActiveHouse().getHouseType()) {
								case STUDIO:
									addResposeEnergy = (long) ((float)addResposeEnergy * 1.05);
								break;
								case MANSION:
									addResposeEnergy = (long) ((float)addResposeEnergy * 1.08);
								break;
								case ESTATE:
									addResposeEnergy = (long) ((float)addResposeEnergy * 1.15);
								break;
								case PALACE:
									addResposeEnergy = (long) ((float)addResposeEnergy * 1.50);
								break;
								default:
									addResposeEnergy = (long) ((float)addResposeEnergy * 1.1);
							}
						}
						pcd.addReposteEnergy(addResposeEnergy > maxRespose ? maxRespose : addResposeEnergy);
					}
				} if (System.currentTimeMillis() / 1000 - lastOnline > 300) {
					player.getCommonData().setDp(0);
				} if (System.currentTimeMillis() / 1000 - lastOnline > 3600) {
					player.getCommonData().setGrowthEnergy(0);
				}
			}
			InstanceService.onPlayerLogin(player);
			client.sendPacket(new SM_A_STATION(1, 1, true));
			
			// Update player skills first + HotFix ?!!!
			if (!player.getSkillList().isSkillPresent(302)) {
				player.getSkillList().addSkill(player, 302, 129);
			}
			
			AbyssSkillService.onEnterWorld(player);
			client.sendPacket(new SM_SKILL_LIST(player, player.getSkillList().getBasicSkills()));
			for (PlayerSkillEntry stigmaSkill : player.getSkillList().getStigmaSkills()) {
				client.sendPacket(new SM_SKILL_LIST(player, stigmaSkill));
			} 
			
			if (player.getSkillCoolDowns() != null) {
				client.sendPacket(new SM_SKILL_COOLDOWN(player.getSkillCoolDowns()));
			} if (player.getItemCoolDowns() != null) {
				client.sendPacket(new SM_ITEM_COOLDOWN(player.getItemCoolDowns()));
			}
			//UpgradeArcadeService.getInstance().onEnterWorld(player);
			FastList<QuestState> questList = FastList.newInstance();
			FastList<QuestState> completeQuestList = FastList.newInstance();
			for (QuestState qs : player.getQuestStateList().getAllQuestState()) {
				if (qs.getStatus() == QuestStatus.NONE && qs.getCompleteCount() == 0) {
					continue;
				} if (qs.getStatus() != QuestStatus.COMPLETE && qs.getStatus() != QuestStatus.NONE) {
					questList.add(qs);
				} if (qs.getCompleteCount() > 0) {
					completeQuestList.add(qs);
				}
			}
			client.sendPacket(new SM_QUEST_COMPLETED_LIST(completeQuestList));
			client.sendPacket(new SM_QUEST_LIST(questList));
			client.sendPacket(new SM_TITLE_INFO(player.getCommonData().getTitleId()));
			client.sendPacket(new SM_TITLE_INFO(6, player.getCommonData().getBonusTitleId()));
			client.sendPacket(new SM_MOTION(player.getMotions().getMotions().values()));
			client.sendPacket(new SM_ENTER_WORLD_CHECK());
			byte[] uiSettings = player.getPlayerSettings().getUiSettings();
			byte[] shortcuts = player.getPlayerSettings().getShortcuts();
			byte[] houseBuddies = player.getPlayerSettings().getHouseBuddies();
			if (uiSettings != null) {
				client.sendPacket(new SM_UI_SETTINGS(uiSettings, 0));
			} if (shortcuts != null) {
				client.sendPacket(new SM_UI_SETTINGS(shortcuts, 1));
			} if (houseBuddies != null) {
				client.sendPacket(new SM_UI_SETTINGS(houseBuddies, 2));
			}
			
			client.sendPacket(new SM_ESSENCE_INFO(player, player.getCpList().getAllCps()));
			CombatPointService.getInstance().onPlayerLogin(player);
			sendItemInfos(client, player);
			if (AStationConfig.A_STATION_ENABLE) {
				AStationService.getInstance().checkAuthorizationRequest(player);
			}
			playerLoggedIn(player);
			client.sendPacket(new SM_INSTANCE_INFO(player, false, player.getCurrentTeam()));
			client.sendPacket(new SM_CHANNEL_INFO(player.getPosition()));
			KiskService.getInstance().onLogin(player);
			TerritoryService.getInstance().onEnterWorld(player);
			/**
			* If a user logs out in any hostile territory, they will be transported back to the last 
			* registered Obelisk.
			*/
			if (CustomConfig.ENABLE_RECONNECT_TO_BIND_POINT) {
				TeleportService2.moveToBindLocation(player, true);
			}
		   /**
			* http://static.ncsoft.com/aion/store/PatchNotes/AION_Patch_Notes_071316.pdf
			* If a user logs out in hostile territory in Iluma/Norsvold, they will be transported back to the last 
			* registered Obelisk.
			*/
			TeleportService2.onLogOutOppositeMap(player);
			//TeleportService2.sendSetBindPoint(player);
			World.getInstance().preSpawn(player);
			player.getController().validateLoginZone();
			SiegeService.getInstance().validateLoginZone(player);
			VortexService.getInstance().validateLoginZone(player);
			//client.sendPacket(new SM_PLAYER_SPAWN(player));
			if (player.getCommonData().isInNewbieGuide()) {
                NewbieGuideService.getInstance().onPlayerEnterWorld(player);
            } else {
                // SM_PLAYER_SPAWN
                client.sendPacket(new SM_PLAYER_SPAWN(player));
            }
			client.sendPacket(new SM_GAME_TIME());
			ProtectorConquerorService.getInstance().onProtectorConquerorLogin(player);
			//Legion Request 4.9.1
			LegionService.getInstance().sendLegionJoinRequest(player);
			if (player.isLegionMember()) {
				LegionService.getInstance().onLogin(player);
                if (player.getLegionMember().isBrigadeGeneral() && !player.getLegion().getJoinRequestMap().isEmpty()) {
                	client.sendPacket(new SM_LEGION_REQUEST_LIST(player.getLegion().getJoinRequestMap().values()));
				}
            } else {
            	DAOManager.getDAO(PlayerDAO.class).getJoinRequestState(player);
            	LegionService.getInstance().handleJoinRequestGetAnswer(player);
            }
			client.sendPacket(new SM_TITLE_INFO(player));
			client.sendPacket(new SM_EMOTION_LIST((byte) 0, player.getEmotions().getEmotions()));
			SiegeService.getInstance().onPlayerLogin(player);
			AtreianPassportService.getInstance().onLogin(player);
			AbyssPointsService.AbyssRankCheck(player);
			// TODO: Send Rift Announce Here
			client.sendPacket(new SM_PRICES());
			DisputeLandService.getInstance().onLogin(player);
			client.sendPacket(new SM_ABYSS_RANK(player.getAbyssRank()));
			//Player Welcome Message
			PacketSendUtility.sendBrightYellowMessageOnCenter(player, ColorChat.colorChat("Welcome " + player.getName() + ". Use .skills for Learn New Skills. ", "1 0 5 0"));
			//Color Chat.
			PacketSendUtility.sendMessage(player, ServerName);
			PacketSendUtility.sendMessage(player, ServerTut1);
			PacketSendUtility.sendBrightYellowMessage(player, ServerLine);
			PacketSendUtility.sendBrightYellowMessage(player, ServerNotice);
			PacketSendUtility.sendBrightYellowMessage(player, ServerLine);
			PacketSendUtility.sendBrightYellowMessage(player, Copyright);
			PacketSendUtility.sendBrightYellowMessage(player, DevTeam);
			PacketSendUtility.sendBrightYellowMessage(player, ServerLine);
			if (player.isMarried()) {
				serviceBuff = new ServiceBuff(1000013);
				serviceBuff.applyEffect(player, 1000013);
				PacketSendUtility.sendYellowMessage(player, "You are Married get extra benefit !");
			}
			//"\uE026" //Timer.
			//"\uE027" //Speaker.
			player.setRates(Rates.getRatesFor(client.getAccount().getMembership()));
			if (CustomConfig.PREMIUM_NOTIFY) {
				showPremiumAccountInfo(client, account);
			}
			
			if (player.getAccessLevel() == 0) {
                for (int al : GmSpecialSkills.getAlType(GmSpecialSkills.AccessLevel10.getLevel()).getSkills()) {
                    if (player.getSkillList().isSkillPresent(al))
                        SkillLearnService.removeSkill(player, al);
                }
            }
			
			if (player.isGM()) {
				if (AdminConfig.INVULNERABLE_GM_CONNECTION || AdminConfig.INVISIBLE_GM_CONNECTION || AdminConfig.ENEMITY_MODE_GM_CONNECTION.equalsIgnoreCase("Neutral")
					|| AdminConfig.ENEMITY_MODE_GM_CONNECTION.equalsIgnoreCase("Enemy") || AdminConfig.VISION_GM_CONNECTION || AdminConfig.WHISPER_GM_CONNECTION) {
					PacketSendUtility.sendMessage(player, "=============================");
					if (AdminConfig.INVULNERABLE_GM_CONNECTION) {
						player.setInvul(true);
						PacketSendUtility.sendMessage(player, ">> Connection in Invulnerable mode <<");
					} if (AdminConfig.INVISIBLE_GM_CONNECTION) {
						player.getEffectController().setAbnormal(AbnormalState.HIDE.getId());
						player.setVisualState(CreatureVisualState.HIDE3);
						PacketSendUtility.broadcastPacket(player, new SM_PLAYER_STATE(player), true);
						PacketSendUtility.sendMessage(player, ">> Connection in Invisible mode <<");
					} if (AdminConfig.ENEMITY_MODE_GM_CONNECTION.equalsIgnoreCase("Neutral")) {
						player.setAdminNeutral(3);
						player.setAdminEnmity(0);
						PacketSendUtility.sendMessage(player, ">> Connection in Neutral mode <<");
					} if (AdminConfig.ENEMITY_MODE_GM_CONNECTION.equalsIgnoreCase("Enemy")) {
						player.setAdminNeutral(0);
						player.setAdminEnmity(3);
						PacketSendUtility.sendMessage(player, ">> Connection in Enemy mode <<");
					} if (AdminConfig.VISION_GM_CONNECTION) {
						player.setSeeState(CreatureSeeState.SEARCH10);
						PacketSendUtility.broadcastPacket(player, new SM_PLAYER_STATE(player), true);
						PacketSendUtility.sendMessage(player, ">> Connection in Vision mode <<");
					} if (AdminConfig.WHISPER_GM_CONNECTION) {
						player.setUnWispable();
						PacketSendUtility.sendMessage(player, ">> Accepting Whisper : OFF <<");
					} if (AdminConfig.GM_MODE_CONNECTION) {
						player.setGmMode(true);
                        PacketSendUtility.sendMessage(player, ">> GM Mode : Enable <<");
                    }
					PacketSendUtility.sendMessage(player, "=============================");
				}
				
				if (player.getAccessLevel() >= AdminConfig.GM_SPECIAL_SKILLS) {
					for (int al : GmSpecialSkills.getAlType(player.getAccessLevel()).getSkills()) {
                        player.getSkillList().addGMSkill(player, al, 1);
    				}
				}
			}
			
			Calendar calendar = Calendar.getInstance();
			if (PvPConfig.ADVANCED_PVP_SYSTEM) {
				if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
					PacketSendUtility.sendMessage(player, "Pvp hari ini di map levinshor, dan silentera canyon");
				}
				else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
					PacketSendUtility.sendMessage(player, "Pvp hari ini di map levinshor, dan silentera canyon");
				}
				else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
					PacketSendUtility.sendMessage(player, "Pvp hari ini di map levinshor, dan silentera canyon");
				}
				else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
				    PacketSendUtility.sendMessage(player, "Pvp hari ini di map levinshor, dan silentera canyon");
				}
				else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				    PacketSendUtility.sendMessage(player, "Pvp hari ini di map levinshor, dan silentera canyon");
				}
			}
			//Service Security Buff.
			if (player.getMembership() >= 0) {
				serviceBuff = new ServiceBuff(2);
				serviceBuff.applyEffect(player, 2);
			}
			//Eminence Of The Beaver.
			if (player.getLevel() >= 66 && player.getLevel() <= 83) {
				playersBonus = new PlayersBonus(10);
				playersBonus.applyEffect(player, 10);
				player.setPlayersBonusId(10);
			}
			//PC Cafe Login Benefits.
			if (player.getClientConnection().getAccount().getMembership() == 2 && player.getLevel() >= 66 && player.getLevel() <= 83) {
				serviceBuff = new ServiceBuff(4);
				serviceBuff.applyEffect(player, 4);
				PacketSendUtility.sendBrightYellowMessageOnCenter(player, "Your account vip ! You get extra service!");
			}
			//Ascension Boost.
			if (player.getLevel() >= 1 && player.getLevel() <= 34) {
				playersBonus = new PlayersBonus(2);
				playersBonus.applyEffect(player, 2);
				player.setPlayersBonusId(2);
			}
			//Veteran Boost.
			else if (player.getLevel() >= 35 && player.getLevel() <= 65) {
				playersBonus = new PlayersBonus(3);
				playersBonus.applyEffect(player, 3);
				player.setPlayersBonusId(3);
			} 
			//Member Boost.
			else if (player.getMembership() >= 0 && player.getLevel() >= 55) {
				playersBonus = new PlayersBonus(2000001);
				playersBonus.applyEffect(player, 2000001);
			} else {
				playersBonus = new PlayersBonus(1);
				playersBonus.endEffect(player, 1);
			}
			//Abyss Logon 4.9
			if (player.getRace() == Race.ELYOS) {
				abyssLightLogon(player);
			} else if (player.getRace() == Race.ASMODIANS) {
				abyssDarkLogon(player);
			}
			client.sendPacket(new SM_BOOST_EVENTS());
			//LoginServerInfo(player);
			GloryPointLoseMsg(player);
			F2pService.getInstance().onEnterWorld(player);
			//Aura Of Growth.
			//Players can gain additional XP from hunting, gathering or crafting by obtaining Growth Aura.
			//Growth Aura can be obtained from hunting monsters, acquiring essence, and through login and quest rewards.
			//For more information on Growth Aura, check the Character XP Status Bar Tool Tip.
			PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_CHARGE_EXP_POINT, 60000);
			//"Auto PowerShard ON"
			if (player.getEquipment().isPowerShardEquipped() && CustomConfig.ENABLE_AUTO_POWERSHARD) {
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_WEAPON_BOOST_BOOST_MODE_STARTED, 7000);
				player.setState(CreatureState.POWERSHARD);
				PacketSendUtility.playerSendPacketTime(player, new SM_EMOTION(player, EmotionType.POWERSHARD_ON, 0, 0), 7000);
			}
			//Alliance Packet after SetBindPoint.
			PlayerAllianceService.onPlayerLogin(player);
			if (player.isInPrison()) {
				PunishmentService.updatePrisonStatus(player);
			} if (player.isNotGatherable()) {
				PunishmentService.updateGatherableStatus(player);
			}
			
			PlayerGroupService.onPlayerLogin(player);
			PetService.getInstance().onPlayerLogin(player);
			MailService.getInstance().onPlayerLogin(player);
			HousingService.getInstance().onPlayerLogin(player);
			BrokerService.getInstance().onPlayerLogin(player);
			sendMacroList(client, player);
			client.sendPacket(new SM_FRIEND_STATUS((byte)1));
			client.sendPacket(new SM_RECIPE_LIST(player.getRecipeList().getRecipeList()));
			PetitionService.getInstance().onPlayerLogin(player);
			if (AutoGroupConfig.AUTO_GROUP_ENABLED) {
				AutoGroupService.getInstance().onPlayerLogin(player);
			}
			// display Class Change Dialog
            if (!player.getCommonData().isInNewbieGuide()) {
                ClassChangeService.showClassChangeDialog(player);
            }
			GMService.getInstance().onPlayerLogin(player);
			player.getLifeStats().updateCurrentStats();
			if (!CustomConfig.RANK_ITEM_LIMIT) {
				player.getEquipment().checkRankLimitItems();
			}
			if (HTMLConfig.ENABLE_HTML_WELCOME) {
				HTMLService.showHTML(player, HTMLCache.getInstance().getHTML("welcome.xhtml"));
			}
			player.getNpcFactions().sendDailyQuest();
			if (HTMLConfig.ENABLE_GUIDES) {
				HTMLService.onPlayerLogin(player);
			} for (StorageType st : StorageType.values()) {
				if (st == StorageType.LEGION_WAREHOUSE) {
					continue;
				}
				IStorage storage = player.getStorage(st.getId());
				if (storage != null) {
					for (Item item : storage.getItemsWithKinah()) {
						if (item.getExpireTime() > 0) {
							ExpireTimerTask.getInstance().addTask(item, player);
						}
					}
				}
			} for (Item item : player.getEquipment().getEquippedItems()) {
				if (item.getExpireTime() > 0) {
					ExpireTimerTask.getInstance().addTask(item, player);
				}
			} for (Motion motion : player.getMotions().getMotions().values()) {
				if (motion.getExpireTime() != 0) {
					ExpireTimerTask.getInstance().addTask(motion, player);
				}
			} for (Emotion emotion : player.getEmotions().getEmotions()) {
				if (emotion.getExpireTime() != 0) {
					ExpireTimerTask.getInstance().addTask(emotion, player);
				}
			} for (Title title : player.getTitleList().getTitles()) {
				if (title.getExpireTime() != 0) {
					ExpireTimerTask.getInstance().addTask(title, player);
				}
			} if (player.getHouseRegistry() != null) {
				for (HouseObject<?> obj : player.getHouseRegistry().getObjects()) {
					if (obj.getPersistentState() != PersistentState.DELETED) {
						if (obj.getObjectTemplate().getUseDays() > 0) {
							ExpireTimerTask.getInstance().addTask(obj, player);
						}
					}
				}
			}
			player.getController().addTask(TaskId.PLAYER_UPDATE, ThreadPoolManager.getInstance().scheduleAtFixedRate(new GeneralUpdateTask(player.getObjectId()), PeriodicSaveConfig.PLAYER_GENERAL * 1000, PeriodicSaveConfig.PLAYER_GENERAL * 1000));
			player.getController().addTask(TaskId.INVENTORY_UPDATE, ThreadPoolManager.getInstance().scheduleAtFixedRate(new ItemUpdateTask(player.getObjectId()), PeriodicSaveConfig.PLAYER_ITEMS * 1000, PeriodicSaveConfig.PLAYER_ITEMS * 1000));
			SurveyService.getInstance().showAvailable(player);
			if (EventsConfig.ENABLE_EVENT_SERVICE) {
				EventService.getInstance().onPlayerLogin(player);
			}
			RelinquishCraftStatus.removeExcessCraftStatus(player, false);
			PlayerTransferService.getInstance().onEnterWorld(player);
			WeddingService.getInstance().onEnterWorld(player);
			//player.setPartnerId(DAOManager.getDAO(WeddingDAO.class).loadPartnerId(player));
			EnchantService.GloryShieldSkill(player);
			RollDiceEventService.getInstance().onEnterWorld(player);
			LunaShopService.getInstance().onLogin(player);
			SkillLearnService.addMissingSkills(player);
			if (player.getActiveHouse() != null) {
				if (player.getSkillList().getSkillEntry(296) == null && player.getRace() == Race.ASMODIANS) {
					player.getSkillList().addSkill(player, 296, 1);
				} if (player.getSkillList().getSkillEntry(295) == null && player.getRace() == Race.ELYOS) {
					player.getSkillList().addSkill(player, 295, 1);
				}
			}
		} else {
			log.info("[DEBUG] enter world" + objectId + ", Player: " + player);
		}
	}
	
	/**
	 * [Glory Point Lose Msg]
	 */
	public static final void GloryPointLoseMsg(Player player) {
		if (player.getAbyssRank().getRank().getId() == AbyssRankEnum.STAR1_OFFICER.getId()) {
			//A set amount of Glory Points are deducted every day based on your Abyss Rank.
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402082));
			//The Glory Points to be deducted for %0 are %1[%gchar:glory_point].
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402209, 14));
		} else if (player.getAbyssRank().getRank().getId() == AbyssRankEnum.STAR2_OFFICER.getId()) {
			//A set amount of Glory Points are deducted every day based on your Abyss Rank.
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402082));
			//The Glory Points to be deducted for %0 are %1[%gchar:glory_point].
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402209, 27));
		} else if (player.getAbyssRank().getRank().getId() == AbyssRankEnum.STAR3_OFFICER.getId()) {
			//A set amount of Glory Points are deducted every day based on your Abyss Rank.
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402082));
			//The Glory Points to be deducted for %0 are %1[%gchar:glory_point].
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402209, 55));
		} else if (player.getAbyssRank().getRank().getId() == AbyssRankEnum.STAR4_OFFICER.getId()) {
			//A set amount of Glory Points are deducted every day based on your Abyss Rank.
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402082));
			//The Glory Points to be deducted for %0 are %1[%gchar:glory_point].
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402209, 98));
		} else if (player.getAbyssRank().getRank().getId() == AbyssRankEnum.STAR5_OFFICER.getId()) {
			//A set amount of Glory Points are deducted every day based on your Abyss Rank.
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402082));
			//The Glory Points to be deducted for %0 are %1[%gchar:glory_point].
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402209, 213));
		} else if (player.getAbyssRank().getRank().getId() == AbyssRankEnum.GENERAL.getId()) {
			//A set amount of Glory Points are deducted every day based on your Abyss Rank.
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402082));
			//The Glory Points to be deducted for %0 are %1[%gchar:glory_point].
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402209, 237));
		} else if (player.getAbyssRank().getRank().getId() == AbyssRankEnum.GREAT_GENERAL.getId()) {
			//A set amount of Glory Points are deducted every day based on your Abyss Rank.
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402082));
			//The Glory Points to be deducted for %0 are %1[%gchar:glory_point].
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402209, 244));
		} else if (player.getAbyssRank().getRank().getId() == AbyssRankEnum.COMMANDER.getId()) {
			//A set amount of Glory Points are deducted every day based on your Abyss Rank.
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402082));
			//The Glory Points to be deducted for %0 are %1[%gchar:glory_point].
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402209, 254));
		} else if (player.getAbyssRank().getRank().getId() == AbyssRankEnum.SUPREME_COMMANDER.getId()) {
			//A set amount of Glory Points are deducted every day based on your Abyss Rank.
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402082));
			//The Glory Points to be deducted for %0 are %1[%gchar:glory_point].
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402209, 294));
		}
	}
	
	/**
	 * [Abyss Logon] 4.9
	 */
	public static final void abyssLightLogon(final Player player) {
		if (player.getAbyssRank().getRank().getId() == AbyssRankEnum.SUPREME_COMMANDER.getId()) {
			World.getInstance().doOnAllPlayers(new Visitor<Player>() {
				@Override
				public void visit(Player players) {
					//Elyos Governor "Player Name" has graced Atreia.
					PacketSendUtility.sendPacket(players, new SM_SYSTEM_MESSAGE(1403134, player.getName()));
				}
			});
		}
	}
	public static final void abyssDarkLogon(final Player player) {
		if (player.getAbyssRank().getRank().getId() == AbyssRankEnum.SUPREME_COMMANDER.getId()) {
			World.getInstance().doOnAllPlayers(new Visitor<Player>() {
				@Override
				public void visit(Player players) {
					//Asmodian Governor "Player Name" has graced Atreia.
					PacketSendUtility.sendPacket(players, new SM_SYSTEM_MESSAGE(1403135, player.getName()));
				}
			});
		}
	}
	
	private static void sendItemInfos(AionConnection client, Player player) {
		int questExpands = player.getQuestExpands();
		int npcExpands = player.getNpcExpands();
		player.getInventory().setLimit(StorageType.CUBE.getLimit() + (questExpands + npcExpands) * 9);
		player.getWarehouse().setLimit(StorageType.REGULAR_WAREHOUSE.getLimit() + player.getWarehouseSize() * 8);
		Storage inventory = player.getInventory();
		List<Item> allItems = new ArrayList<Item>();
		if (inventory.getKinah() == 0) {
			inventory.increaseKinah(0);
		}
		allItems.add(inventory.getKinahItem());
		allItems.addAll(player.getEquipment().getEquippedItems());
		allItems.addAll(inventory.getItems());
		client.sendPacket(new SM_INVENTORY_INFO(true, new ArrayList<Item>(0), npcExpands, questExpands, player));
		ListSplitter<Item> splitter = new ListSplitter<Item>(allItems, 10);
		while (!splitter.isLast()) {
			client.sendPacket(new SM_INVENTORY_INFO(false, splitter.getNext(), npcExpands, questExpands, player));
		}
		client.sendPacket(new SM_INVENTORY_INFO(false, new ArrayList<Item>(0), npcExpands, questExpands, player));
		client.sendPacket(new SM_STATS_INFO(player));
		client.sendPacket(SM_CUBE_UPDATE.stigmaSlots(player.getCommonData().getAdvencedStigmaSlotSize()));
	}
	
	private static void sendMacroList(AionConnection client, Player player) {
		client.sendPacket(new SM_MACRO_LIST(player, false));
		if (player.getMacroList().getSize() > 7) {
			client.sendPacket(new SM_MACRO_LIST(player, true));
		}
	}
	
	private static void playerLoggedIn(Player player) {
		log.info("Player logged in: " + player.getName() + " Account: " + player.getClientConnection().getAccount().getName());
		player.getCommonData().setOnline(true);
		DAOManager.getDAO(PlayerDAO.class).onlinePlayer(player, true);
		player.onLoggedIn();
		player.setOnlineTime();
	}
	
	private static void showPremiumAccountInfo(AionConnection client, Account account) {
		byte membership = account.getMembership();
		if (membership > 0) {
			String accountType = "";
			switch (account.getMembership()) {
			    case 1:
				    accountType = "premium";
			    break;
			    case 2:
				    accountType = "VIP";
			    break;
			}
			client.sendPacket(new SM_MESSAGE(0, null, "Your account is " + accountType, ChatType.GOLDEN_YELLOW));
		}
	}
}