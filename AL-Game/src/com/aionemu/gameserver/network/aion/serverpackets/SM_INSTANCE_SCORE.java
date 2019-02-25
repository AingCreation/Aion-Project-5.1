package com.aionemu.gameserver.network.aion.serverpackets;

import java.util.List;

import javolution.util.FastList;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.instance.InstanceScoreType;
import com.aionemu.gameserver.model.instance.instancereward.*;
import com.aionemu.gameserver.model.instance.playerreward.*;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

@SuppressWarnings("rawtypes")
public class SM_INSTANCE_SCORE extends AionServerPacket
{
	private int type;
	private int mapId;
	private int instanceTime;
	private InstanceScoreType instanceScoreType;
	private InstanceReward instanceReward;
	private List<Player> players;
	private Integer object;
	private int value1 = 0;
    private int value2 = 0;
	
	public SM_INSTANCE_SCORE(int type, int instanceTime, InstanceReward instanceReward, Integer object, int value1, int value2) {
        this.mapId = instanceReward.getMapId();
        this.type = type;
        this.instanceTime = instanceTime;
        this.instanceReward = instanceReward;
        this.object = object;
        this.value1 = value1;
        this.value2 = value2;
        instanceScoreType = instanceReward.getInstanceScoreType();
    }
	
    public SM_INSTANCE_SCORE(int type, int instanceTime, InstanceReward instanceReward, Integer object) {
        this.mapId = instanceReward.getMapId();
        this.type = type;
        this.instanceTime = instanceTime;
        this.instanceReward = instanceReward;
        this.object = object;
        instanceScoreType = instanceReward.getInstanceScoreType();
    }
	
    public SM_INSTANCE_SCORE(int instanceTime, InstanceReward instanceReward, List<Player> players) {
        this.mapId = instanceReward.getMapId();
        this.instanceTime = instanceTime;
        this.instanceReward = instanceReward;
        this.players = players;
        instanceScoreType = instanceReward.getInstanceScoreType();
    }
    
    public SM_INSTANCE_SCORE(int type, int instanceTime, InstanceReward instanceReward, List<Player> players, boolean tis) {
        this.mapId = instanceReward.getMapId();
        this.type = type;
        this.instanceTime = instanceTime;
        this.instanceReward = instanceReward;
        this.players = players;
        instanceScoreType = instanceReward.getInstanceScoreType();
    }
	
    public SM_INSTANCE_SCORE(InstanceReward instanceReward, InstanceScoreType instanceScoreType) {
        this.mapId = instanceReward.getMapId();
        this.instanceReward = instanceReward;
        this.instanceScoreType = instanceScoreType;
    }
	
    public SM_INSTANCE_SCORE(InstanceReward instanceReward) {
        this.mapId = instanceReward.getMapId();
        this.instanceReward = instanceReward;
        this.instanceScoreType = instanceReward.getInstanceScoreType();
    }
	
