package com.aionemu.gameserver.services.dreamergames.services;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.TribeClass;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.templates.npc.AbyssNpcType;
import com.aionemu.gameserver.model.templates.npc.NpcUiType;
import com.aionemu.gameserver.model.templates.tribe.Tribe;

public class TribeRelationService {

	public static boolean isAggressive(Creature creature1, Creature creature2) {
		Tribe tribe1 = DataManager.TRIBE_RELATIONS_DATA.getTribeData(creature1.getTribe());
		Tribe tribe2 = DataManager.TRIBE_RELATIONS_DATA.getTribeData(creature2.getTribe());

		if (tribe1.getAggressive().isEmpty() && tribe2.getAggressive().isEmpty()) {
			switch (creature1.getBaseTribe()) {
				case GUARD_DARK:
					switch (creature2.getBaseTribe()) {
						case PC:
						case GUARD:
						case GENERAL:
						case GUARD_DRAGON:
							return true;
						default:
							break;
					}
					break;
				case GUARD:
					switch (creature2.getBaseTribe()) {
						case PC_DARK:
						case GUARD_DARK:
						case GENERAL_DARK:
						case GUARD_DRAGON:
							return true;
						default:
							break;

					}
					break;
				case GUARD_DRAGON:
					switch (creature2.getBaseTribe()) {
						case PC_DARK:
						case PC:
						case GUARD:
						case GUARD_DARK:
						case GENERAL_DARK:
						case GENERAL:
							return true;
						default:
							break;

					}
					break;
				default:
					break;
			}
		}
		return DataManager.TRIBE_RELATIONS_DATA.isAggressiveRelation(creature1.getTribe(), creature2.getTribe());
	}

	public static boolean isFriend(Creature creature1, Creature creature2) {
		if (creature1.getTribe() == creature2.getTribe()) // OR BASE ????
		{
			return true;
		}
		switch (creature1.getBaseTribe()) {
			case USEALL:
			case FIELD_OBJECT_ALL:
				return true;
			case GENERAL_DARK:
				switch (creature2.getBaseTribe()) {
					case PC_DARK:
					case GUARD_DARK:
						return true;
					default:
						break;
				}
				break;
			case GENERAL:
				switch (creature2.getBaseTribe()) {
					case PC:
					case GUARD:
						return true;
					default:
						break;

				}
				break;
			case FIELD_OBJECT_LIGHT:
				switch (creature2.getBaseTribe()) {
					case PC:
						return true;
					default:
						break;

				}
			case FIELD_OBJECT_DARK:
				switch (creature2.getBaseTribe()) {
					case PC_DARK:
						return true;
					default:
						break;

				}
				break;
			case GUARD_DARK:
				switch (creature2.getBaseTribe()) {
					case PC_DARK:
						return true;
					default:
						break;
				}
				break;
			case GUARD:
				switch (creature2.getBaseTribe()) {
					case PC:
						return true;
					default:
						break;

				}
				break;
			default:
				break;
		}
		return DataManager.TRIBE_RELATIONS_DATA.isFriendlyRelation(creature1.getTribe(), creature2.getTribe());
	}

	public static boolean isSupport(Creature creature1, Creature creature2) {
		// switch (creature1.getBaseTribe()) {
		// case GUARD_DARK:
		// switch (creature2.getBaseTribe()) {
		// case PC_DARK:
		// return true;
		// }
		// break;
		// case GUARD:
		// switch (creature2.getBaseTribe()) {
		// case PC:
		// return true;
		//
		// }
		// break;
		// }
		return DataManager.TRIBE_RELATIONS_DATA.isSupportRelation(creature1.getTribe(), creature2.getTribe());
	}

	public static boolean isInvulnerable(Creature creature1, Creature creature2) {
		switch (creature1.getTribe()) {
			case IDFORTRESS_VRITRA:
				switch (creature2.getBaseTribe()) {
					case PC:
					case PC_DARK:
						if (creature1 instanceof Npc) {
							Npc targetNpc = (Npc) creature1;
							if (creature2.getWorldId() != 301310000 && targetNpc.getObjectTemplate().getNpcUiType().equals(NpcUiType.NONE)) {
								return true;
							}
						}
					default:
						break;
				}
				break;
			case IDFORTRESS_SWITCH_LIGHT:
			case IDKAMAR_PROTECTGUARD_LIGHT:
				switch (creature2.getBaseTribe()) {
					case PC_DARK:
						return true;
					default:
						break;
				}
				break;
			case IDFORTRESS_SWITCH_DARK:
			case IDKAMAR_PROTECTGUARD_DARK:
				switch (creature2.getBaseTribe()) {
					case PC:
						return true;
					default:
						break;
				}
				break;
			default:
				break;
		}
		return false;
	}

	public static boolean isNone(Creature creature1, Creature creature2) {
		if (DataManager.TRIBE_RELATIONS_DATA.isAggressiveRelation(creature1.getTribe(), creature2.getTribe()) || creature1 instanceof Npc && checkSiegeRelation((Npc) creature1, creature2) || DataManager.TRIBE_RELATIONS_DATA.isHostileRelation(creature1.getTribe(), creature2.getTribe()) || DataManager.TRIBE_RELATIONS_DATA.isNeutralRelation(creature1.getTribe(), creature2.getTribe())) {
			return false;
		}
		switch (creature1.getBaseTribe()) {
			case GENERAL_DRAGON:
				return true;
			case GENERAL:
			case FIELD_OBJECT_LIGHT:
				switch (creature2.getBaseTribe()) {
					case PC_DARK:
						return true;
					default:
						break;
				}
				break;
			case GENERAL_DARK:
			case FIELD_OBJECT_DARK:
				switch (creature2.getBaseTribe()) {
					case PC:
						return true;
					default:
						break;

				}
				break;
			default:
				break;
		}
		return DataManager.TRIBE_RELATIONS_DATA.isNoneRelation(creature1.getTribe(), creature2.getTribe());
	}

	public static boolean isNeutral(Creature creature1, Creature creature2) {
		return DataManager.TRIBE_RELATIONS_DATA.isNeutralRelation(creature1.getTribe(), creature2.getTribe());
	}

	public static boolean isHostile(Creature creature1, Creature creature2) {
		if (creature1 instanceof Npc && checkSiegeRelation((Npc) creature1, creature2)) {
			return true;
		}
		switch (creature1.getBaseTribe()) {
			case MONSTER:
				switch (creature2.getBaseTribe()) {
					case PC_DARK:
					case PC:
						return true;
					default:
						break;
				}
				break;
			default:
				break;
		}
		return DataManager.TRIBE_RELATIONS_DATA.isHostileRelation(creature1.getTribe(), creature2.getTribe());
	}

	public static boolean checkSiegeRelation(Npc npc, Creature creature) {
		return npc.getObjectTemplate().getAbyssNpcType() != AbyssNpcType.ARTIFACT && npc.getObjectTemplate().getAbyssNpcType() != AbyssNpcType.NONE && ((npc.getBaseTribe() == TribeClass.GENERAL && creature.getTribe() == TribeClass.PC_DARK) || (npc.getBaseTribe() == TribeClass.GENERAL_DARK && creature.getTribe() == TribeClass.PC) || npc.getBaseTribe() == TribeClass.GENERAL_DRAGON);
	}
}