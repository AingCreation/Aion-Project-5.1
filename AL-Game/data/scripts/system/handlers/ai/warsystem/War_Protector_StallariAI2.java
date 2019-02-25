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
package ai.warsystem;

import ai.AggressiveNpcAI2;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.services.dreamergames.services.WarSystemService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;


@AIName("war_protector_stallari")
public class War_Protector_StallariAI2 extends AggressiveNpcAI2 {
	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
	}
	
	@Override
    protected void handleSpawned() {
		Npc boss2 = getPosition().getWorldMapInstance().getNpc(260008);
		super.handleSpawned();
		getOwner().setState(1);
		PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getObjectId()));
    }
	
	/*public void applyStallriEnergy() {
        World.getInstance().doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(Player player) {
                if (player.getCommonData().getRace() == Race.ELYOS) {
                    SkillEngine.getInstance().applyEffectDirectly(12119, player, player, 0); //Veille's Energy.
                    SkillEngine.getInstance().applyEffectDirectly(20410, player, player, 0); //Victory Salute.
                }
            }
        });
    }*/
	
	@Override
	protected void handleDied() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				//PacketSendUtility.sendBrightYellowMessageOnCenter(player, "[War System] Grand Stallari Has Killed by ELYOSIANS. You Have Acquired Reward. Check Your inventory. ");
				PacketSendUtility.sendBrightYellowMessageOnCenter(player, "[War System] DEFENDER CAPTAIN ASMODIANS SPAWN ");
			}		
		});
		WarSystemService.getInstance().doRewardStallari();
		//applyStallriEnergy();
		super.handleDied();
	}
}
