package com.extron.network.api.utils.punishment;

public enum PunishType {

    BAN, MUTE;

    public PunishReason getReason(String reason) {
        return this == BAN ? BanReason.valueOf(reason) : MuteReason.valueOf(reason);
    }

    public PunishReason getNoReason() {
        return this == BAN ? BanReason.NO_REASON : MuteReason.NO_REASON;
    }

    public String getId() {
        return this.toString().toLowerCase();
    }
}
