package com.extron.network.api.event.command;

import com.extron.network.api.event.ExtronEvent;
import com.extron.network.api.players.ExtronPlayer;
import org.bukkit.event.Cancellable;

import java.util.List;

public abstract class CommandTabCompleteEvent extends ExtronEvent implements Cancellable {

    public final ExtronPlayer sender;
    public String rawMessage;

    public CommandTabCompleteEvent(ExtronPlayer sender, String rawMessage) {
        this.sender = sender;
        this.rawMessage = rawMessage;
    }

    public abstract Phase getPhase();

    public enum Phase {
        PRE, POST;
    }

    public static class Pre extends CommandTabCompleteEvent {

        public Pre(ExtronPlayer sender, String rawMessage) {
            super(sender, rawMessage);
        }

        @Override
        public Phase getPhase() {
            return Phase.PRE;
        }
    }

    public static class Post extends CommandTabCompleteEvent {

        public List<String> completions;

        public Post(ExtronPlayer sender, String rawMessage, List<String> list) {
            super(sender, rawMessage);
            this.completions = list;
        }

        @Override
        public Phase getPhase() {
            return Phase.POST;
        }
    }

}
