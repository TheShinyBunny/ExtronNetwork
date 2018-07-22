package com.extron.network.api.command.defaults;

import com.extron.network.api.Main;
import com.extron.network.api.command.BaseCommand;
import com.extron.network.api.command.argument.ExecuteData;
import com.extron.network.api.command.argument.ExpectedArgs;
import com.extron.network.api.command.argument.ValidArgs;
import com.extron.network.api.game.GameMode;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.ListUtils;

public class CommandPlay extends BaseCommand {
    @Override
    public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
        GameMode gm = Main.getGameMode(args.at(0));
        isNull(gm,"Unknown game mode " + args.at(0));
        sender.findGame(gm);
    }

    @Override
    public ExpectedArgs getArguments() {
        return ExpectedArgs.create()
                .string("game id",ValidArgs.getter(ListUtils.convertAndSupply(Main.getGameModes(),GameMode::getId)));
    }

    @Override
    public String getDescription() {
        return null;
    }
}
