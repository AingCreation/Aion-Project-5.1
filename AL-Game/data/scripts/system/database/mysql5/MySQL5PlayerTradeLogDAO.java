package mysql5;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.dao.PlayerTradeLogDAO;

public class MySQL5PlayerTradeLogDAO extends PlayerTradeLogDAO {

	/**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(String s, int i, int i1) {
        return MySQL5DAOUtils.supports(s, i, i1);
    }

	@Override
	public boolean insertExchange(final int playerId1, final String playerName1, final int playerId2, final String playerName2, final int itemId, final String itemName, final int itemCount, final String description) {
		return DB.insertUpdate("INSERT INTO `log_exchange_player` (`player_id`, `player_name`, `partner_id`, `partner_name`, `item_id`, `item_name`, `item_count`, `description`, `date`) VALUES(?,?,?,?,?,?,?,?,NOW())", new IUStH() {
            @Override
            public void handleInsertUpdate(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setInt(1, playerId1);
                preparedStatement.setString(2, playerName1);
                preparedStatement.setInt(3, playerId2);
                preparedStatement.setString(4, playerName2);
                preparedStatement.setInt(5, itemId);
                preparedStatement.setString(6, itemName);
                preparedStatement.setInt(7, itemCount);
                preparedStatement.setString(8, description);
                preparedStatement.execute();
            }
        });
	}

}
