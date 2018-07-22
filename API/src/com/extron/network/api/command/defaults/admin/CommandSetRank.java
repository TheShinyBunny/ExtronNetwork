package com.extron.network.api.command.defaults.admin;

import com.extron.network.api.command.BaseCommand;
import com.extron.network.api.command.argument.ExecuteData;
import com.extron.network.api.command.argument.ExpectedArgs;
import com.extron.network.api.command.argument.ValidArgs;
import com.extron.network.api.permission.Permission;
import com.extron.network.api.permission.Rank;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.ListUtils;
import org.bukkit.ChatColor;

public class CommandSetRank extends BaseCommand {

	@Override
	public void execute(ExtronPlayer p, ExecuteData args) throws Exception {
		ExtronPlayer ep = getPlayerByName(args.getEntry("player"));
		Rank r = nullOrGet(Rank.fromString(args.at(1)),"Unknown rank " + args.at(1));
		ep.setRank(r);
		ep.sendMessage(ChatColor.GREEN + "You are now " + r.getId().toUpperCase() + "!");
		success("Set rank of player " + ep.getName() + " to " + r.getId() + " successfully!");
	}

	@Override
	public ExpectedArgs getArguments() {
		return ExpectedArgs.create()
				.player("player")
				.string("rank",ValidArgs.getter(()->ListUtils.toStringAll(Rank.ALL)));
	}

	@Override
	public Permission getPermission() {
		return Rank.DEVELOPER.getPermission();
	}

	@Override
	public String getDescription() {
		return "Sets a new rank for the player. Set [permanent] to false to set the rank temporarily, until a server reload.";
	}

	@Override
	public String[] getAliases() {
		return new String[]{"rank","giverank"};
	}

	@Override
	public boolean canConsoleUse() {
		return true;
	}
}
