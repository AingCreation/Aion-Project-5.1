/*
 * =====================================================================================*
 * This file is part of Archsoft (Archsoft Home Software Development)                   *
 * Aion - Archsoft Development is closed Aion Project that use Old Aion Project Base    *
 * Like Aion-Unique, Aion-Lightning, Aion-Engine, Aion-Core, Aion-Extreme,              *
 * Aion-NextGen, Aion-Ger, U3J, Encom And other Aion project, All Credit Content        *
 * That they make is belong to them/Copyright is belong to them. And All new Content    *
 * that Archsoft make the copyright is belong to Archsoft.                              *
 * You may have agreement with Archsoft Development, before use this Engine/Source      *
 * You have agree with all of Term of Services agreement with Archsoft Development      *
 * =====================================================================================*
 */

package com.aionemu.gameserver.services;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.configs.main.NewbieGuide;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.instance.InstanceType;
import com.aionemu.gameserver.model.items.ItemId;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MESSAGE;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.spawnengine.StaticDoorSpawnManager;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldMap;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.WorldMapInstanceFactory;

import java.util.concurrent.Future;

/**
 *
 */
public class NewbieGuideService {

    private int npcObjtId;
    private String npcName;
    private Player player;
    private Npc npc;
    private Npc npc2;
    private Npc npc3;
    private Npc npc4;
    private Npc npc5;
    private Npc npc6;
    private Npc npc7;

    private Future<?> task1;
    private Future<?> task2;
    private Future<?> task3;
    private Future<?> task4;
    private Future<?> task5;
    private Future<?> task6;
    private Future<?> task7;
    private Future<?> task8;

    private static void portToNewbieGuide(final Player player, int worldId, float x, float y, float z, byte h) {
        TeleportService2.teleportTo(player, worldId, player.getObjectId(), x, y, z, h);
    }

    private synchronized static WorldMapInstance createInstance(Player player) {
        WorldMap map = null;
        if (player.getRace() == Race.ELYOS) {
            map = World.getInstance().getWorldMap(NewbieGuide.NEWBIE_GUIDE_ELYOS_SPAWN_WORLD);
        }else{
            map = World.getInstance().getWorldMap(NewbieGuide.NEWBIE_GUIDE_ASMOS_SPAWN_WORLD);
        }

        WorldMapInstance worldMapInstance = WorldMapInstanceFactory.createWorldMapInstance(map, player.getObjectId(), 0, InstanceType.NEWBIE_GUIDE);
        map.addInstance(player.getObjectId(), worldMapInstance);
        StaticDoorSpawnManager.spawnTemplate(worldMapInstance.getMapId(), worldMapInstance.getInstanceId());
        return worldMapInstance;
    }

    public static boolean isInNewbieGuide(Player player) {
        return player.getPosition().getWorldMapInstance().isInNewbieGuide();
    }

    public static NewbieGuideService getInstance() {
        return NewbieGuideService.SingletonHolder.instance;
    }

    public void onPlayerEnterWorld(Player player) {
        createInstance(player);
        player.getLifeStats().setCurrentFp(player.getLifeStats().getMaxFp());
        if (player.getRace() == Race.ELYOS) {
            portToNewbieGuide(player, NewbieGuide.NEWBIE_GUIDE_ELYOS_SPAWN_WORLD, NewbieGuide.NEWBIE_GUIDE_ELYOS_SPAWN_X, NewbieGuide.NEWBIE_GUIDE_ELYOS_SPAWN_Y, NewbieGuide.NEWBIE_GUIDE_ELYOS_SPAWN_Z, NewbieGuide.NEWBIE_GUIDE_ELYOS_SPAWN_H);
        } else {
            portToNewbieGuide(player, NewbieGuide.NEWBIE_GUIDE_ASMOS_SPAWN_WORLD, NewbieGuide.NEWBIE_GUIDE_ASMOS_SPAWN_X, NewbieGuide.NEWBIE_GUIDE_ASMOS_SPAWN_Y, NewbieGuide.NEWBIE_GUIDE_ASMOS_SPAWN_Z, NewbieGuide.NEWBIE_GUIDE_ASMOS_SPAWN_H);
        }
    }

