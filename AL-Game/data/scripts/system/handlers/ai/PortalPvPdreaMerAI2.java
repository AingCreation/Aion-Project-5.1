package ai;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TeleportAnimation;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.teleport.TeleportService2;

/**
 * @author ione542
 */
@AIName("dreamer_aion_pvp_portal1")
public class PortalPvPdreaMerAI2 extends ActionItemNpcAI2 {

    @Override
    protected void handleDialogStart(Player player) {
        handleUseItemFinish(player);
    }

    @Override
    protected void handleUseItemFinish(Player player) {
        if (player.getRace() == Race.ELYOS) {
            TeleportService2.teleportTo(player, 600100000, 951.2105f, 684.0816f, 284.86478f, (byte) 37, TeleportAnimation.BEAM_ANIMATION);
        } else {
            TeleportService2.teleportTo(player, 600100000, 1074.3862f, 1495.2627f, 273.14166f, (byte) 78, TeleportAnimation.BEAM_ANIMATION);
        }
    }
}
