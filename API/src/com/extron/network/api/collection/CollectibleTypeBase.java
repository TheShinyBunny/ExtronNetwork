package com.extron.network.api.collection;

import com.extron.network.api.event.inventory.ButtonClickEvent;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.scoreboard.ScoreLines;
import com.extron.network.api.utils.CiPredicate;
import com.extron.network.api.utils.TextUtils;
import org.bukkit.Material;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class CollectibleTypeBase implements CollectibleType {

    private String id;
    protected String name;
    private Material icon;
    private int damage;
    private String description;
    protected CiPredicate<Collectible,ExtronPlayer,ButtonClickEvent> menuClick;
    protected BiConsumer<ExtronPlayer,Selectable> onSelect;
    protected Consumer<ExtronPlayer> onDeselect;

    public CollectibleTypeBase(String id) {
        this.id = id;
        this.name = TextUtils.capitalize(id);
        this.description = "No description!";
        this.icon = Material.THIN_GLASS;
        this.damage = 14;
    }

    public CollectibleTypeBase setName(String name) {
        this.name = name;
        return this;
    }

    public CollectibleTypeBase setDescription(String description) {
        this.description = description;
        return this;
    }

    public CollectibleTypeBase setIcon(Material icon) {
        this.icon = icon;
        this.damage = 0;
        return this;
    }

    public CollectibleTypeBase setIcon(Material icon, int damage) {
        this.icon = icon;
        this.damage = damage;
        return this;
    }

    public CollectibleTypeBase setMenuClick(CiPredicate<Collectible, ExtronPlayer, ButtonClickEvent> menuClick) {
        this.menuClick = menuClick;
        return this;
    }

    public CollectibleTypeBase setOnDeselect(Consumer<ExtronPlayer> onDeselect) {
        this.onDeselect = onDeselect;
        return this;
    }

    public CollectibleTypeBase setOnSelect(BiConsumer<ExtronPlayer, Selectable> onSelect) {
        this.onSelect = onSelect;
        return this;
    }

    public BiConsumer<ExtronPlayer, Selectable> getOnSelect() {
        return onSelect;
    }

    public CiPredicate<Collectible, ExtronPlayer, ButtonClickEvent> getMenuClick() {
        return menuClick;
    }

    public Consumer<ExtronPlayer> getOnDeselect() {
        return onDeselect;
    }

    @Override
    public Material getIcon() {
        return icon;
    }

    @Override
    public int getIconDamage() {
        return damage;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDisplayName() {
        return "";
    }

}
