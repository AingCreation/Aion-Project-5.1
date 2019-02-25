package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.controllers.CreatureController;
import com.aionemu.gameserver.controllers.effect.EffectController;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FORCED_MOVE;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SkillMoveType;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="PulledEffect")
public class PulledEffect
  extends EffectTemplate
{
  public void applyEffect(Effect effect)
  {
    effect.addToEffectedController();
    Creature effected = effect.getEffected();
    effected.setCriticalEffectMulti(0);
    effected.getController().cancelCurrentSkill();
    effect.setIsPhysicalState(true);
    World.getInstance().updatePosition(effected, effect.getTargetX(), effect.getTargetY(), effect.getTargetZ(), effected.getHeading());
    PacketSendUtility.broadcastPacketAndReceive(effected, new SM_FORCED_MOVE(effect.getEffector(), effected.getObjectId().intValue(), effect.getTargetX(), effect.getTargetY(), effect.getTargetZ()));
  }
  
  public void calculate(Effect effect)
  {
    if (effect.getEffected().getEffectController().hasPhysicalStateEffect()) {
      return;
    }
    if (!super.calculate(effect, StatEnum.PULLED_RESISTANCE, null)) {
      return;
    }
    effect.setSkillMoveType(SkillMoveType.PULL);
    Creature effector = effect.getEffector();
    
    double radian = Math.toRadians(MathUtil.convertHeadingToDegree(effector.getHeading()));
    float x1 = (float)Math.cos(radian);
    float y1 = (float)Math.sin(radian);
    effect.setTargetLoc(effector.getX() + x1, effector.getY() + y1, effector.getZ() + 0.25F);
  }
  
  public void startEffect(Effect effect)
  {
    Creature effected = effect.getEffected();
    effected.getEffectController().setAbnormal(AbnormalState.CANNOT_MOVE.getId());
    effect.setAbnormal(AbnormalState.CANNOT_MOVE.getId());
  }
  
  public void endEffect(Effect effect)
  {
    effect.setIsPhysicalState(false);
    effect.getEffected().setCriticalEffectMulti(1);
    effect.getEffected().getEffectController().unsetAbnormal(AbnormalState.CANNOT_MOVE.getId());
  }
}
