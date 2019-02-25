package com.aionemu.gameserver.services.dreamergames.model;

import com.aionemu.gameserver.model.gameobjects.PersistentState;

/**
 * @author ID
 *
 */
public class PlayerCpEntry extends CpEntry {
	private boolean isStatup;
	private boolean isEnchantSkill;
	private boolean isLearnSkill;
	private PersistentState persistentState;

	public PlayerCpEntry(int slotId, boolean isStatup, boolean isEnchantSkill, boolean isLearnSkill, int cpPoint, PersistentState persistentState) {
		super(slotId, cpPoint);
		this.isStatup = isStatup;
		this.isEnchantSkill = isEnchantSkill;
		this.isLearnSkill = isLearnSkill;
		this.persistentState = persistentState;
	}
	
	public boolean isStatup() {
		return isStatup;
	}
	
	public boolean isEnchantSkill() {
		return isEnchantSkill;
	}
	
	public boolean isLearnSkill() {
	  return isLearnSkill;
	}
	
	public void setCpPoint(int cpPoint) {
		super.setCpPoint(cpPoint);
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}
	
	public PersistentState getPersistentState() {
		return persistentState;
	}
	
	public void setPersistentState(PersistentState persistentState) {
		switch (persistentState) {
		case DELETED: 
			if (persistentState == PersistentState.NEW) {
				persistentState = PersistentState.NOACTION;
			} else {
				persistentState = PersistentState.DELETED;
			}
			break;
		case UPDATE_REQUIRED: 
			if (persistentState != PersistentState.NEW) {
				persistentState = PersistentState.UPDATE_REQUIRED;
			}
			break;
		case NOACTION: 
			break;
		default: 
			this.persistentState = persistentState;
		}
	}
}