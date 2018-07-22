package com.extron.network.skywars;

import com.extron.network.api.ExtronWorld;
import com.extron.network.api.config.Config;
import com.extron.network.api.game.GameSettings;
import com.extron.network.api.game.GameState;
import com.extron.network.api.game.Team;
import com.extron.network.api.game.helpers.LootEntry;
import com.extron.network.api.game.helpers.LootGenerator;
import com.extron.network.api.game.helpers.LootGroup;
import com.extron.network.api.game.helpers.LootPopulator;
import com.extron.network.api.game.listeners.StartEndListener;
import com.extron.network.api.game.managers.PvPGameManager;
import com.extron.network.api.inventory.base.ExtronStack;
import com.extron.network.api.inventory.base.ItemStackHelper;
import com.extron.network.api.inventory.interactions.IronGolemEgg;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.BlockPos;
import com.extron.network.api.utils.TextUtils;
import com.extron.network.api.utils.tasks.Counter;
import com.extron.network.api.utils.tasks.CounterAction;
import com.extron.network.api.utils.tasks.ExtronRunnable;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionType;

import java.util.*;
import java.util.function.Consumer;

public class SkyWarsManager extends PvPGameManager implements StartEndListener {

    private List<BlockPos> spawns;
    private List<BlockPos> middleChests;
    private Map<BlockPos,List<BlockPos>> spawnChestsMap;

    private Loot spawnLoot;
    private Loot midLoot;

    private List<BlockPos> cages;
    private Counter releaseCountdown;

    public SkyWarsManager(ExtronWorld map, GameSettings settings) {
        super(map, settings);
        spawns = new ArrayList<>();
        middleChests = new ArrayList<>();
        spawnChestsMap = new HashMap<>();
        spawnLoot = new Loot(false);
        midLoot = new Loot(true);
        cages = new ArrayList<>();
    }

    @Override
    protected void onCountdownFinished() {
        int i = 0;
        Collections.shuffle(waiting);
        for (BlockPos pos : spawns) {
            fillCage(pos,Material.GLASS,0);
            this.cages.add(pos);
            if (i < waiting.size()) {
                waiting.get(i).teleport(pos);
            }
            i++;
        }
        releaseCountdown = new Counter(10, -1, 20, "cage_release", null);
        releaseCountdown.start(new CounterAction() {

            @Override
            public void onCounterLoop(Counter c) {
                messageAll(waiting,ChatColor.YELLOW + "Cages will be opened in " + ChatColor.RED + c.getCurrent() + ChatColor.YELLOW + " " + TextUtils.addNeededS(c.getCurrent(),"second") + "!");
            }

            @Override
            public void onCounterFinished(Counter c) {
                start();
                for (BlockPos pos : cages) {
                    fillCage(pos,Material.AIR,0);
                }
                refill();
                ((ExtronRunnable) () -> alivePlayers.forEach(p -> p.invulnerable = false)).delay(60);
            }
        });
    }

    @Override
    public boolean setInvulnerableOnStart() {
        return true;
    }

    @Override
    protected void startingIn(int seconds) {
        if (seconds % 10 == 0 || seconds < 6) {
            messageAll(waiting, ChatColor.YELLOW + "The game starts in " + numberChatColor(seconds, false) + seconds + " " + ChatColor.YELLOW + TextUtils.addNeededS(seconds, "second") + "!");
            if (seconds % 10 == 0) {
                titleAll(waiting,ChatColor.AQUA + "Starting In",numberChatColor(seconds,true) + "" + seconds);
            } else {
                titleAll(waiting,ChatColor.AQUA + "Starting In",numberChatColor(seconds,true) + "" + seconds, 0,23,0);
            }
        }
        updateAllScoreboards(waiting);
    }

    private ChatColor numberChatColor(int seconds, boolean title) {
        if (title) {
            return seconds % 10 == 0 ? ChatColor.GREEN : seconds < 6 && seconds > 2 ? ChatColor.YELLOW : ChatColor.RED;
        } else {
            return seconds % 10 == 0 ? ChatColor.GOLD : seconds < 6 && seconds > 2 ? ChatColor.RED : ChatColor.DARK_RED;
        }
    }

