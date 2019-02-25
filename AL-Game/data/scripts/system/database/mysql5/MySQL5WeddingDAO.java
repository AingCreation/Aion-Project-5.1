/*
 * Aion Unity Project
 */
package mysql5;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.dao.WeddingDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.wedding.Wedding;
import com.aionemu.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * @author Ione542
 */
public class MySQL5WeddingDAO extends WeddingDAO {

    private final Logger log = LoggerFactory.getLogger(MySQL5WeddingDAO.class);

    @Override
    public void loadPartner(final Player player) {
        Connection con = null;
        int playerId = player.getObjectId();
        Wedding wedding = null;
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM `weddings` WHERE `player_id`=? OR `partner_id`=?");
            stmt.setInt(1, playerId);
            stmt.setInt(2, playerId);
            ResultSet rset = stmt.executeQuery();
            if (rset.next()) {
                int partner1Id = rset.getInt("player_id");
                int partner2Id = rset.getInt("partner_id");
                Timestamp timeWedd = rset.getTimestamp("wedding_time");
                boolean partner = playerId != partner1Id;
                String text = partner ? rset.getString("p_text") : rset.getString("text");
                String partnerText = !partner ? rset.getString("p_text") : rset.getString("text");
                Timestamp weddingData = rset.getTimestamp("wedding_data");
                int partnerId = partner ? partner1Id : partner2Id;
                Timestamp timeTp = partner ? rset.getTimestamp("teleport_time_partner") : rset.getTimestamp("teleport_time");
                String partnerName = getPartnerName(partnerId);
                wedding = new Wedding(playerId, partnerId, getLastOnlineTime(partnerId), getWorldId(partnerId), timeTp, timeWedd, text, partnerText, weddingData, partnerName, partner);

                Player p = World.getInstance().findPlayer(partnerId);
                if (p != null) {
                    wedding.setPartnerLastOnline(null);
                    wedding.setPartnerWorldId(p.getWorldId());
                    p.getWedding().setPartner(player);
                    wedding.setPartner(p);
                }
                player.setWedding(wedding);
            }
        } catch (Exception e) {
            log.error("Could not get partner for player: " + playerId + " from DB: " + e.getMessage(), e);
        } finally {
            DatabaseFactory.close(con);
        }
    }

    @Override
    public Timestamp getLastOnlineTime(int playerId) {
        Connection con = null;
        Timestamp i = null;
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM players WHERE id = ?");
            stmt.setInt(1, playerId);
            ResultSet lRS = stmt.executeQuery();
            if (lRS.next()) {
                i = lRS.getTimestamp("last_online");
            }
        } catch (SQLException l) {
            log.error(l + "");
        } finally {
            DatabaseFactory.close(con);
        }
        return i;
    }

    @Override
    public int getWorldId(int playerId) {
        Connection con = null;
        int i = 0;
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement("SELECT `world_id` FROM players WHERE id = ?");
            stmt.setInt(1, playerId);
            ResultSet rset = stmt.executeQuery();
            if (rset.next()) {
                i = rset.getInt("world_id");
            }
        } catch (SQLException l) {
            log.error(l + "");
        } finally {
            DatabaseFactory.close(con);
        }
        return i;
    }

    @Override
    public boolean insertWedding(Wedding wedding) {
        Connection con = null;
        boolean flag = false;
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement smtp = con.prepareStatement("INSERT INTO `weddings` (player_id, partner_id, teleport_time, wedding_time, wedding_data, text, p_text) VALUES (?, ?, ?, ?, ?, ?, ?)");
            smtp.setInt(1, wedding.getPlayerId());
            smtp.setInt(2, wedding.getPartnerId());
            smtp.setTimestamp(3, wedding.getTimeTp());
            smtp.setTimestamp(4, wedding.getTimeWedding());
            smtp.setTimestamp(5, wedding.getDataWedding());
            smtp.setString(6, wedding.getText());
            smtp.setString(7, wedding.getPartnerText());
            smtp.execute();
            flag = true;
        } catch (SQLException ex) {
            log.error(ex + "");
        } finally {
            DatabaseFactory.close(con);
        }
        return flag;
    }

    private String getPartnerName(int partnerId) {
        Connection con = null;
        String partnerName = "";
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement("SELECT `name` FROM `players` WHERE `id` = ?");
            stmt.setInt(1, partnerId);
            ResultSet rset = stmt.executeQuery();
            if (rset.next()) {
                partnerName = rset.getString("name");
            }
        } catch (SQLException l) {
            log.error(l + "");
        } finally {
            DatabaseFactory.close(con);
        }
        return partnerName;
    }

    @Override
    public void update(Wedding wedding) {
        Connection connection = null;
        try {
            connection = DatabaseFactory.getConnection();
            if (wedding.isPartner()) {
                PreparedStatement pstat = connection.prepareStatement("UPDATE `weddings` SET teleport_time_partner = ?, p_text = ? WHERE partner_id = ?");
                pstat.setTimestamp(1, wedding.getTimeTp());
                pstat.setString(2, wedding.getText());
                pstat.setInt(3, wedding.getPlayerId());
                pstat.execute();
            } else {
                PreparedStatement pstat = connection.prepareStatement("UPDATE `weddings` SET teleport_time = ?, text = ? WHERE player_id = ?");
                pstat.setTimestamp(1, wedding.getTimeTp());
                pstat.setString(2, wedding.getText());
                pstat.setInt(3, wedding.getPlayerId());
                pstat.execute();
            }
        } catch (SQLException ex) {
            log.error(ex + "");
        } finally {
            DatabaseFactory.close(connection);
        }
    }

    @Override
    public void removeWedding(int playerId, int partnerId) {
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = DatabaseFactory.getConnection();
            stmt = con.prepareStatement("DELETE FROM `weddings` WHERE (`player_id`=? AND `partner_id`=?) OR (`partner_id`=? AND `player_id`=?)");
            stmt.setInt(1, playerId);
            stmt.setInt(2, partnerId);
            stmt.setInt(3, playerId);
            stmt.setInt(4, partnerId);
            stmt.execute();
        } catch (SQLException e) {
            log.error("deleteWedding", e);
        } finally {
            DatabaseFactory.close(stmt, con);
        }
    }

    @Override
    public void insertToLog(Wedding wedding) {
        Connection con = null;
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement smtp = con.prepareStatement("INSERT INTO `weddings_log` (player_id, partner_name, wedding_start, wedding_end) VALUES (?, ?, ?, ?)");
            smtp.setInt(1, wedding.getPlayerId());
            smtp.setString(2, wedding.getPartnerName());
            smtp.setTimestamp(3, wedding.getDataWedding());
            smtp.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            smtp.execute();
        } catch (SQLException ex) {
            log.error(ex + "");
        } finally {
            DatabaseFactory.close(con);
        }
    }

    @Override
    public boolean supports(String databaseName, int majorVersion, int minorVersion) {
        return MySQL5DAOUtils.supports(databaseName, majorVersion, minorVersion);
    }
}
