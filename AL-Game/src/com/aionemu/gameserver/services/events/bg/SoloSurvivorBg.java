/*
 * This file is part of Encom. **ENCOM FUCK OTHER SVN**
 *
 *  Encom is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Encom is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser Public License
 *  along with Encom.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.services.events.bg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.model.team2.group.PlayerGroupService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * Created by wanke on 12/02/2017.
 */

public class SoloSurvivorBg extends Battleground
{
    private int maxRounds = 3;
    private int roundsDone = 0;
    private boolean endCalled = false;
    private static final Logger log = LoggerFactory.getLogger(SoloSurvivorBg.class);
	
    public SoloSurvivorBg() {
        super.name = "[1 VS 1]";
        super.description = "You must kill your opponent to win. You have " + maxRounds + " round. The player who gets the most kill's, win the match !!!";
        super.minSize = 2;
        super.maxSize = 4;
        super.teamCount = 1;
        super.matchLength = 150;
		
		//Nochsana Training Camp.
		BattlegroundMap map1 = new BattlegroundMap(300030000);
		map1.addSpawn(new SpawnPosition(331f, 272f, 384f));
		map1.addSpawn(new SpawnPosition(314f, 325f, 380f));
		map1.addSpawn(new SpawnPosition(366f, 320f, 380f));
		map1.addSpawn(new SpawnPosition(308f, 280f, 392f));
		map1.addSpawn(new SpawnPosition(356f, 271f, 392f));
		map1.addSpawn(new SpawnPosition(343f, 339f, 379f));
		map1.setKillZ(350f);
        super.maps.add(map1);
		//Dark Poeta.
		BattlegroundMap map2 = new BattlegroundMap(300040000);
		map2.addSpawn(new SpawnPosition(1193f, 1255f, 139f));
		map2.addSpawn(new SpawnPosition(1166f, 1215f, 144f));
		map2.addSpawn(new SpawnPosition(1167f, 1245f, 143f));
		map2.addSpawn(new SpawnPosition(1199f, 1223f, 143f));
		map2.addSpawn(new SpawnPosition(1183f, 1232f, 143f));
		map2.addSpawn(new SpawnPosition(1177f, 1261f, 139f));
		map2.setKillZ(110f);
        super.maps.add(map2);
		//Carpus Isle Storeroom.
		BattlegroundMap map3 = new BattlegroundMap(300050000);
		map3.addSpawn(new SpawnPosition(484f, 566f, 201f));
		map3.addSpawn(new SpawnPosition(524f, 591f, 199f));
		map3.addSpawn(new SpawnPosition(556f, 564f, 198f));
		map3.addSpawn(new SpawnPosition(521f, 536f, 199f));
		map3.addSpawn(new SpawnPosition(514f, 564f, 197f));
		map3.addSpawn(new SpawnPosition(530f, 563f, 197f));
		map3.setKillZ(170f);
        super.maps.add(map3);
		//Sulfur Tree Nest.
		BattlegroundMap map4 = new BattlegroundMap(300060000);
		map4.addSpawn(new SpawnPosition(476f, 418f, 163f));
		map4.addSpawn(new SpawnPosition(435f, 440f, 162f));
		map4.addSpawn(new SpawnPosition(430f, 486f, 162f));
		map4.addSpawn(new SpawnPosition(480f, 504f, 162f));
		map4.addSpawn(new SpawnPosition(485f, 460f, 162f));
		map4.addSpawn(new SpawnPosition(453f, 472f, 163f));
		map4.setKillZ(140f);
        super.maps.add(map4);
		//Hamate Isle Storeroom.
		BattlegroundMap map5 = new BattlegroundMap(300070000);
		map5.addSpawn(new SpawnPosition(504f, 423f, 91f));
		map5.addSpawn(new SpawnPosition(503f, 460f, 86f));
		map5.addSpawn(new SpawnPosition(481f, 483f, 87f));
		map5.addSpawn(new SpawnPosition(528f, 483f, 87f));
		map5.addSpawn(new SpawnPosition(504f, 503f, 88f));
		map5.addSpawn(new SpawnPosition(503f, 479f, 87f));
		map5.setKillZ(70f);
        super.maps.add(map5);
		//Left Wing Chamber.
		BattlegroundMap map6 = new BattlegroundMap(300080000);
		map6.addSpawn(new SpawnPosition(488f, 512f, 352f));
		map6.addSpawn(new SpawnPosition(495f, 548f, 354f));
		map6.addSpawn(new SpawnPosition(458f, 530f, 352f));
		map6.addSpawn(new SpawnPosition(485f, 585f, 355f));
		map6.addSpawn(new SpawnPosition(495f, 623f, 354f));
		map6.addSpawn(new SpawnPosition(451f, 618f, 352f));
		map6.setKillZ(320f);
        super.maps.add(map6);
		//Right Wing Chamber.
		BattlegroundMap map7 = new BattlegroundMap(300090000);
		map7.addSpawn(new SpawnPosition(293f, 248f, 102f));
		map7.addSpawn(new SpawnPosition(295f, 189f, 102f));
		map7.addSpawn(new SpawnPosition(232f, 187f, 102f));
		map7.addSpawn(new SpawnPosition(232f, 249f, 102f));
		map7.addSpawn(new SpawnPosition(255f, 228f, 102f));
		map7.addSpawn(new SpawnPosition(270f, 201f, 102f));
		map7.setKillZ(80f);
        super.maps.add(map7);
		//Steel Rake.
		BattlegroundMap map8 = new BattlegroundMap(300100000);
		map8.addSpawn(new SpawnPosition(568f, 489f, 1023f));
		map8.addSpawn(new SpawnPosition(568f, 528f, 1023f));
		map8.addSpawn(new SpawnPosition(544f, 527f, 1023f));
		map8.addSpawn(new SpawnPosition(545f, 489f, 1023f));
		map8.addSpawn(new SpawnPosition(592f, 489f, 1023f));
		map8.addSpawn(new SpawnPosition(592f, 528f, 1023f));
		map8.setKillZ(990f);
        super.maps.add(map8);
		//Baranath Dredgion.
		BattlegroundMap map9 = new BattlegroundMap(300110000);
		map9.addSpawn(new SpawnPosition(485f, 857f, 417f));
		map9.addSpawn(new SpawnPosition(485f, 877f, 405f));
		map9.addSpawn(new SpawnPosition(513f, 889f, 405f));
		map9.addSpawn(new SpawnPosition(457f, 889f, 405f));
		map9.addSpawn(new SpawnPosition(485f, 909f, 405f));
		map9.addSpawn(new SpawnPosition(485f, 814f, 416f));
		map9.setKillZ(380f);
        super.maps.add(map9);
		//Grave Of Steel Storeroom.
		BattlegroundMap map10 = new BattlegroundMap(300120000);
		map10.addSpawn(new SpawnPosition(496f, 826f, 199f));
		map10.addSpawn(new SpawnPosition(492f, 851f, 199f));
		map10.addSpawn(new SpawnPosition(504f, 873f, 199f));
		map10.addSpawn(new SpawnPosition(528f, 881f, 199f));
		map10.addSpawn(new SpawnPosition(552f, 873f, 199f));
		map10.addSpawn(new SpawnPosition(564f, 851f, 199f));
		map10.setKillZ(170f);
        super.maps.add(map10);
		//Twilight Battlefield Storeroom.
		BattlegroundMap map11 = new BattlegroundMap(300130000);
		map11.addSpawn(new SpawnPosition(496f, 826f, 199f));
		map11.addSpawn(new SpawnPosition(492f, 851f, 199f));
		map11.addSpawn(new SpawnPosition(504f, 873f, 199f));
		map11.addSpawn(new SpawnPosition(528f, 881f, 199f));
		map11.addSpawn(new SpawnPosition(552f, 873f, 199f));
		map11.addSpawn(new SpawnPosition(564f, 851f, 199f));
		map11.setKillZ(170f);
        super.maps.add(map11);
		//Isle Of Roots Storeroom.
		BattlegroundMap map12 = new BattlegroundMap(300140000);
		map12.addSpawn(new SpawnPosition(496f, 826f, 199f));
		map12.addSpawn(new SpawnPosition(492f, 851f, 199f));
		map12.addSpawn(new SpawnPosition(504f, 873f, 199f));
		map12.addSpawn(new SpawnPosition(528f, 881f, 199f));
		map12.addSpawn(new SpawnPosition(552f, 873f, 199f));
		map12.addSpawn(new SpawnPosition(564f, 851f, 199f));
		map12.setKillZ(170f);
        super.maps.add(map12);
		//Udas Temple.
		BattlegroundMap map13 = new BattlegroundMap(300150000);
		map13.addSpawn(new SpawnPosition(655f, 506f, 136f));
		map13.addSpawn(new SpawnPosition(617f, 501f, 136f));
		map13.addSpawn(new SpawnPosition(617f, 456f, 136f));
		map13.addSpawn(new SpawnPosition(656f, 457f, 136f));
		map13.addSpawn(new SpawnPosition(636f, 435f, 137f));
		map13.addSpawn(new SpawnPosition(636f, 518f, 131f));
		map13.setKillZ(110f);
        super.maps.add(map13);
		//Lower Udas Temple.
		BattlegroundMap map14 = new BattlegroundMap(300160000);
		map14.addSpawn(new SpawnPosition(571f, 1297f, 187f));
		map14.addSpawn(new SpawnPosition(566f, 1242f, 188f));
		map14.addSpawn(new SpawnPosition(572f, 1344f, 188f));
		map14.addSpawn(new SpawnPosition(636f, 1385f, 186f));
		map14.addSpawn(new SpawnPosition(658f, 1297f, 186f));
		map14.addSpawn(new SpawnPosition(640f, 1215f, 186f));
		map14.setKillZ(150f);
        super.maps.add(map14);
		//Beshmundir Temple.
		BattlegroundMap map15 = new BattlegroundMap(300170000);
		map15.addSpawn(new SpawnPosition(1505f, 1463f, 304f));
		map15.addSpawn(new SpawnPosition(1441f, 1378f, 305f));
		map15.addSpawn(new SpawnPosition(1511f, 1385f, 307f));
		map15.addSpawn(new SpawnPosition(1428f, 1448f, 307f));
		map15.addSpawn(new SpawnPosition(1533f, 1433f, 300f));
		map15.addSpawn(new SpawnPosition(1468f, 1483f, 300f));
		map15.setKillZ(280f);
        super.maps.add(map15);
		//Taloc's Hollow.
		BattlegroundMap map16 = new BattlegroundMap(300190000);
		map16.addSpawn(new SpawnPosition(392f, 897f, 1266f));
		map16.addSpawn(new SpawnPosition(442f, 919f, 1274f));
		map16.addSpawn(new SpawnPosition(434f, 878f, 1276f));
		map16.addSpawn(new SpawnPosition(387f, 862f, 1264f));
		map16.addSpawn(new SpawnPosition(429f, 934f, 1266f));
		map16.addSpawn(new SpawnPosition(382f, 842f, 1271f));
		map16.setKillZ(1190f);
        super.maps.add(map16);
		//Haramel.
		BattlegroundMap map17 = new BattlegroundMap(300200000);
		map17.addSpawn(new SpawnPosition(387f, 315f, 88f));
		map17.addSpawn(new SpawnPosition(376f, 285f, 89f));
		map17.addSpawn(new SpawnPosition(347f, 287f, 90f));
		map17.addSpawn(new SpawnPosition(344f, 331f, 87f));
		map17.addSpawn(new SpawnPosition(356f, 367f, 90f));
		map17.addSpawn(new SpawnPosition(327f, 380f, 89f));
		map17.setKillZ(50f);
        super.maps.add(map17);
		//Chantra Dredgion.
		BattlegroundMap map18 = new BattlegroundMap(300210000);
		map18.addSpawn(new SpawnPosition(458f, 493f, 397f));
		map18.addSpawn(new SpawnPosition(514f, 493f, 397f));
		map18.addSpawn(new SpawnPosition(486f, 455f, 398f));
		map18.addSpawn(new SpawnPosition(484f, 527f, 396f));
		map18.addSpawn(new SpawnPosition(483f, 496f, 397f));
		map18.addSpawn(new SpawnPosition(484f, 420f, 398f));
		map18.setKillZ(370f);
        super.maps.add(map18);
		//Kromede Trial.
		BattlegroundMap map19 = new BattlegroundMap(300230000);
		map19.addSpawn(new SpawnPosition(528f, 640f, 201f));
		map19.addSpawn(new SpawnPosition(493f, 640f, 201f));
		map19.addSpawn(new SpawnPosition(513f, 610f, 201f));
		map19.addSpawn(new SpawnPosition(512f, 670f, 201f));
		map19.addSpawn(new SpawnPosition(557f, 640f, 206f));
		map19.addSpawn(new SpawnPosition(531f, 612f, 201f));
		map19.setKillZ(180f);
        super.maps.add(map19);
		//Aturam Sky Fortress.
		BattlegroundMap map20 = new BattlegroundMap(300240000);
		map20.addSpawn(new SpawnPosition(523f, 485f, 649f));
		map20.addSpawn(new SpawnPosition(576f, 463f, 647f));
		map20.addSpawn(new SpawnPosition(590f, 419f, 648f));
		map20.addSpawn(new SpawnPosition(524f, 404f, 648f));
		map20.addSpawn(new SpawnPosition(456f, 424f, 652f));
		map20.addSpawn(new SpawnPosition(466f, 466f, 647f));
		map20.setKillZ(620f);
        super.maps.add(map20);
		//Esoterrace.
		BattlegroundMap map21 = new BattlegroundMap(300250000);
		map21.addSpawn(new SpawnPosition(1254f, 624f, 296f));
		map21.addSpawn(new SpawnPosition(1217f, 620f, 295f));
		map21.addSpawn(new SpawnPosition(1230f, 664f, 298f));
		map21.addSpawn(new SpawnPosition(1249f, 695f, 299f));
		map21.addSpawn(new SpawnPosition(1286f, 675f, 296f));
		map21.addSpawn(new SpawnPosition(1294f, 623f, 297f));
		map21.setKillZ(270f);
        super.maps.add(map21);
		//Rentus Base.
		BattlegroundMap map22 = new BattlegroundMap(300280000);
		map22.addSpawn(new SpawnPosition(148f, 363f, 257f));
		map22.addSpawn(new SpawnPosition(194f, 349f, 257f));
		map22.addSpawn(new SpawnPosition(228f, 303f, 255f));
		map22.addSpawn(new SpawnPosition(98f, 337f, 255f));
		map22.addSpawn(new SpawnPosition(159f, 306f, 251f));
		map22.addSpawn(new SpawnPosition(170f, 348f, 252f));
		map22.setKillZ(220f);
        super.maps.add(map22);
		//Terath Dredgion.
		BattlegroundMap map23 = new BattlegroundMap(300440000);
		map23.addSpawn(new SpawnPosition(443f, 321f, 403f));
		map23.addSpawn(new SpawnPosition(484f, 342f, 403f));
		map23.addSpawn(new SpawnPosition(485f, 297f, 402f));
		map23.addSpawn(new SpawnPosition(529f, 323f, 403f));
		map23.addSpawn(new SpawnPosition(485f, 314f, 403f));
		map23.addSpawn(new SpawnPosition(424f, 300f, 409f));
		map23.setKillZ(380f);
        super.maps.add(map23);
		//Tiamat Stronghold.
		BattlegroundMap map24 = new BattlegroundMap(300510000);
		map24.addSpawn(new SpawnPosition(1112f, 1095f, 790f));
		map24.addSpawn(new SpawnPosition(1109f, 1039f, 790f));
		map24.addSpawn(new SpawnPosition(1057f, 1069f, 786f));
		map24.addSpawn(new SpawnPosition(1083f, 1040f, 786f));
		map24.addSpawn(new SpawnPosition(1079f, 1096f, 786f));
		map24.addSpawn(new SpawnPosition(1088f, 1068f, 786f));
		map24.setKillZ(760f);
        super.maps.add(map24);
		//Dragon Lord's Refuge.
		BattlegroundMap map25 = new BattlegroundMap(300520000);
		map25.addSpawn(new SpawnPosition(460f, 514f, 417f));
		map25.addSpawn(new SpawnPosition(477f, 478f, 417f));
		map25.addSpawn(new SpawnPosition(530f, 479f, 417f));
		map25.addSpawn(new SpawnPosition(548f, 514f, 417f));
		map25.addSpawn(new SpawnPosition(530f, 550f, 417f));
		map25.addSpawn(new SpawnPosition(476f, 549f, 417f));
		map25.setKillZ(390f);
        super.maps.add(map25);
		//Eternal Bastion.
		BattlegroundMap map26 = new BattlegroundMap(300540000);
		map26.addSpawn(new SpawnPosition(740f, 255f, 253f));
		map26.addSpawn(new SpawnPosition(778f, 288f, 253f));
		map26.addSpawn(new SpawnPosition(754f, 336f, 253f));
		map26.addSpawn(new SpawnPosition(717f, 321f, 252f));
		map26.addSpawn(new SpawnPosition(698f, 287f, 253f));
		map26.addSpawn(new SpawnPosition(766f, 266f, 233f));
		map26.setKillZ(220f);
        super.maps.add(map26);
		//Ophidan Bridge.
		BattlegroundMap map27 = new BattlegroundMap(300590000);
		map27.addSpawn(new SpawnPosition(368f, 491f, 605f));
		map27.addSpawn(new SpawnPosition(301f, 486f, 608f));
		map27.addSpawn(new SpawnPosition(342f, 466f, 607f));
		map27.addSpawn(new SpawnPosition(339f, 513f, 607f));
		map27.addSpawn(new SpawnPosition(349f, 490f, 606f));
		map27.addSpawn(new SpawnPosition(329f, 489f, 607f));
		map27.setKillZ(580f);
        super.maps.add(map27);
		//Raksang Ruins.
		BattlegroundMap map28 = new BattlegroundMap(300610000);
		map28.addSpawn(new SpawnPosition(617f, 646f, 523f));
		map28.addSpawn(new SpawnPosition(590f, 659f, 522f));
		map28.addSpawn(new SpawnPosition(645f, 649f, 522f));
		map28.addSpawn(new SpawnPosition(676f, 687f, 522f));
		map28.addSpawn(new SpawnPosition(577f, 704f, 522f));
		map28.addSpawn(new SpawnPosition(632f, 735f, 522f));
		map28.setKillZ(470f);
        super.maps.add(map28);
		//Occupied Rentus Base.
		BattlegroundMap map29 = new BattlegroundMap(300620000);
		map29.addSpawn(new SpawnPosition(148f, 363f, 257f));
		map29.addSpawn(new SpawnPosition(194f, 349f, 257f));
		map29.addSpawn(new SpawnPosition(228f, 303f, 255f));
		map29.addSpawn(new SpawnPosition(98f, 337f, 255f));
		map29.addSpawn(new SpawnPosition(159f, 306f, 251f));
		map29.addSpawn(new SpawnPosition(170f, 348f, 252f));
		map29.setKillZ(220f);
        super.maps.add(map29);
		//Anguished Dragon Lord's Refuge.
		BattlegroundMap map30 = new BattlegroundMap(300630000);
		map30.addSpawn(new SpawnPosition(223f, 186f, 246f));
		map30.addSpawn(new SpawnPosition(212f, 156f, 246f));
		map30.addSpawn(new SpawnPosition(180f, 162f, 246f));
		map30.addSpawn(new SpawnPosition(180f, 194f, 246f));
		map30.addSpawn(new SpawnPosition(212f, 200f, 246f));
		map30.addSpawn(new SpawnPosition(196f, 175f, 246f));
		map30.setKillZ(220f);
        super.maps.add(map30);
		//The Hexway.
		BattlegroundMap map31 = new BattlegroundMap(300700000);
		map31.addSpawn(new SpawnPosition(488f, 512f, 352f));
		map31.addSpawn(new SpawnPosition(495f, 548f, 354f));
		map31.addSpawn(new SpawnPosition(458f, 530f, 352f));
		map31.addSpawn(new SpawnPosition(485f, 585f, 355f));
		map31.addSpawn(new SpawnPosition(495f, 623f, 354f));
		map31.addSpawn(new SpawnPosition(451f, 618f, 352f));
		map31.setKillZ(320f);
        super.maps.add(map31);
		//Infinity Shard.
		BattlegroundMap map32 = new BattlegroundMap(300800000);
		map32.addSpawn(new SpawnPosition(118f, 116f, 131f));
		map32.addSpawn(new SpawnPosition(107f, 143f, 125f));
		map32.addSpawn(new SpawnPosition(124f, 162f, 129f));
		map32.addSpawn(new SpawnPosition(149f, 147f, 124f));
		map32.addSpawn(new SpawnPosition(145f, 122f, 126f));
		map32.addSpawn(new SpawnPosition(108f, 140f, 112f));
		map32.setKillZ(90f);
        super.maps.add(map32);
		//Danuar Reliquary.
		BattlegroundMap map33 = new BattlegroundMap(301110000);
		map33.addSpawn(new SpawnPosition(269f, 232f, 241f));
		map33.addSpawn(new SpawnPosition(235f, 263f, 241f));
		map33.addSpawn(new SpawnPosition(255f, 289f, 241f));
		map33.addSpawn(new SpawnPosition(280f, 262f, 241f));
		map33.addSpawn(new SpawnPosition(241f, 237f, 241f));
		map33.addSpawn(new SpawnPosition(256f, 257f, 241f));
		map33.setKillZ(220f);
        super.maps.add(map33);
		//Kamar Battlefield.
		BattlegroundMap map34 = new BattlegroundMap(301120000);
		map34.addSpawn(new SpawnPosition(1344f, 1528f, 595f));
		map34.addSpawn(new SpawnPosition(1313f, 1510f, 597f));
		map34.addSpawn(new SpawnPosition(1313f, 1460f, 597f));
		map34.addSpawn(new SpawnPosition(1387f, 1513f, 597f));
		map34.addSpawn(new SpawnPosition(1370f, 1460f, 599f));
		map34.addSpawn(new SpawnPosition(1396f, 1423f, 600f));
		map34.setKillZ(570f);
        super.maps.add(map34);
		//Sauro Supply Base.
		BattlegroundMap map35 = new BattlegroundMap(301130000);
		map35.addSpawn(new SpawnPosition(578f, 503f, 202f));
		map35.addSpawn(new SpawnPosition(611f, 496f, 202f));
		map35.addSpawn(new SpawnPosition(612f, 476f, 202f));
		map35.addSpawn(new SpawnPosition(571f, 486f, 191f));
		map35.addSpawn(new SpawnPosition(586f, 466f, 191f));
		map35.addSpawn(new SpawnPosition(591f, 503f, 191f));
		map35.setKillZ(160f);
        super.maps.add(map35);
		//Seized Danuar Sanctuary.
		BattlegroundMap map36 = new BattlegroundMap(301140000);
		map36.addSpawn(new SpawnPosition(1056f, 645f, 280f));
		map36.addSpawn(new SpawnPosition(1037f, 681f, 282f));
		map36.addSpawn(new SpawnPosition(1075f, 682f, 282f));
		map36.addSpawn(new SpawnPosition(1056f, 718f, 280f));
		map36.addSpawn(new SpawnPosition(1056f, 693f, 282f));
		map36.addSpawn(new SpawnPosition(1056f, 670f, 282f));
		map36.setKillZ(260f);
        super.maps.add(map36);
		//Nightmare Circus.
		BattlegroundMap map37 = new BattlegroundMap(301160000);
		map37.addSpawn(new SpawnPosition(479f, 566f, 202f));
		map37.addSpawn(new SpawnPosition(524f, 592f, 200f));
		map37.addSpawn(new SpawnPosition(521f, 534f, 199f));
		map37.addSpawn(new SpawnPosition(557f, 567f, 199f));
		map37.addSpawn(new SpawnPosition(531f, 558f, 198f));
		map37.addSpawn(new SpawnPosition(515f, 565f, 198f));
		map37.setKillZ(170f);
        super.maps.add(map37);
		//Engulfed Ophidan Bridge.
		BattlegroundMap map38 = new BattlegroundMap(301210000);
		map38.addSpawn(new SpawnPosition(499f, 523f, 597f));
		map38.addSpawn(new SpawnPosition(527f, 541f, 604f));
		map38.addSpawn(new SpawnPosition(494f, 550f, 597f));
		map38.addSpawn(new SpawnPosition(434f, 495f, 600f));
		map38.addSpawn(new SpawnPosition(474f, 490f, 597f));
		map38.addSpawn(new SpawnPosition(448f, 537f, 599f));
		map38.setKillZ(560f);
        super.maps.add(map38);
		//Iron Wall Warfront.
		BattlegroundMap map39 = new BattlegroundMap(301220000);
		map39.addSpawn(new SpawnPosition(491f, 765f, 200f));
		map39.addSpawn(new SpawnPosition(552f, 744f, 197f));
		map39.addSpawn(new SpawnPosition(591f, 777f, 187f));
		map39.addSpawn(new SpawnPosition(565f, 807f, 188f));
		map39.addSpawn(new SpawnPosition(599f, 823f, 187f));
		map39.addSpawn(new SpawnPosition(612f, 776f, 185f));
		map39.setKillZ(160f);
        super.maps.add(map39);
		//Illuminary Obelisk.
		BattlegroundMap map40 = new BattlegroundMap(301230000);
		map40.addSpawn(new SpawnPosition(255f, 317f, 325f));
		map40.addSpawn(new SpawnPosition(255f, 192f, 325f));
		map40.addSpawn(new SpawnPosition(244f, 249f, 318f));
		map40.addSpawn(new SpawnPosition(255f, 223f, 321f));
		map40.addSpawn(new SpawnPosition(255f, 286f, 321f));
		map40.addSpawn(new SpawnPosition(247f, 263f, 318f));
		map40.setKillZ(290f);
        super.maps.add(map40);
		//Linkgate Foundry.
		BattlegroundMap map41 = new BattlegroundMap(301270000);
		map41.addSpawn(new SpawnPosition(228f, 258f, 312f));
		map41.addSpawn(new SpawnPosition(261f, 260f, 312f));
		map41.addSpawn(new SpawnPosition(245f, 242f, 312f));
		map41.addSpawn(new SpawnPosition(243f, 277f, 312f));
		map41.addSpawn(new SpawnPosition(244f, 259f, 312f));
		map41.addSpawn(new SpawnPosition(232f, 245f, 312f));
		map41.setKillZ(290f);
        super.maps.add(map41);
		//Idgel Dome.
		BattlegroundMap map42 = new BattlegroundMap(301310000);
		map42.addSpawn(new SpawnPosition(252f, 246f, 92f));
		map42.addSpawn(new SpawnPosition(276f, 272f, 92f));
		map42.addSpawn(new SpawnPosition(226f, 258f, 89f));
		map42.addSpawn(new SpawnPosition(302f, 258f, 89f));
		map42.addSpawn(new SpawnPosition(248f, 289f, 89f));
		map42.addSpawn(new SpawnPosition(277f, 225f, 89f));
		map42.setKillZ(70f);
        super.maps.add(map42);
		//Danuar Sanctuary.
		BattlegroundMap map43 = new BattlegroundMap(301380000);
		map43.addSpawn(new SpawnPosition(921f, 870f, 278f));
		map43.addSpawn(new SpawnPosition(887f, 857f, 279f));
		map43.addSpawn(new SpawnPosition(900f, 887f, 306f));
		map43.addSpawn(new SpawnPosition(932f, 869f, 305f));
		map43.addSpawn(new SpawnPosition(891f, 843f, 292f));
		map43.addSpawn(new SpawnPosition(923f, 850f, 292f));
		map43.setKillZ(250f);
        super.maps.add(map43);
		//Drakenspire Depths.
		BattlegroundMap map44 = new BattlegroundMap(301390000);
		map44.addSpawn(new SpawnPosition(208f, 542f, 1754f));
		map44.addSpawn(new SpawnPosition(176f, 579f, 1760f));
		map44.addSpawn(new SpawnPosition(127f, 575f, 1754f));
		map44.addSpawn(new SpawnPosition(128f, 461f, 1754f));
		map44.addSpawn(new SpawnPosition(177f, 458f, 1759f));
		map44.addSpawn(new SpawnPosition(208f, 496f, 1754f));
		map44.setKillZ(1730f);
        super.maps.add(map44);
		//The Shugo Emperor's Vault.
		BattlegroundMap map45 = new BattlegroundMap(301400000);
		map45.addSpawn(new SpawnPosition(343f, 727f, 398f));
		map45.addSpawn(new SpawnPosition(393f, 781f, 398f));
		map45.addSpawn(new SpawnPosition(430f, 740f, 398f));
		map45.addSpawn(new SpawnPosition(389f, 690f, 398f));
		map45.addSpawn(new SpawnPosition(361f, 756f, 398f));
		map45.addSpawn(new SpawnPosition(423f, 697f, 398f));
		map45.setKillZ(370f);
        super.maps.add(map45);
		//Stonespear Reach.
		BattlegroundMap map46 = new BattlegroundMap(301500000);
		map46.addSpawn(new SpawnPosition(211f, 263f, 96f));
		map46.addSpawn(new SpawnPosition(251f, 264f, 96f));
		map46.addSpawn(new SpawnPosition(231f, 243f, 96f));
		map46.addSpawn(new SpawnPosition(230f, 286f, 96f));
		map46.addSpawn(new SpawnPosition(231f, 264f, 95f));
		map46.addSpawn(new SpawnPosition(216f, 247f, 96f));
		map46.setKillZ(70f);
        super.maps.add(map46);
		//Sealed Argent Manor.
		BattlegroundMap map47 = new BattlegroundMap(301510000);
		map47.addSpawn(new SpawnPosition(1005f, 1088f, 69f));
		map47.addSpawn(new SpawnPosition(988f, 1064f, 69f));
		map47.addSpawn(new SpawnPosition(959f, 1072f, 69f));
		map47.addSpawn(new SpawnPosition(959f, 1104f, 69f));
		map47.addSpawn(new SpawnPosition(988f, 1113f, 69f));
		map47.addSpawn(new SpawnPosition(979f, 1089f, 70f));
		map47.setKillZ(50f);
        super.maps.add(map47);
		//Archives Of Eternity.
		BattlegroundMap map48 = new BattlegroundMap(301540000);
		map48.addSpawn(new SpawnPosition(234f, 512f, 468f));
		map48.addSpawn(new SpawnPosition(280f, 512f, 468f));
		map48.addSpawn(new SpawnPosition(256f, 532f, 468f));
		map48.addSpawn(new SpawnPosition(256f, 492f, 468f));
		map48.addSpawn(new SpawnPosition(256f, 512f, 468f));
		map48.addSpawn(new SpawnPosition(236f, 493f, 468f));
		map48.setKillZ(440f);
        super.maps.add(map48);
		//Cradle Of Eternity.
		BattlegroundMap map49 = new BattlegroundMap(301550000);
		map49.addSpawn(new SpawnPosition(464f, 1398f, 827f));
		map49.addSpawn(new SpawnPosition(474f, 1418f, 827f));
		map49.addSpawn(new SpawnPosition(510f, 1387f, 823f));
		map49.addSpawn(new SpawnPosition(430f, 1429f, 823f));
		map49.addSpawn(new SpawnPosition(449f, 1374f, 823f));
		map49.addSpawn(new SpawnPosition(491f, 1445f, 823f));
		map49.setKillZ(795f);
        super.maps.add(map49);
		//Cradle Of Eternity [Memorial Path]
		BattlegroundMap map50 = new BattlegroundMap(301550000);
		map50.addSpawn(new SpawnPosition(602f, 806f, 565f));
		map50.addSpawn(new SpawnPosition(626f, 768f, 561f));
		map50.addSpawn(new SpawnPosition(629f, 717f, 555f));
		map50.addSpawn(new SpawnPosition(738f, 727f, 546f));
		map50.addSpawn(new SpawnPosition(685f, 721f, 548f));
		map50.addSpawn(new SpawnPosition(709f, 772f, 547f));
		map50.setKillZ(530f);
        super.maps.add(map50);
		//Theobomos Test Chamber.
		BattlegroundMap map51 = new BattlegroundMap(301610000);
		map51.addSpawn(new SpawnPosition(228f, 256f, 203f));
		map51.addSpawn(new SpawnPosition(240f, 226f, 203f));
		map51.addSpawn(new SpawnPosition(212f, 203f, 203f));
		map51.addSpawn(new SpawnPosition(188f, 222f, 203f));
		map51.addSpawn(new SpawnPosition(193f, 257f, 203f));
		map51.addSpawn(new SpawnPosition(211f, 274f, 202f));
		map51.setKillZ(190f);
        super.maps.add(map51);
		//Theobomos Test Chamber [Elemental Athanor]
		BattlegroundMap map52 = new BattlegroundMap(301610000);
		map52.addSpawn(new SpawnPosition(258f, 119f, 196f));
		map52.addSpawn(new SpawnPosition(310f, 120f, 195f));
		map52.addSpawn(new SpawnPosition(288f, 101f, 195f));
		map52.addSpawn(new SpawnPosition(289f, 137f, 195f));
		map52.addSpawn(new SpawnPosition(285f, 119f, 196f));
		map52.addSpawn(new SpawnPosition(261f, 136f, 196f));
		map52.setKillZ(170f);
        super.maps.add(map52);
		//Drakenseer's Lair.
		BattlegroundMap map53 = new BattlegroundMap(301620000);
		map53.addSpawn(new SpawnPosition(276f, 342f, 336f));
		map53.addSpawn(new SpawnPosition(328f, 309f, 318f));
		map53.addSpawn(new SpawnPosition(350f, 266f, 318f));
		map53.addSpawn(new SpawnPosition(330f, 204f, 319f));
		map53.addSpawn(new SpawnPosition(266f, 197f, 319f));
		map53.addSpawn(new SpawnPosition(237f, 292f, 318f));
		map53.setKillZ(295f);
        super.maps.add(map53);
		//Contaminated Underpath.
		BattlegroundMap map54 = new BattlegroundMap(301630000);
		map54.addSpawn(new SpawnPosition(229f, 184f, 164f));
		map54.addSpawn(new SpawnPosition(229f, 208f, 160f));
		map54.addSpawn(new SpawnPosition(229f, 226f, 160f));
		map54.addSpawn(new SpawnPosition(229f, 253f, 159f));
		map54.addSpawn(new SpawnPosition(229f, 276f, 160f));
		map54.addSpawn(new SpawnPosition(227f, 300f, 160f));
		map54.setKillZ(140f);
        super.maps.add(map54);
		//Secret Munitions Factory.
		BattlegroundMap map55 = new BattlegroundMap(301640000);
		map55.addSpawn(new SpawnPosition(323f, 259f, 192f));
		map55.addSpawn(new SpawnPosition(208f, 258f, 191f));
		map55.addSpawn(new SpawnPosition(271f, 276f, 191f));
		map55.addSpawn(new SpawnPosition(272f, 243f, 191f));
		map55.addSpawn(new SpawnPosition(354f, 266f, 195f));
		map55.addSpawn(new SpawnPosition(244f, 258f, 191f));
		map55.setKillZ(170f);
        super.maps.add(map55);
		//Fallen Poeta.
		BattlegroundMap map56 = new BattlegroundMap(301660000);
		map56.addSpawn(new SpawnPosition(216f, 348f, 130f));
		map56.addSpawn(new SpawnPosition(235f, 382f, 124f));
		map56.addSpawn(new SpawnPosition(183f, 334f, 123f));
		map56.addSpawn(new SpawnPosition(175f, 379f, 120f));
		map56.addSpawn(new SpawnPosition(221f, 400f, 118f));
		map56.addSpawn(new SpawnPosition(193f, 393f, 119f));
		map56.setKillZ(100f);
        super.maps.add(map56);
		//Ophidan Warpath.
		BattlegroundMap map57 = new BattlegroundMap(301670000);
		map57.addSpawn(new SpawnPosition(697f, 466f, 599f));
		map57.addSpawn(new SpawnPosition(676f, 495f, 599f));
		map57.addSpawn(new SpawnPosition(665f, 449f, 600f));
		map57.addSpawn(new SpawnPosition(570f, 412f, 610f));
		map57.addSpawn(new SpawnPosition(599f, 395f, 609f));
		map57.addSpawn(new SpawnPosition(620f, 423f, 607f));
		map57.setKillZ(580f);
        super.maps.add(map57);
		//Aether Mine.
		BattlegroundMap map58 = new BattlegroundMap(301690000);
		map58.addSpawn(new SpawnPosition(174f, 155f, 230f));
		map58.addSpawn(new SpawnPosition(214f, 174f, 230f));
		map58.addSpawn(new SpawnPosition(248f, 190f, 235f));
		map58.addSpawn(new SpawnPosition(281f, 205f, 242f));
		map58.addSpawn(new SpawnPosition(315f, 227f, 250f));
		map58.addSpawn(new SpawnPosition(326f, 263f, 258f));
		map58.setKillZ(200f);
        super.maps.add(map58);
		//Old Fire Temple.
		BattlegroundMap map59 = new BattlegroundMap(302000000);
		map59.addSpawn(new SpawnPosition(370f, 250f, 117f));
		map59.addSpawn(new SpawnPosition(355f, 280f, 110f));
		map59.addSpawn(new SpawnPosition(394f, 289f, 110f));
		map59.addSpawn(new SpawnPosition(396f, 269f, 110f));
		map59.addSpawn(new SpawnPosition(400f, 249f, 110f));
		map59.addSpawn(new SpawnPosition(384f, 239f, 110f));
		map59.setKillZ(80f);
        super.maps.add(map59);
		//Fissure Of Oblivion.
		BattlegroundMap map60 = new BattlegroundMap(302100000);
		map60.addSpawn(new SpawnPosition(326f, 512f, 352f));
		map60.addSpawn(new SpawnPosition(278f, 513f, 351f));
		map60.addSpawn(new SpawnPosition(300f, 531f, 350f));
		map60.addSpawn(new SpawnPosition(301f, 496f, 350f));
		map60.addSpawn(new SpawnPosition(290f, 527f, 350f));
		map60.addSpawn(new SpawnPosition(312f, 499f, 350f));
		map60.setKillZ(330f);
        super.maps.add(map60);
		//Karamatis A.
		BattlegroundMap map61 = new BattlegroundMap(310010000);
		map61.addSpawn(new SpawnPosition(220f, 250f, 206f));
		map61.addSpawn(new SpawnPosition(229f, 288f, 206f));
		map61.addSpawn(new SpawnPosition(241f, 225f, 206f));
		map61.addSpawn(new SpawnPosition(287f, 206f, 207f));
		map61.addSpawn(new SpawnPosition(305f, 271f, 205f));
		map61.addSpawn(new SpawnPosition(277f, 301f, 206f));
		map61.setKillZ(190f);
        super.maps.add(map61);
		//Aerdina.
		BattlegroundMap map62 = new BattlegroundMap(310030000);
		map62.addSpawn(new SpawnPosition(273f, 173f, 204f));
		map62.addSpawn(new SpawnPosition(252f, 261f, 228f));
		map62.addSpawn(new SpawnPosition(238f, 196f, 207f));
		map62.addSpawn(new SpawnPosition(296f, 223f, 211f));
		map62.addSpawn(new SpawnPosition(261f, 228f, 213f));
		map62.addSpawn(new SpawnPosition(229f, 245f, 224f));
		map62.setKillZ(190f);
        super.maps.add(map62);
		//Aetherogenetics Lab.
		BattlegroundMap map63 = new BattlegroundMap(310050000);
		map63.addSpawn(new SpawnPosition(215f, 343f, 126f));
		map63.addSpawn(new SpawnPosition(225f, 343f, 126f));
		map63.addSpawn(new SpawnPosition(236f, 342f, 126f));
		map63.addSpawn(new SpawnPosition(236f, 302f, 126f));
		map63.addSpawn(new SpawnPosition(225f, 303f, 126f));
		map63.addSpawn(new SpawnPosition(215f, 303f, 126f));
		map63.setKillZ(100f);
        super.maps.add(map63);
		//Sanctum Underground Arena.
		BattlegroundMap map64 = new BattlegroundMap(310080000);
		map64.addSpawn(new SpawnPosition(275f, 181f, 162f));
		map64.addSpawn(new SpawnPosition(275f, 242f, 158f));
		map64.addSpawn(new SpawnPosition(275f, 289f, 162f));
		map64.addSpawn(new SpawnPosition(247f, 240f, 158f));
		map64.addSpawn(new SpawnPosition(304f, 240f, 158f));
		map64.addSpawn(new SpawnPosition(260f, 215f, 158f));
		map64.setKillZ(130f);
        super.maps.add(map64);
		//Indratu Fortress.
		BattlegroundMap map65 = new BattlegroundMap(310090000);
		map65.addSpawn(new SpawnPosition(604f, 466f, 1019f));
		map65.addSpawn(new SpawnPosition(617f, 516f, 1019f));
		map65.addSpawn(new SpawnPosition(575f, 540f, 1013f));
		map65.addSpawn(new SpawnPosition(566f, 507f, 1012f));
		map65.addSpawn(new SpawnPosition(552f, 479f, 1011f));
		map65.addSpawn(new SpawnPosition(615f, 562f, 1018f));
		map65.setKillZ(990f);
        super.maps.add(map65);
		//Azoturan Fortress.
		BattlegroundMap map66 = new BattlegroundMap(310100000);
		map66.addSpawn(new SpawnPosition(462f, 442f, 993f));
		map66.addSpawn(new SpawnPosition(417f, 402f, 1004f));
		map66.addSpawn(new SpawnPosition(425f, 398f, 991f));
		map66.addSpawn(new SpawnPosition(459f, 392f, 991f));
		map66.addSpawn(new SpawnPosition(413f, 426f, 991f));
		map66.addSpawn(new SpawnPosition(443f, 419f, 991f));
		map66.setKillZ(970f);
        super.maps.add(map66);
		//Sky Temple Interior.
		BattlegroundMap map67 = new BattlegroundMap(320050000);
		map67.addSpawn(new SpawnPosition(376f, 417f, 374f));
		map67.addSpawn(new SpawnPosition(462f, 399f, 375f));
		map67.addSpawn(new SpawnPosition(439f, 367f, 375f));
		map67.addSpawn(new SpawnPosition(424f, 390f, 374f));
		map67.addSpawn(new SpawnPosition(412f, 456f, 375f));
		map67.addSpawn(new SpawnPosition(443f, 448f, 375f));
		map67.setKillZ(350f);
        super.maps.add(map67);
		//Space Of Destiny.
		BattlegroundMap map68 = new BattlegroundMap(320070000);
		map68.addSpawn(new SpawnPosition(210f, 250f, 125f));
        map68.addSpawn(new SpawnPosition(271f, 246f, 125f));
        map68.addSpawn(new SpawnPosition(233f, 204f, 125f));
        map68.addSpawn(new SpawnPosition(246f, 248f, 125f));
        map68.addSpawn(new SpawnPosition(256f, 285f, 125f));
        map68.addSpawn(new SpawnPosition(221f, 272f, 125f));
		map68.setKillZ(110f);
        super.maps.add(map68);
		//Fire Temple.
		BattlegroundMap map69 = new BattlegroundMap(320100000);
		map69.addSpawn(new SpawnPosition(414f, 97f, 117f));
		map69.addSpawn(new SpawnPosition(392f, 88f, 117f));
		map69.addSpawn(new SpawnPosition(411f, 120f, 117f));
		map69.addSpawn(new SpawnPosition(392f, 128f, 117f));
		map69.addSpawn(new SpawnPosition(377f, 99f, 117f));
		map69.addSpawn(new SpawnPosition(361f, 126f, 116f));
		map69.setKillZ(95f);
        super.maps.add(map69);
		//Alquimia Research Center.
		BattlegroundMap map70 = new BattlegroundMap(320110000);
		map70.addSpawn(new SpawnPosition(286f, 501f, 211f));
        map70.addSpawn(new SpawnPosition(292f, 521f, 209f));
        map70.addSpawn(new SpawnPosition(292f, 481f, 209f));
        map70.addSpawn(new SpawnPosition(365f, 483f, 211f));
        map70.addSpawn(new SpawnPosition(365f, 521f, 209f));
        map70.addSpawn(new SpawnPosition(329f, 501f, 209f));
		map70.setKillZ(200f);
        super.maps.add(map70);
		//Padmarashka's Cave.
		BattlegroundMap map71 = new BattlegroundMap(320150000);
		map71.addSpawn(new SpawnPosition(576f, 279f, 66f));
		map71.addSpawn(new SpawnPosition(605f, 235f, 66f));
		map71.addSpawn(new SpawnPosition(578f, 206f, 66f));
		map71.addSpawn(new SpawnPosition(537f, 209f, 66f));
		map71.addSpawn(new SpawnPosition(524f, 239f, 66f));
		map71.addSpawn(new SpawnPosition(535f, 279f, 66f));
		map71.setKillZ(50f);
        super.maps.add(map71);
		//Transidium Annex.
		BattlegroundMap map72 = new BattlegroundMap(400030000);
		map72.addSpawn(new SpawnPosition(481f, 500f, 674f));
		map72.addSpawn(new SpawnPosition(480f, 524f, 674f));
		map72.addSpawn(new SpawnPosition(497f, 541f, 674f));
		map72.addSpawn(new SpawnPosition(521f, 542f, 674f));
		map72.addSpawn(new SpawnPosition(538f, 524f, 674f));
		map72.addSpawn(new SpawnPosition(538f, 500f, 674f));
		map72.addSpawn(new SpawnPosition(521f, 483f, 674f));
		map72.addSpawn(new SpawnPosition(497f, 483f, 674f));
		map72.setKillZ(650f);
        super.maps.add(map72);
		//Live Party Concert Hall.
		BattlegroundMap map73 = new BattlegroundMap(600080000);
		map73.addSpawn(new SpawnPosition(1543f, 1529f, 565f));
		map73.addSpawn(new SpawnPosition(1543f, 1493f, 565f));
		map73.addSpawn(new SpawnPosition(1521f, 1492f, 565f));
		map73.addSpawn(new SpawnPosition(1521f, 1530f, 565f));
		map73.addSpawn(new SpawnPosition(1509f, 1511f, 565f));
		map73.addSpawn(new SpawnPosition(1503f, 1541f, 566f));
		map73.setKillZ(540f);
        super.maps.add(map73);
    }
	
