package com.extron.network.api.command.argument;

import java.util.function.Function;

public class ArgStringList extends ArgumentBase<String> {


    public ArgStringList(String name, ValidArgs<String> validArgs, Function<String, String> parser) {
        super(name, validArgs, parser);
    }
}
