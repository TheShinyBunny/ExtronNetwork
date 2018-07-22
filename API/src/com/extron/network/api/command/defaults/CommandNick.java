package com.extron.network.api.command.defaults;

import com.extron.network.api.command.Command;
import com.extron.network.api.command.CommandTree;
import com.extron.network.api.command.argument.ExecuteData;
import com.extron.network.api.command.argument.ExpectedArgs;
import com.extron.network.api.command.argument.ValidArgs;
import com.extron.network.api.nick.NickHelper;
import com.extron.network.api.nick.skin.SkinManager;
import com.extron.network.api.permission.Permission;
import com.extron.network.api.permission.Rank;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.ListUtils;

import java.util.function.Consumer;

public class CommandNick extends CommandTree {
    @Override
    public void addSubCommands(Consumer<Command> c) {
        c.accept(new Command() {
            @Override
            public String getName() {
                return "set";
            }

            @Override
            public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
                String name = args.at(0);
                checkRange(name.length(),4,16,"Nick Name must be between 4 and 16 characters.");
                matchRegex(name,"^\\w+$","Nick Name contains invalid characters! use only: a-z, A-Z, 0-9, _");
                if (NickHelper.isNameTaken(name)) {
                    error("A player with this name already exist!");
                }
                performConditioned(NickHelper.nickPlayer(sender,name),"You are now nicked as " + sender.getDisplayName() + "!","An error occurred while trying to nick you!");
            }

            @Override
            public ExpectedArgs getArguments() {
                return ExpectedArgs.create()
                        .string("nick name");
            }

            @Override
            public String getDescription() {
                return "Changes your nick name and sets you with a random Rank.";
            }
        });
        c.accept(new Command() {
            @Override
            public String getName() {
                return "rank";
            }

            @Override
            public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
                if (!sender.isNicked()) {
                    error("You are not nicked!");
                }
                Rank r = nullOrGet(Rank.fromString(args.at(0)),"Unknown rank '" + args.at(0) +"'!");
                if (sender.getNickRank() == r) {
                    error("You are already nicked with this rank!");
                }
                NickHelper.setRank(sender,r);
                success("Changed nick rank to " + r.getName() + " successfully!");
            }

            @Override
            public ExpectedArgs getArguments() {
                return ExpectedArgs.create()
                        .string("rank",ValidArgs.getter(ListUtils.convertAndSupply(ListUtils.filter(Rank.ALL,Rank::isNickable),Rank::getName)));
            }

            @Override
            public String getDescription() {
                return null;
            }
        });
        c.accept(new Command() {
            @Override
            public String getName() {
                return "reset";
            }

            @Override
            public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
                if (!sender.isNicked()) {
                    error("You are not nicked!");
                }
                NickHelper.unnick(sender);
            }

            @Override
            public ExpectedArgs getArguments() {
                return null;
            }

            @Override
            public String getDescription() {
                return "Resets your nick to the default.";
            }
        });
        c.accept(new Command() {
            @Override
            public String getName() {
                return "skin";
            }

            @Override
            public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
                SkinManager.changeSkin(sender,args.at(0));
            }

            @Override
            public ExpectedArgs getArguments() {
                return ExpectedArgs.create()
                        .string("player name");
            }

            @Override
            public String getDescription() {
                return null;
            }
        });
    }

    @Override
    public String getDefaultSubCommandToUse() {
        return "set";
    }

    @Override
    public boolean addHelpSubCommand() {
        return true;
    }

    @Override
    public Permission getPermission() {
        return Rank.HELPER.getPermission();
    }

    @Override
    public String getDescription() {
        return "Sets up your nick name and rank!";
    }
}
