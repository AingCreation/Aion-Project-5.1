package com.aionemu.gameserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.network.NioServer;
import com.aionemu.commons.network.ServerCfg;
import com.aionemu.commons.services.CronService;
import com.aionemu.commons.utils.AEInfos;
import com.aionemu.gameserver.ai2.AI2Engine;
import com.aionemu.gameserver.cache.HTMLCache;
import com.aionemu.gameserver.configs.Config;
import com.aionemu.gameserver.configs.main.*;
import com.aionemu.gameserver.configs.network.NetworkConfig;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.instance.InstanceEngine;
import com.aionemu.gameserver.model.GameEngine;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.house.MaintenanceTask;
import com.aionemu.gameserver.model.siege.Influence;
import com.aionemu.gameserver.network.BannedHDDManager;
import com.aionemu.gameserver.network.BannedMacManager;
import com.aionemu.gameserver.network.NetworkBannedManager;
import com.aionemu.gameserver.network.aion.GameConnectionFactoryImpl;
import com.aionemu.gameserver.network.chatserver.ChatServer;
import com.aionemu.gameserver.network.loginserver.LoginServer;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.services.dreamergames.CustomBosses.BossSpawnService;
import com.aionemu.gameserver.services.abyss.AbyssRankCleaningService;
import com.aionemu.gameserver.services.abyss.AbyssRankUpdateService;
import com.aionemu.gameserver.services.dreamergames.services.WarSystemService;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.services.events.*;
import com.aionemu.gameserver.services.instance.*;
import com.aionemu.gameserver.services.player.LunaShopService;
import com.aionemu.gameserver.services.player.PlayerEventService;
import com.aionemu.gameserver.services.player.PlayerEventService2;
import com.aionemu.gameserver.services.player.PlayerEventService3;
import com.aionemu.gameserver.services.player.PlayerEventService4;
import com.aionemu.gameserver.services.player.PlayerLimitService;
import com.aionemu.gameserver.services.reward.RewardService;
import com.aionemu.gameserver.services.abysslandingservice.LandingUpdateService;
import com.aionemu.gameserver.services.territory.TerritoryService;
import com.aionemu.gameserver.services.teleport.HotspotTeleportService;
import com.aionemu.gameserver.services.thedevils.EventGebukMantan;
import com.aionemu.gameserver.services.thedevils.ExpoEventAsmo;
import com.aionemu.gameserver.services.thedevils.ExpoEventElyos;
import com.aionemu.gameserver.services.thedevils.TreasureAbyss;
import com.aionemu.gameserver.services.reward.TollOnlineBonus;
import com.aionemu.gameserver.services.transfers.PlayerTransferService;
import com.aionemu.gameserver.spawnengine.ShugoImperialTombSpawnManager;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.spawnengine.TemporarySpawnEngine;
import com.aionemu.gameserver.taskmanager.TaskManagerFromDB;
import com.aionemu.gameserver.taskmanager.tasks.PacketBroadcaster;
import com.aionemu.gameserver.utils.AEVersions;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.ThreadUncaughtExceptionHandler;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.utils.chathandlers.ChatProcessor;
import com.aionemu.gameserver.utils.cron.ThreadPoolManagerRunnableRunner;
import com.aionemu.gameserver.utils.gametime.DateTimeUtil;
import com.aionemu.gameserver.utils.gametime.GameTimeManager;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.aionemu.gameserver.utils.javaagent.JavaAgentUtils;
import com.aionemu.gameserver.utils.mui.MuiEngine;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.geo.GeoService;
import com.aionemu.gameserver.world.zone.ZoneService;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

public class GameServer
{
	public static final Logger log = LoggerFactory.getLogger(GameServer.class);
	private static int ELYOS_COUNT = 0;
	private static int ASMOS_COUNT = 0;
	private static double ELYOS_RATIO = 0.0;
	private static double ASMOS_RATIO = 0.0;
	private static final ReentrantLock lock = new ReentrantLock();
	
	private static Set<StartupHook> startUpHooks = new HashSet<StartupHook>();
	
