package com.extron.network.api.config;

import com.extron.network.api.Main;
import com.extron.network.api.utils.ListUtils;
import org.apache.commons.io.FilenameUtils;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ConfigFolder implements Iterable<Config> {

    private File folder;
    private List<Config> configs;

    public ConfigFolder(String path) {
        this(new File(Main.INSTANCE.getDataFolder(),path));
    }

    public ConfigFolder(File folder) {
        this.folder = folder;
        this.configs = new ArrayList<>();
        if (!folder.exists()) {
            folder.mkdirs();
        }
        for (File f : folder.listFiles()) {
            if (FilenameUtils.getExtension(f.getName()).equalsIgnoreCase("yml")) {
                Config c = new Config(f,FilenameUtils.removeExtension(f.getName()),false);
                this.configs.add(c);
            }
        }
        for (Config c : configs) {
            c.load();
        }
    }

    public File getFolder() {
        return folder;
    }

    public List<Config> getConfigs() {
        return configs;
    }

    public Config createConfig(String name) {
        if (getConfig(name) != null) return getConfig(name);
        File f = new File(this.folder,name + ".yml");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Config c = new Config(f,name,true);
        if (!this.hasConfig(c)) {
            this.configs.add(c);
        }
        return c;
    }

    public boolean hasConfig(Config c) {
        return getConfig(c.getName()) != null;
    }

    public Config getConfig(String name) {
        return ListUtils.firstMatch(configs, c->c.getName().equalsIgnoreCase(name));
    }

    @Nonnull
    @Override
    public Iterator<Config> iterator() {
        return this.configs.iterator();
    }
}
