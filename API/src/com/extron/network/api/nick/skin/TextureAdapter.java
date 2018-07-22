package com.extron.network.api.nick.skin;

import org.bukkit.craftbukkit.libs.com.google.gson.*;

import java.lang.reflect.Type;

public class TextureAdapter implements JsonSerializer<TextureModel> {

    @Override
    public JsonElement serialize(TextureModel src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject skin = new JsonObject();
        if (src.isSlim()) {
            JsonObject metadata = new JsonObject();
            metadata.add("model", new JsonPrimitive("slim"));
            skin.add("metadata", metadata);
        }

        skin.addProperty("url", src.getUrl());
        return skin;
    }
}