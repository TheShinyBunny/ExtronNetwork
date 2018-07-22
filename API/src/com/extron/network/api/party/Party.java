package com.extron.network.api.party;

import com.extron.network.api.Main;
import com.extron.network.api.game.managers.GameManager;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.TextUtils;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents a single Party. a party contains 1 leader, members and managers.
 */
public class Party {

	private List<ExtronPlayer> members;
	private List<ExtronPlayer> managers;
	private ExtronPlayer leader;

	private boolean allInvite;
	
	public Party(ExtronPlayer leader) {
		this.members = new ArrayList<>();
		this.managers = new ArrayList<>();
		this.leader = leader;
		this.allInvite = false;
	}

	public void invite(ExtronPlayer sender, ExtronPlayer invited) {
		PartyInvite invite = new PartyInvite(this, sender, invited);
		Main.addPartyInvite(invite);
		invite.timer(0, 20);
		for (ExtronPlayer p : getAllPlayers()) {
			if (p.equals(sender)) {
				p.sendMessage(ChatColor.AQUA + TextUtils.LINE + "\n" +
						ChatColor.YELLOW + "You invited " + invited.getDisplayName() + ChatColor.YELLOW + " to the Party!\nThey have 60 seconds to accept\n" +
						ChatColor.AQUA + TextUtils.LINE);
			} else {
				p.sendMessage(ChatColor.AQUA + TextUtils.LINE + "\n" +
						sender.getDisplayName() + ChatColor.YELLOW + " has invited " + invited.getDisplayName() + ChatColor.YELLOW + " to the Party!\nThey have 60 seconds to accept\n" +
						ChatColor.AQUA + TextUtils.LINE);
			}
		}
		if (sender.equals(leader)) {
			invited.sendMessage(ChatColor.AQUA + TextUtils.LINE + "\n" + sender.getDisplayName() + ChatColor.YELLOW + " has invited you to their party!");
		} else {
			invited.sendMessage(ChatColor.AQUA + TextUtils.LINE + "\n" + sender.getDisplayName() + ChatColor.YELLOW + " has invited you to " + leader.getDisplayName() + ChatColor.YELLOW + "'s party!");
		}
		TextUtils.sendClickableMessage(invited,ChatColor.GOLD + "Click here","p accept " + sender.getName(),ChatColor.YELLOW + " to join. You have 60 seconds to accept.");
		invited.sendMessage(ChatColor.AQUA + TextUtils.LINE);
	}
	
	public void accept(ExtronPlayer accepter) {
		PartyInvite remove = null;
		for (PartyInvite pi : Main.getPartyInvites()) {
			if (pi.getInvited().equals(accepter)) {
				pi.cancel();
				remove = pi;
				accepter.sendMessage(ChatColor.AQUA + TextUtils.LINE + "\n"
						+ ChatColor.GREEN + "You joined " + leader.getDisplayName() + ChatColor.GREEN + "'s Party!"
								+ "\n" + ChatColor.AQUA + TextUtils.LINE);
				for (ExtronPlayer p : this.getAllPlayers()) {
					p.sendMessage(ChatColor.AQUA + TextUtils.LINE + "\n"
							+ ChatColor.GREEN + accepter.getDisplayName() + ChatColor.GREEN + " has joined the party!"
							+ "\n" + ChatColor.AQUA + TextUtils.LINE);
				}
				accepter.setParty(this);
				this.members.add(accepter);
			}
		}
		Main.removePartyInvite(remove);
	}
	
	public PartyMemberRole getRoleOf(ExtronPlayer p) {
		if (p.equals(leader)) {
			return PartyMemberRole.LEADER;
		}
		if (managers.contains(p)) {
			return PartyMemberRole.MANAGER;
		}
		if (members.contains(p)) {
			return PartyMemberRole.MEMBER;
		}
		return PartyMemberRole.MEMBER;
	}

	public ExtronPlayer getLeader() {
		return leader;
	}

	public List<ExtronPlayer> getManagers() {
		return managers;
	}

	public List<ExtronPlayer> getMembers() {
		return members;
	}
	
	public List<ExtronPlayer> getOfficers() {
		List<ExtronPlayer> officers = new ArrayList<>();
		officers.addAll(managers);
		officers.add(leader);
		return officers;
	}
	
