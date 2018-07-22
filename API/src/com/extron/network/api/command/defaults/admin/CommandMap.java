package com.extron.network.api.command.defaults.admin;

import com.extron.network.api.ExtronWorld;
import com.extron.network.api.Main;
import com.extron.network.api.command.Command;
import com.extron.network.api.command.CommandTree;
import com.extron.network.api.command.argument.ExecuteData;
import com.extron.network.api.command.argument.ExpectedArgs;
import com.extron.network.api.command.argument.ValidArgs;
import com.extron.network.api.game.GameMode;
import com.extron.network.api.game.MapCreator;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.ListUtils;
import org.bukkit.World;
import org.bukkit.WorldType;

import java.util.function.Consumer;

public class CommandMap extends CommandTree {
    @Override
    public void addSubCommands(Consumer<Command> c) {
        c.accept(new Command() {
            @Override
            public String getName() {
                return "create";
            }

            @Override
            public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
                String name = args.at(0);
                GameMode gm = Main.getGameMode(args.at(1));
                if (gm == null) {
                    error("Unknown game mode " + args.at(1));
                } else {
                    isNotNull(Main.getMap(name),"Map with this name already exists!");
                    sender.sendMessage("Creating world map " + name + " for " + gm.getName() + "...");
                    ExtronWorld world = Main.createWorld(name, World.Environment.NORMAL, WorldType.NORMAL, getBoolean(args.getEntry("boolean"), true));
                    world.getConfig().set("gamemode",gm.getId());
                    world.getConfig().save();
                    gm.getMaps().add(world);
                    MapCreator creator = world.getMapCreator();
                    if (creator == null) {
                        creator = gm.getNewMapCreator(world);
                        world.setMapCreator(creator);
                    }
                    sender.startCreatingMap(creator);
                }
            }

            @Override
            public ExpectedArgs getArguments() {
                return ExpectedArgs.create()
                        .string("name")
                        .string("gamemode",ValidArgs.getter(ListUtils.convertAndSupply(Main.getGameModes(),GameMode::getId)))
                        .optional()
                        .bool("void");
            }

            @Override
            public String getDescription() {
                return null;
            }
        });
        c.accept(new Command() {
            @Override
            public String getName() {
                return "save";
            }

            @Override
            public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
                isNull(sender.getMapCreator(),"You are not creating a map!");
                sender.stopCreatingMap();
                sender.teleportToLobby();
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
                return "edit";
            }

            @Override
            public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
                sender.stopCreatingMap();
                GameMode gm = Main.getGameMode(args.at(0));
                if (gm == null) {
                    error("Unknown game mode " + args.at(0));
                } else {
                    ExtronWorld world = Main.getWorld(args.at(1));
                    isNull(world, "Unknown map " + args.at(1));
                    MapCreator creator = world.getMapCreator();
                    if (creator == null) {
                        creator = gm.getNewMapCreator(world);
                        world.setMapCreator(creator);
                    }
                    sender.startCreatingMap(creator);
                }
            }

            @Override
            public ExpectedArgs getArguments() {
                return ExpectedArgs.create()
                        .string("gamemode",ValidArgs.getter(ListUtils.convertAndSupply(Main.getGameModes(),GameMode::getId)))
                        .string("map name",ValidArgs.getter(ListUtils.convertAndSupply(Main.getGameMaps(),ExtronWorld::getName)));
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
