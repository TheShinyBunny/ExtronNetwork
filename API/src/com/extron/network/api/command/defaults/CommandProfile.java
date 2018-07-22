package com.extron.network.api.command.defaults;

import com.extron.network.api.command.BaseCommand;
import com.extron.network.api.command.argument.ExecuteData;
import com.extron.network.api.command.argument.ExpectedArgs;
import com.extron.network.api.inventory.defaults.ProfileMenu;
import com.extron.network.api.players.ExtronPlayer;

public class CommandProfile extends BaseCommand {

    @Override
    public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
        if (args.getAlias().equalsIgnoreCase("myprofile")) {
            sender.openInventory(new ProfileMenu(sender));
        } else {
            ExtronPlayer p = getPlayerByName(args.getEntry("player"));
            sender.openInventory(new ProfileMenu(p));
        }
    }

    @Override
    public String getName() {
        return "profile";
    }

    @Override
    public ExpectedArgs getArguments() {
        return ExpectedArgs.create()
                .optional()
                .player("player");
    }

    @Override
    public String[] getAliases() {
        return new String[]{"myprofile"};
    }

    @Override
    public String getDescription() {
        return "See a profile of a player.";
    }
}
