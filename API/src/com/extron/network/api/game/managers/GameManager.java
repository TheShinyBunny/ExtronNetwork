package com.extron.network.api.game.managers;

import com.extron.network.api.ExtronWorld;
import com.extron.network.api.Main;
import com.extron.network.api.config.Config;
import com.extron.network.api.event.EventManager;
import com.extron.network.api.game.*;
import com.extron.network.api.game.listeners.*;
import com.extron.network.api.party.Party;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.scoreboard.Scoreboard;
import com.extron.network.api.utils.MathUtils;
import com.extron.network.api.utils.tasks.Counter;
import com.extron.network.api.utils.tasks.CounterAction;
import com.extron.network.api.utils.tasks.ExtronRunnable;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class GameManager implements IGameManager, GameEventListener, Listener {

    protected final GameMode game;
    protected ExtronWorld map;
    private GameState state;

    protected List<ExtronPlayer> allPlayers;
    protected List<ExtronPlayer> spectators;
    protected List<ExtronPlayer> waiting;

    protected GameSettings settings;

    private boolean loaded;

    protected int elapsedSeconds;

    public static final Map<ChatColor, String> DEFAULT_TEAMS = new HashMap<>();

    public static final DeathMessages DEFAULT_DEATH_MESSAGES = (cause, playerName, killerName) -> {
        switch (cause) {
            case FALL:
                return playerName + " fell of a cliff.";
            case FALL_KILL:
                return playerName + " was thrown off a cliff by " + killerName + ".";
            case VOID:
                return playerName + " fell into the void.";
            case LEAVE:
                return playerName + " has quit.";
            case CACTUS:
                return playerName + " got rekt by a cactus.";
            case VOID_KILL:
                return playerName + " was thrown into the void by " + killerName + ".";
            case ENTITY_KILLED:
            case PLAYER_KILLED:
                return playerName + " was killed by " + killerName + ".";
            case ENTITY_SHOOT:
            case PLAYER_SHOOT:
                return playerName + " was shot by " + killerName + ".";
            case EXPLODE_KILL:
                return playerName + " was blown up by " + killerName + ".";
            case EXPLODED:
                return playerName + " blew up.";
            case FIRE:
                return playerName + " burned to death.";
                default:
                    return playerName + " died.";
        }
    };

    static {
        DEFAULT_TEAMS.put(ChatColor.RED, "Red");
        DEFAULT_TEAMS.put(ChatColor.BLUE, "Blue");
        DEFAULT_TEAMS.put(ChatColor.YELLOW, "Yellow");
        DEFAULT_TEAMS.put(ChatColor.GREEN, "Green");
        DEFAULT_TEAMS.put(ChatColor.WHITE, "White");
        DEFAULT_TEAMS.put(ChatColor.GRAY, "Gray");
        DEFAULT_TEAMS.put(ChatColor.AQUA, "Aqua");
        DEFAULT_TEAMS.put(ChatColor.LIGHT_PURPLE, "Purple");
    }

    private Counter startCountdown;
    private boolean forceStarted;

    public GameManager(ExtronWorld map, GameSettings settings) {
        this.game = settings.getGameMode();
        this.settings = settings;
        this.map = map;
        allPlayers = new ArrayList<>();
        spectators = new ArrayList<>();
        waiting = new ArrayList<>();

        this.state = GameState.NONE;

    }

    @Override
    public GameState getState() {
        return state;
    }

    @Override
    public void setState(GameState state) {
        GameState prev = this.state;
        this.state = state;
        invokeListener(StateChangeListener.class,t->t.onStateChanged(prev,state));
    }

    @Override
    public GameMode getGameMode() {
        return game;
    }

    @Override
    public ExtronWorld getMap() {
        return map;
    }

    @Override
    public final void tryJoinPlayer(ExtronPlayer p) throws GameJoinException {
        if (this.state.canPlayersJoin() || waiting.size() < settings.getTeams() * settings.getPlayersInTeam()) {
            if (p.isInParty()) {
                if (!p.getParty().getLeader().equals(p)) {
                    throw new GameJoinException.NotTheLeader();
                }
                tryJoinParty(p.getParty());
                for (ExtronPlayer pl : p.getParty().getAllPlayers()) {
                    if (!this.waiting.contains(p)) {
                        onPlayerJoined(pl);
                    }
                }
            } else {
                onPlayerJoined(p);
            }
        } else {
            throw new GameJoinException.GameStarted();
        }
    }

    @Override
    public void tryJoinParty(Party party) throws GameJoinException {
        if (getOpenSlotsToJoin() < party.getAllPlayers().size()) {
            if (party.getAllPlayersInGame(this).isEmpty()) {
                throw new GameJoinException.PartyCantFit();
            } else if (getOpenSlotsToJoin() + party.getAllPlayersInGame(this).size() < party.getAllPlayers().size()) {
                throw new GameJoinException.PartyCantFit();
            }
        }
        if (!game.canPartyJoin()) {
            throw new GameJoinException.PartiesNotAllowed();
        }
        List<ExtronPlayer> offline = new ArrayList<>();
        for (ExtronPlayer p : party.getAllPlayers()) {
            if (!p.isOnline()) {
                offline.add(p);
            }
        }
        if (!offline.isEmpty()) {
            throw new GameJoinException.PartyPlayersOffline(offline);
        }
    }

    @Override
    public int getOpenSlotsToJoin() {
        return settings.getTeams() * settings.getPlayersInTeam() - this.waiting.size();
    }

    @Override
    public void onPlayerJoined(ExtronPlayer p) {
        if (map.currentGame == null) {
            game.addManager(this);
            map.currentGame = this;
        }
        if (!loaded) {
            loadFromConfig(map.getConfig());
            loaded = true;
        }
        p.getInventory().clear();
        /*PlayerGameJoinEvent e = new PlayerGameJoinEvent(p,this);
        Main.getEventManager().call(e);
        if (e.isCancelled()) return;*/
        p.changeWorld(this.map);
        waiting.add(p);
        game.giveWaitItems(p,this);
        p.setCurrentGame(this);
        invokeListener(PlayersChangeListener.class,t->t.onPlayerJoined(p));
        if (state == GameState.NONE) state = GameState.WAITING;
        p.disableFlight();
        updateAllScoreboards(waiting);
        sendJoinMessage(p,waiting.size(),game.getPlayersInTeam() * settings.getTeams());
        GameStartRules rules = game.getStartRule();
        if (rules.getMinPlayers() <= waiting.size()) {
            startCountdown(rules.getMinCountdown());
        }
        if (rules.getMatchingHook(waiting.size()) != null) {
            startCountdown.skipTo(rules.getMatchingHook(waiting.size()).getCountdown());
        }
    }

    public void startCountdown(int countdown) {
        startCountdown = new Counter(countdown,-1,20,"game_start",null);
        startCountdown.start(new CounterAction() {
            @Override
            public void onCounterLoop(Counter c) {
                startingIn(c.getCurrent());
                if (!forceStarted) {
                    if (game.getStartRule().getMinPlayers() > waiting.size()) {
                        c.stop();
                    }
                }
            }

            @Override
            public void onCounterStopped(Counter c) {
                startCountdownCancelled();
            }

            @Override
            public void onCounterFinished(Counter cd) {
                onCountdownFinished();
            }
        });
    }

    protected abstract void onCountdownFinished();

    protected abstract void startingIn(int seconds);

    public void startCountdownCancelled() {

    }

    protected abstract void sendJoinMessage(ExtronPlayer p, int players, int maxPlayers);

    public void updateAllScoreboards(List<ExtronPlayer> players) {
        for (ExtronPlayer p : players) {
            p.getScoreboardManager().updateScoreboard(game.createScoreboard(this));
        }
    }

    public abstract void end();

    @Override
    public void onPlayerLeave(ExtronPlayer p) {
        switch (state) {
            case NONE:
            case WAITING:
            case STARTING:
                sendPreGameQuitMessage(p);
                waiting.remove(p);
                if (waiting.isEmpty()) {
                    dispose();
                } else {
                    p.showMainScoreboard();
                }
                return;
            case RUNNING:
                if (!spectators.contains(p)) {
                    onPlayerDeath(new Death(p,DeathCause.LEAVE,null,null));
                }
                if (allPlayers.size() <= 1) {
                    dispose();
                    return;
                }
                break;
            case ENDED:
                spectators.remove(p);
                return;
        }
        spectators.remove(p);
        if (!canPlayerRejoin(p)) {
            allPlayers.remove(p);
        }
        if (allPlayers.isEmpty()) {
            dispose();
        }
    }

    protected abstract void sendPreGameQuitMessage(ExtronPlayer p);

    @Override
    public Death createDeathFromEvent(PlayerDeathEvent e) {
        ExtronPlayer p = getPlayerInGame(e.getEntity());
        if (p == null) return null;
        EntityDamageByEntityEvent edbee = p.getLastEntityDamagedEvent();
        DeathCause cause;
        Entity damager = null;
        EntityDamageEvent ede = p.getLastDamage();
        if (ede == null) return null;
        EntityDamageEvent.DamageCause directCause = ede.getCause();
        if (edbee == null) {
            cause = DeathCause.from(directCause);
        } else if (MathUtils.inRange(p.getTicksSinceLastEntityDamage(),0,180)) {
            cause = DeathCause.from(p,edbee.getCause(),directCause);
            damager = getDamager(edbee);
        } else {
            return null;
        }
        return new Death(p,cause,damager,null);
    }

    @Override
    public void onPlayerDeath(Death death) {
        if (state.isAfterGame()) {
            death.getPlayer().handle.teleport(map.getSpawnPoint());
            return;
        }
        ExtronPlayer p = death.getPlayer();
        Entity e = death.getKiller();
        DeathMessages deathMessages;
        if (game.getCustomDeathMessages() == null) {
            deathMessages = DEFAULT_DEATH_MESSAGES;
        } else {
            deathMessages = game.getCustomDeathMessages();
        }
        messageAll(allPlayers, deathMessages.createDeathMessage(death.getCause(),p.getName(),e == null ? null : e instanceof Player ? ExtronPlayer.of((Player) e).getName() : e.getName()));
        for (ItemStack item : p.handle.getInventory()) {
            if (item != null) {
                p.getWorld().handle.dropItemNaturally(p.getLocation(),item);
            }
        }
        if (p.isOnline()) {
            p.getInventory().clear();
        }
        if (death.getCause() == DeathCause.LEAVE) {
            if (!canPlayerRejoin(p)) {
                allPlayers.remove(p);
            }
        } else {
            setSpectator(p);
            p.handle.teleport(map.getSpawnPoint());
            spectators.add(p);
            if (isFinalKill(p)) {
                sendFinalDeathTitle(p);
            } else {
                sendDeathTitle(p);
            }
        }
        updateAllScoreboards(allPlayers);
    }

    protected abstract void sendDeathTitle(ExtronPlayer p);

    protected abstract void sendFinalDeathTitle(ExtronPlayer p);

    protected abstract boolean isFinalKill(ExtronPlayer p);

    protected abstract void setSpectator(ExtronPlayer p);

    protected abstract boolean canPlayerRejoin(ExtronPlayer p);

    protected Entity getDamager(EntityDamageByEntityEvent ede) {
        Entity damager = ede.getDamager();
        if (damager instanceof Projectile) {
            ProjectileSource src = ((Projectile) damager).getShooter();
            if (src instanceof Entity) {
                return (Entity) src;
            } else {
                return null;
            }
        }
        return damager;
    }

    @Override
    public List<ExtronPlayer> getAllPlayers() {
        return allPlayers;
    }

    public void messageAll(List<ExtronPlayer> list, String msg) {
        list.forEach(p->p.sendMessage(msg));
    }

    public void titleAll(List<ExtronPlayer> list, String title, String subtitle) {
        list.forEach(p->p.sendTitle(title,subtitle));
    }

    public void titleAll(List<ExtronPlayer> list, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        list.forEach(p->p.sendTitle(title,subtitle,fadeIn,stay,fadeOut));
    }

    public void hidePlayer(ExtronPlayer p, List<ExtronPlayer> from) {
        from.forEach(pl->{
            if (pl.isOnline()) {
                p.handle.hidePlayer(p.handle);
            }
        });
    }

    @Override
    public int getStartCountdown() {
        return startCountdown == null ? -1 : startCountdown.getCurrent();
    }

    @Override
    public void start() {
        System.out.println("starting game!");
        allPlayers.addAll(waiting);
        waiting.clear();
        setState(GameState.RUNNING);
        this.map.handle.setTime(0);
        this.map.handle.setGameRuleValue("doDaylightCycle","true");
        this.map.killAllEntities();
        allPlayers.forEach(this::setupPlayerOnStart);
        updateAllScoreboards(allPlayers);
        invokeListener(StartEndListener.class,StartEndListener::onGameStarted);
        new ExtronRunnable() {
            @Override
            public void run() {
                elapsedSeconds++;
                if (state.isAfterGame()) {
                    cancel();
                }
            }
        }.timer(0,20);
    }

    protected abstract void loadFromConfig(Config config);

    protected abstract void sendTeamEliminatedMessage(Team team);

    public <T extends GameListener> void invokeListener(Class<T> listenerType, Consumer<T> invoke) {
        if (listenerType.isInstance(this)) {
            invoke.accept((T) this);
        }
    }

    public <T extends GameListener,R> R invokeResultListener(Class<T> listenerType, Function<T,R> invoke, R def) {
        if (listenerType.isInstance(this)) {
            return invoke.apply((T) this);
        }
        return def;
    }

    public GameSettings getSettings() {
        return settings;
    }

    @Override
    public void dispose() {
        if (startCountdown != null) {
            startCountdown.stop();
        }
        this.setState(GameState.NONE);
        this.game.getManagers().remove(this);
        map.currentGame = null;
    }

    @Override
    public void forceStart(ExtronPlayer p) {
        this.forceStarted = true;
        this.state = GameState.STARTING;
        startCountdown(game.getStartRule().getMinCountdown());
        messageAll(waiting, p.getDisplayName() + ChatColor.GOLD + " has force started the game!");
    }

    @Override
    public List<ExtronPlayer> getWaiting() {
        return waiting;
    }

    @Override
    public List<ExtronPlayer> getSpectators() {
        return spectators;
    }

    public ExtronPlayer getPlayerInGame(HumanEntity entity) {
        ExtronPlayer p = Main.getPlayer((Player) entity);
        if (p.getCurrentGame() != this) {
            return null;
        }
        return p;
    }

    @Override
    public boolean isIngame() {
        return state.isIngame();
    }

    private boolean isInThisMap(Location location) {
        if (location == null) return false;
        return location.getWorld() == map.handle;
    }

    @EventHandler
    private void food(FoodLevelChangeEvent e) {
        ExtronPlayer p = getPlayerInGame(e.getEntity());
        if (p == null) return;
        e.setCancelled(!isIngame());
        int i = onFoodLost(p,e.getFoodLevel());
        if (i >= 0 && i <= 20) {
            e.setFoodLevel(i);
        } else {
            e.setCancelled(true);
        }
    }

    @Override
    public int onFoodLost(ExtronPlayer p, int level) {
        return level;
    }

    @EventHandler
    private void placeBlock(BlockPlaceEvent e) {
        ExtronPlayer p = getPlayerInGame(e.getPlayer());
        if (p == null) return;
        e.setCancelled(!isIngame());
        if (!e.isCancelled()) {
            e.setCancelled(onPlaceBlock(p, e.getBlock(), e.getItemInHand()) && !e.isCancelled());
        }
    }

    @Override
    public boolean onPlaceBlock(ExtronPlayer p, Block placed, ItemStack itemInHand) {
        return false;
    }

    @EventHandler
    private void breakBlock(BlockBreakEvent e) {
        ExtronPlayer p = getPlayerInGame(e.getPlayer());
        if (p == null) return;
        e.setCancelled(!isIngame());
        int i = onBreakBlock(p,e.getBlock(),e.getExpToDrop());
        if (i < 0) {
            e.setCancelled(true);
        } else {
            e.setExpToDrop(i);
        }
    }

    @Override
    public int onBreakBlock(ExtronPlayer p, Block broken, int xp) {
        return xp;
    }

    @EventHandler
    private void explosion(EntityExplodeEvent e) {
        if (!isInThisMap(e.getLocation())) return;
        e.setCancelled(!isIngame());
        float y = onExplosion(e.getEntity(),e.getLocation(),e.blockList(),e.getYield());
        if (y < 0) {
            e.setCancelled(true);
        } else {
            e.setYield(y);
        }
    }

    @Override
    public float onExplosion(Entity entity, Location location, List<Block> blocks, float yield) {
        return yield;
    }

    @EventHandler
    private void pickupItem(PlayerPickupItemEvent e) {
        ExtronPlayer p = getPlayerInGame(e.getPlayer());
        if (p == null) return;
        e.setCancelled(!isIngame());
        if (!e.isCancelled()) {
            e.setCancelled(onItemPickup(p,e.getItem(),e.getRemaining()));
        }
    }

    @Override
    public boolean onItemPickup(ExtronPlayer p, Item item, int remaining) {
        return false;
    }

    @EventHandler
    private void dropItem(PlayerDropItemEvent e) {
        ExtronPlayer p = getPlayerInGame(e.getPlayer());
        if (p == null) return;
        e.setCancelled(!isIngame());
        if (!e.isCancelled()) {
            e.setCancelled(onItemDropped(p,e.getItemDrop()));
        }
    }

    @Override
    public boolean onItemDropped(ExtronPlayer p, Item dropped) {
        return false;
    }

    @EventHandler
    private void xp(PlayerExpChangeEvent e) {
        ExtronPlayer p = getPlayerInGame(e.getPlayer());
        if (isIngame()) {
            e.setAmount(onPickupXP(p,e.getAmount()));
        } else {
            e.setAmount(0);
        }
    }

    @Override
    public int onPickupXP(ExtronPlayer p, int amount) {
        return amount;
    }

}
