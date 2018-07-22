package com.extron.network.api.players;

import com.extron.network.api.ExtronWorld;
import com.extron.network.api.Main;
import com.extron.network.api.collection.*;
import com.extron.network.api.collection.pet.PetInstance;
import com.extron.network.api.command.defaults.admin.CommandHologram;
import com.extron.network.api.command.defaults.admin.CommandParkour;
import com.extron.network.api.entity.ExtronEntity;
import com.extron.network.api.event.EventManager;
import com.extron.network.api.event.game.GameCreatedEvent;
import com.extron.network.api.game.*;
import com.extron.network.api.game.managers.GameManager;
import com.extron.network.api.game.managers.IGameManager;
import com.extron.network.api.hologram.SavedHologram;
import com.extron.network.api.inventory.Book;
import com.extron.network.api.inventory.Button;
import com.extron.network.api.inventory.InventoryMenu;
import com.extron.network.api.inventory.PlayerInventory;
import com.extron.network.api.inventory.base.ExtronStack;
import com.extron.network.api.inventory.base.Inventory;
import com.extron.network.api.nick.PlayerNick;
import com.extron.network.api.parkour.Parkour;
import com.extron.network.api.parkour.ParkourMark;
import com.extron.network.api.party.Party;
import com.extron.network.api.permission.Rank;
import com.extron.network.api.scoreboard.MainScoreboard;
import com.extron.network.api.scoreboard.Scoreboard;
import com.extron.network.api.scoreboard.ScoreboardManager;
import com.extron.network.api.scoreboard.ScoreboardUpdater;
import com.extron.network.api.stats.StatisticManager;
import com.extron.network.api.utils.*;
import com.extron.network.api.utils.punishment.PunishType;
import com.extron.network.api.utils.punishment.Punishment;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_8_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonObject;
import org.bukkit.craftbukkit.v1_8_R1.CraftOfflinePlayer;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

/**
 * The best class of the entire ExtronNetwork API tbh
 */
public class ExtronPlayer extends ExtronEntity implements ScoreboardUpdater {

    /**
     * The bukkit entity representation of the player. Will not be null as long as the player is online.
     * The moment the player leaves the server, it is set to null, and the moment he comes back online,
     * it is set to {@link Bukkit#getPlayer(UUID)} with the saved {@link #uuid}.<br/>
     * <b>PLEASE DO NOT ASSIGN THIS VALUE TO ANOTHER <code>CraftPlayer</code>! </b> - it will basically mess up everything.
     */
    public CraftPlayer handle;

    private PlayerData data;

    /**
     * The last player who messaged this player or this player messaged using the <code>/msg player [msg]</code> command.<br/>
     * Will be reset to <code>null</code> after 3600 ticks (3 minutes) of inactivity (cooldown tracked by {@link #privateMsgDelay})
     */
    public ExtronPlayer privateMassaging;

    /**
     * The current delay of resetting the {@link #privateMassaging private messager} to <code>null</code>.
     */
    public int privateMsgDelay;

    /**
     * The current {@link ChatType chat} this player is talking in. Determines who will receive the sent chat messages from this player.
     */
    public ChatType chat;
    private UUID uuid;
    private Party party;
    private Rank rank;
    private IGameManager currentGame;

    /**
     * The current {@link SavedHologram} this player is editing. Only used by the {@link CommandHologram /hologram} command.
     */
    public SavedHologram editingHologram;

    /**
     * The current {@link Parkour} this player is editing. Only used by the {@link CommandParkour /parkour} command.
     */
    public Parkour editingParkour;
    private final ScoreboardManager scoreboardManager;
    private InventoryMenu openMenu;
    private Punishment ban;
    private Punishment mute;

    private int coins;
    private boolean sendingToLobby = false;

    private int level;
    private int xp;
    private int xpToNextLevel;
    private TimeStamp lastOnline;
    private TimeStamp lastLogin;
    private TimeStamp firstLogin;
    private StatisticManager statsManager;

    private Parkour currentParkour;
    private ParkourMark lastCheckpoint;
    private int currentParkourTime;

    private PlayerInventory inventory;
    private int sendToLobbyDelay;

    /**
     * Whether the player can pick up items.
     */
    public boolean pickupItems;

    private PlayerNick nick;
    private EntityDamageByEntityEvent lastEntityDamaged;
    private TimeStamp entityDamagedTime;
    private EntityDamageEvent lastDamage;
    private TimeStamp damageTime;
    private Book bookToOpen;
    private int bookOpenTimer;
    private Map.Entry<Integer, ItemStack> bookReplaced;
    private PlayerCollection collection;
    private int gadgetCooldown;
    private boolean hasGadgetCooldown = false;

    /**
     * The player's currently selected {@link PetInstance pet}.
     */
    public PetInstance pet;
    private boolean invisible;
    /**
     * Whether the player can drop items.
     */
    public boolean dropItems;
    private MapCreator mapCreator;

    /**
     * Creates a new <i>online</i> ExtronPlayer, with a CraftPlayer handle. Shouldn't be used by other plugins, or even outside the {@link PlayerList}.
     * @param handle The handle of the player. Can be null only when it's a {@link FakePlayer}.
     */
    public ExtronPlayer(@Nullable CraftPlayer handle) {
        this();
        this.handle = handle;
        this.name = handle == null ? "Server" : handle.getName();
        this.uuid = handle == null ? UUID.nameUUIDFromBytes("Server".getBytes()) : handle.getUniqueId();
    }

