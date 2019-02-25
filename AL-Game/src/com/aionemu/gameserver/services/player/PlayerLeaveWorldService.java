package com.aionemu.gameserver.services.player;

import java.sql.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.main.AutoGroupConfig;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.dao.HouseObjectCooldownsDAO;
import com.aionemu.gameserver.dao.ItemCooldownsDAO;
import com.aionemu.gameserver.dao.PlayerBindPointDAO;
import com.aionemu.gameserver.dao.PlayerCooldownsDAO;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.dao.PlayerEffectsDAO;
import com.aionemu.gameserver.dao.PlayerLifeStatsDAO;
import com.aionemu.gameserver.model.account.PlayerAccountData;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.StorageType;
import com.aionemu.gameserver.model.summons.SummonMode;
import com.aionemu.gameserver.model.summons.UnsummonType;
import com.aionemu.gameserver.model.team2.alliance.PlayerAllianceService;
import com.aionemu.gameserver.model.team2.group.PlayerGroupService;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.AutoGroupService;
import com.aionemu.gameserver.services.BrokerService;
import com.aionemu.gameserver.services.ChatService;
import com.aionemu.gameserver.services.DuelService;
import com.aionemu.gameserver.services.ExchangeService;
import com.aionemu.gameserver.services.FindGroupService;
import com.aionemu.gameserver.services.KiskService;
import com.aionemu.gameserver.services.LegionService;
import com.aionemu.gameserver.services.ProtectorConquerorService;
import com.aionemu.gameserver.services.PunishmentService;
import com.aionemu.gameserver.services.RepurchaseService;
import com.aionemu.gameserver.services.dreamergames.services.CpPlayerService;
import com.aionemu.gameserver.services.drop.DropService;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.summons.SummonsService;
import com.aionemu.gameserver.services.toypet.PetSpawnService;
import com.aionemu.gameserver.taskmanager.tasks.ExpireTimerTask;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.audit.GMService;

public class PlayerLeaveWorldService {
	private static final Logger log = LoggerFactory.getLogger(PlayerLeaveWorldService.class);

	public static final void startLeaveWorldDelay(final Player player, int delay) {
		player.getController().stopMoving();
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				startLeaveWorld(player);
			}
		}, delay);
	}

	public static final void startLeaveWorld(Player player) {
		if (CustomConfig.ENABLE_STORE_BINDPOINT) {
			// need to store last position, not to bind it on the logout point
            //player.setBindPoint(new BindPointPosition(player.getWorldId(), player.getX(), player.getY(), player.getZ(), player.getHeading()));
            if(player.getBindPoint() != null)//cannot accept null value to db
                DAOManager.getDAO(PlayerBindPointDAO.class).store(player);
        
        // just for logging
        log.info("Store binding point before logged out " + player.getName() + " x " + player.getX() + " y " + player.getY() + " z " + player.getZ() + " heading " + player.getHeading());
		}
		log.info("Player Logged Out: " + player.getName() + " Account: " + (player.getClientConnection() != null ? player.getClientConnection().getAccount().getName() : "Disconnected"));
		FindGroupService.getInstance().removeFindGroup(player.getRace(), 0x00, player.getObjectId());
		FindGroupService.getInstance().removeFindGroup(player.getRace(), 0x04, player.getObjectId());
		player.onLoggedOut();
		BrokerService.getInstance().removePlayerCache(player);
		ExchangeService.getInstance().cancelExchange(player);
		RepurchaseService.getInstance().removeRepurchaseItems(player);
		CpPlayerService.onPlayerLogout(player);
		if (AutoGroupConfig.AUTO_GROUP_ENABLED) {
			AutoGroupService.getInstance().onPlayerLogOut(player);
		}
		ProtectorConquerorService.getInstance().onLogout(player);
		InstanceService.onLogOut(player);
		GMService.getInstance().onPlayerLogedOut(player);
		KiskService.getInstance().onLogout(player);
		player.getMoveController().abortMove();
		if (player.isLooting()) {
			DropService.getInstance().closeDropList(player, player.getLootingNpcOid());
		}
		if (player.isInPrison()) {
			long prisonTimer = System.currentTimeMillis() - player.getStartPrison();
			prisonTimer = player.getPrisonTimer() - prisonTimer;
			player.setPrisonTimer(prisonTimer);
			log.debug("Update prison timer to " + prisonTimer / 1000 + " seconds !");
		}
		DAOManager.getDAO(PlayerEffectsDAO.class).storePlayerEffects(player);
		DAOManager.getDAO(PlayerCooldownsDAO.class).storePlayerCooldowns(player);
		DAOManager.getDAO(ItemCooldownsDAO.class).storeItemCooldowns(player);
		DAOManager.getDAO(HouseObjectCooldownsDAO.class).storeHouseObjectCooldowns(player);
		DAOManager.getDAO(PlayerLifeStatsDAO.class).updatePlayerLifeStat(player);
		PlayerGroupService.onPlayerLogout(player);
		PlayerAllianceService.onPlayerLogout(player);
		LegionService.getInstance().LegionWhUpdate(player);
		player.getEffectController().removeAllEffects(true);
		player.getLifeStats().cancelAllTasks();
		if (player.getLifeStats().isAlreadyDead()) {
			if (player.isInInstance()) {
				PlayerReviveService.instanceRevive(player);
			} else {
				PlayerReviveService.bindRevive(player);
			}
		} else if (DuelService.getInstance().isDueling(player.getObjectId())) {
			DuelService.getInstance().loseDuel(player);
		}
		Summon summon = player.getSummon();
		if (summon != null) {
			SummonsService.doMode(SummonMode.RELEASE, summon, UnsummonType.LOGOUT);
		}
		PetSpawnService.dismissPet(player, true);
		if (player.getPostman() != null) {
			player.getPostman().getController().onDelete();
		}
		player.setPostman(null);
		PunishmentService.stopPrisonTask(player, true);
		PunishmentService.stopGatherableTask(player, true);
		if (player.isLegionMember()) {
			LegionService.getInstance().onLogout(player);
		}
		QuestEngine.getInstance().onLogOut(new QuestEnv(null, player, 0, 0));
		player.getController().delete();
		player.getCommonData().setOnline(false);
		player.getCommonData().setLastOnline(new Timestamp(System.currentTimeMillis()));
		player.setClientConnection(null);
		DAOManager.getDAO(PlayerDAO.class).onlinePlayer(player, false);
		if (GSConfig.ENABLE_CHAT_SERVER) {
			ChatService.onPlayerLogout(player);
		}
		PlayerService.storePlayer(player);
		ExpireTimerTask.getInstance().removePlayer(player);
		if (player.getCraftingTask() != null) {
			player.getCraftingTask().stop(true);
		}
		player.getEquipment().setOwner(null);
		player.getInventory().setOwner(null);
		player.getWarehouse().setOwner(null);
		player.getStorage(StorageType.ACCOUNT_WAREHOUSE.getId()).setOwner(null);
		//****//
		PlayerAccountData pad = player.getPlayerAccount().getPlayerAccountData(player.getObjectId());
		pad.setEquipment(player.getEquipment().getEquippedItems());
	}

	public static void tryLeaveWorld(Player player) {
		player.getMoveController().abortMove();
		if (player.getController().isInShutdownProgress()) {
			PlayerLeaveWorldService.startLeaveWorld(player);
		} else {
			int delay = 15;
			PlayerLeaveWorldService.startLeaveWorldDelay(player, (delay * 1000));
		}
	}
}