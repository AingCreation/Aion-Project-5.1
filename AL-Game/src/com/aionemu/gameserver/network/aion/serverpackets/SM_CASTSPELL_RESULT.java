package com.aionemu.gameserver.network.aion.serverpackets;

import java.util.List;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.skill.PlayerSkillList;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.skillengine.condition.Condition;
import com.aionemu.gameserver.skillengine.condition.LeftHandCondition;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.skillengine.model.SkillLearnTemplate;

public class SM_CASTSPELL_RESULT extends AionServerPacket
{
	private Creature effector;
	private Creature target;
	private Skill skill;
	private int cooldown;
	private int hitTime;
	private List<Effect> effects;
	private int spellStatus;
	private int dashStatus;
	private int targetType;
	private boolean chainSuccess;
	private boolean dualSkill = false;
	
	public SM_CASTSPELL_RESULT(Skill skill, List<Effect> effects, int hitTime, boolean chainSuccess, int spellStatus, int dashStatus) {
		this.skill = skill;
		this.effector = skill.getEffector();
		this.target = skill.getFirstTarget();
		this.effects = effects;
		this.cooldown = effector.getSkillCooldown(skill.getSkillTemplate());
		this.spellStatus = spellStatus;
		this.chainSuccess = chainSuccess;
		this.targetType = 0;
		this.hitTime = hitTime;
		this.dashStatus = dashStatus;
	}
	
	public SM_CASTSPELL_RESULT(Skill skill, List<Effect> effects, int hitTime, boolean chainSuccess, int spellStatus, int dashStatus, int targetType) {
		this(skill, effects, hitTime, chainSuccess, spellStatus, dashStatus);
		this.targetType = targetType;
	}
	