	@SuppressWarnings("unchecked")
	@Override
	protected void writeImpl(AionConnection con) {
		int playerCount = 0;
		Player owner = con.getActivePlayer();
		Integer ownerObject = owner.getObjectId();
		writeD(mapId);
		writeD(instanceTime);
		writeD(instanceScoreType.getId());
		switch (mapId) {
		    case 300450000: //Arena Of Harmony 3.9
		    case 300570000: //Harmony Training Grounds 3.9
		    case 301100000: //Unity Training Grounds 3.9
			HarmonyArenaReward harmonyArena = (HarmonyArenaReward) instanceReward;
			if (object == null) {
				object = ownerObject;
			}
			HarmonyGroupReward harmonyGroupReward = harmonyArena.getHarmonyGroupReward(object);
			writeC(type);
			switch (type) {
			    case 2:
				    writeD(0);
				    writeD(harmonyArena.getRound());
			    break;
			    case 3:
				    writeD(harmonyGroupReward.getOwner());
				    writeS(harmonyGroupReward.getAGPlayer(object).getName(), 52);
				    writeD(harmonyGroupReward.getId());
				    writeD(object);
			    break;
			    case 4:
				    writeD(harmonyArena.getPlayerReward(object).getRemaningTime());
				    writeD(0);
				    writeD(0);
				    writeD(object);
			    break;
			    case 5:
				    writeD(harmonyGroupReward.getBasicAP());
				    writeD(harmonyGroupReward.getBasicGP());
				    writeD(harmonyGroupReward.getScoreAP());
				    writeD(harmonyGroupReward.getScoreGP());
				    writeD(harmonyGroupReward.getRankingAP());
				    writeD(harmonyGroupReward.getRankingGP());
				    writeD(186000137);
				    writeD(harmonyGroupReward.getBasicCourage());
				    writeD(harmonyGroupReward.getScoreCourage());
				    writeD(harmonyGroupReward.getRankingCourage());
					writeD(186000442); //ë¬´í•œì�˜ í…œíŽ˜ë¥´ íœ˜ìž¥.
				    writeD(harmonyGroupReward.getBasicInfinity());
				    writeD(harmonyGroupReward.getScoreInfinity());
				    writeD(harmonyGroupReward.getRankingInfinity());
				    if (harmonyGroupReward.getGloryTicket() != 0) {
					    writeD(186000185);
					    writeD(harmonyGroupReward.getGloryTicket());
				    } else {
					    writeD(0);
					    writeD(0);
				    }
				    writeD(0);
				    writeD(0);
				    writeD(0);
				    writeD(0);
				    writeD(0);
				    writeD(0);
				    writeD((int) harmonyGroupReward.getParticipation() * 100);
				    writeD(harmonyGroupReward.getPoints());
			    break;
			    case 6:
				    writeD(3);
				    writeD(harmonyArena.getCapPoints());
				    writeD(3);
				    writeD(1);
				    writeD(harmonyArena.getBuffId());
				    writeD(2);
				    writeD(0);
				    writeD(harmonyArena.getRound());
				    FastList<HarmonyGroupReward> groups = harmonyArena.getHarmonyGroupInside();
				    writeC(groups.size());
				    for (HarmonyGroupReward group : groups) {
					    writeC(harmonyArena.getRank(group.getPoints()));
					    writeD(group.getPvPKills());
					    writeD(group.getPoints());
					    writeD(group.getOwner());
					    FastList<Player> members = harmonyArena.getPlayersInside(group);
					    writeC(members.size());
					    int i = 0;
					    for (Player p : members) {
						    PvPArenaPlayerReward rewardedPlayer = harmonyArena.getPlayerReward(p.getObjectId());
						    writeD(0);
						    writeD(rewardedPlayer.getRemaningTime());
						    writeD(0);
						    writeC(group.getOwner());
						    writeC(i);
						    writeH(0);
						    writeS(p.getName(), 52);
						    writeD(p.getObjectId());
						    i++;
					    }
				    }
			    break;
			    case 10:
				    writeC(harmonyArena.getRank(harmonyGroupReward.getPoints()));
				    writeD(harmonyGroupReward.getPvPKills());
				    writeD(harmonyGroupReward.getPoints());
				    writeD(harmonyGroupReward.getOwner());
				break;
			}
		break;
		case 300110000: //Baranath Dredgion.
		case 300210000: //Chantra Dredgion.
		case 300440000: //Terath Dredgion.
		case 301650000: //Ashunatal Dredgion.
			fillTableWithGroup(Race.ELYOS);
			fillTableWithGroup(Race.ASMODIANS);
			DredgionReward dredgionReward = (DredgionReward) instanceReward;
			int elyosScore = dredgionReward.getPointsByRace(Race.ELYOS).intValue();
			int asmosScore = dredgionReward.getPointsByRace(Race.ASMODIANS).intValue();
			writeD(instanceScoreType.isEndProgress() ? (asmosScore > elyosScore ? 1 : 0) : 255);
			writeD(elyosScore);
			writeD(asmosScore);
			writeH(0);
			for (DredgionReward.DredgionRooms dredgionRoom : dredgionReward.getDredgionRooms()) {
				writeC(dredgionRoom.getState());
			}
		break;
		case 301120000: //Kamar Battlefield 4.3
            KamarBattlefieldReward kbr = (KamarBattlefieldReward) instanceReward;
            if (object == null) {
                object = ownerObject;
            }
            KamarBattlefieldPlayerReward kbpr = kbr.getPlayerReward(object);
            writeC(type);
            switch (type) {
                case 2:
                    writeD(0);
                    writeD(kbr.getTime());
                break;
                case 3:
                    writeD(10);
                    writeD(value1);
                    writeD(object);
                    writeD(value2);
			    break;
                case 4:
                    writeD(10);
                    writeD(value1);
                    writeD(object);
                break;
                case 5:
                    writeD((int) kbpr.getParticipation() * 100);
                    writeD(kbpr.getRewardExp());
                    writeD(kbpr.getBonusExp());
					writeD(kbpr.getRewardAp());
                    writeD(kbpr.getBonusAp());
                    writeD(kbpr.getRewardGp());
                    writeD(kbpr.getBonusGp());
					writeD(kbpr.getKamarRewardBox());
                    writeQ(kbpr.getRewardCount());
					writeD(kbpr.getBloodMark());
                    writeQ(kbpr.getRewardCount());
                    writeD(kbpr.getBonusReward());
                    writeD(kbpr.getRewardCount());
                    writeD(kbpr.getBonusReward2());
                    writeD(kbpr.getRewardCount());
					writeD(kbpr.getAdditionalReward());
                    writeD(kbpr.getAdditionalRewardCount());
                    writeC(1);
                break;
                case 6:
                    int counter = 0;
                    writeD(100);
                    for (Player player : players) {
                    	if (player.getRace() != Race.ELYOS) {
                    		continue;
                    	}
                    	writeD(15);
                    	writeD(player.getLifeStats().isAlreadyDead() ? 60 : 0);
                    	writeD(player.getObjectId());
                    	counter++;
                    } if (counter < 24) {
                        writeB(new byte[12 * (24 - counter)]);
                    }
					counter = 0;
					for (Player player : players) {
                    	if (player.getRace() != Race.ASMODIANS) {
                    		continue;
                    	}
                    	writeD(15);
                    	writeD(player.getLifeStats().isAlreadyDead() ? 60 : 0);
                    	writeD(player.getObjectId());
                    	counter++;
                    } if (counter < 24) {
                        writeB(new byte[12 * (24 - counter)]);
                    }
                    writeC(0);
                	writeD(kbr.getPvpKillsByRace(Race.ELYOS).intValue());
                	writeD(kbr.getPointsByRace(Race.ELYOS).intValue());
                	writeD(0);
                	writeD((kbr.getInstanceScoreType() == instanceScoreType.PREPARING ? 65535 : 1));
                	writeC(0);
                	writeD(kbr.getPvpKillsByRace(Race.ASMODIANS).intValue());
                	writeD(kbr.getPointsByRace(Race.ASMODIANS).intValue());
                	writeD(1);
                	writeD((kbr.getInstanceScoreType() == instanceScoreType.PREPARING ? 65535 : 1));
                break;
                case 7:
                    kamarBattlefieldTable(Race.ELYOS);
                    kamarBattlefieldTable(Race.ASMODIANS);
                break;
				case 8:
                    writeD(object);
                break;
                case 10:
                    writeC(0);
                    writeD(kbr.getPvpKillsByRace(kbpr.getRace()).intValue());
                    writeD(kbr.getPointsByRace(kbpr.getRace()).intValue());
                    writeD(kbpr.getRace().getRaceId());
                    writeD(object);
                break;
                case 11:
                    writeC(0);
                    writeD(kbr.getPvpKillsByRace(kbpr.getRace()).intValue());
                    writeD(kbr.getPointsByRace(kbpr.getRace()).intValue());
                    writeD(kbpr.getRace().getRaceId());
                    writeD(object);
                break;
            }
        break;
		case 301210000: //Engulfed Ophidan Bridge 4.5
		case 301670000: //Ophidan Warpath 5.1
            EngulfedOphidanBridgeReward eobr = (EngulfedOphidanBridgeReward) instanceReward;
            if (object == null) {
                object = ownerObject;
            }
            EngulfedOphidanBridgePlayerReward eobpr = eobr.getPlayerReward(object);
            writeC(type);
            switch (type) {
                case 2:
                    writeD(0);
					for (Player player: players) {
					    switch (player.getWorldId()) {
					        case 301210000: //Engulfed Ophidan Bridge 4.7
					            writeD(eobr.getTime());
					        break;
					        case 301670000: //Ophidan Warpath 5.1
					            writeD(eobr.getTime2());
					        break;
						}
				    }
                break;
                case 3:
                    writeD(10);
                    writeD(value1);
                    writeD(object);
                    writeD(value2);
			    break;
                case 4:
                    writeD(10);
                    writeD(value1);
                    writeD(object);
                break;
                case 5:
                    writeD((int) eobpr.getParticipation() * 100);
                    writeD(eobpr.getRewardExp());
                    writeD(eobpr.getBonusExp());
					writeD(eobpr.getRewardAp());
                    writeD(eobpr.getBonusAp());
                    writeD(eobpr.getRewardGp());
                    writeD(eobpr.getBonusGp());
					writeD(eobpr.getOphidanVictoryBox());
                    writeQ(eobpr.getRewardCount());
					writeD(eobpr.getBloodMark());
                    writeQ(eobpr.getRewardCount());
                    writeD(eobpr.getBonusReward());
                    writeD(eobpr.getRewardCount());
                    writeD(eobpr.getBonusReward2());
                    writeD(eobpr.getRewardCount());
					writeD(eobpr.getAdditionalReward());
                    writeD(eobpr.getAdditionalRewardCount());
					writeC(1);
                break;
                case 6:
                    int counter = 0;
                    writeD(100);
                    for (Player player : players) {
                    	if (player.getRace() != Race.ELYOS) {
                    		continue;
                    	}
                    	writeD(15);
                    	writeD(player.getLifeStats().isAlreadyDead() ? 60 : 0);
                    	writeD(player.getObjectId());
                    	counter++;
                    } if (counter < 24) {
                        writeB(new byte[12 * (24 - counter)]);
                    }
					counter = 0;
					for (Player player : players) {
                    	if (player.getRace() != Race.ASMODIANS) {
                    		continue;
                    	}
                    	writeD(15);
                    	writeD(player.getLifeStats().isAlreadyDead() ? 60 : 0);
                    	writeD(player.getObjectId());
                    	counter++;
                    } if (counter < 24) {
                        writeB(new byte[12 * (24 - counter)]);
                    }
                    writeC(0);
                	writeD(eobr.getPvpKillsByRace(Race.ELYOS).intValue());
                	writeD(eobr.getPointsByRace(Race.ELYOS).intValue());
                	writeD(0);
                	writeD((eobr.getInstanceScoreType() == instanceScoreType.PREPARING ? 65535 : 1));
                	writeC(0);
                	writeD(eobr.getPvpKillsByRace(Race.ASMODIANS).intValue());
                	writeD(eobr.getPointsByRace(Race.ASMODIANS).intValue());
                	writeD(1);
                	writeD((eobr.getInstanceScoreType() == instanceScoreType.PREPARING ? 65535 : 1));
                break;
                case 7:
                    engulfedOphidanBridgeTable(Race.ELYOS);
                    engulfedOphidanBridgeTable(Race.ASMODIANS);
                break;
				case 8:
                    writeD(object);
                break;
                case 10:
                    writeC(0);
                    writeD(eobr.getPvpKillsByRace(eobpr.getRace()).intValue());
                    writeD(eobr.getPointsByRace(eobpr.getRace()).intValue());
                    writeD(eobpr.getRace().getRaceId());
                    writeD(object);
                break;
                case 11:
                    writeC(0);
                    writeD(eobr.getPvpKillsByRace(eobpr.getRace()).intValue());
                    writeD(eobr.getPointsByRace(eobpr.getRace()).intValue());
                    writeD(eobpr.getRace().getRaceId());
                    writeD(object);
                break;
            }
        break;
		case 301220000: //Iron Wall Warfront 4.5
            IronWallWarfrontReward iwwr = (IronWallWarfrontReward) instanceReward;
            if (object == null) {
                object = ownerObject;
            }
            IronWallWarfrontPlayerReward iwwpr = iwwr.getPlayerReward(object);
            writeC(type);
            switch (type) {
                case 2:
                    writeD(0);
                    writeD(iwwr.getTime());
                break;
                case 3:
                    writeD(10);
                    writeD(value1);
                    writeD(object);
                    writeD(value2);
			    break;
                case 4:
                    writeD(10);
                    writeD(value1);
                    writeD(object);
                break;
                case 5:
                    writeD((int) iwwpr.getParticipation() * 100);
                    writeD(iwwpr.getRewardExp());
                    writeD(iwwpr.getBonusExp());
					writeD(iwwpr.getRewardAp());
                    writeD(iwwpr.getBonusAp());
                    writeD(iwwpr.getRewardGp());
                    writeD(iwwpr.getBonusGp());
					writeD(iwwpr.getMedalBundle());
                    writeQ(iwwpr.getRewardCount());
					writeD(iwwpr.getBloodMark());
                    writeQ(iwwpr.getRewardCount());
                    writeD(iwwpr.getBonusReward());
                    writeD(iwwpr.getRewardCount());
                    writeD(iwwpr.getBonusReward2());
                    writeD(iwwpr.getRewardCount());
					writeD(iwwpr.getAdditionalReward());
                    writeD(iwwpr.getAdditionalRewardCount());
					writeC(1);
                break;
                case 6:
                    int counter = 0;
                    writeD(100);
                    for (Player player : players) {
                    	if (player.getRace() != Race.ELYOS) {
                    		continue;
                    	}
                    	writeD(15);
                    	writeD(player.getLifeStats().isAlreadyDead() ? 60 : 0);
                    	writeD(player.getObjectId());
                    	counter++;
                    } if (counter < 24) {
                        writeB(new byte[12 * (24 - counter)]);
                    }
					counter = 0;
					for (Player player : players) {
                    	if (player.getRace() != Race.ASMODIANS) {
                    		continue;
                    	}
                    	writeD(15);
                    	writeD(player.getLifeStats().isAlreadyDead() ? 60 : 0);
                    	writeD(player.getObjectId());
                    	counter++;
                    } if (counter < 24) {
                        writeB(new byte[12 * (24 - counter)]);
                    }
                    writeC(0);
                	writeD(iwwr.getPvpKillsByRace(Race.ELYOS).intValue());
                	writeD(iwwr.getPointsByRace(Race.ELYOS).intValue());
                	writeD(0);
                	writeD((iwwr.getInstanceScoreType() == instanceScoreType.PREPARING ? 65535 : 1));
                	writeC(0);
                	writeD(iwwr.getPvpKillsByRace(Race.ASMODIANS).intValue());
                	writeD(iwwr.getPointsByRace(Race.ASMODIANS).intValue());
                	writeD(1);
                	writeD((iwwr.getInstanceScoreType() == instanceScoreType.PREPARING ? 65535 : 1));
                break;
                case 7:
                    ironWallWarfrontTable(Race.ELYOS);
                    ironWallWarfrontTable(Race.ASMODIANS);
                break;
				case 8:
                    writeD(object);
                break;
                case 10:
                    writeC(0);
                    writeD(iwwr.getPvpKillsByRace(iwwpr.getRace()).intValue());
                    writeD(iwwr.getPointsByRace(iwwpr.getRace()).intValue());
                    writeD(iwwpr.getRace().getRaceId());
                    writeD(object);
                break;
                case 11:
                    writeC(0);
                    writeD(iwwr.getPvpKillsByRace(iwwpr.getRace()).intValue());
                    writeD(iwwr.getPointsByRace(iwwpr.getRace()).intValue());
                    writeD(iwwpr.getRace().getRaceId());
                    writeD(object);
                break;
            }
        break;
		case 301310000: //Idgel Dome 4.7
            IdgelDomeReward idr = (IdgelDomeReward) instanceReward;
            if (object == null) {
                object = ownerObject;
            }
            IdgelDomePlayerReward idpr = idr.getPlayerReward(object);
            writeC(type);
            switch (type) {
                case 2:
                    writeD(0);
                    writeD(idr.getTime());
                break;
                case 3:
                    writeD(10);
                    writeD(value1);
                    writeD(object);
                    writeD(value2);
			    break;
                case 4:
                    writeD(10);
                    writeD(value1);
                    writeD(object);
                break;
                case 5:
                    writeD((int) idpr.getParticipation() * 100);
                    writeD(idpr.getRewardExp());
                    writeD(idpr.getBonusExp());
					writeD(idpr.getRewardAp());
                    writeD(idpr.getBonusAp());
                    writeD(idpr.getRewardGp());
                    writeD(idpr.getBonusGp());
					writeD(idpr.getIdgelDomeBox());
                    writeQ(idpr.getRewardCount());
					writeD(idpr.getBloodMark());
                    writeQ(idpr.getRewardCount());
                    writeD(idpr.getBonusReward());
                    writeD(idpr.getRewardCount());
                    writeD(idpr.getBonusReward2());
                    writeD(idpr.getRewardCount());
					writeD(idpr.getAdditionalReward());
                    writeD(idpr.getAdditionalRewardCount());
                    writeC(1);
                break;
                case 6:
                    int counter = 0;
                    writeD(100);
                    for (Player player : players) {
                    	if (player.getRace() != Race.ELYOS) {
                    		continue;
                    	}
                    	writeD(15);
                    	writeD(player.getLifeStats().isAlreadyDead() ? 60 : 0);
                    	writeD(player.getObjectId());
                    	counter++;
                    } if (counter < 24) {
                        writeB(new byte[12 * (24 - counter)]);
                    }
					counter = 0;
					for (Player player : players) {
                    	if (player.getRace() != Race.ASMODIANS) {
                    		continue;
                    	}
                    	writeD(15);
                    	writeD(player.getLifeStats().isAlreadyDead() ? 60 : 0);
                    	writeD(player.getObjectId());
                    	counter++;
                    } if (counter < 24) {
                        writeB(new byte[12 * (24 - counter)]);
                    }
                    writeC(0);
                	writeD(idr.getPvpKillsByRace(Race.ELYOS).intValue());
                	writeD(idr.getPointsByRace(Race.ELYOS).intValue());
                	writeD(0);
                	writeD((idr.getInstanceScoreType() == instanceScoreType.PREPARING ? 65535 : 1));
                	writeC(0);
                	writeD(idr.getPvpKillsByRace(Race.ASMODIANS).intValue());
                	writeD(idr.getPointsByRace(Race.ASMODIANS).intValue());
                	writeD(1);
                	writeD((idr.getInstanceScoreType() == instanceScoreType.PREPARING ? 65535 : 1));
                break;
                case 7:
                    idgelDomeTable(Race.ELYOS);
                    idgelDomeTable(Race.ASMODIANS);
                break;
				case 8:
                    writeD(object);
                break;
                case 10:
                    writeC(0);
                    writeD(idr.getPvpKillsByRace(idpr.getRace()).intValue());
                    writeD(idr.getPointsByRace(idpr.getRace()).intValue());
                    writeD(idpr.getRace().getRaceId());
                    writeD(object);
                break;
                case 11:
                    writeC(0);
                    writeD(idr.getPvpKillsByRace(idpr.getRace()).intValue());
                    writeD(idr.getPointsByRace(idpr.getRace()).intValue());
                    writeD(idpr.getRace().getRaceId());
                    writeD(object);
                break;
            }
        break;
		case 301680000: //Idgel Dome Landmark 5.1
            LandMarkReward lmr = (LandMarkReward) instanceReward;
            if (object == null) {
                object = ownerObject;
            }
            LandMarkPlayerReward lmpr = lmr.getPlayerReward(object);
            writeC(type);
            switch (type) {
                case 2:
                    writeD(0);
                    writeD(lmr.getTime());
                break;
                case 3:
                    writeD(10);
                    writeD(value1);
                    writeD(object);
                    writeD(value2);
			    break;
                case 4:
                    writeD(10);
                    writeD(value1);
                    writeD(object);
                break;
                case 5:
                    writeD((int) lmpr.getParticipation() * 100);
                    writeD(lmpr.getRewardExp());
                    writeD(lmpr.getBonusExp());
					writeD(lmpr.getRewardAp());
                    writeD(lmpr.getBonusAp());
                    writeD(lmpr.getRewardGp());
                    writeD(lmpr.getBonusGp());
					writeD(lmpr.getLandMarkBox());
                    writeQ(lmpr.getRewardCount());
					writeD(lmpr.getBloodMark());
                    writeQ(lmpr.getRewardCount());
                    writeD(lmpr.getBonusReward());
                    writeD(lmpr.getRewardCount());
                    writeD(lmpr.getBonusReward2());
                    writeD(lmpr.getRewardCount());
					writeD(lmpr.getAdditionalReward());
                    writeD(lmpr.getAdditionalRewardCount());
                    writeC(1);
                break;
                case 6:
                    int counter = 0;
                    writeD(100);
                    for (Player player : players) {
                    	if (player.getRace() != Race.ELYOS) {
                    		continue;
                    	}
                    	writeD(15);
                    	writeD(player.getLifeStats().isAlreadyDead() ? 60 : 0);
                    	writeD(player.getObjectId());
                    	counter++;
                    } if (counter < 24) {
                        writeB(new byte[12 * (24 - counter)]);
                    }
					counter = 0;
					for (Player player : players) {
                    	if (player.getRace() != Race.ASMODIANS) {
                    		continue;
                    	}
                    	writeD(15);
                    	writeD(player.getLifeStats().isAlreadyDead() ? 60 : 0);
                    	writeD(player.getObjectId());
                    	counter++;
                    } if (counter < 24) {
                        writeB(new byte[12 * (24 - counter)]);
                    }
                    writeC(0);
                	writeD(lmr.getPvpKillsByRace(Race.ELYOS).intValue());
                	writeD(lmr.getPointsByRace(Race.ELYOS).intValue());
                	writeD(0);
                	writeD((lmr.getInstanceScoreType() == instanceScoreType.PREPARING ? 65535 : 1));
                	writeC(0);
                	writeD(lmr.getPvpKillsByRace(Race.ASMODIANS).intValue());
                	writeD(lmr.getPointsByRace(Race.ASMODIANS).intValue());
                	writeD(1);
                	writeD((lmr.getInstanceScoreType() == instanceScoreType.PREPARING ? 65535 : 1));
                break;
                case 7:
                    landMarkTable(Race.ELYOS);
                    landMarkTable(Race.ASMODIANS);
                break;
				case 8:
                    writeD(object);
                break;
                case 10:
                    writeC(0);
                    writeD(lmr.getPvpKillsByRace(lmpr.getRace()).intValue());
                    writeD(lmr.getPointsByRace(lmpr.getRace()).intValue());
                    writeD(lmpr.getRace().getRaceId());
                    writeD(object);
                break;
                case 11:
                    writeC(0);
                    writeD(lmr.getPvpKillsByRace(lmpr.getRace()).intValue());
                    writeD(lmr.getPointsByRace(lmpr.getRace()).intValue());
                    writeD(lmpr.getRace().getRaceId());
                    writeD(object);
                break;
            }
        break;
		case 300300000: //Empyrean Crucible 2.5
		case 300320000: //Empyrean Crucible Challenge 2.6
			for (CruciblePlayerReward playerReward : (FastList<CruciblePlayerReward>) instanceReward.getInstanceRewards()) {
				writeD(playerReward.getOwner());
				writeD(playerReward.getPoints());
				writeD(instanceScoreType.isEndProgress() ? 3 : 1);
				writeD(playerReward.getInsignia());
				playerCount++;
			} if (playerCount < 6) {
				writeB(new byte[16 * (6 - playerCount)]);
			}
		break;
		case 300040000: //Dark Poeta.
			DarkPoetaReward dpr = (DarkPoetaReward) instanceReward;
			writeD(dpr.getPoints());
			writeD(dpr.getNpcKills());
			writeD(dpr.getGatherCollections());
			writeD(dpr.getRank());
		break;
		case 300540000: //Eternal Bastion 4.8
			for (EternalBastionPlayerReward playerReward : (FastList<EternalBastionPlayerReward>) instanceReward.getInstanceRewards()) {
			    EternalBastionReward etr = (EternalBastionReward) instanceReward;
				writeD(etr.getPoints());
			    writeD(etr.getNpcKills());
			    writeD(0);
			    writeD(etr.getRank());
				writeD(0);
			    writeD(playerReward.getScoreAP());
				writeD(0);
				writeD(0);
				writeD(0);
				if (etr.getPoints() >= 60000) {
				    writeD(188052595); //High Grade Material Box.
				    writeD(playerReward.getHighGradeMaterialBox());
				    writeD(186000242); //Ceramium Medal.
				    writeD(playerReward.getCeramium());
				    writeD(188052598); //Low Grade Material Support Bundle.
				} else {
				    writeD(0);
				    writeD(0);
				    writeD(0);
				    writeD(0);
				    writeD(0);
				} if (etr.getPoints() >= 90000) {
				    writeD(188052594); //Highest Grade Material Box.
				    writeD(playerReward.getHighestGradeMaterialBox());
				    writeD(186000242); //Ceramium Medal.
				    writeD(playerReward.getCeramium());
				    writeD(188052596); //Highest Grade Material Support Bundle.
				} else {
				    writeD(0);
			        writeD(0);
			        writeD(0);
				}
			}
		break;
		case 301400000: //The Shugo Emperor's Vault 4.7.5
		case 301590000: //Emperor Trillirunerk's Safe 4.9.1
			for (ShugoEmperorVaultPlayerReward playerReward : (FastList<ShugoEmperorVaultPlayerReward>) instanceReward.getInstanceRewards()) {
				ShugoEmperorVaultReward sevr = (ShugoEmperorVaultReward) instanceReward;
				writeD(sevr.getPoints());
			    writeD(sevr.getNpcKills());
			    writeD(0);
			    writeD(sevr.getRank());
				writeD(0);
			    writeD(playerReward.getScoreAP());
				writeD(0);
				writeD(0);
				writeD(0);
			    if (sevr.getPoints() >= 165100) {
				    writeD(185000222); //Rusted Vault Key.
				    writeD(playerReward.getRustedVaultKey());
			    } else {
				    writeD(0);
				    writeD(0);
				    writeD(0);
				    writeD(0);
				    writeD(0);
			    } if (sevr.getPoints() >= 878600) {
				    writeD(185000222); //Rusted Vault Key.
				    writeD(playerReward.getRustedVaultKey());
			    } else {
				    writeD(0);
			        writeD(0);
			        writeD(0);
				}
			}
		break;
		case 301500000: //Stonespear Reach 4.8
			for (StonespearReachPlayerReward playerReward : (FastList<StonespearReachPlayerReward>) instanceReward.getInstanceRewards()) {
			    StonespearReachReward srr = (StonespearReachReward) instanceReward;
				writeD(srr.getPoints());
			    writeD(srr.getNpcKills());
			    writeD(srr.getRank());
			}
		break;
		case 301510000: //Sealed Argent Manor 4.9.1
			for (SealedArgentManorPlayerReward playerReward : (FastList<SealedArgentManorPlayerReward>) instanceReward.getInstanceRewards()) {
			    SealedArgentManorReward samr = (SealedArgentManorReward) instanceReward;
				writeD(samr.getPoints());
			    writeD(samr.getNpcKills());
			    writeD(0);
			    writeD(samr.getRank());
				writeD(0);
			    writeD(playerReward.getScoreAP());
				writeD(0);
				writeD(0);
				writeD(0);
				if (samr.getPoints() >= 11500) {
				    writeD(188054115); //Argent Manor Box.
					writeD(playerReward.getArgentManorBox());
					writeD(188054116); //Lesser Argent Manor Box.
				} else {
				    writeD(0);
				    writeD(0);
				    writeD(0);
				    writeD(0);
				    writeD(0);
				} if (samr.getPoints() >= 16000) {
				    writeD(188054114); //Greater Argent Manor Box.
				    writeD(playerReward.getGreaterArgentManorBox());
				} else {
				    writeD(0);
			        writeD(0);
			        writeD(0);
				}
			}
		break;
		case 301630000: //Contaminated Underpath 5.0.5
			for (ContaminatedUnderpathPlayerReward playerReward : (FastList<ContaminatedUnderpathPlayerReward>) instanceReward.getInstanceRewards()) {
				ContaminatedUnderpathReward cur = (ContaminatedUnderpathReward) instanceReward;
				writeD(cur.getPoints());
			    writeD(cur.getNpcKills());
			    writeD(0);
			    writeD(cur.getRank());
				writeD(0);
			    writeD(playerReward.getScoreAP());
				writeD(0);
				writeD(0);
				writeD(0);
			    if (cur.getPoints() >= 50) {
				    writeD(188055664); //Contaminated Underpath Special Pouch.
					writeD(playerReward.getContaminatedUnderpathSpecialPouch());
					writeD(188055599); //Contaminated Highest Reward Bundle.
			    } else {
				    writeD(0);
				    writeD(0);
				    writeD(0);
				    writeD(0);
				    writeD(0);
			    } if (cur.getPoints() >= 540000) {
				    writeD(188055598); //Contaminated Premium Reward Bundle.
				    writeD(playerReward.getContaminatedPremiumRewardBundle());
			    } else {
				    writeD(0);
			        writeD(0);
			        writeD(0);
				}
			}
		break;
		case 301640000: //Secret Munitions Factory 5.0.5
			for (SecretMunitionsFactoryPlayerReward playerReward : (FastList<SecretMunitionsFactoryPlayerReward>) instanceReward.getInstanceRewards()) {
				SecretMunitionsFactoryReward smfr = (SecretMunitionsFactoryReward) instanceReward;
				writeD(smfr.getPoints());
			    writeD(smfr.getNpcKills());
			    writeD(0);
			    writeD(smfr.getRank());
				writeD(0);
			    writeD(playerReward.getScoreAP());
				writeD(0);
				writeD(0);
				writeD(0);
			    if (smfr.getPoints() >= 50) {
				    writeD(188055648); //Mechaturerkâ€™s Special Treasure Box.
					writeD(playerReward.getMechaturerkSpecialTreasureBox());
					writeD(188055647); //Mechaturerkâ€™s Normal Treasure Chest.
			    } else {
				    writeD(0);
				    writeD(0);
				    writeD(0);
				    writeD(0);
				    writeD(0);
			    } if (smfr.getPoints() >= 878600) {
				    writeD(188055475); //Mechaturerk's Secret Box.
				    writeD(playerReward.getMechaturerkSecretBox());
			    } else {
				    writeD(0);
			        writeD(0);
			        writeD(0);
				}
			}
		break;
		case 302000000: //Smoldering Fire Temple 5.1
			for (SmolderingPlayerReward playerReward : (FastList<SmolderingPlayerReward>) instanceReward.getInstanceRewards()) {
				SmolderingReward sr = (SmolderingReward) instanceReward;
				writeD(sr.getPoints());
			    writeD(sr.getNpcKills());
			    writeD(0);
			    writeD(sr.getRank());
				writeD(0);
			    writeD(playerReward.getScoreAP());
				writeD(0);
				writeD(0);
				writeD(0);
			    if (sr.getPoints() >= 165100) {
				    writeD(185000270); //Smoldering Fire Temple Treasure Key.
				    writeD(playerReward.getSmolderingKey());
			    } else {
				    writeD(0);
				    writeD(0);
				    writeD(0);
				    writeD(0);
				    writeD(0);
			    } if (sr.getPoints() >= 878600) {
				    writeD(185000270); //Smoldering Fire Temple Treasure Key.
				    writeD(playerReward.getSmolderingKey());
			    } else {
				    writeD(0);
			        writeD(0);
			        writeD(0);
				}
			}
		break;
		case 302100000: //Fissure Of Oblivion 5.1
			for (OblivionRiftPlayerReward playerReward : (FastList<OblivionRiftPlayerReward>) instanceReward.getInstanceRewards()) {
			    OblivionRiftReward or = (OblivionRiftReward) instanceReward;
				writeD(or.getPoints());
			    writeD(or.getNpcKills());
			    writeD(0);
			    writeD(or.getRank());
				writeD(0);
			    writeD(playerReward.getScoreAP());
				writeD(0);
				writeD(0);
				writeD(0);
				if (or.getPoints() >= 17700) {
				    writeD(186000448); //Frozen Marble Of Memory.
				    writeD(playerReward.getBeadsOfTheFrozen());
				} else {
				    writeD(0);
				    writeD(0);
				    writeD(0);
				    writeD(0);
				    writeD(0);
				} if (or.getPoints() >= 23550) {
				    writeD(186000448); //Frozen Marble Of Memory.
				    writeD(playerReward.getBeadsOfTheFrozen());
				} else {
				    writeD(0);
			        writeD(0);
			        writeD(0);
				}
			}
		break;
		case 300350000: //Arena Of Chaos.
		case 300360000: //Arena Of Discipline.
		case 300420000: //Chaos Training Grounds.
		case 300430000: //Discipline Training Grounds.
		case 300550000: //Arena Of Glory.
			PvPArenaReward arenaReward = (PvPArenaReward) instanceReward;
			PvPArenaPlayerReward rewardedPlayer = arenaReward.getPlayerReward(ownerObject);
			int rank, points;
			boolean isRewarded = arenaReward.isRewarded();
			for (Player player : players) {
				InstancePlayerReward reward = arenaReward.getPlayerReward(player.getObjectId());
				PvPArenaPlayerReward playerReward = (PvPArenaPlayerReward) reward;
				points = playerReward.getPoints();
				rank = arenaReward.getRank(playerReward.getScorePoints());
				writeD(playerReward.getOwner());
				writeD(playerReward.getPvPKills());
				writeD(isRewarded ? points + playerReward.getTimeBonus() : points);
				writeD(0);
				writeC(0);
				writeC(player.getPlayerClass().getClassId());
				writeC(1);
				writeC(rank);
				writeD(playerReward.getRemaningTime());
				writeD(isRewarded ? playerReward.getTimeBonus() : 0);
				writeD(0);
				writeD(0);
				writeH(isRewarded ? (short)(playerReward.getParticipation() * 100) : 0);
				writeS(player.getName(), 54);
				playerCount++;
			} if (playerCount < 12) {
				writeB(new byte[92 * (12 - playerCount)]);
			} if (isRewarded && arenaReward.canRewarded() && rewardedPlayer != null) {
				writeD(rewardedPlayer.getBasicAP());
				writeD(rewardedPlayer.getBasicGP());
				writeD(rewardedPlayer.getRankingAP());
				writeD(rewardedPlayer.getRankingGP());
				writeD(rewardedPlayer.getScoreAP());
				writeD(rewardedPlayer.getScoreGP());
				if (mapId == 300550000) { //Arena Of Glory.
					writeB(new byte[32]);
					if (rewardedPlayer.getMithrilMedal() != 0) {
						writeD(186000147); //Mithril Medal.
						writeD(rewardedPlayer.getMithrilMedal());
					} else if (rewardedPlayer.getPlatinumMedal() != 0) {
						writeD(186000096); //Platinum Medal.
						writeD(rewardedPlayer.getPlatinumMedal());
					} else if (rewardedPlayer.getLifeSerum() != 0) {
						writeD(162000077); //Fine Life Serum.
						writeD(rewardedPlayer.getLifeSerum());
					} else {
						writeD(0);
						writeD(0); 
					} if (rewardedPlayer.getGloriousInsignia() != 0) {
						writeD(182213259); //Glorious Insignia.
						writeD(rewardedPlayer.getGloriousInsignia());
					} else {
						writeD(0);
						writeD(0);
					}
				} else {
					writeD(186000130); //Crucible Insignia.
					writeD(rewardedPlayer.getBasicCrucible());
					writeD(rewardedPlayer.getScoreCrucible());
					writeD(rewardedPlayer.getRankingCrucible());
					writeD(186000137); //Courage Insignia.
					writeD(rewardedPlayer.getBasicCourage());
					writeD(rewardedPlayer.getScoreCourage());
					writeD(rewardedPlayer.getRankingCourage());
					writeD(186000442); //ë¬´í•œì�˜ í…œíŽ˜ë¥´ íœ˜ìž¥.
					writeD(rewardedPlayer.getBasicInfinity());
					writeD(rewardedPlayer.getScoreInfinity());
					writeD(rewardedPlayer.getRankingInfinity());
					if (rewardedPlayer.getOpportunity() != 0) {
						writeD(186000165); //Opportunity Token.
						writeD(rewardedPlayer.getOpportunity());
					} else if (rewardedPlayer.getGloryTicket() != 0) {
						writeD(186000185); //Arena Of Glory Ticket.
						writeD(rewardedPlayer.getGloryTicket());
					} else {
						writeD(0);
						writeD(0);
					}
					writeD(0);
					writeD(0); 
				}
			} else {
				writeB(new byte[60]);
			}
			writeD(arenaReward.getBuffId());
			writeD(0);
			writeD(arenaReward.getRound());
			writeD(arenaReward.getCapPoints());
			writeD(3);
			writeD(0);
			break;
		}
	}
	
