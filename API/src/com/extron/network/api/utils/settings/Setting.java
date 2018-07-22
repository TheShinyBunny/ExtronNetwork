package com.extron.network.api.utils.settings;

import org.bukkit.Material;

public interface Setting<T> {

    String getId();

    String getDescription();

    T getDefaultValue();

    Material getIcon();

    int getIconDamage();

    Material getIndicationIcon(T val);

    int getIndicationIconDamage(T val);

    T toggle(T value);

    String getName();

    Class<T> getType();

    T parseValue(String value);
}
