/*
 * This file is part of aion-lightning <aion-lightning.com>.
 *
 *  aion-lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-lightning.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.stats.calc.functions;

import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.stats.container.StatEnum;

/**
 * @author ATracer
 */
public class StatSetFunction extends StatFunction {

	public StatSetFunction() {
	}

	public StatSetFunction(StatEnum name, int value) {
		super(name, value, false);
	}

	@Override
	public void apply(Stat2 stat) {
		if (isBonus())
			stat.setBonus(getValue());
		else
			stat.setBase(getValue());
	}

	@Override
	public final int getPriority() {
		return isBonus() ? Integer.MAX_VALUE : Integer.MAX_VALUE - 10;
	}

	@Override
	public String toString() {
		return "StatSetFunction [" + super.toString() + "]";
	}

}
