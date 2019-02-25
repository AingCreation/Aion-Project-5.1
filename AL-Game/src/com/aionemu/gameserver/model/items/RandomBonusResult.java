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
package com.aionemu.gameserver.model.items;

import com.aionemu.gameserver.model.stats.calc.functions.StatFunction;
import com.aionemu.gameserver.model.templates.stats.ModifiersTemplate;

/**
 * @author Rolandas
 */
public class RandomBonusResult {

	private final ModifiersTemplate template;
	private final int templateNumber;

	public RandomBonusResult(ModifiersTemplate template, int number) {
		this.template = template;
		for (StatFunction function : template.getModifiers()) {
			function.setRandomNumber(number);
		}
		this.templateNumber = number;
	}

	public ModifiersTemplate getTemplate() {
		return template;
	}

	public int getTemplateNumber() {
		return templateNumber;
	}

}
