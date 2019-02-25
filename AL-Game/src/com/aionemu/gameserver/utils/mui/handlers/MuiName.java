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
package com.aionemu.gameserver.utils.mui.handlers;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Steve
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.TYPE})
public @interface MuiName {

    public abstract String value();

}