	private void fillTableWithGroup(Race race) {
		int count = 0;
		DredgionReward dredgionReward = (DredgionReward) instanceReward;
		for (Player player : players) {
			if (!race.equals(player.getRace())) {
				continue;
			}
			InstancePlayerReward playerReward = dredgionReward.getPlayerReward(player.getObjectId());
			DredgionPlayerReward dpr = (DredgionPlayerReward) playerReward;
			writeD(playerReward.getOwner());
			writeD(player.getAbyssRank().getRank().getId());
			writeD(dpr.getPvPKills());
			writeD(dpr.getMonsterKills());
			writeD(dpr.getZoneCaptured());
			writeD(dpr.getPoints());
			if (instanceScoreType.isEndProgress()) {
				boolean winner = race.equals(dredgionReward.getWinningRace());
				writeD((winner ? dredgionReward.getWinnerPoints() : dredgionReward.getLooserPoints()) + (int) (dpr.getPoints() * 1.6f));
				writeD((winner ? dredgionReward.getWinnerPoints() : dredgionReward.getLooserPoints()));
			} else {
				writeB(new byte[8]);
			}
			writeC(player.getPlayerClass().getClassId());
			writeC(0);
			writeS(player.getName(), 54);
			count++;
		} if (count < 6) {
			writeB(new byte[88 * (6 - count)]);
		}
	}
	
