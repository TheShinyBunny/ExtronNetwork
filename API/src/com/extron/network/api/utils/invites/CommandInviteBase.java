package com.extron.network.api.utils.invites;

import com.extron.network.api.command.Command;
import com.extron.network.api.command.CommandTree;
import com.extron.network.api.command.argument.ExecuteData;
import com.extron.network.api.command.argument.ExpectedArgs;
import com.extron.network.api.command.argument.ValidArgs;
import com.extron.network.api.permission.Permission;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.ListUtils;

import java.util.function.Consumer;

public abstract class CommandInviteBase extends CommandTree {

    protected InviteHelper helper;

    public CommandInviteBase(InviteHelper helper) {
        this.helper = helper;
    }

    @Override
    public void addSubCommands(Consumer<Command> c) {
        c.accept(new Command() {
            @Override
            public String getName() {
                return getInviteCommandName();
            }

            @Override
            public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
                ExtronPlayer p = getPlayerByName(args.getEntry("player"));
                onInvitePlayer(sender,p);
                helper.invite(sender,p);
            }

            @Override
            public ExpectedArgs getArguments() {
                return ExpectedArgs.create()
                        .player("player");
            }

            @Override
            public String getDescription() {
                return null;
            }
        });
        c.accept(new Command() {
            @Override
            public String getName() {
                return getAcceptCommandName();
            }

            @Override
            public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
                ExtronPlayer p = getPlayerByName(args.getEntry("player"));
                if (ListUtils.firstMatch(helper.getInvitesOfPlayer(sender),i->i.getSender().equals(p)) == null) {
                    error(getInvalidAcceptMessage());
                }
                onInviteAccepted(sender,p);
                helper.accept(sender,p);
            }

            @Override
            public ExpectedArgs getArguments() {
                return ExpectedArgs.create()
                        .string("player",ValidArgs.getBySender(s->ListUtils.convertAll(helper.getInvitesOfPlayer(s),i->i.getSender().getName())));
            }

            @Override
            public String getDescription() {
                return null;
            }
        });
    }

    protected abstract Permission getInvitePermission();

    protected abstract String getInvalidAcceptMessage();

    protected String getInviteCommandName() {
        return "invite";
    }

    public abstract void onInvitePlayer(ExtronPlayer sender, ExtronPlayer invited) throws Exception;

    public abstract void onInviteAccepted(ExtronPlayer accepted, ExtronPlayer sender) throws Exception;

    public String getAcceptCommandName() {
        return "accept";
    }
}
