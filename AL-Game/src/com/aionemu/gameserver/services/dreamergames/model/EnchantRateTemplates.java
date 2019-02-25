package com.aionemu.gameserver.services.dreamergames.model;

import com.aionemu.gameserver.model.templates.item.EquipType;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="EnchantRateTemplates")
public class EnchantRateTemplates {
	protected List<EnchantRateQualityTemplates> item_quality;
	@XmlAttribute(name="equipment_type", required=true)
	private EquipType equipmentType;
  
	public List<EnchantRateQualityTemplates> getItemQuality() {
		if (item_quality == null) {
			item_quality = new ArrayList<EnchantRateQualityTemplates>();
		}
		return item_quality;
	}
  
	public int getCategory() {
		return equipmentType.getId();
	}
}