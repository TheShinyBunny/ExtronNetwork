package com.extron.network.api.scoreboard;

import com.extron.network.api.permission.Rank;
import com.extron.network.api.players.ExtronPlayer;
import net.minecraft.server.v1_8_R1.IScoreboardCriteria;
import net.minecraft.server.v1_8_R1.ScoreboardObjective;
import net.minecraft.server.v1_8_R1.ScoreboardScore;
import net.minecraft.server.v1_8_R1.ScoreboardServer;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

/**
 * The scoreboard manager of a player. This will handle and manage everything related to scoreboards.
 *
 */
public class ScoreboardManager {

    /**
     * The current extron scoreboard displayed on the sidebar
     */
    private Scoreboard scoreboard;
    /**
     * The player that owns this manager
     */
    private ExtronPlayer player;

    /**
     * The scoreboard handler. Does not change except on reload. This is basically a scoreboard created for every player.
     */
    private org.bukkit.scoreboard.Scoreboard handle;

    /**
     * Current frame of the sidebar title animation
     */
    private int currentFrame;
    private int frameDelay = 0;
    /**
     * The scoreboard objective to display on the tab list
     */
    private Objective tab;
    /**
     * A {@link ScoreUpdater} to update the tab list objective.
     */
    private ScoreUpdater tabUpdater;
    /**
     * The scoreboard objective to display bellow the nametags
     */
    private Objective bellowName;
    /**
     * A {@link ScoreUpdater} to update the bellow name objective.
     */
    private ScoreUpdater nameUpdater;

    public ScoreboardManager(ExtronPlayer p) {
        this.player = p;
        this.handle = Bukkit.getScoreboardManager().getNewScoreboard();
        this.currentFrame = 0;
    }

    /**
     * Will update the scoreboard on the sidebar.<br/>
     * if the current scoreboard equals to <code>sb</code> it will just update the lines according to the {@link Scoreboard#addLines(ExtronPlayer, ScoreLines)}.<br/>
     * if the current and <code>sb</code> are different, will replace the scoreboard displayed with the new one.
     * @param sb The scoreboard to update
     */
    public void updateScoreboard(Scoreboard sb) {
        if (sb == null) return;
        player.handle.setScoreboard(handle);
        if (scoreboard != null) {
            if (!sb.equals(scoreboard)) {
                if (handle.getObjective(scoreboard.getId()) != null) {
                    handle.getObjective(scoreboard.getId()).unregister();
                    currentFrame = 0;
                }
            }
        }
        Objective obj = handle.getObjective(sb.getId());
        if (obj == null) {
            obj = handle.registerNewObjective(sb.getId(), "dummy");
        } else {
            obj.unregister();
            obj = handle.registerNewObjective(sb.getId(), "dummy");
        }
        obj.setDisplaySlot(sb.getDisplaySlot());
        if (sb.getTitleAnimation() != null) {
            obj.setDisplayName(sb.getTitleAnimation().getFrame(currentFrame));
        } else {
            obj.setDisplayName(sb.getTitle());
        }
        ScoreLines lines = new ScoreLines(player);
        sb.addLines(player,lines);
        int i = lines.size();
        for (String line : lines) {
            Score s = obj.getScore(line);
            s.setScore(i);
            i--;
        }

       if (sb.getTitleAnimation() != null) {
            frameDelay = sb.getTitleAnimation().speed();
        } else {
            obj.setDisplayName(sb.getTitle());
        }
        this.scoreboard = sb;
    }

    /**
     * This method will call the {@link ScoreboardManager#updateScoreboard(Scoreboard)} only if the given scoreboard and the current scoreboard are <b>the same</b>.<br/>
     * in other words, will only update the scoreboard, and won't replace it.
     * @param sb The scoreboard to check
     */
    public void tryUpdate(Scoreboard sb) {
        if (this.scoreboard != null) {
            if (this.scoreboard.equals(sb)) {
                if (this.player.handle != null) {
                    updateScoreboard(sb);
                }
            }
        }
    }

    public void updateCurrentScoreboard() {
        this.updateScoreboard(scoreboard);
    }

    /**
     * Hides the given scoreboard, only if this is the currently displayed scoreboard on the sidebar.
     * @param sb
     */
    public void hide(Scoreboard sb) {
        if (this.scoreboard != null) {
            if (this.scoreboard.equals(sb)) {
                if (player.handle != null) {
                    this.player.handle.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
                }
                this.scoreboard = null;
            }
        }
    }

    public Scoreboard getCurrentScoreboard() {
        return scoreboard;
    }

    public void update() {
        this.updateFrame();
        this.scoreUpdateTick();
    }

    /**
     * Gets called every tick by {@link ExtronPlayer#tick()} to update the scoreboard animated title.
     */
    public void updateFrame() {
        if (player.handle != null) {
            if (scoreboard != null) {
                if (scoreboard.getTitleAnimation() != null) {
                    Objective o = handle.getObjective(scoreboard.getId());
                    if (o != null) {
                        try {
                            o.setDisplayName(scoreboard.getTitleAnimation().getFrame(currentFrame));
                        } catch (Exception ignored) {

                        }
                        if (frameDelay <= 0) {
                            currentFrame++;
                            frameDelay = scoreboard.getTitleAnimation().speed();
                        } else {
                            frameDelay--;
                        }
                        if (currentFrame == scoreboard.getTitleAnimation().frameCount()) {
                            currentFrame = 0;
                        }
                    }
                }
            }
        }
    }

    public org.bukkit.scoreboard.Scoreboard getHandle() {
        return handle;
    }

    /**
     * Gets called every tick by {@link ExtronPlayer#tick()} to update the tab list objective and the bellow name objective.
     */
    public void scoreUpdateTick() {
        if (this.tabUpdater != null && this.tab != null)  {
            int i = tabUpdater.update(this.player);
            Score s = tab.getScore(player.getName());
            s.setScore(i);
        }
        if (this.nameUpdater != null && this.bellowName != null) {
            int i = nameUpdater.update(this.player);
            Score s = bellowName.getScore(player.getName());
            s.setScore(i);
        }
    }

    public void setTabObjective(Objective obj, ScoreUpdater scoreUpdater) {
        this.tab = obj;
        this.tabUpdater = scoreUpdater;
    }

    public void setBellowNameObjective(Objective obj, ScoreUpdater scoreUpdater) {
        this.bellowName = obj;
        this.nameUpdater = scoreUpdater;
    }

    public Objective getTabObjective() {
        return tab;
    }

    public ScoreUpdater getTabUpdater() {
        return tabUpdater;
    }

    public Objective getBellowName() {
        return bellowName;
    }

    public ScoreUpdater getNameUpdater() {
        return nameUpdater;
    }

    public interface ScoreUpdater {
        /**
         * This method should return the integer getValue to display on the tab list or bellow name of the player.
         * @param p
         * @return
         */
        int update(ExtronPlayer p);
    }
}
