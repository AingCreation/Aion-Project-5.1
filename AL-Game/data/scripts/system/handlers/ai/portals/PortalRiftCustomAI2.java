package ai.portals;

import ai.ActionItemNpcAI2;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.configs.main.PvPConfig;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TeleportAnimation;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.dreamergames.DreamersCustomRift;
import com.aionemu.gameserver.services.teleport.TeleportService2;


@AIName("portal_rift_custom")
public class PortalRiftCustomAI2 extends ActionItemNpcAI2 {

    @Override
    protected void handleDialogStart(Player player) {
        handleUseItemFinish(player);
    }

    @Override
    protected void handleUseItemFinish(Player player) {
        int WorldId = 0;
        float xa = 0.0f;
        float ya = 0.0f;
        float za = 0.0f;
        byte ha = 0;

        float xe = 0.0f;
        float ye = 0.0f;
        float ze = 0.0f;
        byte he = 0;

        WorldId = PvPConfig.DEFAULT_CUSTOM_PORTAL_WORLDID;
        String coorA[] = PvPConfig.DEFAULT_CUSTOM_PORTAL_SPAWN_ASMO.split(",");
        String coorE[] = PvPConfig.DEFAULT_CUSTOM_PORTAL_SPAWN_ELY.split(",");

        xa = Float.parseFloat(coorA[0]);
        ya = Float.parseFloat(coorA[1]);
        za = Float.parseFloat(coorA[2]);
        ha = Byte.parseByte(coorA[3]);

        xe = Float.parseFloat(coorE[0]);
        ye = Float.parseFloat(coorE[1]);
        ze = Float.parseFloat(coorE[2]);
        he = Byte.parseByte(coorE[3]);

        if (WorldId != 0) {
            if (player.getRace() == Race.ELYOS) {
                TeleportService2.teleportTo(player, WorldId, xe, ye, ze, he, TeleportAnimation.BEAM_ANIMATION);
            } else if (player.getRace() == Race.ASMODIANS) {
                TeleportService2.teleportTo(player, WorldId, xa, ya, za, ha, TeleportAnimation.BEAM_ANIMATION);
            }
        } else {
            player.sendMessage("Portal was closed");
        }
    }
}