    @Override
    protected void sendJoinMessage(ExtronPlayer p, int players, int maxPlayers) {
        messageAll(waiting,p.getDisplayName() + ChatColor.GREEN + " has joined the game! " + ChatColor.RED + "(" + ChatColor.YELLOW + players + ChatColor.RED + "/" + ChatColor.YELLOW + maxPlayers + ChatColor.RED + ")");
    }

    @Override
    public void end() {
        setState(GameState.ENDED);
        Team t = getWinningTeam();
        if (t == null) {
            titleAll(allPlayers,ChatColor.YELLOW + "" + ChatColor.BOLD + "It's a Draw!","");
        } else {
            printTeamAndTopKills(t);
            titleAll(t.getPlayers(),ChatColor.GOLD + "" + ChatColor.BOLD + "YOU WIN!!","");
        }
    }

    @Override
    protected void sendPreGameQuitMessage(ExtronPlayer p) {
        messageAll(waiting,p.getDisplayName() + ChatColor.RED + " has quit.");
    }

    @Override
    protected void sendDeathTitle(ExtronPlayer p) {
        p.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "YOU DIED!",ChatColor.GRAY + "You are now a spectator");
    }

    @Override
    protected void sendFinalDeathTitle(ExtronPlayer p) {
        sendDeathTitle(p);
    }

    @Override
    public boolean isFinalKill(ExtronPlayer p) {
        return true;
    }

    @Override
    protected void setSpectator(ExtronPlayer p) {
        System.out.println("set player " + p.getName() + " to spectator!");
        p.enableFlight(true);
        p.handle.setFlying(true);
        p.setInvisible(true);
        p.invulnerable = true;
        hidePlayer(p,alivePlayers);
        p.setHealth(20);
        p.setCollisionRule(false);
    }

    @Override
    protected boolean canPlayerRejoin(ExtronPlayer p) {
        return false;
    }

    @Override
    protected void sendTeamEliminatedMessage(Team team) {

    }

    @Override
    public boolean isGoingForever() {
        return false;
    }

    @Override
    public void sendMessage(ExtronPlayer p, String message) {
        messageAll(allPlayers,p.getDisplayName() + ": " + message);
    }

    @Override
    public void setupPlayerOnStart(ExtronPlayer p) {
        p.give(Material.WOOD_PICKAXE);
        p.give(Material.WOOD_SPADE);
        p.give(Material.WOOD_AXE);
    }

    @Override
    protected void loadFromConfig(Config config) {
        if (config.getStringList("island_spawns") != null) {
            for (String bp :  config.getStringList("island_spawns")) {
                BlockPos pos = BlockPos.fromString(bp);
                if (pos != null) {
                    spawns.add(pos);
                    spawnChestsMap.put(pos,new ArrayList<>());
                }
            }
        }
        if (config.getStringList("mid_chests") != null) {
            for (String bp :  config.getStringList("mid_chests")) {
                BlockPos pos = BlockPos.fromString(bp);
                if (pos != null) {
                    middleChests.add(pos);
                }
            }
        }
        if (config.getStringList("spawn_chests") != null) {
            for (String bp :  config.getStringList("spawn_chests")) {
                BlockPos pos = BlockPos.fromString(bp);
                if (pos != null) {
                    BlockPos spawn = getClosestSpawn(pos);
                    if (spawn != null) {
                        if (spawnChestsMap.containsKey(spawn)) {
                            spawnChestsMap.get(spawn).add(pos);
                        }
                    }
                }
            }
        }
    }

    private BlockPos getClosestSpawn(BlockPos pos) {
        BlockPos spawn = null;
        for (BlockPos p : spawns) {
            if (spawn == null || p.distanceTo(pos) < spawn.distanceTo(pos)) {
                spawn = p;
            }
        }
        return spawn;
    }

    private void fillCage(BlockPos pos, Material type, int data) {
        System.out.println("filling cage with " + type.name());
        int i = game.getPlayersInTeam();
        map.fillBlocks(pos.clone().subtract(i,1,i),pos.clone().add(i,3,i),type,data,true);
    }

    public void refill() {
        System.out.println("refilling");
        for (List<BlockPos> list : spawnChestsMap.values()) {
            LootGenerator generator = new LootGenerator(15 + game.getPlayersInTeam(), 25 + (game.getPlayersInTeam() * 2));
            generator.withItemBonus(game.getPlayersInTeam() - 1);
            for (BlockPos pos : list) {
                generator.addChest(pos);
            }
            System.out.println("populating spawn chests");
            generator.populateChests(spawnLoot);
        }
        LootGenerator generator = new LootGenerator(15, 20);
        generator.withItemBonus(game.getPlayersInTeam() - 1);
        for (BlockPos pos : middleChests) {
            generator.addChest(pos);
        }
        System.out.println("populating mid chests");
        generator.populateChests(midLoot);
    }

    public boolean isInCages() {
        return releaseCountdown != null;
    }

    @Override
    public void onGameStarted() {

    }

    @Override
    public void onGameEnded() {

    }

    static class Loot implements LootPopulator {

        private final boolean mid;

        Loot(boolean mid) {
            this.mid = mid;
        }

        @Override
        public void populate(Consumer<LootEntry> c) {
            c.accept(new LootEntry(new ExtronStack(Material.STONE,mid ? 32 : 16),mid ? 2 : 10,LootGroup.BLOCKS));
            c.accept(new LootEntry(new ExtronStack(Material.SANDSTONE,mid ? 24 : 10),mid ? 1 : 7,LootGroup.BLOCKS));
            c.accept(new LootEntry(new IronGolemEgg(),1,LootGroup.UTILS)); // TODO: 7/10/2018 change this
            if (!mid) {
                c.accept(new LootEntry(new ExtronStack(Material.IRON_CHESTPLATE), 9, LootGroup.ARMOR));
                c.accept(new LootEntry(new ExtronStack(Material.IRON_HELMET), 8, LootGroup.ARMOR));
                c.accept(new LootEntry(new ExtronStack(Material.IRON_LEGGINGS), 8, LootGroup.ARMOR));
                c.accept(new LootEntry(new ExtronStack(Material.IRON_BOOTS), 9, LootGroup.ARMOR));
            }
            c.accept(new LootEntry(new ExtronStack(Material.DIAMOND_HELMET),6,LootGroup.ARMOR));
            c.accept(new LootEntry(new ExtronStack(Material.DIAMOND_CHESTPLATE),mid ? 4 : 2,LootGroup.ARMOR));
            c.accept(new LootEntry(new ExtronStack(Material.DIAMOND_LEGGINGS),mid ? 5 : 4,LootGroup.ARMOR));
            c.accept(new LootEntry(new ExtronStack(Material.DIAMOND_BOOTS),mid ? 6 : 5,LootGroup.ARMOR));
            if (!mid) {
                c.accept(new LootEntry(new ExtronStack(Material.IRON_SWORD),10,LootGroup.WEAPONS));
                ExtronStack stone_sword = new ExtronStack(Material.STONE_SWORD);
                stone_sword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
                c.accept(new LootEntry(stone_sword, 6, LootGroup.WEAPONS));
            }
            c.accept(new LootEntry(new ExtronStack(Material.DIAMOND_SWORD),6,LootGroup.WEAPONS));
            ExtronStack ench_diamond = new ExtronStack(Material.DIAMOND_SWORD);
            ench_diamond.addEnchantment(Enchantment.DAMAGE_ALL, 1);
            c.accept(new LootEntry(ench_diamond, mid ? 7 : 2, LootGroup.WEAPONS));
            if (!mid) {
                c.accept(new LootEntry(new ExtronStack(Material.BOW), 3, LootGroup.BOW));
            }
            ExtronStack ench_bow = new ExtronStack(Material.BOW);
            ench_bow.addEnchantment(Enchantment.ARROW_DAMAGE,mid ? 3 : 1);
            c.accept(new LootEntry(ench_bow,mid ? 4 : 3, LootGroup.BOW));
            c.accept(new LootEntry(ItemStackHelper.createPotion(PotionType.SPEED),3,LootGroup.POTIONS));
            c.accept(new LootEntry(ItemStackHelper.createPotion(PotionType.REGEN,false,false,true),3,LootGroup.POTIONS));
            if (mid) {
                c.accept(new LootEntry(new ExtronStack(Material.ENDER_PEARL,new Random().nextInt(4)+3),7));
                c.accept(new LootEntry(new ExtronStack(Material.GOLDEN_APPLE,5),8));
            }
        }
    }

}
