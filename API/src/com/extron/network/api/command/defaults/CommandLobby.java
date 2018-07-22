package com.extron.network.api.command.defaults;

import com.extron.network.api.Main;
import com.extron.network.api.command.BaseCommand;
import com.extron.network.api.command.argument.ExecuteData;
import com.extron.network.api.command.argument.ExpectedArgs;
import com.extron.network.api.players.ExtronPlayer;
import org.bukkit.ChatColor;

public class CommandLobby extends BaseCommand {

    @Override
    public void execute(ExtronPlayer sender, ExecuteData args) throws Exception{
        sender.teleportToLobby();
        sender.getScoreboardManager().updateScoreboard(Main.getScoreboard("main_board"));
        sender.sendMessage(ChatColor.GREEN + "Sending you back to the main lobby!");
    }

    @Override
    public ExpectedArgs getArguments() {
        return null;
    }

    @Override
    public String getDescription() {
        return "Use this to go back to the main lobby, and quit your current game.";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"l","hub","main","spawn"};
    }
}
