package mysql5;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.database.IUStH;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.commons.utils.GenericValidator;
import com.aionemu.gameserver.configs.main.CacheConfig;
import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.dataholders.PlayerInitialData;
import com.aionemu.gameserver.dataholders.PlayerInitialData.LocationData;
import com.aionemu.gameserver.model.Gender;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.account.PlayerAccountData;
import com.aionemu.gameserver.model.gameobjects.player.Mailbox;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.team.legion.*;
import com.aionemu.gameserver.world.MapRegion;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldPosition;
import com.google.common.collect.Maps;

import javolution.util.FastMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

public class MySQL5PlayerDAO extends PlayerDAO
{
	private static final Logger log = LoggerFactory.getLogger(MySQL5PlayerDAO.class);
	private FastMap<Integer, PlayerCommonData> playerCommonData = new FastMap<Integer, PlayerCommonData>().shared();
	private FastMap<String, PlayerCommonData> playerCommonDataByName = new FastMap<String, PlayerCommonData>().shared();
	private MapRegion mr = null;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isNameUsed(final String name) {
		PreparedStatement s = DB.prepareStatement("SELECT count(id) as cnt FROM players WHERE ? = players.name");
		try {
			s.setString(1, name);
			ResultSet rs = s.executeQuery();
			rs.next();
			return rs.getInt("cnt") > 0;
		}
		catch (SQLException e) {
			log.error("Can't check if name " + name + ", is used, returning possitive result", e);
			return true;
		}
		finally {
			DB.close(s);
		}
	}

