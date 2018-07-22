package com.extron.network.api.command.defaults;

import com.extron.network.api.command.BaseCommand;
import com.extron.network.api.command.argument.ExecuteData;
import com.extron.network.api.command.argument.ExpectedArgs;
import com.extron.network.api.permission.Permission;
import com.extron.network.api.permission.Rank;
import com.extron.network.api.players.ExtronPlayer;
import org.bukkit.ChatColor;

public class CommandXP extends BaseCommand {
    @Override
    public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
        ExtronPlayer target = getPlayerByName(args.getEntry("player"));
        EnumXpLevel exl = getEnumValue(args.getEntry("points/levels/needed"),EnumXpLevel.class);
        if (args.at(2) == null) {
            switch (exl) {
                case POINTS:
                    success(target.getName() + " has " + target.getXp() + " xp points");
                case LEVELS:
                    success(target.getName() + " has " + target.getLevel() + " xp levels");
                case NEEDED:
                    success(target.getName() + " needs a total of " + target.getXpToNextLevel() + " xp points to the next level. " + (target.getXpToNextLevel() - target.getXp()) + " xp left!");
            }
        } else {
            int a = args.getInt("amount");
            checkRange(a,1,1000000,"Amount must be positive and less than 1,000,000!");
            switch (exl) {
                case POINTS:
                    target.addXP(a);
                    success("Added " + a + " xp points to " + target.getName());
                case LEVELS:
                    target.addLevels(a);
                    success("Added " + a + " xp levels to " + target.getName());
                case NEEDED:
                    error(ChatColor.RED + "Can't change the needed xp for a player!");
            }
        }
    }

    @Override
    public String getName() {
        return "xp";
    }

    @Override
    public ExpectedArgs getArguments() {
        return ExpectedArgs.create()
                .player("player")
                .enumValue("points/levels/needed",EnumXpLevel.class)
                .number("amount");
    }

    @Override
    public Permission getPermission() {
        return Rank.DEVELOPER.getPermission();
    }

    @Override
    public String getDescription() {
        return "Changes the xp level of a player.";
    }

    public enum EnumXpLevel {
        POINTS, LEVELS, NEEDED;
    }
}
