/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 *
 *  Aion-Lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Aion-Lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details. *
 *  You should have received a copy of the GNU General Public License
 *  along with Aion-Lightning.
 *  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.gameobjects;

import com.aionemu.gameserver.model.TribeClass;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.model.TransformType;

/**
 * @author Rolandas
 */
public class TransformModel {

	private int modelId;
	private int originalModelId;
	private TransformType originalType;
	private TransformType transformType;
	private int panelId;
	private int equipmentId;
	private boolean isActive = false;
	private TribeClass transformTribe;
	private TribeClass overrideTribe;
	
	public TransformModel(Creature creature) {
		if (creature instanceof Player) {
			originalType = TransformType.PC;
		} else {
			originalType = TransformType.NONE;
		}
		originalModelId = creature.getObjectTemplate().getTemplateId();
		transformType = TransformType.NONE;
	}
	
	public int getModelId() {
		if (isActive && modelId > 0) {
			return modelId;
		}
		return originalModelId;
	}
	
	public void setModelId(int modelId) {
		if (modelId == 0 || modelId == this.originalModelId) {
			modelId = originalModelId;
			isActive = false;
		} else {
			this.modelId = modelId;
			isActive = true;
		}
	}
	
	public TransformType getType() {
		if (isActive) {
			return transformType;
		}
		return originalType;
	}
	
	public void setTransformType(TransformType transformType) {
		this.transformType = transformType;
	}
	
	public int getPanelId() {
		if (isActive) {
			return panelId;
		}
		return 0;
	}
	
	public void setPanelId(int id) {
		panelId = id;
	}
	
	public int getEquipmentId() {
		return equipmentId;
	}
	
	public void setEquipmentId(int equipmentId) {
		this.equipmentId = equipmentId;
	}
	
	public boolean isActive() {
		return isActive;
	}
	
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	public TribeClass getTribe() {
		if (isActive && transformTribe != null) {
			return transformTribe;
		}
		return overrideTribe;
	}
	
	public void setTribe(TribeClass transformTribe, boolean override) {
		if (override) {
			overrideTribe = transformTribe;
		} else {
			this.transformTribe = transformTribe;
		}
	}
}
