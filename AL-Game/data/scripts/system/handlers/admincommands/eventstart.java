package admincommands;

import com.aionemu.gameserver.services.dreamergames.services.warEvent;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.controllers.NpcController;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_SPAWN;
import com.aionemu.gameserver.world.knownlist.Visitor;
import java.util.Iterator;

/**
 * Goto command
 *
 * @author Ione542
 */
public class eventstart extends AdminCommand {

    public eventstart() {
        super("war");
    }

    @Override
    public void execute(Player admin, String... params) {
        String cmd = params[0];
        if (cmd.equals("start")) {
        	if (NpcController.war_started){
        		return;
        	}
        		NpcController.war_started = true;
            //spawn dux/stalari
            warEvent.warStatus = 1;
            warEvent.asmoWin = 0;
            warEvent.elyWin = 0;
            float xBoss = (float) 431.218;
            float yBoss = (float) 665.984;
            float zBoss = (float) 185.54448;
            byte hBoss = 22;
            SpawnTemplate spawn;
            VisibleObject visibleObject;

            spawn = SpawnEngine.addNewSpawn(300140000, 260008, xBoss, yBoss, zBoss, hBoss, 0);
            visibleObject = SpawnEngine.spawnObject(spawn, 1);

            spawn = SpawnEngine.addNewSpawn(300140000, 260003, (float) 621.00665, (float) 662.7661, (float) 185.54807, (byte) 38, 0);
            visibleObject = SpawnEngine.spawnObject(spawn, 1);

            //rift pandaemonium 700534
            spawn = SpawnEngine.addNewSpawn(120010000, 209355, (float)1272.4188, (float)1341.3414, (float)204.41998,(byte)119, 0);
            visibleObject = SpawnEngine.spawnObject(spawn, 1);
            //npc 1
            spawn = SpawnEngine.addNewSpawn(120010000, 209355, (float)1279.0991, (float)1341.4058, (float)204.42003,(byte)60, 0);
            visibleObject = SpawnEngine.spawnObject(spawn, 1);
            
            spawn = SpawnEngine.addNewSpawn(120010000,209355 , (float)1279.1167, (float)1338.2874, (float)204.42003,(byte)58, 0);
            visibleObject = SpawnEngine.spawnObject(spawn, 1);
            
            spawn = SpawnEngine.addNewSpawn(120010000, 209355, (float)1272.5153, (float)1338.2782, (float)204.41998,(byte)0, 0);
            visibleObject = SpawnEngine.spawnObject(spawn, 1);
            
            spawn = SpawnEngine.addNewSpawn(120010000, 209355, (float)1272.517, (float)1335.2249, (float)204.41998,(byte)117, 0);
            visibleObject = SpawnEngine.spawnObject(spawn, 1);
            
            spawn = SpawnEngine.addNewSpawn(120010000, 209355, (float)1279.1007, (float)1335.3522, (float)204.42003,(byte)59, 0);
            visibleObject = SpawnEngine.spawnObject(spawn, 1);
            
            spawn = SpawnEngine.addNewSpawn(120010000, 700534, (float)1275.6188, (float)1343.7814, (float)204.42003,(byte)89, 0);
            visibleObject = SpawnEngine.spawnObject(spawn, 1);
            
            //rift sanctum 700458
            spawn = SpawnEngine.addNewSpawn(110010000, 700458, (float)1550.5149, (float)1511.5837, (float)565.9252,(byte)60, 0);
            visibleObject = SpawnEngine.spawnObject(spawn, 1);
            //npc 1
            spawn = SpawnEngine.addNewSpawn(110010000,209355 , (float)1548.824, (float)1514.0428, (float)565.92206,(byte)86, 0);
            visibleObject = SpawnEngine.spawnObject(spawn, 1);
            
            spawn = SpawnEngine.addNewSpawn(110010000, 209355, (float)1546.6298, (float)1513.7242, (float)565.9167,(byte)92, 0);
            visibleObject = SpawnEngine.spawnObject(spawn, 1);
            
            spawn = SpawnEngine.addNewSpawn(110010000, 209355, (float)1548.9551, (float)1509.427, (float)565.92267,(byte)30, 0);
            visibleObject = SpawnEngine.spawnObject(spawn, 1);
            
            spawn = SpawnEngine.addNewSpawn(110010000, 209355, (float)1546.5607, (float)1510.0308, (float)565.9166,(byte)29, 0);
            visibleObject = SpawnEngine.spawnObject(spawn, 1);
            
            spawn = SpawnEngine.addNewSpawn(110010000, 209355, (float)1544.5829, (float)1510.4191, (float)565.9117,(byte)30, 0);
            visibleObject = SpawnEngine.spawnObject(spawn, 1);
            
            spawn = SpawnEngine.addNewSpawn(110010000, 209355, (float)1544.3601, (float)1513.3458, (float)565.91113,(byte)89, 0);
            visibleObject = SpawnEngine.spawnObject(spawn, 1);
       
            Iterator<Player> iter = World.getInstance().getPlayersIterator();
            while (iter.hasNext()) {
                PacketSendUtility.sendBrightYellowMessageOnCenter(iter.next(), " WAR EVENT STARTED, PORTAL SPAWN AT SANCTUM AND PANDAEMONIUM"); 
            }
        }
        else 
        {
        	if (!NpcController.war_started){
        		return;
        	}
        	NpcController.war_started = false;
            warEvent.warStatus = 0;
            warEvent.asmoWin = 1;
            warEvent.elyWin = 1;
           PacketSendUtility.sendMessage(admin, "WAR EVENT ENDS");
           //delet spawn sanctum & pandae 700534 700458
           deleteSpawn(700458,110010000);
           deleteSpawn(700534,120010000);
           //delete npc spawn
           deleteSpawn(209355,120010000);
           deleteSpawn(209355,110010000);
           
           //simple despawn method
           int[] mobs = {260008,260003,209052,209252,218553,700559,218732};
           for(int x : mobs ){
          	 deleteSpawn(x,300140000);
           }
           
           //kick players from map
   				for (Player p : World.getInstance().getAllPlayers())
  				{
  					if ((p.getRace() == Race.ELYOS) && (p.getWorldId() == 300140000)) {
  						p.setKisk(null);
  						TeleportService2.teleportToNpc(p, 203752);
  						PacketSendUtility.sendPacket(p, new SM_PLAYER_SPAWN(p));
  					}
  					else if ((p.getRace() == Race.ASMODIANS) && (p.getWorldId() == 300140000)) {
  						p.setKisk(null);
  						TeleportService2.teleportToNpc(p, 204075);
  						PacketSendUtility.sendPacket(p, new SM_PLAYER_SPAWN(p));
  					}
  				}
        }
    }
    
    public void deleteSpawn(final int id,final int worldId)
    {
        World.getInstance().doOnAllObjects(
        new Visitor<VisibleObject>() {
				@Override
				public void visit(VisibleObject object) {
					if (object.getWorldId() != worldId) {
						return;
					}
					if (object instanceof Npc) {
						Npc npc = (Npc) object;
						if(npc.getNpcId() == id)
						{
						  object.getController().delete();
						}
					}
				}
		});

    }
    
		@Override
		public void onFail(Player player, String message) {
			PacketSendUtility.sendMessage(player, "syntax //war <start|stop>");
		}
}