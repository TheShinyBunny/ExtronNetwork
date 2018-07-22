package com.extron.network.api.party;

import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.tasks.ExtronRunnable;

public class PartyInvite implements ExtronRunnable {

	private Party party;
	private ExtronPlayer sender;
	private ExtronPlayer invited;

	public PartyInvite(Party party, ExtronPlayer sender, ExtronPlayer invited) {
		this.party = party;
		this.sender = sender;
		this.invited = invited;
	}
	
	private int i = 0;
	
	@Override
	public void run() {
		i++;
		if (i>=60) {
			this.cancel();
			party.expiredInvite(this);
		}
	}
	
	public ExtronPlayer getInvited() {
		return invited;
	}
	
	public ExtronPlayer getSender() {
		return sender;
	}

	public Party getParty() {
		return party;
	}
}
