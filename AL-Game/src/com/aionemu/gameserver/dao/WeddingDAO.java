/*
 * =====================================================================================*
 * This file is part of Archsoft (Archsoft Home Software Development)                   *
 * Aion - Archsoft Development is closed Aion Project that use Old Aion Project Base    *
 * Like Aion-Unique, Aion-Lightning, Aion-Engine, Aion-Core, Aion-Extreme,              *
 * Aion-NextGen, Aion-Ger, U3J, Encom And other Aion project, All Credit Content        *
 * That they make is belong to them/Copyright is belong to them. And All new Content    *
 * that Archsoft make the copyright is belong to Archsoft.                              *
 * You may have agreement with Archsoft Development, before use this Engine/Source      *
 * You have agree with all of Term of Services agreement with Archsoft Development      *
 * =====================================================================================*
 */

/*
 * SAO Project
 */
package com.aionemu.gameserver.dao;

import com.aionemu.commons.database.dao.DAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.wedding.Wedding;

import java.sql.Timestamp;

/**
 * @author Alex, Xnemonix
 */
public abstract class WeddingDAO implements DAO {


    public abstract void loadPartner(final Player player);

    public abstract Timestamp getLastOnlineTime(int playerId);

    public abstract int getWorldId(int playerId);

    public abstract boolean insertWedding(Wedding wedding);

    public abstract void update(Wedding wedding);

    public abstract void removeWedding(int playerId, int partnerId);

    public abstract void insertToLog(Wedding wedding);

    @Override
    public String getClassName() {
        return WeddingDAO.class.getName();
    }
}
