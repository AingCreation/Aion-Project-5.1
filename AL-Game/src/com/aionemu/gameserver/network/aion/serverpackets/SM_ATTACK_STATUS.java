package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class SM_ATTACK_STATUS extends AionServerPacket
{
	private Creature creature;
	private Creature attacker;
	private TYPE type;
	private int skillId;
	private int value;
	private int logId;
	
	public static enum TYPE {
		NATURAL_HP(3),
		USED_HP(4),
		REGULAR(5),
		ABSORBED_HP(6),
		DAMAGE(7),
		HP(7),
		PROTECTDMG(8),
		DELAYDAMAGE(10),
		FALL_DAMAGE(17),
		HEAL_MP(19),
		ABSORBED_MP(20),
		MP(21),
		NATURAL_MP(22),
		ATTACK(23),
		FP_RINGS(24),
		FP(25),
		NATURAL_FP(26),
		AUTO_HEAL_FP(27);
		
		private int value;
		
		private TYPE(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return this.value;
		}
	}
	
	public static enum LOG {
		SPELLATK(1),
        HEAL(3),
        MPHEAL(4),
        SKILLLATKDRAININSTANT(23),
        SPELLATKDRAININSTANT(24),
        POISON(25),
        BLEED(26),
        PROCATKINSTANT(92),
        DELAYEDSPELLATKINSTANT(95),
        SPELLATKDRAIN(130),
        FPHEAL(133),
        REGULARHEAL(170),
        REGULAR(189),
        ATTACK(190);//old 190 new 193
		
		private int value;
		
		private LOG(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return this.value;
		}
	}
	
	public SM_ATTACK_STATUS(Creature creature, Creature attacker, TYPE type, int skillId, int value, LOG log) {
		this.creature = creature;
		this.attacker = attacker;
		this.type = type;
		this.skillId = skillId;
		this.value = value;
		this.logId = log.getValue();
	}

	public SM_ATTACK_STATUS(Creature creature, Creature attacker, TYPE type, int skillId, int value) {
		this(creature, attacker, type, skillId, value, LOG.REGULAR);
	}

	public SM_ATTACK_STATUS(Creature creature, Creature attacker, int value) {
		this(creature, attacker, TYPE.REGULAR, 0, value, LOG.REGULAR);
	}
	
	@Override
	protected void writeImpl(AionConnection con) {
		writeD(creature.getObjectId());
	    writeD(attacker.getObjectId());
		switch (type) {
			case ATTACK:
				writeD(-39);
				break;
			case DAMAGE:
			case DELAYDAMAGE:
				writeD(-value);
				break;
			default:
				writeD(value);
		}
		writeC(type.getValue());
		if (type.getValue() == 19 || type.getValue() == 20 || type.getValue() == 21 || type.getValue() == 22) {
			writeC(creature.getLifeStats().getMpPercentage());
		} else {
			writeC(creature.getLifeStats().getHpPercentage());
		}
		writeH(skillId);
		writeH(0);
		if (skillId != 0) {
			writeH(logId);
		} else {
			writeH(LOG.ATTACK.getValue());
		}
	}
}