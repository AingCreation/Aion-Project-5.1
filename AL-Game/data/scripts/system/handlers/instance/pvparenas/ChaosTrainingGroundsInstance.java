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
import com.aionemu.gameserver.model.flyring.FlyRing;
import com.aionemu.gameserver.model.gameobjects.Gatherable;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.instance.playerreward.InstancePlayerReward;
import com.aionemu.gameserver.model.instance.playerreward.PvPArenaPlayerReward;
import com.aionemu.gameserver.model.templates.flyring.FlyRingTemplate;
import com.aionemu.gameserver.model.utils3d.Point3D;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(300420000)
public class ChaosTrainingGroundsInstance extends PvPArenaInstance
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
	
	@Override
	protected void spawnRings() {
		FlyRing f1 = new FlyRing(new FlyRingTemplate("PVP_ARENA_1", mapId,
		new Point3D(674.66974, 1792.8499, 149.77501),
		new Point3D(674.66974, 1792.8499, 155.77501),
		new Point3D(678.83636, 1788.5325, 149.77501), 6), instanceId);
		f1.spawn();
		FlyRing f2 = new FlyRing(new FlyRingTemplate("PVP_ARENA_2", mapId,
		new Point3D(688.30615, 1769.7937, 149.88556),
		new Point3D(688.30615, 1769.7937, 155.88556),
		new Point3D(689.42096, 1763.8982, 149.88556), 6), instanceId);
		f2.spawn();
		FlyRing f3 = new FlyRing(new FlyRingTemplate("PVP_ARENA_3", mapId,
		new Point3D(664.2252, 1761.671, 170.95732),
		new Point3D(664.2252, 1761.671, 176.95732),
		new Point3D(669.2843, 1764.8967, 170.95732), 6), instanceId);
		f3.spawn();
		FlyRing fv1 = new FlyRing(new FlyRingTemplate("PVP_ARENA_VOID_1", mapId,
		new Point3D(690.28625, 1753.8561, 192.07726),
		new Point3D(690.28625, 1753.8561, 198.07726),
		new Point3D(689.4365, 1747.9165, 192.07726), 6), instanceId);
		fv1.spawn();
		FlyRing fv2 = new FlyRing(new FlyRingTemplate("PVP_ARENA_VOID_2", mapId,
		new Point3D(690.1935, 1797.0029, 203.79236),
		new Point3D(690.1935, 1797.0029, 209.79236),
		new Point3D(692.8295, 1802.3928, 203.79236), 6), instanceId);
		fv2.spawn();
		FlyRing fv3 = new FlyRing(new FlyRingTemplate("PVP_ARENA_VOID_3", mapId,
		new Point3D(659.2784, 1766.0273, 207.25465),
		new Point3D(659.2784, 1766.0273, 213.25465),
		new Point3D(665.2619, 1766.4718, 207.25465), 6), instanceId);
		fv3.spawn();
	}
	
	@Override
	public boolean onPassFlyingRing(Player player, String flyingRing) {
		PvPArenaPlayerReward playerReward = getPlayerReward(player.getObjectId());
		if (playerReward == null || !instanceReward.isStartProgress()) {
			return false;
		}
		Npc npc;
		if (flyingRing.equals("PVP_ARENA_1")) {
			npc = getNpc(674.841f, 1793.065f, 150.964f);
			if (npc != null && npc.isSpawned()) {
				npc.getController().scheduleRespawn();
				npc.getController().onDelete();
				sendSystemMsg(player, npc, 250);
				sendPacket();
			}
		} else if (flyingRing.equals("PVP_ARENA_2")) {
			npc = getNpc(688.410f, 1769.611f, 150.964f);
			if (npc != null && npc.isSpawned()) {
				npc.getController().scheduleRespawn();
				npc.getController().onDelete();
				playerReward.addPoints(250);
				sendSystemMsg(player, npc, 250);
				sendPacket();
			}
		} else if (flyingRing.equals("PVP_ARENA_3")) {
			npc = getNpc(664.160f, 1761.933f, 171.504f);
			if (npc != null && npc.isSpawned()) {
				npc.getController().scheduleRespawn();
				npc.getController().onDelete();
				playerReward.addPoints(250);
				sendSystemMsg(player, npc, 250);
				sendPacket();
			}
		} else if (flyingRing.equals("PVP_ARENA_VOID_1")) {
			npc = getNpc(693.061f, 1752.479f, 186.750f);
			if (npc != null && npc.isSpawned()) {
				useSkill(npc, player, 20059, 1);
				npc.getController().scheduleRespawn();
				npc.getController().onDelete();
			}
		} else if (flyingRing.equals("PVP_ARENA_VOID_2")) {
			npc = getNpc(688.061f, 1798.229f, 198.500f);
			if (npc != null && npc.isSpawned()) {
				useSkill(npc, player, 20059, 1);
				npc.getController().scheduleRespawn();
				npc.getController().onDelete();
			}
		} else if (flyingRing.equals("PVP_ARENA_VOID_3")) {
			npc = getNpc(659.311f, 1768.979f, 201.500f);
			if (npc != null && npc.isSpawned()) {
				useSkill(npc, player, 20059, 1);
				npc.getController().scheduleRespawn();
				npc.getController().onDelete();
			}
		}
		return false;
	}
}