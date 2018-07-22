package com.extron.network.api.command.defaults.utils;

import com.extron.network.api.command.BaseCommand;
import com.extron.network.api.command.argument.ExecuteData;
import com.extron.network.api.command.argument.ExpectedArgs;
import com.extron.network.api.players.ExtronPlayer;
import org.bukkit.entity.EntityType;

public class CommandKillAll extends BaseCommand {
    @Override
    public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
        EntityType type = getEnumValue(args.getEntry("type"),EntityType.class);
        int radius = args.getInt("radius");
        int count = sender.getWorld().killAll(sender,type,radius);
        sender.sendMessage("Killed " + count + " " + type.getName());
    }

    @Override
    public ExpectedArgs getArguments() {
        return ExpectedArgs.create()
                .enumValue("type",EntityType.class,t->t != EntityType.PLAYER)
                .optional()
                .number("radius");
    }

    @Override
    public String[] getAliases() {
        return new String[]{"ka","killmob","killentity"};
    }

    @Override
    public String getDescription() {
        return "Kills all entities of the specified type in the specified radius around you, or all of them in the world if no radius specified.";
    }
}
