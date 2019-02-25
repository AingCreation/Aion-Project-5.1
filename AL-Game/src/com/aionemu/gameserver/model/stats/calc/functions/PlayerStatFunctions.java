package com.aionemu.gameserver.model.stats.calc.functions;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.container.StatEnum;

public class PlayerStatFunctions
{
	private static final List<IStatFunction> FUNCTIONS = new ArrayList<IStatFunction>();
	
	static {
		FUNCTIONS.add(new AttackSpeedFunction());
		FUNCTIONS.add(new PhysicalAttackFunction());
		FUNCTIONS.add(new MagicalAttackFunction());
		FUNCTIONS.add(new BoostCastingTimeFunction());
		FUNCTIONS.add(new PvPAttackRatioFunction());
		FUNCTIONS.add(new PvPDefendRatioFunction());
		FUNCTIONS.add(new PvPPhysicalAttackRatioFunction());
		FUNCTIONS.add(new PvPMagicalAttackRatioFunction());
		FUNCTIONS.add(new PvPPhysicalDefendRatioFunction());
		FUNCTIONS.add(new PvPMagicalDefendRatioFunction());
		FUNCTIONS.add(new PDefFunction());
		FUNCTIONS.add(new MaxHpFunction());
		FUNCTIONS.add(new MaxMpFunction());
		FUNCTIONS.add(new AgilityModifierFunction(StatEnum.BLOCK, 0.25f));
		FUNCTIONS.add(new AgilityModifierFunction(StatEnum.PARRY, 0.25f));
		FUNCTIONS.add(new AgilityModifierFunction(StatEnum.EVASION, 0.3f));
	}
	
	public static final List<IStatFunction> getFunctions() {
		return FUNCTIONS;
	}
	
	public static final void addPredefinedStatFunctions(Player player) {
		player.getGameStats().addEffectOnly(null, FUNCTIONS);
	}
}