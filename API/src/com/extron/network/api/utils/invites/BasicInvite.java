package com.extron.network.api.utils.invites;

import com.extron.network.api.players.ExtronPlayer;

public class BasicInvite extends Invite {
    private int delay;

    public BasicInvite(InviteHelper helper, ExtronPlayer sender, ExtronPlayer invited, int delay) {
        super(helper, sender, invited);
        this.delay = delay;
    }

    @Override
    public int getExpirationDelay() {
        return delay;
    }
}
