/*
 * This file is part of Encom. **ENCOM FUCK OTHER SVN**
 *
 *  Encom is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Encom is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser Public License
 *  along with Encom.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.services;

import com.aionemu.gameserver.configs.main.PvPConfig;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PvPSpreeService {
	private static final Logger log = LoggerFactory.getLogger("PVP_LOG");
	private static final String STRING_SPREE1 = "BLOODY STORM";
	private static final String STRING_SPREE2 = "CARNAGE";
	private static final String STRING_SPREE3 = "GENOCIDE";
	private static final String STRING_SPREE4 = "GOODLIKE";
	private static final String STRING_SPREE5 = "LEGENDARY";

	public static void increaseRawKillCount(Player winner) {
		int currentRawKillCount = winner.getRawKillCount();
		winner.setRawKillCount(currentRawKillCount + 1);
		int newRawKillCount = currentRawKillCount + 1;
		PacketSendUtility.sendWhiteMessageOnCenter(winner, "You killed " + newRawKillCount + " players in a row for the moment.");

		if (newRawKillCount == PvPConfig.SPREE_KILL_COUNT || newRawKillCount == PvPConfig.RAMPAGE_KILL_COUNT || newRawKillCount == PvPConfig.GENOCIDE_KILL_COUNT) {
			if (newRawKillCount == PvPConfig.SPREE_KILL_COUNT) {
				updateSpreeLevel(winner, 1);
			} if (newRawKillCount == PvPConfig.RAMPAGE_KILL_COUNT) {
				updateSpreeLevel(winner, 2);
			} if (newRawKillCount == PvPConfig.GENOCIDE_KILL_COUNT) {
				updateSpreeLevel(winner, 3); 
			} if (newRawKillCount == PvPConfig.GOODLIKE_KILL_COUNT) {
				updateSpreeLevel(winner, 4);
			} if (newRawKillCount == PvPConfig.LEGENDARY_KILL_COUNT) {
				updateSpreeLevel(winner, 5);
			}
		}
	}

	private static void updateSpreeLevel(Player winner, int level) {
		winner.setSpreeLevel(level);
		sendUpdateSpreeMessage(winner, level);
	}

	private static void sendUpdateSpreeMessage(Player winner, int level) {
		for (Player p : World.getInstance().getAllPlayers()) {
			if (level == 1) {
				PacketSendUtility.sendBrightYellowMessageOnCenter(p, winner.getName() + " Of " + winner.getCommonData().getRace().toString().toLowerCase() + " has started a " + STRING_SPREE1 + " !");	
			} if (level == 2) {
				PacketSendUtility.sendBrightYellowMessageOnCenter(p, winner.getName() + " Of " + winner.getCommonData().getRace().toString().toLowerCase() + " is performing a true " + STRING_SPREE2 + " ! Stop him fast !");
			} if (level == 3) {
				PacketSendUtility.sendBrightYellowMessageOnCenter(p, winner.getName() + " Of " + winner.getCommonData().getRace().toString().toLowerCase() + " is doing a " + STRING_SPREE3 + " ! Run away if you can! !");
			} if (level == 4) {
				PacketSendUtility.sendBrightYellowMessageOnCenter(p, winner.getName() + " Of " + winner.getCommonData().getRace().toString().toLowerCase() + " Cant Stop ! " + STRING_SPREE4 + " Please kill him !");
			} if (level == 5) {
				PacketSendUtility.sendBrightYellowMessageOnCenter(p, winner.getName() + " Of " + winner.getCommonData().getRace().toString().toLowerCase() + " Asap ! its " + STRING_SPREE5 + " He So Strong ! Run Away !");
			}
		}
		log.info("[PvP][Spree] {Player : " + winner.getName() + "} is now on a level " + level + " Killing Spree");
	}

	public static void cancelSpree(Player victim, Creature killer, boolean isPvPDeath) {
		int killsBeforeDeath = victim.getRawKillCount();
		victim.setRawKillCount(0);
		if (victim.getSpreeLevel() > 0) {
			victim.setSpreeLevel(0);
			sendEndSpreeMessage(victim, killer, isPvPDeath, killsBeforeDeath);
		}
	}

	private static void sendEndSpreeMessage(Player victim, Creature killer, boolean isPvPDeath, int killsBeforeDeath) {
		String spreeEnder = isPvPDeath ? ((Player)killer).getName() : "A monster";
		for (Player p : World.getInstance().getAllPlayers()) {
			PacketSendUtility.sendWhiteMessageOnCenter(p, "The killing spree of " + victim.getName() + " has been stopped by " + spreeEnder + " after " + killsBeforeDeath + " uninterrupted murders !");
		}
		log.info("[PvP][Spree] {The killing spree of " + victim.getName() + "} has been stopped by " + spreeEnder + "}");
	}
}