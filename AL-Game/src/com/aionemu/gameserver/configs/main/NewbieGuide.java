/*
 * =====================================================================================*
 * This file is part of Archsoft (Archsoft Home Software Development)                   *
 * Aion - Archsoft Development is closed Aion Project that use Old Aion Project Base    *
 * Like Aion-Unique, Aion-Lightning, Aion-Engine, Aion-Core, Aion-Extreme,              *
 * Aion-NextGen, Aion-Ger, U3J, Encom And other Aion project, All Credit Content        *
 * That they make is belong to them/Copyright is belong to them. And All new Content    *
 * that Archsoft make the copyright is belong to Archsoft.                              *
 * You may have agreement with Archsoft Development, before use this Engine/Source      *
 * You have agree with all of Term of Services agreement with Archsoft Development      *
 * =====================================================================================*
 */

package com.aionemu.gameserver.configs.main;

import com.aionemu.commons.configuration.Property;

/**
 * Created by xnemonx on 1/19/2017.
 */
public class NewbieGuide {
    /**
     * Enables custom names usage.
     */
    @Property(key = "gameserver.newbie.guide.enabled", defaultValue = "false")
    public static boolean ENABLED_NEWBIE_GUIDE;

    /**
     * Elyos Spawn location
     */
    @Property(key = "gameserver.newbie.guide.elyos.spawn.world", defaultValue = "302100000")
    public static int NEWBIE_GUIDE_ELYOS_SPAWN_WORLD;

    @Property(key = "gameserver.newbie.guide.elyos.spawn.x", defaultValue = "138.878")
    public static float NEWBIE_GUIDE_ELYOS_SPAWN_X;

    @Property(key = "gameserver.newbie.guide.elyos.spawn.y", defaultValue = "129.552")
    public static float NEWBIE_GUIDE_ELYOS_SPAWN_Y;

    @Property(key = "gameserver.newbie.guide.elyos.spawn.z", defaultValue = "20")
    public static float NEWBIE_GUIDE_ELYOS_SPAWN_Z;

    @Property(key = "gameserver.newbie.guide.elyos.spawn.h", defaultValue = "20")
    public static byte NEWBIE_GUIDE_ELYOS_SPAWN_H;
    /**
     * Asmos Spawn location
     */
    @Property(key = "gameserver.newbie.guide.asmos.spawn.world", defaultValue = "302100000")
    public static int NEWBIE_GUIDE_ASMOS_SPAWN_WORLD;

    @Property(key = "gameserver.newbie.guide.asmos.spawn.x", defaultValue = "138.878")
    public static float NEWBIE_GUIDE_ASMOS_SPAWN_X;

    @Property(key = "gameserver.newbie.guide.asmos.spawn.y", defaultValue = "129.552")
    public static float NEWBIE_GUIDE_ASMOS_SPAWN_Y;

    @Property(key = "gameserver.newbie.guide.asmos.spawn.z", defaultValue = "20")
    public static float NEWBIE_GUIDE_ASMOS_SPAWN_Z;

    @Property(key = "gameserver.newbie.guide.asmos.spawn.h", defaultValue = "20")
    public static byte NEWBIE_GUIDE_ASMOS_SPAWN_H;
    /**
     * Agent NPC ID Elyos
     */
    @Property(key = "gameserver.newbie.guide.elyos.agent", defaultValue = "806076")
    public static int NEWBIE_GUIDE_ELYOS_AGENT;

    @Property(key = "gameserver.newbie.guide.elyos.agent.spawn.x", defaultValue = "138.878")
    public static float NEWBIE_GUIDE_ELYOS_AGENT_SPAWN_X;

    @Property(key = "gameserver.newbie.guide.elyos.agent.spawn.y", defaultValue = "129.552")
    public static float NEWBIE_GUIDE_ELYOS_AGENT_SPAWN_Y;

    @Property(key = "gameserver.newbie.guide.elyos.agent.spawn.z", defaultValue = "20")
    public static float NEWBIE_GUIDE_ELYOS_AGENT_SPAWN_Z;

    @Property(key = "gameserver.newbie.guide.elyos.agent.spawn.h", defaultValue = "20")
    public static byte NEWBIE_GUIDE_ELYOS_AGENT_SPAWN_H;
    /**
     * Agent NPC ID Asmos
     */
    @Property(key = "gameserver.newbie.guide.asmos.agent", defaultValue = "806080")
    public static int NEWBIE_GUIDE_ASMOS_AGENT;

