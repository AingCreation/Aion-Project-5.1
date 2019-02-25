package ai;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TeleportAnimation;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.teleport.TeleportService2;

/**
 * @author ione542
 */
@AIName("tele_portal_fallen")
public class TeleFallenAI2 extends ActionItemNpcAI2 {

    @Override
    protected void handleDialogStart(Player player) {
        handleUseItemFinish(player);
    }

    @Override
    protected void handleUseItemFinish(Player player) {
        if (player.getRace() == Race.ELYOS) {
            TeleportService2.teleportTo(player, 210040000, 1452.8607f, 1349.7633f, 174.8038f, (byte) 85, TeleportAnimation.BEAM_ANIMATION);
        } else {
            TeleportService2.teleportTo(player, 210040000, 661.76654f, 2331.3862f, 314.45264f, (byte) 89, TeleportAnimation.BEAM_ANIMATION);
        }
    }
}
