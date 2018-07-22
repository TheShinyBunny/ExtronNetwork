package com.extron.network.api.command.defaults.admin;

import com.extron.network.api.ExtronWorld;
import com.extron.network.api.command.Command;
import com.extron.network.api.command.CommandTree;
import com.extron.network.api.command.argument.ExecuteData;
import com.extron.network.api.command.argument.ExpectedArgs;
import com.extron.network.api.parkour.Parkour;
import com.extron.network.api.permission.Permission;
import com.extron.network.api.permission.Rank;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.TextUtils;
import org.bukkit.ChatColor;

import java.util.function.Consumer;

public class CommandParkour extends CommandTree {

    @Override
    public void addSubCommands(Consumer<Command> c) {
        c.accept(new Command() {
            @Override
            public String getName() {
                return "create";
            }

            @Override
            public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
                ExtronWorld w = sender.getWorld();
                isNull(w,"You are in an unknown world...");
                Parkour p = w.createParkour();
                sender.editingParkour = p;
                success("Created parkour with id " + ChatColor.AQUA + "#%d",p.getId());
            }

            @Override
            public ExpectedArgs getArguments() {
                return null;
            }

            @Override
            public String getDescription() {
                return "Creates and selects a new parkour mission";
            }

            @Override
            public Permission getPermission() {
                return Rank.DEVELOPER.getPermission();
            }
        });
        c.accept(new Command() {
            @Override
            public String getName() {
                return "add";
            }

            @Override
            public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
                isNull(sender.editingParkour,"You are not editing a parkour");
                sender.editingParkour.addCheckpoint(sender.getLocation());
                success("Added parkour checkpoint!");
            }

            @Override
            public ExpectedArgs getArguments() {
                return null;
            }

            @Override
            public String getDescription() {
                return "Adds a parkour start, checkpoint or end to the selected parkour";
            }

            @Override
            public Permission getPermission() {
                return Rank.DEVELOPER.getPermission();
            }
        });
        c.accept(new Command() {
            @Override
            public String getName() {
                return "remove";
            }

            @Override
            public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
                isNull(sender.editingParkour,"You are not editing a parkour");
                int index = args.getIntOfRange("index",sender.editingParkour.getLastIndex(),0,sender.editingParkour.getLastIndex());
                performConditioned(sender.editingParkour.removeCheckpoint(index),"Removed the " + TextUtils.addPosSuffix(index) + " parkour landmark!","Landmark not found!");
            }

            @Override
            public ExpectedArgs getArguments() {
                return ExpectedArgs.create()
                        .optional()
                        .number("index");
            }

            @Override
            public String getDescription() {
                return "Removes the last landmark from the selected parkour. Specify an [index] to remove a specific mark.\n" +
                        "0 = The start\n" +
                        "1 -> last checkpoint index = A checkpoint\n" +
                        "last checkpoint index + 1 = The End";
            }

            @Override
            public Permission getPermission() {
                return Rank.DEVELOPER.getPermission();
            }
        });
        c.accept(new Command() {
            @Override
            public String getName() {
                return "info";
            }

            @Override
            public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
                isNull(sender.editingParkour,"You are not editing a parkour");
                sender.editingParkour.sendInfo(sender);
            }

            @Override
            public ExpectedArgs getArguments() {
                return null;
            }

            @Override
            public String getDescription() {
                return "Sends a debug message to list all landmarks in the selected parkour.";
            }

            @Override
            public Permission getPermission() {
                return Rank.DEVELOPER.getPermission();
            }
        });
        c.accept(new ResetParkour() {
            @Override
            public String getName() {
                return "reset";
            }

            @Override
            public String[] getAliases() {
                return new String[]{"again","tryagain","start"};
            }
        });
        c.accept(new Checkpoint() {
            @Override
            public String getName() {
                return "checkpoint";
            }

            @Override
            public String[] getAliases() {
                return new String[0];
            }
        });
    }

    @Override
    public boolean addHelpSubCommand() {
        return true;
    }

    @Override
    public String getDescription() {
        return "The command to use to start over a parkour or return to the last checkpoint";
    }

    public static class ResetParkour implements Command {
        @Override
        public String getName() {
            return "parkourreset";
        }

        @Override
        public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
            isNull(sender.getCurrentParkour(),"You are currently not in a parkour!");
            sender.handle.teleport(sender.getCurrentParkour().getStart().getLocation());
        }

        @Override
        public ExpectedArgs getArguments() {
            return null;
        }

        @Override
        public String getDescription() {
            return "Resets your timer and teleport you to the start of the current parkour";
        }

        @Override
        public String[] getAliases() {
            return new String[]{"pr","preset"};
        }
    }

    public static class Checkpoint implements Command {

        @Override
        public String getName() {
            return "parkourcheckpoint";
        }

        @Override
        public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
            isNull(sender.getCurrentParkour(),"You are currently not in a parkour!");
            sender.handle.teleport(sender.getLastCheckpoint().getLocation());
        }

        @Override
        public ExpectedArgs getArguments() {
            return null;
        }

        @Override
        public String getDescription() {
            return "Teleports you back to the last checkpoint you passed.";
        }

        @Override
        public String[] getAliases() {
            return new String[]{"pcp","parkourcp","pcheckpoint"};
        }
    }
}
