package com.extron.network.api.command.defaults;

import com.extron.network.api.command.BaseCommand;
import com.extron.network.api.command.argument.ExecuteData;
import com.extron.network.api.command.argument.ExpectedArgs;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.ChatType;

public class CommandChat extends BaseCommand {

    @Override
    public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
        ChatType type = getEnumValue(args.getEntry("type"),ChatType.class);
        performConditioned(type != ChatType.PARTY || sender.getParty() != null,(String)null,"You must be in a party to use this command!");
        performConditioned(type != sender.chat,(String)null,"You are already talking in this chat!");
        sender.chat = type;
        success("Talking in chat " + type.toString());
    }

    @Override
    public ExpectedArgs getArguments() {
        return ExpectedArgs.create()
                .enumValue("type",ChatType.class,t->t != ChatType.PRIVATE);
    }

    @Override
    public String getDescription() {
        return "Moves you to a different chat channel, and any chat message you send will redirect there.\n" +
                "ALL: the default public chat, visible for all players in the same world.\n" +
                "PARTY: visible only for players in your party.\n" +
                "TEAM: visible only for players in your team in a game.";
    }
}
