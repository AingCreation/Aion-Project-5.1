package mysql5;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.dao.PlayerMailLogDAO;

public class MySQL5PlayerMailLogDAO extends PlayerMailLogDAO {

	/**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(String s, int i, int i1) {
        return MySQL5DAOUtils.supports(s, i, i1);
    }

	@Override
	public boolean insertExchange(final int playerSenderId, final String playerSenderName, final int itemId, final String itemName, final int itemCount, final String playerReciveName, final String description) {
		return DB.insertUpdate("INSERT INTO `log_mail_player` (`sender_id`, `sender_name`, `item_id`, `item_name`, `item_count`, `player_recive_name`, `description`, `date`) VALUES(?,?,?,?,?,?,?,NOW())", new IUStH() {
            @Override
            public void handleInsertUpdate(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setInt(1, playerSenderId);
                preparedStatement.setString(2, playerSenderName);
                preparedStatement.setInt(3, itemId);
                preparedStatement.setString(4, itemName);
                preparedStatement.setInt(5, itemCount);
                preparedStatement.setString(6, playerReciveName);
                preparedStatement.setString(7, description);
                preparedStatement.execute();
            }
        });
	}

}
