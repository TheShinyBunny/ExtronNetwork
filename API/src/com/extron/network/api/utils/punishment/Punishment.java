package com.extron.network.api.utils.punishment;

import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.JsonContainer;
import com.extron.network.api.utils.Savable;
import com.extron.network.api.utils.TimeStamp;
import org.bukkit.ChatColor;


public class Punishment implements Savable<JsonContainer> {

    private final PunishType type;
    private ExtronPlayer player;
    private PunishReason reason;
    private TimeStamp created;
    private TimeStamp expires;

    public Punishment(ExtronPlayer p, PunishReason reason, TimeStamp created, TimeStamp expires) {
        this.player = p;
        this.reason = reason;
        this.created = created;
        this.expires = expires;
        this.type = reason.getType();
    }

    public Punishment(ExtronPlayer player, PunishType type) {
        this.player = player;
        this.type = type;
        this.load(player.getData().getJsonObject(type.getId()));
    }

    public static Punishment create(ExtronPlayer player, PunishType type) {
        if (player.getData().getJsonObject(type.getId()) != null) {
            return new Punishment(player,type);
        }
        return null;
    }

    public TimeStamp getExpiration() {
        return expires;
    }

    public ExtronPlayer getPlayer() {
        return player;
    }

    public PunishReason getReason() {
        return reason;
    }

    public PunishType getType() {
        return type;
    }

    public TimeStamp getCreated() {
        return created;
    }

    public void kickPlayer() {
        if (this.type != PunishType.BAN) {
            System.out.println("this is not a ban punishment!");
            return;
        }
        if (this.hasExpired()) {
            System.out.println("but the ban expired");
            return;
        }
        this.player.kick(this.getBanKickMessage());
    }

    public String getBanKickMessage() {
        StringBuilder b = new StringBuilder(ChatColor.RED + "You are banned from this server!\n");
        if (this.isPermanent()) b.append("This ban is " + ChatColor.DARK_RED + "PERMANENT!");
        else b.append(ChatColor.YELLOW + "This ban will expire in " + new TimeStamp().difference(expires));
        b.append(ChatColor.AQUA + "\nReason: " + ChatColor.GOLD + reason.getName());
        b.append(ChatColor.GREEN + "\nAppeal at www.extron.network/appeal");
        return b.toString();
    }

    public boolean isPermanent() {
        return this.expires.isNever();
    }

    public boolean hasExpired() {
        return expires.isInPast();
    }

    @Override
    public void load(JsonContainer obj) {
        reason = type.getReason(obj.getString("reason",type.getNoReason().toString()));
        created = new TimeStamp(obj.getString("created"));
        expires = new TimeStamp(obj.getString("expires"));
    }

    @Override
    public void save() {
        JsonContainer json = new JsonContainer();
        json.set("reason",reason);
        json.set("created",created);
        json.set("expires",expires);
        player.getData().set(type == PunishType.BAN ? "ban" : "mute",json);
    }

    public void remove() {
        player.getData().set(type.getId(),null);
        if (this.type == PunishType.BAN) {
            player.setBan(null);
        } else {
            player.setMute(null);
        }
    }
}
