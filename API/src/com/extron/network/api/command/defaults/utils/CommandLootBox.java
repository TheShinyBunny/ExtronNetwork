package com.extron.network.api.command.defaults.utils;

import com.extron.network.api.collection.Collectible;
import com.extron.network.api.collection.loot.LootBox;
import com.extron.network.api.collection.loot.LootBoxManager;
import com.extron.network.api.command.BaseCommand;
import com.extron.network.api.command.argument.ExecuteData;
import com.extron.network.api.command.argument.ExpectedArgs;
import com.extron.network.api.command.argument.ValidArgs;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.ListUtils;

public class CommandLootBox extends BaseCommand {
    @Override
    public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
        LootBox box = LootBoxManager.getLootBox(args.at(0));
        LootBoxManager.openLootBox(sender,box);
    }

    @Override
    public ExpectedArgs getArguments() {
        return ExpectedArgs.create()
                .string("id",ValidArgs.getter(ListUtils.convertAndSupply(LootBoxManager.getLootBoxes(),Collectible::getId)));
    }

    @Override
    public String getDescription() {
        return "Force opens a loot box";
    }
}
