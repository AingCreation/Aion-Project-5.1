package com.aionemu.gameserver.services.dreamergames.model;

import com.aionemu.gameserver.model.templates.item.ItemCategory;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="TemperingRateTemplates")
public class TemperingRateTemplates {
	protected List<TemperingRateLevelTemplates> tempering_level;
	@XmlAttribute(name="item_category", required=true)
	private ItemCategory itemCategory;
  
	public List<TemperingRateLevelTemplates> getTemperingLevel() {
		if (tempering_level == null) {
			tempering_level = new ArrayList<TemperingRateLevelTemplates>();
		}
		return this.tempering_level;
	}
  
	public int getCategory() {
		return itemCategory.getId();
	}
}