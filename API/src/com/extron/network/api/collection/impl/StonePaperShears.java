package com.extron.network.api.collection.impl;

import com.extron.network.api.collection.Rarity;
import com.extron.network.api.inventory.*;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.TextUtils;
import com.extron.network.api.utils.invites.*;
import com.extron.network.api.utils.tasks.ExtronRunnable;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class StonePaperShears extends InvitingGadget {

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
        return "stone_paper_shears";
    }

    @Override
    public String getDisplayName() {
        return "Stone Paper Shears";
    }

    @Override
    public Material getIcon() {
        return Material.COBBLESTONE;
    }

    @Override
    public int getIconDamage() {
        return 0;
    }

    @Override
    public String getDescription() {
        return "Right click another player with this gadget to invite them to an incredible \"Stone, Paper, Shears\" 1v1!";
    }

    @Override
    protected void onInviteAccepted(ExtronPlayer sender, ExtronPlayer accepted) {
        sender.openInventory(new Menu(new GameInstance(sender,accepted)));
        accepted.openInventory(new Menu(new GameInstance(accepted,sender)));
    }

    @Override
    protected String getCommandName() {
        return "sps";
    }

    private static class Menu extends InventoryMenu {

        private final GameInstance game;
        private int winner;

        public Menu(GameInstance instance) {
            this.game = instance;
        }

        @Override
        public void init() {
            switch (game.phase) {
                case CHOOSE:
                    SmartGrid grid = new SmartGrid();
                    for (EnumType type : EnumType.values()) {
                        grid.addButton(new SimpleButton(type.icon)
                                .setDisplayName(ChatColor.YELLOW + TextUtils.capitalize(type.name()))
                                .setLore(ItemLore.create().description("Click to play with " + type))
                                .setAction(e->{
                                    game.selected = type;
                                    game.nextPhase();
                                }));
                    }
                    grid.addTo(this);
                    break;
                case WAITING:
                    if (game.getOtherInstance() != null && game.getOtherInstance().phase == GamePhase.WAITING) {
                        game.phase = GamePhase.REVEALED;
                        game.getOtherInstance().phase = GamePhase.REVEALED;
                        refresh();
                        game.opponent.getOpenInventory().refresh();
                        new ExtronRunnable() {

                            @Override
                            public void run() {
                                if (game.getOtherInstance() == null) {
                                    this.cancel();
                                    return;
                                }
                                if (game.wins >= 3 || game.loses >= 3) {
                                    game.endGame(null,true);
                                    game.getOtherInstance().endGame(null,true);
                                    return;
                                }
                                game.nextPhase();
                                game.getOtherInstance().nextPhase();
                                refresh();
                                game.opponent.getOpenInventory().refresh();
                                if (game.phase == GamePhase.CHOOSE) {
                                    this.cancel();
                                }
                            }
                        }.timer(60,20);
                        return;
                    }
                    createButton(20,game.selected.icon)
                            .setDisplayName(ChatColor.GREEN + "You selected " + ChatColor.GOLD + game.selected.name());
                    this.drawLine(4,true,(i)->new EmptyButton(Material.COBBLE_WALL));
                    createButton(24,Material.SKULL_ITEM)
                            .setSkullOwner("MHF_Question")
                            .setDisplayName(ChatColor.RED + "????");
                    break;
                case REVEALED:
                    System.out.println("called revealed switch");
                    EnumType mySel = game.selected;
                    EnumType oppSel = game.getOtherInstance().selected;
                    createButton(20,mySel.icon)
                            .setDisplayName(ChatColor.GREEN + "You chose " + ChatColor.YELLOW + mySel.name());
                    createButton(24,oppSel.icon)
                            .setDisplayName(ChatColor.GREEN + game.opponent.getName() + " chose " + ChatColor.YELLOW + oppSel.name());
                    winner = getWinner(mySel,oppSel);
                    if (winner > 0) {
                        this.drawRect(winner, 3, 3, (x,y) -> new SimpleButton(Material.WOOL)
                                .setDamage(5)
                                .setDisplayName(ChatColor.GREEN + "WINNER"),false);
                        if (winner == 10) game.wins++;
                        if (winner == 14) game.loses++;
                    } else {
                        this.drawRect(10,7,3, (x,y)->new SimpleButton(Material.WOOL)
                                .setDamage(4)
                                .setDisplayName(ChatColor.YELLOW + "TIE"),false);
                        game.ties++;
                        System.out.println("the ties are updated for " + owner.getName() + " to " + game.ties);
                    }
                    break;
                case STARTING3:
                case STARTING2:
                case STARTING1:
                    this.drawNumber(game.phase.getNumber(),Material.WOOL,game.phase.getWoolDamage(),3);
                    break;
            }
            createButton(0, Material.SKULL_ITEM)
                    .setSkullOwner(game.opponent)
                    .setDisplayName("Opponent:")
                    .setLore(ItemLore.create()
                            .line(game.opponent.getName()));
            createButton(36,Material.BOOK)
                    .setDisplayName("Stats")
                    .setLore(ItemLore.create().empty().parameter("Wins",game.wins).parameter("Loses",game.loses).parameter("Ties",game.ties).empty());
            createButton(44,Material.BARRIER)
                    .setDisplayName(ChatColor.RED + "END GAME")
                    .setLore(ItemLore.create().clickTo("finish this game"))
                    .setAction(e->{
                        game.endGame(owner,true);
                        game.getOtherInstance().endGame(owner,true);
                    });
            System.out.println("end of init");
        }

        private int getWinner(EnumType sel1, EnumType sel2) {
            if (sel1 == sel2) {
                return -1;
            }
            if (sel1 == EnumType.STONE && sel2 == EnumType.SHEARS || sel1 == EnumType.SHEARS && sel2 == EnumType.PAPER || sel1 == EnumType.PAPER && sel2 == EnumType.STONE) {
                return 10;
            }
            if (sel1 == EnumType.SHEARS && sel2 == EnumType.STONE || sel1 == EnumType.PAPER && sel2 == EnumType.SHEARS || sel1 == EnumType.STONE && sel2 == EnumType.PAPER) {
                return 14;
            }
            return -1;
        }

        @Override
        public String getTitle() {
            if (game.phase == GamePhase.REVEALED) {
                if (winner == 10) {
                    return "YOU WIN!";
                } else if (winner == 14) {
                    return "You Lost...";
                } else {
                    return "It's a tie!";
                }
            }
            return "Stone Paper Shears";
        }

        @Override
        public int getRows() {
            return 5;
        }

        @Override
        public void onClose() {
            if (game.getOtherInstance() != null && !game.getOtherInstance().hasClosed) {
                game.endGame(owner,false);
                game.getOtherInstance().endGame(owner,true);
            }
        }
    }

    private static class GameInstance {

        private final ExtronPlayer player;
        private ExtronPlayer opponent;

        private int wins;
        private int loses;
        private int ties;
        private GamePhase phase;
        private EnumType selected = EnumType.STONE;
        private boolean hasClosed;

        public GameInstance(ExtronPlayer p, ExtronPlayer opponent) {
            phase = GamePhase.CHOOSE;
            this.player = p;
            this.opponent = opponent;
        }

        public void nextPhase() {
            int i = phase.ordinal();
            i++;
            i%=GamePhase.values().length;
            this.phase = GamePhase.values()[i];
            System.out.println("now on phase " + phase);
        }

        public GameInstance getOtherInstance() {
            return opponent.getOpenInventory() instanceof Menu && ((Menu) opponent.getOpenInventory()).game.opponent.equals(player) ? ((Menu) opponent.getOpenInventory()).game : null;
        }

        public void endGame(ExtronPlayer p, boolean closeInv) {
            hasClosed = true;
            if (p == null) {
                player.sendMessage(ChatColor.RED + "The game has ended.");
            } else {
                if (!p.equals(player)) {
                    player.sendMessage(ChatColor.RED + p.getName() + " has ended the game.");
                }
            }
            if (closeInv && player.getOpenInventory() instanceof Menu) {
                player.closeInventory();
            }
            if (wins > loses) {
                player.sendMessage(ChatColor.GREEN + "You won the Stone Paper Shears against " + opponent.getName() + "!");
            } else if (wins < loses) {
                player.sendMessage(ChatColor.RED + "You lost the Stone Paper Shears against " + opponent.getName() + ".");
            } else {
                player.sendMessage(ChatColor.YELLOW + "You tied in the Stone Paper Shears game against " + opponent.getName() + ".");
            }
            player.sendMessage(ChatColor.GREEN + "W" + ChatColor.RESET + "/" + ChatColor.RED + "L" + ChatColor.RESET + "/" + ChatColor.YELLOW + "T" + ChatColor.RESET + " = " + wins + " wins, " + loses + " loses and " + ties + " ties.");
        }
    }

    public enum GamePhase {
        CHOOSE, WAITING, REVEALED, STARTING3, STARTING2, STARTING1;

        public int getNumber() {
            return this == STARTING1 ? 1 : this == STARTING2 ? 2 : this == STARTING3 ? 3 : 0;
        }

        public int getWoolDamage() {
            return this == STARTING1 ? 5 : this == STARTING2 ? 4 : this == STARTING3 ? 14 : 0;
        }
    }

    public enum EnumType {
        STONE(Material.COBBLESTONE), PAPER(Material.PAPER), SHEARS(Material.SHEARS);

        private final Material icon;

        EnumType(Material icon) {
            this.icon = icon;
        }
    }
}
