package com.aionemu.gameserver.skillengine.model;

public class ChainSkill {

	private String category;
	private int chainCount = 0;
	private long useTime;

	public ChainSkill(String category, int chainCount, long useTime) {
		this.category = category;
		this.chainCount = chainCount;
		this.useTime = useTime;
	}

	public void updateChainSkill(String category) {
		this.category = category;
		this.chainCount = 0;
		this.useTime = System.currentTimeMillis();
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String name) {
		category = name;
	}

	public int getChainCount() {
		return chainCount;
	}

	public void setChainCount(int chainCount) {
		this.chainCount = chainCount;
	}

	public void increaseChainCount() {
		this.chainCount ++;
	}

	public long getUseTime() {
		return useTime;
	}

	public void setUseTime(long useTime) {
		this.useTime = useTime;
	}
}