package com.extron.network.api.permission;

import com.extron.network.api.utils.ListUtils;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class Rank {
    public static final Rank NONE = new Rank(0,"default","Default","");
    public static final Rank VENOS = new Rank(1,"venos","Venos",ChatColor.GRAY + "[Venos] ");
    public static final Rank SUPER = new Rank(2,"super","Super",ChatColor.BLUE + "[Super] ");
    public static final Rank ULTRA = new Rank(3,"ultra","Ultra",ChatColor.DARK_AQUA + "[Ultra] ");
    public static final Rank EXTRA = new Rank(4,"extra","Extra",ChatColor.LIGHT_PURPLE + "[Extra] ");
    public static final Rank HELPER = new Rank(5,"helper","Helper",ChatColor.GREEN + "[Helper] ");
    public static final Rank DEVELOPER = new Rank(6,"developer","Developer",ChatColor.YELLOW + "[Dev] ");
    public static final List<Rank> ALL = new ArrayList<>();

    private final int pos;
    private final String id;
    private final String name;
    private final String prefix;

    static {
        registerRank(NONE);
        registerRank(HELPER);
        registerRank(DEVELOPER);
    }

    public static Rank fromString(String id) {
        return ListUtils.firstMatch(ALL,r->r.id.equalsIgnoreCase(id));
    }

    public boolean isAboveOrEqual(Rank rank) {
        return this.pos >= rank.pos;
    }

    public Rank(int pos, String id, String name, String prefix) {
        this.pos = pos;
        this.id = id;
        this.name = name;
        this.prefix = prefix;
    }

    public static void registerRank(Rank r) {
        ALL.add(r);
    }

    /**
     * @return a permission for this rank, where only players with this rank or above are permitted.
     */
    public Permission getPermission() {
        return new PermissionRanked().min(this);
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public int getPos() {
        return pos;
    }

    public String getPrefix() {
        return prefix;
    }

    @Override
    public String toString() {
        return id;
    }

    public boolean isNickable() {
        return Rank.EXTRA.isAboveOrEqual(this);
    }

    public ChatColor getNameColor() {
        return ChatColor.WHITE; // FIXME: 7/2/2018 change
    }
}
