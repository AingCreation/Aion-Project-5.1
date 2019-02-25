package com.aionemu.gameserver.model.stats.calc.functions;

import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.stats.container.StatEnum;

class MaxHpFunction extends StatFunction {

	MaxHpFunction() {
		stat = StatEnum.MAXHP;
	}

	@Override
	public void apply(Stat2 stat) {
		float health = stat.getOwner().getGameStats().getHealth().getCurrent();
		stat.setBase(Math.round(stat.getBase() * health / 100f));
	}

	@Override
	public int getPriority() {
		return 30;
	}
}
