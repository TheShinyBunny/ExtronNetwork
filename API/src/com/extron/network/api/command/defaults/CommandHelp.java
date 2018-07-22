package com.extron.network.api.command.defaults;

import com.extron.network.api.command.BaseCommand;
import com.extron.network.api.command.Command;
import com.extron.network.api.command.argument.ExecuteData;
import com.extron.network.api.command.argument.ExpectedArgs;
import com.extron.network.api.command.argument.ValidArgs;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.ListUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class CommandHelp extends BaseCommand {

    private static final int MAX_PAGE_ROWS = 17;

    private final Command main;

    public CommandHelp(Command main) {
        super();
        this.main = main;
    }

    @Override
    public void execute(ExtronPlayer sender, ExecuteData data) throws Exception {
        performConditionedNull(
                data.at(0),
                ()->this.printAllHelp(sender,getCommandByName(data.at(0))),
                ()->this.printAllHelp(sender,main == null ? getCommandManager().getCommands() : main));
    }

    private void printAllHelp(ExtronPlayer s, Iterable<Command> cmds) {
        if (cmds instanceof Command) {
            printHelpFor(s, (Command) cmds, 0);
        } else {
            s.sendMessage("Printing all help");
        }
        List<Command> list = ListUtils.toList(cmds);
        list.sort(Comparator.comparing(Command::getName));
        for (Command sc : list) {
            if (!getCommandManager().testPermissions(s,sc)) {
                continue;
            }
            printHelpFor(s,sc,0);
        }
    }

    @Override
    public ExpectedArgs getArguments() {
        ExpectedArgs a = ExpectedArgs.create();
        if (main == null) {
            a.optional()
                    .string("command name", ValidArgs.getByExecution(this::completeOnlyMainHelp));
        }
        return a;
    }

    private Collection<String> completeOnlyMainHelp(ExtronPlayer sender, ExecuteData data) {
        if (data.getParent() == null) {
            return ListUtils.convertAll(ListUtils.filter(getCommandManager().getCommands(), cmd->cmd.getPermission().isPermitted(sender)),Command::getName);
        }
        return new ArrayList<>();
    }

    @Override
    public String getDescription() {
        if (main == null) {
            return "Get help for the server or for specific command [command name]";
        }
        return "Get help for the /" + main.getName() + " command.";
    }

}
