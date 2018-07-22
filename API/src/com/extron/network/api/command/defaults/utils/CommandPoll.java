package com.extron.network.api.command.defaults.utils;

import com.extron.network.api.Main;
import com.extron.network.api.command.Command;
import com.extron.network.api.command.CommandTree;
import com.extron.network.api.command.argument.ExecuteData;
import com.extron.network.api.command.argument.ExpectedArgs;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.Poll;
import com.extron.network.api.utils.TextUtils;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class CommandPoll extends CommandTree {
    @Override
    public void addSubCommands(Consumer<Command> c) {
        c.accept(new Command() {
            @Override
            public String getName() {
                return "create";
            }

            @Override
            public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
                String a = args.at(0);
                String question = a.substring(0,a.indexOf('?')+1);
                String[] arr = a.substring(a.indexOf('?')+1).split(",");
                List<String> options = Arrays.asList(arr);
                Main.createPoll(sender,question,options);
                success("Poll created successfully!");
            }

            @Override
            public ExpectedArgs getArguments() {
                return ExpectedArgs.create()
                        .stringList("args");
            }

            @Override
            public String getDescription() {
                return null;
            }
        });
        c.accept(new Command() {
            @Override
            public String getName() {
                return "results";
            }

            @Override
            public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
                Poll p = nullOrGet(Main.getCurrentPoll(),"No open polls!");
                sender.sendMessage(p.getQuestion());
                sender.sendMessage("Poll results:");
                for (Poll.Option o : p.getOptions()) {
                    sender.sendMessage(o.getName() + ": " + o.getVotes() + " " + TextUtils.addNeededS(o.getVotes(),"vote"));
                }
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
                return "close";
            }

            @Override
            public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
                isNull(Main.getCurrentPoll(),"No open poll!");
                Main.closePoll();
                sender.sendMessage("Closed poll successfully!");
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
