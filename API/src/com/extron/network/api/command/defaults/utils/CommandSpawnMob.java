package com.extron.network.api.command.defaults.utils;

import com.extron.network.api.command.BaseCommand;
import com.extron.network.api.command.argument.ExecuteData;
import com.extron.network.api.command.argument.ExpectedArgs;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.Reflection;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class CommandSpawnMob extends BaseCommand {
    @Override
    public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
        EntityType type = getEnumValue(args.getEntry("type"),EntityType.class);
        int count = args.getInt("count",1);
        for (int i = 0; i < count; i++) {
            sender.getWorld().spawnEntity(sender.getLocation(),type);
        }
    }

    @Override
    public ExpectedArgs getArguments() {
        return ExpectedArgs.create()
                .enumValue("type",EntityType.class,EntityType::isSpawnable)
                .optional()
                .number("count",1,40);
    }

    @Override
    public String[] getAliases() {
        return new String[]{"summon","entity"};
    }

    @Override
    public String getDescription() {
        return null;
    }
}
