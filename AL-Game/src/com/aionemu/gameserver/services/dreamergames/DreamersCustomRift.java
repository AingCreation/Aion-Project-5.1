package com.aionemu.gameserver.services.dreamergames;


public class DreamersCustomRift {
    int mapEId = 0;
    int mapAId = 0;

    float ElyX;
    float ElyY;
    float ElyZ;
    byte ElyH;

    float AsmX;
    float AsmY;
    float AsmZ;
    byte AsmH;

    public static DreamersCustomRift getInstance() {
        return DreamersCustomRift.ArchsoftCustomRiftHolder.INSTANCE;
    }

    public int getMapAId() {
        return mapAId;
    }

    public void setMapAId(int mapAId) {
        this.mapAId = mapAId;
    }

    public int getMapEId() {
        return mapEId;
    }

    public void setMapEId(int mapEId) {
        this.mapEId = mapEId;
    }

    public float getAsmX() {
        return AsmX;
    }

    public void setAsmX(float AsmX) {
        this.AsmX = AsmX;
    }

    public float getAsmY() {
        return AsmY;
    }

    public void setAsmY(float AsmY) {
        this.AsmY = AsmY;
    }

    public float getAsmZ() {
        return AsmZ;
    }

    public void setAsmZ(float AsmZ) {
        this.AsmZ = AsmZ;
    }

    public byte getAsmH() {
        return AsmH;
    }

    public void setAsmH(byte AsmH) {
        this.AsmH = AsmH;
    }

    public float getElyX() {
        return ElyX;
    }

    public void setElyX(float elyX) {
        this.ElyX = elyX;
    }

    public float getElyY() {
        return ElyY;
    }

    public void setElyY(float elyY) {
        this.ElyY = elyY;
    }

    public float getElyZ() {
        return ElyZ;
    }

    public void setElyZ(float elyZ) {
        this.ElyZ = elyZ;
    }

    public byte getElyH() {
        return ElyH;
    }

    public void setElyH(byte elyH) {
        this.ElyH = elyH;
    }

    private static class ArchsoftCustomRiftHolder {
        private static final DreamersCustomRift INSTANCE = new DreamersCustomRift();
    }

}