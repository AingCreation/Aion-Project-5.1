package ai;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TeleportAnimation;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.teleport.TeleportService2;

/**
 * @author ione542
 */
@AIName("farm_portal_dreamer")
public class FarmDreamerAI2 extends ActionItemNpcAI2 {

    @Override
    protected void handleDialogStart(Player player) {
        handleUseItemFinish(player);
    }

    @Override
    protected void handleUseItemFinish(Player player) {
        if (player.getRace() == Race.ELYOS) {
            TeleportService2.teleportTo(player, 600010000, 537.5796f, 404.86356f, 324.51672f, (byte) 113, TeleportAnimation.BEAM_ANIMATION);
        } else {
            TeleportService2.teleportTo(player, 600010000, 491.35138f, 1164.3466f, 329.86426f, (byte) 73, TeleportAnimation.BEAM_ANIMATION);
        }
    }
}
