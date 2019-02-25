package com.aionemu.gameserver.model.stats.calc.functions;

import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.stats.container.StatEnum;

class PDefFunction extends StatFunction {

	PDefFunction() {
		stat = StatEnum.PHYSICAL_DEFENSE;
	}

	@Override
	public void apply(Stat2 stat) {
		if (stat.getOwner().isInFlyingState()) {
			stat.setBonus(stat.getBonus() - (stat.getBase() / 2));
		}
	}

	@Override
	public int getPriority() {
		return 60;
	}
}
