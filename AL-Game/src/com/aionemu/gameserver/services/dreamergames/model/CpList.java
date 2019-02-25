package com.aionemu.gameserver.services.dreamergames.model;

import com.aionemu.gameserver.model.gameobjects.Creature;

/**
 * @author ID
 */
public interface CpList<T extends Creature> {
	boolean setCpLearn(T Creature, int slotId, int cpPoint);
	  
	boolean setCpEnchant(T Creature, int slotId, int cpPoint);
	  
	boolean setCpStat(T Creature, int slotId, int cpPoint);
	  
	boolean removeCp(int slotId);
	  
	boolean isCpPresent(int slotId);
	  
	int getCpPoint(int slotId);
	  
	int size();
}