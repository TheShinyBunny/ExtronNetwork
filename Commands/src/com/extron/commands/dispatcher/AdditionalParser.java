package com.extron.commands.dispatcher;

import com.extron.commands.ExecuteException;

public abstract class AdditionalParser<T,R>  {

    private final Class<R> resultType;
    private final ArgumentAdapter<T> parser;
    private final String name;

    public AdditionalParser(Class<R> resultType, ArgumentAdapter<T> parser, String name) {
        this.resultType = resultType;
        this.parser = parser;
        this.name = name;
    }

    public R apply(String s) throws ExecuteException {
        return convert(parser.parse(s,name));
    }

    public Class<R> getResultType() {
        return resultType;
    }

    protected abstract R convert(T parsed);
}
