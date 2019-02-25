/*
 * This file is part of aion-lightning <v3.1>.
 *
 *  aion-lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-lightning. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.aionemu.gameserver.services.dreamergames.CustomBosses;

import com.aionemu.gameserver.configs.main.CustomConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.MuiService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 */
 
public class BossSpawnService {
    private static final Logger log = LoggerFactory.getLogger(BossSpawnService.class);

    public static final BossSpawnService getInstance() {
        return SingletonHolder.instance;
    }
        int delay = 3600000;
        private List<CustomBossSpawn> bosses = new ArrayList<CustomBossSpawn>();
        List<CustomSpawnLocation> locs = new ArrayList<CustomSpawnLocation>();

        private BossSpawnService()
        {
		if (CustomConfig.CUSTOMBOSSES_ENABLE){
            bosses.add(new CustomBossSpawn(281471,  new Integer[] {210020000, 220020000}));//Flamestorm lvl 30
            bosses.add(new CustomBossSpawn(296443,  new Integer[] {210040000, 220040000}));//Awaked Dragon lvl 40
            bosses.add(new CustomBossSpawn(281469,  new Integer[] {210040000, 220040000}));//Shadowshift lvl 50
            bosses.add(new CustomBossSpawn(281453,  new Integer[] {210050000, 220070000}));//Thunder lvl 55
            bosses.add(new CustomBossSpawn(281459,  new Integer[] {210050000, 220070000}));//Terra-Explosion lvl60
			bosses.add(new CustomBossSpawn(217244,  new Integer[] {600010000}));//The Canyonguard lvl60
			//bosses.add(new CustomBossSpawn(216580,  new Integer[] {320150000}));//Padmarashka
			//bosses.add(new CustomBossSpawn(216520,  new Integer[] {320150000}));//Sematariux
            bosses.add(new CustomBossSpawn(250129,  new Integer[] {210050000}));//Big Red Orb lvl60
			//bosses.add(new CustomBossSpawn(217237,  new Integer[] {600020000, 600030000}));//Cedella lvl60
			//bosses.add(new CustomBossSpawn(217308,  new Integer[] {600020000, 600030000}));//Tarotran lvl60
			//bosses.add(new CustomBossSpawn(217647,  new Integer[] {600020000, 600030000}));//Raksha Boilheart lvl58

            log.info("Loaded " + bosses.size() + " Custom Boss Spawns");
            //START Padma
            //locs.add(new CustomSpawnLocation(320150000, 556f, 228f, 66f)); //Should not be used, because its in instance
            //END Padma
			//START SILENTERA SCHLUCHT
			locs.add(new CustomSpawnLocation(600010000, 530f, 767f, 299f));
			//END SILENTERA SCHLUCHT
            //START HEIRON
            locs.add(new CustomSpawnLocation(210040000, 991.8446f, 1409.698f, 113.1347f)); //moveto 210040000 991 1409 113
            locs.add(new CustomSpawnLocation(210040000, 1834.493f, 762.2981f, 249.9011f)); //moveto 210040000 1834 762 249
            locs.add(new CustomSpawnLocation(210040000, 1571.718f, 2440.609f, 114.6739f)); //moveto 210040000 1471 2440 114
            locs.add(new CustomSpawnLocation(210040000, 365.3985f, 2286.308f, 134.882f)); //moveto 210040000 365 2286 134
            locs.add(new CustomSpawnLocation(210040000, 835.8367f, 170.6731f, 101.875f)); //moveto 210040000 835 170 101
            locs.add(new CustomSpawnLocation(210040000, 2246.722f, 2191.281f, 131.6352f)); //moveto 210040000 2246 2191 131
            locs.add(new CustomSpawnLocation(210040000, 1116.717f, 971.1219f, 129.25f)); //moveto 210040000 1116 971 129
            locs.add(new CustomSpawnLocation(210040000, 1682.633f, 128.7882f, 133.9772f)); //moveto 210040000 1682 128 133
            locs.add(new CustomSpawnLocation(210040000, 2715.075f, 1441.724f, 266.819f)); //moveto 210040000 2715 1441 266
            locs.add(new CustomSpawnLocation(210040000, 1654.473f, 1702.269f, 126.625f)); //moveto 210040000 1654 1702 126
            //END HEIRON
            //START ELTNEN
            locs.add(new CustomSpawnLocation(210020000, 1671.997f, 2825.939f, 356.375f)); //moveto 210020000 1671 2825 356
            locs.add(new CustomSpawnLocation(210020000, 2271.296f, 780.8216f, 470.875f)); //moveto 210020000 2271 780 470
            locs.add(new CustomSpawnLocation(210020000, 1411.403f, 396.7277f, 366.125f)); //moveto 210020000 1411 396 366
            locs.add(new CustomSpawnLocation(210020000, 966.6812f, 554.461f, 295.125f)); //moveto 210020000 966 554 295
            locs.add(new CustomSpawnLocation(210020000, 368.1938f, 1719.481f, 236.5967f)); //moveto 210020000 368 1719 236
            locs.add(new CustomSpawnLocation(210020000, 883.3257f, 2853.638f, 264.1477f)); //moveto 210020000 883 2853 264
            locs.add(new CustomSpawnLocation(210020000, 947.884f, 1779.93f, 267.4178f)); //moveto 210020000 947 1779 267
            locs.add(new CustomSpawnLocation(210020000, 1545.241f, 1410.011f, 314.201f)); //moveto 210020000 1545 1410 314
            locs.add(new CustomSpawnLocation(210020000, 1686.223f, 1412.359f, 369.4199f)); //moveto 210020000 1686 1412 369
            locs.add(new CustomSpawnLocation(210020000, 2358.527f, 2459.155f, 363.0958f)); //moveto 210020000 2358 2459 363
            //END ELTNEN
            //START MORHEIM
            locs.add(new CustomSpawnLocation(220020000, 262.6239f, 1637.061f, 357.1875f));  //moveto 220020000 262 1637 357
            locs.add(new CustomSpawnLocation(220020000, 1726.012f, 1224.391f, 24.68805f)); //moveto 220020000 1726 1224 24
            locs.add(new CustomSpawnLocation(220020000, 2280.638f, 1253.577f, 125.3021f)); //moveto 220020000 2280 1253 125
            locs.add(new CustomSpawnLocation(220020000, 2905.801f, 2759.998f, 203.0123f)); //moveto 220020000 2905 2759 203 
            locs.add(new CustomSpawnLocation(220020000, 2115.275f, 2814.667f, 214.625f)); //moveto 220020000 2115 2814 214
            locs.add(new CustomSpawnLocation(220020000, 1521.605f, 2739.26f, 360.5767f)); //moveto 220020000 1521 2739 360
            locs.add(new CustomSpawnLocation(220020000, 717.3057f, 239.8347f, 519.0543f)); //moveto 220020000 717 239 519
            locs.add(new CustomSpawnLocation(220020000, 75.97792f, 295.2102f, 443.25f)); //moveto 220020000 75 295 443
            locs.add(new CustomSpawnLocation(220020000, 759.1101f, 1160.589f, 281.375f)); //moveto 220020000 759 1160 281
            locs.add(new CustomSpawnLocation(220020000, 2912.147f, 656.8403f, 340.2751f)); //moveto 220020000 2912 656 340
            //END MORHEIM
            //START BELUSLAN
            locs.add(new CustomSpawnLocation(220040000, 292.4709f, 1696.353f, 206.3915f)); //moveto 220040000 292 1696 206
            locs.add(new CustomSpawnLocation(220040000, 711.0349f, 2259.349f, 245.7573f)); //moveto 220040000 711 2259 245
            locs.add(new CustomSpawnLocation(220040000, 821.9571f, 1440.199f, 213.0125f)); //moveto 220040000 821 1440 213
            locs.add(new CustomSpawnLocation(220040000, 326.4215f, 1031.829f, 280.8223f)); //moveto 220040000 326 1031 280
            locs.add(new CustomSpawnLocation(220040000, 969.9489f, 155.393f, 394.3163f)); //moveto 220040000 969 155 394
            locs.add(new CustomSpawnLocation(220040000, 1390.801f, 951.6295f, 138.0674f)); //moveto 220040000 1390 951 138
            locs.add(new CustomSpawnLocation(220040000, 1711.239f, 1739.293f, 572.8716f)); //moveto 220040000 1711 1739 572 
            locs.add(new CustomSpawnLocation(220040000, 2768.563f, 1872.804f, 593.3951f)); //moveto 220040000 2768 1872 593
            locs.add(new CustomSpawnLocation(220040000, 1422.149f, 2393.753f, 640.9753f)); //moveto 220040000 1422 2393 640
            locs.add(new CustomSpawnLocation(220040000, 1714.193f, 1736.715f, 573.0924f)); //moveto 220040000 1714 1736 573
            //END BELUSLAN
            //START GELKMAROS
            locs.add(new CustomSpawnLocation(220070000, 479.9383f, 1280.691f, 276.75f)); //moveto 220070000 479 1280 276
            locs.add(new CustomSpawnLocation(220070000, 2362.365f, 753.5611f, 263.0549f));//moveto 220070000 2362 753 263
            locs.add(new CustomSpawnLocation(220070000, 2274.756f, 1714.511f, 376.7991f));//moveto 220070000 2274 1714 376
            locs.add(new CustomSpawnLocation(220070000, 2983.484f, 2151.574f, 522.6516f));//moveto 220070000 2983 2151 522
            locs.add(new CustomSpawnLocation(220070000, 2478.237f, 2602.359f, 364.9928f));//moveto 220070000 2478 2602 364
            locs.add(new CustomSpawnLocation(220070000, 2186.608f, 2927.491f, 482.8031f));//moveto 220070000 2186 2927 482
            locs.add(new CustomSpawnLocation(220070000, 525.6667f, 2812.835f, 473.4272f));//moveto 220070000 525 2812 473
            locs.add(new CustomSpawnLocation(220070000, 1206.327f, 2448.461f, 169.3341f));//moveto 220070000 1206 2448 169
            locs.add(new CustomSpawnLocation(220070000, 853.6302f, 1992.066f, 327.7649f));//moveto 220070000 853 1992 327
            locs.add(new CustomSpawnLocation(220070000, 980.1166f, 826.2012f, 340.4874f));//moveto 220070000 980 826 340
            //END GELKMAROS
            //START INGGISON
            locs.add(new CustomSpawnLocation(210050000, 2544.075f, 146.8751f, 334.75f)); //moveto 210050000 2544 146 334
            locs.add(new CustomSpawnLocation(210050000, 2650.448f, 2096.127f, 200.6035f)); //moveto 210050000 2650 2096 200
            locs.add(new CustomSpawnLocation(210050000, 1219.812f, 1319.158f, 421.4019f)); //moveto 210050000 1219 1319 421 
            locs.add(new CustomSpawnLocation(210050000, 1668.857f, 545.3362f, 456.3004f)); //moveto 210050000 1668 545 456
            locs.add(new CustomSpawnLocation(210050000, 884.9834f, 651.6462f, 534.6029f)); //moveto 210050000 884 651 534
            locs.add(new CustomSpawnLocation(210050000, 1712.298f, 1620.167f, 223.8581f)); //moveto 210050000 1712 1620 223
            locs.add(new CustomSpawnLocation(210050000, 1357.821f, 1804.157f, 254.4583f)); //moveto 210050000 1357 1804 254
            locs.add(new CustomSpawnLocation(210050000, 604.817f, 1778.917f, 333.616f)); //moveto 210050000 604 1778 333
            locs.add(new CustomSpawnLocation(210050000, 182.4316f, 357.6212f, 570.071f)); //moveto 210050000 182 357 570
            locs.add(new CustomSpawnLocation(210050000, 2436.157f, 1092.929f, 269.75f)); //moveto 210050000 2436 1092 269
            //END INGGISON
			/*//START SARPAN
			locs.add(new CustomSpawnLocation(600020000, 1520.7632f, 2088.0115f, 456.09613f));//moveto 600020000 1520 2088 456
			locs.add(new CustomSpawnLocation(600020000, 385.57306f, 2468.2485f, 534.1027f));//moveto 600020000 385 2468 534
			locs.add(new CustomSpawnLocation(600020000, 460.41693f, 952.7681f, 659.0811f));//moveto 600020000 460 952 659
			locs.add(new CustomSpawnLocation(600020000, 2110.2615f, 296.98322f, 587.7191f));//moveto 600020000 2110 296 587
			locs.add(new CustomSpawnLocation(600020000, 2883.9336f, 784.6362f, 587.375f));//moveto 600020000 2883 784 587
			locs.add(new CustomSpawnLocation(600020000, 2235.08f, 1290.8022f, 215.98848f));//moveto 600020000 2235 1290 215
			locs.add(new CustomSpawnLocation(600020000, 130.44632f, 1682.1316f, 631.45264f));//moveto 600020000 130 1682 631
			locs.add(new CustomSpawnLocation(600020000, 1498.0471f, 1033.4885f, 528.75885f));//moveto 600020000 1498 1033 528
			locs.add(new CustomSpawnLocation(600020000, 896.80914f, 1712.6488f, 547.1994f));//moveto 600020000 896 1712 547
			locs.add(new CustomSpawnLocation(600020000, 2857.8354f, 2141.8896f, 540.48975f));//moveto 600020000 2857 2141 540
			//END SARPAN
			//START TIAMARANTA
			locs.add(new CustomSpawnLocation(600030000, 2195.185f, 431.30322f, 186.92593f));//moveto 600030000 2195 431 186
			locs.add(new CustomSpawnLocation(600030000, 1180.099f, 309.05466f, 204.43408f));//moveto 600030000 1180 309 204
			locs.add(new CustomSpawnLocation(600030000, 2288.1929f, 2844.6401f, 285.75f));//moveto 600030000 2288 2844 285
			locs.add(new CustomSpawnLocation(600030000, 2691.7686f, 1037.3234f, 156.52893f));//moveto 600030000 2691 1037 156
			locs.add(new CustomSpawnLocation(600030000, 2587.1436f, 1871.1405f, 266.50037f));//moveto 600030000 2587 1871 266
			locs.add(new CustomSpawnLocation(600030000, 980.98193f, 1518.5125f, 248.74731f));//moveto 600030000 980 1518 248
			locs.add(new CustomSpawnLocation(600030000, 1117.3011f, 2856.2544f, 262.97717f));//moveto 600030000 1117 2856 262
			locs.add(new CustomSpawnLocation(600030000, 679.0532f, 133.6708f, 308.375f));//moveto 600030000 679 133 308
			locs.add(new CustomSpawnLocation(600030000, 1932.7472f, 2244.8933f, 187.87328f));//moveto 600030000 1932 2244 187
			locs.add(new CustomSpawnLocation(600030000, 2851.4731f, 1864.2042f, 206.84438f));//moveto 600030000 2851 1864 206
			//END TIAMARANTA*/



            log.info("Loaded " + locs.size() + " Custom Boss Spawn Locations");
            ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

                @Override
                public void run() {
                    SpawnBoss();
                }

            }, 10000, CustomConfig.CUSTOMBOSSES_DELAY * 60000);
		}
		else
		{
		    log.info("Custom BossSpawnService is disabled");
		}
        }

    public void SpawnBoss()
    {
        Random r = new Random();
        CustomBossSpawn boss;
        CustomSpawnLocation loc;
        if(!isBossFree() || !isLocFree())
            return;
        do
        {
            boss = getBosses().get(r.nextInt(getBosses().size()));

        }while(boss.isSpawned());
        do
        {
            loc = locs.get(r.nextInt(locs.size()));
        }while(loc.isUsed || !boss.isPossibleSpawn(loc.getWorldId()));
        boss.Spawn(loc);
        log.info("[BossSpawn] "+getBossName(boss.getNpc()) + " was spawned in " + getLocation(boss.getLoc().getWorldId()) + " (MAP: "+boss.getLoc().getWorldId()+" X: "+boss.getLoc().getX()+ " Y: "+boss.getLoc().getY()+ " Z: "+boss.getLoc().getZ()+")");

        SendMessage(getBossName(boss.getNpc()) + MuiService.getInstance().getMessage("CUSTOM_BOSS_SPAWNED") + getLocation(boss.getLoc().getWorldId()));
    }

   public String getBossName(Npc npc)
    {
       switch(npc.getNpcId())
       {
           case 296443:
               return MuiService.getInstance().getMessage("CustomMessageId.CUSTOM_BOSS_NAME1");//Awaked Dragon //Erwachter Drache
           case 281469:
               return MuiService.getInstance().getMessage("CustomMessageId.CUSTOM_BOSS_NAME2");//Shadowshift //Schattenschreiter
           case 281471:
               return MuiService.getInstance().getMessage("CustomMessageId.CUSTOM_BOSS_NAME3");//Flamestorm //Flammensturm
           case 281453:
               return MuiService.getInstance().getMessage("CustomMessageId.CUSTOM_BOSS_NAME4");//Thunderstorm //Gewitter
           case 281459:
               return MuiService.getInstance().getMessage("CustomMessageId.CUSTOM_BOSS_NAME5");//Terra-Explosion //Terra-Explosion
		   case 217244:
			   return MuiService.getInstance().getMessage("CustomMessageId.CUSTOM_BOSS_NAME6");//The Canyonguard //Die Schluchtwaechterin
		   case 216580:
		       return MuiService.getInstance().getMessage("CustomMessageId.CUSTOM_BOSS_NAME7");//Padmarashka
		   case 216520:
               return MuiService.getInstance().getMessage("CustomMessageId.CUSTOM_BOSS_NAME8");//Sematariux
		   /*case 217237:
		       return MuiService.getInstance().getMessage("CustomMessageId.CUSTOM_BOSS_NAME9");//Cedella
		   case 217308:
		       return MuiService.getInstance().getMessage("CustomMessageId.CUSTOM_BOSS_NAME10");//Tarotran
		   case 217647:
		       return MuiService.getInstance().getMessage("CustomMessageId.CUSTOM_BOSS_NAME11");//Raksha Boilheart //Raksha Kochherz */
		   case 250129:
		       return MuiService.getInstance().getMessage("CustomMessageId.CUSTOM_BOSS_NAME12");//Big Red Orb //Grosse Rote Kugel
           default:
			   return MuiService.getInstance().getMessage("CustomMessageId.CUSTOM_BOSS_UNK");//Unk NPC //Unbekannter NPC
       }
   }

   public boolean isBossFree()
    {
        for(CustomBossSpawn boss : getBosses())
            if(!boss.isSpawned())
                return true;
        return false;
   }


   public String getLocation(int worldId)
   {
        switch(worldId)
        {
            case 220040000:
				return MuiService.getInstance().getMessage("CustomMessageId.CUSTOM_BOSS_LOC1");//Beluslan
            case 220020000:
				return MuiService.getInstance().getMessage("CustomMessageId.CUSTOM_BOSS_LOC2");//Morheim
            case 210020000:
				return MuiService.getInstance().getMessage("CustomMessageId.CUSTOM_BOSS_LOC3");//Eltnen
            case 210040000:
				return MuiService.getInstance().getMessage("CustomMessageId.CUSTOM_BOSS_LOC4");//Heiron
            case 210050000:
				return MuiService.getInstance().getMessage("CustomMessageId.CUSTOM_BOSS_LOC5");//Inggison
            case 220070000:
				return MuiService.getInstance().getMessage("CustomMessageId.CUSTOM_BOSS_LOC6");//Gelkmaros
			case 600010000:
				return MuiService.getInstance().getMessage("CustomMessageId.CUSTOM_BOSS_LOC7");//Silentera Canyon
			case 320150000:
                return MuiService.getInstance().getMessage("CustomMessageId.CUSTOM_BOSS_LOC8");//Padmarashkas Cave
			/*case 600020000:
			    return MuiService.getInstance().getMessage("CustomMessageId.CUSTOM_BOSS_LOC9");//Sarpan
			case 600030000:
			    return MuiService.getInstance().getMessage("CustomMessageId.CUSTOM_BOSS_LOC10");//Tiamaranta*/
            default: 
			    return MuiService.getInstance().getMessage("CustomMessageId.CUSTOM_BOSS_LOC_UNK");//Nothing to return
        }

   }

   public boolean isLocFree()
    {
        for(CustomSpawnLocation loc : locs)
            if(!loc.isUsed)
                return true;
        return false;
   }

   public void SendMessage(String msg)
    {
       for(Player p : World.getInstance().getAllPlayers())
       {
			PacketSendUtility.sendBrightYellowMessage(p, msg);
        }
   }

   public CustomBossSpawn getCustomBoss(Npc npc)
   {
        for(CustomBossSpawn boss : getBosses())
        {
            if(boss.getNpc() == npc)
                    return boss;
        }
        return null;
   }

    /**
     * @return the bosses
     */
    public List<CustomBossSpawn> getBosses() {
        return bosses;
    }

	@SuppressWarnings("synthetic-access")
    public static class SingletonHolder {
        protected static final BossSpawnService instance = new BossSpawnService();
    }
}
