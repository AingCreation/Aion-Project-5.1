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
package com.aionemu.gameserver.services;

import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.mui.MuiEngine;
import com.aionemu.gameserver.utils.mui.handlers.MuiHandler;

/**
 * @author Steve
 */
public class MuiService {

    private MuiHandler handler;

    public static MuiService getInstance() {
        return SingletonHolder.instance;
    }

    public void load() {
        handler = MuiEngine.getInstance().getNewMuiHandler(GSConfig.SERVER_LANGUAGE);
    }

    public String getNonUTFMessage(String name, Object... params) {
        return handler.getMessage(name, params);
    }

    public String getMessage(String name, Object... params) {
        return convertFromUTF8(handler.getMessage(name, params));
    }

    public void sendNonUTFMessage(Player player, String message) {
        PacketSendUtility.sendMessage(player, message);
    }

    public void sendMessage(Player player, String name, Object... params) {
        PacketSendUtility.sendMessage(player, convertFromUTF8(handler.getMessage(name, params)));
    }

    public String convertFromUTF8(String s) {
        String out;
        try {
            byte[] bytes = s.getBytes();
            for (int i = 0; i < bytes.length - 1; i++) {
                if (bytes[i] == -48 && bytes[i + 1] == 63) {
                    bytes[i] = (byte) 208;
                    bytes[i + 1] = (byte) 152;
                }
            }
            out = new String(bytes, "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            return null;
        }
        return out;
    }

    public String convertFromGB2312(String s) {
        String out;
        try {
            byte[] bytes = s.getBytes();
            for (int i = 0; i < bytes.length - 1; i++) {
                if (bytes[i] == -48 && bytes[i + 1] == 63) {
                    bytes[i] = (byte) 208;
                    bytes[i + 1] = (byte) 152;
                }
            }
            out = new String(bytes, "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            return null;
        }
        return out;
    }

    public String convertFromCP866(String s) {
        String out;
        try {
            out = new String(s.getBytes("Cp866"), "Cp1251");
        } catch (java.io.UnsupportedEncodingException e) {
            return null;
        }
        return out;
    }

    @SuppressWarnings("synthetic-access")
    private static class SingletonHolder {

        protected static final MuiService instance = new MuiService();
    }
}