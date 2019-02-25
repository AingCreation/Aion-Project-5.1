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

import com.aionemu.gameserver.controllers.observer.AttackCalcObserver;
import com.aionemu.gameserver.controllers.observer.AttackShieldObserver;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.skillengine.model.Effect;

/**
 * @author ATracer modified by Wakizashi, Sippolo, kecimis
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ShieldEffect")
public class ShieldEffect extends EffectTemplate {

	@XmlAttribute(name="hitdelta")
	  protected int hitDelta = 0;
	  @XmlAttribute(name="hitvalue")
	  protected int hitValue = 0;
	  @XmlAttribute(name="percent")
	  protected boolean _percent;
	  @XmlAttribute(name="radius")
	  protected int _radius = 0;
	  @XmlAttribute(name="minradius")
	  protected int minRadius = 0;
	  @XmlAttribute(name="condrace")
	  protected Race _condrace = null;

	@Override
	public void applyEffect(Effect effect) {
		// check for condition race, skillId: 10317,10318
		if (_condrace != null && effect.getEffected().getRace() != _condrace) {
			return;
		}

		effect.addToEffectedController();
	}

	@Override
	public void calculate(Effect effect) {
		effect.addSucessEffect(this);
	}

	@Override
	public void startEffect(final Effect effect) {
		int skillLvl = effect.getSkillLevel();
		int valueWithDelta = value + delta * skillLvl;
		int hitValueWithDelta = hitValue + hitDelta * skillLvl;

		AttackShieldObserver asObserver = new AttackShieldObserver(hitValueWithDelta, valueWithDelta, _percent, effect, hitType, this.getType(), this.hitTypeProb);

		effect.getEffected().getObserveController().addAttackCalcObserver(asObserver);
		effect.setAttackShieldObserver(asObserver, position);
		effect.getEffected().getEffectController().setUnderShield(true);
	}

	@Override
	public void endEffect(Effect effect) {
		AttackCalcObserver acObserver = effect.getAttackShieldObserver(position);
		if (acObserver != null) {
			effect.getEffected().getObserveController().removeAttackCalcObserver(acObserver);
		}
		effect.getEffected().getEffectController().setUnderShield(false);
	}

	/**
	 * shieldType 0: convertHeal 1: reflector 2: normal shield 8: protect
	 *
	 * @return
	 */
	public int getType() {
		return 2;
	}
}