	public List<ExtronPlayer> getAllPlayers() {
		List<ExtronPlayer> all = new ArrayList<>();
		all.addAll(members);
		all.addAll(managers);
		all.add(leader);
		return all;
	}

	public List<String> getAllPlayerNames() {
		List<String> names = new ArrayList<>();
		for (ExtronPlayer p : getAllPlayers()) {
			names.add(p.getName());
		}
		return names;
	}

	public static List<String> getAllPlayersInPartyOf(ExtronPlayer p) {
		List<String> list = new ArrayList<>();
		if (!p.isInParty()) {
			return list;
		}
		list.addAll(p.getParty().getAllPlayerNames());
		return list;
	}
	
	public boolean isAllInvite() {
		return allInvite;
	}

	public void expiredInvite(PartyInvite invite) {
		Main.removePartyInvite(invite);
		invite.getSender().sendMessage(ChatColor.AQUA + TextUtils.LINE
				+ ChatColor.RED + "\nThe party invite to " + invite.getInvited().getDisplayName() + ChatColor.RED + " has expired.\n" +
				ChatColor.AQUA + TextUtils.LINE);
		invite.getInvited().sendMessage(ChatColor.AQUA + TextUtils.LINE
				+ ChatColor.RED + "\nThe party invite from " + invite.getSender().getDisplayName() + ChatColor.RED + " has expired.\n" +
				ChatColor.AQUA + TextUtils.LINE);
		checkDisband();
	}

	private void checkDisband() {
		boolean hasInvites = false;
		for (PartyInvite i : Main.getPartyInvites()) {
			if (i.getParty().equals(this)) {
				hasInvites = true;
			}
		}
		if (members.isEmpty() && managers.isEmpty() && !hasInvites) {
			leader.sendMessage(ChatColor.AQUA + TextUtils.LINE
					+ ChatColor.RED + "\nThe party was disbanded because all invites had expired and all party members have left.\n" +
					ChatColor.AQUA + TextUtils.LINE);
			this.disband();
		}
	}

	public void disband() {
		for (ExtronPlayer p : getAllPlayers()) {
			p.leaveParty();
		}
	}

	public static List<String> getInvitesOfPlayer(ExtronPlayer p) {
		List<String> list = new ArrayList<>();
		for (PartyInvite invite : Main.getPartyInvites()) {
			if (invite.getInvited().equals(p)) {
				list.add(invite.getSender().getName());
			}
		}
		return list;
	}

	public void sendMessage(ExtronPlayer p, String message) {
		for (ExtronPlayer pl : getAllPlayers()) {
			pl.sendMessage(ChatColor.BLUE + "[Party] " + ChatColor.RESET + p.getDisplayName() + ": " + message);
		}
	}

	public void leave(ExtronPlayer player) {
		managers.remove(player);
		members.remove(player);
		if (player.equals(leader)) {
			pickRandomLeader();
		}
		for (ExtronPlayer p : this.getAllPlayers()) {
			p.sendMessage(ChatColor.AQUA + TextUtils.LINE + "\n"
					+ player.getDisplayName() + ChatColor.RED + " has left the Party."
					+ "\n" + ChatColor.AQUA + TextUtils.LINE);
		}
		player.sendMessage(ChatColor.AQUA + TextUtils.LINE + "\n"
				+ ChatColor.RED + "You have left the Party."
				+ "\n" + ChatColor.AQUA + TextUtils.LINE);
		checkDisband();
	}

	private void pickRandomLeader() {
		Random r = new Random();
		if (managers.isEmpty()) {
			if (members.size() > 1) {
				leader = members.get(r.nextInt(members.size()));
			} else {
				leader = members.get(0);
			}
			members.remove(leader);
		} else {
			if (managers.size() > 1) {
				leader = managers.get(r.nextInt(managers.size()));
			} else {
				leader = managers.get(0);
			}
			managers.remove(leader);
		}
	}

