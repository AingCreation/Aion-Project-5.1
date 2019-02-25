package ai.npcSupport;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.utils.PacketSendUtility;


@AIName("ngehealbayar")
public class HealingKinahAI2 extends NpcAI2 {
    @Override
    protected void handleDialogStart(final Player player) {
        RequestResponseHandler responseHandler = new RequestResponseHandler(player) {
            @Override
            public void acceptRequest(Creature p2, Player p) {
                if (p.getInventory().tryDecreaseKinah(500000)) {
                    p.getLifeStats().setCurrentHpPercent(100);
                    p.getLifeStats().setCurrentMpPercent(100);
                    p.getLifeStats().sendHpPacketUpdate();
                    p.getLifeStats().sendMpPacketUpdate();
                    PacketSendUtility.sendYellowMessageOnCenter(p, "Your life stat has been restored!");
                    PacketSendUtility.sendPacket(p, new SM_ITEM_USAGE_ANIMATION(p.getObjectId(), 0, 164000136, 0, 1, 0));
                } else {
                    PacketSendUtility.sendYellowMessageOnCenter(p, "You dont have enough kinah!");
                }

            }

            @Override
            public void denyRequest(Creature p2, Player p) {
            }
        };
        boolean requested = player.getResponseRequester().putRequest(902247, responseHandler);
        if (requested) {
            PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(902247, 0, 0, "Your life stat will be restored and need 500.000 Kinah. Do you want to continue?"));
        }
    }

}
