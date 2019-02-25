/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-emu is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.gameobjects;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.aionemu.gameserver.ai2.AI2Engine;
import com.aionemu.gameserver.ai2.AITemplate;
import com.aionemu.gameserver.ai2.poll.AIQuestion;
import com.aionemu.gameserver.configs.main.AIConfig;
import com.aionemu.gameserver.controllers.NpcController;
import com.aionemu.gameserver.controllers.movement.NpcMoveController;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.NpcType;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TribeClass;
import com.aionemu.gameserver.model.drop.NpcDrop;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.siege.SiegeNpc;
import com.aionemu.gameserver.model.skill.NpcSkillList;
import com.aionemu.gameserver.model.stats.container.NpcGameStats;
import com.aionemu.gameserver.model.stats.container.NpcLifeStats;
import com.aionemu.gameserver.model.templates.item.ItemAttackType;
import com.aionemu.gameserver.model.templates.npc.NpcRank;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.model.templates.npc.NpcTemplateType;
import com.aionemu.gameserver.model.templates.npcshout.NpcShout;
import com.aionemu.gameserver.model.templates.npcshout.ShoutEventType;
import com.aionemu.gameserver.model.templates.npcshout.ShoutType;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LOOKATOBJECT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.dreamergames.services.TribeRelationService;
import com.aionemu.gameserver.spawnengine.WalkerGroup;
import com.aionemu.gameserver.spawnengine.WalkerGroupShift;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldPosition;
import com.aionemu.gameserver.world.WorldType;
import com.google.common.base.Preconditions;

/**
 * This class is a base class for all in-game NPCs, what includes: monsters and npcs that player can talk to (aka
 * Citizens)
 * 
 * @author Luno
 */
public class Npc extends Creature {

	private WalkerGroup walkerGroup;
	private boolean isQuestBusy = false;
	private NpcSkillList skillList;
	private WalkerGroupShift walkerGroupShift;
	private long lastShoutedSeconds;
	private String masterName = StringUtils.EMPTY;
	private int creatorId = 0;
	private int townId;
	private int abyssId;
	private NpcType npcType;
	private ItemAttackType attacktype = ItemAttackType.PHYSICAL;
	private int sensoryRange = getObjectTemplate().getAggroRange();

	public Npc(int objId, NpcController controller, SpawnTemplate spawnTemplate, NpcTemplate objectTemplate) {
		this(objId, controller, spawnTemplate, objectTemplate, objectTemplate.getLevel());
	}

	public Npc(int objId, NpcController controller, SpawnTemplate spawnTemplate, NpcTemplate objectTemplate, byte level) {
		super(objId, controller, spawnTemplate, objectTemplate, new WorldPosition(spawnTemplate.getWorldId()));
		Preconditions.checkNotNull(objectTemplate, "Npcs should be based on template");
		controller.setOwner(this);
		moveController = new NpcMoveController(this);
		skillList = new NpcSkillList(this);
		npcType = objectTemplate.getNpcType();
		setupStatContainers(level);

		boolean aiOverride = false;
		if (spawnTemplate.getModel() != null) {
			if (spawnTemplate.getModel().getAi() != null) {
				aiOverride = true;
				AI2Engine.getInstance().setupAI(spawnTemplate.getModel().getAi(), this);
			}
		}

		if (!aiOverride)
			AI2Engine.getInstance().setupAI(objectTemplate.getAi(), this);

		lastShoutedSeconds = System.currentTimeMillis() / 1000;
	}

	@Override
	public NpcMoveController getMoveController() {
		return (NpcMoveController) super.getMoveController();
	}

	/**
	 * @param level
	 */
	protected void setupStatContainers(byte level) {
		setGameStats(new NpcGameStats(this));
		setLifeStats(new NpcLifeStats(this));
	}

	@Override
	public NpcTemplate getObjectTemplate() {
		return (NpcTemplate) objectTemplate;
	}

	@Override
	public String getName() {
		return getObjectTemplate().getName();
	}

	public int getNpcId() {
		return getObjectTemplate().getTemplateId();
	}

	@Override
	public byte getLevel() {
		return getObjectTemplate().getLevel();
	}

