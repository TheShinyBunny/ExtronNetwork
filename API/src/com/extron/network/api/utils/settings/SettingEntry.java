package com.extron.network.api.utils.settings;

public class SettingEntry<T> {

    private Setting<T> setting;
    private T value;

    public SettingEntry(Setting<T> setting) {
        this.setting = setting;
        this.value = setting.getDefaultValue();
    }

    public T getValue() {
        return value;
    }

    public Setting<T> getSetting() {
        return setting;
    }

    public T toggle() {
        this.value = setting.toggle(value);
        return value;
    }

    public void setStringValue(String value) {
        this.value = this.setting.parseValue(value);
    }

    public void setDefaultValue() {
        this.value = setting.getDefaultValue();
    }
}