    public void initData(final Player player, int state) {
        if (player.getRace() == Race.ELYOS) {
            if (player.getWorldId() != NewbieGuide.NEWBIE_GUIDE_ELYOS_SPAWN_WORLD) {
                return;
            }
        } else {
            if (player.getWorldId() != NewbieGuide.NEWBIE_GUIDE_ASMOS_SPAWN_WORLD) {
                return;
            }
        }


        this.player = player;
        int AgentNpc;
        if (player.getRace() == Race.ELYOS) {
            AgentNpc = NewbieGuide.NEWBIE_GUIDE_ELYOS_AGENT;
        } else {
            AgentNpc = NewbieGuide.NEWBIE_GUIDE_ASMOS_AGENT;
        }
        VisibleObject visibleObject;
        VisibleObject visibleObject2;
        VisibleObject visibleObject3;
        VisibleObject visibleObject4;
        VisibleObject visibleObject5;
        VisibleObject visibleObject6;
        VisibleObject visibleObject7;
        if (player.getRace() == Race.ELYOS) {
            SpawnTemplate spawn = SpawnEngine.addNewSpawn(NewbieGuide.NEWBIE_GUIDE_ELYOS_SPAWN_WORLD, AgentNpc, NewbieGuide.NEWBIE_GUIDE_ELYOS_AGENT_SPAWN_X, NewbieGuide.NEWBIE_GUIDE_ELYOS_AGENT_SPAWN_Y, NewbieGuide.NEWBIE_GUIDE_ELYOS_AGENT_SPAWN_Z, NewbieGuide.NEWBIE_GUIDE_ELYOS_AGENT_SPAWN_H, 0);
            visibleObject = SpawnEngine.spawnObject(spawn, player.getInstanceId());

            SpawnTemplate spawn2 = SpawnEngine.addNewSpawn(NewbieGuide.NEWBIE_GUIDE_ELYOS_SPAWN_WORLD, NewbieGuide.NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT, NewbieGuide.NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT_SPAWN_X, NewbieGuide.NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT_SPAWN_Y, NewbieGuide.NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT_SPAWN_Z, NewbieGuide.NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT_SPAWN_H, 0);
            visibleObject2 = SpawnEngine.spawnObject(spawn2, player.getInstanceId());

            SpawnTemplate spawn3 = SpawnEngine.addNewSpawn(NewbieGuide.NEWBIE_GUIDE_ELYOS_SPAWN_WORLD, NewbieGuide.NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT2, NewbieGuide.NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT2_SPAWN_X, NewbieGuide.NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT2_SPAWN_Y, NewbieGuide.NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT2_SPAWN_Z, NewbieGuide.NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT2_SPAWN_H, 0);
            visibleObject3 = SpawnEngine.spawnObject(spawn3, player.getInstanceId());

            SpawnTemplate spawn4 = SpawnEngine.addNewSpawn(NewbieGuide.NEWBIE_GUIDE_ELYOS_SPAWN_WORLD, NewbieGuide.NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT3, NewbieGuide.NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT3_SPAWN_X, NewbieGuide.NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT3_SPAWN_Y, NewbieGuide.NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT3_SPAWN_Z, NewbieGuide.NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT3_SPAWN_H, 0);
            visibleObject4 = SpawnEngine.spawnObject(spawn4, player.getInstanceId());

            SpawnTemplate spawn5 = SpawnEngine.addNewSpawn(NewbieGuide.NEWBIE_GUIDE_ELYOS_SPAWN_WORLD, NewbieGuide.NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT4, NewbieGuide.NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT4_SPAWN_X, NewbieGuide.NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT4_SPAWN_Y, NewbieGuide.NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT4_SPAWN_Z, NewbieGuide.NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT4_SPAWN_H, 0);
            visibleObject5 = SpawnEngine.spawnObject(spawn5, player.getInstanceId());

            SpawnTemplate spawn6 = SpawnEngine.addNewSpawn(NewbieGuide.NEWBIE_GUIDE_ELYOS_SPAWN_WORLD, NewbieGuide.NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT5, NewbieGuide.NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT5_SPAWN_X, NewbieGuide.NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT5_SPAWN_Y, NewbieGuide.NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT5_SPAWN_Z, NewbieGuide.NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT5_SPAWN_H, 0);
            visibleObject6 = SpawnEngine.spawnObject(spawn6, player.getInstanceId());

            SpawnTemplate spawn7 = SpawnEngine.addNewSpawn(NewbieGuide.NEWBIE_GUIDE_ELYOS_SPAWN_WORLD, NewbieGuide.NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT6, NewbieGuide.NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT6_SPAWN_X, NewbieGuide.NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT6_SPAWN_Y, NewbieGuide.NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT6_SPAWN_Z, NewbieGuide.NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT6_SPAWN_H, 0);
            visibleObject7 = SpawnEngine.spawnObject(spawn7, player.getInstanceId());
        } else {
            SpawnTemplate spawn = SpawnEngine.addNewSpawn(NewbieGuide.NEWBIE_GUIDE_ELYOS_SPAWN_WORLD, AgentNpc, NewbieGuide.NEWBIE_GUIDE_ASMOS_AGENT_SPAWN_X, NewbieGuide.NEWBIE_GUIDE_ASMOS_AGENT_SPAWN_Y, NewbieGuide.NEWBIE_GUIDE_ASMOS_AGENT_SPAWN_Z, NewbieGuide.NEWBIE_GUIDE_ASMOS_AGENT_SPAWN_H, 0);
            visibleObject = SpawnEngine.spawnObject(spawn, player.getInstanceId());

            SpawnTemplate spawn2 = SpawnEngine.addNewSpawn(NewbieGuide.NEWBIE_GUIDE_ASMOS_SPAWN_WORLD, NewbieGuide.NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT, NewbieGuide.NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT_SPAWN_X, NewbieGuide.NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT_SPAWN_Y, NewbieGuide.NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT_SPAWN_Z, NewbieGuide.NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT_SPAWN_H, 0);
            visibleObject2 = SpawnEngine.spawnObject(spawn2, player.getInstanceId());

            SpawnTemplate spawn3 = SpawnEngine.addNewSpawn(NewbieGuide.NEWBIE_GUIDE_ASMOS_SPAWN_WORLD, NewbieGuide.NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT2, NewbieGuide.NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT2_SPAWN_X, NewbieGuide.NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT2_SPAWN_Y, NewbieGuide.NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT2_SPAWN_Z, NewbieGuide.NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT2_SPAWN_H, 0);
            visibleObject3 = SpawnEngine.spawnObject(spawn3, player.getInstanceId());

            SpawnTemplate spawn4 = SpawnEngine.addNewSpawn(NewbieGuide.NEWBIE_GUIDE_ASMOS_SPAWN_WORLD, NewbieGuide.NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT3, NewbieGuide.NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT3_SPAWN_X, NewbieGuide.NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT3_SPAWN_Y, NewbieGuide.NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT3_SPAWN_Z, NewbieGuide.NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT3_SPAWN_H, 0);
            visibleObject4 = SpawnEngine.spawnObject(spawn4, player.getInstanceId());

            SpawnTemplate spawn5 = SpawnEngine.addNewSpawn(NewbieGuide.NEWBIE_GUIDE_ASMOS_SPAWN_WORLD, NewbieGuide.NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT4, NewbieGuide.NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT4_SPAWN_X, NewbieGuide.NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT4_SPAWN_Y, NewbieGuide.NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT4_SPAWN_Z, NewbieGuide.NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT4_SPAWN_H, 0);
            visibleObject5 = SpawnEngine.spawnObject(spawn5, player.getInstanceId());

            SpawnTemplate spawn6 = SpawnEngine.addNewSpawn(NewbieGuide.NEWBIE_GUIDE_ASMOS_SPAWN_WORLD, NewbieGuide.NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT5, NewbieGuide.NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT5_SPAWN_X, NewbieGuide.NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT5_SPAWN_Y, NewbieGuide.NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT5_SPAWN_Z, NewbieGuide.NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT5_SPAWN_H, 0);
            visibleObject6 = SpawnEngine.spawnObject(spawn6, player.getInstanceId());

            SpawnTemplate spawn7 = SpawnEngine.addNewSpawn(NewbieGuide.NEWBIE_GUIDE_ASMOS_SPAWN_WORLD, NewbieGuide.NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT6, NewbieGuide.NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT6_SPAWN_X, NewbieGuide.NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT6_SPAWN_Y, NewbieGuide.NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT6_SPAWN_Z, NewbieGuide.NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT6_SPAWN_H, 0);
            visibleObject7 = SpawnEngine.spawnObject(spawn7, player.getInstanceId());
        }

        this.npc = (Npc) visibleObject;
        this.npc2 = (Npc) visibleObject2;
        this.npc3 = (Npc) visibleObject3;
        this.npc4 = (Npc) visibleObject4;
        this.npc5 = (Npc) visibleObject5;
        this.npc6 = (Npc) visibleObject6;
        this.npc7 = (Npc) visibleObject7;

        this.npcObjtId = npc.getObjectId();
        this.npcName = npc.getName();


        ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                player.getEffectController().setAbnormal(AbnormalState.ROOT.getId());
                player.getEffectController().updatePlayerEffectIconsImpl();
            }
        }, 3000);


        if (state == 1) {
            sendShout();
        } else if (state == 2) {
            sendShoutChooseClass();
        } else if (state == 3) {
            sendShoutAfterChooseClass();
        } else if (state == 4) {
            sendShoutAfterOpenStigma();
        }

    }

    public void sendShout() {
        if (npc.getNpcId() == NewbieGuide.NEWBIE_GUIDE_ELYOS_AGENT || npc.getNpcId() == NewbieGuide.NEWBIE_GUIDE_ASMOS_AGENT) {
            ThreadPoolManager.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    PacketSendUtility.sendPacket(player, new SM_MESSAGE(npcObjtId, npcName, MuiService.getInstance().getMessage("NEWBIE_GUIDE_WELCOME", player.getName(), GSConfig.SERVER_NAME), ChatType.ALLIANCE));
                    sendShoutChooseClass();
                    return;
                }
            }, 7000);
        }
    }

    public void sendShoutChooseClass() {
        ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                PacketSendUtility.sendPacket(player, new SM_MESSAGE(npcObjtId, npcName, MuiService.getInstance().getMessage("NEWBIE_GUIDE_CHOOSE_CLASS"), ChatType.ALLIANCE));
                ThreadPoolManager.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        if (player.getInventory().getItemCountByItemId(addStigmaBundleId()) <= 0) {
                            addStigmaBundle();
                        }
                        ClassChangeService.showClassChangeDialog(player);
                        player.getCommonData().setinNewbieGuide(2);
                        DAOManager.getDAO(PlayerDAO.class).updateNewbieGuide(player.getObjectId(), 2);
                        return;
                    }
                }, 3000);

            }
        }, 5000);
    }

    public void sendShoutAfterChooseClass() {
        ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if (player.getInventory().getItemCountByItemId(addStigmaBundleId()) <= 0) {
                    addStigmaBundle();
                }
                PacketSendUtility.sendPacket(player, new SM_MESSAGE(npcObjtId, npcName, MuiService.getInstance().getMessage("NEWBIE_GUIDE_OPEN_STIGMA_BUNDLE", addStigmaBundleId()), ChatType.ALLIANCE));
                player.getCommonData().setinNewbieGuide(3);
                DAOManager.getDAO(PlayerDAO.class).updateNewbieGuide(player.getObjectId(), 3);
                player.getCommonData().setLevel(NewbieGuide.NEWBIE_GUIDE_SET_LEVEL );
                return;
            }
        }, 5000);

    }

    public void sendShoutAfterOpenStigma() {
        task1 = ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                PacketSendUtility.sendPacket(player, new SM_MESSAGE(npcObjtId, npcName, MuiService.getInstance().getMessage("NEWBIE_GUIDE_MESSAGES1"), ChatType.ALLIANCE));
                task1.cancel(true);
                task2 = ThreadPoolManager.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        PacketSendUtility.sendPacket(player, new SM_MESSAGE(npcObjtId, npcName, MuiService.getInstance().getMessage("NEWBIE_GUIDE_MESSAGES2"), ChatType.ALLIANCE));
                        task2.cancel(true);
                        task3 = ThreadPoolManager.getInstance().schedule(new Runnable() {
                            @Override
                            public void run() {
                                PacketSendUtility.sendPacket(player, new SM_MESSAGE(npcObjtId, npcName, MuiService.getInstance().getMessage("NEWBIE_GUIDE_MESSAGES3"), ChatType.ALLIANCE));
                                task3.cancel(true);
                                task4 = ThreadPoolManager.getInstance().schedule(new Runnable() {
                                    @Override
                                    public void run() {
                                        PacketSendUtility.sendPacket(player, new SM_MESSAGE(npcObjtId, npcName, MuiService.getInstance().getMessage("NEWBIE_GUIDE_MESSAGES4"), ChatType.ALLIANCE));
                                        task4.cancel(true);
                                        task5 = ThreadPoolManager.getInstance().schedule(new Runnable() {
                                            @Override
                                            public void run() {
                                                PacketSendUtility.sendPacket(player, new SM_MESSAGE(npcObjtId, npcName, MuiService.getInstance().getMessage("NEWBIE_GUIDE_MESSAGES5"), ChatType.ALLIANCE));
                                                task5.cancel(true);
                                                task6 = ThreadPoolManager.getInstance().schedule(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        PacketSendUtility.sendPacket(player, new SM_MESSAGE(npcObjtId, npcName, MuiService.getInstance().getMessage("NEWBIE_GUIDE_MESSAGES6"), ChatType.ALLIANCE));
                                                        task6.cancel(true);
                                                        task7 = ThreadPoolManager.getInstance().schedule(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                if (player != null && player.isOnline()) {
                                                                    npc.getController().delete();
                                                                    npc2.getController().delete();
                                                                    npc3.getController().delete();
                                                                    npc4.getController().delete();
                                                                    npc5.getController().delete();
                                                                    npc6.getController().delete();
                                                                    npc7.getController().delete();
                                                                    player.getEffectController().unsetAbnormal(AbnormalState.ROOT.getId());
                                                                    TeleportService2.teleportToCapital2(player);
                                                                    task7.cancel(true);
                                                                    task8 = ThreadPoolManager.getInstance().schedule(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            player.getCommonData().setinNewbieGuide(0);
                                                                            DAOManager.getDAO(PlayerDAO.class).updateNewbieGuide(player.getObjectId(), 0);
                                                                            addStarterPack();
                                                                            ClassChangeService.completeQuest(player);
                                                                            SkillLearnService.addMissingSkills(player);
                                                                            //QuestEngine.getInstance().onLvlUp(new QuestEnv(null, player, 0, 0));
                                                                            //player.getController().updateNearbyQuests();

                                                                            task8.cancel(true);
                                                                        }
                                                                    }, 5000);
                                                                }
                                                            }
                                                        }, 3000);
                                                    }
                                                }, 3000);
                                            }
                                        }, 7000);
                                    }
                                }, 5000);
                            }
                        }, 4000);
                    }
                }, 4000);
            }
        }, 1000);
    }

    private int addStigmaBundleId() {
        switch (player.getPlayerClass()) {
            case GLADIATOR:
                return 188053914;
            case GUNSLINGER:
                return 188053914;
            case AETHERTECH:
                return 188053914;
            case SONGWEAVER:
                return 188053914;
            case TEMPLAR:
                return 188053914;
            case ASSASSIN:
                return 188053914;
            case RANGER:
                return 188053914;
            case SORCERER:
                return 188053914;
            case SPIRIT_MASTER:
                return 188053914;
            case CLERIC:
                return 188053914;
            case CHANTER:
                return 188053914;
            default:
                return 188053914;
        }
    }

    private void addStigmaBundle() {
        switch (player.getPlayerClass()) {
            case GLADIATOR:
                ItemService.addItem(player, 188053914, 20, "Daeva Guide");
                break;
            case GUNSLINGER:
                ItemService.addItem(player, 188053914, 20, "Daeva Guide");
                break;
            case AETHERTECH:
                ItemService.addItem(player, 188053914, 20, "Daeva Guide");
                break;
            case SONGWEAVER:
                ItemService.addItem(player, 188053914, 20, "Daeva Guide");
                break;
            case TEMPLAR:
                ItemService.addItem(player, 188053914, 20, "Daeva Guide");
                break;
            case ASSASSIN:
                ItemService.addItem(player, 188053914, 20, "Daeva Guide");
                break;
            case RANGER:
                ItemService.addItem(player, 188053914, 20, "Daeva Guide");
                break;
            case SORCERER:
                ItemService.addItem(player, 188053914, 20, "Daeva Guide");
                break;
            case SPIRIT_MASTER:
                ItemService.addItem(player, 188053914, 20, "Daeva Guide");
                break;
            case CLERIC:
                ItemService.addItem(player, 188053914, 20, "Daeva Guide");
                break;
            case CHANTER:
                ItemService.addItem(player, 188053914, 20, "Daeva Guide");
                break;
            default:
                break;
        }
    }

    private void addStarterPack() {
        switch (player.getPlayerClass()) {
            case GLADIATOR:
                for (String itemids : NewbieGuide.NEWBIE_GUIDE_GLADIATOR.split(",")) {
                    ItemService.addItem(player, Integer.parseInt(itemids), 1, "Daeva Guide");
                }
                break;
            case TEMPLAR:
                for (String itemids : NewbieGuide.NEWBIE_GUIDE_TEMPLAR.split(",")) {
                    ItemService.addItem(player, Integer.parseInt(itemids), 1, "Daeva Guide");
                }
                break;
            case ASSASSIN:
                for (String itemids : NewbieGuide.NEWBIE_GUIDE_ASSASSIN.split(",")) {
                    ItemService.addItem(player, Integer.parseInt(itemids), 1, "Daeva Guide");
                }
                break;
            case RANGER:
                for (String itemids : NewbieGuide.NEWBIE_GUIDE_RANGER.split(",")) {
                    ItemService.addItem(player, Integer.parseInt(itemids), 1, "Daeva Guide");
                }
                break;
            case GUNSLINGER:
                for (String itemids : NewbieGuide.NEWBIE_GUIDE_GUNNER.split(",")) {
                    ItemService.addItem(player, Integer.parseInt(itemids), 1, "Daeva Guide");
                }
                break;
            case AETHERTECH:
                for (String itemids : NewbieGuide.NEWBIE_GUIDE_RIDER.split(",")) {
                    ItemService.addItem(player, Integer.parseInt(itemids), 1, "Daeva Guide");
                }
                break;
            case SONGWEAVER:
                for (String itemids : NewbieGuide.NEWBIE_GUIDE_BARD.split(",")) {
                    ItemService.addItem(player, Integer.parseInt(itemids), 1, "Daeva Guide");
                }
                break;
            case SORCERER:
                for (String itemids : NewbieGuide.NEWBIE_GUIDE_SORCERER.split(",")) {
                    ItemService.addItem(player, Integer.parseInt(itemids), 1, "Daeva Guide");
                }
                break;
            case SPIRIT_MASTER:
                for (String itemids : NewbieGuide.NEWBIE_GUIDE_SPIRITMASTER.split(",")) {
                    ItemService.addItem(player, Integer.parseInt(itemids), 1, "Daeva Guide");
                }
                break;
            case CLERIC:
                for (String itemids : NewbieGuide.NEWBIE_GUIDE_CLERIC.split(",")) {
                    ItemService.addItem(player, Integer.parseInt(itemids), 1, "Daeva Guide");
                }
                break;
            case CHANTER:
                for (String itemids : NewbieGuide.NEWBIE_GUIDE_CHANTER.split(",")) {
                    ItemService.addItem(player, Integer.parseInt(itemids), 1, "Daeva Guide");
                }
                break;
            default:
                break;
        }
        ItemService.addItem(player, ItemId.KINAH.value(), NewbieGuide.NEWBIE_GUIDE_KINAH);
        for (String otherItems : NewbieGuide.NEWBIE_GUIDE_OTHER_ITEM.split(",")) {
            String[] parts = otherItems.split(":");
            int itemid = Integer.parseInt(parts[0]);
            int itemCount = Integer.parseInt(parts[1]);
            ItemService.addItem(player, itemid, itemCount, "Daeva Guide");
        }
    }

    @SuppressWarnings("synthetic-access")
    private static class SingletonHolder {

        protected static final NewbieGuideService instance = new NewbieGuideService();
    }
}