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
package quest.ishalgen;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAY_MOVIE;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Rinzler (Encom)
/****/

public class _2000Prologue extends QuestHandler
{
	private final static int questId = 2000;
	
	public _2000Prologue() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnMovieEndQuest(2, questId);
		qe.registerOnEnterZone(ZoneName.get("ALDELLE_BASIN_220010000"), questId);
	}
	
	@Override
	public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
		if (zoneName == ZoneName.get("ALDELLE_BASIN_220010000")) {
			final Player player = env.getPlayer();
			if (player.getCommonData().getRace() != Race.ASMODIANS)
			   return false;
			QuestState qs = player.getQuestStateList().getQuestState(questId);
			if (qs == null) {
			    env.setQuestId(questId);
			    QuestService.startQuest(env);
		    }
			qs = player.getQuestStateList().getQuestState(questId);
			if (qs.getStatus() == QuestStatus.START) {
			    if (player.getCommonData().isInNewbieGuide()) {
                return false;
            } else {
                PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(1, 2));
            }
			    return true;
		    }
		}
		return false;
	}
	
	@Override
	public boolean onMovieEndEvent(QuestEnv env, int movieId) {
		if (movieId != 2)
			return false;
		Player player = env.getPlayer();
		if (player.getCommonData().getRace() != Race.ASMODIANS)
			return false;
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() != QuestStatus.START)
			return false;
		qs.setStatus(QuestStatus.REWARD);
		QuestService.finishQuest(env);
		return true;
	}
}