package com.aionemu.gameserver.dao;

import com.aionemu.commons.database.dao.DAO;
import com.aionemu.gameserver.services.dreamergames.model.PlayerCpList;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * @author ID
 *
 */
public abstract class PlayerCpListDAO implements DAO {
	public final String getClassName() {
		return PlayerCpListDAO.class.getName();
	}
  
	public abstract PlayerCpList loadCpList(int paramInt);
  
	public abstract boolean storeCps(Player paramPlayer);
	
	public abstract void insertCps(Player paramPlayer, int paramInt1, int paramInt2, String paramString);
  
	public abstract void clearCpBeforeInsert(Player paramPlayer, String paramString);
}