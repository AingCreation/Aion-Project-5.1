/*
 * This file is part of aion-lightning <aion-lightning.com>.
 *
 *  aion-lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-lightning.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.skillengine.condition;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.model.Skill;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ChainCondition")
public class ChainCondition extends Condition {

	@XmlAttribute(name = "selfcount")
	private int selfCount;

	@XmlAttribute(name = "precount")
	private int preCount;
	@XmlAttribute(name = "category")
	private String category;
	@XmlAttribute(name = "precategory")
	private String precategory;
	@XmlAttribute(name = "time")
	private int time;

	@Override
	public boolean validate(Skill env) {
		if ((env.getEffector() instanceof Player) && (precategory != null || selfCount > 0)) {
			Player pl = (Player) env.getEffector();
			
			if (this.time <= 0) {
		       time = 3000;
			}

			if (selfCount > 0) {
				boolean canUse = false;

				if (precategory != null && pl.getChainSkills().chainSkillEnabled(precategory, time)) {
					canUse = true;
				}

				if (pl.getChainSkills().chainSkillEnabled(category, time)) {
					canUse = true;
				}
				else if (precategory == null) {
					canUse = true;
				}

				if (!canUse) {
					return false;
				}
				if (selfCount <= pl.getChainSkills().getChainCount(pl, env.getSkillTemplate(), category)) {
					return false;
				}
				env.setIsMultiCast(true);
			}
			else if (preCount > 0 && selfCount > 0) {
				if (!pl.getChainSkills().chainSkillEnabled(precategory, time) || preCount != pl.getChainSkills().getChainCount(pl, env.getSkillTemplate(), precategory)) {
					return false;
				}
			}
			else if (!pl.getChainSkills().chainSkillEnabled(precategory, time)) {
				return false;
			}
		}

		env.setChainCategory(category);
		return true;
	}

	public int getSelfCount() {
		return selfCount;
	}

	public String getCategory() {
		return category;
	}

	public int getTime() {
		return time;
	}
}
