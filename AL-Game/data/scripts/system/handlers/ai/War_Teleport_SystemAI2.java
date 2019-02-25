package ai;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TeleportAnimation;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.teleport.TeleportService2;

/**
 * @author ione542
 */
@AIName("teleportwarsystem")
public class War_Teleport_SystemAI2 extends ActionItemNpcAI2 {

    @Override
    protected void handleDialogStart(Player player) {
        handleUseItemFinish(player);
    }

    @Override
    protected void handleUseItemFinish(Player player) {
        if (player.getRace() == Race.ELYOS) {
            TeleportService2.teleportTo(player, 220070000, 2333.7795f, 1948.7344f, 446.71222f, (byte) 57, TeleportAnimation.BEAM_ANIMATION);
        } else {
            TeleportService2.teleportTo(player, 220070000, 1417.7607f, 1843.8981f, 382.49213f, (byte) 29, TeleportAnimation.BEAM_ANIMATION);
        }
    }
}