	public void remove(ExtronPlayer player, ExtronPlayer remover) {
		managers.remove(player);
		if (members.contains(player)) {
			managers.remove(player);
		}
		for (ExtronPlayer p : this.getAllPlayers()) {
			if (p.equals(remover)) {
				p.sendMessage(ChatColor.AQUA + TextUtils.LINE + "\n"
						+ ChatColor.RED + " You removed " + player.getDisplayName() + ChatColor.RED + " from the Party."
						+ "\n" + ChatColor.AQUA + TextUtils.LINE);
			} else {
				p.sendMessage(ChatColor.AQUA + TextUtils.LINE + "\n"
						+ remover.getDisplayName() + ChatColor.RED + " has removed " + player.getDisplayName() + ChatColor.RED + " from the Party."
						+ "\n" + ChatColor.AQUA + TextUtils.LINE);
			}
		}
		player.sendMessage(ChatColor.AQUA + TextUtils.LINE + "\n"
				+ remover.getDisplayName() + ChatColor.RED + " has removed you from the Party."
				+ "\n" + ChatColor.AQUA + TextUtils.LINE);
		checkDisband();
	}

	public List<ExtronPlayer> getAllPlayersInGame(GameManager manager) {
		List<ExtronPlayer> pl = new ArrayList<>();
		for (ExtronPlayer p : getAllPlayers()) {
			if (p.getCurrentGame() != null) {
				if (p.getCurrentGame().equals(manager)) {
					pl.add(p);
				}
			}
		}
		return pl;
	}

	public static List<String> getAllNonLeaderNames(ExtronPlayer p) {
		List<String> list = new ArrayList<>();
		if (!p.isInParty()) return list;
		for (ExtronPlayer mem : p.getParty().members) {
			list.add(mem.getName());
		}
		for (ExtronPlayer man : p.getParty().managers) {
			list.add(man.getName());
		}
		return list;
	}

	public static List<String> getAllManagerNames(ExtronPlayer p) {
		List<String> list = new ArrayList<>();
		if (!p.isInParty()) return list;
		for (ExtronPlayer man : p.getParty().managers) {
			list.add(man.getName());
		}
		return list;
	}

	public void setLeader(ExtronPlayer p, ExtronPlayer sender) {
		this.setRoleSilent(sender,PartyMemberRole.MANAGER);
		this.setRole(p,PartyMemberRole.LEADER,sender);
	}

	public void setRole(ExtronPlayer player, PartyMemberRole role, ExtronPlayer sender) {
		String dir = "demoted";
		if (getRoleOf(player).getPos() < role.getPos()) {
			dir = "promoted";
		}
		for (ExtronPlayer p : this.getAllPlayers()) {
			if (p.equals(sender)) {
				p.sendMessage(ChatColor.AQUA + TextUtils.LINE + "\n"
						+ ChatColor.YELLOW + "You " + dir + " " + player.getDisplayName() + ChatColor.YELLOW + " to " + role.getNiceName() + "."
						+ "\n" + ChatColor.AQUA + TextUtils.LINE);
			} else if (p.equals(player)) {
				p.sendMessage(ChatColor.AQUA + TextUtils.LINE + "\n"
						+ sender.getDisplayName() + (dir.equalsIgnoreCase("demoted") ? ChatColor.RED : ChatColor.GREEN) + " has " + dir + " you to " + role.getNiceName() + "!"
						+ "\n" + ChatColor.AQUA + TextUtils.LINE);
			} else {
				p.sendMessage(ChatColor.AQUA + TextUtils.LINE + "\n"
						+ sender.getDisplayName() + ChatColor.YELLOW + " has " + dir + " " + player.getDisplayName() + ChatColor.YELLOW + " to " + role.getNiceName() + "."
						+ "\n" + ChatColor.AQUA + TextUtils.LINE);
			}
		}
		this.setRoleSilent(player,role);
	}

	private void setRoleSilent(ExtronPlayer p, PartyMemberRole role) {
		managers.remove(p);
		members.remove(p);
		if (leader == p) {
			leader = null;
		}
		switch (role) {
			case MEMBER:
				members.add(p);
				break;
			case MANAGER:
				managers.add(p);
				break;
			case LEADER:
				leader = p;
				break;
		}
	}

