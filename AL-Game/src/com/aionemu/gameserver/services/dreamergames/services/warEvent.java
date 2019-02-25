package com.aionemu.gameserver.services.dreamergames.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.aionemu.gameserver.controllers.CreatureController;
import com.aionemu.gameserver.controllers.NpcController;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_SPAWN;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * War DreamerGames Aion System
 *
 * @author Ione542
 */
public class warEvent extends CreatureController<Npc> {
	private static final Logger log = LoggerFactory.getLogger(NpcController.class);

	public static int warStatus;
	public static int asmoWin;
	public static int elyWin;

	public void set_asmo_win(int d)
	{
		asmoWin = d;
	}

	public void set_ely_win(int d) {
		elyWin = d;
	}

	public int get_ely_win() {
		return asmoWin;
	}

	public int get_asmo_win() {
		return elyWin;
	}

	public void set_war_status(int a) {
		warStatus = a;
	}

	public int get_war_status() {
		return warStatus;
	}
	
	public void doWar(int npcId, int worldIdx){
	//int npcId = npcIdxy;
	//int worldIdx = worldIdxy;
	try {
		if ((npcId == 260003) && (worldIdx == 300140000))
		{
			float xBoss = 636.78638F;
			float yBoss = 432.30298F;
			float zBoss = 196.79149F;
			byte hBoss = 92;
			SpawnTemplate spawn = SpawnEngine.addNewSpawn(300140000, 209052, xBoss, yBoss, zBoss, hBoss, 0);
			VisibleObject visibleObject = SpawnEngine.spawnObject(spawn, 1);

			Iterator iter = World.getInstance().getPlayersIterator();
			while (iter.hasNext()) {
				PacketSendUtility.sendBrightYellowMessageOnCenter((Player)iter.next(), "MEGADUX HAS BEEN KILLED BY ASMODIANS");
			}
		}
		else if ((npcId == 260008) && (worldIdx == 300140000))
		{
			float xBoss = 412.55566F;
			float yBoss = 432.34677F;
			float zBoss = 196.79149F;
			byte hBoss = 92;
			SpawnTemplate spawn = SpawnEngine.addNewSpawn(300140000, 209252, xBoss, yBoss, zBoss, hBoss, 0);
			VisibleObject visibleObject = SpawnEngine.spawnObject(spawn, 1);

			Iterator iter = World.getInstance().getPlayersIterator();
			while (iter.hasNext()) {
				PacketSendUtility.sendBrightYellowMessageOnCenter((Player)iter.next(), "GRAND STALLARI HAS BEEN KILLED BY ELYOS");
			}
		}
		else
		{
			VisibleObject visibleObject;
			if ((npcId == 209052) && (worldIdx == 300140000))
			{
				for (Player p : World.getInstance().getAllPlayers())
				{
					if ((p.getRace() == Race.ELYOS) && (p.getWorldId() == 300140000)) {
						ItemService.addItem(p, 186000147, 1L);
						ItemService.addItem(p, 186000096, 2L);
						p.setKisk(null);
						TeleportService2.teleportToNpc(p, 203752);
						PacketSendUtility.sendPacket(p, new SM_PLAYER_SPAWN(p));
						PacketSendUtility.sendBrightYellowMessageOnCenter(p, "DEFENDER CAPTAIN ELYOS HAS BEEN KILLED");
						PacketSendUtility.sendMessage(p, "YOU GET WAR REWARD.CHECK YOUR INVETORY");
					}
					else
					{
						PacketSendUtility.sendBrightYellowMessageOnCenter(p, "DEFENDER CAPTAIN ELYOS HAS BEEN KILLED");
					}
				}

				set_ely_win(1);

				float xBoss = 525.84918F;
				float yBoss = 845.63879F;
				float zBoss = 199.40396F;
				byte hBoss = 48;
				SpawnTemplate spawn = SpawnEngine.addNewSpawn(300140000, 218553, xBoss, yBoss, zBoss, hBoss, 0);
				visibleObject = SpawnEngine.spawnObject(spawn, 1);

				Iterator iter = World.getInstance().getPlayersIterator();
				while (iter.hasNext()) {
					PacketSendUtility.sendBrightYellowMessageOnCenter((Player)iter.next(), "MAIN BOSS SPAWN");
				}
			}
			else if ((npcId == 209252) && (worldIdx == 300140000)) {
				for (Player p : World.getInstance().getAllPlayers())
				{
					if ((p.getRace() == Race.ASMODIANS) && (p.getWorldId() == 300140000)) {
						ItemService.addItem(p, 186000147, 1L);
						ItemService.addItem(p, 186000096, 2L);
						p.setKisk(null);
						TeleportService2.teleportToNpc(p, 204075);
						PacketSendUtility.sendPacket(p, new SM_PLAYER_SPAWN(p));
						PacketSendUtility.sendBrightYellowMessageOnCenter(p, "DEFENDER CAPTAIN ASMODIAN HAS BEEN KILLED");
						PacketSendUtility.sendMessage(p, "YOU GET WAR REWARD.CHECK YOUR INVETORY");
					}
					else
					{
						PacketSendUtility.sendBrightYellowMessageOnCenter(p, "DEFENDER CAPTAIN ASMODIAN HAS BEEN KILLED");
					}
				}

				set_asmo_win(1);

				float xBoss = 525.84918F;
				float yBoss = 845.63879F;
				float zBoss = 199.40396F;
				byte hBoss = 48;
				SpawnTemplate spawn = SpawnEngine.addNewSpawn(300140000, 218553, xBoss, yBoss, zBoss, hBoss, 0);
				visibleObject = SpawnEngine.spawnObject(spawn, 1);
				
				Iterator iter = World.getInstance().getPlayersIterator();
				while (iter.hasNext()) {
					PacketSendUtility.sendBrightYellowMessageOnCenter((Player)iter.next(), "MAIN BOSS SPAWN");
				}
			}
			else if ((npcId == 218553) && (worldIdx == 300140000))
			{
				Iterator iter = World.getInstance().getPlayersIterator();
				while (iter.hasNext()) {
					PacketSendUtility.sendBrightYellowMessageOnCenter((Player)iter.next(), "MAIN BOSS HAS BEEN KILLED . TAKE ON TREASURE");
				}

				SpawnTemplate spawn = SpawnEngine.addNewSpawn(300140000, 700559, 531.39423F, 815.66479F, 199.22353F, (byte)20, 0);
				visibleObject = SpawnEngine.spawnObject(spawn, 1);
				spawn = SpawnEngine.addNewSpawn(300140000, 700559, 828.67493F, 198.57877F, 198.57877F, (byte)12, 0);
				visibleObject = SpawnEngine.spawnObject(spawn, 1);
				spawn = SpawnEngine.addNewSpawn(300140000, 700559, 501.44934F, 852.94055F, 198.57877F, (byte)89, 0);
				visibleObject = SpawnEngine.spawnObject(spawn, 1);
				spawn = SpawnEngine.addNewSpawn(300140000, 700559, 543.84851F, 864.12476F, 198.57877F, (byte)45, 0);
				visibleObject = SpawnEngine.spawnObject(spawn, 1);
				spawn = SpawnEngine.addNewSpawn(300140000, 700559, 504.13675F, 840.71375F, 198.57877F, (byte)9, 0);
				visibleObject = SpawnEngine.spawnObject(spawn, 1);
				spawn = SpawnEngine.addNewSpawn(300140000, 700559, 510.42804F, 837.87952F, 198.57877F, (byte) 106, 0);
				visibleObject = SpawnEngine.spawnObject(spawn, 1);
				spawn = SpawnEngine.addNewSpawn(300140000, 700559, 515.8974F, 833.00629F, 198.57877F, (byte) 106, 0);
				visibleObject = SpawnEngine.spawnObject(spawn, 1);
				spawn = SpawnEngine.addNewSpawn(300140000, 700559, 512.57928F, 864.22632F, 198.57877F, (byte)61, 0);
				visibleObject = SpawnEngine.spawnObject(spawn, 1);
				spawn = SpawnEngine.addNewSpawn(300140000, 700559, 518.20026F, 869.04041F, 198.57877F, (byte)63, 0);
				visibleObject = SpawnEngine.spawnObject(spawn, 1);
				spawn = SpawnEngine.addNewSpawn(300140000, 700559, 530.13312F, 872.7041F, 198.57877F, (byte)78, 0);
				visibleObject = SpawnEngine.spawnObject(spawn, 1);
				spawn = SpawnEngine.addNewSpawn(300140000, 700559, 535.75787F, 869.94751F, 198.57877F, (byte)93, 0);
				visibleObject = SpawnEngine.spawnObject(spawn, 1);
				spawn = SpawnEngine.addNewSpawn(300140000, 700559, 555.771F, 842.53278F, 198.57877F, (byte)41, 0);
				visibleObject = SpawnEngine.spawnObject(spawn, 1);
				spawn = SpawnEngine.addNewSpawn(300140000, 700559, 552.00305F, 848.96887F, 198.57877F, (byte)69, 0);
				visibleObject = SpawnEngine.spawnObject(spawn, 1);
				spawn = SpawnEngine.addNewSpawn(300140000, 700559, 550.66498F, 829.966F, 198.57877F, (byte)40, 0);
				visibleObject = SpawnEngine.spawnObject(spawn, 1);
				spawn = SpawnEngine.addNewSpawn(300140000, 700559, 540.34607F, 822.6933F, 198.57877F, (byte)94, 0);
				visibleObject = SpawnEngine.spawnObject(spawn, 1);
				spawn = SpawnEngine.addNewSpawn(300140000, 700559, 515.85638F, 822.22247F, 198.57877F, (byte)72, 0);
				visibleObject = SpawnEngine.spawnObject(spawn, 1);
				spawn = SpawnEngine.addNewSpawn(300140000, 700559, 505.80798F, 829.78961F, 198.57877F, (byte)7, 0);
				visibleObject = SpawnEngine.spawnObject(spawn, 1);
				spawn = SpawnEngine.addNewSpawn(300140000, 700559, 521.40344F, 862.19238F, 198.57877F, (byte)5, 0);
				visibleObject = SpawnEngine.spawnObject(spawn, 1);
				spawn = SpawnEngine.addNewSpawn(300140000, 700559, 530.66956F, 863.50433F, 198.57877F, (byte)113, 0);
				visibleObject = SpawnEngine.spawnObject(spawn, 1);
				spawn = SpawnEngine.addNewSpawn(300140000, 700559, 544.83728F, 843.9259F, 198.57877F, (byte)88, 0);
				visibleObject = SpawnEngine.spawnObject(spawn, 1);
				spawn = SpawnEngine.addNewSpawn(300140000, 700559, 541.51947F, 836.18701F, 198.57877F, (byte)86, 0);
				visibleObject = SpawnEngine.spawnObject(spawn, 1);
				spawn = SpawnEngine.addNewSpawn(300140000, 700559, 537.80243F, 828.16553F, 198.57877F, (byte)19, 0);
				visibleObject = SpawnEngine.spawnObject(spawn, 1);
				spawn = SpawnEngine.addNewSpawn(300140000, 218732, 516.58136F, 822.98578F, 198.57877F, (byte)36, 0);
				visibleObject = SpawnEngine.spawnObject(spawn, 1);
				spawn = SpawnEngine.addNewSpawn(300140000, 218732, 507.71252F, 833.8385F, 198.57877F, (byte)106, 0);
				visibleObject = SpawnEngine.spawnObject(spawn, 1);
				spawn = SpawnEngine.addNewSpawn(300140000, 218732, 499.35425F, 843.31934F, 198.57877F, (byte)38, 0);
				visibleObject = SpawnEngine.spawnObject(spawn, 1);
				spawn = SpawnEngine.addNewSpawn(300140000, 218732, 516.01947F, 862.42877F, 198.57877F, (byte)24, 0);
				visibleObject = SpawnEngine.spawnObject(spawn, 1);
				spawn = SpawnEngine.addNewSpawn(300140000, 218732, 527.54858F, 867.42609F, 198.57877F, (byte)0, 0);
				visibleObject = SpawnEngine.spawnObject(spawn, 1);
				spawn = SpawnEngine.addNewSpawn(300140000, 218732, 539.11145F, 863.3844F, 198.57877F, (byte)105, 0);
				visibleObject = SpawnEngine.spawnObject(spawn, 1);
				spawn = SpawnEngine.addNewSpawn(300140000, 218732, 548.15967F, 845.02496F, 198.57877F, (byte)93, 0);
				visibleObject = SpawnEngine.spawnObject(spawn, 1);
				spawn = SpawnEngine.addNewSpawn(300140000, 218732, 551.53027F, 834.95496F, 198.57877F, (byte)109, 0);
				visibleObject = SpawnEngine.spawnObject(spawn, 1);
				spawn = SpawnEngine.addNewSpawn(300140000, 218732, 538.32965F, 824.65552F, 198.57877F, (byte)115, 0);
				visibleObject = SpawnEngine.spawnObject(spawn, 1);

				if (get_asmo_win() == 1) {
					for (Player p : World.getInstance().getAllPlayers())
					{
						if ((p.getRace() == Race.ASMODIANS) && (p.getWorldId() == 300140000)) {
							if (p.getPlayerAccount().getMembership() > 0) {
								ItemService.addItem(p, 186000147, 4L);
								ItemService.addItem(p, 186000096, 10L);
								PacketSendUtility.sendMessage(p, "YOU GET WAR REWARD.CHECK YOUR INVETORY");
							} else {
								ItemService.addItem(p, 186000147, 2L);
								ItemService.addItem(p, 186000096, 5L);
								PacketSendUtility.sendMessage(p, "YOU GET WAR REWARD.CHECK YOUR INVETORY");
							}
						}
					}

				}
				else
				{
					for (Player p : World.getInstance().getAllPlayers())
					{
						if ((p.getRace() == Race.ELYOS) && (p.getWorldId() == 300140000)) {
							if (p.getPlayerAccount().getMembership() > 0) {
								ItemService.addItem(p, 186000147, 4L);
								ItemService.addItem(p, 186000096, 10L);
								PacketSendUtility.sendMessage(p, "YOU GET WAR REWARD.CHECK YOUR INVETORY");
							} else {
								ItemService.addItem(p, 186000147, 2L);
								ItemService.addItem(p, 186000096, 5L);
								PacketSendUtility.sendMessage(p, "YOU GET WAR REWARD.CHECK YOUR INVETORY");
							}
						}
					}
					
				}

			}

		}

	}
	catch (Exception e)
	{
		log.info(e.toString());
	}
	
}
}