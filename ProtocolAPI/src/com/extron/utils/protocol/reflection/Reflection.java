package com.extron.utils.protocol.reflection;

import com.extron.utils.protocol.packets.PacketWrapper;

import java.lang.reflect.Constructor;
import java.util.Optional;

public class Reflection {

    public static <T extends PacketWrapper> T newInstance(Class<T> clazz, Class[] constructorTypes, Object... params) {
        Constructor<T> c = getConstructor(clazz,constructorTypes);
        if (c == null) return null;
        WrappedConstructor<T> wc = new WrappedConstructor<>(c,params);
        Optional<T> obj = wc.newInstance();
        return obj.orElse(null);
    }

    public static <T extends PacketWrapper> Constructor<T> getConstructor(Class<T> clazz, Class[] types) {
        try {
            return clazz.getConstructor(types);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static WrappedConstructor<?> getConstructor(String className, PackageType pkg, Class[] types) {
        return null;
    }
}
