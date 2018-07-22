package com.extron.network.api.command.defaults.trolls;

import com.extron.network.api.command.Command;
import com.extron.network.api.command.CommandTree;
import com.extron.network.api.command.argument.ExecuteData;
import com.extron.network.api.command.argument.ExpectedArgs;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.tasks.ExtronRunnable;
import org.bukkit.ChatColor;

import java.util.function.Consumer;

public class CommandFind extends CommandTree {
    @Override
    public void addSubCommands(Consumer<Command> c) {
        c.accept(new Command() {
            @Override
            public String getName() {
                return "tomerbrain";
            }

            @Override
            public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
                sender.sendMessage(ChatColor.YELLOW + "Searching...");
                new ExtronRunnable() {
                    @Override
                    public void run() {
                        sender.sendMessage(ChatColor.RED + "An error occurred while trying to find the specified object.");
                    }
                }.delay(100);
            }

            @Override
            public ExpectedArgs getArguments() {
                return null;
            }

            @Override
            public String getDescription() {
                return null;
            }
        });
        c.accept(new Command() {
            @Override
            public String getName() {
                return "redspongeyt";
            }

            @Override
            public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
                sender.sendMessage(ChatColor.YELLOW + "Searching for a sponge...");
                new ExtronRunnable() {
                    @Override
                    public void run() {
                        sender.sendMessage(ChatColor.RED + "An error occurred while trying to find the specified efes.");
                    }
                }.delay(100);
            }

            @Override
            public ExpectedArgs getArguments() {
                return null;
            }

            @Override
            public String getDescription() {
                return null;
            }
        });
    }

    @Override
    public boolean addHelpSubCommand() {
        return false;
    }

    @Override
    public String getDescription() {
        return null;
    }
}
