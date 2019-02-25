package com.aionemu.gameserver.services.dreamergames;

/**
 * Created by xnemonix on 3/7/2017.
 */
public class WorldMapHigherGP {
    float rateIncrease = 10.0f;
    float rateIncrease2 = 10.0f;
    float rateIncrease3 = 10.0f;
    float rateIncrease4 = 13.0f;
    float rateIncrease5 = 0.0f;
    int mapId = 0;
    int mapId2 = 0;
    int mapId3 = 0;
    int mapId4 = 0;
    int mapId5 = 0;

    public static WorldMapHigherGP getInstance() {
        return WorldMapHigherGP.WorldMapHigherGPHolder.INSTANCE;
    }

    public int getMapId() {
        return mapId;
    }

    public void setMapId(int mapId) {
        this.mapId = mapId;
    }

    public float getRateIncrease() {
        return rateIncrease;
    }

    public void setRateIncrease(float rateIncrease) {
        this.rateIncrease = rateIncrease;
    }

    public int getMapId2() {
        return mapId2;
    }

    public void setMapId2(int mapId2) {
        this.mapId2 = mapId2;
    }

    public float getRateIncrease2() {
        return rateIncrease2;
    }

    public void setRateIncrease2(float rateIncrease2) {
        this.rateIncrease2 = rateIncrease2;
    }

    public int getMapId3() {
        return mapId3;
    }

    public void setMapId3(int mapId3) {
        this.mapId3 = mapId3;
    }

    public float getRateIncrease3() {
        return rateIncrease3;
    }

    public void setRateIncrease3(float rateIncrease3) {
        this.rateIncrease3 = rateIncrease3;
    }

    public int getMapId4() {
        return mapId4;
    }

    public void setMapId4(int mapId4) {
        this.mapId4 = mapId4;
    }

    public float getRateIncrease4() {
        return rateIncrease4;
    }

    public void setRateIncrease4(float rateIncrease4) {
        this.rateIncrease4 = rateIncrease4;
    }

    public int getMapId5() {
        return mapId5;
    }

    public void setMapId5(int mapId5) {
        this.mapId5 = mapId5;
    }

    public float getRateIncrease5() {
        return rateIncrease5;
    }

    public void setRateIncrease5(float rateIncrease5) {
        this.rateIncrease5 = rateIncrease5;
    }

    public void initWorldMapHigherGP() {

    }

    private static class WorldMapHigherGPHolder {
        private static final WorldMapHigherGP INSTANCE = new WorldMapHigherGP();
    }
}