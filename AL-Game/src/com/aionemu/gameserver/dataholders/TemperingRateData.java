package com.aionemu.gameserver.dataholders;

import com.aionemu.gameserver.services.dreamergames.model.TemperingRateTemplates;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="tempering_rates")
@XmlAccessorType(XmlAccessType.FIELD)
public class TemperingRateData {
	@XmlElement(name="tempering_rate")
	private List<TemperingRateTemplates> tlist;
	private TIntObjectHashMap<TemperingRateTemplates> rateData = new TIntObjectHashMap<TemperingRateTemplates>();
	private Map<Integer, TemperingRateTemplates> rateMapData = new HashMap<Integer, TemperingRateTemplates>(1);
  
	void afterUnmarshal(Unmarshaller u, Object parent) {
		for (TemperingRateTemplates id : tlist) {
			rateData.put(id.getCategory(), id);
			rateMapData.put(id.getCategory(), id);
		}
	}
  
	public int size() {
		return rateData.size();
	}
  
	public TemperingRateTemplates getCategory(int id) {
		return rateData.get(id);
	}
  
	public Map<Integer, TemperingRateTemplates> getAll() {
		return rateMapData;
	}
}