	@Override
	protected void writeImpl(AionConnection con) {
		Player player = con.getActivePlayer();
		if (player == null) {
			return;
		}
		writeD(effector.getObjectId());
		writeC(targetType);
		switch (targetType) {
		    case 0:
		    case 3:
		    case 4:
			    writeD(target.getObjectId());
		    break;
		    case 1:
			    writeF(skill.getX());
			    writeF(skill.getY());
			    writeF(skill.getZ());
		    break;
		    case 2:
			    writeF(skill.getX());
			    writeF(skill.getY());
			    writeF(skill.getZ());
			    writeF(0);
			    writeF(0);
			    writeF(0);
			    writeF(0);
			    writeF(0);
			    writeF(0);
			    writeF(0);
			    writeF(0);
		    break;
		}
		writeH(skill.getSkillTemplate().getSkillId());
		writeC(skill.getSkillTemplate().getLvl());
		
		int skillEnchantLvl = 0;
	    int level = player.getLevel();
		int reduceDelay = skill.getSkillTemplate().getDelayTimeLv();
		for (int i = 0; i <= level; i++) {
			SkillLearnTemplate[] skilltemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
			PlayerSkillList playerSkillList = player.getSkillList();
			for (SkillLearnTemplate template : skilltemplates) {
				if (template.getSkillGroup() != null) {
					if (template.getSkillGroup().equals(skill.getSkillTemplate().getSkillGroup())) {
						skillEnchantLvl = playerSkillList.getSkillLevel(template.getSkillId());
					}
				}
			}
		} if (player.getSkillList().isSkillPresent(skill.getSkillTemplate().getSkillId())) {
			if (player.getSkillList().getSkillLevel(skill.getSkillTemplate().getSkillId()) > 1) {
				int cooldownReduce = cooldown + reduceDelay * skillEnchantLvl;
				
				writeD(cooldownReduce);
				writeH(hitTime);
			} else {
				int cooldownReduce = cooldown + reduceDelay * skillEnchantLvl;
				writeD(cooldownReduce);
				writeH(hitTime);
			}
		} else {
			int cooldownReduce = cooldown + reduceDelay * skillEnchantLvl;
			writeD(cooldownReduce);
			writeH(hitTime);
		}
		
		writeC(0);
		
		if (effects.isEmpty()) {
			writeH(16);
		} else if (chainSuccess && skill.getChainCategory() != null) {
			writeH(32);
        } else if (skill.getChainCategory() == null) {
        	writeH(160);
		} else {
			writeH(0);
		}
		
		writeC(dashStatus);
		switch (dashStatus) {
			case 0:
				break;
		    case 1:
            case 2:
            case 3:
            case 4:
            case 6:
			    writeC(skill.getH());
			    writeF(skill.getX());
			    writeF(skill.getY());
			    writeF(skill.getZ());
			    break;
            default:
            	break;
		}
		writeH(effects.size());
		for (Effect effect : effects) {
			Creature effected = effect.getEffected();
			if (effected != null) {
				writeD(effected.getObjectId());
				writeC(effect.getEffectResult().getId());
				writeC((int) (100f * effected.getLifeStats().getCurrentHp() / target.getLifeStats().getMaxHp()));
			} else {
				writeD(0);
				writeC(0);
				writeC(0);
            }
			writeC((int) (100f * effector.getLifeStats().getCurrentHp() / effector.getLifeStats().getMaxHp()));
			
			writeC(this.spellStatus);
			writeC(effect.getSkillMoveType().getId());
			writeH(0);
			writeC(effect.getCarvedSignet());
			switch (this.spellStatus) {
			    case 1:
			    case 2:
			    case 4:
			    case 8:
				    writeF(effect.getTargetX());
				    writeF(effect.getTargetY());
				    writeF(effect.getTargetZ());
				break;
			    case 16:
                case 3:
				    writeC(effect.getEffector().getHeading());
			    break;
                case 5: 
                case 6: 
                case 7: 
                case 9: 
                case 10: 
                case 11: 
                case 12: 
                case 13: 
                case 14: 
                case 15:
			    default:
				switch (effect.getSkillMoveType()) {
				    case PULL:
				    case KNOCKBACK:
					    writeF(effect.getTargetX());
					    writeF(effect.getTargetY());
					    writeF(effect.getTargetZ());
				    default:
					break;
				}
				break;
			}
			if (effect.getSkillTemplate().getUseEquipmentconditions() != null) {
				if (effect.getSkillTemplate().getUseEquipmentconditions().getConditions() != null) {
					for (Condition condition : effect.getSkillTemplate().getUseEquipmentconditions().getConditions()) {
						if (condition instanceof LeftHandCondition) {
							dualSkill = true;
							writeC(2);
						}
					}
				}
			} else {
				writeC(1);
			}
			
			writeC(effect.isMphealInstant() ? 1 : 0);
			if (effect.isDelayedDamage()) {
				writeD(0);
			} else {
				writeD(effect.getReserved1());
			}
			
			writeC(effect.getAttackStatus().getId());
			
			if (effect.getEffected() instanceof Player) {
				if (effect.getAttackStatus().isCounterSkill()) {
					((Player) effect.getEffected()).setLastCounterSkill(effect.getAttackStatus());
				}
			}
			
			writeC(effect.getShieldDefense());
			
			if (dualSkill) {
				writeC(1);
				writeD(0);
				writeC(10);
				writeC(0);
				dualSkill = false;
			}
				
			switch (effect.getShieldDefense()) {
				case 0:
				case 2:
					break;
				case 8:
				case 10:
					writeD(effect.getProtectorId());
					writeD(effect.getProtectedDamage());
					writeD(effect.getProtectedSkillId());
					break;
				case 16:
					writeD(0);
					writeD(0);
					writeD(0);
					writeD(0);
					writeD(0);
					writeD(effect.getMpShield());
					writeD(effect.getReflectedSkillId());
					break;
				case 32: 
					writeD(effect.getProtectorId());
					writeD(effect.getProtectedDamage());
					writeD(effect.getProtectedSkillId());
					writeD(effect.getReflectedDamage());
					writeD(effect.getReflectedSkillId());
					writeD(0);
					writeD(0);
					break;
				default:
					writeD(effect.getProtectorId());
					writeD(effect.getProtectedDamage());
					writeD(effect.getProtectedSkillId());
					writeD(effect.getReflectedDamage());
					writeD(effect.getReflectedSkillId());
					writeD(0);
					writeD(0);
					break;
			}	
		}
	}
}