    /**
     * Creates a new <i>offline</i> ExtronPlayer, with a CraftOfflinePlayer handle. Shouldn't be used by other plugins, or even outside the {@link PlayerList}.
     * @param handle The offline handle of the player.
     */
    public ExtronPlayer(CraftOfflinePlayer handle) {
        this();
        this.handle = null;
        this.name = handle.getName();
        this.uuid = handle.getUniqueId();
    }

    @Deprecated
    @Override
    public void spawn() {

    }

    /**
     * A shared constructor for both {@link #ExtronPlayer(CraftPlayer)} and {@link #ExtronPlayer(CraftOfflinePlayer)}.<br/>
     * Creates the scoreboard, stats, inventory and collectibles managers.
     */
    private ExtronPlayer() {
        super();
        this.invulnerable = true;
        this.rank = Rank.NONE;
        this.world = Main.getLobby();
        this.scoreboardManager = new ScoreboardManager(this);
        this.data = new PlayerData(this);
        this.statsManager = new StatisticManager(this);
        this.level = 1;
        this.xpToNextLevel = calcXpToNext(level);
        this.chat = ChatType.ALL;
        this.collection = new PlayerCollection(this);
    }

    /**
     * Returns an extron player representing this bukkit Player. <br/>
     * An equivalent of using {@link Main#getPlayer(Player)}.
     * @param player The bukkit {@link Player}
     * @return The ExtronPlayer representing that player.
     */
    public static ExtronPlayer of(Player player) {
        return Main.getPlayer(player);
    }

    void init() {
        try {
            this.ban = Punishment.create(this,PunishType.BAN);
            this.mute = Punishment.create(this,PunishType.MUTE);
            this.coins = this.data.getInt("coins",2500);
            this.name = this.data.getString("name",name);
            this.rank = Rank.fromString(this.data.getString("rank","default"));
            this.nick = new PlayerNick(this);
            if (this.data.getString("first_login") == null) {
                this.data.set("first_login",new TimeStamp().toString());
            }
            this.lastOnline = new TimeStamp(data.getString("last_online", isOnline() ? new TimeStamp().toString() : "never"));
            this.lastLogin = new TimeStamp(data.getString("last_login", TimeStamp.never().toString()));
            this.firstLogin = new TimeStamp(data.getString("first_login", new TimeStamp().toString()));
            this.level = data.getInt("level",1);
            this.xpToNextLevel = this.calcXpToNext(level);
            System.out.println("set xp to next from data: " + xpToNextLevel);
            this.xp = data.getInt("xp",0);
            this.statsManager.load(Main.getStatistics());
            this.nick.load(data);
            this.collection.load(data);
            /*
            this.challengeManager.load();*/
        } catch (Exception e) {
            System.out.println("An error occurred while initializing player " + this.name);
            e.printStackTrace();
        }
    }

    /**
     * This methods runs every tick, so there is no point of calling it anyways.<br/>
     * Ticks and updates the scoreboard, private message delay, gadgets cooldown, etc.
     */
    @Override
    public void tick() {
        if (currentParkour != null) {
            currentParkourTime++;
        }
        if (scoreboardManager.getCurrentScoreboard() != null) {
            if (scoreboardManager.getCurrentScoreboard().getTitleAnimation() != null) {
                scoreboardManager.update();
            }
        }
        if (sendingToLobby) {
            sendToLobbyDelay++;
            if (sendToLobbyDelay >= 60) {
                changeWorld(Main.getLobby());
                sendMessage(ChatColor.GREEN + "Sending you back to the lobby...");
                sendingToLobby = false;
                sendToLobbyDelay = 0;
            }
        }
        if (privateMassaging != null) {
            privateMsgDelay--;
            if (privateMsgDelay <= 0) {
                privateMassaging = null;
            }
        }
        if (hasGadgetCooldown) {
            gadgetCooldown--;
            if (this.getSelectedGadget() != null) {
                if (gadgetCooldown < 1) {
                    //this.sendActionBar(ChatColor.GREEN + "You can now use the " + getSelectedGadget().getDisplayName() + "!");
                    cancelGadgetCooldown();
                } else {
                    ExtronPlayer.this.sendActionBar(ChatColor.RED + "Cooldown: " + ChatColor.YELLOW + ((gadgetCooldown / 20) + 1));
                }
            }
        }
        if (bookToOpen != null) {
            bookOpenTimer++;
            if (bookOpenTimer == 1) {
                this.bookReplaced = new Map.Entry<Integer, ItemStack>() {
                    @Override
                    public Integer getKey() {
                        return ExtronPlayer.this.handle.getInventory().getHeldItemSlot();
                    }

                    @Override
                    public ItemStack getValue() {
                        return ExtronPlayer.this.handle.getInventory().getItem(handle.getInventory().getHeldItemSlot());
                    }

                    @Override
                    public ItemStack setValue(ItemStack value) {
                        return null;
                    }

                };
                this.handle.getInventory().setItemInHand(bookToOpen.toBukkitItem());
            }
            if (bookOpenTimer == 2) {
                this.getNMS().openBook(bookToOpen.toNMSItem());
            }
            if (bookOpenTimer == 3) {
                this.handle.getInventory().clear(bookReplaced.getKey());
                this.handle.getInventory().setItem(bookReplaced.getKey(),bookReplaced.getValue());
                bookToOpen = null;
                bookOpenTimer = 0;
                bookReplaced = null;
            }
        }
    }

    @Override
    public void kill() {
        this.getNMS().G();
    }

    void join() {
        this.handle = (CraftPlayer) Bukkit.getPlayer(uuid);
        this.world = Main.getLobby();
        this.world.getPlayers().add(this);
        this.lastLogin = new TimeStamp();
        this.data.set("last_login",lastLogin.toString());
        if (pet != null) {
            pet.spawn();
        }
        this.nick.init();
        this.inventory = new PlayerInventory(this);
    }

