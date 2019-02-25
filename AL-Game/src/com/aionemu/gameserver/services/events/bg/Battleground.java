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

import javolution.util.FastMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ScheduledFuture;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.dao.LadderDAO;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Gatherable;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureSeeState;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.gameobjects.state.CreatureVisualState;
import com.aionemu.gameserver.model.summons.UnsummonType;
import com.aionemu.gameserver.model.team2.TeamType;
import com.aionemu.gameserver.model.team2.alliance.PlayerAlliance;
import com.aionemu.gameserver.model.team2.alliance.PlayerAllianceMember;
import com.aionemu.gameserver.model.team2.alliance.PlayerAllianceService;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.model.team2.group.PlayerGroupService;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.DuelService;
import com.aionemu.gameserver.services.StaticDoorService;
import com.aionemu.gameserver.services.events.LadderService;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.player.PlayerReviveService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.skillengine.effect.EffectTemplate;
import com.aionemu.gameserver.skillengine.model.DispelCategoryType;
import com.aionemu.gameserver.skillengine.model.SkillTargetSlot;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.WorldPosition;

/**
 * Created by wanke on 12/02/2017.
 */

public abstract class Battleground
{
    private static final Logger log = LoggerFactory.getLogger(Battleground.class);
    protected static final int TELEPORT_DEFAULT_DELAY = 0;
    protected final int K_VALUE = 20;
	
    @SuppressWarnings("serial")
    protected static final Map<String, Class<?>> aliases = new HashMap<String, Class<?>>() {
		{
            put("[DeathMatch]", DeathmatchBg.class);
            put("[1 VS 1]", SoloSurvivorBg.class);
            put("[Team VS Team]", TwoTeamBg.class);
            put("[Team VS Team]", TwoTeamSmallBg.class);
        }
    };
    protected String name = "";
    protected String description = "";
    protected int minSize = 0;
    protected int maxSize = 0;
    protected int teamCount = 0;
    protected int matchLength = 0;
    protected List<BattlegroundMap> maps = new ArrayList<BattlegroundMap>();
    protected int instanceId = -1;
    protected Integer bgId = -1;
    protected long startStamp = 0;
    protected boolean isTournament = false;
    protected boolean isEvent = false;
    protected boolean is1v1 = false;
    protected WorldMapInstance instance = null;
    protected ScheduledFuture<?> expireTask = null;
    protected ScheduledFuture<?> backgroundTask = null;
    protected int backgroundCounter = 0;
    protected BattlegroundMap map = null;
    protected int mapId = 0;
    protected boolean isDone = false;
    protected boolean shouldDisband = true;
    protected boolean teamBased = false;
    protected Map<Integer, WorldPosition> previousLocations = new FastMap<Integer, WorldPosition>();
    protected List<Player> _players = Collections.synchronizedList(new ArrayList<Player>());
    protected List<PlayerGroup> _groups = Collections.synchronizedList(new ArrayList<PlayerGroup>());
    protected List<PlayerAlliance> _alliances = Collections.synchronizedList(new ArrayList<PlayerAlliance>());
    protected List<Player> _spectators = Collections.synchronizedList(new ArrayList<Player>());
    protected Map<Integer, AionObject> _leavers = Collections.synchronizedMap(new FastMap<Integer, AionObject>());
    public abstract void createMatch(List<Integer> players);
    public abstract void startMatch();
    public abstract void onDie(final Player player, Creature lastAttacker);
    public abstract void onLeave(Player player, boolean isLogout, boolean isAfk);
	
    public void onArtifactDie(int teamIndex) {
    }
	
    public void onResourceGathered(Gatherable resource, int teamIndex) {
    }
	
    public boolean isStealthRestricted() {
        return false;
    }
	
    public boolean isEffectAllowed(EffectTemplate et) {
        return true;
    }
	
    public boolean isFlightRestricted() {
        if (map != null && map.isRestrictFlight()) {
            return true;
        }
        return false;
    }
	
    public boolean createTournament(List<List<Player>> teams) {
        return false;
    }
	
    public List<SpawnPosition> getSpawnPositions() {
        if (map == null) {
            return new ArrayList<SpawnPosition>();
        } else {
            return map.getSpawnPoints();
        }
    }
	
    public boolean isInBg(Player player) {
        for (BattlegroundMap bgMap : maps) {
            if (bgMap.getMapId() == player.getWorldId()) {
                return true;
            }
        }
        return false;
    }
	
    protected int getRandomSize(int playerCount) {
        int avgCount = (int) Math.floor(playerCount / getTeamCount());
        if (isTeamBased()) {
            return avgCount;
        } else if (avgCount <= getMaxSize() && avgCount >= getMinSize()) {
            return avgCount;
        } else if (avgCount < getMinSize()) {
            return getMinSize();
        } else if (isEvent() || isTournament()) {
            return getMaxSize();
        } else {
            return Rnd.get(getMinSize(), getMaxSize());
        }
    }
	
