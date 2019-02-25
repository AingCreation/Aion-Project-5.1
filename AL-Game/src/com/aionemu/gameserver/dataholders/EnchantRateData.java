package com.aionemu.gameserver.dataholders;

import com.aionemu.gameserver.services.dreamergames.model.EnchantRateTemplates;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="enchant_rates")
@XmlAccessorType(XmlAccessType.FIELD)
public class EnchantRateData {
	@XmlElement(name="enchant_rate")
	private List<EnchantRateTemplates> tlist;
	private TIntObjectHashMap<EnchantRateTemplates> rateData = new TIntObjectHashMap<EnchantRateTemplates>();
	private Map<Integer, EnchantRateTemplates> rateMapData = new HashMap<Integer, EnchantRateTemplates>(1);
  
	void afterUnmarshal(Unmarshaller u, Object parent) {
		for (EnchantRateTemplates id : tlist) {
			rateData.put(id.getCategory(), id);
			rateMapData.put(id.getCategory(), id);
		}
	}
  
	public int size() {
		return rateData.size();
	}
  
	public EnchantRateTemplates getCategory(int id) {
		return rateData.get(id);
	}
  
	public Map<Integer, EnchantRateTemplates> getAll() {
		return rateMapData;
	}
}