package com.aionemu.gameserver.services.dreamergames.templates;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="player_cp_point")
public class PlayerCpTemplate {
	@XmlAttribute(name="player_level", required=true)
	private int playerlevel;
	@XmlAttribute(name="cp", required=true)
	private int cp;
  
	public int getPlayerCpPoint() {
		return this.playerlevel;
	}
  
	public int getCpPoint() {
		return this.cp;
	}
}