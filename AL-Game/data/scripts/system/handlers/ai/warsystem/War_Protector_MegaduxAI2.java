package ai.warsystem;

import java.util.concurrent.atomic.AtomicBoolean;

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

@AIName("war_protector_megadux")
public class War_Protector_MegaduxAI2 extends AggressiveNpcAI2 {
	

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
	}

	
	@Override
    protected void handleSpawned() {
		Npc boss2 = getPosition().getWorldMapInstance().getNpc(260003);
		super.handleSpawned();
		getOwner().setState(1);
		PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getObjectId()));
    }
    
    /*public void applyMegaduxEnergy() {
        World.getInstance().doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(Player player) {
                if (player.getCommonData().getRace() == Race.ASMODIANS) {
                    SkillEngine.getInstance().applyEffectDirectly(12120, player, player, 0); //Mastarius's Energy.
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
				//PacketSendUtility.sendBrightYellowMessageOnCenter(player, "[War System] Megadux has killed by ASMODIANS. You Have Acquired Reward. Check Your inventory. ");
				PacketSendUtility.sendBrightYellowMessageOnCenter(player, "[War System] DEFENDER CAPTAIN ELYOS SPAWN ");
			}		
		});
		WarSystemService.getInstance().doRewardMegadux();
		//applyMegaduxEnergy();
		super.handleDied();
	}
}
