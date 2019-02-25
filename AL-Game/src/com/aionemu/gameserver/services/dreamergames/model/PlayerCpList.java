package com.aionemu.gameserver.services.dreamergames.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * @author ID
 *
 */
public final class PlayerCpList implements CpList<Player> {
	private Map<Integer, PlayerCpEntry> learnskills;
	private Map<Integer, PlayerCpEntry> statUpCps;
	private Map<Integer, PlayerCpEntry> enchantSkillCps;
	private List<PlayerCpEntry> deletedCps;
	
	public PlayerCpList() {
		learnskills = new HashMap<Integer, PlayerCpEntry>();
		statUpCps = new HashMap<Integer, PlayerCpEntry>(0);
	    enchantSkillCps = new HashMap<Integer, PlayerCpEntry>(0);
	    deletedCps = new ArrayList<PlayerCpEntry>(0);
	}
	
	public PlayerCpList(List<PlayerCpEntry> skills) {
		this();
		for (PlayerCpEntry entry : skills) {
			if (entry.isStatup()) {
				statUpCps.put(entry.getSlotId(), entry);
			} else if (entry.isEnchantSkill()) {
				enchantSkillCps.put(entry.getSlotId(), entry);
			} else if (entry.isLearnSkill()) {
				learnskills.put(entry.getSlotId(), entry);
			}
		}
	}
	
	public PlayerCpEntry[] getAllCps() {
		List<PlayerCpEntry> allCps = new ArrayList<PlayerCpEntry>();
		allCps.addAll(learnskills.values());
		allCps.addAll(statUpCps.values());
		allCps.addAll(enchantSkillCps.values());
		return (PlayerCpEntry[])allCps.toArray(new PlayerCpEntry[allCps.size()]);
	}
	
	public PlayerCpEntry[] getLearnCps() {
		return learnskills.values().toArray(new PlayerCpEntry[learnskills.size()]);
	}
	
	public PlayerCpEntry[] getStatUpCps() {
		return statUpCps.values().toArray(new PlayerCpEntry[statUpCps.size()]);
	}
	
	public PlayerCpEntry[] getEnchantSkillCps() {
		return enchantSkillCps.values().toArray(new PlayerCpEntry[enchantSkillCps.size()]);
	}
	
	public PlayerCpEntry[] getDeletedCps() {
		return deletedCps.toArray(new PlayerCpEntry[deletedCps.size()]);
	}
	
	public PlayerCpEntry getCpEntry(int slotId) {
		if (learnskills.containsKey(slotId)) {
			return learnskills.get(slotId);
		}
		if (statUpCps.containsKey(slotId)) {
			return statUpCps.get(slotId);
		}
		return enchantSkillCps.get(slotId);
	}
	
	public PlayerCpEntry getEnchantSkillCpEntry(int slotId) {
		return enchantSkillCps.get(slotId);
	}
	
	@Override
	public boolean setCpStat(Player player, int slotId, int cpPoint) {
		// TODO Auto-generated method stub
		return addCp(player, slotId, cpPoint, true, false, PersistentState.NEW);
	}

	@Override
	public boolean setCpEnchant(Player player, int slotId, int cpPoint) {
		// TODO Auto-generated method stub
		return addCp(player, slotId, cpPoint, false, true, PersistentState.NEW);
	}
	
	@Override
	public boolean setCpLearn(Player player, int slotId, int cpPoint) {
		// TODO Auto-generated method stub
		return addCp(player, slotId, cpPoint, false, false, PersistentState.NEW);
	}
	
	private synchronized boolean addCp(Player player, int slotId, int cpPoint, boolean isStatUp, boolean isEnchantSkill, PersistentState state) {
		PlayerCpEntry existingCp = isStatUp ? statUpCps.get(slotId) : enchantSkillCps.get(slotId);
		if (isStatUp) {
			existingCp = statUpCps.get(slotId);
		} else if (isEnchantSkill) {
			existingCp = enchantSkillCps.get(slotId);
		} else {
			existingCp = learnskills.get(slotId);
		}
		
		if (existingCp != null) {
			existingCp.setCpPoint(cpPoint);
		} else if (isStatUp) {
			statUpCps.put(slotId, new PlayerCpEntry(slotId, true, false, false, cpPoint, state));
		} else if (isEnchantSkill) {
			enchantSkillCps.put(slotId, new PlayerCpEntry(slotId, false, true, false, cpPoint, state));
		} else {
			learnskills.put(slotId, new PlayerCpEntry(slotId, false, false, true, cpPoint, state));
		}
		
		if (player.isSpawned()) {}
		return true;
	}
	
	@Override
	public boolean isCpPresent(int slotId) {
		// TODO Auto-generated method stub
		return statUpCps.containsKey(slotId) || enchantSkillCps.containsKey(slotId) || learnskills.containsKey(slotId);
	}
	
	@Override
	public int getCpPoint(int slotId) {
		// TODO Auto-generated method stub
		if (statUpCps.containsKey(slotId)) {
			return statUpCps.get(slotId).getCpPoint();
		}
		if (learnskills.containsKey(slotId)) {
			return learnskills.get(slotId).getCpPoint();
		}
		return enchantSkillCps.get(slotId).getCpPoint();
	}

	@Override
	public boolean removeCp(int slotId) {
		// TODO Auto-generated method stub
		PlayerCpEntry entry = enchantSkillCps.get(slotId);
		if (entry == null) {
			entry = statUpCps.get(slotId);
		}
		if (entry == null) {
			entry = enchantSkillCps.get(slotId);
		}
		if (entry != null) {
			entry.setPersistentState(PersistentState.DELETED);
			deletedCps.add(entry);
			learnskills.remove(Integer.valueOf(slotId));
			statUpCps.remove(Integer.valueOf(slotId));
			enchantSkillCps.remove(slotId);
		}
		return entry != null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return learnskills.size() + statUpCps.size() + enchantSkillCps.size();
	}
}