	private void kamarBattlefieldTable(Race race) {
    	int count = 0;
    	KamarBattlefieldReward kbr = (KamarBattlefieldReward) instanceReward;
    	boolean isFirst = false;
    	for (Player player : players) {
    		if (!race.equals(player.getRace())) {
    			continue;
    		}
    		KamarBattlefieldPlayerReward kbpr = kbr.getPlayerReward(player.getObjectId());
    		writeD(player.getObjectId());
    		writeC(player.getPlayerClass().getClassId());
    		writeC(player.getAbyssRank().getRank().getId());
    		writeC(0);
    		writeH(0);
			writeD(kbpr.getPvPKills());
			writeD(kbpr.getPoints());
			writeS(player.getName(), 52);
			count++;
    	}
    	if (count < 12) {
            writeB(new byte[69 * (12 - count)]);
        }
    	writeB(new byte[828]);
    }
	
	private void engulfedOphidanBridgeTable(Race race) {
    	int count = 0;
    	EngulfedOphidanBridgeReward eobr = (EngulfedOphidanBridgeReward) instanceReward;
    	boolean isFirst = false;
    	for (Player player : players) {
    		if (!race.equals(player.getRace())) {
    			continue;
    		}
    		EngulfedOphidanBridgePlayerReward eobpr = eobr.getPlayerReward(player.getObjectId());
    		writeD(player.getObjectId());
    		writeC(player.getPlayerClass().getClassId());
    		writeC(player.getAbyssRank().getRank().getId());
    		writeC(0);
    		writeH(0);
			writeD(eobpr.getPvPKills());
			writeD(eobpr.getPoints());
			writeS(player.getName(), 52);
			count++;
    	}
    	if (count < 12) {
            writeB(new byte[69 * (12 - count)]);
        }
    	writeB(new byte[828]);
    }
	
