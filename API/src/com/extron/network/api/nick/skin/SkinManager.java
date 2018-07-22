package com.extron.network.api.nick.skin;

import com.extron.network.api.Main;
import com.extron.network.api.config.Config;
import com.extron.network.api.nick.PlayerProfile;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.players.PlayerList;
import com.extron.network.api.utils.tasks.NetworkUtils;
import com.google.common.net.HttpHeaders;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.util.UUIDTypeAdapter;
import net.minecraft.server.v1_8_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.entity.Entity;

import javax.naming.InvalidNameException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

public class SkinManager {

    private static Config skinsConfig;
    private static Map<UUID,Skin> skins;
    private static Map<String,UUID> savedUUIDs;
    private static final Pattern validNamePattern = Pattern.compile("^\\w{2,16}$");

    private static Gson gson = new GsonBuilder().registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create();

    private static final String UUID_URL = "https://api.mojang.com/users/profiles/minecraft/";
    private static final String SKIN_URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false";

    public static void init() {
        //skinsConfig = new Config("skins");
        skins = new HashMap<>();
        savedUUIDs = new HashMap<>();
    }

    public static void changeSkin(ExtronPlayer p, String playerName) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.INSTANCE,new UUIDResolver(playerName,p));
    }

    public static void changeSkin(ExtronPlayer p, Skin skin) {
        GameProfile profile = p.getProfile();
        profile.getProperties().clear();
        if (skin != null) {                         // "textures"
            profile.getProperties().put(SkinProperty.SKIN_KEY,new Property(SkinProperty.SKIN_KEY,skin.getEncodedValue(),skin.getSignature()));
        }
        updatePlayer(p);
    }

    public static void downloadSkin(String playerName) throws InvalidNameException, IOException {
        Optional<UUID> uuid = getUUID(playerName);
        if (uuid.isPresent()) {
            Optional<Skin> s = downloadSkin(uuid.get());
            s.ifPresent(skin -> skins.put(uuid.get(), skin));
        }
    }

    public static Map<String, UUID> getUUIDCache() {
        return savedUUIDs;
    }

    public static Optional<Skin> downloadSkin(UUID uuid) {
        if (skins.get(uuid) != null) {
            return Optional.of(skins.get(uuid));
        }
        String mojandUUID = UUIDTypeAdapter.fromUUID(uuid);
        try {
            HttpURLConnection connection = NetworkUtils.openConnection(String.format(SKIN_URL,mojandUUID));
            connection.setRequestProperty(HttpHeaders.CONTENT_TYPE,"application/json");
            if (connection.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT) {
                return Optional.empty();
            }

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))
            ) {
                return parseSkinTexture(reader);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private static Optional<Skin> parseSkinTexture(Reader reader) {
        TexturesModel model = gson.fromJson(reader,TexturesModel.class);
        SkinProperty[] properties = model.getProperties();
        if (properties != null && properties.length > 0) {
            SkinProperty propertiesModel = properties[0];

            String encodedSkin = propertiesModel.getValue();
            String signature = propertiesModel.getSignature();

            return Optional.of(Skin.from(encodedSkin, signature));
        }

        return Optional.empty();
    }

    public static Optional<UUID> getUUID(String name) throws InvalidNameException, IOException {
        System.out.println("resolving UUID of player name " + name);
        if (!validNamePattern.matcher(name).matches()) {
            throw new InvalidNameException("Invalid name pattern " + name);
        }
        HttpURLConnection connection = NetworkUtils.openConnection(UUID_URL + name);
        connection.setRequestProperty(HttpHeaders.CONTENT_TYPE,"application/json");
        return getUUID(connection,name);
    }

    public static Optional<UUID> getUUID(HttpURLConnection connection, String name) throws IOException, InvalidNameException {
        int response = connection.getResponseCode();
        if (response == HttpURLConnection.HTTP_NO_CONTENT) {
            throw new InvalidNameException("Unknown name " + name);
        }
        if (response == HttpURLConnection.HTTP_OK) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),StandardCharsets.UTF_8))){
                return parseUUID(reader);
            }
        }
        return Optional.empty();
    }

    public static Optional<UUID> parseUUID(Reader reader) {
        PlayerProfile playerProfile = gson.fromJson(reader,PlayerProfile.class);
        return Optional.of(playerProfile.getId());
    }

    public static void updatePlayer(ExtronPlayer p) {
        Optional.ofNullable(p.handle.getVehicle()).ifPresent(Entity::eject);

        p.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER,p.getNMS()));
        p.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER,p.getNMS()));
        p.sendPacket(new PacketPlayOutNamedEntitySpawn(p.getNMS()));

        for (ExtronPlayer other : PlayerList.getOnlinePlayers()) {
            other.handle.hidePlayer(p.handle);
            other.handle.showPlayer(p.handle);
        }
    }

    public static Map<UUID, Skin> getSkins() {
        return skins;
    }
}
