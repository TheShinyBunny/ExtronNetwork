package com.extron.network.api.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ExtronEvent extends Event {

	private static final HandlerList HANDLER_LIST = new HandlerList();
	private boolean cancelled;

	public void setCancelled(boolean cancel) {
		if (this instanceof Cancellable) {
			this.cancelled = cancel;
		}
	}

	public boolean isCancelled() {
		return this instanceof Cancellable && cancelled;
	}


	@Override
	public HandlerList getHandlers() {
		return HANDLER_LIST;
	}

	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}
}
