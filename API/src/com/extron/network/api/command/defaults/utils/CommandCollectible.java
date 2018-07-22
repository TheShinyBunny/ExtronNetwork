package com.extron.network.api.command.defaults.utils;

import com.extron.network.api.Main;
import com.extron.network.api.collection.*;
import com.extron.network.api.collection.loot.LootBoxManager;
import com.extron.network.api.collection.loot.LootInstance;
import com.extron.network.api.command.Command;
import com.extron.network.api.command.CommandTree;
import com.extron.network.api.command.argument.ExecuteData;
import com.extron.network.api.command.argument.ExpectedArgs;
import com.extron.network.api.command.argument.ValidArgs;
import com.extron.network.api.inventory.base.ItemDisplayable;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.ListUtils;

import java.util.ArrayList;
import java.util.function.Consumer;

public class CommandCollectible extends CommandTree {
    @Override
    public void addSubCommands(Consumer<Command> c) {
        c.accept(new Command() {
            @Override
            public String getName() {
                return "give";
            }

            @Override
            public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
                CollectibleType type = Main.getCollectibleType(args.at(0));
                isNull(type,"Unknown collectible type!");
                Collectible c = Main.getCollectible(args.at(1), type);
                isNull(c,"Unknown collectible!");
                if (c.obtained(sender)) {
                    error("You already found that collectible!");
                }
                if (type instanceof LobbyCollectibleType) {
                    if (!(c instanceof LobbyCollectible)) {
                        error("Not a lobby collectible!");
                        return;
                    }
                    LootInstance instance = LootBoxManager.createCustomBox(sender,new LobbyCollectible[]{(LobbyCollectible) c});
                    instance.open(sender);
                } else {
                    sender.getCollection().add(c);
                    success("Added " + c.getDisplayName() + " to your collection!");
                }
            }

            @Override
            public ExpectedArgs getArguments() {
                return ExpectedArgs.create()
                        .string("type",ValidArgs.getter(ListUtils.convertAndSupply(LobbyCollectibleType.getAllTypes(),ItemDisplayable::getId)))
                        .string("id",ValidArgs.getByExecution((p,d)->{
                            CollectibleType type = Main.getCollectibleType(d.at(0));
                            if (type == null) {
                                return new ArrayList<>();
                            }
                            return ListUtils.convertAll(ListUtils.filter(Main.getCollectiblesOfType(type),Collectible::isObtainable),ItemDisplayable::getId);
                        }));
            }

            @Override
            public String getDescription() {
                return null;
            }
        });
        c.accept(new Command() {
            @Override
            public String getName() {
                return "revoke";
            }

            @Override
            public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
                CollectibleType type = Main.getCollectibleType(args.at(0));
                isNull(type,"Unknown collectible type!");
                Collectible c = Main.getCollectible(args.at(1), type);
                isNull(c,"Unknown collectible!");
                if (c.obtained(sender)) {
                    sender.getCollection().remove(c);
                    success("Removed collectible " + c.getDisplayName());
                } else {
                    error("You do not own this collectible!");
                }
            }

            @Override
            public ExpectedArgs getArguments() {
                return ExpectedArgs.create()
                        .string("type",ValidArgs.getter(ListUtils.convertAndSupply(Main.getCollectibleTypes(),ItemDisplayable::getId)))
                        .string("id",ValidArgs.getByExecution((p,d)->{
                            CollectibleType type = Main.getCollectibleType(d.at(0));
                            if (type == null) {
                                return new ArrayList<>();
                            }
                            return ListUtils.convertAll(Main.getCollectiblesOfType(type),ItemDisplayable::getId);
                        }));
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
