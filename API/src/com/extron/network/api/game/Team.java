package com.extron.network.api.game;

import com.extron.network.api.game.managers.GameManager;
import com.extron.network.api.game.managers.ICompetitiveManager;
import com.extron.network.api.players.ExtronPlayer;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class Team {

	private final ICompetitiveManager manager;
	private List<ExtronPlayer> players;
	private List<ExtronPlayer> alivePlayers;
	private ChatColor color;
	
	private boolean alive;
	private String name;

	public Team(String name, ICompetitiveManager manager) {
		this(name, manager, ChatColor.GREEN);
	}
	
	public Team(String name, ICompetitiveManager manager, ChatColor color) {
		this.name = name;
		this.manager = manager;
		this.players = new ArrayList<>();
		this.alivePlayers = new ArrayList<>();
		this.alive = false;
		this.color = color;
	}

	public void addMember(ExtronPlayer p) {
		players.add(p);
		alivePlayers.add(p);
		alive = true;
	}
	
	public boolean isAlive() {
		return alive;
	}

	public List<ExtronPlayer> getPlayers() {
		return players;
	}
	
	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	public String getName() {
		return name;
	}

	public ICompetitiveManager getManager() {
		return manager;
	}

	public void kickAllMembers() {
		players.clear();
	}
	
	public ChatColor getColor() {
		return color;
	}

	public void memberFinalKilled(ExtronPlayer p) {
		alivePlayers.remove(p);
		if (alivePlayers.isEmpty()) {
			this.alive = false;
			manager.teamEliminated(this);
		}
	}

	public boolean isFull() {
		return players.size() >= manager.getSettings().getPlayersInTeam();
	}

	public List<ExtronPlayer> getAlivePlayers() {
		return alivePlayers;
	}

	@Override
	public String toString() {
		StringBuilder string = new StringBuilder();
		String lastValue = null;

		for (ExtronPlayer value : players) {
			string.append(lastValue = value.getDisplayName()).append(", ");
		}

		string.delete(string.length() - 2, 2147483647);
		if (lastValue != null) {
			if (string.length() != lastValue.length()) {
				string.replace(string.length() - lastValue.length() - 2, string.length() - lastValue.length(), " and ");
			}
		}

		return string.toString();

	}

	public void removeMember(ExtronPlayer p) {
		this.players.remove(p);
		if (manager.isAlive(p)) {
			this.alivePlayers.remove(p);
		}
	}

	public String getColoredName() {
		return this.color + this.name;
	}

	public boolean contains(ExtronPlayer member) {
		return players.contains(member);
	}
}
