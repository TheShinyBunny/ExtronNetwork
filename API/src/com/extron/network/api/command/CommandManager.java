package com.extron.network.api.command;

import com.extron.network.api.command.argument.ArgStringList;
import com.extron.network.api.command.argument.Argument;
import com.extron.network.api.command.argument.ExecuteData;
import com.extron.network.api.command.argument.ExpectedArgs;
import com.extron.network.api.command.exceptions.CommandException;
import com.extron.network.api.event.EventManager;
import com.extron.network.api.event.command.CommandExecuteEvent;
import com.extron.network.api.event.command.CommandTabCompleteEvent;
import com.extron.network.api.permission.Permission;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.players.FakePlayer;
import com.extron.network.api.utils.ListUtils;
import com.extron.network.api.utils.TextUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class CommandManager {

	private final Pattern SPACE_SPLITTER = Pattern.compile(" ", Pattern.LITERAL);
	private Map<String,Command> commandMap;
	private List<Command> cmds;

    public CommandManager() {
		this.commandMap = new HashMap<>();
		this.cmds = new ArrayList<>();
	}

	public boolean handle(ExtronPlayer sender, String message) {
		System.out.println("handling command!");
		if (message == null || sender == null) return false;
		if (message.startsWith("/give") || message.startsWith("give")) {
		    sender.dropItems = true;
        }
        if (!EventManager.callEvent(new CommandExecuteEvent.PreProcess(sender,message))) return false;
        String s = message;
        if (message.startsWith("/")) {
            s = s.substring(1);
        }
        String[] rawArgs = SPACE_SPLITTER.split(s.trim());
        String alias = rawArgs[0].toLowerCase();
        rawArgs = removeFirst(rawArgs);
        Command cmd = this.getCommand(alias);
        if (cmd == null) {
        	System.out.println("not a piston command!");
            return false;
        }
        System.out.println("the command is /" + cmd.getName());
        this.process(sender,cmd,rawArgs,new ExecuteData(cmd, alias));
		return true;
	}
	
	private void process(ExtronPlayer sender, Command cmd, String[] args, ExecuteData d) {
        if (!this.testPermissions(sender,cmd)) return;
        try {
            ExpectedArgs expected = cmd.getArguments() == null ? ExpectedArgs.create() : cmd.getArguments();
            ExecuteData ed = expected.validate(sender, cmd, d.getAlias(), args);
            ExecuteData data = d.getParent() == null ? ed : d.merge(ed);
            System.out.println("checking sub commands");
            if (data.getSubCommand() == null) {
                if (!EventManager.callEvent(new CommandExecuteEvent.PreExecute(sender, cmd, data))) return;
                System.out.println("executing command /" + cmd.getName());
                System.out.println(data.toString());
                if (cmd instanceof CommandTree) {
                    data.getCommand().execute(sender,data);
                } else {
                    cmd.execute(sender, data);
                }
                EventManager.callEvent(new CommandExecuteEvent.PostExecute(sender, cmd, data));
            } else {
                System.out.println("found sub command!");
                this.process(sender, data.getSubCommand().getSecond(), ListUtils.subArray(args, data.getSubCommandIndex() + 1), data);
            }
        } catch (CommandException e) {
            sender.sendMessage(e.getPrefix() + e.getMessage());
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "An unknown error occurred while executing this command.");
            e.printStackTrace();
        }
    }
	
	public void forceExecute(ExtronPlayer sender, Command cmd, ExecuteData data) {
        try {
            cmd.execute(sender,data);
        } catch (CommandException e) {
            sender.sendMessage(e.getPrefix() + e.getMessage());
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "An unknown error occurred while executing this command.");
            e.printStackTrace();
        }
    }
	
	/**
     * Tests the sender's permissions to use the command.
     * @param sender The sender executed the command
     * @param cmd The command being processed
     * @return Whether the sender is permitted.
     */
    public boolean testPermissions(ExtronPlayer sender, Command cmd) {
        Permission p = cmd.getPermission();
        if (p != null) {
            if (!p.isPermitted(sender)) {
                String msg = p.getCommandPermMessage(sender);
                sender.sendMessage(ChatColor.RED + msg);
                return false;
            }
        }
        return true;
    }
    
    /**
     * Tab completes a command.
     * @param sender The sender who pressed TAB
     * @param msg The current message typed to the command line.
     * @return The list of strings to pass back to a {@link net.minecraft.server.v1_8_R1.PacketPlayOutTabComplete}
     */
    public List<String> tabComplete(ExtronPlayer sender, String msg) {
        List<String> list = new ArrayList<>();
        if (!EventManager.callEvent(new CommandTabCompleteEvent.Pre(sender, msg))) return list;
        if (msg.startsWith("/")) {
            msg = msg.substring(1);
        }
        //msg = msg.trim();
        if (msg.contains(" ")) {
            String name = msg.substring(0, msg.indexOf(" "));
            Command c = this.getCommand(name);
            if (c == null) {
                return list;
            }
            list.addAll(tabCompleteArguments(sender, c, name, msg.substring(name.length() + 1)));
        } else {
            String prefix = sender instanceof FakePlayer ? "" : "/";
            for (Map.Entry<String, Command> e : commandMap.entrySet()) {
                if (e.getValue().getPermission().isPermitted(sender)) {
                    if (TextUtils.startsWithIgnoreCase(e.getKey(), msg)) {
                        list.add(prefix + e.getKey());
                    }
                }
            }
        }
        list.sort(String.CASE_INSENSITIVE_ORDER);
        if (!EventManager.callEvent(new CommandTabCompleteEvent.Post(sender, msg, list))) return new ArrayList<>();
        return list;
    }

    private List<String> tabCompleteArguments(ExtronPlayer sender, Command c, String name, String msg) {
        System.out.println("tab completing arguments for: " + name + " -> '" + msg + "'");
        List<String> list = new ArrayList<>();
        if (!c.getPermission().isPermitted(sender)) {
            return list;
        }
        ExecuteData data = new ExecuteData(c,name);
        String[] args = SPACE_SPLITTER.split(msg,-1);
        System.out.println("args:");
        ListUtils.printArray(args);
        ExpectedArgs ea = c.getArguments();
        ExpectedArgs expected = ea == null ? ExpectedArgs.create() : ea;
        for (Argument<?> a : expected.toList()) {
            String arg = a.getIndex() >= args.length ? null : args[a.getIndex()];
            if (a instanceof ArgStringList) {
                arg = arg == null ? null : String.join(" ", ListUtils.subArray(args,a.getIndex()));
            }
            data.add(a,arg);
        }
        if (data.getSubCommand() != null) {
            if (data.getSubCommand().getSecond() != null) {
                if (!msg.substring(data.at(data.getSubCommandIndex()).length()).isEmpty()) {
                    list.addAll(tabCompleteArguments(sender, data.getSubCommand().getSecond(), data.at(data.getSubCommandIndex()), msg.substring(data.at(data.getSubCommandIndex()).length()+1)));
                    return list;
                }
            }
        }
        list.addAll(expected.tabComplete(sender, data, args[args.length - 1], args.length - 1));
        return list;
    }
	
	private String[] removeFirst(String[] args) {
		String[] newArgs = new String[args.length - 1];
        System.arraycopy(args, 1, newArgs, 0, args.length - 1);
        return newArgs;
	}

	public boolean handle(CommandSender p, String message) {
        System.out.println("handling non-player command!");
        if (message == null || p == null) return false;
        FakePlayer sender = FakePlayer.of(p);
        if (!EventManager.callEvent(new CommandExecuteEvent.PreProcess(sender,message))) return false;
        String s = message;
        if (message.startsWith("/")) {
            s = s.substring(1);
        }
        String[] rawArgs = s.trim().split(" ");
        String alias = rawArgs[0].toLowerCase();
        rawArgs = removeFirst(rawArgs);
        Command cmd = this.getCommand(alias);
        if (cmd == null) {
            System.out.println("not a piston command!");
            return false;
        } else if (!cmd.canConsoleUse()) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }
        System.out.println("the command is /" + cmd.getName());
        this.process(sender,cmd,rawArgs,new ExecuteData(cmd,alias));
        return true;
    }

    /**
     * Registers a custom command to the command registry. basically just adds every alias of the command to the {@code HashMap<String,ICommand>}, and just the command to the {@link Command}.
     * @param cmd the command to register
     */
    public void register(Command cmd) {
        this.commandMap.put(cmd.getName(),cmd);
        this.cmds.add(cmd);
        for (String a : cmd.getAliases()) {
            this.commandMap.put(a,cmd);
        }
    }

	public Command getCommand(String alias) {
		if (alias == null) return null;
        return commandMap.get(alias);
	}

	public Command getParent(Command cmd) {
		return ListUtils.firstMatch(cmds,c->ListUtils.arrayContains(c.getSubCommands(),cmd));
	}

	public Iterable<Command> getCommands() {
		return cmds;
	}
}
