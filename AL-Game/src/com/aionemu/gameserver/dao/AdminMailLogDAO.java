package com.aionemu.gameserver.dao;

import com.aionemu.commons.database.dao.DAO;

public abstract class AdminMailLogDAO implements DAO {
	public final String getClassName() {
		return AdminMailLogDAO.class.getName();
	}
	  
	public abstract boolean insertExchange(int adminId, String adminName, int itemId, String itemName, int itemCount, String playerReciveName, String Date);
}
