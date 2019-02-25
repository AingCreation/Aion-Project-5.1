package ai;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TeleportAnimation;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.teleport.TeleportService2;

/**
 * @author ione542
 */
@AIName("tele_portal_dreamer")
public class TeleDreamerAI2 extends ActionItemNpcAI2 {

    @Override
    protected void handleDialogStart(Player player) {
        handleUseItemFinish(player);
    }

    @Override
    protected void handleUseItemFinish(Player player) {
        if (player.getRace() == Race.ELYOS) {
            TeleportService2.teleportTo(player, 210100000, 1414.0f, 1281.0f, 336.43802f, (byte) 68, TeleportAnimation.BEAM_ANIMATION);
        } else {
            TeleportService2.teleportTo(player, 220110000, 1807.0f, 1986.0f, 197.80386f, (byte) 68, TeleportAnimation.BEAM_ANIMATION);
        }
    }
}
