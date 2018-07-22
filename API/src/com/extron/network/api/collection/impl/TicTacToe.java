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

public class TicTacToe extends InvitingGadget {

    @Override
    public int getCooldown() {
        return 60;
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
        return "tictactoe";
    }

    @Override
    public String getDisplayName() {
        return "Tic Tac Toe";
    }

    @Override
    public Material getIcon() {
        return Material.COMMAND;
    }

    @Override
    public int getIconDamage() {
        return 0;
    }

    @Override
    public String getDescription() {
        return "Right click another player to invite them to an amazing \"Tic Tac Toe\" game!";
    }

    @Override
    protected String getCommandName() {
        return "ttt";
    }

    @Override
    protected void onInviteAccepted(ExtronPlayer sender, ExtronPlayer accepted) {
        sender.openInventory(new Menu(new GameInstance(sender,accepted)));
        accepted.openInventory(new Menu(new GameInstance(accepted,sender)));
    }

    private static class Menu extends InventoryMenu {

        private GameInstance game;

        public Menu(GameInstance game) {
            this.game = game;
        }

        @Override
        public void init() {
            this.drawRect(12,3,3,(x,y)-> game.tiles[x][y].createButton(game,x,y),true);
            if (game.end) {
                if (game.win) {
                    this.drawRect(0,9,5,(x,y)-> new SimpleButton(Material.WOOL)
                            .setDamage(game.endGameTime % 2 == 0 ? DyeColor.GREEN.getWoolData() : DyeColor.LIME.getWoolData())
                            .setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "YOU WIN!"),false);
                } else if (game.tie) {
                    this.drawRect(0,9,5,(x,y)-> new SimpleButton(Material.WOOL)
                            .setDamage(DyeColor.YELLOW.getWoolData())
                            .setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "TIE"),true);
                } else {
                    this.drawRect(0,9,5,(x,y)-> new SimpleButton(Material.WOOL)
                            .setDamage(game.endGameTime % 2 == 0 ? DyeColor.RED.getWoolData() : DyeColor.PINK.getWoolData())
                            .setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "YOU LOST..."),true);
                }
            } else {
                createButton(36, Material.SKULL_ITEM)
                        .setSkullOwner(game.opponent)
                        .setDisplayName("Opponent:")
                        .setLore(ItemLore.create()
                                .line(game.opponent.getName()));
                createButton(44,Material.BARRIER)
                        .setDisplayName(ChatColor.RED + "END GAME")
                        .setLore(ItemLore.create().clickTo("finish this game"))
                        .setAction(e->{
                            e.getPlayer().closeInventory();
                        });
            }
        }

        @Override
        public String getTitle() {
            if (game.end) {
                if (game.win) {
                    return "YOU WIN!";
                } else if (game.tie) {
                    return "It's a Tie!";
                } else {
                    return "You Lost...";
                }
            }
            if (game.isMyTurn()) {
                return "Tic Tac Toe - YOUR TURN";
            } else {
                return "Tic Tac Toe - Opponent's turn";
            }
        }

        @Override
        public int getRows() {
            return 5;
        }

        @Override
        public void onClose() {
            if (game.getOtherInstance() != null && game.endTimer != null) {
                game.endTimer.cancel();
                game.opponent.closeInventory();
            }
        }
    }

    private static class GameInstance {

        private final ExtronPlayer player;
        private final ExtronPlayer opponent;
        private Tile[][] tiles;
        private boolean end;
        private boolean win;
        private boolean tie;
        private int endGameTime;
        private ExtronPlayer turn;
        private ExtronRunnable endTimer;

        public GameInstance(ExtronPlayer p, ExtronPlayer opponent) {
            tiles = new Tile[3][3];
            for (int i=0;i<3;i++) {
                for (int j=0;j<3;j++) {
                    tiles[i][j] = new Tile();
                }
            }
            this.player = p;
            this.opponent = opponent;
            if (getOtherInstance() == null) {
                turn = opponent;
            } else {
                turn = player;
            }
        }

        public ExtronPlayer getPlayer() {
            return player;
        }

        public ExtronPlayer getOpponent() {
            return opponent;
        }

        public void occupy(int x, int y) {
            if (!isMyTurn()) {
                player.sendMessage(ChatColor.RED + "It's not your turn!");
            } else if (this.tiles[x][y].isOccupied()) {
                player.sendMessage(ChatColor.RED + "This tile is occupied!");
            } else if (getOtherInstance() != null) {
                this.tiles[x][y].owner = this;
                turn = opponent;
                getOtherInstance().update(this);
                opponent.getOpenInventory().refresh();
            }
        }

        public void update(GameInstance instance) {
            this.tiles = instance.tiles;
            this.turn = instance.turn;
        }

        private GameInstance getOtherInstance() {
            return opponent.getOpenInventory() instanceof Menu && ((Menu) opponent.getOpenInventory()).game.opponent.equals(player) ? ((Menu) opponent.getOpenInventory()).game : null;
        }

        public void checkWin() {
            for (int i = 0; i < 3; i++) {
                boolean b = true;
                for (int j = 0; j < 3; j++) {
                    if (!tiles[i][j].isOwnedBy(this)) {
                        b = false;
                    }
                }
                if (b) {
                    win();
                    return;
                }
            }
            for (int i = 0; i < 3; i++) {
                boolean b = true;
                for (int j = 0; j < 3; j++) {
                    if (!tiles[j][i].isOwnedBy(this)) {
                        b = false;
                    }
                }
                if (b) {
                    win();
                    return;
                }
            }
            boolean b = true;
            for (int i = 0; i < 3; i++) {
                if (!tiles[i][i].isOwnedBy(this)) {
                    b = false;
                }
            }
            if (b) {
                win();
                return;
            }
            b = true;
            for (int i = 0; i < 3; i++) {
                if (!tiles[i][2 - i].isOwnedBy(this)) {
                    b = false;
                }
            }
            if (b) {
                win();
            }
            for (Tile[] t : tiles) {
                for (Tile t2 : t) {
                    if (!t2.isOccupied()) {
                        return;
                    }
                }
            }
            tie();
        }

        private void tie() {
            this.win = false;
            this.end = true;
            this.tie = true;
            if (getOtherInstance() != null) {
                getOtherInstance().end = true;
                getOtherInstance().win = false;
                getOtherInstance().tie = true;
            }
            endGame();
        }

        private void win() {
            this.win = true;
            this.end = true;
            if (getOtherInstance() != null) {
                getOtherInstance().end = true;
                getOtherInstance().win = false;
            }
            endGame();
        }

        private void endGame() {
            endTimer = new ExtronRunnable() {

                @Override
                public void run() {
                    if (player.getOpenInventory() instanceof Menu && opponent.getOpenInventory() instanceof Menu && getOtherInstance() != null) {
                        player.getOpenInventory().refresh();
                        opponent.getOpenInventory().refresh();
                        if (endGameTime >= 10) {
                            player.closeInventory();
                        } else {
                            getOtherInstance().endGameTime++;
                        }
                    } else {
                        this.cancel();
                    }
                    endGameTime++;
                }
            }.timer(10,10);
        }

        public boolean isMyTurn() {
            return turn.equals(player);
        }
    }

    private static class Tile {

        private GameInstance owner;

        public Tile() {
            this.owner = null;
        }

        public boolean isOccupied() {
            return owner != null;
        }

        public GameInstance getOwner() {
            return owner;
        }

        public boolean isOwnedBy(GameInstance instance) {
            return isOccupied() && instance.player.equals(owner.player);
        }

        public Button createButton(GameInstance instance, int x, int y) {
            return new SimpleButton(Material.STAINED_CLAY)
                    .setDamage(isOccupied() ? isOwnedBy(instance) ? DyeColor.BLUE.getWoolData() : DyeColor.RED.getWoolData() : 0)
                    .setDisplayName("")
                    .setAction((e)->{
                        instance.occupy(x,y);
                        instance.checkWin();
                    });
        }
    }
}
