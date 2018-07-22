package com.extron.commands;

import javax.annotation.Nonnull;
import java.util.Optional;

public class EnumArg<E extends Enum<E>> {

    private final Class<E> type;
    private E value;

    public EnumArg(@Nonnull E value) {
        this.value = value;
        this.type = value.getDeclaringClass();
    }

    public EnumArg(Class<E> enumClass) {
        this.type = enumClass;
    }

    public Class<E> getType() {
        return type;
    }

    public E get() {
        return value;
    }

    public E getOrDefault(E def) {
        return Optional.ofNullable(value).orElse(def);
    }

}
