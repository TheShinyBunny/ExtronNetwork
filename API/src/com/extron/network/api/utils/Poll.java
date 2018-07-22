package com.extron.network.api.utils;

import com.extron.network.api.players.ExtronPlayer;

import java.util.Collection;
import java.util.List;

public class Poll {

    private ExtronPlayer asked;
    private String question;
    private Collection<Option> options;
    private int totalVotes;

    public Poll(ExtronPlayer p, String question, List<String> options) {
        this.asked = p;
        this.question = question;
        this.options = ListUtils.convertAll(options, Option::new);
    }

    public Collection<Option> getOptions() {
        return options;
    }

    public int getTotalVotes() {
        return totalVotes;
    }

    public String getQuestion() {
        return question;
    }

    public class Option {

        private String name;
        private int votes;

        public Option(String name) {
            this.name = name;
        }

        private void vote() {
            votes++;
            totalVotes++;
        }

        public int getVotes() {
            return votes;
        }

        public String getName() {
            return name;
        }
    }

}
