package com.extron.network.api.utils.settings;

import org.bukkit.Material;

public class BooleanSetting extends SettingBase<Boolean> {

    public BooleanSetting(Boolean def, String id, String name, String description, Material icon, int iconDamage) {
        super(def, id, name, description, icon, iconDamage);
    }

    @Override
    public Boolean toggle(Boolean value) {
        return !value;
    }

    @Override
    public Class<Boolean> getType() {
        return Boolean.TYPE;
    }

    @Override
    public int getIndicationIconDamage(Boolean val) {
        return val ? 10 : 8;
    }

    @Override
    public int getIconDamage() {
        return 0;
    }

    @Override
    public Material getIndicationIcon(Boolean val) {
        return Material.INK_SACK;
    }

    @Override
    public Boolean parseValue(String value) {
        return Boolean.parseBoolean(value);
    }
}
