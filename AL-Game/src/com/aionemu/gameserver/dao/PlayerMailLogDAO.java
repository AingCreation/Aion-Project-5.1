package com.aionemu.gameserver.dao;

import com.aionemu.commons.database.dao.DAO;

public abstract class PlayerMailLogDAO implements DAO {
	
	public final String getClassName() {
		return PlayerMailLogDAO.class.getName();
	}
	  
	public abstract boolean insertExchange(int playerSenderId, String playerSenderName, int itemId, String itemName, int itemCount, String playerReciveName, String Date);

}