	@Override
	public Map<Integer, String> getPlayerNames(Collection<Integer> playerObjectIds) {

		if(GenericValidator.isBlankOrNull(playerObjectIds)){
			return Collections.emptyMap();
		}

		Map<Integer, String> result = Maps.newHashMap();

		String sql = "SELECT id, `name` FROM players WHERE id IN(%s)";
		sql = String.format(sql, StringUtils.join(playerObjectIds, ", "));
		PreparedStatement s = DB.prepareStatement(sql);
		try{
			ResultSet rs = s.executeQuery();
			while(rs.next()){
				int id = rs.getInt("id");
				String name = rs.getString("name");
				result.put(id, name);
			}
		} catch (SQLException e){
			throw new RuntimeException("Failed to load player names", e);
		} finally {
			DB.close(s);
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void storePlayer(final Player player) {
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con
				.prepareStatement("UPDATE players SET name=?, exp=?, recoverexp=?, x=?, y=?, z=?, heading=?, world_id=?, gender=?, race=?, player_class=?, last_online=?, quest_expands=?, npc_expands=?, advenced_stigma_slot_size=?, warehouse_size=?, note=?, title_id=?, bonus_title_id=?, dp=?, soul_sickness=?, mailbox_letters=?, reposte_energy=?, mentor_flag_time=?, world_owner=?, stamps=?, rewarded_pass=?, last_stamp=?, is_archdaeva=?, growth_aura=?, join_legion_id=?, join_state=?, golden_points=?, luna_consume=?, muni_keys=?, luna_consume_count=?, wardrobe_slot=? WHERE id=?");

			log.debug("[DAO: MySQL5PlayerDAO] storing player " + player.getObjectId() + " " + player.getName());
			PlayerCommonData pcd = player.getCommonData();
			stmt.setString(1, player.getName());
			stmt.setLong(2, pcd.getExp());
			stmt.setLong(3, pcd.getExpRecoverable());
			stmt.setFloat(4, player.getX());
			stmt.setFloat(5, player.getY());
			stmt.setFloat(6, player.getZ());
			stmt.setInt(7, player.getHeading());
			stmt.setInt(8, player.getWorldId());
			stmt.setString(9, player.getGender().toString());
			stmt.setString(10, player.getRace().toString());
			stmt.setString(11, pcd.getPlayerClass().toString());
			stmt.setTimestamp(12, pcd.getLastOnline());
			stmt.setInt(13, player.getQuestExpands());
			stmt.setInt(14, player.getNpcExpands());
			stmt.setInt(15, pcd.getAdvencedStigmaSlotSize());
			stmt.setInt(16, player.getWarehouseSize());
			stmt.setString(17, pcd.getNote());
			stmt.setInt(18, pcd.getTitleId());
			stmt.setInt(19, pcd.getBonusTitleId());
			stmt.setInt(20, pcd.getDp());
			stmt.setInt(21, pcd.getDeathCount());
			Mailbox mailBox = player.getMailbox();
			int mails = mailBox != null ? mailBox.size() : pcd.getMailboxLetters();
			stmt.setInt(22, mails);
			stmt.setLong(23, pcd.getCurrentReposteEnergy());
			stmt.setInt(24, pcd.getMentorFlagTime());
			stmt.setInt(25, player.getPosition().getWorldMapInstance().getOwnerId());
			stmt.setInt(26, pcd.getPassportStamps());
            stmt.setInt(27, pcd.getPassportReward());
            stmt.setTimestamp(28, pcd.getLastStamp());
            stmt.setBoolean(29, pcd.isArchDaeva());
            stmt.setLong(30, pcd.getGrowthEnergy());
			stmt.setInt(31, pcd.getJoinRequestLegionId());
            stmt.setString(32, pcd.getJoinRequestState().toString());
			stmt.setLong(33, pcd.getGoldenStarEnergy());
			stmt.setInt(34, pcd.getLunaConsumePoint());
			stmt.setInt(35, pcd.getMuniKeys());
			stmt.setInt(36, pcd.getLunaConsumeCount());
			stmt.setInt(37, pcd.getWardrobeSlot());
			stmt.setInt(38, player.getObjectId());
			stmt.execute();
			stmt.close();
		}
		catch (Exception e) {
			log.error("Error saving player: " + player.getObjectId() + " " + player.getName(), e);
		}
		finally {
			DatabaseFactory.close(con);
		}
		if (CacheConfig.CACHE_COMMONDATA) {
			PlayerCommonData cached = playerCommonData.get(player.getObjectId());
			if (cached != null) {
				playerCommonData.putEntry(player.getCommonData().getPlayerObjId(), player.getCommonData());
				playerCommonDataByName.putEntry(player.getName().toLowerCase(), player.getCommonData());
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean saveNewPlayer(final PlayerCommonData pcd, final int accountId, final String accountName) {
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement preparedStatement = con
				.prepareStatement("INSERT INTO players(id, `name`, account_id, account_name, x, y, z, heading, world_id, gender, race, player_class, quest_expands, npc_expands, warehouse_size, bonus_title_id, is_archdaeva, wardrobe_slot, online) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 2, 0)");

			log.debug("[DAO: MySQL5PlayerDAO] saving new player: " + pcd.getPlayerObjId() + " " + pcd.getName());

			preparedStatement.setInt(1, pcd.getPlayerObjId());
			preparedStatement.setString(2, pcd.getName());
			preparedStatement.setInt(3, accountId);
			preparedStatement.setString(4, accountName);
			preparedStatement.setFloat(5, pcd.getPosition().getX());
			preparedStatement.setFloat(6, pcd.getPosition().getY());
			preparedStatement.setFloat(7, pcd.getPosition().getZ());
			preparedStatement.setInt(8, pcd.getPosition().getHeading());
			preparedStatement.setInt(9, pcd.getPosition().getMapId());
			preparedStatement.setString(10, pcd.getGender().toString());
			preparedStatement.setString(11, pcd.getRace().toString());
			preparedStatement.setString(12, pcd.getPlayerClass().toString());
			preparedStatement.setInt(13, pcd.getQuestExpands());
			preparedStatement.setInt(14, pcd.getNpcExpands());
			preparedStatement.setInt(15, pcd.getWarehouseSize());
			preparedStatement.setInt(16, pcd.getBonusTitleId());
			preparedStatement.setBoolean(17, pcd.isArchDaeva());
			preparedStatement.execute();
			preparedStatement.close();
		}
		catch (Exception e) {
			log.error("Error saving new player: " + pcd.getPlayerObjId() + " " + pcd.getName(), e);
			return false;
		}
		finally {
			DatabaseFactory.close(con);
		}
		if (CacheConfig.CACHE_COMMONDATA) {
			playerCommonData.put(pcd.getPlayerObjId(), pcd);
			playerCommonDataByName.put(pcd.getName().toLowerCase(), pcd);
		}
		return true;
	}

	@Override
	public PlayerCommonData loadPlayerCommonDataByName(final String name) {
		Player player = World.getInstance().findPlayer(name);
		if (player != null)
			return player.getCommonData();
		PlayerCommonData pcd = playerCommonDataByName.get(name.toLowerCase());
		if (pcd != null)
			return pcd;
		int playerObjId = 0;

		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement("SELECT id FROM players WHERE name = ?");
			stmt.setString(1, name);
			ResultSet rset = stmt.executeQuery();
			if (rset.next())
				playerObjId = rset.getInt("id");
			rset.close();
			stmt.close();
		}
		catch (Exception e) {
			//log.error("Could not restore playerId data for player name: " + name + " from DB: " + e.getMessage(), e);
		}
		finally {
			DatabaseFactory.close(con);
		}

		if (playerObjId == 0) {
			return null;
		}
		return loadPlayerCommonData(playerObjId);
	}

	@Override
	public PlayerCommonData loadPlayerCommonData(final int playerObjId) {

		PlayerCommonData cached = playerCommonData.get(playerObjId);
		if (cached != null) {
			log.debug("[DAO: MySQL5PlayerDAO] PlayerCommonData for id: " + playerObjId + " obtained from cache");
			return cached;
		}
		final PlayerCommonData cd = new PlayerCommonData(playerObjId);
		boolean success = false;
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement("SELECT * FROM players WHERE id = ?");
			stmt.setInt(1, playerObjId);
			ResultSet resultSet = stmt.executeQuery();
			log.debug("[DAO: MySQL5PlayerDAO] loading from db " + playerObjId);

			if (resultSet.next()) {
				success = true;
				cd.setName(resultSet.getString("name"));
				// set player class before exp
				cd.setPlayerClass(PlayerClass.valueOf(resultSet.getString("player_class")));
				cd.setExp(resultSet.getLong("exp"), false);
				cd.setRecoverableExp(resultSet.getLong("recoverexp"));
				cd.setRace(Race.valueOf(resultSet.getString("race")));
				cd.setGender(Gender.valueOf(resultSet.getString("gender")));
				cd.setLastOnline(resultSet.getTimestamp("last_online"));
				cd.setNote(resultSet.getString("note"));
				cd.setQuestExpands(resultSet.getInt("quest_expands"));
				cd.setNpcExpands(resultSet.getInt("npc_expands"));
				cd.setAdvencedStigmaSlotSize(resultSet.getInt("advenced_stigma_slot_size"));
				cd.setTitleId(resultSet.getInt("title_id"));
				cd.setBonusTitleId(resultSet.getInt("bonus_title_id"));
				cd.setWarehouseSize(resultSet.getInt("warehouse_size"));
				cd.setOnline(resultSet.getBoolean("online"));
				cd.setMailboxLetters(resultSet.getInt("mailbox_letters"));
				cd.setDp(resultSet.getInt("dp"));
				cd.setDeathCount(resultSet.getInt("soul_sickness"));
				cd.setCurrentReposteEnergy(resultSet.getLong("reposte_energy"));
				cd.setinNewbieGuide(resultSet.getInt("is_newbie_guide"));

				float x = resultSet.getFloat("x");
				float y = resultSet.getFloat("y");
				float z = resultSet.getFloat("z");
				byte heading = resultSet.getByte("heading");
				int worldId = resultSet.getInt("world_id");
				PlayerInitialData playerInitialData = DataManager.PLAYER_INITIAL_DATA;

                if (worldId == 0) {
                    LocationData ld = playerInitialData.getSpawnLocation(cd.getRace());
                    x = ld.getX();
                    y = ld.getY();
                    z = ld.getZ();
                    heading = ld.getHeading();
                    worldId = ld.getMapId();
                } else {
                    boolean checkThis = World.getInstance().getWorldMap(worldId).isInstanceType();
                    // this helps to pretend an player loading error
                    // if you have a better idea do it :)
                    if (checkThis) {
                        mr = null;
                    } else {
                        mr = World.getInstance().getWorldMap(worldId).getMainWorldMapInstance().getRegion(x, y, z);
                    }

                    if (mr == null && playerInitialData != null) {
                        // unstuck unlucky characters :)
                        LocationData ld = playerInitialData.getSpawnLocation(cd.getRace());
                        x = ld.getX();
                        y = ld.getY();
                        z = ld.getZ();
                        heading = ld.getHeading();
                        worldId = ld.getMapId();
                    }
                }
				WorldPosition position = World.getInstance().createPosition(worldId, x, y, z, heading, 0);
				cd.setPosition(position);
				cd.setWorldOwnerId(resultSet.getInt("world_owner"));
				cd.setMentorFlagTime(resultSet.getInt("mentor_flag_time"));
				cd.setLastTransferTime(resultSet.getLong("last_transfer_time"));
				cd.setPassportStamps(resultSet.getInt("stamps"));
                cd.setPassportReward(resultSet.getInt("rewarded_pass"));
                cd.setLastStamp(resultSet.getTimestamp("last_stamp"));
                cd.setArchDaeva(resultSet.getBoolean("is_archdaeva"));
                cd.setAddonCP(resultSet.getInt("addon_cp"));
                cd.addGrowthEnergy(resultSet.getLong("growth_aura"));
				cd.setJoinRequestLegionId(resultSet.getInt("join_legion_id"));
                cd.setJoinRequestState(LegionJoinRequestState.valueOf(resultSet.getString("join_state")));
				cd.addGoldenStarEnergy(resultSet.getLong("golden_points"));
				cd.setLunaConsumePoint(resultSet.getInt("luna_consume"));
				cd.setMuniKeys(resultSet.getInt("muni_keys"));
				cd.setLunaConsumeCount(resultSet.getInt("luna_consume_count"));
				cd.setWardrobeSlot(resultSet.getInt("wardrobe_slot"));
			}
			resultSet.close();
			stmt.close();
		}
		catch (Exception e) {
			//log.error("Could not restore PlayerCommonData data for player: " + playerObjId + " from DB: " + e.getMessage(), e);
		}
		finally {
			DatabaseFactory.close(con);
		}

		if (success) {
			if (CacheConfig.CACHE_COMMONDATA) {
				playerCommonData.put(playerObjId, cd);
				playerCommonDataByName.put(cd.getName().toLowerCase(), cd);
			}
			return cd;
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deletePlayer(int playerId) {
		PreparedStatement statement = DB.prepareStatement("DELETE FROM players WHERE id = ?");
		try {
			statement.setInt(1, playerId);
		}
		catch (SQLException e) {
			log.error("Some crap, can't set int parameter to PreparedStatement", e);
		}
		if (CacheConfig.CACHE_COMMONDATA) {
			PlayerCommonData pcd = playerCommonData.remove(playerId);
			if (pcd != null)
				playerCommonDataByName.remove(pcd.getName().toLowerCase());
		}
		DB.executeUpdateAndClose(statement);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Integer> getPlayerOidsOnAccount(final int accountId) {
		final List<Integer> result = new ArrayList<Integer>();
		boolean success = DB.select("SELECT id FROM players WHERE account_id = ?", new ParamReadStH() {

			@Override
			public void handleRead(ResultSet resultSet) throws SQLException {
				while (resultSet.next()) {
					result.add(resultSet.getInt("id"));
				}
			}

			@Override
			public void setParams(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setInt(1, accountId);
			}
		});

		return success ? result : null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCreationDeletionTime(final PlayerAccountData acData) {
		DB.select("SELECT creation_date, deletion_date FROM players WHERE id = ?", new ParamReadStH() {

			@Override
			public void setParams(PreparedStatement stmt) throws SQLException {
				stmt.setInt(1, acData.getPlayerCommonData().getPlayerObjId());
			}

			@Override
			public void handleRead(ResultSet rset) throws SQLException {
				rset.next();

				acData.setDeletionDate(rset.getTimestamp("deletion_date"));
				acData.setCreationDate(rset.getTimestamp("creation_date"));
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateDeletionTime(final int objectId, final Timestamp deletionDate) {
		DB.insertUpdate("UPDATE players set deletion_date = ? where id = ?", new IUStH() {

			@Override
			public void handleInsertUpdate(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setTimestamp(1, deletionDate);
				preparedStatement.setInt(2, objectId);
				preparedStatement.execute();
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void storeCreationTime(final int objectId, final Timestamp creationDate) {
		DB.insertUpdate("UPDATE players set creation_date = ? where id = ?", new IUStH() {

			@Override
			public void handleInsertUpdate(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setTimestamp(1, creationDate);
				preparedStatement.setInt(2, objectId);
				preparedStatement.execute();
			}
		});
	}

	@Override
	public void storeLastOnlineTime(final int objectId, final Timestamp lastOnline) {
		DB.insertUpdate("UPDATE players set last_online = ? where id = ?", new IUStH() {

			@Override
			public void handleInsertUpdate(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setTimestamp(1, lastOnline);
				preparedStatement.setInt(2, objectId);
				preparedStatement.execute();
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int[] getUsedIDs() {
		PreparedStatement statement = DB.prepareStatement("SELECT id FROM players", ResultSet.TYPE_SCROLL_INSENSITIVE,
			ResultSet.CONCUR_READ_ONLY);

		try {
			ResultSet rs = statement.executeQuery();
			rs.last();
			int count = rs.getRow();
			rs.beforeFirst();
			int[] ids = new int[count];
			for (int i = 0; i < count; i++) {
				rs.next();
				ids[i] = rs.getInt("id");
			}
			return ids;
		}
		catch (SQLException e) {
			log.error("Can't get list of id's from players table", e);
		}
		finally {
			DB.close(statement);
		}

		return new int[0];
	}

	/**
	 * {@inheritDoc} - Saelya
	 */
	@Override
	public void onlinePlayer(final Player player, final boolean online) {
		DB.insertUpdate("UPDATE players SET online=? WHERE id=?", new IUStH() {

			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException {
				log.debug("[DAO: MySQL5PlayerDAO] online status " + player.getObjectId() + " " + player.getName());

				stmt.setBoolean(1, online);
				stmt.setInt(2, player.getObjectId());
				stmt.execute();
			}
		});
	}

	/**
	 * {@inheritDoc} - Nemiroff
	 */
	@Override
	public void setPlayersOffline(final boolean online) {
		DB.insertUpdate("UPDATE players SET online=?", new IUStH() {

			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException {
				stmt.setBoolean(1, online);
				stmt.execute();
			}
		});
	}

	@Override
	public String getPlayerNameByObjId(final int playerObjId) {
		final String[] result = new String[1];
		DB.select("SELECT name FROM players WHERE id = ?", new ParamReadStH() {

			@Override
			public void handleRead(ResultSet arg0) throws SQLException {
				// TODO: Auto-generated method stub
				arg0.next();
				result[0] = arg0.getString("name");
			}

			@Override
			public void setParams(PreparedStatement arg0) throws SQLException {
				// TODO: Auto-generated method stub
				arg0.setInt(1, playerObjId);
			}
		});
		return result[0];
	}
	
	@Override
	public int getPlayerLunaConsumeByObjId(final int playerObjId) {
		final int[] result = new int[1];
		DB.select("SELECT luna_consume FROM players WHERE id = ?", new ParamReadStH() {

			@Override
			public void handleRead(ResultSet arg0) throws SQLException {
				// TODO: Auto-generated method stub
				arg0.next();
				result[0] = arg0.getInt("luna_consume");
			}

			@Override
			public void setParams(PreparedStatement arg0) throws SQLException {
				// TODO: Auto-generated method stub
				arg0.setInt(1, playerObjId);
			}
		});
		return result[0];
	}
	
	@Override
	public int getPlayerIdByName(final String playerName) {
		final int[] result = new int[1];
		DB.select("SELECT id FROM players WHERE name = ?", new ParamReadStH() {

			@Override
			public void handleRead(ResultSet arg0) throws SQLException {
				// TODO: Auto-generated method stub
				arg0.next();
				result[0] = arg0.getInt("id");
			}

			@Override
			public void setParams(PreparedStatement arg0) throws SQLException {
				// TODO: Auto-generated method stub
				arg0.setString(1, playerName);
			}
		});
		return result[0];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getAccountIdByName(final String name) {
		Connection con = null;
		int accountId = 0;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement s = con.prepareStatement("SELECT `account_id` FROM `players` WHERE `name` = ?");
			s.setString(1, name);
			ResultSet rs = s.executeQuery();
			rs.next();
			accountId = rs.getInt("account_id");
			rs.close();
			s.close();
		}
		catch (Exception e) {
			return 0;
		}
		finally {
			DatabaseFactory.close(con);
		}
		return accountId;
	}

	/**
	 * @author xTz
	 */
	@Override
	public void storePlayerName(final PlayerCommonData recipientCommonData) {
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement("UPDATE players SET name=? WHERE id=?");

			log.debug("[DAO: MySQL5PlayerDAO] storing playerName " + recipientCommonData.getPlayerObjId() + " "
				+ recipientCommonData.getName());

			stmt.setString(1, recipientCommonData.getName());
			stmt.setInt(2, recipientCommonData.getPlayerObjId());
			stmt.execute();
			stmt.close();
		}
		catch (Exception e) {
			log.error(
				"Error saving playerName: " + recipientCommonData.getPlayerObjId() + " " + recipientCommonData.getName(), e);
		}
		finally {
			DatabaseFactory.close(con);
		}
	}

	@Override
	public int getCharacterCountOnAccount(final int accountId) {
		Connection con = null;
		int cnt = 0;

		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con
				.prepareStatement("SELECT COUNT(*) AS cnt FROM `players` WHERE `account_id` = ? AND (players.deletion_date IS NULL || players.deletion_date > CURRENT_TIMESTAMP)");
			stmt.setInt(1, accountId);
			ResultSet rs = stmt.executeQuery();
			rs.next();
			cnt = rs.getInt("cnt");
			rs.close();
			stmt.close();
		}
		catch (Exception e) {
			return 0;
		}
		finally {
			DatabaseFactory.close(con);
		}

		return cnt;
	}

	@Override
	public int getCharacterCountForRace(Race race) {
		Connection con = null;
		int count = 0;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con
				.prepareStatement("SELECT COUNT(DISTINCT(`account_name`)) AS `count` FROM `players` WHERE `race` = ? AND `exp` >= ?");
			stmt.setString(1, race.name());
			stmt.setLong(2, DataManager.PLAYER_EXPERIENCE_TABLE.getStartExpForLevel(GSConfig.RATIO_MIN_REQUIRED_LEVEL));
			ResultSet rs = stmt.executeQuery();
			rs.next();
			count = rs.getInt("count");
			rs.close();
			stmt.close();
		}
		catch (Exception e) {
			return 0;
		}
		finally {
			DatabaseFactory.close(con);
		}

		return count;
	}

	@Override
	public int getOnlinePlayerCount() {
		Connection con = null;
		int count = 0;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement("SELECT COUNT(*) AS `count` FROM `players` WHERE `online` = ?");
			stmt.setBoolean(1, true);
			ResultSet rs = stmt.executeQuery();
			rs.next();
			count = rs.getInt("count");
			rs.close();
			stmt.close();
		}
		catch (Exception e) {
			return 0;
		}
		finally {
			DatabaseFactory.close(con);
		}

		return count;
	}

	/**
	 * {@inheritDoc}
	 */
    @Override
    public List<Integer> getPlayersToDelete(final int daysOfInactivity, int limitation) {
		String SELECT_QUERY = "SELECT id FROM players WHERE UNIX_TIMESTAMP(CURDATE())-UNIX_TIMESTAMP(last_online) > ? * 24 * 60 * 60";
		
		//limitation
		if (limitation > 0)
			SELECT_QUERY += " LIMIT " + limitation;
		
        final List<Integer> playersToDelete = new ArrayList<Integer>();

        DB.select(SELECT_QUERY, new ParamReadStH() {

            @Override
            public void setParams(PreparedStatement stmt) throws SQLException {
                stmt.setInt(1, daysOfInactivity);
			}
                
            @Override  
            public void handleRead(ResultSet rset) throws SQLException {        
                while (rset.next()) {          
                    int id = rset.getInt("id");           
                    playersToDelete.add(id);       
                } 
            }
        });
        return playersToDelete;
    }

	/**
	 * {@inheritDoc} - KID
	 */
	@Override
	public void setPlayerLastTransferTime(final int playerId, final long time) {
		DB.insertUpdate("UPDATE players SET last_transfer_time=? WHERE id=?", new IUStH() {
			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException {
				stmt.setLong(1, time);
				stmt.setInt(2, playerId);
				stmt.execute();
			}
		});
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean supports(String s, int i, int i1) {
		return MySQL5DAOUtils.supports(s, i, i1);
	}
	
	@Override
	public Timestamp getCharacterCreationDateId(final int obj) {
		Connection con = null;
		Timestamp creationDate;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement s = con.prepareStatement("SELECT `creation_date` FROM `players` WHERE `id` = ?");
			s.setInt(1, obj);
			ResultSet rs = s.executeQuery();
			rs.next();
			creationDate = rs.getTimestamp("creation_date");
			rs.close();
			s.close();
		}
		catch (Exception e) {
			return null;
		}
		finally {
			DatabaseFactory.close(con);
		}
		return creationDate;
	}
	
	@Override
    public void updateLegionJoinRequestState(final int playerId, final LegionJoinRequestState state)  {
    	DB.insertUpdate("UPDATE players SET join_state=? WHERE id=?", new IUStH()  {
            @Override
            public void handleInsertUpdate(PreparedStatement stmt) throws SQLException  {
                stmt.setString(1, state.name());
                stmt.setInt(2, playerId);
                stmt.execute();
            }
        });        
    }
	
	@Override
    public void clearJoinRequest(final int playerId) {
        Connection con = null;
        try  {
            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement("UPDATE players SET join_legion_id=?, join_state=? WHERE id=?");
            stmt.setInt(1, 0);
            stmt.setString(2, "NONE");
            stmt.setInt(3, playerId);
		} catch (Exception e) {
		} finally {
			DatabaseFactory.close(con);
		}
    }
	
	@Override
    public void updateNewbieGuide(final int objectId, final int state) {
        DB.insertUpdate("UPDATE players SET is_newbie_guide = ? where id = ?", new IUStH() {
            @Override
            public void handleInsertUpdate(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setInt(1, state);
                preparedStatement.setInt(2, objectId);
                preparedStatement.execute();
            }
        });
    }
	
	@Override
    public void getJoinRequestState(final Player player) {
        String SELECT_QUERY = "SELECT * FROM players WHERE id=?";
        DB.select(SELECT_QUERY, new ParamReadStH() {
            @Override
            public void setParams(PreparedStatement stmt) throws SQLException {
                stmt.setInt(1, player.getObjectId());
            }
            @Override
            public void handleRead(ResultSet rset) throws SQLException {
            	if (rset.next()) {
            		player.getCommonData().setJoinRequestState(LegionJoinRequestState.valueOf(rset.getString("join_state")));
            	}
            }
        });
    }
}