    @Property(key = "gameserver.newbie.guide.asmos.agent.spawn.x", defaultValue = "138.878")
    public static float NEWBIE_GUIDE_ASMOS_AGENT_SPAWN_X;

    @Property(key = "gameserver.newbie.guide.asmos.agent.spawn.y", defaultValue = "129.552")
    public static float NEWBIE_GUIDE_ASMOS_AGENT_SPAWN_Y;

    @Property(key = "gameserver.newbie.guide.asmos.agent.spawn.z", defaultValue = "20")
    public static float NEWBIE_GUIDE_ASMOS_AGENT_SPAWN_Z;

    @Property(key = "gameserver.newbie.guide.asmos.agent.spawn.h", defaultValue = "20")
    public static byte NEWBIE_GUIDE_ASMOS_AGENT_SPAWN_H;

    /**
     * Support Agent NPC ID ELYOS (1)
     */
    @Property(key = "gameserver.newbie.guide.elyos.agent.support", defaultValue = "806076")
    public static int NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT;

    @Property(key = "gameserver.newbie.guide.elyos.agent.support.spawn.x", defaultValue = "138.878")
    public static float NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT_SPAWN_X;

    @Property(key = "gameserver.newbie.guide.elyos.agent.support.spawn.y", defaultValue = "129.552")
    public static float NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT_SPAWN_Y;

    @Property(key = "gameserver.newbie.guide.elyos.agent.support.spawn.z", defaultValue = "20")
    public static float NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT_SPAWN_Z;

    @Property(key = "gameserver.newbie.guide.elyos.agent.support.spawn.h", defaultValue = "20")
    public static byte NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT_SPAWN_H;

    /**
     * Support Agent NPC ID ELYOS (2)
     */
    @Property(key = "gameserver.newbie.guide.elyos.agent.support2", defaultValue = "806076")
    public static int NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT2;

    @Property(key = "gameserver.newbie.guide.elyos.agent.support2.spawn.x", defaultValue = "138.878")
    public static float NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT2_SPAWN_X;

    @Property(key = "gameserver.newbie.guide.elyos.agent.support2.spawn.y", defaultValue = "129.552")
    public static float NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT2_SPAWN_Y;

    @Property(key = "gameserver.newbie.guide.elyos.agent.support2.spawn.z", defaultValue = "20")
    public static float NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT2_SPAWN_Z;

    @Property(key = "gameserver.newbie.guide.elyos.agent.support2.spawn.h", defaultValue = "20")
    public static byte NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT2_SPAWN_H;
    /**
     * Support Agent NPC ID ELYOS (3)
     */
    @Property(key = "gameserver.newbie.guide.elyos.agent.support3", defaultValue = "806076")
    public static int NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT3;

    @Property(key = "gameserver.newbie.guide.elyos.agent.support3.spawn.x", defaultValue = "138.878")
    public static float NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT3_SPAWN_X;

    @Property(key = "gameserver.newbie.guide.elyos.agent.support3.spawn.y", defaultValue = "129.552")
    public static float NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT3_SPAWN_Y;

    @Property(key = "gameserver.newbie.guide.elyos.agent.support3.spawn.z", defaultValue = "20")
    public static float NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT3_SPAWN_Z;

    @Property(key = "gameserver.newbie.guide.elyos.agent.support3.spawn.h", defaultValue = "20")
    public static byte NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT3_SPAWN_H;
    /**
     * Support Agent NPC ID ELYOS (4)
     */
    @Property(key = "gameserver.newbie.guide.elyos.agent.support4", defaultValue = "806076")
    public static int NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT4;

    @Property(key = "gameserver.newbie.guide.elyos.agent.support4.spawn.x", defaultValue = "138.878")
    public static float NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT4_SPAWN_X;

    @Property(key = "gameserver.newbie.guide.elyos.agent.support4.spawn.y", defaultValue = "129.552")
    public static float NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT4_SPAWN_Y;

    @Property(key = "gameserver.newbie.guide.elyos.agent.support4.spawn.z", defaultValue = "20")
    public static float NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT4_SPAWN_Z;

    @Property(key = "gameserver.newbie.guide.elyos.agent.support4.spawn.h", defaultValue = "20")
    public static byte NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT4_SPAWN_H;
    /**
     * Support Agent NPC ID ELYOS (5)
     */
    @Property(key = "gameserver.newbie.guide.elyos.agent.support5", defaultValue = "806076")
    public static int NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT5;

