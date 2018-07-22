package com.extron.commands;

import org.bukkit.command.CommandSender;

import java.util.function.Function;

public interface SenderCompletion extends Function<CommandSender,String[]> {

}
