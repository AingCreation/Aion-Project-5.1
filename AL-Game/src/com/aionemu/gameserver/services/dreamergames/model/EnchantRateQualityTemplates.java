package com.aionemu.gameserver.services.dreamergames.model;

import com.aionemu.gameserver.model.templates.item.ItemQuality;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="EnchantRateQualityTemplates")
public class EnchantRateQualityTemplates {
	protected List<EnchantRateLevelTemplates> enchant_level;
	@XmlAttribute(name="quality", required=true)
	private ItemQuality itemQuality;
  
	public List<EnchantRateLevelTemplates> getEnchantLevel() {
		if (enchant_level == null) {
			enchant_level = new ArrayList<EnchantRateLevelTemplates>();
		}
		return enchant_level;
	}
  
	public ItemQuality getItemQuality() {
		return itemQuality;
	}
}