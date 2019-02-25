package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.templates.recipe.RecipeTemplate;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author Ranastic
 */
public class SM_AETHERFORGING_PLAYER extends AionServerPacket
{
	private int action;
	private int recipe;
	private int timerPeriod = 4000;
	
	public SM_AETHERFORGING_PLAYER(int action, RecipeTemplate recipeTemplate) {
		this.action = action;
		this.recipe = recipeTemplate.getId();
	}
	
	public SM_AETHERFORGING_PLAYER(int action, int recipe) {
		this.action = action;
		this.recipe = recipe;
	}
	
	@Override
	protected void writeImpl(AionConnection client) {
		writeC(action);
		switch (action) {
			case 0:
				writeD(recipe);
				writeD(timerPeriod); // Time
				break;
			case 1:
				writeD(0);
				writeD(0);
				break;
			case 2:
				writeD(recipe);
				writeD(0);
				break;
		}
	}
}
