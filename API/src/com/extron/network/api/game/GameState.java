package com.extron.network.api.game;

public enum GameState {
	NONE, WAITING, STARTING, RUNNING, ENDED;
	
	public boolean isBeforeStart() {
		return this == WAITING || this == GameState.STARTING;
	}
	
	public boolean isIdle() {
		return this == NONE;
	}

	public boolean isAfterGame() {
		return this == ENDED || this == NONE;
	}
	
	public boolean isIngame() {
		return this == GameState.RUNNING;
	}
	
	public boolean canPlayersJoin() {
		return this == NONE || this == WAITING;
	}
}
