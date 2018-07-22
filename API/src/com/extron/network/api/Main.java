package com.extron.network.api;

import com.extron.network.api.collection.Collectible;
import com.extron.network.api.collection.CollectibleType;
import com.extron.network.api.collection.Rarity;
import com.extron.network.api.collection.impl.*;
import com.extron.network.api.collection.loot.LootBox;
import com.extron.network.api.collection.loot.LootBoxManager;
import com.extron.network.api.collection.pet.Pet;
import com.extron.network.api.collection.pet.PetSimple;
import com.extron.network.api.command.Command;
import com.extron.network.api.command.CommandManager;
import com.extron.network.api.command.defaults.*;
import com.extron.network.api.command.defaults.admin.*;
import com.extron.network.api.command.defaults.trolls.*;
import com.extron.network.api.command.defaults.utils.*;
import com.extron.network.api.config.Config;
import com.extron.network.api.config.ConfigFolder;
import com.extron.network.api.data.DataTable;
import com.extron.network.api.data.DatabaseManager;
import com.extron.network.api.entity.BasicEntity;
import com.extron.network.api.entity.ExtronEntity;
import com.extron.network.api.event.EventManager;
import com.extron.network.api.event.network.ExtronLoadedEvent;
import com.extron.network.api.game.Game;
import com.extron.network.api.game.GameMode;
import com.extron.network.api.hologram.Hologram;
import com.extron.network.api.hologram.PlayerHologram;
import com.extron.network.api.inventory.base.ExtronStack;
import com.extron.network.api.inventory.base.ItemStackHelper;
import com.extron.network.api.inventory.interactions.StackInteractAction;
import com.extron.network.api.nick.skin.SkinManager;
import com.extron.network.api.party.CombinationRequest;
import com.extron.network.api.party.PartyInvite;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.players.PlayerList;
import com.extron.network.api.scoreboard.MainScoreboard;
import com.extron.network.api.scoreboard.Scoreboard;
import com.extron.network.api.stats.Statistic;
import com.extron.network.api.utils.ListUtils;
import com.extron.network.api.utils.Poll;
import com.extron.network.api.utils.VoidGenerator;
import com.extron.network.api.utils.tasks.TaskManager;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.AsyncCatcher;

import java.io.File;
import java.util.*;

/**
 * The <code>Main</code> class is responsible of managing the entire ExtronNetwork API.
 * Here you should register any scoreboard, game, player holograms, statistic or commands (commands can be registered via {@link #getCommandManager()} -> {@link CommandManager#register(Command)})
 * or just using {@link #registerCommand(Command)}.
 * Almost all methods in this class are static, for easier access.
 */
public class Main extends JavaPlugin {

    public static final String WEBSITE_ADDRESS = "www.ExtronWeb.com";
    /**
     * Whether the Main plugin is completely loaded or not. Similar to a static version of {@link JavaPlugin#isEnabled()},
     * but will be <code>true</code> only after ALL plugins in the server are enabled.
     */
    public static boolean LOADED = false;

    /**
     * The instance of the <code>JavaPlugin</code>. Not very useful since almost all methods in the Main class are static.
     */
    public static Main INSTANCE;

    private static TaskManager taskManager;
    private static CommandManager commandManager;
    private static List<PartyInvite> partyInvites;
    private static List<CombinationRequest> partyCombinations;
    private static ExtronWorld mainLobby;
    private static List<ExtronWorld> worlds;
    private static ConfigFolder worldsFolder;
    private static Config mainConfig;
    private static List<Game> games;
    private static DatabaseManager databaseManager;
    private static DataTable<ExtronPlayer> playerDataTable;
    private static List<Statistic> statistics;
    private static List<PlayerHologram> playerHolograms;
    private static List<Scoreboard> scoreboards;
    private static Map<CollectibleType,List<Collectible>> collectibles;
    private static Poll currentPoll;
    public static List<Hologram> holograms;
    private static List<StackInteractAction> interactActions;


