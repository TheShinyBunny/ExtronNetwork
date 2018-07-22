package com.extron.network.api.utils.settings;

import org.bukkit.Material;

public abstract class SettingBase<T> implements Setting<T> {
    protected final T def;
    private final String id;
    private final String name;
    private final String desc;
    private final Material icon;
    private final int iconDamage;

    public SettingBase(T def, String id, String name, String description, Material icon, int iconDamage) {
        this.def = def;
        this.id = id;
        this.name = name;
        this.desc = description;
        this.icon = icon;
        this.iconDamage = iconDamage;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDescription() {
        return desc;
    }

    @Override
    public T getDefaultValue() {
        return def;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getIconDamage() {
        return iconDamage;
    }

    @Override
    public Material getIcon() {
        return icon;
    }
}
