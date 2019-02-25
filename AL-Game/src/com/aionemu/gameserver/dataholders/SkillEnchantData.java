package com.aionemu.gameserver.dataholders;

import com.aionemu.gameserver.skillengine.model.SkillEnchantTemplate;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="cp_infos")
@XmlAccessorType(XmlAccessType.FIELD)
public class SkillEnchantData {
	private final TIntObjectHashMap<ArrayList<SkillEnchantTemplate>> templates = new TIntObjectHashMap<ArrayList<SkillEnchantTemplate>>();
	@XmlElement(name="cp_info")
	private List<SkillEnchantTemplate> skillTemplates;
	private TIntObjectHashMap<SkillEnchantTemplate> skillData = new TIntObjectHashMap<SkillEnchantTemplate>();
	
	void afterUnmarshal(Unmarshaller u, Object parent) {
		for (SkillEnchantTemplate template : skillTemplates) {
			addTemplate(template);
		}
	}
	
	private void addTemplate(SkillEnchantTemplate template) {
		int slotId = template.getId();
		ArrayList<SkillEnchantTemplate> value = templates.get(slotId);
		if (value == null) {
			value = new ArrayList<SkillEnchantTemplate>();
			templates.put(slotId, value);
		}
		value.add(template);
	}
	
	public TIntObjectHashMap<ArrayList<SkillEnchantTemplate>> getTemplates() {
		return templates;
	}
	
	public SkillEnchantTemplate getSlotTemplate(int slotId) {
		return (SkillEnchantTemplate)skillData.get(slotId);
	}
	
	public SkillEnchantTemplate[] getTemplatesForGroup(int slotid) {
		List<SkillEnchantTemplate> newSkills = new ArrayList<SkillEnchantTemplate>();
		List<SkillEnchantTemplate> generalTemplates = templates.get(slotid);
		if (generalTemplates != null) {
			newSkills.addAll(generalTemplates);
		}
		return (SkillEnchantTemplate[])newSkills.toArray(new SkillEnchantTemplate[newSkills.size()]);
	}
	
	public int size() {
		int size = 0;
		for (Integer key : templates.keys())
			size += templates.get(key).size();
		return size;
	}
}