    void leave() {
        this.handle = null;
        this.world.getPlayers().remove(this);
        this.world = null;
        this.lastOnline = new TimeStamp();
        this.data.set("last_online", lastOnline.toString());
        statsManager.increment(Main.getStatistic("time_played"), lastOnline.minuteDifference(lastLogin));
        this.saveData();
        if (pet != null) {
            System.out.println("despawning pet");
            pet.kill();
        }
        if (mapCreator != null) {
            this.stopCreatingMap();
        }
        this.inventory = null;
    }

    /**
     * The "<code>Universal Unique Identifier</code>" of this player.
     */
    public UUID getUUID() {
        return uuid;
    }

    /**
     * @return The real name of the player.
     */
    public String getName() {
        return name;
    }

    @Override
    public CraftEntity getEntity() {
        return handle;
    }

    /**
     * Sends this player a message in chat.
     * @param msg The message to send
     */
    public void sendMessage(String msg) {
        if (!isOnline()) return;
        if (msg == null) return;
        this.handle.sendMessage(msg);
    }

    /**
     * Sends this player a message in chat, with {@link String#format(String, Object...)} args.
     * @param msg The message
     * @param formatArgs The format objects.
     */
    public void sendMessage(String msg, Object... formatArgs) {
        sendMessage(String.format(msg,formatArgs));
    }

    /**
     * The name displayed for other players on the server of this player. Consists of <code>Nick rank prefix + Nick rank name color + Nickname</code>
     * @return The display name. For example, <span style="color: blue">[Super]</span><span style="color: #6495ED"> TheShinyBunny</span>
     */
    public String getDisplayName() {
        return nick.getDisplayName();
    }

    /**
     * The real name displayed for other players in parties of this player. Consists of <code>Rank prefix + Rank name color + Name</code>
     * @return The real display name. For example, <span style="color: red">[Owner] TheShinyBunny</span>
     */
    public String getRealDisplayName() {
        return nick.getRealDisplayName();
    }

    /**
     * Sends the player a message like using /tellraw player {"some":"json","here":true}
     * @param json The json string
     */
    public void sendJsonMessage(String json) {
        if (this.handle == null) {
            return;
        }
        this.sendPacket(new PacketPlayOutChat(ChatSerializer.a(json)));
    }

    /**
     * Whether this player is a member of a party
     * @return Whether {@link #party} != null
     */
    public boolean isInParty() {
        return party != null;
    }

    /**
     * Leaves the players current party (sets {@link #party} = null)
     */
    public void leaveParty() {
        this.party = null;
    }

    /**
     * The current {@link Party} this player is a member of.
     */
    public Party getParty() {
        return party;
    }

    /**
     * Sets the players current party.
     * @param party The party to be in
     */
    public void setParty(Party party) {
        this.party = party;
    }

    /**
     * The current {@link IGameManager game} this player is playing in. If the player left that game but can rejoin, it won't be set to null until the player connects to a new game and it ends.
     */
    public IGameManager getCurrentGame() {
        return currentGame;
    }

    public void kick(String msg) {
        if (handle != null) {
            handle.kickPlayer(msg);
        }
    }

    @Override
    public EntityType getType() {
        return EntityType.PLAYER;
    }

    /**
     * Whether this player is currently connected to the server and his {@link #handle} is not null.
     */
    public boolean isOnline() {
        return handle != null;
    }

    public boolean isOp() {
        return handle != null && handle.isOp();
    }

    public Rank getRank() {
        return rank;
    }

    /**
     * Returns the player's NMS {@link EntityPlayer}.
     * @return {@link CraftPlayer#getHandle() handle.getHandle()}
     */
    public EntityPlayer getNMS() {
        if (!isOnline()) return null;
        return handle.getHandle();
    }

    /**
     * Returns the player's connection
     * @return {@link EntityPlayer#playerConnection handle.getHandle().playerConnection}
     */
    public PlayerConnection getConnection() {
        if (!isOnline()) return null;
        return handle.getHandle().playerConnection;
    }

    /**
     * @return The current location of the player entity, or null if the player is not online.
     */
    public Location getLocation() {
        return handle == null ? null : handle.getLocation();
    }

    /**
     * The world the player is currently found in
     * @return The ExtronWorld of the player
     */
    public ExtronWorld getWorld() {
        return world;
    }

    /**
     * Executes a command by the player.
     * @param cmd The raw command to execute.
     */
    public void executeCommand(String cmd) {
        if (!isOnline()) return;
        if (!Main.getCommandManager().handle(this,cmd)) {
            System.out.println("executing vanilla command " + cmd);
            handle.performCommand(cmd);
            this.dropItems = false; // FIXME: 7/5/2018 depend on current game
        }
    }

    /**
     * Sends a private message from this player to the last player who communicated with this player via private messages.
     * @param msg The message to send
     */
    public void sendPrivateMessage(String msg) {
        if (!isOnline() || this.privateMassaging == null || this.privateMassaging.handle == null) return;
        this.privateMassaging.receivePrivateMessage(this,msg);
        this.sendMessage(ChatColor.LIGHT_PURPLE + "[Private] " + ChatColor.GREEN + "To: " + privateMassaging.getName() + ": " + msg);
    }

