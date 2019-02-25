package com.aionemu.gameserver.services.player;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.AbyssRankDAO;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.dao.PlayerPassportsDAO;
import com.aionemu.gameserver.dao.PlayerQuestListDAO;
import com.aionemu.gameserver.dao.PlayerSkillListDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.house.House;
import com.aionemu.gameserver.world.World;

class GeneralUpdateTask
  implements Runnable
{
  private static final Logger log = LoggerFactory.getLogger(GeneralUpdateTask.class);
  private final int playerId;

  GeneralUpdateTask(int playerId)
  {
    this.playerId = playerId;
  }

  public void run()
  {
    Player player = World.getInstance().findPlayer(playerId);
    if (player != null)
      try {
        DAOManager.getDAO(AbyssRankDAO.class).storeAbyssRank(player);
        DAOManager.getDAO(PlayerSkillListDAO.class).storeSkills(player);
        DAOManager.getDAO(PlayerQuestListDAO.class).store(player);
        DAOManager.getDAO(PlayerPassportsDAO.class).store(player);
        DAOManager.getDAO(PlayerDAO.class).storePlayer(player);
        for (House house : player.getHouses())
          house.save();
      }
      catch (Exception ex) {
        log.error("Exception during periodic saving of player " + player.getName(), ex);
      }
  }
}