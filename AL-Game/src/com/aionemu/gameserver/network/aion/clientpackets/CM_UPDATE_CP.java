package com.aionemu.gameserver.network.aion.clientpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerCpListDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.services.dreamergames.services.CombatPointService;
import com.aionemu.gameserver.skillengine.model.SkillEnchantTemplate;

public class CM_UPDATE_CP extends AionClientPacket {
	
	private static Logger log = LoggerFactory.getLogger(CM_UPDATE_CP.class);
	private int action;
	private int slotId;
	private int cpPoint;
	private int size;
	private Player activePlayer;
	
	public CM_UPDATE_CP(int opcode, AionConnection.State state, AionConnection.State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		activePlayer = ((AionConnection)getConnection()).getActivePlayer();
		action = readC();
		size = readH();
		
		if (this.action == 0) {
			for (int i = 0; i < size; i++) {
				slotId = readD();
				cpPoint = readH();
				
				SkillEnchantTemplate[] slotIds = DataManager.SKILL_ENCHANT_DATA.getTemplatesForGroup(this.slotId);
				if (slotIds[0].getCategory().equals("stat_up")) {
					if (slotIds[0].getCpCountMax() < cpPoint) {
						log.warn("Player try cheating cp point with point : " + cpPoint + " max allowed: " + slotIds[0].getCpCountMax());
						break;
					}
					DAOManager.getDAO(PlayerCpListDAO.class).insertCps(activePlayer, slotId, cpPoint, "stat_up");
				} else if (slotIds[0].getCategory().equals("learn_skill")) {
					DAOManager.getDAO(PlayerCpListDAO.class).insertCps(activePlayer, slotId,cpPoint, "learn_skill");
				} else if (slotIds[0].getCategory().equals("enchant_skill")) {
					if (slotIds[0].getCpCountMax() < cpPoint) {
						log.warn("Player try cheating cp point with point Slots: " + cpPoint + " max allowed: " + slotIds[0].getCpCountMax());
						break;
					}
					DAOManager.getDAO(PlayerCpListDAO.class).insertCps(activePlayer, slotId, cpPoint, "enchant_skill");
				}
			}
		} else {
			DAOManager.getDAO(PlayerCpListDAO.class).clearCpBeforeInsert(activePlayer, "stat_up");
			DAOManager.getDAO(PlayerCpListDAO.class).clearCpBeforeInsert(activePlayer, "learn_skill");
			DAOManager.getDAO(PlayerCpListDAO.class).clearCpBeforeInsert(activePlayer, "enchant_skill");
		}
	}
	
	@Override
	protected void runImpl() {
		if (action == 0) {
			CombatPointService.getInstance().executeCp(activePlayer);
		} else {
			CombatPointService.getInstance().resetCp(activePlayer);
		}
	}
	
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}