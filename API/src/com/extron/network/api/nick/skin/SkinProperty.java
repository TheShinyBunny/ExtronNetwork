package com.extron.network.api.nick.skin;

public class SkinProperty {

    public static final String SKIN_KEY = "textures";

    private String value;
    private String signature;

    public String getValue() {
        return value;
    }

    public String getSignature() {
        return signature;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + '{' +
                "name='" + SKIN_KEY + '\'' +
                ", value='" + value + '\'' +
                ", signature='" + signature + '\'' +
                '}';
    }
}