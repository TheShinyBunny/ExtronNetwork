package com.extron.network.api.command.argument;

import com.extron.network.api.command.exceptions.InvalidNumberException;
import com.extron.network.api.players.ExtronPlayer;

import java.util.function.Function;


public class ArgumentBase<T> implements Argument<T> {

    protected Function<String, T> parser;
    private int index;
    private final String name;
    private ValidArgs<T> validArgs;
    private boolean required;
    private Argument<?> replacement;

    public ArgumentBase(String name, ValidArgs<T> validArgs, Function<String,T> parser) {
        this.index = -1;
        this.name = name;
        this.validArgs = validArgs;
        this.parser = parser;
        if (validArgs == null) {
            this.validArgs = ValidArgs.all();
        }
        this.required = true;
    }

    public ArgumentBase(String name, ValidArgs<T> validArgs) {
        this(name,validArgs,null);
    }

    public String getName() {
        return name;
    }

    @Override
    public ValidArgs<T> getValidArgs() {
        return validArgs;
    }

    @Override
    public boolean isValid(ExtronPlayer sender, ExecuteData data, String input) throws Exception {
        T parsed = this.parse(input);
        if (parsed == null) {
            return replacement != null && replacement.isValid(sender,data,input);
        }
        return validArgs.isValid(sender,data,parsed);
    }

    @Override
    public void setRequired(boolean b) {
        this.required = b;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public T parse(String input) throws Exception {
        try {
            return parser.apply(input);
        } catch (NumberFormatException e) {
            throw new InvalidNumberException(this,input);
        }
    }

    @Override
    public void setReplacement(Argument<?> arg) {
        this.replacement = arg;
    }

    @Override
    public Argument<?> getReplacement() {
        return replacement;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public void setIndex(int i) {
        if (index == -1) {
            this.index = i;
        }
    }
}
