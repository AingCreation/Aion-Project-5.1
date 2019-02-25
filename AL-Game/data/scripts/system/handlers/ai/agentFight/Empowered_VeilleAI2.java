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
package ai.agentFight;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import ai.AggressiveNpcAI2;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.AgentService;
import com.aionemu.gameserver.services.BaseService;
import com.aionemu.gameserver.services.HTMLService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldPosition;
import com.aionemu.gameserver.world.knownlist.Visitor;

/****/
/** Author Rinzler (Encom)
 * @rework idhacker542
 */
/****/

@AIName("empowered_veille")
public class Empowered_VeilleAI2 extends AggressiveNpcAI2
{
	private int veillePhase = 0;
	private AtomicBoolean isAggred = new AtomicBoolean(false);
	
	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (isAggred.compareAndSet(false, true)) {
			switch (getNpcId()) {
				case 235064: //Empowered Veille.
				    announceAgentUnderAttack();
				break;
			}
		}
		checkPercentage(getLifeStats().getHpPercentage());
	}
	
	private void checkPercentage(int hpPercentage) {
		if (hpPercentage == 50 && veillePhase < 1) {
			veillePhase = 1;
			announceJusinOdSpawn();
			announceEmpyreanLordAgentHP50();
		} if (hpPercentage == 10 && veillePhase < 2) {
			veillePhase = 2;
			announceEmpyreanLordAgentHP10();
		}
	}
	
	@Override
	protected void handleDied() {
		final WorldPosition p = getPosition();
		if (p != null) {
			sendVeilleGuide();
		}
		despawnNpc(296907); //Veille's Aether Concentrator I.
		despawnNpc(296908); //Veille's Aether Concentrator II.
		applyMastariusEnergy();
		announceKilledVeille();
		announceEmpoweredVeilleDie();
		AgentService.getInstance().stopAgentFight(1);
		AgentService.getInstance().onRewardVeille();
		AgentService.getInstance().onRewardVeilles();
		BaseService.getInstance().capture(90, Race.ASMODIANS);
		super.handleDied();
	}
	
	private void sendVeilleGuide() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (MathUtil.isIn3dRange(player, getOwner(), 15)) {
					HTMLService.sendGuideHtml(player, "Agent_Fight");
				}
			}
		});
	}
	
	private void announceKilledVeille() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				AionObject winner = getAggroList().getMostDamage();
				if (winner instanceof Creature) {
					final Creature kill = (Creature) winner;
					//"Player Name" of the "Race" has killed Kaisinel's Agent Veille.
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400324, kill.getRace().getRaceDescriptionId(), kill.getName()));
				}
			}
		});
	}
	private void announceAgentUnderAttack() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				//Kaisinel's Agent Veille is under attack!
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_FIELDABYSS_LIGHTBOSS_ATTACKED);
			}
		});
	}
	private void announceJusinOdSpawn() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				//The Empyrean Lord Agent summoned the Aether Concentrator.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF4_Jusin_OdSpawn, 0);
				//The Empyrean Lord Agent has enabled the Aether Concentrator.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF4_Jusin_OdStart, 20000);
			}
		});
	}
	private void announceEmpyreanLordAgentHP50() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				//The Empyrean Lord Agent's HP has dropped below 50%
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF4_Jusin_Hp50);
			}
		});
	}
	private void announceEmpyreanLordAgentHP10() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				//The Empyrean Lord Agent's HP has dropped below 10%
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF4_Jusin_Hp10);
			}
		});
	}
	private void announceEmpoweredVeilleDie() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				//The Agent battle has ended.
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF4_Advance_GodElite_time_03);
			}
		});
	}
	
	public void applyMastariusEnergy() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.getCommonData().getRace() == Race.ASMODIANS) {
				    SkillEngine.getInstance().applyEffectDirectly(12120, player, player, 0); //Mastarius's Energy.
					SkillEngine.getInstance().applyEffectDirectly(20410, player, player, 0); //Victory Salute.
				}
			}
		});
	}
	
	private void despawnNpc(int npcId) {
		if (getPosition().getWorldMapInstance().getNpcs(npcId) != null) {
			List<Npc> npcs = getPosition().getWorldMapInstance().getNpcs(npcId);
			for (Npc npc: npcs) {
				npc.getController().onDelete();
			}
		}
	}
}