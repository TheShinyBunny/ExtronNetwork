package com.extron.network.api.command.defaults.utils;

import com.extron.network.api.command.Command;
import com.extron.network.api.command.CommandTree;
import com.extron.network.api.command.argument.ExecuteData;
import com.extron.network.api.command.argument.ExpectedArgs;
import com.extron.network.api.players.ExtronPlayer;
import org.bukkit.ChatColor;

import java.util.function.Consumer;

public class CommandCooldown extends CommandTree {
    @Override
    public void addSubCommands(Consumer<Command> c) {
        c.accept(new Command() {
            @Override
            public String getName() {
                return "cancel";
            }

            @Override
            public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
                if (args.at(0) == null || getOnlinePlayerByName(args.getEntry("player")).equals(sender)) {
                    sender.cancelGadgetCooldown();
                    success("Cancelled your gadget cooldown.");
                } else {
                    ExtronPlayer p = getOnlinePlayerByName(args.getEntry("player"));
                    p.cancelGadgetCooldown();
                    success("Cancelled " + p.getName() + "'s gadget cooldown.");
                }
            }

            @Override
            public ExpectedArgs getArguments() {
                return ExpectedArgs.create()
                        .optional()
                        .onlinePlayer("player");
            }

            @Override
            public String getDescription() {
                return "Cancels your/other player's current gadget cooldown";
            }
        });
        c.accept(new Command() {
            @Override
            public String getName() {
                return "set";
            }

            @Override
            public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
                int seconds = args.getIntOfRange("seconds",-1,0,-1);
                if (args.at(1) == null || getOnlinePlayerByName(args.getEntry("player")).equals(sender)) {
                    sender.setGadgetCooldown(seconds);
                    success("Set your gadget cooldown to " + ChatColor.GOLD + seconds + ChatColor.GREEN + "s");
                } else {
                    ExtronPlayer p = getOnlinePlayerByName(args.getEntry("player"));
                    p.setGadgetCooldown(seconds);
                    success("Set " + p.getName() + "'s gadget cooldown to " + ChatColor.GOLD + seconds + ChatColor.GREEN + "s");
                }
            }

            @Override
            public ExpectedArgs getArguments() {
                return ExpectedArgs.create()
                        .number("seconds")
                        .optional()
                        .onlinePlayer("player");
            }

            @Override
            public String getDescription() {
                return "Changes yours or another player's gadget cooldown to the specified seconds";
            }
        });

    }

    @Override
    public boolean addHelpSubCommand() {
        return false;
    }

    @Override
    public String[] getAliases() {
        return new String[]{"gadgetcooldown"};
    }

    @Override
    public String getDescription() {
        return null;
    }
}
