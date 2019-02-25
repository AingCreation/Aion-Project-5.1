package com.aionemu.gameserver.dataholders;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.skillengine.model.SkillLearnTemplate;

/**
 * @author ATracer
 * @Rework idhacker542
 */
@XmlRootElement(name = "skill_tree")
@XmlAccessorType(XmlAccessType.FIELD)
public class SkillTreeData {
	
	private static Logger log = LoggerFactory.getLogger(SkillTreeData.class);

	@XmlElement(name = "skill")
	private List<SkillLearnTemplate> skillTemplates;
	
	private final HashMap<Race, HashMap<String, HashMap<Integer, Integer>>> stigmaTree = new HashMap<Race, HashMap<String, HashMap<Integer, Integer>>>();
	private final TIntObjectHashMap<ArrayList<SkillLearnTemplate>> templates = new TIntObjectHashMap<ArrayList<SkillLearnTemplate>>();
	private final TIntObjectHashMap<ArrayList<SkillLearnTemplate>> templatesById = new TIntObjectHashMap<ArrayList<SkillLearnTemplate>>();

	private static int makeHash(int classId, int race, int level) {
		int result = classId << 8;
		result = (result | race) << 8;
		return result | level;
	}
	
	void afterUnmarshal(Unmarshaller u, Object parent) {
		for (SkillLearnTemplate template : skillTemplates) {
			addTemplate(template);
		}
	}

	private void addTemplate(SkillLearnTemplate template) {
		Race race = template.getRace();
		if (race == null)
			race = Race.PC_ALL;

		int hash = makeHash(template.getClassId().ordinal(), race.ordinal(), template.getMinLevel());
		ArrayList<SkillLearnTemplate> value = templates.get(hash);
		if (value == null) {
			value = new ArrayList<SkillLearnTemplate>();
			templates.put(hash, value);
		}

		value.add(template);

		value = templatesById.get(template.getSkillId());
		if (value == null) {
			value = new ArrayList<SkillLearnTemplate>();
			templatesById.put(template.getSkillId(), value);
		}

		value.add(template);
	}

	/**
	 * @return the templates
	 */
	public TIntObjectHashMap<ArrayList<SkillLearnTemplate>> getTemplates() {
		return templates;
	}
	
	public ArrayList<SkillLearnTemplate> getSkillTemplate(int skillId) {
	    return templates.get(skillId);
	}

	/**
	 * Perform search for: - class specific skills (race = ALL) - class and race specific skills - non-specific skills
	 * (race = ALL, class = ALL)
	 * 
	 * @param playerClass
	 * @param level
	 * @param race
	 * @return SkillLearnTemplate[]
	 */
	public SkillLearnTemplate[] getTemplatesFor(PlayerClass playerClass, int level, Race race) {
		List<SkillLearnTemplate> newSkills = new ArrayList<SkillLearnTemplate>();

		List<SkillLearnTemplate> classRaceSpecificTemplates = templates.get(makeHash(PlayerClass.ALL.ordinal(), race.ordinal(),
			level));
		List<SkillLearnTemplate> classSpecificTemplates = templates.get(makeHash(playerClass.ordinal(),
			Race.PC_ALL.ordinal(), level));
		List<SkillLearnTemplate> generalTemplates = templates.get(makeHash(PlayerClass.ALL.ordinal(),
			Race.PC_ALL.ordinal(), level));
		List<SkillLearnTemplate> classBySpecificTemplates = templates.get(makeHash(playerClass.ordinal(),
				race.ordinal(), level));

		if (classRaceSpecificTemplates != null)
			newSkills.addAll(classRaceSpecificTemplates);
		if (classSpecificTemplates != null)
			newSkills.addAll(classSpecificTemplates);
		if (generalTemplates != null)
			newSkills.addAll(generalTemplates);
		if (classBySpecificTemplates != null) 
			newSkills.addAll(classBySpecificTemplates);
		    

		return newSkills.toArray(new SkillLearnTemplate[newSkills.size()]);
	}

	public SkillLearnTemplate[] getTemplatesForSkill(int skillId) {
		List<SkillLearnTemplate> searchSkills = new ArrayList<SkillLearnTemplate>();

		List<SkillLearnTemplate> byId = templatesById.get(skillId);
		if (byId != null)
			searchSkills.addAll(byId);

		return searchSkills.toArray(new SkillLearnTemplate[searchSkills.size()]);
	}

	public boolean isLearnedSkill(int skillId) {
		return templatesById.get(skillId) != null;
	}

	public int size() {
		int size = 0;
		for (Integer key : templates.keys())
			size += templates.get(key).size();
		return size;
	}
	
	public void loadStigmaDataTree() {
	    log.info("Loading stigma skill data...");
	    String skillStackName;
	    int playerLevel;
	    for (SkillLearnTemplate skillLearnTemplate : skillTemplates) {
	    	skillStackName = DataManager.SKILL_DATA.getSkillTemplate(skillLearnTemplate.getSkillId()).getStack();
	    	playerLevel = skillLearnTemplate.getMinLevel();
	    	ArrayList<Race> addRaceList = new ArrayList<Race>();
	    	if (skillLearnTemplate.getRace() == Race.PC_ALL) {
	    		addRaceList.add(Race.ASMODIANS);
	    		addRaceList.add(Race.ELYOS);
	    	} else {
	    		addRaceList.add(skillLearnTemplate.getRace());
	    	}
	    	for (Race addRace : addRaceList) {
	    		if (stigmaTree.get(addRace) != null) {
	    			if (stigmaTree.get(addRace).get(skillStackName) != null) {
	    				stigmaTree.get(addRace).get(skillStackName).put(playerLevel, skillLearnTemplate.getSkillId());
	    			} else {
	    				HashMap<Integer, Integer> skillMap = new HashMap<Integer, Integer>();
	    				skillMap.put(playerLevel, skillLearnTemplate.getSkillId());
	    				stigmaTree.get(addRace).put(skillStackName, skillMap);
	    			}
	    		} else {
	    			HashMap<String, HashMap<Integer, Integer>> stackMap = new HashMap<String, HashMap<Integer, Integer>>();
	    			HashMap<Integer, Integer> skillMap = new HashMap<Integer, Integer>();
	    			skillMap.put(Integer.valueOf(skillLearnTemplate.getSkillId()), Integer.valueOf(playerLevel));
	    			stackMap.put(skillStackName, skillMap);
	    			stigmaTree.put(addRace, stackMap);
	    		}
	    	}
	    }
	    log.info("Loaded " + this.stigmaTree.size() + " stigma skill data.");
	    skillTemplates = null;
	}
	
	public HashMap<Race, HashMap<String, HashMap<Integer, Integer>>> getStigmaTree() {
		return stigmaTree;
	}
}