    @Property(key = "gameserver.newbie.guide.elyos.agent.support5.spawn.x", defaultValue = "138.878")
    public static float NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT5_SPAWN_X;

    @Property(key = "gameserver.newbie.guide.elyos.agent.support5.spawn.y", defaultValue = "129.552")
    public static float NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT5_SPAWN_Y;

    @Property(key = "gameserver.newbie.guide.elyos.agent.support5.spawn.z", defaultValue = "20")
    public static float NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT5_SPAWN_Z;

    @Property(key = "gameserver.newbie.guide.elyos.agent.support5.spawn.h", defaultValue = "20")
    public static byte NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT5_SPAWN_H;
    /**
     * Support Agent NPC ID ELYOS (6)
     */
    @Property(key = "gameserver.newbie.guide.elyos.agent.support6", defaultValue = "806076")
    public static int NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT6;

    @Property(key = "gameserver.newbie.guide.elyos.agent.support6.spawn.x", defaultValue = "138.878")
    public static float NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT6_SPAWN_X;

    @Property(key = "gameserver.newbie.guide.elyos.agent.support6.spawn.y", defaultValue = "129.552")
    public static float NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT6_SPAWN_Y;

    @Property(key = "gameserver.newbie.guide.elyos.agent.support6.spawn.z", defaultValue = "20")
    public static float NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT6_SPAWN_Z;

    @Property(key = "gameserver.newbie.guide.elyos.agent.support6.spawn.h", defaultValue = "20")
    public static byte NEWBIE_GUIDE_ELYOS_AGENT_SUPPORT6_SPAWN_H;


    /**
     * Support Agent NPC ID Asmos (1)
     */
    @Property(key = "gameserver.newbie.guide.asmos.agent.support", defaultValue = "806076")
    public static int NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT;

    @Property(key = "gameserver.newbie.guide.asmos.agent.support.spawn.x", defaultValue = "138.878")
    public static float NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT_SPAWN_X;

    @Property(key = "gameserver.newbie.guide.asmos.agent.support.spawn.y", defaultValue = "129.552")
    public static float NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT_SPAWN_Y;

    @Property(key = "gameserver.newbie.guide.asmos.agent.support.spawn.z", defaultValue = "20")
    public static float NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT_SPAWN_Z;

    @Property(key = "gameserver.newbie.guide.asmos.agent.support.spawn.h", defaultValue = "20")
    public static byte NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT_SPAWN_H;

    /**
     * Support Agent NPC ID asmos (2)
     */
    @Property(key = "gameserver.newbie.guide.asmos.agent.support2", defaultValue = "806076")
    public static int NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT2;

    @Property(key = "gameserver.newbie.guide.asmos.agent.support2.spawn.x", defaultValue = "138.878")
    public static float NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT2_SPAWN_X;

    @Property(key = "gameserver.newbie.guide.asmos.agent.support2.spawn.y", defaultValue = "129.552")
    public static float NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT2_SPAWN_Y;

    @Property(key = "gameserver.newbie.guide.asmos.agent.support2.spawn.z", defaultValue = "20")
    public static float NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT2_SPAWN_Z;

    @Property(key = "gameserver.newbie.guide.asmos.agent.support2.spawn.h", defaultValue = "20")
    public static byte NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT2_SPAWN_H;
    /**
     * Support Agent NPC ID asmos (3)
     */
    @Property(key = "gameserver.newbie.guide.asmos.agent.support3", defaultValue = "806076")
    public static int NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT3;

    @Property(key = "gameserver.newbie.guide.asmos.agent.support3.spawn.x", defaultValue = "138.878")
    public static float NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT3_SPAWN_X;

    @Property(key = "gameserver.newbie.guide.asmos.agent.support3.spawn.y", defaultValue = "129.552")
    public static float NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT3_SPAWN_Y;

    @Property(key = "gameserver.newbie.guide.asmos.agent.support3.spawn.z", defaultValue = "20")
    public static float NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT3_SPAWN_Z;

    @Property(key = "gameserver.newbie.guide.asmos.agent.support3.spawn.h", defaultValue = "20")
    public static byte NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT3_SPAWN_H;
    /**
     * Support Agent NPC ID asmos (4)
     */
    @Property(key = "gameserver.newbie.guide.asmos.agent.support4", defaultValue = "806076")
    public static int NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT4;

    @Property(key = "gameserver.newbie.guide.asmos.agent.support4.spawn.x", defaultValue = "138.878")
    public static float NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT4_SPAWN_X;

