package com.extron.network.islands;

import com.extron.network.api.players.ExtronPlayer;

import java.util.ArrayList;
import java.util.List;

public class Island {

    private List<ExtronPlayer> members;
    private ExtronPlayer leader;

    public Island(ExtronPlayer leader) {
        this.leader = leader;
        this.members = new ArrayList<>(5);
        this.members.add(leader);
    }

    public ExtronPlayer getLeader() {
        return leader;
    }

    public List<ExtronPlayer> getMembers() {
        return members;
    }


}
