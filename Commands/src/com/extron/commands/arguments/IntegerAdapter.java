package com.extron.commands.arguments;

import com.extron.commands.InvalidArgumentException;
import com.extron.commands.dispatcher.ArgumentAdapter;

import javax.annotation.Nullable;

public class IntegerAdapter implements ArgumentAdapter<Integer> {
    @Override
    public Class<Integer> getType() {
        return Integer.TYPE;
    }

    @Override
    public Integer parse(String input, String argName) throws InvalidArgumentException {
        try {
            return Integer.parseInt(input);
        } catch (Exception e) {
            throw InvalidArgumentException.invalidNumber(argName,input);
        }
    }

    @Nullable
    @Override
    public Integer defaultsTo() {
        return 0;
    }
}
