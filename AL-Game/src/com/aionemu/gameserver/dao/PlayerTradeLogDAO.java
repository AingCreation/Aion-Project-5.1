package com.aionemu.gameserver.dao;

import com.aionemu.commons.database.dao.DAO;

public abstract class PlayerTradeLogDAO implements DAO {
	
	public final String getClassName() {
		return PlayerTradeLogDAO.class.getName();
	}
	  
	public abstract boolean insertExchange(int playerId1, String playerName1, int playerId2, String playerName2, int itemId, String itemName, int itemCount, String Date);

}