	private static void initalizeLoggger() {
		new File("./log/backup/").mkdirs();
		File[] files = new File("log").listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".log");
			}
		});
		if (files != null && files.length > 0) {
			byte[] buf = new byte[1024];
			try {
				String outFilename = "./log/backup/" + new SimpleDateFormat("yyyy-MM-dd HHmmss").format(new Date()) + ".zip";
				ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outFilename));
				out.setMethod(ZipOutputStream.DEFLATED);
				out.setLevel(Deflater.BEST_COMPRESSION);
				for (File logFile : files) {
					FileInputStream in = new FileInputStream(logFile);
					out.putNextEntry(new ZipEntry(logFile.getName()));
					int len;
					while ((len = in.read(buf)) > 0) {
						out.write(buf, 0, len);
					}
					out.closeEntry();
					in.close();
					logFile.delete();
				}
				out.close();
			}
			catch (IOException e) {
			}
		}
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		try {
			JoranConfigurator configurator = new JoranConfigurator();
			configurator.setContext(lc);
			lc.reset();
			configurator.doConfigure("config/slf4j-logback.xml");
		} catch (JoranException je) {
			throw new RuntimeException("Failed to configure loggers, shutting down...", je);
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		long start = System.currentTimeMillis();
		final GameEngine[] parallelEngines = { QuestEngine.getInstance(), InstanceEngine.getInstance(), AI2Engine.getInstance(), ChatProcessor.getInstance() };
		
		final CountDownLatch progressLatch = new CountDownLatch(parallelEngines.length);
		final GameEngine[] loadFirstEngine = new GameEngine[]{MuiEngine.getInstance()};
		final CountDownLatch progressFirstLatch = new CountDownLatch(loadFirstEngine.length);
		
		initalizeLoggger();
		initUtilityServicesAndConfig();
		
		Util.printSection("MULTI LANGUAGE ENGINE");
		for (int i = 0; i < loadFirstEngine.length; i++) {
			final int index = i;
			ThreadPoolManager.getInstance().execute(new Runnable() {
				@Override
				public void run() {
					loadFirstEngine[index].load(progressFirstLatch);
				}
			});
		}
		try {
			progressFirstLatch.await();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		MuiService.getInstance().load();
		
		DataManager.getInstance();
		Util.printSection("Zone");
		ZoneService.getInstance().load(null);
		Util.printSection("World");
		World.getInstance();
		Util.printSection("Luna Shop initialization");
		LunaShopService.getInstance().init();
		Util.printSection("Drops");
		DropRegistrationService.getInstance();
		GameServer gs = new GameServer();
		DAOManager.getDAO(PlayerDAO.class).setPlayersOffline(false);
		Util.printSection("HDDBan");
		BannedMacManager.getInstance();
		BannedHDDManager.getInstance();
		NetworkBannedManager.getInstance();
		Util.printSection("Cleaning");
 		DatabaseCleaningService.getInstance();
		AbyssRankCleaningService.getInstance();
		Util.printSection("Geodata");
		GeoService.getInstance().initializeGeo();
		System.gc();
		for (int i = 0; i < parallelEngines.length; i++) {
			final int index = i;
			ThreadPoolManager.getInstance().execute(new Runnable() {
				public void run() {
					parallelEngines[index].load(progressLatch);
				}
			});
		}
		try {
			progressLatch.await();
		}
		catch (InterruptedException e1) {
		}
		//Siege
		Util.printSection("Siege Location Data");
		SiegeService.getInstance().initSiegeLocations();
		//Base
		Util.printSection("Base Location Data");
		BaseService.getInstance().initBaseLocations();
		Util.printSection("Base Reset");
		BaseService.getInstance().initBaseReset();
		//Vortex
		Util.printSection("Vortex Location Data");
		VortexService.getInstance().initVortex();
		VortexService.getInstance().initVortexLocations();
		//Beritra Invasion
		Util.printSection("Beritra Location Data");
		BeritraService.getInstance().initBeritra();
		BeritraService.getInstance().initBeritraLocations();
		//Agent Fight
		Util.printSection("Agent Location Data");
		AgentService.getInstance().initAgent();
		AgentService.getInstance().initAgentLocations();
		//Berserk Anoha
		Util.printSection("Anoha Location Data");
		AnohaService.getInstance().initAnoha();
		AnohaService.getInstance().initAnohaLocations();
		//Panesterra
		Util.printSection("Svs Location Data");
		SvsService.getInstance().initSvs();
		SvsService.getInstance().initSvsLocations();
		//Rvr 4.9
		Util.printSection("Rvr Location Data");
		RvrService.getInstance().initRvr();
		RvrService.getInstance().initRvrLocations();
		//Live Party Concert Hall 4.3
		Util.printSection("Concert Location Data");
		IuService.getInstance().initConcertLocations();
		//Nightmare Circus 4.3
		Util.printSection("Nightmare Circus Location Data");
		NightmareCircusService.getInstance().initCircus();
		NightmareCircusService.getInstance().initCircusLocations();
		//Dynamic Rift
		Util.printSection("Dynamic Rift Location Data");
		DynamicRiftService.getInstance().initDynamicRiftLocations();
		//Instance Rift 4.9
		Util.printSection("Instance Rift Location Data");
		InstanceRiftService.getInstance().initInstance();
		InstanceRiftService.getInstance().initInstanceLocations();
		//Zorshiv Dredgion
		Util.printSection("Zorshiv Dredgion Location Data");
		ZorshivDredgionService.getInstance().initZorshivDredgion();
		ZorshivDredgionService.getInstance().initZorshivDredgionLocations();
		//Moltenus
		Util.printSection("Moltenus Location Data");
		MoltenusService.getInstance().initMoltenus();
		MoltenusService.getInstance().initMoltenusLocations();
		//Rift
		Util.printSection("Rift Location Data");
		RiftService.getInstance().initRiftLocations();
		//Conquest/Offering 4.8
		Util.printSection("Conquest/Offering Location Data");
		ConquestService.getInstance().initOffering();
		ConquestService.getInstance().initConquestLocations();
		//Abyss Landing 4.9.1
		Util.printSection("Abyss Landing Location Data");
		AbyssLandingService.getInstance().initLandingLocations();
		LandingUpdateService.getInstance().initResetQuestPoints();
		LandingUpdateService.getInstance().initResetAbyssLandingPoints();
		AbyssLandingSpecialService.getInstance().initLandingSpecialLocations();
		//Idian Depths 4.8
		Util.printSection("Idian Depths Location Data");
		IdianDepthsService.getInstance().initIdianDepthsLocations();
		Util.printSection("Spawns");
		SpawnEngine.spawnAll();
		RiftService.getInstance().initRifts();
		TerritoryService.getInstance().initTerritory();
		//Event 4.3/4.7
		if (EventsConfig.IMPERIAL_TOMB_ENABLE) { //Shugo Imperial Tomb 4.3
		    ShugoImperialTombSpawnManager.getInstance().start();
		} if (EventsConfig.ENABLE_CRAZY) {
			Util.printSection("Crazy Daeva");
			CrazyDaevaService.getInstance().startTimer();
		}
		TemporarySpawnEngine.spawnAll();
		Util.printSection("War System");
		WarSystemService.getInstance().InitSystemWar();
		Util.printSection("PvP System");
		if (PvPModConfig.FFA_ENABLED) {
			FFAService.getInstance();
		} if (PvPModConfig.BG_ENABLED) {
			//LadderService.getInstance();
			BGService.getInstance();
		}
		Util.printSection("Limits");
		//LimitedItemTradeService.getInstance().start();
		if (CustomConfig.LIMITS_ENABLED) {
			PlayerLimitService.getInstance().scheduleUpdate();
		}
		GameTimeManager.startClock();
		Util.printSection("Siege Schedule initialization");
		SiegeService.getInstance().initSieges();
		Util.printSection("TaskManagers");
		PacketBroadcaster.getInstance();
		GameTimeService.getInstance();
		AnnouncementService.getInstance();
		DebugService.getInstance();
		WeatherService.getInstance();
		BrokerService.getInstance();
		Influence.getInstance();
		ExchangeService.getInstance();
		PeriodicSaveService.getInstance();
		PetitionService.getInstance();
		if (AIConfig.SHOUTS_ENABLE) {
			NpcShoutsService.getInstance();
		}
		InstanceService.load();
		FlyRingService.getInstance();
		CuringZoneService.getInstance();
		SpringZoneService.getInstance();
		RoadService.getInstance();
		HTMLCache.getInstance();
		if (RankingConfig.TOP_RANKING_UPDATE_SETTING) {
			AbyssRankUpdateService.getInstance().scheduleUpdateHour();
		} else {
			AbyssRankUpdateService.getInstance().scheduleUpdateMinute();
		}
		TaskManagerFromDB.getInstance();
		if (SiegeConfig.SIEGE_SHIELD_ENABLED) {
			ShieldService.getInstance().spawnAll();
		}
		//Dredgion
		if (AutoGroupConfig.AUTO_GROUP_ENABLED) {
			Util.printSection("[Baranath/Chantra/Terath] Dredgion");
			DredgionService2.getInstance().initDredgion();
		} if (AutoGroupConfig.AUTO_GROUP_ENABLED) {
			Util.printSection("[Ashunatal] Dredgion 5.1");
			AsyunatarService.getInstance().initAsyunatar();
		}
		//Battlefield
		if (AutoGroupConfig.AUTO_GROUP_ENABLED) {
			Util.printSection("Kamar Battlefield 4.3");
			KamarBattlefieldService.getInstance().initKamarBattlefield();
		} if (AutoGroupConfig.AUTO_GROUP_ENABLED) {
			Util.printSection("Engulfed Ophidan Bridge 4.5");
			EngulfedOphidanBridgeService.getInstance().initEngulfedOphidan();
		} if (AutoGroupConfig.AUTO_GROUP_ENABLED) {
			Util.printSection("Ophidan Warpath 5.1");
			SuspiciousOphidanBridgeService.getInstance().initSuspiciousOphidan();
		} if (AutoGroupConfig.AUTO_GROUP_ENABLED) {
			Util.printSection("Iron Wall Warfront 4.5");
			IronWallWarfrontService.getInstance().initIronWallWarfront();
		} if (AutoGroupConfig.AUTO_GROUP_ENABLED) {
			Util.printSection("Idgel Dome 4.7");
			IdgelDomeService.getInstance().initIdgelDome();
		} if (AutoGroupConfig.AUTO_GROUP_ENABLED) {
			Util.printSection("Idgel Dome Landmark 5.1");
			IdgelDomeLandmarkService.getInstance().initLandmark();
		}
		//Aionunity Event
		Util.printSection("Elyos Babi Event");
        ExpoEventElyos.ScheduleCron();
        Util.printSection("Asmodians Babi Event");
        ExpoEventAsmo.ScheduleCron();
		Util.printSection("Treasure Abyss Event");
		TreasureAbyss.ScheduleCron();
		Util.printSection("Gebuk Mantan Event");
        EventGebukMantan.ScheduleCron();
		//Custom Spawn boss
		Util.printSection("Custom Boss Spawn");
		BossSpawnService.getInstance();
		//Custom
		if (CustomConfig.ENABLE_REWARD_SERVICE) {
			RewardService.getInstance();
		} if (EventsConfig.EVENT_ENABLED) {
			PlayerEventService.getInstance();
		} if (EventsConfig.EVENT_ENABLED2) {
			PlayerEventService2.getInstance();
		} if (EventsConfig.EVENT_ENABLED2) {
			PlayerEventService3.getInstance();
		} if (EventsConfig.EVENT_ENABLED2) {
			PlayerEventService4.getInstance();
		} if (EventsConfig.ENABLE_EVENT_SERVICE) {
			EventService.getInstance().start();
		} if (EventsConfig.ENABLE_ATREIAN_PASSPORT) {
			AtreianPassportService.getInstance().onStart();
		/*} if (WeddingsConfig.WEDDINGS_ENABLE) {
			WeddingService.getInstance();*/
		}
		  if (MembershipConfig.TOLL_ONLINE_BONUS_ENABLE) {
            TollOnlineBonus.getInstance();
        }
		AdminService.getInstance();
		PlayerTransferService.getInstance();
		MaintenanceTask.getInstance();
		HousingBidService.getInstance().start();
		TownService.getInstance();
		ChallengeTaskService.getInstance();
		HotspotTeleportService.getInstance();
		Util.printSection("Protector/Conqueror initialization");
		ProtectorConquerorService.getInstance().initSystem();
		Util.printSection("Dispute Land initialization");
		DisputeLandService.getInstance().initDisputeLand();
		BaseService.getInstance().initBases();
		Util.printSection("System");
		AEVersions.printFullVersionInfo();
		System.gc();
		AEInfos.printAllInfos();
		Util.printSection("GameServerLog");
		log.info("Encom Server started in " + (System.currentTimeMillis() - start) / 1000 + " seconds.");
		gs.startServers();
		Runtime.getRuntime().addShutdownHook(ShutdownHook.getInstance());
		if (GSConfig.ENABLE_RATIO_LIMITATION) {
			addStartupHook(new StartupHook() {
				@Override
				public void onStartup() {
					lock.lock();
					try {
						ASMOS_COUNT = DAOManager.getDAO(PlayerDAO.class).getCharacterCountForRace(Race.ASMODIANS);
						ELYOS_COUNT = DAOManager.getDAO(PlayerDAO.class).getCharacterCountForRace(Race.ELYOS);
						computeRatios();
					}
					catch (Exception e) {
					}
					finally {
						lock.unlock();
					}
					displayRatios(false);
				}
			});
		}
		onStartup();
	}
	
	private void startServers() {
		Util.printSection("Starting Network");
		NioServer nioServer = new NioServer(NetworkConfig.NIO_READ_WRITE_THREADS, new ServerCfg(NetworkConfig.GAME_BIND_ADDRESS, NetworkConfig.GAME_PORT, "Game Connections", new GameConnectionFactoryImpl()));
		LoginServer ls = LoginServer.getInstance();
		ChatServer cs = ChatServer.getInstance();
		ls.setNioServer(nioServer);
		cs.setNioServer(nioServer);
		nioServer.connect();
		ls.connect();
		if (GSConfig.ENABLE_CHAT_SERVER)
			cs.connect();
	}
	
	private static void initUtilityServicesAndConfig() {
		Thread.setDefaultUncaughtExceptionHandler(new ThreadUncaughtExceptionHandler());
		if (JavaAgentUtils.isConfigured()) {
			log.info("JavaAgent [Callback Support] is configured.");
		}
		CronService.initSingleton(ThreadPoolManagerRunnableRunner.class);
		Config.load();
		DateTimeUtil.init();
		Util.printSection("DataBase");
		DatabaseFactory.init();
		DAOManager.init();
		Util.printSection("IDFactory");
		IDFactory.getInstance();
		Util.printSection("Threads");
		ThreadConfig.load();
		ThreadPoolManager.getInstance();
	}
	
	public static synchronized void addStartupHook(StartupHook hook) {
		if (startUpHooks != null)
			startUpHooks.add(hook);
		else
			hook.onStartup();
	}
	
	private synchronized static void onStartup() {
		final Set<StartupHook> startupHooks = startUpHooks;
		startUpHooks = null;
		for (StartupHook hook : startupHooks)
			hook.onStartup();
	}
	
	public static void updateRatio(Race race, int i) {
		lock.lock();
		try {
			switch (race) {
			case ASMODIANS:
				ASMOS_COUNT += i;
			break;
			case ELYOS:
				ELYOS_COUNT += i;
			break;
			default:
				break;
			}
			computeRatios();
		}
		catch (Exception e) {
		}
		finally {
			lock.unlock();
		}
		displayRatios(true);
	}
	
	private static void computeRatios() {
		if (ASMOS_COUNT <= GSConfig.RATIO_MIN_CHARACTERS_COUNT && ELYOS_COUNT <= GSConfig.RATIO_MIN_CHARACTERS_COUNT) {
			ASMOS_RATIO = GameServer.ELYOS_RATIO = 50.0;
		} else {
			ASMOS_RATIO = ASMOS_COUNT * 100.0 / (ASMOS_COUNT + ELYOS_COUNT);
			ELYOS_RATIO = ELYOS_COUNT * 100.0 / (ASMOS_COUNT + ELYOS_COUNT);
		}
	}
	
	private static void displayRatios(boolean updated) {
		log.info("FACTIONS RATIO " + (updated ? "UPDATED " : "") + ": E " + String.format("%.1f", GameServer.ELYOS_RATIO) + " % / A " + String.format("%.1f", GameServer.ASMOS_RATIO) + " %");
	}
	
	public static double getRatiosFor(Race race) {
		switch (race) {
		case ASMODIANS:
			return ASMOS_RATIO;
		case ELYOS:
			return ELYOS_RATIO;
		default:
			return 0.0;
		}
	}
	
	public static int getCountFor(Race race) {
		switch (race) {
		case ASMODIANS:
			return ASMOS_COUNT;
		case ELYOS:
			return ELYOS_COUNT;
		default:
			return 0;
		}
	}
	
	public static abstract interface StartupHook {
		public abstract void onStartup();
	}
}