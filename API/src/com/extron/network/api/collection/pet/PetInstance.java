package com.extron.network.api.collection.pet;

import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.JsonContainer;
import net.minecraft.server.v1_8_R1.NavigationAbstract;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;

public interface PetInstance {

    ExtronPlayer getOwner();

    String getName();

    PetStats getStats();

    PetInstance.Status getStatus();

    void spawn();

    void kill();

    JsonContainer saveToJson();

    NavigationAbstract getPathNavigator();

    boolean isBeingRidden();

    CraftEntity getEntity();

    void mountOwner();

    void onPlayerDismount();

    enum Status {
        VERY_HAPPY(0, ChatColor.GREEN + "Very Happy"), HAPPY(1, ChatColor.DARK_GREEN + "Happy"),ALRIGHT(2,ChatColor.BLUE + "Alright"),SAD(3,ChatColor.YELLOW + "Sad"),SICK(4,ChatColor.RED + "Sick"),DEAD(5,ChatColor.DARK_GRAY + "DEAD");

        private final int index;
        private final String name;

        Status(int index, String name) {
            this.index = index;
            this.name = name;
        }

        public int getIndex() {
            return index;
        }

        public String getName() {
            return name;
        }
    }
}
