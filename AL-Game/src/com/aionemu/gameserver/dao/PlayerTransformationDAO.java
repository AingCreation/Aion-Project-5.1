package com.aionemu.gameserver.dao;

import com.aionemu.commons.database.dao.DAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;

public abstract class PlayerTransformationDAO implements DAO
{
    public abstract void loadPlTransfo(Player player);
    public abstract boolean storePlTransfo(int playerId, int panelId, int ItemId);
    public abstract boolean deletePlTransfo(int playerId);
	
    public String getClassName() {
        return PlayerTransformationDAO.class.getName();
    }
}