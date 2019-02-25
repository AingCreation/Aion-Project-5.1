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
package instance.pvparenas;

import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Gatherable;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.instance.playerreward.InstancePlayerReward;
import com.aionemu.gameserver.model.instance.playerreward.PvPArenaPlayerReward;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(300360000)
public class ArenaOfDisciplineInstance extends PvPArenaInstance
{
	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		killBonus = 200;
		deathFine = -100;
		super.onInstanceCreate(instance);
	}
	
	@Override
	public void onGather(Player player, Gatherable gatherable) {
		if (!instanceReward.isStartProgress()) {
			return;
		}
		getPlayerReward(player.getObjectId()).addPoints(1250);
		sendPacket();
		int nameId = gatherable.getObjectTemplate().getNameId();
		DescriptionId name = new DescriptionId(nameId * 2 + 1);
		PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400237, name, 1250));
	}
	
	protected void reward() {
		int totalPoints = instanceReward.getTotalPoints();
		int size = instanceReward.getInstanceRewards().size();
		float totalAP = (1.0f * size) * 100;
		//float totalGP = (1.0f * size) * 100;
		float totalCrucible = (0.01f * size) * 100;
		float totalCourage = (0.01f * size) * 100;
		float totalInfinity = (0.01f * size) * 100;
		for (InstancePlayerReward playerReward : instanceReward.getInstanceRewards()) {
			PvPArenaPlayerReward reward = (PvPArenaPlayerReward) playerReward;
			if (!reward.isRewarded()) {
				float playerRate = 1;
				Player player = instance.getPlayer(playerReward.getOwner());
				if (player != null) {
					playerRate = player.getRates().getDisciplineRewardRate();
				}
				int score = reward.getScorePoints();
				float scoreRate = ((float) score / (float) totalPoints);
				int rank = instanceReward.getRank(score);
				float percent = reward.getParticipation();
				int basicAP = 100;
				//int basicGP = 100;
				int rankingAP = 431;
				//int rankingGP = 231;
				if (size > 1) {
					rankingAP = rank == 0 ? 1108 : 431;
					//rankingGP = rank == 0 ? 908 : 231;
				}
				int scoreAP = (int) (totalAP * scoreRate);
				//int scoreGP = (int) (totalGP * scoreRate);
				//<Abyss Points>
				basicAP *= percent;
				rankingAP *= percent;
				rankingAP *= playerRate;
				reward.setBasicAP(basicAP);
				reward.setRankingAP(rankingAP);
				reward.setScoreAP(scoreAP);
				//<Glory Points>
				//basicGP *= percent;
				//rankingGP *= percent;
				//rankingGP *= playerRate;
				//reward.setBasicGP(basicGP);
				//reward.setRankingGP(rankingGP);
				//reward.setScoreGP(scoreGP);
				int basicCrI = 0;
				basicCrI *= percent;
				int rankingCrI = 150;
				if (size > 1) {
					rankingCrI = rank == 0 ? 500 : 150;
				}
				rankingCrI *= percent;
				rankingCrI *= playerRate;
				int scoreCrI = (int) (totalCrucible * scoreRate);
				reward.setBasicCrucible(basicCrI);
				reward.setRankingCrucible(rankingCrI);
				reward.setScoreCrucible(scoreCrI);
				int basicCoI = 0;
				basicCoI *= percent;
				int rankingCoI = 23;
				if (size > 1) {
					rankingCoI = rank == 0 ? 59 : 23;
				}
				rankingCoI *= percent;
				rankingCoI *= playerRate;
				int scoreCoI = (int) (totalCourage * scoreRate);
				reward.setBasicCourage(basicCoI);
				reward.setRankingCourage(rankingCoI);
				reward.setScoreCourage(scoreCoI);
				//5.1 "Crucible Insignia of Infinity" can be obtained from new "ArchDaeva" Arena
				int basicCiI = 0;
				basicCiI *= percent;
				int rankingCiI = 23;
				if (size > 1) {
					rankingCiI = rank == 0 ? 59 : 23;
				}
				rankingCiI *= percent;
				rankingCiI *= playerRate;
				int scoreCiI = (int) (totalInfinity * scoreRate);
				reward.setBasicInfinity(basicCiI);
				reward.setRankingInfinity(rankingCiI);
				reward.setScoreInfinity(scoreCiI);
				if (instanceReward.canRewardOpportunityToken(reward)) {
					reward.setOpportunity(4);
				}
			}
		}
		super.reward();
	}
}