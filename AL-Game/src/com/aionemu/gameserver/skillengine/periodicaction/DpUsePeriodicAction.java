package com.aionemu.gameserver.skillengine.periodicaction;

import javax.xml.bind.annotation.XmlAttribute;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class DpUsePeriodicAction extends PeriodicAction {
	@XmlAttribute(name="value")
	protected int value;
	@XmlAttribute(name="ratio")
	protected boolean ratio;
	@XmlAttribute(name="percent")
	protected boolean percent;
  
	public void act(Effect effect) {
		Player player = (Player)effect.getEffector();
		int currentDp = player.getCommonData().getDp();
		if (currentDp <= 0 || currentDp < value) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_SKILL_NOT_ENOUGH_DP);
			effect.endEffect();
			player.getCommonData().setDp(currentDp);
			return;
		}
		player.getCommonData().setDp(currentDp - value);
	}
}