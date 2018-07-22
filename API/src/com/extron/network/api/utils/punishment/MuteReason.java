package com.extron.network.api.utils.punishment;

public enum MuteReason implements PunishReason {

    SPAM("Spamming"), LANGUAGE("Offensive Language"), ADVERTISE("Advertising"), NO_REASON("NO REASON!");

    private String name;

    MuteReason(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public PunishType getType() {
        return PunishType.MUTE;
    }

}
