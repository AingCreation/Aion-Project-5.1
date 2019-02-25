package com.aionemu.gameserver.model.stats.calc.functions;

import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.stats.container.StatEnum;

class PhysicalAttackFunction extends StatFunction {

	PhysicalAttackFunction() {
		stat = StatEnum.PHYSICAL_ATTACK;
	}

	@Override
	public void apply(Stat2 stat) {
		float power = stat.getOwner().getGameStats().getPower().getCurrent();
		stat.setBase(Math.round(stat.getBase() * power / 100f));
	}

	@Override
	public int getPriority() {
		return 30;
	}
}
