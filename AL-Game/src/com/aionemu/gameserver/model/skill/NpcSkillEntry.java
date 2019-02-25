package com.aionemu.gameserver.model.skill;


public abstract class NpcSkillEntry extends SkillEntry
{
	protected long lastTimeUsed = 0;
	
	public NpcSkillEntry(int skillId, int skillLevel) {
		super(skillId, skillLevel, 0);
	}
	
	public abstract boolean isReady(int hpPercentage, long fightingTimeInMSec);
	public abstract boolean chanceReady();
	public abstract boolean hpReady(int hpPercentage);
	public abstract boolean timeReady(long fightingTimeInMSec);
	public abstract boolean hasCooldown();
	public abstract boolean UseInSpawned();
	
	public long getLastTimeUsed() {
		return lastTimeUsed;
	}
	
	public void setLastTimeUsed() {
		lastTimeUsed = System.currentTimeMillis();
	}
}