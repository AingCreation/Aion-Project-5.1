package playercommands;

import com.aionemu.gameserver.configs.administration.CommandsConfig;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldMap;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.WorldMapType;

/**
 * Goto command
 * 
 * @author Dwarfpicker
 * @rework Imaginary
 */
public class cmd_farm extends PlayerCommand{

	public cmd_farm() {
		super("farm");
	}

	@Override
	public void execute(Player player, String... params) {
		if (params == null || params.length < 1) {
			PacketSendUtility.sendMessage(player, "syntax: .farm <location>");
			return;
		}
		
		StringBuilder sbDestination = new StringBuilder();
		for(String p : params)
			sbDestination.append(p + " ");
		
		String destination = sbDestination.toString().trim();
		
		// Eltnen
		if (player.getRace() == Race.ELYOS && (player.getAccessLevel() > 2 || player.getWorldId() == 110010000)){
			if (destination.equalsIgnoreCase("silentera"))
				goTo(player, 600010000, 535.11774f, 397.09406f, 324.51672f);
			else
				PacketSendUtility.sendMessage(player, "Wrong location!");
		}
		else if (player.getRace() == Race.ASMODIANS && (player.getAccessLevel() > 2 || player.getWorldId() == 120010000))
		{
			if (destination.equalsIgnoreCase("silentera"))
				goTo(player, 600010000, 494.3849f, 1169.771f, 329.86426f);
			else
				PacketSendUtility.sendMessage(player, "Wrong location!");
		}
		else
		{
			if (player.getRace() == Race.ELYOS){
				PacketSendUtility.sendMessage(player, "This command only usable in Sanctum");
				return;
			}else{
				PacketSendUtility.sendMessage(player, "This command only usable in Pandaemonium");
				return;
			}
		}
	}
	
	private static void goTo(final Player player, int worldId, float x, float y, float z) {
		WorldMap destinationMap = World.getInstance().getWorldMap(worldId);
		if (destinationMap.isInstanceType())
			TeleportService2.teleportTo(player, worldId, getInstanceId(worldId, player), x, y, z);
		else
			TeleportService2.teleportTo(player, worldId, x, y, z);
	}
	
	private static int getInstanceId(int worldId, Player player) {
		if (player.getWorldId() == worldId)	{
			WorldMapInstance registeredInstance = InstanceService.getRegisteredInstance(worldId, player.getObjectId());
			if (registeredInstance != null)
				return registeredInstance.getInstanceId();
		}
		WorldMapInstance newInstance = InstanceService.getNextAvailableInstance(worldId);
		InstanceService.registerPlayerWithInstance(newInstance, player);
		return newInstance.getInstanceId();
	}
	
	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "Syntax : .farm <location>");
	}
}