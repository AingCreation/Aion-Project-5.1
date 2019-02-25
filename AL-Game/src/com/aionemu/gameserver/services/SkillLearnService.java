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
package com.aionemu.gameserver.services;

import com.aionemu.gameserver.configs.main.MembershipConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.skill.PlayerSkillEntry;
import com.aionemu.gameserver.model.skill.PlayerSkillList;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_REMOVE;
import com.aionemu.gameserver.skillengine.model.SkillLearnTemplate;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class SkillLearnService {
	
	public static void addNewSkills(Player player) {
		int level = player.getCommonData().getLevel();
		PlayerClass playerClass = player.getCommonData().getPlayerClass();
		Race playerRace = player.getRace();
		if (level == 10 && player.getSkillList().getSkillEntry(30001) != null) {
			int skillLevel = player.getSkillList().getSkillLevel(30001);
			removeSkill(player, 30001);
			PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player, player.getSkillList().getBasicSkills()));
			// Why adding after the packet ?
			player.getSkillList().addSkill(player, 30002, skillLevel);
		}
		addSkills(player, level, playerClass, playerRace);
	}
	
	public static void addMissingSkills(Player player) {
		int level = player.getCommonData().getLevel();
		PlayerClass playerClass = player.getCommonData().getPlayerClass();
		Race playerRace = player.getRace();
		for (int i = 0; i <= level; i++) {
			addSkills(player, i, playerClass, playerRace);
		} if (!playerClass.isStartingClass()) {
			PlayerClass startinClass = PlayerClass.getStartingClassFor(playerClass);
			for (int i = 1; i < 10; i++) {
				addSkills(player, i, startinClass, playerRace);
			} if (player.getSkillList().getSkillEntry(30001) != null) {
				int skillLevel = player.getSkillList().getSkillLevel(30001);
				player.getSkillList().removeSkill(30001);
				// Not sure about that, mysterious code
				PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player, player.getSkillList().getBasicSkills()));
				for (PlayerSkillEntry stigmaSkill : player.getSkillList().getStigmaSkills())
					PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player, stigmaSkill));
				// Why adding after the packet ?
				player.getSkillList().addSkill(player, 30002, skillLevel);
			}
		}
	}
	
	public static void addSkills(Player player, int level, PlayerClass playerClass, Race playerRace) {
		SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(playerClass, level, playerRace);
		PlayerSkillList playerSkillList = player.getSkillList();
		
		for (SkillLearnTemplate template : skillTemplates) {
			// this possible ?
			if (checkLearnIsPossible(player, playerSkillList, template)) {
				if (template.isStigma()) {
					playerSkillList.addStigmaSkill(player, template.getSkillId(), template.getSkillLevel(), true);
				} else {
					playerSkillList.addSkill(player, template.getSkillId(), template.getSkillLevel());
				}
			}
		}
	}
	
	private static boolean checkLearnIsPossible(Player player, PlayerSkillList playerSkillList, SkillLearnTemplate template) {
		
		if (playerSkillList.isSkillPresent(template.getSkillId())) {
			return true;
		}

		if (player.havePermission(MembershipConfig.STIGMA_AUTOLEARN) && template.isStigma()) {
			return true;
		}

		if (template.isAutoLearn()  && !template.isStigma()) {
			return true;
		} 
		// check if stigma ? block
		if (template.isStigma()) {
		      return false;
	    }
		
		return false;
	}
	
	public static void learnSkillBook(Player player, int skillId) {
		SkillLearnTemplate[] skillTemplates = null;
		int maxLevel = 0;
		SkillTemplate passiveSkill = DataManager.SKILL_DATA.getSkillTemplate(skillId);
		for (int i = 1; i <= player.getLevel(); i++) {
			skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
			for (SkillLearnTemplate skill : skillTemplates) {
				if (skillId == skill.getSkillId()) {
					if (skill.getSkillLevel() > maxLevel) {
						maxLevel = skill.getSkillLevel();
					}
				}
			}
		}
		player.getSkillList().addSkill(player, skillId, maxLevel);
		if (passiveSkill.isPassive()) {
			player.getController().updatePassiveStats();
		}
	}
	
	public static void removeSkill(Player player, int skillId) {
		if (player.getSkillList().isSkillPresent(skillId)) {
			Integer skillLevel = player.getSkillList().getSkillLevel(skillId);
			if (skillLevel == null) {
				skillLevel = 1;
			} if (player.getEffectController().hasAbnormalEffect(skillId)) {
				player.getEffectController().removeEffect(skillId);
			}
			//PacketSendUtility.sendPacket(player, new SM_SKILL_REMOVE(skillId, skillLevel, player.getSkillList().getSkillEntry(skillId).isStigma(), false));
			PacketSendUtility.sendPacket(player, new SM_SKILL_REMOVE(skillId, skillLevel, player.getSkillList().getSkillEntry(skillId).isStigma()));
			player.getSkillList().removeSkill(skillId);
		}
	}
	
	public static void removeSkillBuff(Player player, int skillId) {
		if (player.getSkillList().isSkillPresent(skillId)) {
			PacketSendUtility.sendPacket(player, new SM_SKILL_REMOVE(skillId, 1, false));
			player.getSkillList().removeSkill(skillId);
		}
	}
	
	// remove stigma skill
	public static void removeStigmaSkill(Player player, int skillId, int skillEnchantLevel) {
		if (player.getSkillList().isSkillPresent(skillId)) {
			PacketSendUtility.sendPacket(player, new SM_SKILL_REMOVE(skillId, skillEnchantLevel, player.getSkillList().getSkillEntry(skillId).isStigma()));
			player.getSkillList().removeSkill(skillId);
		}
	}
	 
	// advance stimga remove ?
	public static void removeLinkedSkill(Player player, int skillId, int skilllevel) {
		if (player.getSkillList().isSkillPresent(skillId)) {
			PacketSendUtility.sendPacket(player, new SM_SKILL_REMOVE(skillId, skilllevel, player.getSkillList().getSkillEntry(skillId).isStigma(), false));
			PacketSendUtility.sendPacket(player, new SM_SKILL_REMOVE(skillId, skilllevel, false, player.getSkillList().getSkillEntry(skillId).isLinked()));
			player.getSkillList().removeSkill(skillId);
			player.setLinkedSkill(0);
		}
	}
	
	public static int getSkillLearnLevel(int skillId, int playerLevel, int wantedSkillLevel) {
		SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesForSkill(skillId);
		int learnFinishes = 0;
		int maxLevel = 0;
		for (SkillLearnTemplate template : skillTemplates) {
			if (maxLevel < template.getSkillLevel()) {
				maxLevel = template.getSkillLevel();
			}
		} if (maxLevel == 0) {
			return wantedSkillLevel;
		}
		learnFinishes = playerLevel + maxLevel;
		if (learnFinishes > DataManager.PLAYER_EXPERIENCE_TABLE.getMaxLevel()) {
			learnFinishes = DataManager.PLAYER_EXPERIENCE_TABLE.getMaxLevel();
		}
		return Math.max(wantedSkillLevel, Math.min(playerLevel - (learnFinishes - maxLevel) + 1, maxLevel));
	}
	
	public static int getSkillMinLevel(int skillId, int playerLevel, int wantedSkillLevel) {
		SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesForSkill(skillId);
		SkillLearnTemplate foundTemplate = null;
		for (SkillLearnTemplate template : skillTemplates) {
			if (template.getSkillLevel() <= wantedSkillLevel && template.getMinLevel() <= playerLevel) {
				foundTemplate = template;
			}
		} if (foundTemplate == null) {
			return playerLevel;
		}
		return foundTemplate.getMinLevel();
	}
}