package com.aionemu.gameserver.model.stats.container;

import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Nonnull;

import org.apache.commons.lang.NullArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.LOG;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import com.aionemu.gameserver.services.LifeStatsRestoreService;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.utils.PacketSendUtility;

public abstract class CreatureLifeStats<T extends Creature>
{
	private static final Logger log = LoggerFactory.getLogger(CreatureLifeStats.class);
	protected int currentHp;
	protected int currentMp;
	protected boolean alreadyDead = false;
	protected T owner;
	private final Lock hpLock = new ReentrantLock();
	private final Lock mpLock = new ReentrantLock();
	protected final Lock restoreLock = new ReentrantLock();
	protected volatile Future<?> lifeRestoreTask;
	
	public CreatureLifeStats(T owner, int currentHp, int currentMp) {
		this.owner = owner;
		this.currentHp = currentHp;
		this.currentMp = currentMp;
	}
	
	public T getOwner() {
		return owner;
	}
	
	public int getCurrentHp() {
		return currentHp;
	}
	
	public int getCurrentMp() {
		return currentMp;
	}
	
	public int getMaxHp() {
		return this.getOwner().getGameStats().getMaxHp().getCurrent();
	}
	
	public int getMaxMp() {
		return this.getOwner().getGameStats().getMaxMp().getCurrent();
	}
	
	public boolean isAlreadyDead() {
		return alreadyDead;
	}
	
	public int reduceHp(int value, @Nonnull Creature attacker) {
		if (attacker == null) {
			throw new NullArgumentException("attacker");
		}
		boolean isDied = false;
		hpLock.lock();
		try {
			if (!alreadyDead) {
				int newHp = this.currentHp - value;
				if (newHp < 0) {
					newHp = 0;
					alreadyDead = true;
					isDied = true;
				}
				this.currentHp = newHp;
			}
		} finally {
			hpLock.unlock();
		} if (value != 0) {
			onReduceHp();
		} if (isDied) {
			getOwner().getController().onDie(attacker);
		}
		return currentHp;
	}
	
	public int reduceMp(int value) {
		mpLock.lock();
		try {
			int newMp = this.currentMp - value;
			if (newMp < 0) {
				newMp = 0;
			}
			this.currentMp = newMp;
		} finally {
			mpLock.unlock();
		} if (value != 0) {
			onReduceMp();
		}
		return currentMp;
	}
	
	protected void sendAttackStatusPacketUpdate(TYPE type, int value, int skillId, LOG log) {
		if (owner == null) {
			return;
		}
		PacketSendUtility.broadcastPacketAndReceive(owner, new SM_ATTACK_STATUS(owner, owner, type, skillId, value, log));
	}
	
	public int increaseHp(TYPE type, int value) {
		return this.increaseHp(type, value, 0, LOG.REGULAR);
	}
	
	public int increaseHp(TYPE type, int value, int skillId, LOG log) {
		boolean hpIncreased = false;
		if (this.getOwner().getEffectController().isAbnormalSet(AbnormalState.DISEASE)) {
			return currentHp;
		}
		hpLock.lock();
		try {
			if (isAlreadyDead()) {
				return 0;
			}
			int newHp = this.currentHp + value;
			if (newHp > getMaxHp()) {
				newHp = getMaxHp();
			} if (currentHp != newHp) {
				this.currentHp = newHp;
				hpIncreased = true;
			}
		} finally {
			hpLock.unlock();
		} if (hpIncreased) {
			onIncreaseHp(type, value, skillId, log);
		}
		return currentHp;
	}
	
	public int increaseMp(TYPE type, int value) {
		return this.increaseMp(type, value, 0, LOG.REGULAR);
	}
	
	public int increaseMp(TYPE type, int value, int skillId, LOG log) {
		boolean mpIncreased = false;
		mpLock.lock();
		try {
			if (isAlreadyDead()) {
				return 0;
			}
			int newMp = this.currentMp + value;
			if (newMp > getMaxMp()) {
				newMp = getMaxMp();
			} if (currentMp != newMp) {
				this.currentMp = newMp;
				mpIncreased = true;
			}
		} finally {
			mpLock.unlock();
		} if (mpIncreased) {
			onIncreaseMp(type, value, skillId, log);
		}
		return currentMp;
	}
	
