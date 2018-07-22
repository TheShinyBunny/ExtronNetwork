package com.extron.commands.arguments;

import com.extron.commands.InvalidArgumentException;
import com.extron.commands.dispatcher.ArgumentAdapter;

import javax.annotation.Nullable;

public class StringAdapter implements ArgumentAdapter<String> {
    @Override
    public Class<String> getType() {
        return String.class;
    }

    @Override
    public String parse(String input, String argName) throws InvalidArgumentException {
        return input;
    }

    @Nullable
    @Override
    public String defaultsTo() {
        return null;
    }
}