    protected boolean handleQueueSolo(List<Integer> players) {
        int size = getRandomSize(players.size());
        if (players.size() < size) {
            return false;
        }
        int playerIndex = 0;
        while (players.size() > 0) {
            int objId = players.remove(0);
            Player pl = World.getInstance().findPlayer(objId);
            if (pl == null) {
                continue;
            }
            pl.setBgIndex(playerIndex);
            pl.setBattleground(this);
            addPlayer(pl);
            players.remove((Integer) objId);
            removePlayerFromTeam(pl);
            playerIndex++;
            if (getPlayers().size() >= size) {
                break;
            }
        }
        return true;
    }
	
    protected boolean handleQueueGroup(List<Integer> players) {
        int groupSize = getRandomSize(players.size());
        if (players.size() < groupSize * getTeamCount()) {
            return false;
        }
        int groupIndex = 0;
        while (players.size() >= groupSize) {
            List<Player> groupPlayers = new ArrayList<Player>();
            while (groupPlayers.size() < groupSize && players.size() > 0) {
                int objId = players.remove(0);
                Player pl = World.getInstance().findPlayer(objId);
                if (pl != null) {
                    removePlayerFromTeam(pl);
                    groupPlayers.add(pl);
                    pl.setBattleground(this);
                    players.remove((Integer) objId);
                }
            }
            PlayerGroup group = PlayerGroupService.createGroup(groupPlayers.get(0));
            for (int i = 1; i < groupPlayers.size(); i++) {
                PlayerGroupService.addPlayer(group, groupPlayers.get(i));
            }
            group.setBgIndex(groupIndex);
            addGroup(group);
            groupIndex++;
            if (getGroups().size() >= getTeamCount()) {
                break;
            }
        }
        return true;
    }
	
    protected boolean handleQueueAlliance(List<Integer> players) {
        int allianceSize = getRandomSize(players.size());
        if (players.size() < allianceSize * getTeamCount()) {
            return false;
        }
        int allianceIndex = 0;
        while (players.size() >= allianceSize) {
            List<Player> alliancePlayers = new ArrayList<Player>();
            while (alliancePlayers.size() < allianceSize && players.size() > 0) {
                int objId = players.remove(0);
                Player pl = World.getInstance().findPlayer(objId);
                if (pl != null) {
                    alliancePlayers.add(pl);
                    pl.setBattleground(this);
                    removePlayerFromTeam(pl);
                }
            }
            PlayerAlliance alliance = new PlayerAlliance(new PlayerAllianceMember(alliancePlayers.get(0)), TeamType.ALLIANCE);
            for (int i = 0; i < alliancePlayers.size(); i++) {
                PlayerAllianceService.addPlayerToAlliance(alliance, alliancePlayers.get(i));
            }
            alliance.setBgIndex(allianceIndex);
            addAlliance(alliance);
            allianceIndex++;
            if (getAlliances().size() >= getTeamCount()) {
                break;
            }
        }
        return true;
    }
	
    protected boolean createGroups(List<List<Player>> teams) {
        if (teams.size() < getTeamCount()) {
            return false;
        }
        int groupSize = 100;
        for (List<Player> team : teams) {
            if (team.size() < groupSize) {
                groupSize = team.size();
            }
        } if (groupSize < 1) {
            return false;
        }
        int groupIndex = 0;
        while (getGroups().size() < getTeamCount()) {
            List<Player> players = teams.remove(0);
            while (players.size() > groupSize) {
                players.remove(players.size());
            }
            for (Player pl : players) {
                pl.setBattleground(this);
                removePlayerFromTeam(pl);
            }
            PlayerGroup group = PlayerGroupService.createGroup(players.get(0));
            for (int i = 1; i < players.size(); i++) {
                PlayerGroupService.addPlayer(group, players.get(i));
            }
            group.setBgIndex(groupIndex);
            addGroup(group);
            groupIndex++;
        }
        return true;
    }
	
    protected boolean createAlliances(List<List<Player>> teams) {
        if (teams.size() < getTeamCount()) {
            return false;
        }
        int allianceSize = 100;
        for (List<Player> team : teams) {
            if (team.size() < allianceSize) {
                allianceSize = team.size();
            }
        } if (allianceSize < 1) {
            return false;
        }
        int allianceIndex = 0;
        while (getAlliances().size() < getTeamCount()) {
            List<Player> players = teams.remove(0);
            while (players.size() > allianceSize) {
                players.remove(players.size());
            }
            for (Player pl : players) {
                pl.setBattleground(this);
                removePlayerFromTeam(pl);
            }
            PlayerAlliance alliance = new PlayerAlliance(new PlayerAllianceMember(players.get(0)), TeamType.ALLIANCE);
            for (int i = 0; i < players.size(); i++) {
                PlayerAllianceService.addPlayerToAlliance(alliance, players.get(i));
            }
            alliance.setBgIndex(allianceIndex);
            addAlliance(alliance);
            allianceIndex++;
        }
        return true;
    }
	
