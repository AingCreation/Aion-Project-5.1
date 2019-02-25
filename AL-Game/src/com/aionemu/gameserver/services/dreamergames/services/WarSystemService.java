package com.aionemu.gameserver.services.dreamergames.services;

import java.util.concurrent.Future;

import javolution.util.FastMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.services.CronService;
import com.aionemu.gameserver.configs.main.WarConfig;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.ingameshop.InGameShopEn;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_SPAWN;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldMapType;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 * @author Idhacker542
 */
public class WarSystemService {
	private static final Logger log = LoggerFactory.getLogger("WAR-SYSTEM_LOG");
	private static final String WAR_SYSTEM_SCHEDULE = WarConfig.WAR_SYSTEM_SCHEDULE;
	private FastMap<Integer, VisibleObject> wars = new FastMap<Integer, VisibleObject>();
	private Future<?> timeStop;
	private Future<?> timeStop2;
	
	public void InitSystemWar() {
		if (!WarConfig.WAR_ENABLE) {
			log.info("[WarService] War System are disabled...");
		} else {
			log.info("[WarService] War System Are Actived...");
			startWar();
		}
	}
	
	public void startWar() {
		CronService.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				spawnBos();
				spawnRift();
				announceWar();
				log.info("[WarService] War System Started...");
				World.getInstance().doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						PacketSendUtility.sendBrightYellowMessageOnCenter(player, "[War System] War Has Begun, Rift Spawn in Sanctum and Pandaemonium. 2 Hours From Now !?");
					}		
				});
			}	
		}, WAR_SYSTEM_SCHEDULE, true);
	}
	
	public void spawnBos() {
		despawnTimer();
		wars.put(260008, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(300140000, 260008, 431.218F, 665.984F, 185.54448F, (byte)22), 1));//megadux
		wars.put(260003, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(300140000, 260003, 621.00665F, 662.7661F, 185.54807F, (byte)38), 1));//stallari
	}
	
	public void spawnRift() {
		despawnTimer();
		// Rift Sanctum
		wars.put(700458, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(110010000, 700458, 1550.5149F, 1511.5837F, 565.9252F, (byte)60), 1));
		wars.put(209355, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(110010000, 209355, 1548.824F, 1514.0428F, 565.92206F, (byte)86), 1));
		wars.put(209355, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(110010000, 209355, 1546.6298F, 1513.7242F, 565.9167F, (byte)92), 1));
		wars.put(209355, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(110010000, 209355, 1548.9551F, 1509.427F, 565.92267F, (byte)30), 1));
		wars.put(209355, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(110010000, 209355, 1546.5607F, 1510.0308F, 565.9166F, (byte)29), 1));
		wars.put(209355, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(110010000, 209355, 1544.5829F, 1510.4191F, 565.9117F, (byte)30), 1));
		wars.put(209355, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(110010000, 209355, 1544.3601F, 1513.3458F, 565.91113F, (byte)89), 1));
		// Rift Pandaemonium
		wars.put(700534, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(120010000, 700534, 1275.6188F, 1343.7814F, 204.42003F, (byte)89), 1));
		wars.put(209355, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(120010000, 209355, 1272.4188F, 1341.3414F, 204.41998F, (byte)119), 1));
		wars.put(209355, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(120010000, 209355, 1279.0991F, 1341.4058F, 204.42003F, (byte)60), 1));
		wars.put(209355, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(120010000, 209355, 1279.1167F, 1338.2874F, 204.42003F, (byte)58), 1));
		wars.put(209355, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(120010000, 209355, 1272.5153F, 1338.2782F, 204.41998F, (byte)0), 1));
		wars.put(209355, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(120010000, 209355, 1272.517F, 1335.2249F, 204.41998F, (byte)117), 1));
		wars.put(209355, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(120010000, 209355, 1279.1007F, 1335.3522F, 204.42003F, (byte)59), 1));
	}
	
	public void announceWar() {
		timeStop = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				announceWar2();
				log.info("[WarService] War System Announce : 10 Minute Left");
				World.getInstance().doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						PacketSendUtility.sendBrightYellowMessageOnCenter(player, "[War System] War System Announce : 10 Minute Left");
					}	
				});
			}	
		}, WarConfig.WAR_ANNOUNCE * 60 * 1000);
	}
	
	public void announceWar2() {
		timeStop2 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				log.info("[WarService] War System Announce : 5 Minute Left");
				World.getInstance().doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						PacketSendUtility.sendBrightYellowMessageOnCenter(player, "[War System] War System Announce : 5 Minute Left");
					}					
				});
			}		
		}, 300000);
	}
	
	private void despawnTimer() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				endAnnounce();
				despawnNpcs();
			}			
		}, WarConfig.WAR_RUNTIME * 3600 * 1000);
	}
	
	private void compliteWar() {
		//endAnnounce();
		//despawnNpcs();
	}
	
	public void endAnnounce() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				log.info("[WarService] War System Announce : War Finish.");
				PacketSendUtility.sendBrightYellowMessageOnCenter(player, "[War System] War System Announce : War Ended... Thankyou for Participation.");
			}			
		});
	}
	
	//TODO
	public void kickPlayer() {
		for (Player player : World.getInstance().getAllPlayers()) {
			if (player.getRace() == Race.ELYOS && player.getWorldId() == 300140000) {
				player.setKisk(null);
				TeleportService2.teleportToNpc(player, 203752);
				PacketSendUtility.sendPacket(player, new SM_PLAYER_SPAWN(player));
			} else if (player.getRace() == Race.ASMODIANS && player.getWorldId() == 300140000) {
				player.setKisk(null);
				TeleportService2.teleportToNpc(player, 204075);
				PacketSendUtility.sendPacket(player, new SM_PLAYER_SPAWN(player));
			}
		}
	}
	
	public void despawnNpcs() {
		for (VisibleObject vo : wars.values()) {
			if(vo != null) {
				Npc npc = (Npc) vo;
				if (!npc.getLifeStats().isAlreadyDead()) {
					npc.getController().onDelete();
				}
			}
		}
		wars.clear();
		timeStop.cancel(true);
		timeStop2.cancel(true);
		//delete spawn sanctum & pandae
        deleteSpawn(700458,110010000);
        deleteSpawn(700534,120010000);
        deleteSpawn(209355,120010000);
        deleteSpawn(209355,110010000);
        //kickPlayer();  TODO after colldown time end war <player willbe get kick auto>
	}
	
	public void deleteSpawn(final int id,final int worldId) {
        World.getInstance().doOnAllObjects(new Visitor<VisibleObject>() {
        	@Override
        	public void visit(VisibleObject object) {
        		if (object.getWorldId() != worldId) {
        			return;
        		}
        		if (object instanceof Npc) {
        			Npc npc = (Npc) object;
        			if(npc.getNpcId() == id) {
        				object.getController().delete();
        			}
        		}
        	}
        });        
    }
	
	public void doRewardMegadux() {
        World.getInstance().getWorldMap(WorldMapType.GELKMAROS.getId()).getMainWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(Player player) {
                if (player.getRace() == Race.ASMODIANS) {
                    int qt = 100;
                    AbyssPointsService.addGp(player, WarConfig.WAR_GP_REWARD);
					ItemService.addItem(player, WarConfig.WAR_ITEM_REWARD, WarConfig.WAR_ITEM_AMOUNT);
					ItemService.addItem(player, WarConfig.WAR_ITEM_REWARD_1, WarConfig.WAR_ITEM_AMOUNT_1);
					ItemService.addItem(player, WarConfig.WAR_ITEM_REWARD_2, WarConfig.WAR_ITEM_AMOUNT_2);
					ItemService.addItem(player, WarConfig.WAR_ITEM_REWARD_3, WarConfig.WAR_ITEM_AMOUNT_3);
                    InGameShopEn.getInstance().addToll(player, qt);
                    PacketSendUtility.sendBrightYellowMessage(player, "You obtained " + qt + " Web Point.");
					} else {
                    int qt = 50;
                    AbyssPointsService.addGp(player, WarConfig.WAR_GP_REWARD);
					ItemService.addItem(player, WarConfig.WAR_ITEM_REWARD, WarConfig.WAR_ITEM_AMOUNT_LOSS);
					ItemService.addItem(player, WarConfig.WAR_ITEM_REWARD_1, WarConfig.WAR_ITEM_AMOUNT_1_LOSS);
					ItemService.addItem(player, WarConfig.WAR_ITEM_REWARD_2, WarConfig.WAR_ITEM_AMOUNT_2_LOSS);
					ItemService.addItem(player, WarConfig.WAR_ITEM_REWARD_3, WarConfig.WAR_ITEM_AMOUNT_3_LOSS);
                    InGameShopEn.getInstance().addToll(player, qt);
                    PacketSendUtility.sendBrightYellowMessage(player, "You obtained " + qt + " Web Point.");
                }
            }
        });
        compliteWar();
    }
    public void doRewardStallari() {
        World.getInstance().getWorldMap(WorldMapType.GELKMAROS.getId()).getMainWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(Player player) {
                if (player.getRace() == Race.ELYOS) {
                    int qt = 100;
                    AbyssPointsService.addGp(player, WarConfig.WAR_GP_REWARD);
					ItemService.addItem(player, WarConfig.WAR_ITEM_REWARD, WarConfig.WAR_ITEM_AMOUNT);
					ItemService.addItem(player, WarConfig.WAR_ITEM_REWARD_1, WarConfig.WAR_ITEM_AMOUNT_1);
					ItemService.addItem(player, WarConfig.WAR_ITEM_REWARD_2, WarConfig.WAR_ITEM_AMOUNT_2);
					ItemService.addItem(player, WarConfig.WAR_ITEM_REWARD_3, WarConfig.WAR_ITEM_AMOUNT_3);
                    InGameShopEn.getInstance().addToll(player, qt);
                    PacketSendUtility.sendBrightYellowMessage(player, "You obtained " + qt + " Web Point.");
					} else {
                    int qt = 50;
                    AbyssPointsService.addGp(player, WarConfig.WAR_GP_REWARD);
					ItemService.addItem(player, WarConfig.WAR_ITEM_REWARD, WarConfig.WAR_ITEM_AMOUNT_LOSS);
					ItemService.addItem(player, WarConfig.WAR_ITEM_REWARD_1, WarConfig.WAR_ITEM_AMOUNT_1_LOSS);
					ItemService.addItem(player, WarConfig.WAR_ITEM_REWARD_2, WarConfig.WAR_ITEM_AMOUNT_2_LOSS);
					ItemService.addItem(player, WarConfig.WAR_ITEM_REWARD_3, WarConfig.WAR_ITEM_AMOUNT_3_LOSS);
                    InGameShopEn.getInstance().addToll(player, qt);
                    PacketSendUtility.sendBrightYellowMessage(player, "You obtained " + qt + " Web Point.");
                }
            }
        });
        compliteWar();
    }
	
	public static WarSystemService getInstance() {
		return WarSystemServiceHolder.INSTANCE;	
	}
	
	private static class WarSystemServiceHolder {
		private static final WarSystemService INSTANCE = new WarSystemService();
	}

}
