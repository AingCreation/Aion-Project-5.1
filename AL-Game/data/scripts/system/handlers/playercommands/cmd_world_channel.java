package playercommands;

import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.HashMap;

/**
 * @author Ione542
 */
public class cmd_world_channel extends PlayerCommand {
    public HashMap<Integer, Long> cooldowns = new HashMap<Integer, Long>();

    public cmd_world_channel() {
        super("world");
    }

    @Override
    public void execute(Player player, String... params) {
        int i = 1;
        boolean check = true;
        int cooldownTime = 15; // The number of seconds the player has to wait

        String adminTag = "";

        if (params.length < 1) {
            PacketSendUtility.sendMessage(player, "syntax : .world <message>");
            return;
        }

        if (!player.getInventory().decreaseByItemId(188910002, 1)) {
            PacketSendUtility.sendMessage(player, "You need [item:188910002] to use world chat channel.");
            return;
        }

        adminTag += player.getName() + " : ";

        StringBuilder sbMessage;
        if (player.isGM()) {
            sbMessage = new StringBuilder("\uE058 " + "[color:Staff~;0.5 1 0]" + " " + "[color:" + player.getName() + ";1 0 0.5]" + " \uE027 : ");
        } else if (player.getRace() == Race.ASMODIANS) {
            sbMessage = new StringBuilder("\uE058 " + "[color:Asmodian~;0.5 1 0]" + " " + "[color:" + player.getName() + ";1 0.4 0]" + " \uE027 : ");
        } else {
            sbMessage = new StringBuilder("\uE058 " + "[color:Elysean~;0.5 1 0]" + " " + "[color:" + player.getName() + ";1 1 1]" + " \uE027 : ");
        }


        for (String s : params) {
            if (i++ != 0 && (check)) {
                sbMessage.append(s).append(" ");
            }
        }

        String message = sbMessage.toString().trim();
        int messageLenght = message.length();

        final String sMessage = message.substring(0, CustomConfig.MAX_CHAT_TEXT_LENGHT > messageLenght ? messageLenght : CustomConfig.MAX_CHAT_TEXT_LENGHT);

        if (cooldowns.containsKey(player.getObjectId())) {
            long secondsLeft = ((cooldowns.get(player.getObjectId()) / 1000) + cooldownTime) - (System.currentTimeMillis() / 1000);
            if (secondsLeft > 0) {
                player.sendMessage("You cant do this chat for another " + secondsLeft + " seconds!");
                return;
            }
            World.getInstance().doOnAllPlayers(new Visitor<Player>() {
                @Override
                public void visit(Player player) {
                    PacketSendUtility.sendMessage(player, sMessage);
                }
            });
            cooldowns.put(player.getObjectId(), System.currentTimeMillis());
        } else {
            World.getInstance().doOnAllPlayers(new Visitor<Player>() {
                @Override
                public void visit(Player player) {
                    PacketSendUtility.sendMessage(player, sMessage);
                }
            });
            cooldowns.put(player.getObjectId(), System.currentTimeMillis());
        }

    }

    @Override
    public void onFail(Player player, String message) {
        PacketSendUtility.sendMessage(player, "syntax : .world <message>");
    }
}
