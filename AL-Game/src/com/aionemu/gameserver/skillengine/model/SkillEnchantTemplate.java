package com.aionemu.gameserver.skillengine.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * @author ID
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="cp_info")
public class SkillEnchantTemplate {
	@XmlAttribute(name="id", required=true)
	private int id;
	@XmlAttribute(name="name", required=true)
	private String name;
	@XmlAttribute(name="category", required=true)
	private String category;
	@XmlAttribute(name="skill_group", required=true)
	private String skillgroup;
	@XmlAttribute(name="additional_learn_skill")
	private String additionallearnskill;
	@XmlAttribute(name="stat_value")
	private int statValue;
	@XmlAttribute(name="cp_count_max", required=true)
	private int cpCountMax;
	@XmlAttribute(name="cp_cost")
	private int cpCost;
	@XmlAttribute(name="cp_cost_adj")
	private int cpCostAdjust;
	@XmlAttribute(name="cp_cost_max")
	private int cpCostMax;
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getCategory() {
		return category;
	}
	
	public String getSkillGroup() {
		return skillgroup;
	}
	
	public String getAdditionallearnskill() {
		return additionallearnskill;
	}
	
	public int getStatValue() {
		return statValue;
	}
	
	public int getCpCountMax() {
		return cpCountMax;
	}
	
	public int getCpCost() {
		return cpCost;
	}
	
	public int getCpCostMax() {
		return cpCostMax;
	}
	
	public int getCpCostAdj() {
		return cpCostAdjust;
	}
}