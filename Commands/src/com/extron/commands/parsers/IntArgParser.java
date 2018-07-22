package com.extron.commands.parsers;

import com.extron.commands.arguments.IntegerAdapter;
import com.extron.commands.dispatcher.AdditionalParser;

public abstract class IntArgParser<R> extends AdditionalParser<Integer,R> {

    public IntArgParser(Class<R> resultType, String name) {
        super(resultType, new IntegerAdapter(), name);
    }
}