	@Override
	public NpcLifeStats getLifeStats() {
		return (NpcLifeStats) super.getLifeStats();
	}

	@Override
	public NpcGameStats getGameStats() {
		return (NpcGameStats) super.getGameStats();
	}

	@Override
	public NpcController getController() {
		return (NpcController) super.getController();
	}

	public NpcSkillList getSkillList() {
		return this.skillList;
	}

	@Override
	public ItemAttackType getAttackType() {
		return this.ai2.modifyAttackType(attacktype);
	}

	public boolean hasWalkRoutes() {
		return getSpawn().getWalkerId() != null || (getSpawn().hasRandomWalk() && AIConfig.ACTIVE_NPC_MOVEMENT);
	}

	public boolean isPeace() {
		return getNpcType().equals(NpcType.PEACE);
	}

	public boolean isFriendTo(Player player) {
		if(this.getTribe() == TribeClass.NOFIGHT)
			return false;
		return DataManager.TRIBE_RELATIONS_DATA.isFriendlyRelation(getTribe(), player.getTribe());
	}

	@Override
	public boolean isAggressiveTo(Creature creature) {
		if (creature instanceof Player)
			return ((Player) creature).isAggroFrom(this);
		else if (creature instanceof Summon)
			return ((Summon) creature).isAggroFrom(this);
		
		if(this.getTribe() == TribeClass.XDRAKAN_DGUARD && creature.getTribe() == TribeClass.XDRAKAN_LGUARD
			|| this.getTribe() == TribeClass.XDRAKAN_LGUARD && creature.getTribe() == TribeClass.XDRAKAN_DGUARD)
			return true;

		if (DataManager.TRIBE_RELATIONS_DATA.isAggressiveRelation(getTribe(), creature.getTribe()))
			return true;
		else
			return (creature instanceof Npc && guardAgainst((Npc) creature));
	}

	/**
	 * Represents the action of a guard defending its position
	 * 
	 * @param npc
	 * @return true if this npc is a guard and the given npc is aggro to their PC race
	 */
	public boolean guardAgainst(Npc npc) {
		// Even if NPCs aggro players they shouldn't aggro between (as Hippolytus)
		if (this.getRace() == npc.getRace())
			return false;
		if ((getTribe().isLightGuard() || this.getRace() == Race.ELYOS
			&& this.getObjectTemplate().getNpcTemplateType() == NpcTemplateType.GUARD)
			&& DataManager.TRIBE_RELATIONS_DATA.isAggressiveRelation(npc.getTribe(), TribeClass.PC))
			return true;
		if ((getTribe().isDarkGuard() || this.getRace() == Race.ASMODIANS
			&& this.getObjectTemplate().getNpcTemplateType() == NpcTemplateType.GUARD)
			&& DataManager.TRIBE_RELATIONS_DATA.isAggressiveRelation(npc.getTribe(), TribeClass.PC_DARK))
			return true;
		return false;
	}

	@Override
	public boolean isAggroFrom(Npc npc) {
		return DataManager.TRIBE_RELATIONS_DATA.isAggressiveRelation(npc.getTribe(), getTribe());
	}

	@Override
	public boolean isHostileFrom(Npc npc) {
		return DataManager.TRIBE_RELATIONS_DATA.isHostileRelation(npc.getTribe(), getTribe());
	}

	@Override
	public boolean isSupportFrom(Npc npc) {
		return DataManager.TRIBE_RELATIONS_DATA.isSupportRelation(npc.getTribe(), getTribe());
	}

	@Override
	public boolean isFriendFrom(Npc npc) {
		return DataManager.TRIBE_RELATIONS_DATA.isFriendlyRelation(npc.getTribe(), getTribe());
	}
	
	public boolean isNoneRelation(Player player) {
		return DataManager.TRIBE_RELATIONS_DATA.isNoneRelation(getTribe(), player.getTribe());
	}

	@Override
	public TribeClass getTribe() {
		TribeClass transformTribe = getTransformModel().getTribe();
		if (transformTribe != null) {
			return transformTribe;
		}
		return this.getObjectTemplate().getTribe();
	}

