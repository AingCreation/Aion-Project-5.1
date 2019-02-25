package com.aionemu.gameserver.network.aion.clientpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class CM_CASTSPELL extends AionClientPacket
{
	private Logger log = LoggerFactory.getLogger(CM_CASTSPELL.class);
	private int spellid;
	// 0 - obj id, 1 - point location, 2 - unk, 3 - object not in sight(skill 1606)? 4 - unk
	private int targetType;
	private float x, y, z;
	@SuppressWarnings("unused")
	private int targetObjectId;
	private int hitTime;
	private int level;
	private int unk;

	/**
	 * Constructs new instance of <tt>CM_CM_REQUEST_DIALOG </tt> packet
	 *
	 * @param opcode
	 */
	public CM_CASTSPELL(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		Player player = getConnection().getActivePlayer();
		spellid = readH();
		if (spellid == 0) {
			player.getController().cancelCurrentSkill();
			return;
		}
		
		level = readC();

		targetType = readC();

		switch (targetType) {
			case 0:
			case 3:
			case 4:
			case 87:
				targetObjectId = readD();
				break;
			case 1:
				x = readF();
				y = readF();
				z = readF();
				break;
			case 2:
				x = readF();
				y = readF();
				z = readF();
				readF();// unk1
				readF();// unk2
				readF();// unk3
				readF();// unk4
				readF();// unk5
				readF();// unk6
				readF();// unk7
				readF();// unk8
				break;
		}

		hitTime = readH();
		unk = readD();// unk can be big values
		log.debug("[CM_CASTSPELL] Unk value: " + unk);
	}

	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();

		SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(spellid);
		if (template == null || template.isPassive()) {
			return;
		}

		if (player.isProtectionActive()) {
			player.getController().stopProtectionActiveTask();
		}

		long currentTime = System.currentTimeMillis();
		if (player.getNextSkillUse() > currentTime) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300021));
			return;
		}

		if (!player.getLifeStats().isAlreadyDead()) {
			player.getController().useSkill(template, targetType, x, y, z, hitTime, level);
		}
	}
}