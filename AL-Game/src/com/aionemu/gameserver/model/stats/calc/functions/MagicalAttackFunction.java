package com.aionemu.gameserver.model.stats.calc.functions;

import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.stats.container.StatEnum;

class MagicalAttackFunction extends StatFunction {

	MagicalAttackFunction() {
		stat = StatEnum.MAGICAL_ATTACK;
	}

	@Override
	public void apply(Stat2 stat) {
		float knowledge = stat.getOwner().getGameStats().getKnowledge().getCurrent();
		stat.setBase(Math.round(stat.getBase() * knowledge / 100f));
	}

	@Override
	public int getPriority() {
		return 30;
	}
}
