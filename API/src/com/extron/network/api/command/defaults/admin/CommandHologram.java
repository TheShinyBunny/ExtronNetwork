package com.extron.network.api.command.defaults.admin;

import com.extron.network.api.Main;
import com.extron.network.api.command.Command;
import com.extron.network.api.command.CommandTree;
import com.extron.network.api.command.argument.ExecuteData;
import com.extron.network.api.command.argument.ExpectedArgs;
import com.extron.network.api.command.argument.ValidArgs;
import com.extron.network.api.hologram.SavedHologram;
import com.extron.network.api.hologram.PlayerHologram;
import com.extron.network.api.permission.Permission;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.ListUtils;
import org.bukkit.ChatColor;

import java.util.function.Consumer;

public class CommandHologram extends CommandTree {
    @Override
    public void addSubCommands(Consumer<Command> c) {
        c.accept(new Command() {
            @Override
            public void execute(ExtronPlayer sender, ExecuteData data) throws Exception {
                sender.getWorld().createHologram(translateChatColor(data.at(0)),sender.getLocation());
                success("Hologram created successfully!");
            }

            @Override
            public ExpectedArgs getArguments() {
                return ExpectedArgs.create()
                        .stringList("line");
            }

            @Override
            public String getDescription() {
                return "Creates a new hologram with a first line of <line...>";
            }

            @Override
            public String getName() {
                return "create";
            }
        });
        c.accept(new Command() {
            @Override
            public void execute(ExtronPlayer sender, ExecuteData data) throws Exception {
                SavedHologram h = sender.editingHologram;
                conditionedNullAndSuccess(h,()->
                {
                    h.addLine(translateChatColor(data.at(0)));
                    h.reload();
                },"Line added successfully!","You are not editing an hologram!");
            }

            @Override
            public ExpectedArgs getArguments() {
                return ExpectedArgs.create()
                        .stringList("line");
            }

            @Override
            public String getDescription() {
                return "Adds a line to the currently edited hologram";
            }

            @Override
            public String getName() {
                return "addline";
            }
        });
        c.accept(new Command() {
            @Override
            public void execute(ExtronPlayer sender, ExecuteData data) throws Exception {
                SavedHologram h = sender.editingHologram;
                isNull(h,"You are not editing an hologram!");
                int i = data.getIntOfRange("index",h.getLines().size()-1,0,h.getLines().size());
                h.removeLine(i);
                h.reload();
                success("Line removed successfully!");
            }

            @Override
            public ExpectedArgs getArguments() {
                return ExpectedArgs.create()
                        .optional()
                        .number("index");
            }

            @Override
            public String getDescription() {
                return "Removes a line from the hologram";
            }

            @Override
            public String getName() {
                return "removeline";
            }
        });
        c.accept(new Command() {
            @Override
            public void execute(ExtronPlayer sender, ExecuteData data) throws Exception {
                conditionedNullAndSuccess(sender.editingHologram,
                        ()-> sender.editingHologram = null,
                        "Hologram deselected!",
                        "You are not editing an hologram!");
            }

            @Override
            public ExpectedArgs getArguments() {
                return null;
            }

            @Override
            public String getDescription() {
                return "Deselects currently selected hologram";
            }

            @Override
            public String getName() {
                return "deselect";
            }
        });
        c.accept(new Command() {
            @Override
            public void execute(ExtronPlayer sender, ExecuteData data) throws Exception {
                SavedHologram h = sender.editingHologram;
                conditionedNullAndSuccess(h,
                        h::despawn,
                        "Hologram is now hidden",
                        "You are not editing an hologram!");
            }

            @Override
            public ExpectedArgs getArguments() {
                return null;
            }

            @Override
            public String getDescription() {
                return "Hides currently selected hologram";
            }

            @Override
            public String getName() {
                return "hide";
            }
        });
        c.accept(new Command() {
            @Override
            public void execute(ExtronPlayer sender, ExecuteData data) throws Exception {
                SavedHologram h = sender.editingHologram;
                isNull(h,"You are not editing an hologram!");
                checkMethod(
                        h::spawn,
                        "Hologram is now visible!",
                        "Hologram was already visible!"
                    );
            }

            @Override
            public ExpectedArgs getArguments() {
                return null;
            }

            @Override
            public String getDescription() {
                return "Shows currently hidden selected hologram";
            }

            @Override
            public String getName() {
                return "show";
            }
        });
        c.accept(new Command() {
            @Override
            public void execute(ExtronPlayer sender, ExecuteData data) throws Exception {
                SavedHologram h = sender.getWorld().findNearestHologram(sender);
                isNull(h,"No holograms exist in this world!");
                sender.editingHologram = h;
                sender.sendMessage(ChatColor.GREEN + "Selected hologram:");
                h.display(sender);
            }

            @Override
            public ExpectedArgs getArguments() {
                return null;
            }

            @Override
            public String getDescription() {
                return "Selects the closest hologram in this world!";
            }

            @Override
            public String getName() {
                return "select";
            }
        });
        c.accept(new Command() {
            @Override
            public void execute(ExtronPlayer sender, ExecuteData data) throws Exception {
                SavedHologram h = sender.editingHologram;
                isNull(h,"You are not editing an hologram!");
                h.delete();
                sender.sendMessage(ChatColor.GREEN + "Deleted hologram:");
                h.display(sender);
                sender.editingHologram = null;
            }

            @Override
            public ExpectedArgs getArguments() {
                return null;
            }

            @Override
            public String getDescription() {
                return "Deletes currently selected hologram";
            }

            @Override
            public String getName() {
                return "delete";
            }
        });
        c.accept(new Command() {
            @Override
            public String getName() {
                return "player";
            }

            @Override
            public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
                PlayerHologram ph = checkMethod(()->Main.getPlayerHologram(args.at(0)),null,"Unknown player hologram",false);
                performConditioned(!ph.isInitialized(),(String)null,"This player hologram is already initialized!");
                ph.initialize(sender.getWorld(),sender.getLocation());
                ph.spawn();
                success("Player Hologram " + ph.getId() + " has been spawned!");
            }

            @Override
            public ExpectedArgs getArguments() {
                return ExpectedArgs.create()
                        .string("id",ValidArgs.getter(()->ListUtils.toStringAll(Main.getNotInitializedHolograms())));
            }

            @Override
            public String getDescription() {
                return "Choose a location for the per player variable holograms.";
            }
        });
    }

    @Override
    public boolean addHelpSubCommand() {
        return true;
    }

    @Override
    public Permission getPermission() {
        return Permission.DEVS;
    }

    @Override
    public String getDescription() {
        return "Command to control holograms!";
    }
}
