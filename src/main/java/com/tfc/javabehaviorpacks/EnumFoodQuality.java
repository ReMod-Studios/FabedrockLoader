package com.tfc.javabehaviorpacks;

public enum EnumFoodQuality {
	POOR("poor", 1),
	LOW("low", 2),
	NORMAL("normal", 3),
	GOOD("normal", 4),
	MAX("max", 5),
	SUPERNATURAL("supernatural", 6);
	
	private final String name;
	private final int val;
	
	EnumFoodQuality(String name, int val) {
		this.name = name;
		this.val = val;
	}
	
	public static EnumFoodQuality forName(String name) {
		for (EnumFoodQuality quality : values()) {
			if (quality.getName().equals(name)) {
				return quality;
			}
		}
		return null;
	}
	
	public float getVal() {
		return val / 5f;
	}
	
	public String getName() {
		return name;
	}
}
