/*
#
# This file is part of aion-lightning <aion-lightning.org>.
#
# aion-lightning is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# aion-lightning is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with aion-lightning.  If not, see <http://www.gnu.org/licenses/>.
#
 */


package com.aionemu.gameserver.services.thedevils;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.services.CronService;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.configs.main.EventsConfig;
import com.aionemu.gameserver.controllers.observer.ActionObserver;
import com.aionemu.gameserver.controllers.observer.ObserverType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Hckd05
 */
public class EventGebukMantan {
	private static final Logger log = LoggerFactory.getLogger(EventGebukMantan.class);
	private static List<float[]> floatArray = new ArrayList<float[]>();
	private static final String MANTAN_EVENT_SCHEDULE = EventsConfig.MANTAN_EVENT_SCHEDULE;
	private static int WORLD_ID = 600100000;
	private static int NPC_ID = 831573;
	private static int[] rewards = {
188055499, 188055498, 188055497, 166030005, 186000253, 164002167, 166050169, 166050170};
        private static Npc mainN;

        public static void ScheduleCron(){
             CronService.getInstance().schedule(new Runnable(){

                  @Override
                  public void run() {
                       startEvent(); //To change body of generated methods, choose Tools | Templates.
                  }

             },MANTAN_EVENT_SCHEDULE);
             log.info("Gebuk Mantan Event start to:" + EventsConfig.MANTAN_EVENT_SCHEDULE + " duration 30 min");
        }

        public static void startEvent(){
                initCoordinates();

                World.getInstance().doOnAllPlayers(new Visitor<Player>(){

                        @Override
                        public void visit(Player object) {
                                PacketSendUtility.sendYellowMessageOnCenter(object, "Event, Gebuk mantan di mulai location [pos:Levinshor;0 600100000 859.4 1066.3 0.0 0] cepat gebuk simantan untuk mengambil hadiah!");
                        }
                });

                initPig();

                ThreadPoolManager.getInstance().schedule(new Runnable(){

                     @Override
                     public void run() {
                          endEvent(); //To change body of generated methods, choose Tools | Templates.
                     }
                }, 30 * 60 * 1000);

        }

        private static void initPig() {
                float[] coords = floatArray.get(Rnd.get(floatArray.size()));
                SpawnTemplate spawn = SpawnEngine.addNewSingleTimeSpawn(WORLD_ID, NPC_ID, coords[0], coords[1], coords[2], (byte) coords[3]);
                VisibleObject mainObject = SpawnEngine.spawnObject(spawn, 1);
                if(mainObject instanceof Npc) {
                      mainN = (Npc) mainObject;
                }
                ActionObserver observer = new ActionObserver(ObserverType.ATTACKED){

                        @Override
                        public void attacked(Creature creature) {
                                if(creature instanceof Player) {
                                        final Player player = (Player) creature;
                                        final int id = rewards[Rnd.get(rewards.length)];
                                        ItemService.addItem(player, id, EventsConfig.MANTAN_EVENT_COUNT_REWARD);
                                        World.getInstance().doOnAllPlayers(new Visitor<Player>(){

                                                @Override
                                                public void visit(Player object) {
                                                        PacketSendUtility.sendYellowMessageOnCenter(object, player.getName()  + "Mantan di gebuk dan mendapatkan [item:%d]");
                                                }
                                        });
                                }
                                mainN.getObserveController().removeObserver(this);
                                //mainN.setSpawn(null);
                                mainN.getController().onDelete();
                                initPig();
                        }
                };
                if(mainN != null) {
                        mainN.getObserveController().attach(observer);
                }
        }

        public static void endEvent(){
                World.getInstance().doOnAllPlayers(new Visitor<Player>(){

                        @Override
                        public void visit(Player object) {
                                PacketSendUtility.sendYellowMessageOnCenter(object, "Event Gebuk mantan telah selesai terimakasih atas partisipasinya!");
                        }
                });

                mainN.getController().onDelete();
        }

        private static void initCoordinates(){
				floatArray.add(new float[] { 875.3577f, 1089.8434f, 332.7829f, 16f } ); //sudah
				floatArray.add(new float[] { 917.708f, 1156.8873f, 330.25f, 103f } ); //sudah
				floatArray.add(new float[] { 987.03375f, 1191.5072f, 325.16364f, 85f } ); //sudah
				floatArray.add(new float[] { 1040.025f, 1141.0299f, 320.1791f, 70f } ); //sudah
				floatArray.add(new float[] { 1085.9094f, 1099.6996f, 330.00745f, 65f } ); //sudah
				floatArray.add(new float[] { 1074.1266f, 1013.3673f, 336.8528f, 56f } ); //sudah
				floatArray.add(new float[] { 1054.117f, 919.644f, 336.88928f, 54f } ); //sudah
				floatArray.add(new float[] { 1014.0818f, 900.4131f, 323.58768f, 41f } ); //sudah
				floatArray.add(new float[] { 941.393f, 901.51196f, 323.36313f, 37f } ); //sudah
				// Event gebuk mantan untuk seseorang yang susah moveon
				floatArray.add(new float[] { 902.55316f, 930.18933f, 328.35226f, 33f } ); //sudah
				floatArray.add(new float[] { 819.7018f, 960.6209f, 336.19098f, 118f } ); //sudah
				floatArray.add(new float[] { 829.7636f, 1033.1862f, 330.69913f, 119f } ); //sudah
				floatArray.add(new float[] { 809.5449f, 1080.5912f, 338.7728f, 114f } ); //sudah
				floatArray.add(new float[] { 894.47107f, 1045.9132f, 332.3143f, 55f } ); //sudah
				floatArray.add(new float[] { 1020.66864f, 1067.5288f, 331.77188f, 24f } ); //sudah
				/*floatArray.add(new float[] { 1516.5026f, 1407.1633f, 201.70367f, 0f } );
				floatArray.add(new float[] { 1493.8002f, 1460.6648f, 176.9295f, 0f } );
				floatArray.add(new float[] { 1275.4224f, 1352.994f, 204.4608f, 0f } );
				floatArray.add(new float[] { 1312.3104f, 1234.2994f, 214.66357f, 0f } );
				floatArray.add(new float[] { 1235.8165f, 1170.0706f, 215.13603f, 0f } );
				floatArray.add(new float[] { 1075.684f, 1065.3511f, 201.52081f, 0f } );
				floatArray.add(new float[] { 992.7121f, 1044.0405f, 201.52101f, 0f } );
				floatArray.add(new float[] { 942.4062f, 1131.7365f, 206.84773f, 0f } );
				floatArray.add(new float[] { 956.87994f, 1188.8582f, 201.4773f, 0f } );//
				floatArray.add(new float[] { 1226.273f, 1306.5815f, 208.125f, 0f } );
				floatArray.add(new float[] { 1016.5605f, 1518.2861f, 220.50787f, 0f } );*/
        }
}
