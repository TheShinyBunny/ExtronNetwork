package com.extron.commands.examples;

public enum BanReason {

    HACKING("Hacking"), LANGUAGE("Offensive Language"), ABUSE("Abuse"), MODS("Blacklisted Mods"), NO_REASON("NO REASON!"), TEAMING("Teaming/Cross Teaming"), BUILD("Inappropriate Build");

    private String name;

    BanReason(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