    protected boolean createPlayers(List<List<Player>> players) {
        List<Integer> playerList = new ArrayList<Integer>();
        for (List<Player> plList : players) {
            for (Player pl : plList) {
                playerList.add(pl.getObjectId());
            }
        }
        while (playerList.size() > getMaxSize()) {
            playerList.remove(playerList.size() - 1);
        }
        return handleQueueSolo(playerList);
    }
	
    protected void addPlayer(Player player) {
        if (!getPlayers().contains(player)) {
            getPlayers().add(player);
        }
    }
	
    protected void addGroup(PlayerGroup group) {
        if (!getGroups().contains(group)) {
            getGroups().add(group);
        }
    }
	
    protected void addAlliance(PlayerAlliance alliance) {
        if (!getAlliances().contains(alliance)) {
            getAlliances().add(alliance);
        }
    }
	
    protected void freezeNoEnd(Player player) {
        player.getEffectController().setAbnormal(AbnormalState.PARALYZE.getId());
        player.getEffectController().updatePlayerEffectIcons();
        player.getEffectController().broadCastEffects();
    }
	
    protected void freezePlayer(final Player player, int duration) {
        player.getEffectController().setAbnormal(AbnormalState.PARALYZE.getId());
        player.getEffectController().updatePlayerEffectIcons();
        player.getEffectController().broadCastEffects();
        ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                player.getEffectController().unsetAbnormal(AbnormalState.PARALYZE.getId());
                player.getEffectController().updatePlayerEffectIcons();
                player.getEffectController().broadCastEffects();
            }
        }, duration);
    }
	
    protected void healPlayer(Player player) {
        this.healPlayer(player, true);
    }
	
    protected void healPlayer(Player player, boolean resetDp) {
        player.getLifeStats().increaseHp(SM_ATTACK_STATUS.TYPE.HP, player.getLifeStats().getMaxHp() + 1);
        player.getLifeStats().increaseMp(SM_ATTACK_STATUS.TYPE.MP, player.getLifeStats().getMaxMp() + 1);
        if (resetDp) {
            player.getCommonData().setDp(0);
        }
    }
	
    protected void performTeleport(Player player, float x, float y, float z) {
        player.getController().abortCast();
        previousLocations.put(player.getObjectId(), player.getPosition().clone());
        TeleportService2.teleportTo(player, getMapId(), getInstanceId(), x, y, z);
    }
	
    protected void performCdReset(Player player) {
        List<Integer> delayIds = new ArrayList<Integer>();
        if (player.getSkillCoolDowns() != null) {
            long currentTime = System.currentTimeMillis();
            for (Map.Entry<Integer, Long> en : player.getSkillCoolDowns().entrySet()) {
                delayIds.add(en.getKey());
            } for (Integer delayId : delayIds) {
                player.setSkillCoolDown(delayId, currentTime);
            }
            delayIds.clear();
            PacketSendUtility.sendPacket(player, new SM_SKILL_COOLDOWN(player.getSkillCoolDowns()));
        }
    }
	
    protected void returnToPreviousLocation(Player player) {
        player.setBattleground(null);
        if (player.getLifeStats().isAlreadyDead()) {
            PlayerReviveService.bgRevive(player);
        }
        healPlayer(player, false);
        endTimer(player);
        WorldPosition previousPos = previousLocations.get(player.getObjectId());
        if (previousPos == null) {
            TeleportService2.moveToBindLocation(player, true);
            return;
        }
        previousLocations.remove(player.getObjectId());
        TeleportService2.teleportTo(player, previousPos.getMapId(), previousPos.getX(), previousPos.getY(), previousPos.getZ() + 1);
    }
	
    protected void scheduleAnnouncement(final Player player, final String sender, final String msg, int delay) {
        if (delay > 0) {
            ThreadPoolManager.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    PacketSendUtility.sendSys3Message(player, sender, msg);
                }
            }, delay);
        } else {
            PacketSendUtility.sendSys3Message(player, sender, msg);
        }
    }
	
    protected void scheduleAnnouncement(Player player, String msg, int delay) {
        if (player.getBattleground() instanceof SoloSurvivorBg) {
            this.scheduleAnnouncement(player, "1vs1", msg, delay);
        } else if (player.getBattleground() instanceof DeathmatchBg) {
            this.scheduleAnnouncement(player, "DM", msg, delay);
        } else {
            this.scheduleAnnouncement(player, "BG", msg, delay);
        }
    }
	
    protected void specAnnounce(String msg) {
        for (Iterator<Player> it = getSpectators().iterator(); it.hasNext();) {
            PacketSendUtility.sendSys3Message(it.next(), "BG", msg);
        }
    }
	
    protected void specAnnounce(final String msg, int delay) {
        ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                for (Iterator<Player> it = getSpectators().iterator(); it.hasNext();) {
                    PacketSendUtility.sendSys3Message(it.next(), "BG", msg);
                }
            }
        }, delay);
    }
	
    protected void scheduleCountdown(Player player, int length, int startTime) {
        for (int i = length; i > 0; i--) {
            scheduleAnnouncement(player, "The match start in " + i + " seconds!", startTime - i * 1000);
        }
    }
	
    protected void scheduleGroupDisband(final PlayerGroup group, int delay) {
        ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                while (group.size() > 0) {
                    PlayerGroupService.removePlayer((Player) group.getMembers().toArray()[0]);
                }
            }
        }, delay);
    }
	
    protected void scheduleAllianceDisband(final PlayerAlliance alliance, int delay) {
        ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                while (alliance.size() > 0) {
                    PlayerAllianceService.removePlayer(((Player) alliance.getMembers().toArray()[0]));
                }
            }
        }, delay);
    }
	
    protected void preparePlayer(final Player pl, int time) {
        this.preparePlayer(pl, time, true);
    }
	
    protected void preparePlayer(final Player pl, int time, boolean announce) {
        DuelService.getInstance().loseDuel(pl);
        pl.setKillStreak(0);
        pl.setLastAction();
        pl.getFlyController().endFly(true);
        if (pl.getLifeStats().isAlreadyDead()) {
            PlayerReviveService.skillRevive(pl);
        }
        healPlayer(pl);
        InstanceService.registerPlayerWithInstance(getInstance(), pl);
        if (time > 0) {
            freezePlayer(pl, time);
            scheduleCountdown(pl, 5, time);
        } if (announce) {
            pl.setTotalKills(0);
            removecd(pl);
            if (time > 0) {
                scheduleAnnouncement(pl, "You have join " + getName() + " battleground!", 0);
                scheduleAnnouncement(pl, "Description: " + getDescription(), 10000);
                scheduleAnnouncement(pl, "The match begin's!!!", time);
				//sendEventPacket(StageType.PVP_STAGE_1, 0);
            }
            ThreadPoolManager.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    pl.getEffectController().removeAllEffects();
                }
            }, 2500);
            ThreadPoolManager.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    createTimer(pl, getSecondsLeft());
                }
            }, time - 5000);
        } else {
            pl.getEffectController().removeAbnormalEffectsByTargetSlot(SkillTargetSlot.DEBUFF);
            pl.getEffectController().removeEffectByDispelCat(DispelCategoryType.ALL, SkillTargetSlot.DEBUFF, 100, 2, 100, false);
            pl.setTarget(null);
            PacketSendUtility.sendPacket(pl, new SM_TARGET_SELECTED(pl));
            scheduleAnnouncement(pl, "The match begin's!!!", time);
            createTimer(pl, getSecondsLeft());
        }
    }
	
    public void removecd(Player player) {
        List<Integer> delay = new ArrayList<Integer>();
        if (player.getSkillCoolDowns() != null) {
            for (Map.Entry<Integer, Long> en : player.getSkillCoolDowns().entrySet()) {
                delay.add(en.getKey());
			} for (Integer delayId : delay) {
                player.setSkillCoolDown(delayId, 0);
			}
            delay.clear();
            PacketSendUtility.sendPacket(player, new SM_SKILL_COOLDOWN(player.getSkillCoolDowns()));
        }
    }
	
    protected void resetPlayerKnownlist(final Player player, int delay) {
        if (delay > 0) {
            ThreadPoolManager.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    player.clearKnownlist();
                    PacketSendUtility.sendPacket(player, new SM_PLAYER_INFO(player, false));
                    PacketSendUtility.sendPacket(player, new SM_MOTION(player.getMotions().getMotions().values()));
                    player.getEffectController().updatePlayerEffectIcons();
                    player.getKnownList().doUpdate();
                }
            }, delay);
        } else {
            player.clearKnownlist();
            PacketSendUtility.sendPacket(player, new SM_PLAYER_INFO(player, false));
            PacketSendUtility.sendPacket(player, new SM_MOTION(player.getMotions().getMotions().values()));
            player.getEffectController().updatePlayerEffectIcons();
            player.getKnownList().doUpdate();
        }
    }
	
    public int getSecondsLeft() {
        return (getMatchLength() - Math.round((float) (System.currentTimeMillis() - getStartStamp()) / 1000));
    }
	
    protected void createTimer(Player player, int seconds) {
        PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(0, seconds));
    }
	
    protected void endTimer(Player player) {
        PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(0, 0));
    }
	
    protected void playerWinMatch(Player player, int ratingChange) {
        if (is1v1()) {
            return;
        }
        getLadderDAO().addWin(player);
        getLadderDAO().addRating(player, Math.round(ratingChange / (getLadderDAO().getRating(player) * 0.0015f)));
    }
	
    protected void playerLoseMatch(Player player, int ratingChange) {
        if (is1v1()) {
            return;
        }
        getLadderDAO().addLoss(player);
        getLadderDAO().addRating(player, Math.round(ratingChange * (getLadderDAO().getRating(player) * 0.0015f)));
    }
	
    protected void performLadderUpdate(Collection<Player> winner, Collection<Player> loser) {
        int avgWinnerRating = 0;
        int avgLoserRating = 0;
        for (Player pl : winner) {
            getLadderDAO().addWin(pl);
            avgWinnerRating += getLadderDAO().getRating(pl);
        } for (Player pl : winner) {
            getLadderDAO().addLoss(pl);
            avgWinnerRating += getLadderDAO().getRating(pl);
        } if (winner.size() > 0) {
            avgWinnerRating = avgWinnerRating / winner.size();
        } if (loser.size() > 0) {
            avgLoserRating = avgLoserRating / loser.size();
        }
        int ratingChange = calcRatingChange(avgWinnerRating, avgLoserRating);
        for (Player pl : winner) {
            getLadderDAO().addRating(pl, +ratingChange);
        } for (Player pl : loser) {
            getLadderDAO().addRating(pl, -ratingChange);
        }
    }
	
    protected int calcRatingChange(int ratingA, int ratingB) {
        return (int) Math.round(K_VALUE * (1 / (1 + Math.pow(10, ((float) ratingB - (float) ratingA) / 400))));
    }
	
    protected void removePlayerFromTeam(Player player) {
        if (player.isInGroup2()) {
            PlayerGroupService.removePlayer(player);
        } if (player.isInAlliance2()) {
            PlayerAllianceService.removePlayer(player);
        }
    }
	
    protected void startBackgroundTask() {
        setBackgroundTask(ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                backgroundCounter++;
                zCheck();
                if ((backgroundCounter % 5) == 0) {
                    backgroundCounter = 0;
                }
            }
        }, 30 * 1000, 1 * 1000));
        ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if (getBackgroundTask() != null) {
                    getBackgroundTask().cancel(true);
                }
            }
        }, 10 * getMatchLength() * 1000);
    }
	
    protected void zCheck() {
        if (getGroups().size() > 0) {
            for (PlayerGroup group : getGroups()) {
                for (Player pl : group.getMembers()) {
                    if (pl == null || pl.getLifeStats().isAlreadyDead() || map.getMapId() != pl.getWorldId() || pl.getBattleground() == null) {
                        continue;
                    } if (pl.getZ() < map.getKillZ()) {
                        pl.getLifeStats().reduceHp(100000, pl);
                        //PacketSendUtility.sendPacket(pl, new SM_ATTACK_STATUS(pl, SM_ATTACK_STATUS.TYPE.FALL_DAMAGE, 0, -100000));
                    }
                }
            }
        } if (getAlliances().size() > 0) {
            for (PlayerAlliance alliance : getAlliances()) {
                for (Player pl : alliance.getMembers()) {
                    if (pl == null || pl.getLifeStats().isAlreadyDead() || map.getMapId() != pl.getWorldId() || pl.getBattleground() == null) {
                        continue;
                    } if (pl.getZ() < map.getKillZ()) {
                        pl.getLifeStats().reduceHp(100000, pl);
                        //PacketSendUtility.sendPacket(pl, new SM_ATTACK_STATUS(pl, SM_ATTACK_STATUS.TYPE.FALL_DAMAGE, 0, -100000));
                    }
                }
            }
        } if (getPlayers().size() > 0) {
            for (Player pl : getPlayers()) {
                if (pl == null || pl.getLifeStats().isAlreadyDead() || map.getMapId() != pl.getWorldId() || pl.getBattleground() == null) {
                    continue;
                } if (pl.getZ() < map.getKillZ()) {
                    pl.getLifeStats().reduceHp(100000, pl);
                    //PacketSendUtility.sendPacket(pl, new SM_ATTACK_STATUS(pl, SM_ATTACK_STATUS.TYPE.FALL_DAMAGE, 0, -100000));
                }
            }
        }
    }
	
    protected void onLeaveDefault(Player player, boolean isLogout, boolean isAfk) {
        if (player.isSpectating()) {
            onSpectatorLeave(player, false);
            return;
        } if (player.isInAlliance2()) {
            getLeavers().put(player.getObjectId(), player.getPlayerAlliance2());
        } else if (player.isInGroup2()) {
            getLeavers().put(player.getObjectId(), player.getPlayerGroup2());
        } else {
            getLeavers().put(player.getObjectId(), null);
        } if (isLogout) {
            TeleportService2.moveToBindLocation(player, true);
        } else if (!isAfk) {
            if (!this.isDone && !player.getController().isInShutdownProgress()) {
                scheduleAnnouncement(player, "You are penalized for leaving the battleground!", 10000);
            } else {
                returnToPreviousLocation(player);
                scheduleAnnouncement(player, "You are penalized for being absent too long.", 10000);
            } if (!isAfk) {
                List<Player> players = getPlayers();
                synchronized (players) {
                    players.remove(player);
                }
            }
            player.setBattleground(null);
            if (!this.isDone && !player.getController().isInShutdownProgress()) {
                getLadderDAO().addLeave(player);
                getLadderDAO().addRating(player, -K_VALUE);
            } if (isLogout || isAfk) {
                player.setAfk(true);
            }
            removePlayerFromTeam(player);
            endTimer(player);
        }
    }
	
    public void onDie(Creature creature, Creature lastAttacker) {
    }
	
    protected void onDieDefault(Player player, Creature lastAttacker) {
        Summon summon = player.getSummon();
        if (summon != null) {
            summon.getController().release(UnsummonType.UNSPECIFIED);
        }
        PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, lastAttacker == null ? 0 : lastAttacker.getObjectId()), true);
        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_DEATH_MESSAGE_ME);
        player.getMoveController().abortMove();
        player.setState(CreatureState.DEAD);
        player.getObserveController().notifyDeathObservers(player);
        player.getEffectController().removeAbnormalEffectsByTargetSlot(SkillTargetSlot.DEBUFF);
        player.getEffectController().removeEffectByDispelCat(DispelCategoryType.ALL, SkillTargetSlot.DEBUFF, 100, 3, 100, false);
        player.setTarget(null);
        PacketSendUtility.sendPacket(player, new SM_TARGET_SELECTED(player));
        if (lastAttacker instanceof Player && lastAttacker.getObjectId() != player.getObjectId()) {
            Player killer = (Player) lastAttacker;
            killer.setTotalKills(killer.getTotalKills() + 1);
            if (killer.getPlayerGroup2() != null) {
                killer.getPlayerGroup2().setKillCount(killer.getPlayerGroup2().getKillCount() + 1);
            } else if (killer.getPlayerAlliance2() != null) {
                killer.getPlayerAlliance2().setKillCount(killer.getPlayerAlliance2().getKillCount() + 1);
            }
        }
    }
	
    protected void onEndFirstDefault() {
        if (getExpireTask() != null) {
            getExpireTask().cancel(true);
        } if (getBackgroundTask() != null) {
            getBackgroundTask().cancel(true);
        } if (getPlayers().size() > 0) {
            for (Player pl : getPlayers()) {
                if (!pl.getLifeStats().isAlreadyDead()) {
                    healPlayer(pl, false);
                }
            }
        } if (getGroups().size() > 0) {
            for (PlayerGroup group : getGroups()) {
                for (Player pl : group.getMembers()) {
                    if (!pl.getLifeStats().isAlreadyDead()) {
                        healPlayer(pl, false);
                    }
                }
            }
        } if (getAlliances().size() > 0) {
            for (PlayerAlliance alliance : getAlliances()) {
                for (Player pl : alliance.getMembers()) {
                    if (pl == null) {
                        continue;
                    } if (!pl.getLifeStats().isAlreadyDead()) {
                        healPlayer(pl, false);
                    }
                }
            }
        }
    }
	
    protected void onEndDefault() {
        LadderService.getInstance().onBgEnd(this);
        if (getPlayers().size() > 0) {
            for (Player pl : getPlayers()) {
                freezePlayer(pl, 7500);
            }
            ThreadPoolManager.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    for (Player pl : getPlayers()) {
                        returnToPreviousLocation(pl);
                    }
                }
            }, 5000);
        } if (getGroups().size() > 0) {
            for (PlayerGroup group : getGroups()) {
                for (Player pl : group.getMembers()) {
                    freezePlayer(pl, 7500);
                }
            }
            ThreadPoolManager.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    for (PlayerGroup group : getGroups()) {
                        for (Player pl : group.getMembers()) {
                            returnToPreviousLocation(pl);
                        } if (!isTournament() && shouldDisband()) {
                            scheduleGroupDisband(group, 2000);
                        }
                    }
                }
            }, 5000);
        } if (getAlliances().size() > 0) {
            for (PlayerAlliance alliance : getAlliances()) {
                for (Player pl : alliance.getMembers()) {
                    if (pl == null) {
                        continue;
                    }
                    freezePlayer(pl, 7500);
                }
            }
            ThreadPoolManager.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    for (PlayerAlliance alliance : getAlliances()) {
                        for (Player pl : alliance.getMembers()) {
                            if (pl == null) {
                                continue;
                            }
                            returnToPreviousLocation(pl);
                        } if (!isTournament() && shouldDisband()) {
                            scheduleAllianceDisband(alliance, 2000);
                        }
                    }
                }
            }, 5000);
        }
        ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                List<Player> spectators = getSpectators();
                synchronized (spectators) {
                    for (Iterator<Player> it = spectators.iterator(); it.hasNext();) {
                        Player pl = it.next();
                        onSpectatorLeave(pl, true);
                        it.remove();
                    }
                }
            }
        }, 5000);
        ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                for (Player pl : getInstance().getPlayersInside()) {
                    returnToPreviousLocation(pl);
                }
            }
        }, 15000);

        this.isDone = true;
    }
	
    public void onSpectatorJoin(Player spectator) {
        spectator.setBattleground(this);
        spectator.setSpectating(true);
        getSpectators().add(spectator);
        InstanceService.registerPlayerWithInstance(getInstance(), spectator);
        previousLocations.put(spectator.getObjectId(), spectator.getPosition().clone());
        SpawnPosition pos = getSpawnPositions().get(Rnd.get(getSpawnPositions().size()));
        TeleportService2.teleportTo(spectator, getMapId(), getInstanceId(), pos.getX(), pos.getY(), pos.getZ());
        spectator.getEffectController().setAbnormal(AbnormalState.HIDE.getId());
        spectator.setVisualState(CreatureVisualState.HIDE3);
        spectator.setInvul(true);
        spectator.setSeeState(CreatureSeeState.SEARCH2);
        PacketSendUtility.broadcastPacket(spectator, new SM_PLAYER_STATE(spectator), true);
        createTimer(spectator, getSecondsLeft());
        scheduleAnnouncement(spectator, "You have join " + getName() + " <Spectator> battleground!", 0);
    }
	
    public void onSpectatorLeave(final Player spectator, boolean isIterating) {
        endTimer(spectator);
        returnToPreviousLocation(spectator);
        ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                spectator.getEffectController().unsetAbnormal(AbnormalState.HIDE.getId());
                spectator.unsetVisualState(CreatureVisualState.HIDE3);
                spectator.setInvul(false);
                spectator.unsetSeeState(CreatureSeeState.SEARCH2);
                spectator.setSpectating(false);
                PacketSendUtility.broadcastPacket(spectator, new SM_PLAYER_STATE(spectator), true);
            }
        }, TELEPORT_DEFAULT_DELAY);
        if (!isIterating) {
            List<Player> spectators = getSpectators();
            synchronized (spectators) {
                spectators.remove(spectator);
            }
        }
    }
	
    public void reconnectPlayer(Player player) {
        if (player.getBattleground() != null) {
            return;
        } if (player.isAfk()) {
            player.setAfk(false);
        }
        AionObject obj = null;
        synchronized (getLeavers()) {
            obj = getLeavers().remove(player.getObjectId());
        }
        String msg = player.getName() + " is back in <Battleground>!";
        boolean success = false;
        SpawnPosition pos = null;
        if (obj == null) {
            success = true;
            addPlayer(player);
            int bgIndex = getPlayers().get(getPlayers().size() - 1).getBgIndex() + 1;
            player.setBgIndex(bgIndex);
            pos = getSpawnPositions().get(Rnd.get(getSpawnPositions().size()));
            for (Player pl : getPlayers()) {
                scheduleAnnouncement(pl, msg, 0);
            }
        } else if (obj instanceof PlayerAlliance) {
            success = true;
            PlayerAlliance alliance = (PlayerAlliance) obj;
            PlayerAllianceService.onPlayerLogin(player);
            pos = getSpawnPositions().get(alliance.getBgIndex());
            for (PlayerAlliance ally : getAlliances()) {
                for (Player pl : ally.getMembers()) {
                    if (pl == null) {
                        continue;
                    }
                    scheduleAnnouncement(pl, msg, 0);
                }
            }
        } else if (obj instanceof PlayerGroup) {
            success = true;
            PlayerGroup group = (PlayerGroup) obj;
            PlayerGroupService.onPlayerLogin(player);
            pos = getSpawnPositions().get(group.getBgIndex());
            for (PlayerGroup grp : getGroups()) {
                for (Player pl : grp.getMembers()) {
                    scheduleAnnouncement(pl, msg, 0);
                }
            }
        } if (success) {
            player.setBattleground(this);
            preparePlayer(player, 0, false);
            performTeleport(player, pos.getX(), pos.getY(), pos.getZ());
            getLadderDAO().setLeaves(player, getLadderDAO().getLeaves(player) - 1);
            getLadderDAO().addRating(player, K_VALUE);
        }
    }
	
    protected static LadderDAO getLadderDAO() {
        return DAOManager.getDAO(LadderDAO.class);
    }
	
    protected WorldMapInstance createInstance() {
        if (maps == null || maps.size() == 0) {
            return null;
        }
        this.map = maps.get(Rnd.get(maps.size()));
        this.mapId = map.getMapId();
        WorldMapInstance instance = InstanceService.getNextBgInstance(getMapId());
        if (instance != null) {
            setInstanceId(instance.getInstanceId());
        }
        setInstance(instance);
        setStartStamp(System.currentTimeMillis());
        return instance;
    }
	
    protected void openStaticDoors() {
        if (getMap().getStaticDoors() == null || getMap().getStaticDoors().isEmpty()) {
            return;
        } for (Player pl : getPlayers()) {
            for (Integer doorId : getMap().getStaticDoors()) {
                StaticDoorService.getInstance().openStaticDoor(pl, doorId);
            }
        }
    }
	
    public String getName() {
        return name;
    }
	
    public String getDescription() {
        return description;
    }
	
    public int getMinSize() {
        return minSize;
    }
	
    public int getMaxSize() {
        return maxSize;
    }
	
    public int getTeamCount() {
        return teamCount;
    }
	
    public int getMapId() {
        return mapId;
    }
	
    public void setMatchLength(int matchLength) {
        this.matchLength = matchLength;
    }
	
    public int getMatchLength() {
        return matchLength;
    }
	
    public BattlegroundMap getMap() {
        return map;
    }
	
    protected void setInstanceId(int instanceId) {
        this.instanceId = instanceId;
    }
	
    public int getInstanceId() {
        return instanceId;
    }
	
    public void setBgId(Integer bgId) {
        this.bgId = bgId;
    }
	
    public Integer getBgId() {
        return bgId;
    }
	
    protected void setStartStamp(long startStamp) {
        this.startStamp = startStamp;
    }
	
    public long getStartStamp() {
        return startStamp;
    }
	
    public void setIsTournament(boolean isTournament) {
        this.isTournament = isTournament;
    }
	
    public boolean isTournament() {
        return isTournament;
    }
	
    public void setIsEvent(boolean isEvent) {
        this.isEvent = isEvent;
    }
	
    public boolean isEvent() {
        return isEvent;
    }
	
    public boolean is1v1() {
        return is1v1;
    }
	
    public void setIs1v1(boolean is1v1) {
        this.is1v1 = is1v1;
    }
	
    public boolean shouldDisband() {
        return shouldDisband;
    }
	
    public void setShouldDisband(boolean shouldDisband) {
        this.shouldDisband = shouldDisband;
    }
	
    public boolean isTeamBased() {
        return teamBased;
    }
	
    public void setTeamBased(boolean teamBased) {
        this.teamBased = teamBased;
    }
	
    public WorldMapInstance getInstance() {
        return instance;
    }
	
    public void setInstance(WorldMapInstance instance) {
        this.instance = instance;
    }
	
    protected List<Player> getPlayers() {
        return _players;
    }
	
    protected List<PlayerGroup> getGroups() {
        return _groups;
    }
	
    protected List<PlayerAlliance> getAlliances() {
        return _alliances;
    }
	
    protected List<Player> getSpectators() {
        return _spectators;
    }
	
    public Map<Integer, AionObject> getLeavers() {
        return _leavers;
    }
	
    public boolean hasPlayers() {
        return (getPlayers().size() > 0 || getGroups().size() > 0 || getAlliances().size() > 0);
    }
	
    protected void setExpireTask(ScheduledFuture<?> expireTask) {
        this.expireTask = expireTask;
    }
	
    protected ScheduledFuture<?> getExpireTask() {
        return expireTask;
    }
	
    public ScheduledFuture<?> getBackgroundTask() {
        return backgroundTask;
    }
	
    public void setBackgroundTask(ScheduledFuture<?> backgroundTask) {
        this.backgroundTask = backgroundTask;
    }
	
    public static Map<String, Class<?>> getAliases() {
        return aliases;
    }
	
    public static class SpawnPosition {
        private int mapId = 0;
        private float x;
        private float y;
        private float z;
		
        public SpawnPosition(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
		
        public SpawnPosition(int mapId, float x, float y, float z) {
            this.mapId = mapId;
            this.x = x;
            this.y = y;
            this.z = z;
        }
		
        public float getX() {
            return x;
        }
		
        public void setX(float x) {
            this.x = x;
        }
		
        public float getY() {
            return y;
        }
		
        public void setY(float y) {
            this.y = y;
        }
		
        public float getZ() {
            return z;
        }
		
        public void setZ(float z) {
            this.z = z;
        }
		
        public int getMapId() {
            return mapId;
        }
		
        public void setMapId(int mapId) {
            this.mapId = mapId;
        }
    }
	
    public static class BattlegroundMap {
        private int mapId = 0;
        private List<SpawnPosition> spawnPoints = null;
        private List<Integer> staticDoors = null;
        private float killZ = 0f;
        private boolean restrictFlight = false;
		
        public BattlegroundMap(int mapId) {
            this.setMapId(mapId);
        }
		
        public void addSpawn(SpawnPosition pos) {
            if (spawnPoints == null) {
                spawnPoints = new ArrayList<SpawnPosition>();
            }
            spawnPoints.add(pos);
        }
		
        public void addStaticDoor(Integer doorId) {
            if (staticDoors == null) {
                staticDoors = new ArrayList<Integer>();
            }
            staticDoors.add(doorId);
        }
		
        public void setMapId(int mapId) {
            this.mapId = mapId;
        }
		
        public int getMapId() {
            return mapId;
        }
		
        public List<SpawnPosition> getSpawnPoints() {
            return spawnPoints;
        }
		
        public void setSpawnPoints(List<SpawnPosition> spawnPoints) {
            this.spawnPoints = spawnPoints;
        }
		
        public List<Integer> getStaticDoors() {
            return staticDoors;
        }
		
        public void setKillZ(float killZ) {
            this.killZ = killZ;
        }
		
        public float getKillZ() {
            return killZ;
        }
		
        public boolean isRestrictFlight() {
            return restrictFlight;
        }
		
        public void setRestrictFlight(boolean restrictFlight) {
            this.restrictFlight = restrictFlight;
        }
    }
}