    @Override
    public void onEnable() {
        INSTANCE = this;
        AsyncCatcher.enabled = false;
        commandManager = new CommandManager();
        taskManager = new TaskManager();
        holograms = new ArrayList<>();
        interactActions = new ArrayList<>();
        partyInvites = new ArrayList<>();
        partyCombinations = new ArrayList<>();
        collectibles = new HashMap<>();
        worlds = new ArrayList<>();
        mainConfig = new Config("config");
        databaseManager = new DatabaseManager();
        SkinManager.init();
        statistics = new ArrayList<>();
        registerConfigStringOverrides();
        registerDefaultCommands();
        registerGeneralStats();
        playerHolograms = new ArrayList<>();
        games = new ArrayList<>();
        scoreboards = new ArrayList<>();
        EventManager.register();
        registerScoreboard(new MainScoreboard());
        registerCollectibles();
        worldsFolder = new ConfigFolder("worlds");
        for (Config c : worldsFolder) {
            loadWorld(c);
        }
        for (World w : Bukkit.getWorlds()) {
            if (getWorld(w) == null) {
                ExtronWorld world = ExtronWorld.createFromBukkit(w);
                if (!worlds.contains(world)) {
                    worlds.add(world);
                }
            }
        }
        if (mainConfig.get("main_lobby") == null) {
            mainConfig.set("main_lobby","world");
            mainConfig.save();
        }

        mainLobby = getWorld(mainConfig.getString("main_lobby"));
        new BukkitRunnable() {
            @Override
            public void run() {
                if (isEnabled()) {
                    try {
                        Main.this.tick();
                    } catch (Exception e) {
                        System.out.println("an error has occurred while performing server tick:");
                        e.printStackTrace();
                    }
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(Main.INSTANCE,0,1);
    }

    private static void registerCollectibles() {
        registerCollectible(new CakeFountain());
        registerCollectible(new Launcher());
        registerCollectible(new LuckCookie());
        registerCollectible(new RocketShip());
        registerCollectible(new CommonLootBox());
        registerCollectible(new SpecialLootBox());
        registerCollectible(new RidingPearl());
        registerCollectible(new StonePaperShears());
        registerCollectible(new TicTacToe());
        registerCollectible(new Connect4());
        registerCollectible(new FireWaves());
        registerCollectible(new BasicLootBox());
        registerCollectible(new AdvancedLootBox());
        registerCollectible(new SlimePad());
        Pet.registerPets();
    }

    private static void registerGeneralStats() {
        registerStatistic("time_played","Minutes Played");
        registerStatistic("chat_messages","Chat Messages Sent");
        registerStatistic("times_login","Times Logged In");
    }

    private static void registerDefaultCommands() {
        registerCommand(new CommandHelp(null));
        registerCommand(new CommandTitleAll());
        registerCommand(new CommandItem());
        //registerCommand(new CommandHologram());
        registerCommand(new CommandBan());
        registerCommand(new CommandChat());
        registerCommand(new CommandCoins());
        registerCommand(new CommandCreateWorld(true));
        registerCommand(new CommandFakeChat());
        registerCommand(new CommandFakeOP());
        registerCommand(new CommandLobby());
        registerCommand(new CommandMessage());
        registerCommand(new CommandParkour());
        registerCommand(new CommandParkour.Checkpoint());
        registerCommand(new CommandParkour.ResetParkour());
        registerCommand(new CommandProfile());
        registerCommand(new CommandReply());
        registerCommand(new CommandSetRank());
        registerCommand(new CommandWorld());
        registerCommand(new CommandXP());
        registerCommand(new CommandParty());
        registerCommand(new CommandLightning());
        registerCommand(new CommandTrap());
        registerCommand(new CommandVanish());
        registerCommand(new CommandKillAll());
        registerCommand(new CommandSpawnMob());
        registerCommand(new CommandTpHere());
        registerCommand(new CommandNick());
        registerCommand(new CommandLootBox());
        registerCommand(new CommandUnban());
        registerCommand(new CommandPlay());
        registerCommand(new CommandMap());
        registerCommand(new CommandGame());
        registerCommand(new CommandCollectible());
        registerCommand(new CommandCooldown());
        registerCommand(new CommandFind());
    }

    private static void registerConfigStringOverrides() {
        Config.registerStringConverter(Location.class,l->Config.encodeLocation(l,true,true));
    }

    @Override
    public void onDisable() {
        for (Hologram h : holograms) {
            h.despawn();
        }
        worlds.forEach(ExtronWorld::despawnHolograms);
        playerHolograms.forEach(PlayerHologram::despawn);
        hideScoreboards("main_scoreboard");
        databaseManager.closeDB();
    }

    private void tick() {
        if (!LOADED) {
            boolean flag = true;
            for (Plugin p : Bukkit.getPluginManager().getPlugins()) {
                if (!p.isEnabled()) {
                    flag = false;
                }
            }
            LOADED = flag;
            if (LOADED) {
                //commandManager.unregisterAllVanilla();
                playerDataTable = new JsonDataTable<>("players");
                databaseManager.addTable(playerDataTable);
                try {
                    /*challengeSystem = new ChallengeSystem();
                    challengeSystem.init();*/
                    databaseManager.setupDB();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                PlayerList.init();
                //console = new ConsolePlayer("consoleextronplayer");
                EventManager.callEvent(new ExtronLoadedEvent());
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Extron Network has finished loading!");
            } else {
                return;
            }
        }
        for (ExtronPlayer p : PlayerList.getOnlinePlayers()) {
            p.tick();
        }
        try {
            taskManager.tick();
        } catch (Exception e) {
            System.out.println("error in server tick:");
            e.printStackTrace();
        }
        for (ExtronWorld w : worlds) {
            w.tick();
        }
    }

    /**
     * @return The ExtronNetwork {@link CommandManager}. A class responsible of managing, handling, processing and executing of commands.
     */
    public static CommandManager getCommandManager() {
        return commandManager;
    }

    /**
     * Gets an ExtronPlayer by name. The player can be online or offline.
     * @param name The name to search for
     * @return The ExtronPlayer that have this name.
     */
    public static ExtronPlayer getPlayer(String name) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(name);
        if (op == null) {
            return getOnlinePlayer(name);
        }
        return getExtronPlayer(op);
    }

    private static ExtronPlayer getExtronPlayer(OfflinePlayer player) {
        return PlayerList.getPlayer(player);
    }

    public static ExtronPlayer getOnlinePlayer(String name) {
        return PlayerList.getOnlinePlayer(name);
    }

    public static TaskManager getTaskManager() {
        return taskManager;
    }

    public static void addPartyInvite(PartyInvite invite) {
        partyInvites.add(invite);
    }

    public static List<PartyInvite> getPartyInvites() {
        return partyInvites;
    }

    public static void removePartyInvite(PartyInvite remove) {
        partyInvites.remove(remove);
    }

    public static void addPartyCombine(CombinationRequest request) {
        partyCombinations.add(request);
    }

    public static void removePartyCombine(CombinationRequest request) {
        partyCombinations.remove(request);
    }

    public static List<CombinationRequest> getPartyCombinations() {
        return partyCombinations;
    }

    public static ExtronPlayer getPlayer(Player player) {
        return PlayerList.getPlayer(player);
    }

    public static ExtronWorld getLobby() {
        return mainLobby;
    }

    public static void loadWorld(Config config) {
        World w = Bukkit.getWorld(config.getName());
        if (w == null) {
            createWorld(config);
            w = Bukkit.getWorld(config.getName());
        }
        ExtronWorld world = getWorld(config.getName());
        if (world != null) {
            return;
        }
        world = new ExtronWorld((CraftWorld) w, config);
        if (!worlds.contains(world)) {
            worlds.add(world);
        }
    }

    private static void createWorld(Config config) {
        createWorld(config.getName(),World.Environment.valueOf(config.getString("environment","NORMAL")),WorldType.valueOf(config.getString("world_type","NORMAL")),config.getBoolean("void",false));
    }

    public static ExtronWorld createWorld(String name, World.Environment env, WorldType type, boolean isVoid) {
        for (ExtronWorld w : worlds) {
            if (w.getName().equalsIgnoreCase(name)) {
                return getWorld(Bukkit.getWorld(name));
            }
        }
        if (Bukkit.getWorld(name) != null) {
            if (getWorld(Bukkit.getWorld(name)) == null) {
                loadWorld(worldsFolder.createConfig(name));
            }
            return getWorld(Bukkit.getWorld(name));
        }
        WorldCreator creator = new WorldCreator(name);
        creator.type(type);
        creator.environment(env);
        creator.generateStructures(!isVoid);
        return createWorld(name,creator,isVoid);
    }

    public static ExtronWorld createWorld(String name, WorldCreator creator, boolean isVoid) {
        if (isVoid) {
            creator.generator(new VoidGenerator());
        }
        World w = creator.createWorld();
        ExtronWorld extronWorld = new ExtronWorld((CraftWorld) w,worldsFolder.createConfig(name));
        extronWorld.getConfig().set("void",isVoid);
        extronWorld.getConfig().set("environment",creator.environment());
        extronWorld.getConfig().set("world_type",creator.type());
        extronWorld.getConfig().save();
        worlds.add(extronWorld);
        return extronWorld;
    }

    public static ExtronWorld getWorld(World world) {
        return world == null ? null : getWorld(world.getName());
    }

    public static ExtronWorld getWorld(String name) {
        return ListUtils.firstMatch(worlds, w->w.getName().equalsIgnoreCase(name));
    }

    public static List<ExtronWorld> getWorlds() {
        return worlds;
    }

    public static boolean deleteWorld(ExtronWorld world) {
        Bukkit.getServer().unloadWorld(world.handle, true);
        world.getConfig().getFile().delete();
        worlds.remove(world);
        deleteFiles(world.getFolder());
        return true;
    }

    public static void deleteFiles(File f) {
        if (f.exists()) {
            File[] files = f.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteFiles(file);
                } else {
                    file.delete();
                }
            }
        }
        f.delete();
    }

    public static Collection<String> getWorldNames() {
        return ListUtils.convertAll(worlds,ExtronWorld::getName);
    }

    public static ExtronPlayer createPlayer(Player player) {
        if (getPlayer(player) != null) return null;
        return PlayerList.create(player);
    }

    public static Config getMainConfig() {
        return mainConfig;
    }

    public static Game getGame(String id) {
        return ListUtils.firstMatch(games,g->g.getId().equalsIgnoreCase(id));
    }

    /**
     * Registers a game to the network and initialize its maps and statistics.
     * @param game The game to add
     */
    public static void addGame(Game game) {
        games.add(game);
        loadMaps();
    }

    private static void loadMaps() {
        for (Config config : worldsFolder) {
            if (config.getString("gamemode") != null) {
                if (Main.getGameMode(config.getString("gamemode")) != null) {
                    loadWorld(config);
                    Main.getGameMode(config.getString("gamemode")).getMaps().add(Main.getWorld(Bukkit.getWorld(config.getName())));
                }
            }
        }
    }

    public static GameMode getGameMode(String id) {
        for (Game game : games) {
            for (GameMode gm : game.getGameModes()) {
                if (gm.getId().equalsIgnoreCase(id)) {
                    return gm;
                }
            }
        }
        return null;
    }

    public static List<String> getGamemodeIds(Game game) {
        List<String> list = new ArrayList<>();
        for (GameMode gm : game.getGameModes()) {
            list.add(gm.getId());
        }
        return list;
    }

    public static ExtronWorld getMap(String name) {
        for (Game game : games) {
            for (ExtronWorld map : game.getMaps()) {
                if (map.getName().equalsIgnoreCase(name)) {
                    return map;
                }
            }
        }
        return null;
    }

    public static DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public static DataTable<ExtronPlayer> getPlayersData() {
        return playerDataTable;
    }


    public static List<Statistic> getStatistics() {
        return statistics;
    }

    public static Statistic getStatistic(String id,Game game) {
        return ListUtils.firstMatch(ListUtils.filter(statistics,s->s.getGame() == game),s->s.getId().equalsIgnoreCase(id));
    }

    public static Statistic getStatistic(String id) {
        return getStatistic(id,null);
    }

    public static void registerStatistic(Statistic s) {
        statistics.add(s);
    }

    public static void registerStatistic(String id, String name) {
        registerStatistic(id,name,null);
    }

    public static void registerStatistic(String id, String name, Game game) {
        registerStatistic(new Statistic(id,name,game));
    }

    public static List<Statistic> getStatistics(Game game) {
        return ListUtils.filter(statistics,s->s.getGame() == game);
    }

    public static List<Game> getGames() {
        return games;
    }

    public static void showPlayerHolograms(ExtronPlayer p) {
        for (PlayerHologram ph : playerHolograms) {
            ph.spawnFor(p);
        }
    }

    public static void hidePlayerHolograms(ExtronPlayer p) {
        for (PlayerHologram ph : playerHolograms) {
            ph.despawnFor(p);
        }
    }

    public static List<PlayerHologram> getNotInitializedHolograms() {
        List<PlayerHologram> list = new ArrayList<>();
        for (PlayerHologram ph : playerHolograms) {
            if (!ph.isInitialized()) {
                list.add(ph);
            }
        }
        return list;
    }

    public static List<PlayerHologram> getInitializedHolograms() {
        List<PlayerHologram> list = new ArrayList<>();
        for (PlayerHologram ph : playerHolograms) {
            if (ph.isInitialized()) {
                list.add(ph);
            }
        }
        return list;
    }

    public static PlayerHologram getPlayerHologram(String id) {
        for (PlayerHologram ph : playerHolograms) {
            if (ph.getId().equalsIgnoreCase(id)) {
                return ph;
            }
        }
        return null;
    }

    public static void registerPlayerHologram(PlayerHologram ph) {
        playerHolograms.add(ph);
    }

    public static void updateMainScoreboard() {
        updateScoreboard("main_board");
    }

    public static void updateScoreboard(String id) {
        updateScoreboard(getScoreboard(id));
    }

    public static void updateScoreboard(Scoreboard sb) {
        PlayerList.forEachOnline(p->p.getScoreboardManager().tryUpdate(sb));
    }

    public static Scoreboard getScoreboard(String id) {
        return ListUtils.firstMatch(scoreboards,sb->sb.getId().equalsIgnoreCase(id));
    }

    public static void hideScoreboards(String id) {
        hideScoreboards(getScoreboard(id));
    }

    public static void hideScoreboards(Scoreboard sb) {
        for (ExtronPlayer p : PlayerList.getOnlinePlayers()) {
            p.getScoreboardManager().hide(sb);
        }
    }

    public static void registerScoreboard(Scoreboard sb) {
        scoreboards.add(sb);
    }

    public static Scoreboard getMainScoreboard() {
        return getScoreboard("main_board");
    }

    public static void registerCommand(Command cmd) {
        commandManager.register(cmd);
    }

    public static List<Scoreboard> getScoreboards() {
        return scoreboards;
    }

    public static void registerCollectible(Collectible c) {
        if (!collectibles.containsKey(c.getType())) {
            collectibles.put(c.getType(),new ArrayList<>());
        }
        collectibles.get(c.getType()).add(c);
        if (c instanceof LootBox) {
            LootBoxManager.addLootBox((LootBox) c);
        }
        c.onRegister();
    }

    public static Collectible getCollectible(String id, CollectibleType type) {
        return ListUtils.firstMatch(collectibles.getOrDefault(type,new ArrayList<>()),c->c.getId().equalsIgnoreCase(id));
    }

    public static List<Collectible> getCollectiblesOfType(CollectibleType type) {
        return collectibles.get(type);
    }

    public static void registerCollectibleType(CollectibleType type) {
        collectibles.put(type,new ArrayList<>());
    }

    public static CollectibleType getCollectibleType(String id) {
        return ListUtils.firstMatch(collectibles.keySet(),ct->ct.getId().equalsIgnoreCase(id));
    }

    public static Set<CollectibleType> getCollectibleTypes() {
        return collectibles.keySet();
    }

    public static List<Collectible> getCollectibles() {
        List<Collectible> list = new ArrayList<>();
        for (List<Collectible> l : collectibles.values()) {
            list.addAll(l);
        }
        return list;
    }

    public static void createPoll(ExtronPlayer sender, String question, List<String> options) {
        currentPoll = new Poll(sender,question,options);
    }

    public static Poll getCurrentPoll() {
        return currentPoll;
    }

    public static void closePoll() {
        currentPoll = null;
    }

    public static ExtronEntity getExtronEntity(Entity entity) {
        if (entity instanceof Player) {
            return ExtronPlayer.of((Player) entity);
        }
        ExtronWorld w = Main.getWorld(entity.getWorld());
        if (w == null) {
            return null;
        }
        for (ExtronEntity e : w.getEntities()) {
            if (e != null) {
                if (e.getEntity() != null) {
                    if (e.getEntity().equals(entity)) {
                        return e;
                    }
                }
            }
        }
        ExtronEntity e = new BasicEntity(w,entity);
        w.getEntities().add(e);
        return e;
    }

    public static List<GameMode> getGameModes() {
        List<GameMode> list = new ArrayList<>();
        for (Game game : games) {
            list.addAll(Arrays.asList(game.getGameModes()));
        }
        return list;
    }

    public static List<ExtronWorld> getGameMaps() {
        return ListUtils.filter(getWorlds(),ExtronWorld::isMap);
    }

    public static void registerInteractAction(StackInteractAction a) {
        if (ListUtils.firstMatch(interactActions,n->n.getId().equalsIgnoreCase(a.getId())) == null) {
            interactActions.add(a);
        }
    }

    public static List<StackInteractAction> getInteractActions() {
        return interactActions;
    }
}
