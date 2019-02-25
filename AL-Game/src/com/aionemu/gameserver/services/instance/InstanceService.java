package com.aionemu.gameserver.services.instance;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.configs.main.AutoGroupConfig;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.main.MembershipConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.instance.InstanceEngine;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.alliance.PlayerAlliance;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.model.team2.league.League;
import com.aionemu.gameserver.model.templates.world.WorldMapTemplate;
import com.aionemu.gameserver.network.aion.SystemMessageId;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.AutoGroupService;
import com.aionemu.gameserver.services.HousingService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.spawnengine.StaticDoorSpawnManager;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldMap;
import com.aionemu.gameserver.world.WorldMap2DInstance;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.WorldMapInstanceFactory;
import com.aionemu.gameserver.world.WorldMapType;
import com.aionemu.gameserver.world.zone.ZoneInstance;

import javolution.util.FastList;

public class InstanceService
{
	private static final Logger log = LoggerFactory.getLogger(InstanceService.class);
	private static final FastList<Integer> instanceAggro = new FastList<Integer>();
	private static final FastList<Integer> instanceCoolDownFilter = new FastList<Integer>();
	private static final int SOLO_INSTANCES_DESTROY_DELAY = 5 * 60 * 1000;
	
	public static void load() {
		for (String s : CustomConfig.INSTANCES_MOB_AGGRO.split(",")) {
			instanceAggro.add(Integer.parseInt(s));
		} for (String s : CustomConfig.INSTANCES_COOL_DOWN_FILTER.split(",")) {
			instanceCoolDownFilter.add(Integer.parseInt(s));
		}
	}
	
	public synchronized static WorldMapInstance getNextAvailableInstance(int worldId, boolean noSpawn) {
        return getNextAvailableInstance(worldId, 0, noSpawn);
    }

    public synchronized static WorldMapInstance getNextAvailableInstance(int worldId, int ownerId, boolean nospawn) {
        WorldMap map = World.getInstance().getWorldMap(worldId);

        if (!map.isInstanceType()) {
            throw new UnsupportedOperationException("Invalid call for next available instance  of " + worldId);
        }

        int nextInstanceId = map.getNextInstanceId();
        log.info("Creating new instance for new player:" + worldId + " id:" + nextInstanceId + " owner:" + ownerId);
        WorldMapInstance worldMapInstance = WorldMapInstanceFactory.createWorldMapInstance(map, nextInstanceId, ownerId);

        map.addInstance(nextInstanceId, worldMapInstance);
        // finally start the checker
        if (map.isInstanceType()) {
            startInstanceChecker(worldMapInstance);
        }

        return worldMapInstance;
    }
	
	public synchronized static WorldMapInstance getNextAvailableInstance(int worldId, int ownerId) {
		WorldMap map = World.getInstance().getWorldMap(worldId);
		if (!map.isInstanceType()) {
			throw new UnsupportedOperationException("Invalid call for next available instance  of " + worldId);
		}
		int nextInstanceId = map.getNextInstanceId();
		log.info("<Instance In Progress>" + worldId + " id:" + nextInstanceId + " owner:" + ownerId);
		WorldMapInstance worldMapInstance = WorldMapInstanceFactory.createWorldMapInstance(map, nextInstanceId, ownerId);
		map.addInstance(nextInstanceId, worldMapInstance);
		SpawnEngine.spawnInstance(worldId, worldMapInstance.getInstanceId(), (byte) 0, ownerId);
		InstanceEngine.getInstance().onInstanceCreate(worldMapInstance);
		if (map.isInstanceType()) {
			startInstanceChecker(worldMapInstance);
		}
		return worldMapInstance;
	}
	
	public synchronized static WorldMapInstance getNextAvailableInstance(int worldId) {
		return getNextAvailableInstance(worldId, 0);
	}
	
