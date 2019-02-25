package com.aionemu.gameserver.network.aion.serverpackets;

import java.util.List;

import com.aionemu.gameserver.controllers.attack.AttackResult;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class SM_ATTACK extends AionServerPacket
{
	private int attackno;
	private int time;
	private int type;
	private int SimpleAttackType;
	private List<AttackResult> attackList;
	private Creature attacker;
	private Creature target;
	
	public SM_ATTACK(Creature attacker, Creature target, int attackno, int time, int type, List<AttackResult> attackList) {
		this.attacker = attacker;
		this.target = target;
		this.attackno = attackno;
		this.time = time;
		this.type = type;
		this.attackList = attackList;
		this.SimpleAttackType = attacker.getController().getSimpleAttackType();
	}
	
	@Override
	protected void writeImpl(AionConnection con) {
		writeD(attacker.getObjectId());
		writeC(attackno);
		writeH(time);
		writeC((byte) SimpleAttackType);
		writeC(type);
		writeD(target.getObjectId());
		int attackerMaxHp = attacker.getLifeStats().getMaxHp();
		int attackerCurrHp = attacker.getLifeStats().getCurrentHp();
		int targetMaxHp = target.getLifeStats().getMaxHp();
		int targetCurrHp = target.getLifeStats().getCurrentHp();
		writeC((int) (100f * targetCurrHp / targetMaxHp));
		writeC((int) (100f * attackerCurrHp / attackerMaxHp));
		switch (attackList.get(0).getAttackStatus().getId()) {
			case 4: // case BLOCK
			case 5:
			case 196: // case CRITICAL_BLOCK 4.5
			case 213:
				writeD(32);
				break;
			case 2: // case PARRY
			case 3:
			case 194: // case CRITICAL_PARRY 4.5
			case 211:
				writeD(64);
				break;
			case 0: // case DODGE
			case 1:
			case 192: // case CRITICAL_DODGE 4.5
			case 209:
				writeD(128);
				break;
			case 6: // case RESIST
			case 7:
			case 198: // case CRITICAL_RESIST 4.5
			case 215:
				writeD(256); // need more info becuz sometimes 0
				break;
			default:
				writeD(0);
				break;
		} 
		
		if (target instanceof Player) {
			if (attackList.get(0).getAttackStatus().isCounterSkill()) {
				((Player) target).setLastCounterSkill(attackList.get(0).getAttackStatus());
			}
		}
		
		writeC(attackList.size());
		for (AttackResult attack : attackList) {
			writeD(attack.getDamage());
			writeC(attack.getAttackStatus().getId());
			
			byte shieldType = (byte) attack.getShieldType();
			writeC(shieldType);
			switch (shieldType) {
				case 0:
				case 2:
					break;
				case 8:
				case 10:
					writeD(attack.getProtectorId());
					writeD(attack.getProtectedDamage());
					writeD(attack.getProtectedSkillId());
					break;
				case 16:
					writeD(0);
					writeD(0);
					writeD(0);
					writeD(0);
					writeD(0);
					writeD(attack.getShieldMp());
					writeD(attack.getReflectedSkillId());
					break;
				case 32: 
					writeD(0);
					writeD(0);
					writeD(0);
					writeD(0);
					writeD(attack.getReflectedSkillId());
					writeD(0);
					writeD(0);
					break;
				default:
					writeD(attack.getProtectorId());
					writeD(attack.getProtectedDamage());
					writeD(attack.getProtectedSkillId());
					writeD(attack.getReflectedDamage());
					writeD(attack.getReflectedSkillId());
					writeD(0);
					writeD(0);
					break;
				}
			}
		writeC(0);
	}
}