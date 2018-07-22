package com.extron.network.api.event.command;

import com.extron.network.api.command.Command;
import com.extron.network.api.command.argument.ExecuteData;
import com.extron.network.api.event.ExtronEvent;
import com.extron.network.api.players.ExtronPlayer;
import org.bukkit.event.Cancellable;

public abstract class CommandExecuteEvent extends ExtronEvent {

    public final ExtronPlayer executor;

    public CommandExecuteEvent(ExtronPlayer executor) {
        this.executor = executor;
    }

    public abstract Phase getPhase();

    public enum Phase {
        PRE_PROCESS, PRE_EXECUTE, POST_EXECUTE;
    }

    public static class PreProcess extends CommandExecuteEvent implements Cancellable {

        public String command;

        public PreProcess(ExtronPlayer executor, String command) {
            super(executor);
            this.command = command;
        }

        @Override
        public Phase getPhase() {
            return Phase.PRE_PROCESS;
        }
    }

    public static class PreExecute extends CommandExecuteEvent implements Cancellable {

        public final ExecuteData data;
        public final Command cmd;

        public PreExecute(ExtronPlayer executor, Command cmd, ExecuteData data) {
            super(executor);
            this.cmd = cmd;
            this.data = data;
        }

        @Override
        public Phase getPhase() {
            return Phase.PRE_EXECUTE;
        }
    }

    public static class PostExecute extends CommandExecuteEvent {

        public final ExecuteData data;
        public final Command cmd;

        public PostExecute(ExtronPlayer executor, Command cmd, ExecuteData data) {
            super(executor);
            this.cmd = cmd;
            this.data = data;
        }

        @Override
        public Phase getPhase() {
            return Phase.POST_EXECUTE;
        }
    }

}
