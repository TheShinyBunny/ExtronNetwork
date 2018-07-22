package com.extron.utils.protocol.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class WrappedConstructor<T> {

    private final Object[] args;
    private Constructor<T> constructor;

    public WrappedConstructor(Constructor<T> cons, Object... args) {
        this.constructor = cons;
        this.args = args;
    }

    public Constructor<T> getConstructor() {
        return constructor;
    }

    public Optional<T> newInstance() {
        try {
            T t = constructor.newInstance(args);
            return Optional.of(t);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            return Optional.empty();
        }
    }
}
