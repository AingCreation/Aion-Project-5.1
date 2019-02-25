package com.aionemu.gameserver.dataholders;

import com.aionemu.gameserver.services.dreamergames.templates.PlayerCpTemplate;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="player_cp_points")
@XmlAccessorType(XmlAccessType.FIELD)
public class PlayerCpTable {
	private final TIntObjectHashMap<ArrayList<PlayerCpTemplate>> templates = new TIntObjectHashMap<ArrayList<PlayerCpTemplate>>();
	@XmlElement(name="player_cp_point")
	private List<PlayerCpTemplate> skillTemplates;
	private TIntObjectHashMap<PlayerCpTemplate> skillData = new TIntObjectHashMap<PlayerCpTemplate>();
  
	void afterUnmarshal(Unmarshaller u, Object parent) {
		for (PlayerCpTemplate template : this.skillTemplates) {
			addTemplate(template);
		}
	}
  
	private void addTemplate(PlayerCpTemplate template) {
		int playerlevel = template.getPlayerCpPoint();
		ArrayList<PlayerCpTemplate> value = templates.get(playerlevel);
		if (value == null) {
			value = new ArrayList<PlayerCpTemplate>();
			this.templates.put(playerlevel, value);
		}
		value.add(template);
	}
  
	public TIntObjectHashMap<ArrayList<PlayerCpTemplate>> getTemplates() {
		return this.templates;
	}
  
	public PlayerCpTemplate getSlotTemplate(int slotId)	{
		return (PlayerCpTemplate)this.skillData.get(slotId);
	}
  
	public PlayerCpTemplate[] getTemplatesForGroup(int slotid) {
		List<PlayerCpTemplate> newSkills = new ArrayList<PlayerCpTemplate>();
		List<PlayerCpTemplate> generalTemplates = templates.get(slotid);
		if (generalTemplates != null) {
			newSkills.addAll(generalTemplates);
		}
		return (PlayerCpTemplate[])newSkills.toArray(new PlayerCpTemplate[newSkills.size()]);
	}
  
	public int size() {
		int size = 0;
		int[] arr$ = templates.keys();int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++) {
			Integer key = Integer.valueOf(arr$[i$]);
			size += templates.get(key.intValue()).size();
		}
		return size;
	}
}