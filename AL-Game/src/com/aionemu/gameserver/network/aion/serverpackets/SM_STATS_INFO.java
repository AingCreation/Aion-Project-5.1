package com.aionemu.gameserver.network.aion.serverpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.stats.container.PlayerGameStats;
import com.aionemu.gameserver.model.stats.container.PlayerLifeStats;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.model.templates.item.WeaponType;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.utils.gametime.GameTimeManager;

public class SM_STATS_INFO extends AionServerPacket
{
	Logger log = LoggerFactory.getLogger(SM_STATS_INFO.class);
    private Player player;
    private PlayerGameStats pgs;
    private PlayerLifeStats pls;
    private PlayerCommonData pcd;
    public SM_STATS_INFO(Player player) {
        this.player = player;
        this.pcd = player.getCommonData();
        this.pgs = player.getGameStats();
        this.pls = player.getLifeStats();
    }
    @Override
    protected void writeImpl(AionConnection con) {
        writeD(player.getObjectId());
        writeD(GameTimeManager.getGameTime().getTime());
        writeH(pgs.getPower().getCurrent());
        writeH(pgs.getHealth().getCurrent());
        writeH(pgs.getAccuracy().getCurrent());
        writeH(pgs.getAgility().getCurrent());
        writeH(pgs.getKnowledge().getCurrent());
        writeH(pgs.getWill().getCurrent());
        writeH(pgs.getStat(StatEnum.WATER_RESISTANCE, 0).getCurrent());
        writeH(pgs.getStat(StatEnum.WIND_RESISTANCE, 0).getCurrent());
        writeH(pgs.getStat(StatEnum.EARTH_RESISTANCE, 0).getCurrent());
        writeH(pgs.getStat(StatEnum.FIRE_RESISTANCE, 0).getCurrent());
        writeH(pgs.getStat(StatEnum.ELEMENTAL_RESISTANCE_LIGHT, 0).getCurrent());
        writeH(pgs.getStat(StatEnum.ELEMENTAL_RESISTANCE_DARK, 0).getCurrent());
        writeH(player.getLevel());
        writeH(0);
        writeH(0);
        writeH(0);
        writeQ(pcd.getExpNeed());
        writeQ(pcd.getExpRecoverable());
        writeQ(pcd.getExpShown());
        writeD(0);
        writeD(pgs.getMaxHp().getCurrent());
        writeD(pls.getCurrentHp());
        writeD(pgs.getMaxMp().getCurrent());
        writeD(pls.getCurrentMp());
        writeH(pgs.getMaxDp().getCurrent());
        writeH(pcd.getDp());
        writeD(pgs.getFlyTime().getCurrent());
        writeD(pls.getCurrentFp());
        writeH(player.getFlyState());
        writeH(pgs.getMainHandPAttack().getCurrent());
        writeH(pgs.getOffHandPAttack().getCurrent());
        writeH(0);
        writeD(pgs.getPDef().getCurrent());
        if (player.getEquipment().getMainHandWeapon() != null
                && (player.getEquipment().getMainHandWeaponType() == WeaponType.BOOK_2H
                || player.getEquipment().getMainHandWeaponType() == WeaponType.ORB_2H
                || player.getEquipment().getMainHandWeaponType() == WeaponType.GUN_1H
                || player.getEquipment().getMainHandWeaponType() == WeaponType.CANNON_2H
                || player.getEquipment().getMainHandWeaponType() == WeaponType.HARP_2H
                || player.getEquipment().getMainHandWeaponType() == WeaponType.KEYBLADE_2H))
            writeH(pgs.getMainHandMAttack().getCurrent());
        else
            writeH(pgs.getMainHandPAttack().getCurrent());
        if (player.getEquipment().getOffHandWeaponType()!= null
                && (player.getEquipment().getOffHandWeaponType() == WeaponType.BOOK_2H
                || player.getEquipment().getOffHandWeaponType() == WeaponType.ORB_2H
                || player.getEquipment().getOffHandWeaponType() == WeaponType.GUN_1H
                || player.getEquipment().getOffHandWeaponType() == WeaponType.CANNON_2H
                || player.getEquipment().getOffHandWeaponType() == WeaponType.HARP_2H
                || player.getEquipment().getOffHandWeaponType() == WeaponType.KEYBLADE_2H))
            writeH(pgs.getOffHandMAttack().getCurrent());
        else
            writeH(pgs.getOffHandPAttack().getCurrent());
        writeD(pgs.getMDef().getCurrent());
        writeH(pgs.getMResist().getCurrent());
        writeH(pgs.getMainHandMAttack().getCurrent());
        writeF(pgs.getAttackRange().getCurrent() / 1000f);
        writeH(pgs.getAttackSpeed().getCurrent());
        writeH(pgs.getEvasion().getCurrent());
        writeH(pgs.getParry().getCurrent());
        writeH(pgs.getBlock().getCurrent());
        writeH(pgs.getMainHandPCritical().getCurrent());
        writeH(pgs.getOffHandPCritical().getCurrent());
        writeH(pgs.getMainHandPAccuracy().getCurrent());
        writeH(pgs.getOffHandPAccuracy().getCurrent());
        writeH(1);
        writeH(pgs.getMAccuracy().getCurrent());
        writeH(pgs.getMCritical().getCurrent());
        writeH(0);
        writeF(pgs.getReverseStat(StatEnum.BOOST_CASTING_TIME, 1000).getCurrent() / 1000f);
        writeH(0);
        writeH(pgs.getConcentration().getCurrent());
        writeH(pgs.getMBoost().getCurrent());
        writeH(pgs.getMBResist().getCurrent());
        writeH(pgs.getStat(StatEnum.HEAL_BOOST, 0).getCurrent());
        writeH(pgs.getStrikeResist().getCurrent());
        writeH(pgs.getStat(StatEnum.PHYSICAL_CRITICAL_DAMAGE_REDUCE, 0).getCurrent());
        writeH(pgs.getSpellResist().getCurrent());
        writeH(pgs.getStat(StatEnum.MAGICAL_CRITICAL_DAMAGE_REDUCE, 0).getCurrent());
        writeH(0);
        writeD((27 + (player.getInventory().size() * 9)));
        writeD(player.getInventory().size());
        writeD(0);
        writeD(0);
        writeD(pcd.getPlayerClass().getClassId());
        writeD(5);
        writeH(1);
        writeH(0);
        writeQ(pcd.getCurrentReposteEnergy());
        writeQ(pcd.getMaxReposteEnergy());
        writeQ(pcd.getCurrentSalvationPercent());
        writeQ(0);
        writeQ(pcd.getGoldenStarEnergy()); //Golden Star Of Lodi.
        writeQ(pcd.getGrowthEnergy()); //Aura Of Growth.
        writeH(pgs.getStat(StatEnum.HSTR, 0).getCurrent());
        writeH(pgs.getStat(StatEnum.HVIT, 0).getCurrent());
        writeH(pgs.getStat(StatEnum.HDEX, 0).getCurrent());
        writeH(pgs.getStat(StatEnum.HAGI, 0).getCurrent());
        writeH(pgs.getStat(StatEnum.HKNO, 0).getCurrent());
        writeH(pgs.getStat(StatEnum.HWIL, 0).getCurrent());
        writeH(1000);//unk
        writeH(0);
        writeH(pgs.getPower().getBase());
        writeH(pgs.getHealth().getBase());
        writeH(pgs.getAccuracy().getBase());
        writeH(pgs.getAgility().getBase());
        writeH(pgs.getKnowledge().getBase());
        writeH(pgs.getWill().getBase());
        writeH(pgs.getStat(StatEnum.WATER_RESISTANCE, 0).getBase());
        writeH(pgs.getStat(StatEnum.WIND_RESISTANCE, 0).getBase());
        writeH(pgs.getStat(StatEnum.EARTH_RESISTANCE, 0).getBase());
        writeH(pgs.getStat(StatEnum.FIRE_RESISTANCE, 0).getBase());
        writeH(pgs.getStat(StatEnum.ELEMENTAL_RESISTANCE_LIGHT, 0).getBase());
        writeH(pgs.getStat(StatEnum.ELEMENTAL_RESISTANCE_DARK, 0).getBase());
        writeD(pgs.getMaxHp().getBase());
        writeD(pgs.getMaxMp().getBase());
        writeD(pgs.getMaxDp().getBase());
        writeD(pgs.getFlyTime().getBase());
        writeH(pgs.getMainHandPAttack().getBase());
        writeH(pgs.getOffHandPAttack().getBase());
        if (player.getEquipment().getMainHandWeapon() != null
                && (player.getEquipment().getMainHandWeaponType() == WeaponType.BOOK_2H
                || player.getEquipment().getMainHandWeaponType() == WeaponType.ORB_2H
                || player.getEquipment().getMainHandWeaponType() == WeaponType.GUN_1H
                || player.getEquipment().getMainHandWeaponType() == WeaponType.CANNON_2H
                || player.getEquipment().getMainHandWeaponType() == WeaponType.HARP_2H
                || player.getEquipment().getMainHandWeaponType() == WeaponType.KEYBLADE_2H))
            writeD(pgs.getMainHandMAttack().getBase());
        else
            writeD(pgs.getMainHandPAttack().getBase());
        writeD(pgs.getPDef().getBase());
        writeD(pgs.getMDef().getBase());
        writeD(pgs.getMResist().getBase());
        writeF(pgs.getAttackRange().getBase() / 1000f);
        writeH(pgs.getEvasion().getBase());
        writeH(pgs.getParry().getBase());
        writeH(pgs.getBlock().getBase());
        writeH(pgs.getMainHandPCritical().getBase());
        writeH(pgs.getOffHandPCritical().getBase());
        writeH(pgs.getMCritical().getBase());
        writeH(pgs.getStat(StatEnum.BLOCK_PENETRATION, 0).getBase());
        writeH(pgs.getMainHandPAccuracy().getBase());
        writeH(pgs.getOffHandPAccuracy().getBase());
        writeH(1);
        writeH(pgs.getMAccuracy().getBase());
        writeH(pgs.getConcentration().getBase());
        writeH(pgs.getMBoost().getBase());
        writeH(pgs.getMBResist().getBase());
        writeH(pgs.getStat(StatEnum.HEAL_BOOST, 0).getBase());
        writeH(pgs.getStrikeResist().getBase());
        writeH(pgs.getStat(StatEnum.PHYSICAL_CRITICAL_DAMAGE_REDUCE, 0).getBase());
        writeH(pgs.getSpellResist().getBase());
        writeH(pgs.getStat(StatEnum.MAGICAL_CRITICAL_DAMAGE_REDUCE, 0).getBase());
        writeH(0);
    }
}