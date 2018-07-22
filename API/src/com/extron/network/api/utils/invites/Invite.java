package com.extron.network.api.utils.invites;

import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.tasks.ExtronRunnable;

public abstract class Invite implements ExtronRunnable {

    private final InviteHelper helper;
    private ExtronPlayer sender;
    private ExtronPlayer invited;

    public Invite(InviteHelper helper, ExtronPlayer sender, ExtronPlayer invited) {
        this.helper = helper;
        this.sender = sender;
        this.invited = invited;
    }

    private int timer;

    // in seconds
    public abstract int getExpirationDelay();

    @Override
    public void run() {
        timer++;
        if (timer >= getExpirationDelay()) {
            this.cancel();
            this.helper.expiredInvite(this);
        }
    }

    public void startTimer() {
        this.timer(20,20);
    }

    public ExtronPlayer getSender() {
        return sender;
    }

    public ExtronPlayer getInvited() {
        return invited;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Invite && ((Invite) obj).helper == helper && ((Invite) obj).invited.equals(invited) && ((Invite) obj).sender.equals(sender);
    }
}
