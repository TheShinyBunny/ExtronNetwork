package com.extron.commands.dispatcher;

import com.extron.commands.InvalidArgumentException;

import javax.annotation.Nullable;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;

public interface ArgumentAdapter<T> {

    Class<T> getType();

    T parse(String input, String argName) throws InvalidArgumentException;

    @Nullable T defaultsTo();

    default Collection<String> getArgCompletions(Parameter p) {
        return new ArrayList<>();
    }
}
