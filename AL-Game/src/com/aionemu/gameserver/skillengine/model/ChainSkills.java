package com.aionemu.gameserver.skillengine.model;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import javolution.util.FastMap;

public class ChainSkills
{
	private Map<String, ChainSkill> multiSkills = new FastMap<String, ChainSkill>();
	private ChainSkill chainSkill = new ChainSkill("", 0, 0);
	
	public int getChainCount(Player player, SkillTemplate template, String category) {
		if (category == null) {
			return 0;
		}
		long nullTime = player.getSkillCoolDown(template.getCooldownId());
		if (this.multiSkills.get(category) != null) {
			if (System.currentTimeMillis() >= nullTime && this.multiSkills.get(category).getUseTime() <= nullTime) {
				this.multiSkills.get(category).setChainCount(0);
			}
			return this.multiSkills.get(category).getChainCount();
		}
		return 0;
	}
	
	public long getLastChainUseTime(String category) {
		if (this.multiSkills.get(category) != null) {
			return this.multiSkills.get(category).getUseTime();
		} else if (chainSkill.getCategory().equals(category)) {
			return this.chainSkill.getUseTime();
		} else {
			return 0;
		}
	}
	
	public boolean chainSkillEnabled(String category, int time) {
		long useTime = 0;
		if (this.multiSkills.get(category) != null) {
			useTime = this.multiSkills.get(category).getUseTime();
		} else if (chainSkill.getCategory().equals(category)) {
			useTime = chainSkill.getUseTime();
		} if ((useTime + time) >= System.currentTimeMillis())
			return true;
		else
			return false;
	}
	
	public void addChainSkill(String category, boolean multiCast) {
		if (multiCast) {
			if (this.multiSkills.get(category) != null) {
				if (multiCast) {
					this.multiSkills.get(category).increaseChainCount();
				}
				this.multiSkills.get(category).setUseTime(System.currentTimeMillis());
			} else {
				this.multiSkills.put(category, new ChainSkill(category, (multiCast ? 1 : 0), System.currentTimeMillis()));
			}
		} else {
			chainSkill.updateChainSkill(category);
		}
	}
	
	public Collection<ChainSkill> getChainSkills() {
		Collection<ChainSkill> collection = new ArrayList<ChainSkill>();
		collection.add(this.chainSkill);
		collection.addAll(this.multiSkills.values());
		return collection;
	}
}