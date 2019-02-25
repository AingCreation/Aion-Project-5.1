package com.aionemu.gameserver.model.templates.item;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name="Stigma")
public class Stigma {
	@XmlAttribute(name="kinah")
	protected int Kinah;
	@XmlAttribute(name="skill_group1")
	protected String skillgroup1;
	@XmlAttribute(name="skill_group2")
	protected String skillgroup2;
  
	public int getKinah() {
		return Kinah;
	}
  
	public String getSkillGroup1() {
		return skillgroup1;
	}
  
	public String getSkillGroup2() {
		return skillgroup2;
	}
}