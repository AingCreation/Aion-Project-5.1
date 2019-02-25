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

@InstanceID(300350000)
public class ArenaOfChaosInstance extends PvPArenaInstance
{
	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		killBonus = 1000;
		deathFine = -125;
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
	
	@Override
	protected void reward() {
		int totalPoints = instanceReward.getTotalPoints();
		int size = instanceReward.getInstanceRewards().size();
		float totalScoreAP = (1.0f * size) * 100;
		//float totalScoreGP = (1.0f * size) * 100;
		float totalScoreCrucible = (0.01f * size) * 100;
		float totalScoreCourage = (0.01f * size) * 100;
		float totalScoreInfinity = (0.01f * size) * 100;
		float rankingRate = 0;
		if (size > 1) {
			rankingRate = (0.077f * (8 - size));
		}
		float totalRankingAP = 750 - 750 * rankingRate;
		//float totalRankingGP = 250 - 250 * rankingRate;
		float totalRankingCrucible = 500 - 500 * rankingRate;
		float totalRankingCourage = 100 - 100 * rankingRate;
		float totalRankingInfinity = 100 - 100 * rankingRate;
		for (InstancePlayerReward playerReward : instanceReward.getInstanceRewards()) {
			PvPArenaPlayerReward reward = (PvPArenaPlayerReward) playerReward;
			if (!reward.isRewarded()) {
				float playerRate = 1;
				Player player = instance.getPlayer(playerReward.getOwner());
				if (player != null) {
					playerRate = player.getRates().getChaosRewardRate();
				}
				int score = reward.getScorePoints();
				float scoreRate = ((float) score / (float) totalPoints);
				int rank = instanceReward.getRank(score);
				float percent = reward.getParticipation();
				float generalRate = 0.167f + rank * 0.095f;
				int basicAP = 100;
				//int basicGP = 100;
				float rankingAP = totalRankingAP;
				//float rankingGP = totalRankingGP;
				if (rank > 0) {
					rankingAP = rankingAP - rankingAP * generalRate;
					//rankingGP = rankingGP - rankingGP * generalRate;
				}
				int scoreAP = (int) (totalScoreAP * scoreRate);
				//int scoreGP = (int) (totalScoreGP * scoreRate);
				//<Abyss Points>
				basicAP *= percent;
				rankingAP *= percent;
				rankingAP *= playerRate;
				reward.setBasicAP(basicAP);
				reward.setRankingAP((int) rankingAP);
				reward.setScoreAP(scoreAP);
				//<Glory Points>
				//basicGP *= percent;
				//rankingGP *= percent;
				//rankingGP *= playerRate;
				//reward.setBasicGP(basicGP);
				//reward.setRankingGP((int) rankingGP);
				//reward.setScoreGP(scoreGP);
				int basicCrI = 0;
				basicCrI *= percent;
				float rankingCrI = totalRankingCrucible;
				if (rank > 0) {
					rankingCrI = rankingCrI - rankingCrI * generalRate;
				}
				rankingCrI *= percent;
				rankingCrI *= playerRate;
				int scoreCrI = (int) (totalScoreCrucible * scoreRate);
				reward.setBasicCrucible(basicCrI);
				reward.setRankingCrucible((int) rankingCrI);
				reward.setScoreCrucible(scoreCrI);
				int basicCoI = 0;
				basicCoI *= percent;
				float rankingCoI = totalRankingCourage;
				if (rank > 0) {
					rankingCoI = rankingCoI - rankingCoI * generalRate;
				}
				rankingCoI *= percent;
				rankingCoI *= playerRate;
				int scoreCoI = (int) (totalScoreCourage * scoreRate);
				reward.setBasicCourage(basicCoI);
				reward.setRankingCourage((int) rankingCoI);
				reward.setScoreCourage(scoreCoI);
				//5.1 "Crucible Insignia of Infinity" can be obtained from new "ArchDaeva" Arena
				int basicCiI = 0;
				basicCiI *= percent;
				float rankingCiI = totalRankingInfinity;
				if (rank > 0) {
					rankingCiI = rankingCiI - rankingCiI * generalRate;
				}
				rankingCiI *= percent;
				rankingCiI *= playerRate;
				int scoreCiI = (int) (totalScoreInfinity * scoreRate);
				reward.setBasicInfinity(basicCiI);
				reward.setRankingInfinity((int) rankingCiI);
				reward.setScoreInfinity(scoreCiI);
				if (instanceReward.canRewardOpportunityToken(reward)) {
					reward.setOpportunity(4);
				} if (rank < 2) {
					reward.setGloryTicket(1);
				}
			}
		}
		super.reward();
	}
}