package com.extron.network.api.scoreboard;

import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.players.PlayerList;
import com.extron.network.api.utils.AnimatedText;
import org.bukkit.ChatColor;

public class MainScoreboard extends Scoreboard {

    private AnimatedText animation = new AnimatedText("Extron Network") {
        @Override
        public String getHighlightColor() {
            return ChatColor.GOLD + "" + ChatColor.BOLD;
        }

        @Override
        public String getBaseColor() {
            return ChatColor.YELLOW + "" + ChatColor.BOLD;
        }

        @Override
        public int getLetterSkip() {
            return 1;
        }
        @Override
        public int speed() {
            return 2;
        }

        @Override
        public boolean stepLetters() {
            return true;
        }

        @Override
        public int stepLettersDir() {
            return 1;
        }
    };

    @Override
    public String getId() {
        return "main_board";
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public void addLines(ExtronPlayer p, ScoreLines lines) {
        lines.addEmpty();
        lines.addParam("Name",ChatColor.WHITE + "" + ChatColor.BOLD + p.getName());
        lines.addParam("Rank", p.getRank().getName());
        lines.addEmpty();
        lines.addParam("Players",PlayerList.getOnlinePlayers().size());
        lines.addEmpty();
        lines.addParam("Coins",p.getCoins());
        lines.addParam("Level",p.getLevel());
        lines.addEmpty();
        lines.addWebsite();
    }

    @Override
    public AnimatedText getTitleAnimation() {
        return animation;
    }
}
