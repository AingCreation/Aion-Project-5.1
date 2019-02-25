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
package com.aionemu.gameserver.skillengine.periodicaction;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.stats.container.CreatureGameStats;
import com.aionemu.gameserver.model.stats.container.CreatureLifeStats;
import com.aionemu.gameserver.skillengine.model.Effect;
import javax.xml.bind.annotation.XmlAttribute;

public class MpUsePeriodicAction
  extends PeriodicAction
{
  @XmlAttribute(name="value")
  protected int value;
  @XmlAttribute(name="ratio")
  protected boolean ratio;
  @XmlAttribute(name="percent")
  protected boolean percent;
  
  public void act(Effect effect)
  {
    Creature effected = effect.getEffected();
    int requiredMp = this.value;
    if (effected.getLifeStats().getCurrentMp() < requiredMp)
    {
      effect.endEffect();
      return;
    }
    if ((this.ratio) || (this.percent))
    {
      int maxMp = effected.getGameStats().getMaxMp().getCurrent();
      requiredMp = (int)(maxMp * (this.value / 100.0F));
    }
    effected.getLifeStats().reduceMp(requiredMp);
  }
}