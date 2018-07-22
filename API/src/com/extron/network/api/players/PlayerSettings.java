package com.extron.network.api.players;

import com.extron.network.api.utils.DataObject;
import com.extron.network.api.utils.settings.BooleanSetting;
import com.extron.network.api.utils.settings.SettingGroup;
import org.bukkit.Material;

public class PlayerSettings extends SettingGroup {

    private ExtronPlayer player;

    public PlayerSettings(ExtronPlayer player) {
        super();
        this.player = player;
    }

    @Override
    public void initializeSettings() {
        addSetting(new BooleanSetting(true,
                "player_visible",
                "Player Visibility",
                "Toggles visibility of other players",
                Material.GOLDEN_CARROT,0));
        addSetting(new BooleanSetting(true,
                "lobby_command_protect",
                "/lobby protection",
                "Whether you should type the '/lobby' command twice to teleport back to the lobby",
                Material.COMMAND_MINECART,0));

    }

    public String getSavePath() {
        return "settings";
    }

    @Override
    public DataObject getDataObject() {
        return player.getData();
    }
}
