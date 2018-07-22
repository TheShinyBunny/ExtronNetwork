package com.extron.utils.protocol.reflection;

public class PackageType {
    public static final PackageType NMS = new PackageType("net.minecraft.server.%s.");
    public static final PackageType CRAFT_BUKKIT = new PackageType("org.bukkit.craftbukkit.%s.");
    private final String name;

    public PackageType(String pkgName) {
        this.name = pkgName;
    }

    public String getName() {
        return name;
    }

    public PackageType subPackage(String path) {
        return new PackageType(name + path + ".");
    }

}
