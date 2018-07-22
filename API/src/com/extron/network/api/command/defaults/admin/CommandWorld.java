package com.extron.network.api.command.defaults.admin;

import com.extron.network.api.ExtronWorld;
import com.extron.network.api.Main;
import com.extron.network.api.command.Command;
import com.extron.network.api.command.CommandTree;
import com.extron.network.api.command.argument.ExecuteData;
import com.extron.network.api.command.argument.ExpectedArgs;
import com.extron.network.api.players.ExtronPlayer;

import java.util.function.Consumer;

public class CommandWorld extends CommandTree {

    @Override
    public void addSubCommands(Consumer<Command> c) {
        c.accept(new CommandCreateWorld(false));

        c.accept(new Command() {

            @Override
            public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
                String name = args.at(0);
                ExtronWorld w = Main.getWorld(name);
                isNull(w,"This world does not exist!");
                checkMethod(()->Main.deleteWorld(w),"World " + name + " was deleted successfully!","An error occurred while deleting world " + name + ".");
            }

            @Override
            public String getDescription() {
                return "Deletes a world from the server.";
            }

            @Override
            public String getName() {
                return "delete";
            }

            @Override
            public ExpectedArgs getArguments() {
                return ExpectedArgs.create()
                        .world("world name");
            }
        });

        c.accept(new Command() {

            @Override
            public void execute(ExtronPlayer sender, ExecuteData data) throws Exception {
                listNames(sender,"Worlds: ", Main.getWorlds(),ExtronWorld::getName,true);
            }

            @Override
            public ExpectedArgs getArguments() {
                return null;
            }

            @Override
            public String getDescription() {
                return "Lists all worlds on the server.";
            }

            @Override
            public String getName() {
                return "list";
            }
        });

        c.accept(new Command() {
            @Override
            public void execute(ExtronPlayer sender, ExecuteData data) throws Exception {
                ExtronWorld to = Main.getWorld(data.at(0));
                isNull(to,"This world does not exist!");
                sender.changeWorld(to);
            }

            @Override
            public ExpectedArgs getArguments() {
                return ExpectedArgs.create()
                        .world("world name");
            }

            @Override
            public String getDescription() {
                return "Teleport to another world";
            }

            @Override
            public String getName() {
                return "tp";
            }
        });
    }

    @Override
    public boolean addHelpSubCommand() {
        return true;
    }

    @Override
    public String getDescription() {
        return "useful commands for creating worlds!";
    }
}
