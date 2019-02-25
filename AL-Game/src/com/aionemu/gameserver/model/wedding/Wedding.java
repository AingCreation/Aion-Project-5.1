package com.aionemu.gameserver.model.wedding;

import com.aionemu.gameserver.model.gameobjects.player.Player;

import java.sql.Timestamp;

/**
 * @author Ione542
 */
public class Wedding {

    private final int playerId;
    private final boolean partner;
    private int partnerId;
    private Timestamp lastOnline;
    private Timestamp timeTeleport;
    private Timestamp timeWedding;
    private int worldId;
    private String text;
    private Timestamp dataWedding;
    private String partnerText;
    private String partnerName;
    private boolean update = false;
    private Player p;

    public Wedding(int player_id, int partner_id, Timestamp lastOnline, int lastWorldId, Timestamp timeTeleport, Timestamp timeWedding, String text, String partnerText, Timestamp dataWedding, String partnerName, boolean partner) {
        this.playerId = player_id;
        this.partnerId = partner_id;
        this.lastOnline = lastOnline;
        this.worldId = lastWorldId;
        this.timeTeleport = timeTeleport;
        this.timeWedding = timeWedding;
        this.text = text;
        this.partnerText = partnerText;
        this.dataWedding = dataWedding;
        this.partnerName = partnerName;
        this.partner = partner;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getPartnerWorldId() {
        return worldId;
    }

    public void setPartnerWorldId(int id) {
        this.worldId = id;
    }

    public Timestamp getPartnerLastOnline() {
        return this.lastOnline;
    }

    public void setPartnerLastOnline(Timestamp time) {
        this.lastOnline = time;
    }

    public Timestamp getTimeTp() {
        return timeTeleport;
    }

    public void setTimeTp(Timestamp time) {
        this.timeTeleport = time;
        this.update = true;
    }

    public Timestamp getTimeWedding() {
        return timeWedding;
    }

    public void setTimeWedding(Timestamp time) {
        this.timeWedding = time;
        this.update = true;
    }

    public int getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(int partnerId) {
        this.partnerId = partnerId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        this.update = true;
    }

    public Timestamp getDataWedding() {
        return dataWedding;
    }

    public void setDataWedding(Timestamp dw) {
        this.dataWedding = dw;
    }

    public String getPartnerText() {
        return this.partnerText;
    }

    public void setPartnerText(String t) {
        this.partnerText = t;
        this.update = true;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String name) {
        this.partnerName = name;
    }

    public boolean isUpdate() {
        return update;
    }

    public boolean isPartner() {
        return partner;
    }

    public boolean isOnline() {
        return p != null;
    }

    public Player getPartner() {
        return p;
    }

    public void setPartner(Player player) {
        this.p = player;
    }
}