	public int getAggroRange() {
		return ai2.modifySensoryRange(sensoryRange);
	}

	/**
	 * Check whether npc located near initial spawn location
	 * 
	 * @return true or false
	 */
	public boolean isAtSpawnLocation() {
		return getDistanceToSpawnLocation() < 3;
	}

	/**
	 * @return distance to spawn location
	 */
	public double getDistanceToSpawnLocation() {
		return MathUtil.getDistance(getSpawn().getX(), getSpawn().getY(), getSpawn().getZ(), getX(), getY(), getZ());
	}

	@Override
	public boolean isEnemy(Creature creature) {
		if (creature instanceof Player) {
			if (getAi2().ask(AIQuestion.CAN_ATTACK_PLAYER).isPositive()) {
				return true;
			}
		}
		return creature.isEnemyFrom(this);
	}

	@Override
	public boolean isEnemyFrom(Creature creature) {
		return TribeRelationService.isAggressive(creature, this) || TribeRelationService.isHostile(creature, this);
	}

	@Override
	public boolean isEnemyFrom(Npc npc) {
		return TribeRelationService.isAggressive(this, npc) || TribeRelationService.isHostile(this, npc);
	}

	@Override
	public boolean isEnemyFrom(Player player) {
		return player.isEnemyFrom(this);
	}

	@Override
	public int getType(Creature creature) {
		int typeForPlayer = -1;
		if (TribeRelationService.isInvulnerable(this, creature)) {
			typeForPlayer = CreatureType.INVULNERABLE.getId();
		}
		else if (TribeRelationService.isNone(this, creature)) {
			typeForPlayer = CreatureType.PEACE.getId();
		}
		else if (TribeRelationService.isAggressive(this, creature)) {
			typeForPlayer = CreatureType.AGGRESSIVE.getId();
		}
		else if (TribeRelationService.isHostile(this, creature)) {
			typeForPlayer = CreatureType.ATTACKABLE.getId();
		}
		else if (TribeRelationService.isFriend(this, creature) || TribeRelationService.isNeutral(this, creature)) {
			typeForPlayer = CreatureType.FRIEND.getId();
		}
		else if (TribeRelationService.isSupport(this, creature)) {
			typeForPlayer = CreatureType.SUPPORT.getId();
		}
		if (typeForPlayer == CreatureType.PEACE.getId() || typeForPlayer == CreatureType.SUPPORT.getId()) {
			if (getObjectTemplate().isDialogNpc()) {
				typeForPlayer = CreatureType.FRIEND.getId();
			}
		}
		return typeForPlayer;
	}

	@Override
	public int getSeeState() {
		int skillSeeState = super.getSeeState();
		int congenitalSeeState = getObjectTemplate().getRating().getCongenitalSeeState().getId();
		return Math.max(skillSeeState, congenitalSeeState);
	}

	public boolean getIsQuestBusy() {
		return isQuestBusy;
	}

	public void setIsQuestBusy(boolean busy) {
		isQuestBusy = busy;
	}

	@Override
	public boolean isAttackableNpc() {
		return getNpcType() == NpcType.ATTACKABLE;
	}

	/**
	 * @return Name of the Master
	 */
	public String getMasterName() {
		return masterName;
	}
	
	public void setMasterName(String masterName) {
		this.masterName = masterName;
	}

	/**
	 * @return UniqueId of the VisibleObject which created this Npc (could be player or house)
	 */
	public int getCreatorId() {
		return creatorId;
	}
	
	public void setCreatorId(int creatorId) {
		this.creatorId = creatorId;
	}

	public int getTownId() {
		return townId;
	}

	public void setTownId(int townId) {
		this.townId = townId;
	}

	public int getAbyssId() {
		return abyssId;
	}

	public void setAbyssId(int abyssId) {
		this.abyssId = abyssId;
	}

	public VisibleObject getCreator() {
		return null;
	}

	@Override
	public void setTarget(VisibleObject creature) {
		if (getTarget() != creature) {
			super.setTarget(creature);
			super.clearAttackedCount();
			getGameStats().renewLastChangeTargetTime();
			if (!getLifeStats().isAlreadyDead()) {
				PacketSendUtility.broadcastPacket(this, new SM_LOOKATOBJECT(this));
			}
		}
	}

