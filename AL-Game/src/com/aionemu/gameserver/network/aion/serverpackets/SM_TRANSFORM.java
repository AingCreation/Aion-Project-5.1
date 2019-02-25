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
package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.skillengine.model.TransformType;

/**
 * @author Sweetkr, xTz
 */
public class SM_TRANSFORM extends AionServerPacket {

	private Creature creature;
	private int state;
	private int modelId;
	private boolean applyEffect;
	private int panelId;
	
	public SM_TRANSFORM(Creature creature, boolean applyEffect) {
		this.creature = creature;
		state = creature.getState();
		modelId = creature.getTransformModel().getModelId();
		this.applyEffect = applyEffect;
	}
	
	public SM_TRANSFORM(Creature creature, int panelId, boolean applyEffect) {
		this.creature = creature;
		state = creature.getState();
		modelId = creature.getTransformModel().getModelId();
		this.panelId = panelId;
		this.applyEffect = applyEffect;
	}
	
	public SM_TRANSFORM(Creature creature, int panelId, boolean applyEffect, int itemId) {
		this.creature = creature;
		state = creature.getState();
		modelId = creature.getTransformModel().getModelId();
		this.panelId = panelId;
		this.applyEffect = applyEffect;
	}
	
	protected void writeImpl(AionConnection con) {
		writeD(creature.getObjectId().intValue());
		writeD(modelId);
		writeH(state);
		writeF(0.25F);
		writeF(2.0F);
		writeC(applyEffect && creature.getTransformModel().getType() == TransformType.NONE ? 1 : 0);
		writeD(creature.getTransformModel().getType().getId());
		writeC(0);
		writeC(0);
		writeC(0);
		writeC(0);
		writeC(0);
		writeC(0);
		writeD(panelId);
		writeD(creature.getTransformModel().getEquipmentId());
		writeH(creature.getTransformModel().getEquipmentId() != 0 ? 1 : 0);
		writeC(0);
	}
}
