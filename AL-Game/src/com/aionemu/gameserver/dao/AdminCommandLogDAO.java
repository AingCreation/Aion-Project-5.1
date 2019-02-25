package com.aionemu.gameserver.dao;

import com.aionemu.commons.database.dao.DAO;

public abstract class AdminCommandLogDAO implements DAO {
	public final String getClassName() {
		return AdminCommandLogDAO.class.getName();
	}
	  
	public abstract boolean insertCommandAdd(int paramInt1, String paramString1, int paramInt2, String paramString2, int paramInt3, int paramInt4, String paramString3, int paramInt5, String paramString4);
}