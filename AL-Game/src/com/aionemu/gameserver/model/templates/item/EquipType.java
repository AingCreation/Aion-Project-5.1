/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.templates.item;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * @author ATracer
 */
@XmlType(name = "equipType")
@XmlEnum
public enum EquipType {

	ARMOR (1),
	WEAPON (1),
	STIGMA (3),
	ESTIMA (4),
	ACCESSORY (5),
	NONE (6);
	
	private int id;
	
	private EquipType(int id) {
		this.id = id;
	}

	public String value() {
		return name();
	}

	public static EquipType fromValue(String v) {
		return valueOf(v);
	}

	public int getId() {
		// TODO Auto-generated method stub
		return id;
	}

}
