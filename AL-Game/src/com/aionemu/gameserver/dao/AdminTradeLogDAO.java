package com.aionemu.gameserver.dao;

import com.aionemu.commons.database.dao.DAO;

public abstract class AdminTradeLogDAO implements DAO {
	public final String getClassName() {
		return AdminTradeLogDAO.class.getName();
	}
	  
	public abstract boolean insertExchange(int adminId, String adminName, int playerId, String playerName, int itemId, String itemName, int itemCount, String Date);
}