    /**
     * Called by another player who attempted to send a private message to this player.
     * @param sender The player send the message
     * @param msg The message itself
     */
    public void receivePrivateMessage(ExtronPlayer sender, String msg) {
        if (this.privateMassaging == null || this.privateMassaging.handle == null || !sender.equals(this.privateMassaging)) return;
        this.sendMessage(ChatColor.LIGHT_PURPLE + "[Private] " + ChatColor.RED + "From: " + sender.getName() + ": " + msg);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ExtronPlayer) {
            return this.uuid.equals(((ExtronPlayer) obj).uuid);
        }
        return false;
    }

    /**
     * Changes the player's world.
     * <p>
     *     This method does many things to the player in order to not mess up the server and bring to the new world
     *     and changes made in the previous one.<br/>
     *     It teleports the player to the new world, and then resets its health, xp, food level, inventory, etc.
     * </p>
     * <b>Please use this method to change the player's world, and use {@link #setWorld(ExtronWorld)} just in case no reset is necessary.</b>
     * @param ew The new world to teleport to.
     */
    public void changeWorld(ExtronWorld ew) {
        System.out.println("changing world");
        this.handle.teleport(ew.getSpawnPoint(), PlayerTeleportEvent.TeleportCause.NETHER_PORTAL);
        this.inventory.clear();
        this.handle.setFoodLevel(20);
        this.handle.setHealth(20.0);
        this.handle.setVelocity(new Vector(0,0,0));
        this.handle.setLevel(0);
        this.handle.setExp(0);
        this.clearPotionEffects();
        this.getWorld().getPlayers().remove(this);
        this.stopCreatingMap();
        if (this.currentGame != null) {
            this.currentGame.onPlayerLeave(this);
            this.currentGame = null;
        }
        if (ew.equals(Main.getLobby())) {
            this.updateScoreboard();
            this.inventory.init();
            if (pet != null) {
                pet.spawn();
            }
        } else {
            if (pet != null) {
                pet.kill();
            }
        }
        this.setWorld(ew);
        this.handle.closeInventory();
        this.cancelParkour();
        ew.getPlayers().add(this);
        this.updateXpBar();
        this.cancelGadgetCooldown();
    }

    /**
     * Sets the player's world object to the specified ExtronWorld. Might be useful inside games to disable reset of the health, inventory and other stuff done by the {@link #changeWorld(ExtronWorld)} method.
     * @param ew
     */
    public void setWorld(ExtronWorld ew) {
        this.world = ew;
    }

    /**
     * Clears the player's active potion effects.
     */
    public void clearPotionEffects() {
        for (PotionEffect effect : handle.getActivePotionEffects()) {
            handle.removePotionEffect((effect).getType());
        }
    }

    /**
     * @return The player's inventory. Used for buttons and special items.
     */
    public PlayerInventory getInventory() {
        return inventory;
    }

    /**
     * Sets the player's current game manager
     * @param currentGame
     */
    public void setCurrentGame(GameManager currentGame) {
        this.currentGame = currentGame;
    }

    /**
     * Disables the player's ability to fly, if it had such ability.
     */
    public void disableFlight() {
        if (handle == null) return;
        handle.setFlying(false);
        handle.setAllowFlight(false);
    }

    /**
     * Sends a packet to the EntityPlayer represented by this player via the PlayerConnection.
     * @param packet
     */
    public void sendPacket(Packet packet) {
        this.getConnection().sendPacket(packet);
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    /**
     * Opens an inventory menu for the player.
     * <ul>
     *     <li>If the player is offline, does nothing.</li>
     *     <li>If the previous open menu is not null, closes that inventory first.</li>
     *     <li>If the specified menu is not null and its owner is not null, does nothing.</li>
     *     <li>Changes the openMenu field to the specified menu</li>
     *     <li>Sets the menu's owner to this player</li>
     *     <li>Calls the {@link InventoryMenu#init()} method</li>
     *     <li>Creates the bukkit inventory out of the menu and opens it.</li>
     * </ul>
     * @param menu The menu to open. use null to close the opened inventory.
     */
    public void openInventory(InventoryMenu menu) {
        if (!isOnline()) return;
        closeInventory();
        if (menu != null) {
            if (menu.getOwner() != null) return;
            this.openMenu = menu;
            menu.setOwner(this);
            menu.init();
            Inventory inv = menu.createInventory();
            this.getNMS().openContainer(inv.getHandle());
        }
    }

    public void closeInventory() {
        if (!isOnline()) return;
        if (openMenu != null && !openMenu.refreshing) {
            openMenu.onClose();
            openMenu.setOwner(null);
            openMenu = null;
            handle.closeInventory();
        }
    }

    public void setBan(Punishment ban) {
        this.ban = ban;
        if (ban != null) {
            this.ban.save();
        }
    }

    public boolean isBanned() {
        return ban != null;
    }

    public Punishment getBan() {
        return ban;
    }

    public void setMute(Punishment mute) {
        this.mute = mute;
        if (mute != null) {
            this.mute.save();
        }
    }

    public boolean isMuted() {
        return mute != null;
    }

    public Punishment getMute() {
        return mute;
    }

    public int getLevel() {
        return level;
    }

    public int getCoins() {
        return coins;
    }

    public int getXp() {
        return xp;
    }

    /**
     * Adds coins to the player's coins parameter.<br/>
     * If the value is negative, checks if the player has enough coins to remove that amount.<br/>
     * Updates the PlayerData and the scoreboard.
     * @param amount The amount of coins.
     * @return True, unless the player haven't had enough coins in case of a negative parameter.
     */
    public boolean addCoins(int amount) {
        if (amount >= 0 || this.hasEnoughCoins(-amount)) {
            this.coins += amount;
            this.data.set("coins",coins);
            this.updateScoreboard();
            return true;
        }
        return false;
    }

    /**
     * Changes the amount of coins of the player. Does not check for negative values.
     * @param coins
     */
    public void setCoins(int coins) {
        this.coins = coins;
        this.data.set("coins",coins);
        this.updateScoreboard();
    }

    /**
     * Whether the player's coins is bigger or equal to the given price
     * @param price The price to check
     * @return Result of <code>price &lt;= this.coins</code>
     */
    public boolean hasEnoughCoins(int price) {
        return price <= this.coins;
    }

    public void removeCoins(int amount) {
        this.coins -= amount;
        this.data.set("coins",coins);
        this.updateScoreboard();
    }

    /**
     * Performs a reset of the title to the player (like /title @p reset) and sends a new title, and a subtitle. The title & subtitle parameters should be formatted with the {@link ChatColor} format.
     * @param title The title
     * @param subtitle The sub title
     */
    public void sendTitle(String title, String subtitle) {
        if (this.handle == null) return;
        resetTitle();
        IChatBaseComponent ptitle = ChatSerializer.a(new TextUtils.JsonBuilder().formattedText(title).toString());
        IChatBaseComponent psubtitle = ChatSerializer.a(new TextUtils.JsonBuilder().formattedText(subtitle).toString());
        PacketPlayOutTitle packet = new PacketPlayOutTitle(EnumTitleAction.TITLE, ptitle);
        PacketPlayOutTitle packet2 = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, psubtitle);

        this.sendPacket(packet);
        this.sendPacket(packet2);
    }
    /**
     * Performs a reset of the title to the player (like /title @p reset) and sends a new title, a subtitle and the fade in, stay and fade out. The title & subtitle parameters should be formatted with the {@link ChatColor} things.
     * @param title The title
     * @param subtitle The sub title
     * @param fadeIn How many ticks the title takes to have full opacity
     * @param fadeOut How many ticks the title takes to lose all opacity and disappear
     * @param stay How many ticks the title should stay in full opacity after it faded in and before it will fade out.
     */
    public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        if (this.handle == null) return;
        resetTitle();
        PacketPlayOutTitle packet3 = new PacketPlayOutTitle(fadeIn, stay, fadeOut);
        this.sendPacket(packet3);
        IChatBaseComponent psubtitle = ChatSerializer.a(new TextUtils.JsonBuilder().formattedText(subtitle).toString());
        PacketPlayOutTitle packet2 = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, psubtitle);
        IChatBaseComponent ptitle = ChatSerializer.a(new TextUtils.JsonBuilder().formattedText(title).toString());
        PacketPlayOutTitle packet = new PacketPlayOutTitle(EnumTitleAction.TITLE, ptitle);
        this.sendPacket(packet);
        this.sendPacket(packet2);
    }

    /**
     * Resets the player's title, the equivalent to /title @p reset.
     */
    public void resetTitle() {
        PacketPlayOutTitle packet = new PacketPlayOutTitle(EnumTitleAction.RESET,null);
        this.sendPacket(packet);
    }

    /**
     * Sends an actionbar text (above the health bar) to the player.
     * @param text The text to display (formatted with ChatColor)
     */
    public void sendActionBar(String text) {
        if (this.handle == null) return;
        PacketPlayOutChat packet = new PacketPlayOutChat(ChatSerializer.a("{\"text\":\"" + text + "\"}"), (byte)2);
        this.sendPacket(packet);
    }

    /**
     * Finds the best open game of the specified game mode, and if no open games are available, creates a new one.
     * @param gm The game mode
     */
    public void findGame(GameMode gm) {
        List<GameManager> opens = new ArrayList<>();
        List<GameManager> empty = new ArrayList<>();
        if (gm == null) {
            this.sendMessage("Game mode does not exist!");
            return;
        }
        if (gm.getMaps() == null) {
            this.sendMessage("There are no maps!");
            return;
        }
        for (ExtronWorld map : gm.getMaps()) {
            GameManager manager = map.currentGame;
            if (manager == null) {
                empty.add(gm.createManager(map,new GameSettings(gm,this)));
            } else {
                if (manager.getState().canPlayersJoin() && manager.getWaiting().size() < gm.getMaxPlayers()) {
                    opens.add(manager);
                }
            }
        }
        if (opens.isEmpty()) {
            GameManager m;
            if (empty.size() > 1) {
                Random r = new Random();
                m = empty.get(r.nextInt(empty.size()));
            } else if (empty.size() == 1) {
                m = empty.get(0);
            } else {
                m = null;
            }
            String s = this.joinGame(gm,m);
            if (s.isEmpty()) {
                EventManager.registerEvents(m);
                EventManager.callEvent(new GameCreatedEvent(this,m,gm));
            } else {
                this.sendMessage(ChatColor.RED + s);
            }
        } else {
            int i = 0;
            String s = "";
            for (GameManager m : opens) {
                if (party == null || m.getOpenSlotsToJoin() >= party.getAllPlayers().size()) {
                    if (m.getWaiting().size() > i) {
                        i = m.getWaiting().size();
                        s = this.joinGame(gm,m);
                        if (s.isEmpty()) {
                            return;
                        }
                    }
                }
            }
            this.sendMessage(ChatColor.RED + s);
        }
    }

    /**
     * Tries to join the player into a game.
     * @param gm The game mode of the game
     * @param manager The game manager to join
     * @return An error message in case can't enter that game. Will be a literal empty ("") string if no errors were thrown.
     */
    public String joinGame(GameMode gm, @Nullable GameManager manager) {
        if (manager == null) {
            return "There are currently no open " + gm.getName() + " games for you to join!\nTry again in a few minutes.";
        } else {
            try {
                manager.tryJoinPlayer(this);
                this.sendMessage(ChatColor.GREEN + "Sending you to play " + gm.getName());
            } catch (GameJoinException.PartyCantFit e) {
                return "Your party doesn't fit in this game!";
            } catch (GameJoinException.GameStarted e) {
                return "This game had already started!";
            } catch (GameJoinException.NotTheLeader e) {
                return "You must be the party leader to join games!";
            } catch (GameJoinException.PartiesNotAllowed e) {
                return "This game doesn't support parties!";
            } catch (GameJoinException.PartyPlayersOffline e) {
                return "Can't join a game, because " + TextUtils.listNamesNicely(e.getPlayers()) + (e.getPlayers().size() == 1 ? " is " : " are ") + "not online!";
            } catch (GameJoinException e) {
                return "Can't join this game right now!";
            }
        }
        return "";
    }

    public void playSound(Sound sound) {
        if (this.handle == null) {
            return;
        }
        this.handle.playSound(this.handle.getLocation(),sound,1.0f,0.5f);
    }

    public void playSound(Sound sound,float volume, float pitch) {
        if (this.handle == null) {
            return;
        }
        this.handle.playSound(this.handle.getLocation(),sound,volume,pitch);
    }

    /**
     * Sends a tab list header and footer.
     * @param header The text above the player list
     * @param footer The text below the player list
     */
    public void sendTabList(String header, String footer) {
        if (handle == null) return;
        PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();

        Reflection.setValue(packet, "a", ChatSerializer.a("{\"text\":\"" + header + "\"}"));
        Reflection.setValue(packet, "b", ChatSerializer.a("{\"text\":\"" + footer + "\"}"));
        this.sendPacket(packet);
    }

    @Override
    public String toString() {
        return this.uuid.toString();
    }

    /**
     * Adds an amount of xp points to the player, and levels him up while exceeds the {@link #getXpToNextLevel()}.
     * @param xp The amount of points to add.
     */
    public void addXP(int xp) {
        this.xp += xp;
        while (this.xp >= xpToNextLevel) {
            this.level++;
            this.sendMessage(ChatColor.AQUA + "You leveled up! You are now level " + ChatColor.GOLD + level);
            this.xp -= xpToNextLevel;
            this.xpToNextLevel = this.calcXpToNext(level);
        }
        this.data.set("level",level);
        this.data.set("xp",this.xp);
        this.updateXpBar();

    }

    /**
     * Adds an amount of levels to the player.
     * @param levels
     */
    public void addLevels(int levels) {
        this.level += levels;
        this.data.set("level",level);
        this.xpToNextLevel = this.calcXpToNext(level);
        this.updateXpBar();
    }

    public int calcXpToNext(int level) {
        return 100 + ((level - 1) * 10);
    }

    /**
     * The amount of xp points needed for the player to level up.
     */
    public int getXpToNextLevel() {
        return xpToNextLevel;
    }

    /**
     * Updates the player's XP bar (above the hotbar) according to the current xp level, only if the player is in the lobby.
     */
    public void updateXpBar() {
        if (this.world.isLobby() && this.handle != null) {
            this.handle.setExp(0);
            this.handle.setLevel(1000);
            int i = this.handle.getExpToLevel();
            int j = (this.xp * 100) / xpToNextLevel;
            int k = (j * i) / 100;
            this.handle.getHandle().giveExp(k);
            this.handle.setLevel(level);
        }
    }

    /**
     * The player's {@link JsonContainer} Data. Holds and saves information about the player to the database.<br/>
     * Very useful for external plugins to save data of the player, but used a lot by the API as well. Use this method to get or set data,
     * and/or {@link #setData(String, Object)}.
     */
    public PlayerData getData() {
        return data;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
        this.data.set("rank",rank.getId());
        this.updateScoreboard();
        this.nick.setRealRank(rank);
    }

    public void saveData() {
        this.data.save();
    }

    public void setData(String key, Object value) {
        this.data.set(key,value);
    }

    public StatisticManager getStatistics() {
        return statsManager;
    }

    /**
     * Called when the player steps on the START pressure plate of a parkour.
     * @param parkour The parkour the player is just about to start.
     */
    public void startParkour(Parkour parkour) {
        if (this.currentParkour != null) {
            if (this.currentParkour.equals(parkour)) {
                this.sendMessage(ChatColor.RED + "Resetted your timer to 0!");
            }
            this.cancelParkour();
        }
        this.sendMessage(ChatColor.GREEN + "Parkour started! Go to the finish line as fast as you can!");
        this.currentParkour = parkour;
        this.lastCheckpoint = parkour.getStart();
        this.currentParkourTime = 0;
    }

    /**
     * Resets the parkour timer and currentParkour = null.
     */
    public void cancelParkour() {
        this.currentParkour = null;
        this.lastCheckpoint = null;
        this.currentParkourTime = 0;
    }

    /**
     * Called when the player steps on a CHECKPOINT pressure plate in a parkour.
     * @param parkour The parkour the pressure plate belongs to
     * @param mark The parkour checkpoint mark
     */
    public void parkourCheckpoint(Parkour parkour, ParkourMark mark) {
        if (this.currentParkour != null) {
            if (this.currentParkour.equals(parkour)) {
                if (this.lastCheckpoint.isBefore(mark)) {
                    this.sendMessage(ChatColor.YELLOW + "You have reached checkpoint " + ChatColor.RED + "" + ChatColor.BOLD + "#" + mark.getNumber() + ChatColor.YELLOW + "! To go to the last checkpoint, type " + ChatColor.GOLD + "/parkour checkpoint");
                    this.lastCheckpoint = mark;
                }
            }
        }
    }

    /**
     * Called when the player steps on the END pressure plate in a parkour, to reset time, check for highscore and sends messages.
     * @param parkour
     */
    public void finishedParkour(Parkour parkour) {
        if (this.currentParkour == null || !this.lastCheckpoint.isBefore(parkour.getEnd())) {
            this.sendMessage(ChatColor.RED + "This is the finish line of this parkour!");
        } else {
            this.sendMessage(ChatColor.GREEN + "You have finished the parkour! Your time is " + TextUtils.ticksToTime(currentParkourTime) + "");
            List<JsonContainer> records = this.data.getObjectList("parkours");
            boolean newRecord = false;
            int prev = 0;
            if (records == null) {
                records = new ArrayList<>();
                JsonContainer r = new JsonContainer(new JsonObject());
                r.set("id",currentParkour.getId());
                r.set("record",currentParkourTime);
                records.add(r);
                newRecord = true;
            } else {
                boolean found = false;
                for (JsonContainer c : records) {
                    if (c.getInt("id",0) == currentParkour.getId()) {
                        found = true;
                        if (c.getInt("record",0) > currentParkourTime) {
                            newRecord = true;
                            c.set("record",currentParkourTime);
                        } else {
                            prev = c.getInt("record",0);
                        }
                    }
                }
                if (!found) {
                    JsonContainer r = new JsonContainer(new JsonObject());
                    r.set("id",currentParkour.getId());
                    r.set("record",currentParkourTime);
                    records.add(r);
                    newRecord = true;
                }
            }
            if (newRecord) {
                sendMessage(ChatColor.GOLD + "This is a new record!");
                this.data.set("parkours",records);
            } else {
                if (prev > 0) {
                    sendMessage(ChatColor.YELLOW + "You haven't beaten your record of " + TextUtils.ticksToTime(prev));
                }
            }
            this.cancelParkour();
        }
    }

    public InventoryMenu getOpenInventory() {
        return openMenu;
    }

    public void setHealth(int health) {
        if (this.handle != null) handle.setHealth(health);
    }

    public void setCollisionRule(boolean b) {
        if (this.handle != null) handle.spigot().setCollidesWithEntities(b);
    }

    public void enableFlight(boolean force) {
        if (handle == null) return;
        if (this.rank.isAboveOrEqual(Rank.HELPER) || force) {
            this.handle.setAllowFlight(true);
        }
    }

    /**
     * This method is called when a player clicks a "Return to the lobby" item.
     * It won't send the player immediately, but will set sendingToLobby = true, and after 3 seconds, will send the player to the lobby
     * - unless this method get called again beforehand.
     */
    public void sendToTheLobby() {
        if (this.sendingToLobby) {
            sendingToLobby = false;
            this.sendMessage(ChatColor.RED + "Teleporting cancelled!");
        } else {
            sendingToLobby = true;
            this.sendMessage(ChatColor.GREEN + "Teleporting you to the main lobby!\nClick again to cancel.");
        }
    }

    /**
     * Teleports this player to the server's lobby.
     */
    public void teleportToLobby() {
        if (handle == null) return;
        if (world.equals(Main.getLobby())) {
            this.handle.teleport(Main.getLobby().getSpawnPoint());
            this.reloadInventory();
        } else {
            this.changeWorld(Main.getLobby());
        }
    }

    /**
     * The current parkour the player is playing
     * @return The {@link Parkour} object managing all checkpoints of the parkour course.
     */
    public Parkour getCurrentParkour() {
        return currentParkour;
    }

    /**
     * The last {@link ParkourMark checkpoint} of a parkor the player has visited during a parkour course.
     */
    public ParkourMark getLastCheckpoint() {
        return lastCheckpoint;
    }

    /**
     * The number of times the player has logged in to the server.
     */
    public int getTimesLogin() {
        return getStatistics().get(Main.getStatistic("times_login"));
    }

    /**
     * A nice string representation of how long ago the player has disconnected from the server.
     */
    public String getLastOnline() {
        return new TimeStamp().howLongAgo(lastOnline,2);
    }

    /**
     * The string representation of the player's first login date.
     */
    public String getFirstLogin() {
        return firstLogin.toString();
    }

    public Rank getNickRank() {
        return nick.getNickRank();
    }

    public boolean isNicked() {
        return nick.isNicked();
    }

    /**
     * The player's {@link PlayerNick nick manager} that manages and stores the player's name, rank, nick name and nick rank.
     */
    public PlayerNick getNickManager() {
        return nick;
    }

    public EntityDamageByEntityEvent getLastEntityDamagedEvent() {
        return lastEntityDamaged;
    }

    public EntityDamageEvent getLastDamage() {
        return lastDamage;
    }

    public int getTicksSinceLastDamage() {
        return damageTime == null ? -1 : new TimeStamp().secondDifference(damageTime) * 20;
    }

    public int getTicksSinceLastEntityDamage() {
        return entityDamagedTime == null ? -1 : new TimeStamp().secondDifference(entityDamagedTime) * 20;
    }

    public Entity getLastDamager() {
        return lastEntityDamaged == null ? null : lastEntityDamaged.getDamager();
    }

    public void setLastDamagedEntity(EntityDamageByEntityEvent e) {
        this.lastEntityDamaged = e;
        this.entityDamagedTime = new TimeStamp();
    }

    public void setLastDamage(EntityDamageEvent e) {
        this.lastDamage = e;
        this.damageTime = new TimeStamp();
    }

    public void showMainScoreboard() {
        this.scoreboardManager.updateScoreboard(Main.getMainScoreboard());
    }

    public void showScoreboard(String id) {
        this.scoreboardManager.updateScoreboard(Main.getScoreboard(id));
    }

    /**
     * Makes the player open the specified book.
     * @param book The book to open
     */
    public void openBook(Book book) {
        if (handle == null) return;
        this.bookToOpen = book;
        this.bookOpenTimer = 0;
    }

    @Override
    public boolean shouldUpdate(Scoreboard sb) {
        return sb instanceof MainScoreboard;
    }

    @Override
    public void update(Scoreboard sb) {
        this.scoreboardManager.tryUpdate(sb);
    }

    /**
     * The current cooldown of the last activated gadget.
     * @return The cooldown in seconds.
     */
    public int getGadgetCooldown() {
        return gadgetCooldown;
    }

    /**
     * Starts the gadget cooldown on a gadget.
     * @param g The gadget
     */
    public void startGadgetCooldown(Gadget g) {
        this.gadgetCooldown = g.getCooldown() * 20;
        this.hasGadgetCooldown = true;
    }

    /**
     * Cancels the current gadget cooldown.
     */
    public void cancelGadgetCooldown() {
        this.gadgetCooldown = 0;
        this.hasGadgetCooldown = false;
    }

    /**
     * The {@link Gadget} the player has currently selected.
     */
    public Gadget getSelectedGadget() {
        return (Gadget) collection.getSelected(LobbyCollectibleType.GADGET);
    }

    /**
     * The player collection manager, that manages all collectibles owned by the player.
     */
    public PlayerCollection getCollection() {
        return collection;
    }

    /**
     * Gets the selected collectible of the specified type.
     * @param type The type of collectible
     * @return The selected {@link Selectable} collectible of that type.
     */
    public Selectable getSelectedCollectible(CollectibleType type) {
        return collection.getSelected(type);
    }

    /**
     * Clears and re-initialize the {@link PlayerInventory player's inventory}.
     */
    public void reloadInventory() {
        if (!isOnline()) return;
        this.inventory.clear();
        this.inventory.init();
    }

    public GameProfile getProfile() {
        return handle == null ? null : handle.getProfile();
    }

    /**
     * The player's {@link EntityPlayer#getId() entity id}
     * @return
     */
    public int getEntityID() {
        return handle == null ? 0 : handle.getEntityId();
    }

    public String getNickName() {
        return nick.getNickName();
    }

    /**
     * Whether the player has found the specified collectible.
     * @param c The collectible to test
     * @return True if the player's collection contains that collectible.
     */
    public boolean foundCollectible(Collectible c) {
        return collection.contains(c);
    }

    public void setInvisible(boolean invisible) {
        for (ExtronPlayer p : PlayerList.getOnlinePlayers()) {
            p.sendPacket(new PacketPlayOutPlayerInfo(invisible ? EnumPlayerInfoAction.REMOVE_PLAYER : EnumPlayerInfoAction.ADD_PLAYER, getNMS()));
        }
        this.invisible = invisible;
    }

    public boolean isInvisible() {
        return invisible;
    }

    public void strikeLightning() {
        world.handle.strikeLightningEffect(getLocation());
    }

    /**
     * The current slot index the player is holding in the hotbar
     */
    public int getSelectedSlot() {
        return handle == null ? -1 : handle.getInventory().getHeldItemSlot();
    }

    /**
     * Gives the player an item.
     * @param m The type of item
     */
    public void give(Material m) {
        this.give(m,1);
    }

    /**
     * Gives the player an item.
     * @param m The type of item
     * @param count The amount of that item
     */
    public void give(Material m, int count) {
        this.give(m,count,0);
    }

    /**
     * Gives the player an item.
     * @param m The type of item
     * @param count The amount of that item
     * @param data The data value of the item
     */
    public void give(Material m, int count, int data) {
        this.give(m,count,data,null);
    }

    /**
     * Gives the player an item.
     * @param m The type of item
     * @param count The amount of that item
     * @param data The data value of the item
     * @param meta An optional method to change the item's meta.
     */
    public void give(Material m, int count, int data, Consumer<ItemMeta> meta) {
        ItemStack stack = new ItemStack(m,count, (short) data);
        if (meta != null) {
            ItemMeta im = stack.getItemMeta();
            meta.accept(im);
            stack.setItemMeta(im);
        }
        if (handle != null) {
            handle.getInventory().addItem(stack);
        }
    }

    public void give(ExtronStack stack) {
        if (!isOnline()) return;
        this.inventory.addItem(stack);
    }

    public void teleport(BlockPos pos) {
        if (!isOnline()) return;
        if (!pos.getWorld().equals(world)) {
            this.changeWorld(pos.getWorld());
        }
        this.handle.teleport(pos.toLocation());
    }

    public void stopCreatingMap() {
        if (mapCreator != null) {
            mapCreator.removeCreator(this);
            this.mapCreator = null;
        }
    }

    public void startCreatingMap(MapCreator creator) {
        if (creator != null) {
            System.out.println("starting map creation");
            creator.addCreator(this);
            this.changeWorld(creator.getMap());
            creator.getItems(this::giveButton);
            this.mapCreator = creator;
        }
    }

    public void giveButton(Button button) {
        if (!isOnline()) return;
        this.inventory.addButton(button);
    }

    public MapCreator getMapCreator() {
        return mapCreator;
    }

    public void setGadgetCooldown(int seconds) {
        this.hasGadgetCooldown = true;
        this.gadgetCooldown = seconds * 20;
    }
}