package com.extron.network.api.party;

public enum PartyMemberRole {
	MEMBER(0,"a Member","You must be in a party to use this command!"), MANAGER(1,"a Manager","You must be a Manager or the Leader in your party to use this command!"), LEADER(2,"The Leader","You must be the Leader of your party to use this command!");

    private String niceName;
    private int pos;
    private String permMsg;

    PartyMemberRole(int pos, String s, String msg) {
        this.pos = pos;
        this.niceName = s;
        this.permMsg = msg;
    }

    public int getPos() {
        return pos;
    }

    public String getNiceName() {
        return niceName;
    }

    public String getPermissionMessage() {
        return permMsg;
    }
}
