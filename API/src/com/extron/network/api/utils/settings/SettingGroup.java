package com.extron.network.api.utils.settings;

import com.extron.network.api.utils.DataObject;
import com.extron.network.api.utils.ListUtils;
import com.extron.network.api.utils.Savable;

import java.util.ArrayList;
import java.util.List;

public abstract class SettingGroup implements Savable<DataObject> {

    private List<SettingEntry<?>> settings;

    public SettingGroup() {
        this.settings = new ArrayList<>();
        initializeSettings();
    }

    public abstract void initializeSettings();

    public void addSetting(Setting<?> setting) {
        this.settings.add(new SettingEntry<>(setting));
    }

    public <T> T getValue(String id, Class<T> type) {
        SettingEntry<T> entry = getEntry(id,type);
        return entry == null ? null : entry.getValue();
    }

    private <T> T getValue(Setting<T> setting) {
        SettingEntry<?> entry = getEntry(setting);
        if (entry == null) return null;
        if (setting.getType().isInstance(entry.getValue())) {
            return (T) entry.getValue();
        }
        return null;
    }


    public <T> SettingEntry<?> getEntry(Setting<T> setting) {
        return ListUtils.firstMatch(settings,e->e.getSetting().getId().equalsIgnoreCase(setting.getId()));
    }

    public <T> SettingEntry<T> getEntry(String id, Class<T> type) {
        SettingEntry<?> entry = ListUtils.firstMatch(settings,e->e.getSetting().getId().equalsIgnoreCase(id));
        if (entry == null) return null;
        if (type.isInstance(entry.getValue())) {
            return (SettingEntry<T>) entry;
        }
        return null;
    }

    public <T> T toggleSetting(Setting<T> setting) {
        SettingEntry<?> entry = getEntry(setting);
        if (entry != null) {
            return (T) entry.toggle();
        }
        return null;
    }

    public <T> T get(String id, Class<T> type) {
        SettingEntry<T> e = getEntry(id,type);
        if (e == null) return null;
        return e.getValue();
    }

    @Override
    public void save() {
        DataObject obj = getDataObject();
        for (SettingEntry<?> e : settings) {
            obj.set(getSavePath() + "." + e.getSetting().getId(),e.getValue().toString());
        }
    }

    @Override
    public void load(DataObject obj) {
        for (SettingEntry e : settings) {
            String value = obj.getString(getSavePath() + "." + e.getSetting().getId());
            if (value == null) {
                e.setDefaultValue();
            } else {
                e.setStringValue(value);
            }
        }
    }

    public abstract String getSavePath();

    public abstract DataObject getDataObject();

}
