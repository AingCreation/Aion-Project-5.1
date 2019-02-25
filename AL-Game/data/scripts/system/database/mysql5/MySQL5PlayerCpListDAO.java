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

package mysql5;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.database.IUStH;
import com.aionemu.commons.utils.GenericValidator;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.dao.PlayerCpListDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.dreamergames.model.PlayerCpEntry;
import com.aionemu.gameserver.services.dreamergames.model.PlayerCpList;
import com.aionemu.gameserver.skillengine.model.SkillEnchantTemplate;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MySQL5PlayerCpListDAO extends PlayerCpListDAO {

    public static final String INSERT_QUERY = "INSERT INTO `player_combat_points` (`player_id`, `slot_id`, `cp_point`) VALUES (?,?,?)";
    public static final String UPDATE_QUERY = "UPDATE `player_combat_points` set cp_point=? where player_id=? AND slot_id=?";
    public static final String DELETE_QUERY = "DELETE FROM `player_combat_points` WHERE `player_id`=? AND slot_id=?";
    public static final String SELECT_QUERY = "SELECT `slot_id`, `cp_point` FROM `player_combat_points` WHERE `player_id`=?";
    private static final Logger log = LoggerFactory.getLogger(MySQL5PlayerCpListDAO.class);
    private static final Predicate<PlayerCpEntry> cpsToInsertPredicate = new Predicate<PlayerCpEntry>() {
        @Override
        public boolean apply(@Nullable PlayerCpEntry input) {
            return input != null && PersistentState.NEW == input.getPersistentState();
        }
    };
    private static final Predicate<PlayerCpEntry> cpsToUpdatePredicate = new Predicate<PlayerCpEntry>() {
        @Override
        public boolean apply(@Nullable PlayerCpEntry input) {
            return input != null && PersistentState.UPDATE_REQUIRED == input.getPersistentState();
        }
    };
    private static final Predicate<PlayerCpEntry> cpsToDeletePredicate = new Predicate<PlayerCpEntry>() {
        @Override
        public boolean apply(@Nullable PlayerCpEntry input) {
            return input != null && PersistentState.DELETED == input.getPersistentState();
        }
    };

    @Override
    public PlayerCpList loadCpList(int playerId) {
        List<PlayerCpEntry> cps = new ArrayList<PlayerCpEntry>();
        Connection con = null;
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement(SELECT_QUERY);
            stmt.setInt(1, playerId);
            ResultSet rset = stmt.executeQuery();
            while (rset.next()) {
                int id = rset.getInt("slot_id");
                int lv = rset.getInt("cp_point");

                SkillEnchantTemplate[] slotIds = DataManager.SKILL_ENCHANT_DATA.getTemplatesForGroup(id);

                if (slotIds[0].getCategory().equals("stat_up")) {
                    cps.add(new PlayerCpEntry(id, true, false, false, lv, PersistentState.UPDATED));
                } else if (slotIds[0].getCategory().equals("enchant_skill")) {
                    cps.add(new PlayerCpEntry(id, false, true, false, lv, PersistentState.UPDATED));
                } else {
                    cps.add(new PlayerCpEntry(id, false, false, true, lv, PersistentState.UPDATED));
                }


            }
            rset.close();
            stmt.close();
        } catch (Exception e) {
            log.error("Could not restore Cp List data for player: " + playerId + " from DB: " + e.getMessage(), e);
        } finally {
            DatabaseFactory.close(con);
        }
        return new PlayerCpList(cps);
    }

    /**
     * Stores all player cps according to their persistence state
     */
    @Override
    public boolean storeCps(Player player) {
        List<PlayerCpEntry> cpsActive = Lists.newArrayList(player.getCpList().getAllCps());
        List<PlayerCpEntry> cpsDeleted = Lists.newArrayList(player.getCpList().getDeletedCps());
        store(player, cpsActive);
        store(player, cpsDeleted);

        return true;
    }

    private void store(Player player, List<PlayerCpEntry> cps) {
        Connection con = null;
        try {
            con = DatabaseFactory.getConnection();
            con.setAutoCommit(false);

            deleteCps(con, player, cps);
            addCps(con, player, cps);
            updateCps(con, player, cps);

        } catch (SQLException e) {
            log.error("Failed to open connection to database while saving Cp List for player " + player.getObjectId());
        } finally {
            DatabaseFactory.close(con);
        }

        for (PlayerCpEntry cp : cps) {
            cp.setPersistentState(PersistentState.UPDATED);
        }
    }

    private void addCps(Connection con, Player player, List<PlayerCpEntry> cps) {

        Collection<PlayerCpEntry> cpsToInsert = Collections2.filter(cps, cpsToInsertPredicate);
        if (GenericValidator.isBlankOrNull(cpsToInsert)) {
            return;
        }

        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(INSERT_QUERY);

            for (PlayerCpEntry cp : cpsToInsert) {
                //log.info("MYSQL INSERT SKILL: " + skill.getSkillId());
                ps.setInt(1, player.getObjectId());
                ps.setInt(2, cp.getSlotId());
                ps.setInt(3, cp.getCpPoint());
                ps.addBatch();
            }

            ps.executeBatch();
            con.commit();
        } catch (SQLException e) {
            //log.error("Can't add cps for player: " + player.getObjectId() + " Execption is : " + e.getMessage(), e);
        } finally {
            DatabaseFactory.close(ps);
        }
    }

    private void updateCps(Connection con, Player player, List<PlayerCpEntry> cps) {

        Collection<PlayerCpEntry> cpsToUpdate = Collections2.filter(cps, cpsToUpdatePredicate);
        if (GenericValidator.isBlankOrNull(cpsToUpdate)) {
            return;
        }

        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(UPDATE_QUERY);

            for (PlayerCpEntry cp : cpsToUpdate) {
                ps.setInt(1, cp.getCpPoint());
                ps.setInt(2, player.getObjectId());
                ps.setInt(3, cp.getSlotId());
                ps.addBatch();
            }

            ps.executeBatch();
            con.commit();
        } catch (SQLException e) {
            log.error("Can't update cps for player: " + player.getObjectId());
        } finally {
            DatabaseFactory.close(ps);
        }
    }

    private void deleteCps(Connection con, Player player, List<PlayerCpEntry> cps) {

        Collection<PlayerCpEntry> cpsToDelete = Collections2.filter(cps, cpsToDeletePredicate);
        if (GenericValidator.isBlankOrNull(cpsToDelete)) {
            return;
        }

        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(DELETE_QUERY);

            for (PlayerCpEntry cp : cpsToDelete) {
                ps.setInt(1, player.getObjectId());
                ps.setInt(2, cp.getSlotId());
                ps.addBatch();
            }

            ps.executeBatch();
            con.commit();
        } catch (SQLException e) {
            log.error("Can't delete cp for player: " + player.getObjectId());
        } finally {
            DatabaseFactory.close(ps);
        }
    }

    @Override
    public void clearCpBeforeInsert(final Player player, final String category) {
        DB.insertUpdate("DELETE FROM `player_combat_points` WHERE `player_id`=? AND category=? ", new IUStH() {

            @Override
            public void handleInsertUpdate(PreparedStatement stmt) throws SQLException {
                stmt.setInt(1, player.getObjectId());
                stmt.setString(2, category);
                stmt.execute();
            }
        });
    }

    @Override
    public void insertCps(final Player player, final int slotId, final int cpPoint, final String category) {
        DB.insertUpdate("INSERT INTO player_combat_points (player_id, slot_id, cp_point, category) VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE player_id=VALUES(player_id), slot_id=VALUES(slot_id), cp_point=VALUES(cp_point), category=VALUES(category)", new IUStH() {
            @Override
            public void handleInsertUpdate(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setInt(1, player.getObjectId());
                preparedStatement.setInt(2, slotId);
                preparedStatement.setInt(3, cpPoint);
                preparedStatement.setString(4, category);
                preparedStatement.execute();
            }
        });
    }

    @Override
    public boolean supports(String databaseName, int majorVersion, int minorVersion) {
        return MySQL5DAOUtils.supports(databaseName, majorVersion, minorVersion);
    }
}