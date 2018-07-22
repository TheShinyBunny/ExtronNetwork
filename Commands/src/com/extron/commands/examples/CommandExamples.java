package com.extron.commands.examples;

import com.extron.commands.*;
import com.extron.commands.dispatcher.Dispatcher;
import com.extron.commands.parsers.IntArgParser;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.function.Function;

public class CommandExamples implements CommandHolder {

    @Command(aliases = {"spawn","mob"},permission = "command.admin",description = "Spawns mob(s) of the given type and count.")
    public void spawn(@Sender Player sender, EntityType type, @Range(min = 1,max = 40) @DefaultInt(1) int count) {
        for (int i = 0; i < count; i++) {
            sender.getWorld().spawnEntity(sender.getLocation(),type);
        }
        success("Spawned x" + count + " " + type.getName() + "!");
    }

    @Command(aliases = "goodbye",description = "Bans a player.")
    public void ban(@Sender Player sender, OfflinePlayer target, EnumArg<BanReason> reason) {
        Bukkit.getServer().getBanList(BanList.Type.NAME).addBan(target.getName(),reason.getOrDefault(BanReason.NO_REASON).getName(),null,sender.getName());
        success("banned player " + target.getName());
    }

    @Command(aliases = "gm",description = "Changes a player's game mode.")
    public void gamemode(@Sender Player sender, GameMode gameMode, @NotRequired Player target) {
        if (target == null) {
            sender.setGameMode(gameMode);
        } else {
            target.setGameMode(gameMode);
        }
    }

    @Command(description = "")
    public void createWorld(@Sender Player sender, String name, @NotRequired World.Environment env, @NotRequired WorldType type) {

    }

    static {
        Dispatcher.registerAdditionalParser(new IntArgParser<>(GameMode.class,"gamemode id") {
            @Override
            protected GameMode convert(Integer parsed) {
                return GameMode.getByValue(parsed);
            }
        });
    }
}
