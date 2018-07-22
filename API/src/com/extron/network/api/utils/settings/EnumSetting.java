package com.extron.network.api.utils.settings;

import org.bukkit.Material;

public class EnumSetting<E extends Enum<E>> extends SettingBase<E> {
    private final Class<E> clazz;

    public EnumSetting(Class<E> enumClass, E def, String id, String name, String description, Material icon, int iconDamage) {
        super(def, id, name, description, icon, iconDamage);
        this.clazz = enumClass;
    }

    @Override
    public Material getIndicationIcon(E val) {
        return null;
    }

    @Override
    public int getIndicationIconDamage(E val) {
        return 0;
    }

    @Override
    public E toggle(E value) {
        int i = value.ordinal();
        i++;
        i%=clazz.getEnumConstants().length;
        return clazz.getEnumConstants()[i];
    }

    @Override
    public Class<E> getType() {
        return clazz;
    }

    @Override
    public E parseValue(String value) {
        return Enum.valueOf(clazz,value.toUpperCase());
    }
}