	public final void restoreHp() {
		increaseHp(TYPE.NATURAL_HP, getOwner().getGameStats().getHpRegenRate().getCurrent());
	}
	
	public final void restoreMp() {
		increaseMp(TYPE.NATURAL_MP, getOwner().getGameStats().getMpRegenRate().getCurrent());
	}
	
	public void triggerRestoreTask() {
		restoreLock.lock();
		try {
			if (lifeRestoreTask == null && !alreadyDead) {
				lifeRestoreTask = LifeStatsRestoreService.getInstance().scheduleRestoreTask(this);
			}
		} finally {
			restoreLock.unlock();
		}
	}
	
	public void cancelRestoreTask() {
		restoreLock.lock();
		try {
			if (lifeRestoreTask != null) {
				lifeRestoreTask.cancel(false);
				lifeRestoreTask = null;
			}
		} finally {
			restoreLock.unlock();
		}
	}
	
	public boolean isFullyRestoredHpMp() {
		return getMaxHp() == currentHp && getMaxMp() == currentMp;
	}
	
	public boolean isFullyRestoredHp() {
		return getMaxHp() == currentHp;
	}
	
	public boolean isFullyRestoredMp() {
		return getMaxMp() == currentMp;
	}
	
	public void synchronizeWithMaxStats() {
		int maxHp = getMaxHp();
		if (currentHp != maxHp) {
			currentHp = maxHp;
		}
		int maxMp = getMaxMp();
		if (currentMp != maxMp) {
			currentMp = maxMp;
		}
	}
	
	public void updateCurrentStats() {
		int maxHp = getMaxHp();
		if (maxHp < currentHp) {
			currentHp = maxHp;
		}
		int maxMp = getMaxMp();
		if (maxMp < currentMp) {
			currentMp = maxMp;
		} if (!isFullyRestoredHpMp()) {
			triggerRestoreTask();
		}
	}
	
	public int getHpPercentage() {
		return (int) (100f * currentHp / getMaxHp());
	}
	
	public int getMpPercentage() {
		return (int) (100f * currentMp / getMaxMp());
	}
	
	protected abstract void onIncreaseMp(TYPE type, int value, int skillId, LOG log);
	protected abstract void onReduceMp();
	protected abstract void onIncreaseHp(TYPE type, int value, int skillId, LOG log);
	protected abstract void onReduceHp();
	
	public int increaseFp(TYPE type, int value) {
		return 0;
	}
	
	public int getMaxFp() {
		return 0;
	}
	
	public int getCurrentFp() {
		return 0;
	}
	
	public void cancelAllTasks() {
		cancelRestoreTask();
	}
	
	public void setCurrentHpPercent(int hpPercent) {
		hpLock.lock();
		try {
			long maxHp = getMaxHp();
			this.currentHp = (int) (maxHp * hpPercent / 100f);
			if (this.currentHp > 0) {
				this.alreadyDead = false;
			}
		} finally {
			hpLock.unlock();
		}
	}
	
	public void setCurrentHp(int hp) {
		boolean hpNotAtMaxValue = false;
		hpLock.lock();
		try {
			this.currentHp = hp;
			if (this.currentHp > 0) {
				this.alreadyDead = false;
			} if (this.currentHp < getMaxHp()) {
				hpNotAtMaxValue = true;
			}
		} finally {
			hpLock.unlock();
		} if (hpNotAtMaxValue) {
			onReduceHp();
		}
	}
	
	public int setCurrentMp(int value) {
		mpLock.lock();
		try {
			int newMp = value;
			if (newMp < 0) {
				newMp = 0;
			}
			this.currentMp = newMp;
		} finally {
			mpLock.unlock();
		}
		onReduceMp();
		return currentMp;
	}
	
	public void setCurrentMpPercent(int mpPercent) {
		mpLock.lock();
		try {
			long maxMp = getMaxMp();
			this.currentMp = (int) (maxMp * mpPercent / 100f);
		} finally {
			mpLock.unlock();
		}
	}
}