package mysql5;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import com.aionemu.gameserver.dao.AdminCommandLogDAO;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySQL5AdminCommandLogDAO extends AdminCommandLogDAO {

    @Override
    public boolean insertCommandAdd(final int adminId, final String adminName, final int playerTargetId, final String playerTargetName, final int itemObjId, final int itemId, final String itemName, final int itemCount, final String description) {
        return DB.insertUpdate("INSERT INTO `log_command_add` (`admin_id`, `admin_name`, `player_id`, `player_name`, `item_unique`, `item_id`, `item_name`, `item_count`, `description`, `date`) VALUES(?,?,?,?,?,?,?,?,?,NOW())", new IUStH() {
            @Override
            public void handleInsertUpdate(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setInt(1, adminId);
                preparedStatement.setString(2, adminName);
                preparedStatement.setInt(3, playerTargetId);
                preparedStatement.setString(4, playerTargetName);
                preparedStatement.setInt(5, itemObjId);
                preparedStatement.setInt(6, itemId);
                preparedStatement.setString(7, itemName);
                preparedStatement.setInt(8, itemCount);
                preparedStatement.setString(9, description);
                preparedStatement.execute();
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
}