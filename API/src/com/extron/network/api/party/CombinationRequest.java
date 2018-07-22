package com.extron.network.api.party;

import com.extron.network.api.utils.tasks.ExtronRunnable;

public class CombinationRequest implements ExtronRunnable {


    private Party sender;
    private Party receiver;
    private CombinationType type;
    private int timer = 0;

    public CombinationRequest(Party sender, Party receiver, CombinationType type) {
        this.sender = sender;
        this.receiver = receiver;
        this.type = type;
    }

    public CombinationType getType() {
        return type;
    }

    public Party getReceiver() {
        return receiver;
    }

    public Party getSender() {
        return sender;
    }

    @Override
    public void run() {
        timer++;
        if (timer>=120) {
            this.cancel();
            sender.expiredCombine(this);
        }
    }
}