	public void promote(ExtronPlayer p, ExtronPlayer sender) {
		if (getRoleOf(p) == PartyMemberRole.MEMBER) {
			setRole(p,PartyMemberRole.MANAGER,sender);
		} else if (getRoleOf(p) == PartyMemberRole.MANAGER) {
			setLeader(p,sender);
		}
	}

	public void demote(ExtronPlayer p, ExtronPlayer sender) {
		if (getRoleOf(p) == PartyMemberRole.MANAGER) {
			setRole(p,PartyMemberRole.MEMBER,sender);
		}
	}

	public void requestCombine(Party to, CombinationType type) {
		CombinationRequest request = new CombinationRequest(this,to,type);
		Main.addPartyCombine(request);
		request.timer(0, 20);
		leader.sendMessage(ChatColor.AQUA + TextUtils.LINE + "\n" +
				ChatColor.YELLOW + "You requested " + to.leader.getDisplayName() + ChatColor.YELLOW + " to combine " + type.toString().toLowerCase() + " his Party!\nThey have 2 minutes to accept\n" +
				ChatColor.AQUA + TextUtils.LINE);
		to.leader.sendMessage(ChatColor.AQUA + TextUtils.LINE + "\n" +
						this.leader.getDisplayName() + ChatColor.YELLOW + " has sent you a request to combine " + type.opposite().toString().toLowerCase() + " his Party!");
		TextUtils.sendClickableMessage(to.leader, ChatColor.BLUE+ "Click here","p combine accept " + leader.getName(),ChatColor.YELLOW + " to combine! The request will expire in 2 minutes!");
		to.leader.sendMessage(ChatColor.AQUA + TextUtils.LINE);
	}

	public void expiredCombine(CombinationRequest request) {
		Main.removePartyCombine(request);
		request.getSender().getLeader().sendMessage(ChatColor.AQUA + TextUtils.LINE
				+ ChatColor.RED + "\nThe party combination request to " + request.getReceiver().getLeader().getDisplayName() + ChatColor.RED + " has expired.\n" +
				ChatColor.AQUA + TextUtils.LINE);
		request.getReceiver().getLeader().sendMessage(ChatColor.AQUA + TextUtils.LINE
				+ ChatColor.RED + "\nThe party combination request from " + request.getSender().getLeader().getDisplayName() + ChatColor.RED + " has expired.\n" +
				ChatColor.AQUA + TextUtils.LINE);
	}

	public void acceptCombine(CombinationRequest request) {
		Main.removePartyCombine(request);
		request.cancel();
		this.sendAll(ChatColor.AQUA + TextUtils.LINE + "\n"
				+ ChatColor.GREEN + "Your party has been combined " + request.getType().opposite().toString().toLowerCase() + " " + request.getSender().leader.getDisplayName() + ChatColor.GREEN + "'s Party!"
				+ "\n" + ChatColor.AQUA + TextUtils.LINE);
		request.getSender().sendAll(ChatColor.AQUA + TextUtils.LINE + "\n"
				+ ChatColor.GREEN + "Your party has been combined " + request.getType().toString().toLowerCase() + " " + leader.getDisplayName() + ChatColor.GREEN + "'s party!"
				+ "\n" + ChatColor.AQUA + TextUtils.LINE);
		if (request.getType() == CombinationType.TO) {
			request.getSender().transferTo(this);
		} else {
			this.transferTo(request.getSender());
		}
	}

	private void transferTo(Party other) {
		for (ExtronPlayer p : getAllPlayers()) {
			p.setParty(other);
			if (p == leader) {
				other.managers.add(p);
			} else {
				other.members.add(p);
			}
		}
	}

	public void sendAll(String msg) {
		for (ExtronPlayer p : getAllPlayers()) {
			p.sendMessage(msg);
		}
	}

	public void getStolenBy(ExtronPlayer thief) {

	}

	public boolean canPlayerInvite(ExtronPlayer sender) {
		return allInvite || getRoleOf(sender) != PartyMemberRole.MEMBER;
	}

	public void toggleAllInvite(ExtronPlayer sender) {
		allInvite = !allInvite;
		if (allInvite) {
			sendAll(sender.getRealDisplayName() + " has enabled all-invite!");
		} else {
			sendAll(sender.getRealDisplayName() + " has disabled all-invite!");
		}
	}
}
