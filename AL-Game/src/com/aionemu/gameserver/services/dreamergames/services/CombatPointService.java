package com.aionemu.gameserver.services.dreamergames.services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerCpListDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.services.SkillLearnService;
import com.aionemu.gameserver.services.dreamergames.model.PlayerCpEntry;
import com.aionemu.gameserver.services.dreamergames.model.PlayerCpList;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.skill.PlayerSkillEntry;
import com.aionemu.gameserver.model.skill.PlayerSkillList;
import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.stats.calc.StatOwner;
import com.aionemu.gameserver.model.stats.calc.functions.IStatFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatFunction;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ESSENCE_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ESSENCE_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.skillengine.model.SkillEnchantTemplate;
import com.aionemu.gameserver.skillengine.model.SkillLearnTemplate;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.audit.AuditLogger;

/**
 * @author IDhacker
 *
 */
public class CombatPointService implements StatOwner {
private static final Logger log = LoggerFactory.getLogger(CombatPointService.class);
	
	public void onPlayerLogin(Player player) {
		PlayerCpList playerCpList = player.getCpList();
		int level = player.getLevel();
		
		int TotalCpUse = 0;
		PacketSendUtility.sendPacket(player, new SM_ESSENCE_LIST(player, player.getCpList().getAllCps()));
		
		player.getGameStats().endEffect(this);
		
		for (PlayerCpEntry cpEntry : playerCpList.getStatUpCps()) {
			int slotId = cpEntry.getSlotId();
			int cppoint = cpEntry.getCpPoint();
			
			SkillEnchantTemplate[] slotIds = DataManager.SKILL_ENCHANT_DATA.getTemplatesForGroup(slotId);
			
			TotalCpUse += slotIds[0].getCpCostAdj() * cppoint;
			
			List<IStatFunction> modifiers = new ArrayList<IStatFunction>();
			
			try {
				switch (slotId) {
					case 1:
						modifiers.add(new executeStatEnchant(StatEnum.HSTR, cppoint));
						break;
					case 2:
						modifiers.add(new executeStatEnchant(StatEnum.HVIT, cppoint));
						break;
					case 3:
						modifiers.add(new executeStatEnchant(StatEnum.HDEX, cppoint));
						break;
					case 4:
						modifiers.add(new executeStatEnchant(StatEnum.HAGI, cppoint));
						break;
					case 5:
						modifiers.add(new executeStatEnchant(StatEnum.HKNO, cppoint));
						break;
					case 6:
						modifiers.add(new executeStatEnchant(StatEnum.HWIL, cppoint));
					default:
						break;
				}
				player.getGameStats().addEffect(this, modifiers);
			} catch (Exception ex) {
				log.error("Error on enchant stat.", ex);
			}
		} for (PlayerCpEntry cpEntry : playerCpList.getLearnCps()) {
			int slotid = cpEntry.getSlotId();
			int cppoint = cpEntry.getCpPoint();
		      
			SkillEnchantTemplate[] slotIds = DataManager.SKILL_ENCHANT_DATA.getTemplatesForGroup(slotid);
		      
			TotalCpUse += slotIds[0].getCpCost();
			for (int i = 0; i <= level; i++) {
				SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
				PlayerSkillList playerSkillList = player.getSkillList();
				for (SkillLearnTemplate template : skillTemplates) {
					if (template.getSkillGroup() != null) {
						if (template.getSkillGroup().equals(slotIds[0].getSkillGroup())) {
							int skillid = template.getSkillId();
							if (cppoint > 0) {
								playerSkillList.addTransformationSkill(player, skillid, 1);
							} else {
								PlayerSkillEntry skills = player.getSkillList().getSkillEntry(skillid);
								if (skills != null) {
									SkillLearnService.removeSkill(player, skillid);
								}
							}
						}
					}
				} if (slotIds[0].getAdditionallearnskill() != null) {
					for (SkillLearnTemplate templates : skillTemplates) {
						if (templates.getSkillGroup() != null) {
							if (templates.getSkillGroup().equals(slotIds[0].getAdditionallearnskill())) {
								int skillids = templates.getSkillId();
								if (cppoint > 0) {
									playerSkillList.addTransformationSkill(player, skillids, 1);
								} else {
									PlayerSkillEntry skills = player.getSkillList().getSkillEntry(skillids);
									if (skills != null) {
										SkillLearnService.removeSkill(player, skillids);
									}
								}
							}
						}
					}
				}
			}
		} for (PlayerCpEntry cpEntry : playerCpList.getEnchantSkillCps()) {
			int slotId = cpEntry.getSlotId();
			int cppoint = cpEntry.getCpPoint();
			
			SkillEnchantTemplate[] slotIds = DataManager.SKILL_ENCHANT_DATA.getTemplatesForGroup(slotId);
			for (int i = 0; i <= level; i++) {
				SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
				for (SkillLearnTemplate template : skillTemplates) {
					if (template.getSkillGroup() != null) {
						if (template.getSkillGroup().equals(slotIds[0].getSkillGroup())) {
							int skillid = template.getSkillId();
							PlayerSkillEntry skills = player.getSkillList().getSkillEntry(skillid);
							if (skills != null) {
								skills.setSkillLvl(0);
								skills.setSkillLvl(cppoint + 1);
							}
						}
					}
				}
			}
		}
		player.getCommonData().setTotalCpUse(TotalCpUse);
	    if (TotalCpUse > player.getCommonData().getMaxCp()) {
	    	AuditLogger.info(player, "Player trying abusing CP Enchant Point, Total CP Use: " + TotalCpUse + " Player MaxCP: " + player.getCommonData().getMaxCp());
	    	resetCp(player);
	    	DAOManager.getDAO(PlayerCpListDAO.class).clearCpBeforeInsert(player, "stat_up");
	    	DAOManager.getDAO(PlayerCpListDAO.class).clearCpBeforeInsert(player, "learn_skill");
	    	DAOManager.getDAO(PlayerCpListDAO.class).clearCpBeforeInsert(player, "enchant_skill");
	    	return;
	    }
	    PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player, player.getSkillList().getBasicSkills()));
	    PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));
	}
	
	public void resetCp(Player player) {
		// TODO Auto-generated method stub
		PlayerCpList playerCpList = player.getCpList();
		int level = player.getLevel();
		for (PlayerCpEntry cpEntry : playerCpList.getStatUpCps()) {
			int slotId = cpEntry.getSlotId();
			int cppoint = cpEntry.getCpPoint();
			player.getCpList().setCpStat(player, slotId, 0);
		      
			List<IStatFunction> modifiers = new ArrayList<IStatFunction>();
			try {
				switch (slotId) {
					case 1: 
						modifiers.add(new executeStatEnchant(StatEnum.HSTR, cppoint));
						break;
					case 2: 
						modifiers.add(new executeStatEnchant(StatEnum.HVIT, cppoint));
						break;
					case 3: 
						modifiers.add(new executeStatEnchant(StatEnum.HDEX, cppoint));
						break;
					case 4: 
						modifiers.add(new executeStatEnchant(StatEnum.HAGI, cppoint));
						break;
					case 5: 
						modifiers.add(new executeStatEnchant(StatEnum.HKNO, cppoint));
						break;
					case 6: 
						modifiers.add(new executeStatEnchant(StatEnum.HWIL, cppoint));
					default:
						break;
				}
				player.getGameStats().endEffect(this);
			} catch (Exception ex) {
		        log.error("Error on item equip.", ex);
			}
		} for (PlayerCpEntry cpEntry : playerCpList.getLearnCps()) {
			int slotId = cpEntry.getSlotId();
			player.getCpList().setCpLearn(player, slotId, 0);
			SkillEnchantTemplate[] slotIds = DataManager.SKILL_ENCHANT_DATA.getTemplatesForGroup(slotId);
			for (int i = 0; i <= level; i++) {
				SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
				for (SkillLearnTemplate template : skillTemplates) {
					if (template.getSkillGroup() != null) {
						if (template.getSkillGroup().equals(slotIds[0].getSkillGroup())) {
							int skillid = template.getSkillId();
							PlayerSkillEntry skills = player.getSkillList().getSkillEntry(skillid);
							if (skills != null) {
								SkillLearnService.removeSkill(player, skillid);
							}
						}
					}
				} if (slotIds[0].getAdditionallearnskill() != null) {
					for (SkillLearnTemplate templates : skillTemplates) {
						if (templates.getSkillGroup() != null) {
							if (templates.getSkillGroup().equals(slotIds[0].getAdditionallearnskill())) {
								int skillids = templates.getSkillId();
								PlayerSkillEntry skills = player.getSkillList().getSkillEntry(skillids);
								if (skills != null) {
									SkillLearnService.removeSkill(player, skillids);
								}
							}
						}
					}
				}
			}
		} for (PlayerCpEntry cpEntry : playerCpList.getEnchantSkillCps()) {
		      int slotId = cpEntry.getSlotId();
		      player.getCpList().setCpEnchant(player, slotId, 0);
		      SkillEnchantTemplate[] slotIds = DataManager.SKILL_ENCHANT_DATA.getTemplatesForGroup(slotId);
		      for (int i = 0; i <= level; i++) {
		    	  SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
		    	  for (SkillLearnTemplate template : skillTemplates) {
		    		  if (template.getSkillGroup() != null) {
		    			  if (template.getSkillGroup().equals(slotIds[0].getSkillGroup())) {
			    			  int skillid = template.getSkillId();
			    			  PlayerSkillEntry skills = player.getSkillList().getSkillEntry(skillid);
			    			  if (skills != null) {
			    				  skills.setSkillLvl(0);
			    			  }
		    			  }
		    		  }
		    	  }
		      }
		} for (PlayerCpEntry cpEntry : playerCpList.getAllCps()) {
			int slotId = cpEntry.getSlotId();
			playerCpList.removeCp(slotId);
		}
		player.getCommonData().setTotalCpUse(0);
		PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player, player.getSkillList().getBasicSkills()));
		PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));
		PacketSendUtility.sendPacket(player, new SM_ESSENCE_LIST(player, player.getCpList().getAllCps()));
		PacketSendUtility.sendPacket(player, new SM_ESSENCE_INFO(player, player.getCpList().getAllCps()));
	}
	
	private void checkEssenceQuest(Player player) {
		int questId;
		
		if (player.getRace() == Race.ASMODIANS) {
			questId = 20522;
		} else {
			questId = 10522;
		}
		
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		
		if (qs != null && qs.getQuestVars().getQuestVars() == 0) {
			qs.setStatus(QuestStatus.REWARD);
			qs.setQuestVar(1);
			PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(questId, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			if ((qs.getStatus() == QuestStatus.COMPLETE) || (qs.getStatus() == QuestStatus.REWARD)) {
				player.getController().updateNearbyQuests();
			}
		}
	}
	
	public void executeCp(Player player) {
		int level = player.getLevel();
		int TotalCpUse = 0;
	    
		boolean Statup = false;
		
		checkEssenceQuest(player);
	    
		player.setCpList(DAOManager.getDAO(PlayerCpListDAO.class).loadCpList(player.getObjectId()));
	    PlayerCpList playerCpList = player.getCpList();
	    
	    player.getGameStats().endEffect(this);
	    for (PlayerCpEntry cpEntry : playerCpList.getStatUpCps()) {
	    	int slotId = cpEntry.getSlotId();
	    	int pointId = cpEntry.getCpPoint();
	    	SkillEnchantTemplate[] slotIds = DataManager.SKILL_ENCHANT_DATA.getTemplatesForGroup(slotId);
	    	if (pointId <= 0) {
	    		playerCpList.removeCp(slotId);
	    	} else {
	    		TotalCpUse += slotIds[0].getCpCostAdj() * pointId;
	    	}
	      
	    	List<IStatFunction> modifiers = new ArrayList<IStatFunction>();
	      
	    	try {
	    		switch (slotId) {
	    			case 1: 
	    				modifiers.add(new executeStatEnchant(StatEnum.HSTR, pointId));
	    				break;
	    			case 2: 
	    				modifiers.add(new executeStatEnchant(StatEnum.HVIT, pointId));
	    				break;
	    			case 3: 
	    				modifiers.add(new executeStatEnchant(StatEnum.HDEX, pointId));
	    				break;
	    			case 4: 
	    				modifiers.add(new executeStatEnchant(StatEnum.HAGI, pointId));
	    				break;
	    			case 5: 
	    				modifiers.add(new executeStatEnchant(StatEnum.HKNO, pointId));
	    				break;
	    			case 6: 
	    				modifiers.add(new executeStatEnchant(StatEnum.HWIL, pointId));
	    				break;
	    		}
	    		Statup = true;
	    		player.getGameStats().addEffect(this, modifiers);
	    	} catch (Exception ex) {
	    		log.error("Error on stat apply.", ex);
	    	}
	    } for (PlayerCpEntry cpEntry : playerCpList.getLearnCps()) {
	    	int slotId = cpEntry.getSlotId();
	    	int pointId = cpEntry.getCpPoint();
	      
	    	SkillEnchantTemplate[] slotIds = DataManager.SKILL_ENCHANT_DATA.getTemplatesForGroup(slotId);
	    	if (pointId <= 0) {
	    		playerCpList.removeCp(slotId);
	    	} else {
	    		TotalCpUse += slotIds[0].getCpCost();
	    	}
	    	for (int i = 0; i <= level; i++) {
	    		SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
	    		PlayerSkillList playerSkillList = player.getSkillList();
	    		for (SkillLearnTemplate template : skillTemplates) {
	    			if (template.getSkillGroup() != null) {
	    				if (template.getSkillGroup().equals(slotIds[0].getSkillGroup())) {
		    				int skillid = template.getSkillId();
		    				if (pointId > 0) {
		    					playerSkillList.addTransformationSkill(player, skillid, 1);
		    				} else {
		    					PlayerSkillEntry skills = player.getSkillList().getSkillEntry(skillid);
		    					if (skills != null) {
		    						SkillLearnService.removeSkill(player, skillid);
		    					}
		    				}
	    				}
	    			}
	    		} if (slotIds[0].getAdditionallearnskill() != null) {
	    			for (SkillLearnTemplate templates : skillTemplates) {
	    				if (templates.getSkillGroup() != null) {
	    					if (templates.getSkillGroup().equals(slotIds[0].getAdditionallearnskill())) {
		    					int skillids = templates.getSkillId();
		    					if (pointId > 0) {
		    						playerSkillList.addTransformationSkill(player, skillids, 1);
		    					} else {
		    						PlayerSkillEntry skills = player.getSkillList().getSkillEntry(skillids);
		    						if (skills != null) {
		    							SkillLearnService.removeSkill(player, skillids);
		    						}
		    					}
	    					}
	    				}
	    			}
	    		}
	    	}
	    } for (PlayerCpEntry cpEntry : playerCpList.getEnchantSkillCps()) {
	      int slotId = cpEntry.getSlotId();
	      int pointId = cpEntry.getCpPoint();
	      
	      SkillEnchantTemplate[] slotIds = DataManager.SKILL_ENCHANT_DATA.getTemplatesForGroup(slotId);
	      for (int i = 1; i <= pointId; i++) {
	    	  if (slotIds[0].getCpCostAdj() > 0) {
	    		  TotalCpUse += slotIds[0].getCpCostAdj() + slotIds[0].getCpCost() + slotIds[0].getCpCost() * (i - 1);
	    	  } else if (i >= slotIds[0].getCpCostMax()) {
	        	TotalCpUse += slotIds[0].getCpCostMax();
	    	  } else {
	    		  TotalCpUse += slotIds[0].getCpCost() * i;
	    	  }
	      } for (int i = 0; i <= level; i++) {
	    	  SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
	    	  for (SkillLearnTemplate template : skillTemplates) {
	    		  if (template.getSkillGroup() != null) {
	    			  if (template.getSkillGroup().equals(slotIds[0].getSkillGroup())) {
		    			  int skillid = template.getSkillId();
		    			  PlayerSkillEntry skills = player.getSkillList().getSkillEntry(skillid);
		    			  if (skills != null) {
		    				  skills.setSkillLvl(0);
		    				  skills.setSkillLvl(pointId + 1);
		    			  }
	    			  }
	    		  }
	    	  }
	      	} if (pointId <= 0) {
	    	  playerCpList.removeCp(slotId);
	      	}
	    }
	    
	    player.getCommonData().setTotalCpUse(TotalCpUse);
	    if (TotalCpUse > player.getCommonData().getMaxCp()) {
	    	AuditLogger.info(player, "Player trying abusing CP Enchant Point, Total CP Use: " + TotalCpUse + " Player MaxCP: " + player.getCommonData().getMaxCp());
	    	resetCp(player);
	    	DAOManager.getDAO(PlayerCpListDAO.class).clearCpBeforeInsert(player, "stat_up");
	    	DAOManager.getDAO(PlayerCpListDAO.class).clearCpBeforeInsert(player, "learn_skill");
	    	DAOManager.getDAO(PlayerCpListDAO.class).clearCpBeforeInsert(player, "enchant_skill");
	    	return;
	    }
	    PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_GIVE_CP_ENCHANT);
	    PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player, player.getSkillList().getBasicSkills()));
	    if (Statup) {
	    	PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));
	    }
	    PacketSendUtility.sendPacket(player, new SM_ESSENCE_LIST(player, player.getCpList().getAllCps()));
	    PacketSendUtility.sendPacket(player, new SM_ESSENCE_INFO(player, player.getCpList().getAllCps()));
	  }

	class executeStatEnchant extends StatFunction {
		int modifier = 1;
    
		executeStatEnchant(StatEnum stat, int modifier) {
			this.stat = stat;
			this.modifier = modifier;
		}
    
		public void apply(Stat2 stat) {
			switch (stat.getStat()) {
				case HSTR: 
					stat.addToBonus(modifier);
					break;
				case HVIT: 
					stat.addToBonus(modifier);
					break;
				case HDEX: 
					stat.addToBonus(modifier);
					break;
				case HAGI: 
					stat.addToBonus(modifier);
					break;
				case HKNO: 
					stat.addToBonus(modifier);
					break;
				case HWIL: 
					stat.addToBonus(modifier);
					break;
			default:
				break;
			}
		}
    
		public int getPriority() {
			return 60;
		}
	}
	
	public static final CombatPointService getInstance() {
		return SingletonHolder.instance;
	}
	
	private static class SingletonHolder {
	    protected static final CombatPointService instance = new CombatPointService();
	}
}