package com.extron.network.api.command.defaults;

import com.extron.network.api.command.BaseCommand;
import com.extron.network.api.command.argument.ExecuteData;
import com.extron.network.api.command.argument.ExpectedArgs;
import com.extron.network.api.permission.Rank;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.TextUtils;
import org.bukkit.ChatColor;

public class CommandCoins extends BaseCommand {

    @Override
    public void execute(ExtronPlayer sender, ExecuteData args) throws Exception{
        if (args.isEmpty()) {
            sender.sendMessage(ChatColor.GOLD + "You have currently " + TextUtils.numberComma(sender.getCoins()) + " network coins.");
        } else {
            if (!Rank.DEVELOPER.getPermission().isPermitted(sender)) {
                error("Invalid usage! use /coins");
            }
            ExtronPlayer target = getPlayerByName(args.getEntry("player"));
            if (args.length() > 1) {
                String s = args.at(1).substring(1);
                switch (args.at(1).charAt(0)) {
                    case '+':
                        int i = parseInt(s,"Invalid add getValue! use '+(number)");
                        target.addCoins(i);
                        success("Given " + i + " coins to " + target.getDisplayName());
                    case '-':
                        int i2 = parseInt(s,"Invalid remove getValue! use '-(number)");
                        performConditioned(target.addCoins(-i2),
                                "Taken " + i2 + " coins from " + target.getDisplayName(),
                                target.getName() + " does not have enough coins!");
                    default:
                        int i3 = parseInt(args.at(1),"Invalid operation! use use +[n] to add, -[n] to remove, and [n] to set!");
                        target.setCoins(i3);
                        success("Set " + target.getDisplayName() + "'s coins to " + i3);
                }
            } else {
                success(target.getDisplayName() + " has " + TextUtils.numberComma(target.getCoins()) + " coins.");
            }
        }
    }

    @Override
    public ExpectedArgs getArguments() {
        return ExpectedArgs.create()
                .optional()
                .player("player")
                .string("operation");
    }

    @Override
    public String getDescription() {
        return "Will tell you the amount of network coins you currently have.";
    }
}