    public void createMatch(List<Integer> players) {
        super.handleQueueSolo(players);
        if (super.getPlayers().isEmpty()) {
            return;
        }
        startMatch();
    }
	
    public boolean isInBg(Player player) {
        for (BattlegroundMap bgMap : maps) {
            if (bgMap.getMapId() == player.getWorldId()) {
                return true;
            }
        }
        return false;
    }
	
    public void startMatch() {
        super.createInstance();
        synchronized (super.getPlayers()) {
            for (Player pl : super.getPlayers()) {
                super.preparePlayer(pl, 25000);
                SpawnPosition pos = getSpawnPositions().get(pl.getBgIndex());
                performCdReset(pl);
                if (pl.isInGroup2()) {
                    PlayerGroupService.removePlayer(pl);
                } if (pos != null) {
                    performTeleport(pl, pos.getX(), pos.getY(), pos.getZ());
                }
            }
        }
        ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                openStaticDoors();
            }
        }, 25 * 1000);
        super.setExpireTask(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                endSoloSurvivorMatch(true);
            }
        }, getMatchLength() * 1000));
        super.startBackgroundTask();
    }
	
    @Override
    public boolean createTournament(List<List<Player>> players) {
        if (!super.createPlayers(players)) {
            return false;
        }
        this.maxRounds = 5;
        this.matchLength = 145;
        this.description = "You must kill your opponent to win. You have " + maxRounds + " round. The player who gets the most kill's, win the match !!!";
        startMatch();
        return true;
    }
	
    @Override
    public boolean isStealthRestricted() {
        return true;
    }
	
    public void onDie(final Player player, Creature lastAttacker) {
        if (lastAttacker == null && is1v1() && getPlayers().size() == 2) {
            lastAttacker = getPlayers().get(player.getBgIndex() == 0 ? 1 : 0);
        }
        super.onDieDefault(player, lastAttacker);
        if (lastAttacker instanceof Player && ((Player) lastAttacker).getObjectId() != player.getObjectId()) {
            Player killer = (Player) lastAttacker;
            for (Player pl : super.getPlayers()) {
                scheduleAnnouncement(pl, killer.getName() + " has killed " + player.getName(), 0);
            }
            super.specAnnounce(killer.getName() + " has killed " + player.getName());
        }
        int deadCounter = 0;
        for (Player pl : super.getPlayers()) {
            if (pl.getLifeStats().isAlreadyDead() || pl.isAfk()) {
                deadCounter++;
            }
        } if (deadCounter >= (super.getPlayers().size() - 1)) {
            endSoloSurvivorMatch(false);
        }
    }
	
    @Override
    public void onLeave(Player player, boolean isLogout, boolean isAfk) {
        super.onLeaveDefault(player, isLogout, isAfk);
        if (super.getPlayers().size() <= 1) {
            endSoloSurvivorMatch(false);
        }
    }
	
    private void startNewRound() {
        super.setStartStamp(System.currentTimeMillis());
        synchronized (super.getPlayers()) {
            for (final Player pl : super.getPlayers()) {
                ThreadPoolManager.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        preparePlayer(pl, 8000, false);
                        SpawnPosition pos = getSpawnPositions().get(pl.getBgIndex());
                        if (pos != null) {
                            TeleportService2.teleportTo(pl, getMapId(), pos.getX(), pos.getY(), pos.getZ());
                        }
                    }
                }, 5000);
            }
        } for (Player pl : super.getSpectators()) {
            super.createTimer(pl, getMatchLength());
        } super.setExpireTask(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                endSoloSurvivorMatch(true);
            }
        }, getMatchLength() * 1000));
        super.startBackgroundTask();
        ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                endCalled = false;
                if (getPlayers().size() <= 1) {
                    endSoloSurvivorMatch(false);
                }
            }
        }, 5000);
    }
	
    private Player getRoundWinner() {
        Player winner = null;
        for (Player pl : super.getPlayers()) {
            if (!pl.getLifeStats().isAlreadyDead()) {
                winner = pl;
                break;
            }
        }
        return winner;
    }
	
    private Player getWinner() {
        Player winner = null;
        int drawPoints = 0;
        for (Player pl : super.getPlayers()) {
            if (winner == null && pl.getTotalKills() > drawPoints) {
                winner = pl;
            } else if (winner == null) {
                continue;
            } else if (winner.getTotalKills() < pl.getTotalKills()) {
                winner = pl;
            } else if (winner.getTotalKills() == pl.getTotalKills()) {
                drawPoints = winner.getTotalKills();
                winner = null;
            }
        }
        return winner;
    }
	
    private void endSoloSurvivorMatch(boolean isDraw) {
        if (endCalled) {
            return;
        }
        endCalled = true;
        for (Player pl : super.getPlayers()) {
            super.freezeNoEnd(pl);
        }
        super.onEndFirstDefault();
        Player roundWinner = getRoundWinner();
        if (roundsDone < maxRounds) {
            roundsDone++;
        } if (roundsDone < maxRounds) {
            if (!isDraw) {
                if (roundWinner != null) {
                    for (Player pl : super.getPlayers()) {
                        super.scheduleAnnouncement(pl, roundWinner.getName() + " Win's the round!", 0);
                        pl.sendMessage("Number of round:" + roundsDone);
                    }
                    super.specAnnounce(roundWinner.getName() + " Win's the round!");
                    startNewRound();
                    return;
                }
            } else {
                for (Player pl : super.getPlayers()) {
                    super.scheduleAnnouncement(pl, "The round ended with a draw!", 0);
                }
                super.specAnnounce("The round ended with a draw!");
            }
        }
        Player winner = getWinner();
        if (winner == null) {
			for (Player pl : super.getPlayers()) {
				scheduleAnnouncement(pl, "The round ended with a draw! You will have more luck next time.", 0);
				byte level = pl.getLevel();
				//Reward Player if "DRAW"
				if (level >= 10 && level <= 50) {
					ItemService.addItem(pl, 186000030, 2); //Gold Medal.
					ItemService.addItem(pl, 186000031, 2); //Silver Medal.
					//ItemService.addItem(pl, 166030005, 1); //Tempering Solution.
					ItemService.addItem(pl, 166020000, 1); //Omega Enchantment Stone.
					ItemService.addItem(pl, 188100335, 50); //Enchantment Stone Dust.
					AbyssPointsService.addAp(pl, 5000);
					AbyssPointsService.addGp(pl, 5);
				} if (level >= 51 && level <= 65) {
					ItemService.addItem(pl, 186000096, 2); //Platinum Medal.
					ItemService.addItem(pl, 186000147, 2); //Mithril Medal.
					ItemService.addItem(pl, 186000223, 2); //Honorable Mithril Medal.
					//ItemService.addItem(pl, 166030005, 1); //Tempering Solution.
					ItemService.addItem(pl, 166020000, 1); //Omega Enchantment Stone.
					ItemService.addItem(pl, 188100335, 100); //Enchantment Stone Dust.
					AbyssPointsService.addAp(pl, 25000);
					AbyssPointsService.addGp(pl, 25);
				} if (level >= 66 && level <= 83) {
					ItemService.addItem(pl, 186000236, 2); //Blood Mark.
					ItemService.addItem(pl, 186000414, 2); //Blood Medal.
					//ItemService.addItem(pl, 166030005, 1); //Tempering Solution.
					ItemService.addItem(pl, 166020000, 1); //Omega Enchantment Stone.
					ItemService.addItem(pl, 188100335, 150); //Enchantment Stone Dust.
					AbyssPointsService.addAp(pl, 50000);
					AbyssPointsService.addGp(pl, 50);
				}
            }
            super.specAnnounce("The round ended with a draw!");
        } else {
            for (Player pl : super.getPlayers()) {
                if (pl.getObjectId() == winner.getObjectId()) {
                    super.playerWinMatch(pl, super.K_VALUE / 2);
					byte level = pl.getLevel();
					//Reward Player if "WINNER"
					if (level >= 10 && level <= 50) {
						ItemService.addItem(pl, 186000030, 5); //Gold Medal.
						ItemService.addItem(pl, 186000031, 5); //Silver Medal.
						//ItemService.addItem(pl, 166030005, 2); //Tempering Solution.
						ItemService.addItem(pl, 166020000, 2); //Omega Enchantment Stone.
						ItemService.addItem(pl, 188100335, 100); //Enchantment Stone Dust.
						ItemService.addItem(pl, 188710113, 1); //No Limits Pack "1 Day"
						AbyssPointsService.addAp(pl, 5000);
						AbyssPointsService.addGp(pl, 5);
					} if (level >= 51 && level <= 65) {
						ItemService.addItem(pl, 186000096, 5); //Platinum Medal.
						ItemService.addItem(pl, 186000147, 5); //Mithril Medal.
						ItemService.addItem(pl, 186000223, 5); //Honorable Mithril Medal.
						//ItemService.addItem(pl, 166030005, 2); //Tempering Solution.
						ItemService.addItem(pl, 166020000, 2); //Omega Enchantment Stone.
						ItemService.addItem(pl, 188100335, 200); //Enchantment Stone Dust.
						ItemService.addItem(pl, 188710113, 1); //No Limits Pack "1 Day"
						AbyssPointsService.addAp(pl, 25000);
						AbyssPointsService.addGp(pl, 25);
					} if (level >= 66 && level <= 83) {
						ItemService.addItem(pl, 186000236, 5); //Blood Mark.
						ItemService.addItem(pl, 186000414, 5); //Blood Medal.
						//ItemService.addItem(pl, 166030005, 2); //Tempering Solution.
						ItemService.addItem(pl, 166020000, 2); //Omega Enchantment Stone.
						ItemService.addItem(pl, 188100335, 300); //Enchantment Stone Dust.
						ItemService.addItem(pl, 188710113, 1); //No Limits Pack "1 Day"
						AbyssPointsService.addAp(pl, 50000);
						AbyssPointsService.addGp(pl, 50);
					}
                    super.scheduleAnnouncement(pl, "You won the match with " + winner.getTotalKills() + " kill's!", 0);
                    super.scheduleAnnouncement(pl, "You have received rewards " + (is1v1() ? "for your efforts!" : "."), 3000);
                } else {
                    super.playerLoseMatch(pl, -super.K_VALUE / 5);
					byte level = pl.getLevel();
					//Reward Player if "LOSER"
					if (level >= 10 && level <= 50) {
						ItemService.addItem(pl, 186000030, 2); //Gold Medal.
						ItemService.addItem(pl, 186000031, 2); //Silver Medal.
						//ItemService.addItem(pl, 166030005, 1); //Tempering Solution.
						ItemService.addItem(pl, 166020000, 1); //Omega Enchantment Stone.
						ItemService.addItem(pl, 188100335, 50); //Enchantment Stone Dust.
						AbyssPointsService.addAp(pl, -5000);
						AbyssPointsService.addGp(pl, -5);
					} if (level >= 51 && level <= 65) {
						ItemService.addItem(pl, 186000096, 2); //Platinum Medal.
						ItemService.addItem(pl, 186000147, 2); //Mithril Medal.
						ItemService.addItem(pl, 186000223, 2); //Honorable Mithril Medal.
						//ItemService.addItem(pl, 166030005, 1); //Tempering Solution.
						ItemService.addItem(pl, 166020000, 1); //Omega Enchantment Stone.
						ItemService.addItem(pl, 188100335, 50); //Enchantment Stone Dust.
						AbyssPointsService.addAp(pl, -25000);
						AbyssPointsService.addGp(pl, -25);
					} if (level >= 66 && level <= 83) {
						ItemService.addItem(pl, 186000236, 2); //Blood Mark.
						ItemService.addItem(pl, 186000414, 2); //Blood Medal.
						//ItemService.addItem(pl, 166030005, 1); //Tempering Solution.
						ItemService.addItem(pl, 166020000, 1); //Omega Enchantment Stone.
						ItemService.addItem(pl, 188100335, 50); //Enchantment Stone Dust.
						AbyssPointsService.addAp(pl, -50000);
						AbyssPointsService.addGp(pl, -50);
					}
                    super.scheduleAnnouncement(pl, winner.getName() + " win's the match with " + winner.getTotalKills() + " kill's!", 0);
                    super.scheduleAnnouncement(pl, "You have received rewards" + (is1v1() ? "." : "."), 3000);
                }
            }
            super.specAnnounce(winner.getName() + " win's the match with " + winner.getTotalKills() + " kill's!");
        }
        super.onEndDefault();
    }
}