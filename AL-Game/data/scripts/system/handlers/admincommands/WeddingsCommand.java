/*
 * Unity Aion project
 */
package admincommands;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.main.HTMLConfig;
import com.aionemu.gameserver.configs.main.WeddingsConfig;
import com.aionemu.gameserver.dao.WeddingDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.group.PlayerGroupService;
import com.aionemu.gameserver.model.wedding.Wedding;
import com.aionemu.gameserver.model.wedding.WeddingService;
import com.aionemu.gameserver.services.MuiService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * @author Alex, Ione542
 */
public class WeddingsCommand extends AdminCommand {

    public WeddingsCommand() {
        super("wedding");
    }

    @Override
    public void execute(Player player, String... params) {
        if (!WeddingsConfig.WEDDINGS_ENABLE) {
            PacketSendUtility.sendBrightYellowMessageOnCenter(player, MuiService.getInstance().getMessage("WEDDING_DISABLED"));
            return;
        }

        if (params == null || params.length < 1) {
            PacketSendUtility.sendMessage(player, "Command List //wedding < marry | tp | cometome | unmarry | party | info | text>");
            return;
        }

        String command = params[0];
        if (command.equals("marry")) {
            WeddingService.getInstance().marry(player, params);
        } else if (command.equals("tp")) {
            WeddingService.getInstance().teleport(player);
        } else if (command.equals("cometome")) {
            WeddingService.getInstance().summon(player);
        } else if (command.equals("unmarry")) {
            WeddingService.getInstance().unMarry(player);
        } else if (command.equals("party")) {
            if (!player.isMarried()) {
                PacketSendUtility.sendMessage(player, MuiService.getInstance().getMessage("WEDDING_NOT_MARRIAGE"));
                return;
            } else if (!player.getWedding().isOnline()) {
                PacketSendUtility.sendMessage(player, MuiService.getInstance().getMessage("WEDDING_PARTNER_NOT_ONLINE"));
                return;
            }
            PlayerGroupService.inviteToGroup(player, player.getWedding().getPartner());
            PacketSendUtility.sendMessage(player, MuiService.getInstance().getMessage("WEDDING_INVITE_GROUP"));
        } else if (command.equals("info")) {
            if (HTMLConfig.ENABLE_HTML_WELCOME) {
                WeddingService.getInstance().showInfoWedding(player);
            } else {
                PacketSendUtility.sendMessage(player, MuiService.getInstance().getMessage("WEDDING_CONTACT_ADMIN"));
            }
        } else if (command.equals("text")) {
            String text = null;
            try {
                text = Util.convertName(params[1]);
                for (int itr = 2; itr < params.length; itr++) {
                    text += " " + params[itr];
                }
            } catch (Exception ex2) {
                PacketSendUtility.sendMessage(player, MuiService.getInstance().getMessage("WEDDING_TEXT_NULL"));
                return;
            }
            Wedding wed = player.getWedding();
            wed.setText(text);
            DAOManager.getDAO(WeddingDAO.class).update(wed);
            PacketSendUtility.sendMessage(player, MuiService.getInstance().getMessage("WEDDING_TEXT_SUCCESS") + text + "");
        } else if (command.equals("hide") && player.isMarried()) {
            WeddingService.getInstance().hideWedding(player);
            /*
            player.setShowMarried(!player.isShowMarried());
        	player.sendPacket(new SM_PLAYER_INFO(player, false));
        	player.getController().stopProtectionActiveTask();
        	player.getKnownList().clear();
        	player.updateKnownlist();
        	*/
        } else {
            PacketSendUtility.sendMessage(player, "Command //wedding < marry | unmarry | party | info | text | hide >");
        }
    }
}
