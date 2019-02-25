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
package com.aionemu.gameserver.skillengine.effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CUSTOM_SETTINGS;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ShapeChangeEffect")
public class ShapeChangeEffect extends TransformEffect {

	@Override
	public void startEffect(Effect effect) {
		super.startEffect(effect);
		if (effect.getEffected() instanceof Player) {
			final Player player = (Player)effect.getEffected();
			player.getKnownList().doOnAllNpcs(new Visitor<Npc>() {
				
				@Override
				public void visit(Npc npc) {
					PacketSendUtility.sendPacket(player, new SM_CUSTOM_SETTINGS(npc.getObjectId(), 0, npc.getType(player), 0));
				}
				
			});
		}
	}

	@Override
	public void endEffect(Effect effect) {
		super.endEffect(effect);
		if (effect.getEffected() instanceof Player) {
			final Player player = (Player)effect.getEffected();
			player.getKnownList().doOnAllNpcs(new Visitor<Npc>() {
				
				@Override
				public void visit(Npc npc) {
					PacketSendUtility.sendPacket(player, new SM_CUSTOM_SETTINGS(npc.getObjectId(), 0, npc.getType(player), 0));
					player.getTransformModel().setTribe(null, false);
				}
				
			});
		}
	}
}