	public static void destroyInstance(WorldMapInstance instance) {
		if (instance.getEmptyInstanceTask() != null) {
			instance.getEmptyInstanceTask().cancel(false);
		}
		int worldId = instance.getMapId();
		WorldMap map = World.getInstance().getWorldMap(worldId);
		if (!map.isInstanceType()) {
			return;
		}
		int instanceId = instance.getInstanceId();
		map.removeWorldMapInstance(instanceId);
		log.info("<Instance Destroy>" + worldId + " " + instanceId);
		Iterator<VisibleObject> it = instance.objectIterator();
		while (it.hasNext()) {
			VisibleObject obj = it.next();
			if (obj instanceof Player) {
				Player player = (Player) obj;
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(SystemMessageId.LEAVE_INSTANCE_NOT_PARTY));
				moveToExitPoint((Player) obj);
			} else {
				obj.getController().onDelete();
			}
		}
		instance.getInstanceHandler().onInstanceDestroy();
		if (instance instanceof WorldMap2DInstance) {
			WorldMap2DInstance w2d = (WorldMap2DInstance) instance;
			if (w2d.isPersonal()) {
				HousingService.getInstance().onInstanceDestroy(w2d.getOwnerId());
			}
		}
	}
	
	public static void registerPlayerWithInstance(WorldMapInstance instance, Player player) {
		Integer obj = player.getObjectId();
		instance.register(obj);
		instance.setSoloPlayerObj(obj);
	}
	
	public static void registerGroupWithInstance(WorldMapInstance instance, PlayerGroup group) {
		instance.registerGroup(group);
	}
	
	public static void registerAllianceWithInstance(WorldMapInstance instance, PlayerAlliance group) {
		instance.registerGroup(group);
	}
	
	public static void registerLeagueWithInstance(WorldMapInstance instance, League group) {
		instance.registerGroup(group);
	}
	
	public static WorldMapInstance getRegisteredInstance(int worldId, int objectId) {
		Iterator<WorldMapInstance> iterator = World.getInstance().getWorldMap(worldId).iterator();
		while (iterator.hasNext()) {
			WorldMapInstance instance = iterator.next();
			if (instance.isRegistered(objectId)) {
				return instance;
			}
		}
		return null;
	}
	
	public static WorldMapInstance getPersonalInstance(int worldId, int ownerId) {
		if (ownerId == 0) {
			return null;
		}
		Iterator<WorldMapInstance> iterator = World.getInstance().getWorldMap(worldId).iterator();
		while (iterator.hasNext()) {
			WorldMapInstance instance = iterator.next();
			if (instance.isPersonal() && instance.getOwnerId() == ownerId) {
				return instance;
			}
		}
		return null;
	}
	
	public static WorldMapInstance getBeginnerInstance(int worldId, int registeredId) {
        WorldMapInstance instance = getRegisteredInstance(worldId, registeredId);
        if (instance == null) {
            return null;
        }
        return instance.isBeginnerInstance() ? instance : null;
    }
	
	private static int getLastRegisteredId(Player player) {
        int lookupId;
        boolean isPersonal = WorldMapType.getWorld(player.getWorldId()).isPersonal();
        if (player.isInGroup2()) {
            lookupId = player.getPlayerGroup2().getTeamId();
        } else if (player.isInAlliance2()) {
            lookupId = player.getPlayerAlliance2().getTeamId();
            if (player.isInLeague()) {
                lookupId = player.getPlayerAlliance2().getLeague().getObjectId();
            }
        } else if (isPersonal && player.getCommonData().getWorldOwnerId() != 0) {
            lookupId = player.getCommonData().getWorldOwnerId();
        } else {
            lookupId = player.getObjectId();
        }
        return lookupId;
    }
	
	public static void onPlayerLogin(Player player) {
        int worldId = player.getWorldId();
        int lookupId = getLastRegisteredId(player);

        WorldMapInstance beginnerInstance = getBeginnerInstance(worldId, lookupId);
        if (beginnerInstance != null) {
            // set to correct twin instanceId, not to #1
            World.getInstance().setPosition(player, worldId, beginnerInstance.getInstanceId(), player.getX(), player.getY(), player.getZ(), player.getHeading());
        }

        WorldMapTemplate worldTemplate = DataManager.WORLD_MAPS_DATA.getTemplate(worldId);
        if (worldTemplate.isInstance()) {
            boolean isPersonal = WorldMapType.getWorld(player.getWorldId()).isPersonal();
            WorldMapInstance registeredInstance = isPersonal ? getPersonalInstance(worldId, lookupId) : getRegisteredInstance(worldId, lookupId);

            if (isPersonal) {
                if (registeredInstance == null) {
                    registeredInstance = getNextAvailableInstance(player.getWorldId(), lookupId);
                }

                if (!registeredInstance.isRegistered(player.getObjectId())) {
                    registerPlayerWithInstance(registeredInstance, player);
                }
            }

            if (registeredInstance != null) {
                World.getInstance().setPosition(player, worldId, registeredInstance.getInstanceId(), player.getX(), player.getY(), player.getZ(), player.getHeading());
                player.getPosition().getWorldMapInstance().getInstanceHandler().onPlayerLogin(player);
                return;
            }

            moveToExitPoint(player);
        }
    }
	
	public static void moveToExitPoint(Player player) {
		TeleportService2.moveToInstanceExit(player, player.getWorldId(), player.getRace());
	}
	
	public static boolean isInstanceExist(int worldId, int instanceId) {
		return World.getInstance().getWorldMap(worldId).getWorldMapInstanceById(instanceId) != null;
	}
	
	private static void startInstanceChecker(WorldMapInstance worldMapInstance) {
		int delay = 150000;
		int period = 60000;
		worldMapInstance.setEmptyInstanceTask(ThreadPoolManager.getInstance().scheduleAtFixedRate(new EmptyInstanceCheckerTask(worldMapInstance), delay, period));
	}
	
	private static class EmptyInstanceCheckerTask implements Runnable {

        private WorldMapInstance worldMapInstance;
        private long soloInstanceDestroyTime;

        private EmptyInstanceCheckerTask(WorldMapInstance worldMapInstance) {
            this.worldMapInstance = worldMapInstance;
            this.soloInstanceDestroyTime = System.currentTimeMillis() + SOLO_INSTANCES_DESTROY_DELAY;
        }

        private boolean canDestroySoloInstance() {
            return System.currentTimeMillis() > this.soloInstanceDestroyTime;
        }

        private void updateSoloInstanceDestroyTime() {
            this.soloInstanceDestroyTime = System.currentTimeMillis() + SOLO_INSTANCES_DESTROY_DELAY;
        }

        @Override
        public void run() {
            int instanceId = worldMapInstance.getInstanceId();
            int worldId = worldMapInstance.getMapId();
            WorldMap map = World.getInstance().getWorldMap(worldId);
            PlayerGroup registeredGroup = worldMapInstance.getRegisteredGroup();
            if (registeredGroup == null) {
                if (worldMapInstance.playersCount() > 0) {
                    updateSoloInstanceDestroyTime();
                    return;
                }
                if (worldMapInstance.playersCount() == 0) {
                    if (canDestroySoloInstance()) {
                        map.removeWorldMapInstance(instanceId);
                        destroyInstance(worldMapInstance);
                        return;
                    } else {
                        return;
                    }
                }
                Iterator<Player> playerIterator = worldMapInstance.playerIterator();
                int mapId = worldMapInstance.getMapId();
                while (playerIterator.hasNext()) {
                    Player player = playerIterator.next();
                    if (player.isOnline() && player.getWorldId() == mapId) {
                        return;
                    }
                }
                map.removeWorldMapInstance(instanceId);
                destroyInstance(worldMapInstance);
            } else if (registeredGroup.size() == 0) {
                map.removeWorldMapInstance(instanceId);
                destroyInstance(worldMapInstance);
            }
        }
    }
	
	public static void onLogOut(Player player) {
		player.getPosition().getWorldMapInstance().getInstanceHandler().onPlayerLogOut(player);
	}
	
	public static void onEnterInstance(Player player) {
		player.getController().updateNearbyQuests();
		player.getPosition().getWorldMapInstance().getInstanceHandler().onEnterInstance(player);
		AutoGroupService.getInstance().onEnterInstance(player);
		for (Item item : player.getInventory().getItems()) {
			if (item.getItemTemplate().getOwnershipWorld() == 0) {
				continue;
			} if (item.getItemTemplate().getOwnershipWorld() != player.getWorldId()) {
				player.getInventory().decreaseByObjectId(item.getObjectId(), item.getItemCount());
			}
		}
	}
	
	public static void onLeaveInstance(Player player) {
		player.getPosition().getWorldMapInstance().getInstanceHandler().onLeaveInstance(player);
		for (Item item : player.getInventory().getItems()) {
			if (item.getItemTemplate().getOwnershipWorld() == player.getWorldId()) {
				player.getInventory().decreaseByObjectId(item.getObjectId(), item.getItemCount());
			}
		} if (AutoGroupConfig.AUTO_GROUP_ENABLED) {
			AutoGroupService.getInstance().onLeaveInstance(player);
		}
	}
	
	public static void onEnterZone(Player player, ZoneInstance zone) {
		player.getPosition().getWorldMapInstance().getInstanceHandler().onEnterZone(player, zone);
	}
	
	public static void onLeaveZone(Player player, ZoneInstance zone) {
		player.getPosition().getWorldMapInstance().getInstanceHandler().onLeaveZone(player, zone);
	}
	
	public static boolean isAggro(int mapId) {
		return instanceAggro.contains(mapId);
	}
	
	public static int getInstanceRate(Player player, int mapId) {
		int instanceCooldownRate = player.havePermission(MembershipConfig.INSTANCES_COOLDOWN) && !instanceCoolDownFilter.contains(mapId) ? CustomConfig.INSTANCES_RATE : 1;
		if (instanceCoolDownFilter.contains(mapId)) {
			instanceCooldownRate = 1;
		}
		return instanceCooldownRate;
	}
	
	public synchronized static WorldMapInstance getNextBgInstance(int worldId) {
		WorldMap map = World.getInstance().getWorldMap(worldId);
		int nextInstanceId = map.getNextInstanceId();
		WorldMapInstance worldMapInstance = WorldMapInstanceFactory.createWorldMapInstance(map, nextInstanceId);
		map.addInstance(nextInstanceId, worldMapInstance);
		StaticDoorSpawnManager.spawnTemplate(worldId, worldMapInstance.getInstanceId());
		if (map.isInstanceType()) {
			startInstanceChecker(worldMapInstance);
		}
		return worldMapInstance;
	}
}