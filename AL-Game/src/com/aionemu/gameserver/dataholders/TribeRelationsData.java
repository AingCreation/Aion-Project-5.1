package com.aionemu.gameserver.dataholders;

import gnu.trove.map.hash.THashMap;

import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.TribeClass;
import com.aionemu.gameserver.model.templates.tribe.Tribe;

@XmlRootElement(name = "tribe_relations")
@XmlAccessorType(XmlAccessType.FIELD)
public class TribeRelationsData
{
	@XmlElement(name = "tribe", required = true)
	protected List<Tribe> tribeList;
	
	protected THashMap<TribeClass, Tribe> tribeNameMap = new THashMap<TribeClass, Tribe>();
	
	void afterUnmarshal(Unmarshaller u, Object parent) {
		for (Tribe tribe: tribeList) {
			tribeNameMap.put(tribe.getName(), tribe);
		}
		tribeList = null;
	}
	
	public int size() {
		return tribeNameMap.size();
	}
	
	public TribeClass getBaseTribe(TribeClass tribeName) {
		Tribe tribe = tribeNameMap.get(tribeName);
		return tribe.getBase();
	}
	
	public boolean hasAggressiveRelations(TribeClass tribeName) {
		Tribe tribe = tribeNameMap.get(tribeName);
		if (tribe == null)
			return false;
		Tribe baseTribe = tribe.isBasic() ? (Tribe) tribeNameMap.get(tribe.getBase()) : null;
		return !tribe.getAggressive().isEmpty() || (baseTribe != null && !baseTribe.getAggressive().isEmpty());
	}
	
	public boolean hasHostileRelations(TribeClass tribeName) {
		Tribe tribe = tribeNameMap.get(tribeName);
		if (tribe == null)
			return false;
		Tribe baseTribe = tribe.isBasic() ? tribeNameMap.get(tribe.getBase()) : null;
		return !tribe.getHostile().isEmpty() || (baseTribe != null && !baseTribe.getHostile().isEmpty());
	}
	
	public boolean hasSupportRelations(TribeClass tribeName) {
		Tribe tribe = tribeNameMap.get(tribeName);
		if (tribe == null)
			return false;
		Tribe baseTribe = tribe.isBasic() ? tribeNameMap.get(tribe.getBase()) : null;
		return !tribe.getSupport().isEmpty() || (baseTribe != null && !baseTribe.getSupport().isEmpty());
	}
	
	public boolean hasFriendRelations(TribeClass tribeName) {
		Tribe tribe = tribeNameMap.get(tribeName);
		if (tribe == null)
			return false;
		Tribe baseTribe = tribe.isBasic() ? tribeNameMap.get(tribe.getBase()) : null;
		return !tribe.getFriendly().isEmpty() || (baseTribe != null && !baseTribe.getFriendly().isEmpty());
	}
	
	public boolean hasNoneRelations(TribeClass tribeName) {
		Tribe tribe = tribeNameMap.get(tribeName);
		if (tribe == null)
			return false;
		Tribe baseTribe = tribe.isBasic() ? tribeNameMap.get(tribe.getBase()) : null;
		return !tribe.getNone().isEmpty() || (baseTribe != null && !baseTribe.getNone().isEmpty());
	}
	
	public boolean hasNeutralRelations(TribeClass tribeName) {
		Tribe tribe = tribeNameMap.get(tribeName);
		if (tribe == null)
			return false;
		Tribe baseTribe = tribe.isBasic() ? tribeNameMap.get(tribe.getBase()) : null;
		return !tribe.getNeutral().isEmpty() || (baseTribe != null && !baseTribe.getNeutral().isEmpty());
	}
	
	public boolean isAggressiveRelation(TribeClass tribeName1, TribeClass tribeName2) {
		Tribe tribe1 = tribeNameMap.get(tribeName1);
		Tribe tribe2 = tribeNameMap.get(tribeName2);
		if (tribe1 == null || tribe2 == null)
			return false;
		if (isFriendlyRelation(tribeName1, tribeName2))
			return false;
		return tribe1.getAggressive().contains(tribeName2) || tribe2.isBasic() && tribe1.getAggressive().contains(tribe2.getBase());
	}
	
	public boolean isSupportRelation(TribeClass tribeName1, TribeClass tribeName2) {
		Tribe tribe1 = tribeNameMap.get(tribeName1);
		Tribe tribe2 = tribeNameMap.get(tribeName2);
		if (tribe1 == null || tribe2 == null)
			return false;
		return tribe1.getSupport().contains(tribeName2) || tribe2.isBasic() && tribe1.getAggressive().contains(tribe2.getBase());
	}
	
	public boolean isFriendlyRelation(TribeClass tribeName1, TribeClass tribeName2) {
		Tribe tribe1 = tribeNameMap.get(tribeName1);
		Tribe tribe2 = tribeNameMap.get(tribeName2);
		if (tribe1 == null || tribe2 == null)
			return false;
		return tribe1.getFriendly().contains(tribeName2) || tribe2.isBasic() && tribe1.getFriendly().contains(tribe2.getBase());
	}
	
	public boolean isNeutralRelation(TribeClass tribeName1, TribeClass tribeName2) {
		Tribe tribe1 = tribeNameMap.get(tribeName1);
		Tribe tribe2 = tribeNameMap.get(tribeName2);
		if (tribe1 == null || tribe2 == null)
			return false;
		return tribe1.getNeutral().contains(tribeName2) || tribe2.isBasic() && tribe1.getNeutral().contains(tribe2.getBase());
	}
	
	public boolean isNoneRelation(TribeClass tribeName1, TribeClass tribeName2) {
		Tribe tribe1 = tribeNameMap.get(tribeName1);
		Tribe tribe2 = tribeNameMap.get(tribeName2);
		if (tribe1 == null || tribe2 == null)
			return false;
		return tribe1.getNone().contains(tribeName2) || tribe2.isBasic() && tribe1.getNone().contains(tribe2.getBase());
	}
	
	public boolean isHostileRelation(TribeClass tribeName1, TribeClass tribeName2) {
		Tribe tribe1 = tribeNameMap.get(tribeName1);
		Tribe tribe2 = tribeNameMap.get(tribeName2);
		if (tribe1 == null || tribe2 == null)
			return false;
		return tribe1.getHostile().contains(tribeName2) || tribe2.isBasic() && tribe1.getHostile().contains(tribe2.getBase());
	}
	
	public boolean hasAnySupporter(TribeClass tribeName) {
		Tribe tribe1 = tribeNameMap.get(tribeName);
		if (tribe1 == null) {
			return false;
		}
		for (TribeClass tribe2 : tribeNameMap.keySet()) {
			if (isSupportRelation(tribe2, tribeName))
				return true;
		}
		return false;
	}
	
	public Tribe getTribeData(TribeClass tribeName) {
		return tribeNameMap.get(tribeName);
	}
}