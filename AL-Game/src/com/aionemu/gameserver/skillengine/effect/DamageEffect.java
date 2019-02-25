/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 *
 *  Aion-Lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Aion-Lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details. *
 *  You should have received a copy of the GNU General Public License
 *  along with Aion-Lightning.
 *  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.skillengine.effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.controllers.attack.AttackUtil;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.action.DamageType;
import com.aionemu.gameserver.skillengine.change.Func;
import com.aionemu.gameserver.skillengine.effect.modifier.ActionModifier;
import com.aionemu.gameserver.skillengine.model.Effect;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DamageEffect")
public abstract class DamageEffect extends EffectTemplate {

	@XmlAttribute
	protected Func mode = Func.ADD;
	@XmlAttribute
	protected boolean shared;

	@Override
	public void applyEffect(Effect effect) {
		effect.getEffected().getController().onAttack(effect.getEffector(), effect.getSkillId(), effect.getReserved1(), true);
		effect.getEffector().getObserveController().notifyAttackObservers(effect.getEffected());
	}

	public boolean calculate(Effect effect, DamageType damageType) {
		if (!super.calculate(effect, null, null)) {
			return false;
		}

		int skillLvl = effect.getSkillLevel();
		int valueWithDelta = value + delta * skillLvl;
		if (this instanceof SpellAttackInstantEffect || this instanceof SpellAtkDrainInstantEffect) {
			if (effect.getEffector() instanceof Player) {
				if (((Player) effect.getEffector()).getPlayerClass() == PlayerClass.SORCERER) {
					valueWithDelta = (int) (valueWithDelta + valueWithDelta * 0.2);
				} else if (((Player)effect.getEffector()).getPlayerClass() == PlayerClass.SPIRIT_MASTER) {
					valueWithDelta = (int)(valueWithDelta + valueWithDelta * 0.15);
				}
			}
		}
		ActionModifier modifier = getActionModifiers(effect);
		int accMod = this.accMod2 + this.accMod1 * skillLvl;
		int critAddDmg = this.critAddDmg2 + this.critAddDmg1 * skillLvl;
		if (this.value <= 0 && getEffectType() == EffectType.DASH && effect.getSkillTemplate().getEffectTemplate(2) != null) {
			int value2 = effect.getSkillTemplate().getEffectTemplate(2).value;
			int delta2 = effect.getSkillTemplate().getEffectTemplate(2).delta;
			int skillvl2 = effect.getSkillTemplate().getLvl();
			int skillenc = effect.getSkillLevel();
			valueWithDelta = value2 + delta2 * (skillvl2 + skillenc);
			if (effect.getSkillTemplate().getEffectTemplate(2).getEffectType() == EffectType.SPELLATTACKINSTANT) {
				damageType = DamageType.MAGICAL;
			}
		} if (getEffectType() == EffectType.SPELLATTACKINSTANT && effect.getSkillTemplate().getEffectTemplate(2) != null && effect.getSkillTemplate().getEffectTemplate(2).getEffectType() == EffectType.SPELLATTACKINSTANT) {
			damageType = DamageType.MAGICAL;
			int value2 = effect.getSkillTemplate().getEffectTemplate(2).value;
			int valueWithDelta2 = value2 + effect.getSkillLevel() * this.enchantvalue;
			valueWithDelta += valueWithDelta2;
		}
		
		switch (damageType) {
			case PHYSICAL:
				boolean cannotMiss = false;
				if (this instanceof SkillAttackInstantEffect) {
					cannotMiss = ((SkillAttackInstantEffect) this).isCannotmiss();
				}
				int rndDmg = (this instanceof SkillAttackInstantEffect ? ((SkillAttackInstantEffect) this).getRnddmg() : 0);
				AttackUtil.calculateSkillResult(effect, valueWithDelta, modifier, this.getMode(), rndDmg, accMod, this.critProbMod2, critAddDmg, cannotMiss, shared, false, false);
				break;
			case MAGICAL:
				boolean useKnowledge = true;
				if (this instanceof SpellAttackInstantEffect) {
					useKnowledge = true;
					AttackUtil.calculateMagicalSkillResult(effect, valueWithDelta, modifier, getElement(), true, useKnowledge, false, this.getMode(), this.critProbMod2, critAddDmg, shared, false);
				} else if (this instanceof ProcAtkInstantEffect) {
					useKnowledge = false;
					AttackUtil.calculateMagicalSkillResult(effect, valueWithDelta, modifier, getElement(), true, useKnowledge, false, this.getMode(), this.critProbMod2, critAddDmg, shared, false);
				} else {
					useKnowledge = true;
					AttackUtil.calculateMagicalSkillResult(effect, valueWithDelta, modifier, getElement(), true, useKnowledge, false, this.getMode(), this.critProbMod2, critAddDmg, shared, false);
				}
				break;
			default:
				AttackUtil.calculateSkillResult(effect, 0, null, this.getMode(), 0, accMod, 100, 0, false, shared, false, false);
		}

		return true;
	}

	public Func getMode() {
		return mode;
	}
}
