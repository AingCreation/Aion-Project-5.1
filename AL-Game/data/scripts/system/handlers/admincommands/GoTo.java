package admincommands;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldMap;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.WorldMapType;

public class GoTo extends AdminCommand
{
	public GoTo() {
		super("goto");
	}
	
	@Override
	public void execute(Player player, String... params) {
		if (params == null || params.length < 1) {
			PacketSendUtility.sendMessage(player, "syntax //goto <location>");
			return;
		}
		StringBuilder sbDestination = new StringBuilder();
		for(String p: params) {
			sbDestination.append(p + " ");
		}
		String destination = sbDestination.toString().trim();
		
		if (destination.equalsIgnoreCase("Sanctum"))
			goTo(player, WorldMapType.SANCTUM.getId(), 1322, 1511, 568);
		else if (destination.equalsIgnoreCase("Kaisinel1"))
			goTo(player, WorldMapType.KAISINEL.getId(), 2155, 1567, 1205);
		else if (destination.equalsIgnoreCase("Poeta"))
			goTo(player, WorldMapType.POETA.getId(), 806, 1242, 119);
		else if (destination.equalsIgnoreCase("Melponeh"))
			goTo(player, WorldMapType.POETA.getId(), 426, 1740, 119);
		else if (destination.equalsIgnoreCase("Verteron"))
			goTo(player, WorldMapType.VERTERON.getId(), 1643, 1500, 119);
		else if (destination.equalsIgnoreCase("Cantas"))
			goTo(player, WorldMapType.VERTERON.getId(), 2384, 788, 102);
		else if (destination.equalsIgnoreCase("Ardus"))
			goTo(player, WorldMapType.VERTERON.getId(), 2333, 1817, 193);
		else if (destination.equalsIgnoreCase("Pilgrims"))
			goTo(player, WorldMapType.VERTERON.getId(), 2063, 2412, 274);
		else if (destination.equalsIgnoreCase("Tolbas"))
			goTo(player, WorldMapType.VERTERON.getId(), 1291, 2206, 142);
		else if (destination.equalsIgnoreCase("Eltnen"))
			goTo(player, WorldMapType.ELTNEN.getId(), 343, 2724, 264);
		else if (destination.equalsIgnoreCase("Golden"))
			goTo(player, WorldMapType.ELTNEN.getId(), 688, 431, 332);
		else if (destination.equalsIgnoreCase("Eltnen Observatory"))
			goTo(player, WorldMapType.ELTNEN.getId(), 1779, 883, 422);
		else if (destination.equalsIgnoreCase("Novan"))
			goTo(player, WorldMapType.ELTNEN.getId(), 947, 2215, 252);
		else if (destination.equalsIgnoreCase("Agairon"))
			goTo(player, WorldMapType.ELTNEN.getId(), 1921, 2045, 361);
		else if (destination.equalsIgnoreCase("Kuriullu"))
			goTo(player, WorldMapType.ELTNEN.getId(), 2411, 2724, 361);
		else if (destination.equalsIgnoreCase("Theobomos"))
			goTo(player, WorldMapType.THEOBOMOS.getId(), 1398, 1557, 31);
		else if (destination.equalsIgnoreCase("Jamanok"))
			goTo(player, WorldMapType.THEOBOMOS.getId(), 458, 1257, 127);
		else if (destination.equalsIgnoreCase("Meniherk"))
			goTo(player, WorldMapType.THEOBOMOS.getId(), 1396, 1560, 31);
		else if (destination.equalsIgnoreCase("obsvillage"))
			goTo(player, WorldMapType.THEOBOMOS.getId(), 2234, 2284, 50);
		else if (destination.equalsIgnoreCase("Josnack"))
			goTo(player, WorldMapType.THEOBOMOS.getId(), 901, 2774, 62);
		else if (destination.equalsIgnoreCase("Anangke"))
			goTo(player, WorldMapType.THEOBOMOS.getId(), 2681, 847, 138);
		else if (destination.equalsIgnoreCase("Heiron"))
			goTo(player, WorldMapType.HEIRON.getId(), 2540, 343, 411);
		else if (destination.equalsIgnoreCase("Heiron Observatory"))
			goTo(player, WorldMapType.HEIRON.getId(), 1423, 1334, 175);
		else if (destination.equalsIgnoreCase("Senemonea"))
			goTo(player, WorldMapType.HEIRON.getId(), 971, 686, 135);
		else if (destination.equalsIgnoreCase("Jeiaparan"))
			goTo(player, WorldMapType.HEIRON.getId(), 1635, 2693, 115);
		else if (destination.equalsIgnoreCase("Changarnerk"))
			goTo(player, WorldMapType.HEIRON.getId(), 916, 2256, 157);
		else if (destination.equalsIgnoreCase("Kishar"))
			goTo(player, WorldMapType.HEIRON.getId(), 1999, 1391, 118);
		else if (destination.equalsIgnoreCase("Arbolu"))
			goTo(player, WorldMapType.HEIRON.getId(), 170, 1662, 120);
		else if  (destination.equalsIgnoreCase("Pandaemonium"))
			goTo(player, WorldMapType.PANDAEMONIUM.getId(), 1679, 1400, 195);
		else if (destination.equalsIgnoreCase("Marchutan1"))
			goTo(player, WorldMapType.MARCHUTAN.getId(), 1557, 1429, 266);
		else if (destination.equalsIgnoreCase("Ishalgen"))
			goTo(player, WorldMapType.ISHALGEN.getId(), 529, 2449, 281);
		else if (destination.equalsIgnoreCase("Anturoon"))
			goTo(player, WorldMapType.ISHALGEN.getId(), 940, 1707, 259);
		else if (destination.equalsIgnoreCase("Altgard"))
			goTo(player, WorldMapType.ALTGARD.getId(), 1748, 1807, 254);
		else if (destination.equalsIgnoreCase("Basfelt"))
			goTo(player, WorldMapType.ALTGARD.getId(), 1903, 696, 260);
		else if (destination.equalsIgnoreCase("Trader"))
			goTo(player, WorldMapType.ALTGARD.getId(), 2680, 1024, 311);
		else if (destination.equalsIgnoreCase("Impetusium"))
			goTo(player, WorldMapType.ALTGARD.getId(), 2643, 1658, 324);
		else if (destination.equalsIgnoreCase("Altgard Observatory"))
			goTo(player, WorldMapType.ALTGARD.getId(), 1468, 2560, 299);
		else if (destination.equalsIgnoreCase("Morheim"))
			goTo(player, WorldMapType.MORHEIM.getId(), 308, 2274, 449);
		else if (destination.equalsIgnoreCase("Desert"))
			goTo(player, WorldMapType.MORHEIM.getId(), 634, 900, 360);
		else if (destination.equalsIgnoreCase("Slag"))
			goTo(player, WorldMapType.MORHEIM.getId(), 1772, 1662, 197);
		else if (destination.equalsIgnoreCase("Kellan"))
			goTo(player, WorldMapType.MORHEIM.getId(), 1070, 2486, 239);
		else if (destination.equalsIgnoreCase("Alsig"))
			goTo(player, WorldMapType.MORHEIM.getId(), 2387, 1742, 102);
		else if (destination.equalsIgnoreCase("Morheim Observatory"))
			goTo(player, WorldMapType.MORHEIM.getId(), 2794, 1122, 171);
		else if (destination.equalsIgnoreCase("Halabana"))
			goTo(player, WorldMapType.MORHEIM.getId(), 2346, 2219, 127);
		else if (destination.equalsIgnoreCase("Brusthonin"))
			goTo(player, WorldMapType.BRUSTHONIN.getId(), 2917, 2421, 15);
		else if (destination.equalsIgnoreCase("Baltasar"))
			goTo(player, WorldMapType.BRUSTHONIN.getId(), 1413, 2013, 51);
		else if (destination.equalsIgnoreCase("Bollu"))
			goTo(player, WorldMapType.BRUSTHONIN.getId(), 840, 2016, 307);
		else if (destination.equalsIgnoreCase("Edge"))
			goTo(player, WorldMapType.BRUSTHONIN.getId(), 1523, 374, 231);
		else if (destination.equalsIgnoreCase("Bubu"))
			goTo(player, WorldMapType.BRUSTHONIN.getId(), 526, 848, 76);
		else if (destination.equalsIgnoreCase("Settlers"))
			goTo(player, WorldMapType.BRUSTHONIN.getId(), 2917, 2417, 15);
		else if (destination.equalsIgnoreCase("Beluslan"))
			goTo(player, WorldMapType.BELUSLAN.getId(), 398, 400, 222);
		else if (destination.equalsIgnoreCase("Besfer"))
			goTo(player, WorldMapType.BELUSLAN.getId(), 533, 1866, 262);
		else if (destination.equalsIgnoreCase("Kidorun"))
			goTo(player, WorldMapType.BELUSLAN.getId(), 1243, 819, 260);
		else if (destination.equalsIgnoreCase("Red Mane"))
			goTo(player, WorldMapType.BELUSLAN.getId(), 2358, 1241, 470);
		else if (destination.equalsIgnoreCase("Kistenian"))
			goTo(player, WorldMapType.BELUSLAN.getId(), 1942, 513, 412);
		else if (destination.equalsIgnoreCase("Hoarfrost"))
			goTo(player, WorldMapType.BELUSLAN.getId(), 2431, 2063, 579);
		else if (destination.equalsIgnoreCase("Inggison"))
			goTo(player, WorldMapType.INGGISON.getId(), 1335, 276, 590);
		else if (destination.equalsIgnoreCase("Ufob"))
			goTo(player, WorldMapType.INGGISON.getId(), 382, 951, 460);
		else if (destination.equalsIgnoreCase("Soteria"))
			goTo(player, WorldMapType.INGGISON.getId(), 2713, 1477, 382);
		else if (destination.equalsIgnoreCase("Hanarkand"))
			goTo(player, WorldMapType.INGGISON.getId(), 1892, 1748, 327);
		else if (destination.equalsIgnoreCase("Gelkmaros"))
			goTo(player, WorldMapType.GELKMAROS.getId(), 1763, 2911, 554);
		else if (destination.equalsIgnoreCase("Subterranea"))
			goTo(player, WorldMapType.GELKMAROS.getId(), 2503, 2147, 464);
		else if (destination.equalsIgnoreCase("Rhonnam"))
			goTo(player, WorldMapType.GELKMAROS.getId(), 845, 1737, 354);
		else if (destination.equalsIgnoreCase("haramel") || destination.equalsIgnoreCase("Haramel"))
			goTo(player, 300200000, 176, 21, 144);
		else if (destination.equalsIgnoreCase("nochsana") || destination.equalsIgnoreCase("Nochsana Training Camp"))
			goTo(player, 300030000, 513, 668, 331);
		else if (destination.equalsIgnoreCase("arcanis") || destination.equalsIgnoreCase("Sky Temple Of Arcanis"))
			goTo(player, 320050000, 177, 229, 536);
		else if (destination.equalsIgnoreCase("firetemple") || destination.equalsIgnoreCase("Fire Temple"))
			goTo(player, 320100000, 148, 461, 141);
		else if (destination.equalsIgnoreCase("kromede") || destination.equalsIgnoreCase("Kromede Trial"))
			goTo(player, 300230000, 248, 244, 189);
		else if (destination.equalsIgnoreCase("steelrake") || destination.equalsIgnoreCase("Steel Rake"))
			goTo(player, 300100000, 237, 506, 948);
		else if (destination.equalsIgnoreCase("indratu") || destination.equalsIgnoreCase("Indratu Fortress"))
			goTo(player, 310090000, 562, 335, 1015);
		else if (destination.equalsIgnoreCase("azoturan") || destination.equalsIgnoreCase("Azoturan Fortress"))
			goTo(player, 310100000, 300, 338, 1018);
		else if (destination.equalsIgnoreCase("aethero") || destination.equalsIgnoreCase("Aetherogenetics Lab"))
			goTo(player, 310050000, 360, 230, 147);
		else if (destination.equalsIgnoreCase("adma") || destination.equalsIgnoreCase("Adma Stronghold"))
			goTo(player, 320130000, 450, 200, 168);
		else if (destination.equalsIgnoreCase("alquimia") || destination.equalsIgnoreCase("Alquimia Research Center"))
			goTo(player, 320110000, 603, 527, 200);
		else if (destination.equalsIgnoreCase("draupnir") || destination.equalsIgnoreCase("Draupnir Cave"))
			goTo(player, 320080000, 491, 373, 622);
		else if (destination.equalsIgnoreCase("theobomoslab") || destination.equalsIgnoreCase("Theobomos Research Lab"))
			goTo(player, 310110000, 477, 201, 170);
		else if (destination.equalsIgnoreCase("dp") || destination.equalsIgnoreCase("Dark Poeta"))
			goTo(player, 300040000, 1214, 412, 140);
		else if (destination.equalsIgnoreCase("sulfur") || destination.equalsIgnoreCase("Sulfur Tree Nest"))
			goTo(player, 300060000, 462, 345, 163);
		else if (destination.equalsIgnoreCase("left") || destination.equalsIgnoreCase("Left Wing Chamber"))
			goTo(player, 300080000, 672, 606, 321);
		else if (destination.equalsIgnoreCase("right") || destination.equalsIgnoreCase("Right Wing Chamber"))
			goTo(player, 300090000, 263, 386, 103);
		else if (destination.equalsIgnoreCase("carpus") || destination.equalsIgnoreCase("Carpus Isle Storeroom")) //4.9
			goTo(player, 300050000, 469, 568, 202);
		else if (destination.equalsIgnoreCase("twilight") || destination.equalsIgnoreCase("Twilight Battlefield Storeroom")) //4.9
			goTo(player, 300130000, 527, 120, 176);
		else if (destination.equalsIgnoreCase("grave") || destination.equalsIgnoreCase("Grave Of Steel Storeroom")) //4.9
			goTo(player, 300120000, 528, 121, 176);
		else if (destination.equalsIgnoreCase("isle") || destination.equalsIgnoreCase("Isle Of Roots Storeroom")) //4.9
			goTo(player, 300140000, 528, 109, 176);
		else if (destination.equalsIgnoreCase("hamate") || destination.equalsIgnoreCase("Hamate Isle Storeroom")) //4.9
			goTo(player, 300070000, 504, 396, 94);
		else if (destination.equalsIgnoreCase("baranath") || destination.equalsIgnoreCase("Baranath Dredgion"))
			goTo(player, 300110000, 399, 169, 432);
		else if (destination.equalsIgnoreCase("chantra") || destination.equalsIgnoreCase("Chantra Dredgion"))
			goTo(player, 300210000, 399, 169, 432);
		else if (destination.equalsIgnoreCase("taloc") || destination.equalsIgnoreCase("Taloc's Hollow"))
			goTo(player, 300190000, 200, 214, 1099);
		else if (destination.equalsIgnoreCase("udas") || destination.equalsIgnoreCase("Udas Temple"))
			goTo(player, 300150000, 637, 657, 134);
		else if (destination.equalsIgnoreCase("udas2") || destination.equalsIgnoreCase("Lower Udas Temple"))
			goTo(player, 300160000, 1146, 277, 116);
		else if (destination.equalsIgnoreCase("besh") || destination.equalsIgnoreCase("Beshmundir Temple"))
			goTo(player, 300170000, 1477, 237, 243);
		else if (destination.equalsIgnoreCase("padma") || destination.equalsIgnoreCase("Padmarashka Cave"))
			goTo(player, 320150000, 385, 506, 66);
		else if (destination.equalsIgnoreCase("karamatisa") || destination.equalsIgnoreCase("Karamatis A"))
			goTo(player, 310010000, 221, 250, 206);
		else if (destination.equalsIgnoreCase("karamatisb") || destination.equalsIgnoreCase("Karamatis B"))
			goTo(player, 310020000, 312, 274, 206);
		else if (destination.equalsIgnoreCase("karamatisc") || destination.equalsIgnoreCase("Karamatis C"))
			goTo(player, 310120000, 221, 250, 206);
		else if (destination.equalsIgnoreCase("aerdina") || destination.equalsIgnoreCase("Aerdina"))
			goTo(player, 310030000, 275, 168, 205);
		else if (destination.equalsIgnoreCase("geranaia") || destination.equalsIgnoreCase("Geranaia"))
			goTo(player, 310040000, 275, 168, 205);
		else if (destination.equalsIgnoreCase("sliver") || destination.equalsIgnoreCase("Sliver Of Darkness"))
			goTo(player, 310070000, 247, 249, 1392);
		else if (destination.equalsIgnoreCase("space") || destination.equalsIgnoreCase("Space Of Destiny"))
			goTo(player, 320070000, 246, 246, 125);
		else if (destination.equalsIgnoreCase("ataxiar a") || destination.equalsIgnoreCase("Ataxiar A"))
			goTo(player, 320010000, 221, 250, 206);
		else if (destination.equalsIgnoreCase("ataxiar b") || destination.equalsIgnoreCase("Ataxiar B"))
			goTo(player, 320020000, 221, 250, 206);
		else if (destination.equalsIgnoreCase("bregirun") || destination.equalsIgnoreCase("Bregirun"))
			goTo(player, 320030000, 275, 168, 205);
		else if (destination.equalsIgnoreCase("nidalber") || destination.equalsIgnoreCase("Nidalber"))
			goTo(player, 320040000, 275, 168, 205);
		else if (destination.equalsIgnoreCase("sanctum arena") || destination.equalsIgnoreCase("Sanctum Undeground Arena"))
			goTo(player, 310080000, 275, 242, 159);
		else if (destination.equalsIgnoreCase("triniel arena") || destination.equalsIgnoreCase("Triniel Undeground Arena"))
			goTo(player, 320090000, 275, 239, 159);
		else if (destination.equalsIgnoreCase("prisone") || destination.equalsIgnoreCase("Prison Elyos"))
			goTo(player, 510010000, 256, 256, 49);
		else if (destination.equalsIgnoreCase("prisona") || destination.equalsIgnoreCase("Prison Asmos"))
			goTo(player, 520010000, 256, 256, 49);
		else if (destination.equalsIgnoreCase("IDAbPro"))
			goTo(player, 300010000, 270, 200, 206);
		else if (destination.equalsIgnoreCase("gm"))
			goTo(player, 900180000, 469, 390, 103);
		else if (destination.equalsIgnoreCase("kaisinel2"))
			goTo(player, 110070000, 459, 251, 128);
		else if (destination.equalsIgnoreCase("marchutan2"))
			goTo(player, 120080000, 577, 250, 94);
		else if (destination.equalsIgnoreCase("esoterrace") || destination.equalsIgnoreCase("Esoterrace"))
			goTo(player, 300250000, 333, 437, 326);
		else if (destination.equalsIgnoreCase("aturam") || destination.equalsIgnoreCase("Aturam Sky Fortress"))
			goTo(player, 300240000, 684, 460, 655);
		else if (destination.equalsIgnoreCase("rentus") || destination.equalsIgnoreCase("Rentus Base"))
			goTo(player, 300280000, 564, 595, 153);
		else if (destination.equalsIgnoreCase("crucible") || destination.equalsIgnoreCase("Empyrean Crucible")) //2.5
			goTo(player, 300300000, 354, 350, 96);
		else if (destination.equalsIgnoreCase("crucible2") || destination.equalsIgnoreCase("Crucible Challenge")) //2.6
			goTo(player, 300320000, 357, 1662, 95);
		else if (destination.equalsIgnoreCase("chaos") || destination.equalsIgnoreCase("Arena Of Chaos")) //2.7
			goTo(player, 300350000, 1332, 1078, 340);
		else if (destination.equalsIgnoreCase("discipline") || destination.equalsIgnoreCase("Arena Of Discipline")) //2.7
			goTo(player, 300360000, 707, 1779, 165);
		else if (destination.equalsIgnoreCase("chaos2") || destination.equalsIgnoreCase("Chaos Training Grounds")) //2.7
			goTo(player, 300420000, 1332, 1078, 340);
		else if (destination.equalsIgnoreCase("discipline2") || destination.equalsIgnoreCase("Discipline Training Grounds")) //2.7
			goTo(player, 300430000, 707, 1779, 165);
		else if (destination.equalsIgnoreCase("terath") || destination.equalsIgnoreCase("Terath Dredgion"))
			goTo(player, 300440000, 399, 169, 432);
		else if (destination.equalsIgnoreCase("harmony") || destination.equalsIgnoreCase("Arena Of Harmony")) //3.9
			goTo(player, 300450000, 500, 371, 211);
		else if (destination.equalsIgnoreCase("cabin") || destination.equalsIgnoreCase("Steel Rake Cabin"))
			goTo(player, 300460000, 248, 244, 189);	
		else if (destination.equalsIgnoreCase("sealed") || destination.equalsIgnoreCase("Sealed Danuar Mysticarium")) //4.8
			goTo(player, 300480000, 179, 122, 231);
		else if (destination.equalsIgnoreCase("stronghold") || destination.equalsIgnoreCase("Tiamat Stronghold")) //3.9
			goTo(player, 300510000, 1581, 1068, 492);
		else if (destination.equalsIgnoreCase("refuge") || destination.equalsIgnoreCase("Dragon Lord Refuge")) //3.9
			goTo(player, 300520000, 503, 524, 240);
		else if (destination.equalsIgnoreCase("bastion") || destination.equalsIgnoreCase("Eternal Bastion")) //4.3
			goTo(player, 300540000, 743, 293, 233);
		else if (destination.equalsIgnoreCase("glory") || destination.equalsIgnoreCase("Arena Of Glory")) //3.9
			goTo(player, 300550000, 500, 371, 211);
		else if (destination.equalsIgnoreCase("shugo") || destination.equalsIgnoreCase("Shugo Imperial Tomb")) //3.9
			goTo(player, 300560000, 152, 199, 538);
		else if (destination.equalsIgnoreCase("harmonyTraining") || destination.equalsIgnoreCase("Harmony Training Grounds")) //3.9
			goTo(player, 300570000, 500, 371, 211);
		else if (destination.equalsIgnoreCase("ophidan") || destination.equalsIgnoreCase("Ophidan Bridge")) //4.3
			goTo(player, 300590000, 769, 558, 572);
		else if (destination.equalsIgnoreCase("raksang") || destination.equalsIgnoreCase("Raksang Ruins")) //4.8
			goTo(player, 300610000, 851, 947, 1206);
		else if (destination.equalsIgnoreCase("rentus2") || destination.equalsIgnoreCase("[Occupied] Rentus Base")) //4.8
			goTo(player, 300620000, 573, 596, 153);
		else if (destination.equalsIgnoreCase("refuge2") || destination.equalsIgnoreCase("[Anguished] Dragon Lord's Refuge")) //4.8
			goTo(player, 300630000, 503, 524, 240);
		else if (destination.equalsIgnoreCase("hexway") || destination.equalsIgnoreCase("The Hexway")) //3.9
			goTo(player, 300700000, 672, 606, 321);
		else if (destination.equalsIgnoreCase("infinity") || destination.equalsIgnoreCase("Infinity Shard")) //4.3
			goTo(player, 300800000, 118, 115, 131);
		else if (destination.equalsIgnoreCase("unity") || destination.equalsIgnoreCase("Unity Training Grounds")) //3.7
			goTo(player, 301100000, 500, 371, 211);
		else if (destination.equalsIgnoreCase("reliquary") || destination.equalsIgnoreCase("Danuar Reliquary")) //4.3
			goTo(player, 301110000, 256, 257, 241);
		else if (destination.equalsIgnoreCase("kamar") || destination.equalsIgnoreCase("Kamar Battlefield")) //4.3
			goTo(player, 301120000, 1374, 1455, 600);
		else if (destination.equalsIgnoreCase("sauro") || destination.equalsIgnoreCase("Sauro Supply Base")) //4.3
			goTo(player, 301130000, 641, 176, 195);
		else if (destination.equalsIgnoreCase("seized") || destination.equalsIgnoreCase("Seized Danuar Sanctuary")) //4.3
			goTo(player, 301140000, 388, 1184, 55);
		else if (destination.equalsIgnoreCase("asteriasolo") || destination.equalsIgnoreCase("Asteria IU Solo")) //4.3
			goTo(player, 301150000, 500, 500, 500);
		else if (destination.equalsIgnoreCase("circus") || destination.equalsIgnoreCase("Nightmare Circus")) //4.3
			goTo(player, 301160000, 472, 564, 202);
		else if (destination.equalsIgnoreCase("circus2") || destination.equalsIgnoreCase("The Nightmare Circus")) //4.3
			goTo(player, 301200000, 472, 564, 202);
		else if (destination.equalsIgnoreCase("engulfed") || destination.equalsIgnoreCase("Engulfed Ophidan Bridge")) //4.5
			goTo(player, 301210000, 690, 469, 599);
		else if (destination.equalsIgnoreCase("iron") || destination.equalsIgnoreCase("Iron Wall Warfront")) //4.5
			goTo(player, 301220000, 743, 293, 233);
		else if (destination.equalsIgnoreCase("obelisk") || destination.equalsIgnoreCase("Illuminary Obelisk")) //4.5
			goTo(player, 301230000, 321, 323, 405);
		else if (destination.equalsIgnoreCase("kysis2") || destination.equalsIgnoreCase("[Hidden] Kysis Barracks")) //4.9.1
			goTo(player, 301240000, 528, 121, 176);
		else if (destination.equalsIgnoreCase("miren2") || destination.equalsIgnoreCase("[Hidden] Miren Barracks")) //4.9.1
			goTo(player, 301250000, 527, 120, 176);
		else if (destination.equalsIgnoreCase("krotan2") || destination.equalsIgnoreCase("[Hidden] Krotan Barracks")) //4.9.1
			goTo(player, 301260000, 528, 109, 176);
		else if (destination.equalsIgnoreCase("foundry") || destination.equalsIgnoreCase("Linkgate Foundry")) //4.7
			goTo(player, 301270000, 357, 259, 311);
		else if (destination.equalsIgnoreCase("kysis3") || destination.equalsIgnoreCase("Kysis Barracks")) //4.5
			goTo(player, 301280000, 528, 121, 176);
		else if (destination.equalsIgnoreCase("miren3") || destination.equalsIgnoreCase("Miren Barracks")) //4.5
			goTo(player, 301290000, 527, 120, 176);
		else if (destination.equalsIgnoreCase("krotan3") || destination.equalsIgnoreCase("Krotan Barracks")) //4.5
			goTo(player, 301300000, 528, 109, 176);
		else if (destination.equalsIgnoreCase("dome") || destination.equalsIgnoreCase("Idgel Dome")) //4.7
			goTo(player, 301310000, 261, 262, 85);
		else if (destination.equalsIgnoreCase("ophidan2") || destination.equalsIgnoreCase("Lucky Ophidan Bridge")) //4.5
			goTo(player, 301320000, 769, 558, 572);
		else if (destination.equalsIgnoreCase("reliquary2") || destination.equalsIgnoreCase("Lucky Danuar Reliquary")) //4.5
			goTo(player, 301330000, 256, 257, 241);
		else if (destination.equalsIgnoreCase("foundryquest") || destination.equalsIgnoreCase("Linkgate Foundry Quest")) //4.7
			goTo(player, 301340000, 241, 332, 91);
		else if (destination.equalsIgnoreCase("foundry2") || destination.equalsIgnoreCase("[Legion] Linkgate Foundry")) //4.7
			goTo(player, 301350000, 357, 259, 311);
		else if (destination.equalsIgnoreCase("reliquary3") || destination.equalsIgnoreCase("[Infernal] Danuar Reliquary")) //4.7
			goTo(player, 301360000, 256, 257, 241);
		else if (destination.equalsIgnoreCase("obelisk2") || destination.equalsIgnoreCase("[Infernal] Illuminary Obelisk")) //4.7
			goTo(player, 301370000, 321, 323, 405);
		else if (destination.equalsIgnoreCase("sanctuary2") || destination.equalsIgnoreCase("Danuar Sanctuary")) //4.8
			goTo(player, 301380000, 388, 1184, 55);
		else if (destination.equalsIgnoreCase("drakenspire") || destination.equalsIgnoreCase("Drakenspire Depths")) //4.8
			goTo(player, 301390000, 327, 183, 1687);
		else if (destination.equalsIgnoreCase("vault") || destination.equalsIgnoreCase("The Shugo Emperor's Vault")) //4.7.5
			goTo(player, 301400000, 543, 294, 400);
		else if (destination.equalsIgnoreCase("reach") || destination.equalsIgnoreCase("Stonespear Reach")) //4.8
			goTo(player, 301500000, 219, 268, 96);
		else if (destination.equalsIgnoreCase("manor") || destination.equalsIgnoreCase("Sealed Argent Manor")) //4.9.1
			goTo(player, 301510000, 995, 1207, 65);
		else if (destination.equalsIgnoreCase("drakenspire2") || destination.equalsIgnoreCase("Drakenspire Depths")) //4.9.1
			goTo(player, 301520000, 327, 183, 1687);
		else if (destination.equalsIgnoreCase("archives") || destination.equalsIgnoreCase("Archives Of Eternity")) //5.0
			goTo(player, 301540000, 737, 511, 469);
		else if (destination.equalsIgnoreCase("cradle") || destination.equalsIgnoreCase("Cradle Of Eternity")) //5.1
			goTo(player, 301550000, 1477, 774, 1035);
		else if (destination.equalsIgnoreCase("archives2") || destination.equalsIgnoreCase("Archives Of Eternity")) //5.0
			goTo(player, 301570000, 690, 432, 468);
		else if (destination.equalsIgnoreCase("vault2") || destination.equalsIgnoreCase("Emperor Trillirunerk's Safe")) //4.9.1
			goTo(player, 301590000, 543, 294, 400);
		else if (destination.equalsIgnoreCase("adma2") || destination.equalsIgnoreCase("Adma's Fall")) //5.0
			goTo(player, 301600000, 480, 462, 173);
		else if (destination.equalsIgnoreCase("lap") || destination.equalsIgnoreCase("Theobomos Test Chamber")) //5.0
			goTo(player, 301610000, 219, 357, 202);
		else if (destination.equalsIgnoreCase("dragon") || destination.equalsIgnoreCase("Drakenseer Lair")) //5.0
			goTo(player, 301620000, 275, 349, 336);
		/////////////////////5.0.5/////////////////
		else if (destination.equalsIgnoreCase("underpath") || destination.equalsIgnoreCase("Contaminated Underpath")) //5.0.5
			goTo(player, 301630000, 230, 169, 164);
		else if (destination.equalsIgnoreCase("factory") || destination.equalsIgnoreCase("Secret Munitions Factory")) //5.0.5
			goTo(player, 301640000, 407, 292, 198);
		/////////////////////5.1///////////////////
		else if (destination.equalsIgnoreCase("dred4") || destination.equalsIgnoreCase("Ashunatal Dredgion")) //5.1
			goTo(player, 301650000, 399, 169, 432);
		else if (destination.equalsIgnoreCase("fallen") || destination.equalsIgnoreCase("Fallen Poeta")) //5.1
			goTo(player, 301660000, 286, 972, 107);
		else if (destination.equalsIgnoreCase("warpath") || destination.equalsIgnoreCase("Ophidan Warpath")) //5.1
			goTo(player, 301670000, 690, 469, 599);
		else if (destination.equalsIgnoreCase("landmark") || destination.equalsIgnoreCase("Idgel Dome Landmark")) //5.1
			goTo(player, 301680000, 261, 262, 85);
		else if (destination.equalsIgnoreCase("firetemple2") || destination.equalsIgnoreCase("Old Fire Temple")) //5.1
			goTo(player, 302000000, 148, 461, 141);
		else if (destination.equalsIgnoreCase("oblivion") || destination.equalsIgnoreCase("Fissure Of Oblivion")) //5.1
			goTo(player, 302100000, 916, 461, 352);
		else if (destination.equalsIgnoreCase("annex") || destination.equalsIgnoreCase("Transidium Annex")) //4.7
			goTo(player, 400030000, 287, 290, 680);
		//**Zones**//
		else if (destination.equalsIgnoreCase("silentera"))
			goTo(player, 600010000, 583, 767, 300);
		//**4.3**//
		else if (destination.equalsIgnoreCase("iu"))
			goTo(player, 600080000, 1510, 1511, 565);
		//**4.7**//
		else if (destination.equalsIgnoreCase("kaldor"))
			goTo(player, 600090000, 743, 1156, 148);
		else if (destination.equalsIgnoreCase("levinshor"))
			goTo(player, 600100000, 207, 183, 374);
		//**Panesterra**//
		else if (destination.equalsIgnoreCase("belus"))
			goTo(player, 400020000, 1238, 1232, 1518);
		else if (destination.equalsIgnoreCase("aspida"))
			goTo(player, 400040000, 1238, 1232, 1518);
		else if (destination.equalsIgnoreCase("atanatos"))
			goTo(player, 400050000, 1238, 1232, 1518);
		else if (destination.equalsIgnoreCase("disillon"))
			goTo(player, 400060000, 1238, 1232, 1518);
		else if (destination.equalsIgnoreCase("oriel"))
			goTo(player, 700010000, 1261, 1845, 98);
		else if (destination.equalsIgnoreCase("pernon"))
			goTo(player, 710010000, 1069, 1539, 98);
		else if (destination.equalsIgnoreCase("wisplight")) //4.7.2
			goTo(player, 130090000, 247, 236, 129);
		else if (destination.equalsIgnoreCase("fatebound")) //4.7.2
			goTo(player, 140010000, 272, 266, 96);
		//**4.8**//
		else if (destination.equalsIgnoreCase("cygnea")) //4.8
			goTo(player, 210070000, 2917, 838, 569);
		else if (destination.equalsIgnoreCase("griffoen")) //4.8
			goTo(player, 210080000, 263, 199, 498);
		else if (destination.equalsIgnoreCase("enshar")) //4.8
			goTo(player, 220080000, 470, 2341, 216);
		else if (destination.equalsIgnoreCase("habrok")) //4.8
			goTo(player, 220090000, 252, 170, 504);
		else if (destination.equalsIgnoreCase("idianely")) //4.8
			goTo(player, 210090000, 701, 693, 514);
		else if (destination.equalsIgnoreCase("idianasmo")) //4.8
			goTo(player, 220100000, 701, 693, 514);
		//**4.9.1**//
		else if (destination.equalsIgnoreCase("teminon")) //4.9.1
			goTo(player, 400010000, 2867, 1034, 1528);
		else if (destination.equalsIgnoreCase("primum")) //4.9.1
			goTo(player, 400010000, 1078, 2839, 1636);
		else if (destination.equalsIgnoreCase("redemption")) //4.9.1
			goTo(player, 400010000, 2404, 391, 2938);
		else if (destination.equalsIgnoreCase("harbinger")) //4.9.1
			goTo(player, 400010000, 1292, 3123, 3034);
		//**5.0**//
		else if (destination.equalsIgnoreCase("iluma")) //5.0
			goTo(player, 210100000, 1414, 1281, 336);
		else if (destination.equalsIgnoreCase("tower1")) //5.0
			goTo(player, 210110000, 474, 499, 303);
		else if (destination.equalsIgnoreCase("norsvold")) //5.0
			goTo(player, 220110000, 1807, 1986, 197);
		else if (destination.equalsIgnoreCase("tower2")) //5.0
			goTo(player, 220120000, 474, 499, 303);
		//**Map Test**//
		else if (destination.equalsIgnoreCase("art") || destination.equalsIgnoreCase("Test Server Art"))
			goTo(player, 900180000, 508, 540, 100);
		else if (destination.equalsIgnoreCase("tag") || destination.equalsIgnoreCase("Test Tag Match"))
			goTo(player, 900190000, 96, 114, 20);
		else if (destination.equalsIgnoreCase("attack") || destination.equalsIgnoreCase("Test Time Attack"))
			goTo(player, 900200000, 96, 114, 20);
		else if (destination.equalsIgnoreCase("stage") || destination.equalsIgnoreCase("Tournament Stage Test"))
			goTo(player, 900210000, 276, 242, 37);
		else if (destination.equalsIgnoreCase("lobby") || destination.equalsIgnoreCase("Tournament Lobby Test"))
			goTo(player, 900230000, 276, 242, 37);
		else
			PacketSendUtility.sendMessage(player, "Could not find the specified destination !");
	}
	
	private static void goTo(final Player player, int worldId, float x, float y, float z) {
		WorldMap destinationMap = World.getInstance().getWorldMap(worldId);
		if (destinationMap.isInstanceType()) {
			TeleportService2.teleportTo(player, worldId, getInstanceId(worldId, player), x, y, z);
		} else {
			TeleportService2.teleportTo(player, worldId, x, y, z);
		}
	}
	
	private static int getInstanceId(int worldId, Player player) {
		if (player.getWorldId() == worldId)	{
			WorldMapInstance registeredInstance = InstanceService.getRegisteredInstance(worldId, player.getObjectId());
			if (registeredInstance != null) {
				return registeredInstance.getInstanceId();
			}
		}
		WorldMapInstance newInstance = InstanceService.getNextAvailableInstance(worldId);
		InstanceService.registerPlayerWithInstance(newInstance, player);
		return newInstance.getInstanceId();
	}
	
	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "Syntax : //goto <location>");
	}
}