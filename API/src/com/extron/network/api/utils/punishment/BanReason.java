package com.extron.network.api.utils.punishment;

public enum BanReason implements PunishReason {
	HACKING("Hacking"), LANGUAGE("Offensive Language"), ABUSE("Abuse"), MODS("Blacklisted Mods"), NO_REASON("NO REASON!"), TEAMING("Teaming/Cross Teaming"), BUILD("Inappropriate Build");
	
	private String name;
	
	BanReason(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public PunishType getType() {
		return PunishType.BAN;
	}
}