	public void setWalkerGroup(WalkerGroup wg) {
		this.walkerGroup = wg;
	}

	public WalkerGroup getWalkerGroup() {
		return walkerGroup;
	}

	public void setWalkerGroupShift(WalkerGroupShift shift) {
		this.walkerGroupShift = shift;
	}

	public WalkerGroupShift getWalkerGroupShift() {
		return walkerGroupShift;
	}

	public boolean isBoss() {
		return getObjectTemplate().getRank() == NpcRank.EXPERT;
	}

	public boolean hasEntity() {
		return getSpawn().getEntityId() != 0;
	}

	@Override
	public Race getRace() {
		return this.getObjectTemplate().getRace();
	}

	public NpcDrop getNpcDrop() {
		return getObjectTemplate().getNpcDrop();
	}

	public NpcType getNpcType() {
		return npcType;
	}
	
	public void setNpcType(NpcType newType) {
	   npcType = newType;
	}

	public boolean isRewardAP() {
		if (this instanceof SiegeNpc) {
			return true;
		} else if (this.getWorldType() == WorldType.ABYSS) {
			return true;
		} else if (this.getAi2().ask(AIQuestion.SHOULD_REWARD_AP).isPositive()) {
			return true;
		} else if (this.getWorldType() == WorldType.BALAUREA || this.getWorldType() == WorldType.PANESTERRA || this.isInInstance()) {
			return getRace() == Race.DRAKAN || getRace() == Race.DRAGON || getRace() == Race.NAGA || getRace() == Race.LIZARDMAN || getRace() == Race.GCHIEF_DRAGON;
		}
		return false;
	}

	public boolean mayShout(int delaySeconds) {
		if (!DataManager.NPC_SHOUT_DATA.hasAnyShout(getPosition().getMapId(), getNpcId()))
			return false;
		return (System.currentTimeMillis() - lastShoutedSeconds) / 1000 >= delaySeconds;
	}

	public void shout(final NpcShout shout, final Creature target, final Object param, int delaySeconds) {
		if (shout.getWhen() != ShoutEventType.DIED && shout.getWhen() != ShoutEventType.BEFORE_DESPAWN
			&& getLifeStats().isAlreadyDead() || !mayShout(delaySeconds))
			return;

		if (shout.getPattern() != null
			&& !((AITemplate) getAi2()).onPatternShout(shout.getWhen(), shout.getPattern(), shout.getSkillNo()))
			return;

		final int shoutRange = getObjectTemplate().getMinimumShoutRange();
		if (shout.getShoutType() == ShoutType.SAY && !(target instanceof Player) || target != null
			&& !MathUtil.isIn3dRange(target, this, shoutRange))
			return;

		final Npc thisNpc = this;
		final SM_SYSTEM_MESSAGE message = new SM_SYSTEM_MESSAGE(true, shout.getStringId(), getObjectId(), 1, param);
		lastShoutedSeconds = System.currentTimeMillis() / 1000;

		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (thisNpc.getLifeStats().isAlreadyDead() && shout.getWhen() != ShoutEventType.DIED
					&& shout.getWhen() != ShoutEventType.BEFORE_DESPAWN)
					return;

				// message for the specific player (when IDLE we are already broadcasting!!!)
				if (shout.getShoutType() == ShoutType.SAY || shout.getWhen() == ShoutEventType.IDLE) {
					// [RR] Should we have lastShoutedSeconds separated from broadcasts (??)
					PacketSendUtility.sendPacket((Player) target, message);
				}
				else {
					Iterator<Player> iter = thisNpc.getKnownList().getKnownPlayers().values().iterator();
					while (iter.hasNext()) {
						Player kObj = iter.next();
						if (kObj.getLifeStats().isAlreadyDead() || !kObj.isOnline())
							continue;
						if (MathUtil.isIn3dRange(kObj, thisNpc, shoutRange))
							PacketSendUtility.sendPacket(kObj, message);
					}
				}
			}
		}, delaySeconds * 1000);
	}
}