	private void ironWallWarfrontTable(Race race) {
    	int count = 0;
    	IronWallWarfrontReward iwwr = (IronWallWarfrontReward) instanceReward;
    	boolean isFirst = false;
    	for (Player player : players) {
    		if (!race.equals(player.getRace())) {
    			continue;
    		}
    		IronWallWarfrontPlayerReward iwwpr = iwwr.getPlayerReward(player.getObjectId());
    		writeD(player.getObjectId());
    		writeC(player.getPlayerClass().getClassId());
    		writeC(player.getAbyssRank().getRank().getId());
    		writeC(0);
    		writeH(0);
			writeD(iwwpr.getPvPKills());
			writeD(iwwpr.getPoints());
			writeS(player.getName(), 52);
			count++;
    	}
    	if (count < 12) {
            writeB(new byte[69 * (12 - count)]);
        }
    	writeB(new byte[828]);
    }
	
	private void idgelDomeTable(Race race) {
    	int count = 0;
    	IdgelDomeReward idr = (IdgelDomeReward) instanceReward;
    	boolean isFirst = false;
    	for (Player player : players) {
    		if (!race.equals(player.getRace())) {
    			continue;
    		}
    		IdgelDomePlayerReward idpr = idr.getPlayerReward(player.getObjectId());
    		writeD(player.getObjectId());
    		writeC(player.getPlayerClass().getClassId());
    		writeC(player.getAbyssRank().getRank().getId());
    		writeC(0);
    		writeH(0);
			writeD(idpr.getPvPKills());
			writeD(idpr.getPoints());
			writeS(player.getName(), 52);
			count++;
    	}
    	if (count < 12) {
            writeB(new byte[69 * (12 - count)]);
        }
    	writeB(new byte[828]);
    }
	
	private void landMarkTable(Race race) {
    	int count = 0;
    	LandMarkReward lmr = (LandMarkReward) instanceReward;
    	boolean isFirst = false;
    	for (Player player : players) {
    		if (!race.equals(player.getRace())) {
    			continue;
    		}
    		LandMarkPlayerReward lmpr = lmr.getPlayerReward(player.getObjectId());
    		writeD(player.getObjectId());
    		writeC(player.getPlayerClass().getClassId());
    		writeC(player.getAbyssRank().getRank().getId());
    		writeC(0);
    		writeH(0);
			writeD(lmpr.getPvPKills());
			writeD(lmpr.getPoints());
			writeS(player.getName(), 52);
			count++;
    	} if (count < 12) {
            writeB(new byte[69 * (12 - count)]);
        }
    	writeB(new byte[828]);
    }
}