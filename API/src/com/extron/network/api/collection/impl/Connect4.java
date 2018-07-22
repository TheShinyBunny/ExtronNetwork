package com.extron.network.api.collection.impl;

import com.extron.network.api.collection.Rarity;
import com.extron.network.api.inventory.Button;
import com.extron.network.api.inventory.InventoryMenu;
import com.extron.network.api.inventory.ItemLore;
import com.extron.network.api.inventory.SimpleButton;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.invites.InvitingGadget;
import com.extron.network.api.utils.tasks.ExtronRunnable;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;

public class Connect4 extends InvitingGadget {
    @Override
    protected String getCommandName() {
        return "c4";
    }

    @Override
    public int getCooldown() {
        return 40;
    }

    @Override
    public Rarity getRarity() {
        return Rarity.LEGENDARY;
    }

    @Override
    public boolean foundInBasicLoot() {
        return false;
    }

    @Override
    public String getId() {
        return "connect4";
    }

    @Override
    public String getDisplayName() {
        return "Connect 4";
    }

    @Override
    public Material getIcon() {
        return Material.SAND;
    }

    @Override
    public int getIconDamage() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "Right click a player to invite them to an epic \"Connect 4\" game!";
    }

    @Override
    protected void onInviteAccepted(ExtronPlayer sender, ExtronPlayer accepted) {
        sender.openInventory(new Menu(new GameInstance(sender,accepted)));
        accepted.openInventory(new Menu(new GameInstance(accepted,sender)));
    }

    private static class Menu extends InventoryMenu {

        public GameInstance game;

        public Menu(GameInstance game) {
            this.game = game;
        }

        @Override
        public void init() {
            if (game.turn != null) {
                if (game.turn.equals(owner)) {
                    this.drawLine(0, false, i -> new SimpleButton(Material.STONE_BUTTON)
                            .setDisplayName("Insert Here")
                            .setLore(ItemLore.create().description("Click to insert the coin here!"))
                            .setAction(e -> {
                                game.insert(i);
                            }));
                }
            }
            this.drawRect(9,9,5,(x,y)->game.board[x][y].createButton(game),true);
        }

        @Override
        public String getTitle() {
            if (game.end) {
                if (game.win) {
                    return "YOU WIN!";
                } else if (game.tie) {
                    return "It's a tie!";
                } else {
                    return game.opponent.getName() + " Won...";
                }
            } if (game.turn == null) {
                return "Connect 4";
            } else if (game.turn.equals(owner)) {
                return "Connect 4 - YOUR TURN";
            } else {
                return "Connect 4 - Opponent's turn";
            }
        }

        @Override
        public int getRows() {
            return 6;
        }

        @Override
        public void onClose() {
            if (!game.done) {
                game.done = true;
                if (game.getOtherInstance() != null) {
                    game.getOtherInstance().done = true;
                    game.opponent.closeInventory();
                }
            }
        }
    }

    private static class GameInstance {
        private ExtronPlayer player;
        private ExtronPlayer opponent;

        private Tile[][] board;
        private int color;
        private ExtronPlayer turn;
        private boolean win;
        private boolean end;
        private boolean tie;
        int endX = 0;
        int endY = 0;
        private boolean done;

        public GameInstance(ExtronPlayer player, ExtronPlayer opponent) {
            this.player = player;
            this.opponent = opponent;
            board = new Tile[9][5];
            for (int i=0;i<9;i++) {
                for (int j=0;j<5;j++) {
                    board[i][j] = new Tile();
                }
            }
            this.color = getOtherInstance() == null ? DyeColor.RED.getWoolData() : DyeColor.BLUE.getWoolData();
            this.turn = getOtherInstance() == null ? opponent : player;
        }

        public void update(GameInstance instance) {
            this.board = instance.board;
            this.turn = instance.turn;
        }

        private GameInstance getOtherInstance() {
            return opponent.getOpenInventory() instanceof Menu && ((Menu) opponent.getOpenInventory()).game.opponent.equals(player) ? ((Menu) opponent.getOpenInventory()).game : null;
        }

        public void insert(int x) {
            turn = null;
            new ExtronRunnable() {
                int i = 0;

                @Override
                public void run() {
                    if (getOtherInstance() == null || !(player.getOpenInventory() instanceof Menu)) {
                        System.out.println("bai");
                        cancel();
                        return;
                    }
                    if (board[x][i].isOccupied()) {
                        if (i == 0) {
                            player.sendMessage(ChatColor.RED + "This column is full!");
                            turn = player;
                            player.getOpenInventory().refresh();
                        } else {
                            setCoin(x,i-1);
                        }
                        cancel();
                        return;
                    } else {
                        if (i == 4) {
                            board[x][3].owner = null;
                            setCoin(x, i);
                            cancel();
                            return;
                        } else {
                            board[x][i].owner = GameInstance.this;
                            if (i > 0) {
                                board[x][i - 1].owner = null;
                            }
                            player.getOpenInventory().refresh();
                            opponent.getOpenInventory().refresh();
                            getOtherInstance().update(GameInstance.this);
                        }
                    }
                    if (i < 5) {
                        i++;
                    } else {
                        cancel();
                    }
                }
            }.timer(10,10);
        }

        private void setCoin(int x, int y) {
            System.out.println("setting coin at " + x + ", " + y);
            board[x][y].owner = GameInstance.this;
            turn = opponent;
            if (getOtherInstance() != null) {
                getOtherInstance().update(GameInstance.this);
                player.getOpenInventory().refresh();
                opponent.getOpenInventory().refresh();
            }
            checkWin();
        }

        private void checkWin() {
            for (int x = 0; x < 9; x++) {
                int ver = 0;
                for (int y = 0; y < 5; y++) {
                    Tile t = board[x][y];
                    if (t.isOwnedBy(this)) {
                        ver++;
                        if (x < 6) {
                            int hor = 1;
                            for (int k = 1; k < 4; k++) {
                                if (board[x + k][y].isOwnedBy(this)) {
                                    hor++;
                                } else {
                                    hor = 0;
                                }
                                if (hor == 4) {
                                    win();
                                    return;
                                }
                            }
                            if (y > 2) {
                                int upDia = 1;
                                for (int k = 1; k < 4; k++) {
                                    if (board[x + k][y - k].isOwnedBy(this)) {
                                        upDia++;
                                    } else {
                                        upDia = 0;
                                    }
                                    if (upDia == 4) {
                                        win();
                                        return;
                                    }
                                }
                            }
                            if (y < 2) {
                                int downDia = 1;
                                for (int k = 1; k < 4; k++) {
                                    if (board[x + k][y + k].isOwnedBy(this)) {
                                        downDia++;
                                    } else {
                                        downDia = 0;
                                    }
                                    if (downDia == 4) {
                                        win();
                                        return;
                                    }
                                }
                            }
                        }
                    } else {
                        ver = 0;
                    }
                    if (ver == 4) {
                        win();
                        return;
                    }
                }
            }
            for (Tile[] t : board) {
                for (Tile t2 : t) {
                    if (!t2.isOccupied()) {
                        return;
                    }
                }
            }
            tie();
        }

        private void tie() {
            this.end = true;
            this.tie = true;
            if (getOtherInstance() != null) {
                getOtherInstance().end = true;
                getOtherInstance().tie = true;
            }
            endGame();
        }

        private void win() {
            System.out.println(player.getName() + " won!");
            this.win = true;
            this.end = true;
            if (getOtherInstance() != null) {
                getOtherInstance().end = true;
                getOtherInstance().win = false;
                turn = null;
                getOtherInstance().turn = null;
                endGame();
            }
        }

        private void endGame() {
            endX = 0;
            endY = 0;
            new ExtronRunnable() {

                @Override
                public void run() {
                    if (player.getOpenInventory() instanceof Menu && opponent.getOpenInventory() instanceof Menu && getOtherInstance() != null) {
                        board[endX][endY].winner = GameInstance.this;
                        getOtherInstance().board[endX][endY].winner = GameInstance.this;
                        player.getOpenInventory().refresh();
                        opponent.getOpenInventory().refresh();
                    } else {
                        this.cancel();
                        return;
                    }
                    endY++;
                    if (endY >= 5) {
                        endX++;
                        endY = 0;
                        if (endX >= 9) {
                            this.cancel();
                            done = true;
                            player.closeInventory();
                            opponent.closeInventory();
                        }
                    }
                }
            }.timer(5,1);
        }
    }

    private static class Tile {

        private GameInstance owner;
        private GameInstance winner;

        public Tile() {
            this.owner = null;
        }

        public boolean isOccupied() {
            return owner != null;
        }

        public boolean isOwnedBy(GameInstance instance) {
            return isOccupied() && instance.player.equals(owner.player);
        }

        public Button createButton(GameInstance instance) {
            return new SimpleButton(Material.STAINED_CLAY)
                    .setDamage(winner == null ? owner == null ? 0 : owner.color : winner == instance ? DyeColor.GREEN.getWoolData() : DyeColor.RED.getWoolData())
                    .setDisplayName(winner == null ? "" : winner == instance ? "YOU WON!" : "You Lost...");
        }
    }
}