    @Property(key = "gameserver.newbie.guide.asmos.agent.support4.spawn.y", defaultValue = "129.552")
    public static float NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT4_SPAWN_Y;

    @Property(key = "gameserver.newbie.guide.asmos.agent.support4.spawn.z", defaultValue = "20")
    public static float NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT4_SPAWN_Z;

    @Property(key = "gameserver.newbie.guide.asmos.agent.support4.spawn.h", defaultValue = "20")
    public static byte NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT4_SPAWN_H;
    /**
     * Support Agent NPC ID asmos (5)
     */
    @Property(key = "gameserver.newbie.guide.asmos.agent.support5", defaultValue = "806076")
    public static int NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT5;

    @Property(key = "gameserver.newbie.guide.asmos.agent.support5.spawn.x", defaultValue = "138.878")
    public static float NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT5_SPAWN_X;

    @Property(key = "gameserver.newbie.guide.asmos.agent.support5.spawn.y", defaultValue = "129.552")
    public static float NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT5_SPAWN_Y;

    @Property(key = "gameserver.newbie.guide.asmos.agent.support5.spawn.z", defaultValue = "20")
    public static float NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT5_SPAWN_Z;

    @Property(key = "gameserver.newbie.guide.asmos.agent.support5.spawn.h", defaultValue = "20")
    public static byte NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT5_SPAWN_H;
    /**
     * Support Agent NPC ID asmos (6)
     */
    @Property(key = "gameserver.newbie.guide.asmos.agent.support6", defaultValue = "806076")
    public static int NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT6;

    @Property(key = "gameserver.newbie.guide.asmos.agent.support6.spawn.x", defaultValue = "138.878")
    public static float NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT6_SPAWN_X;

    @Property(key = "gameserver.newbie.guide.asmos.agent.support6.spawn.y", defaultValue = "129.552")
    public static float NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT6_SPAWN_Y;

    @Property(key = "gameserver.newbie.guide.asmos.agent.support6.spawn.z", defaultValue = "20")
    public static float NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT6_SPAWN_Z;

    @Property(key = "gameserver.newbie.guide.asmos.agent.support6.spawn.h", defaultValue = "20")
    public static byte NEWBIE_GUIDE_ASMOS_AGENT_SUPPORT6_SPAWN_H;

    /**
     * Starter Pack
     */
    @Property(key = "gameserver.newbie.guide.gladiator.pack", defaultValue = "806076")
    public static String NEWBIE_GUIDE_GLADIATOR;

    @Property(key = "gameserver.newbie.guide.templar.pack", defaultValue = "806076")
    public static String NEWBIE_GUIDE_TEMPLAR;

    @Property(key = "gameserver.newbie.guide.assassin.pack", defaultValue = "806076")
    public static String NEWBIE_GUIDE_ASSASSIN;

    @Property(key = "gameserver.newbie.guide.ranger.pack", defaultValue = "806076")
    public static String NEWBIE_GUIDE_RANGER;

    @Property(key = "gameserver.newbie.guide.gunner.pack", defaultValue = "806076")
    public static String NEWBIE_GUIDE_GUNNER;

    @Property(key = "gameserver.newbie.guide.rider.pack", defaultValue = "806076")
    public static String NEWBIE_GUIDE_RIDER;

    @Property(key = "gameserver.newbie.guide.bard.pack", defaultValue = "806076")
    public static String NEWBIE_GUIDE_BARD;

    @Property(key = "gameserver.newbie.guide.sorcerer.pack", defaultValue = "806076")
    public static String NEWBIE_GUIDE_SORCERER;

    @Property(key = "gameserver.newbie.guide.spiritmaster.pack", defaultValue = "806076")
    public static String NEWBIE_GUIDE_SPIRITMASTER;

    @Property(key = "gameserver.newbie.guide.cleric.pack", defaultValue = "806076")
    public static String NEWBIE_GUIDE_CLERIC;

    @Property(key = "gameserver.newbie.guide.chanter.pack", defaultValue = "806076")
    public static String NEWBIE_GUIDE_CHANTER;

    @Property(key = "gameserver.newbie.guide.kinah", defaultValue = "10000000")
    public static long NEWBIE_GUIDE_KINAH;

    @Property(key = "gameserver.newbie.guide.other.item", defaultValue = "806076")
    public static String NEWBIE_GUIDE_OTHER_ITEM;

    @Property(key = "gameserver.newbie.setlevel.after.changeclass", defaultValue = "65")
    public static int NEWBIE_GUIDE_SET_LEVEL;

}
