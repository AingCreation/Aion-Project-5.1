package com.aionemu.gameserver.skillengine.task;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.StaticObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.recipe.RecipeTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_AETHERFORGING_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_AETHERFORGING_PLAYER;
import com.aionemu.gameserver.services.craft.AetherForging;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

public class AetherForgingTask extends CraftingTask {

	public AetherForgingTask(Player requestor, StaticObject responder, RecipeTemplate recipeTemplate) {
		super(requestor, responder, recipeTemplate, 0, 0);
		// TODO Auto-generated constructor stub
		this.maxSuccessValue = 2000000;
		this.maxFailureValue = 2000000;
	}
	
	@Override
	public void start() {
		onInteractionStart();
		task = ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (!validateParticipants()) {
					stop(true);
				}
				boolean stopTask = onSuccessFinish();
				if (stopTask) {
					stop(false);
				}
			}
		}, 4000);
	}

	@Override
	protected void analyzeInteraction() {
	}

	@Override
	protected boolean onSuccessFinish() {
		PacketSendUtility.sendPacket(requestor, new SM_AETHERFORGING_ANIMATION(requestor.getObjectId(), 2));
		PacketSendUtility.sendPacket(requestor, new SM_AETHERFORGING_PLAYER(2, recipeTemplate));
		AetherForging.finishForgingCrafting(requestor, recipeTemplate, 0, 0);
		return true;
	}

	@Override
	protected void onInteractionFinish() {
		requestor.setCraftingTask(null);
	}

	@Override
	protected void sendInteractionUpdate() {
	}

	@Override
	protected void onInteractionStart() {
		this.itemTemplate = DataManager.ITEM_DATA.getItemTemplate(recipeTemplate.getProductid());
		PacketSendUtility.sendPacket(requestor, new SM_AETHERFORGING_PLAYER(0, recipeTemplate));
		PacketSendUtility.sendPacket(requestor, new SM_AETHERFORGING_ANIMATION(requestor.getObjectId(), 0));
	}

	@Override
	protected void onInteractionAbort() {
		PacketSendUtility.sendPacket(requestor, new SM_AETHERFORGING_ANIMATION(requestor.getObjectId(), 1));
		PacketSendUtility.sendPacket(requestor, new SM_AETHERFORGING_PLAYER(1, 0));
		requestor.setCraftingTask(null);
	}

}
