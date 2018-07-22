package com.extron.network.api.nick.skin;

import com.extron.network.api.players.ExtronPlayer;

import java.util.Optional;
import java.util.UUID;

public class UUIDResolver implements Runnable {

    private final String name;
    private final ExtronPlayer requester;

    public UUIDResolver(String name, ExtronPlayer requester) {
        this.name = name;
        this.requester = requester;
    }

    @Override
    public void run() {
        UUID uuid = SkinManager.getUUIDCache().get(name.toLowerCase());
        if (uuid == null) {
            try {
                Optional<UUID> optUUID = SkinManager.getUUID(name);
                if (optUUID.isPresent()) {
                    uuid = optUUID.get();
                    SkinManager.getUUIDCache().put(name.toLowerCase(),uuid);
                }
            } catch (Exception e) {
                if (requester == null) {
                    e.printStackTrace();
                } else {
                    requester.sendMessage(e.getMessage());
                }
            }
        }
        if (uuid != null) {
            Optional<Skin> s = SkinManager.downloadSkin(uuid);
            if (s.isPresent()) {
                SkinManager.getSkins().put(uuid,s.get());
                if (requester != null) {
                    SkinManager.changeSkin(requester,s.get());
                }
            } else if (requester == null) {
                System.out.println("Can't download skin");
            } else {
                requester.sendMessage("Error while downloading skin.");
            }
        }
    }
}
