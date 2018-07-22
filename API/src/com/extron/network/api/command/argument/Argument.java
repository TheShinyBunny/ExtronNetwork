package com.extron.network.api.command.argument;

import com.extron.network.api.players.ExtronPlayer;

public interface Argument<T> {

    int getIndex();

    void setIndex(int i);

    ValidArgs<T> getValidArgs();

    boolean isValid(ExtronPlayer sender, ExecuteData data, String input) throws Exception;

    String getName();

    void setRequired(boolean b);

    boolean isRequired();

    T parse(String input) throws Exception;

    void setReplacement(Argument<?> arg);

    Argument<?> getReplacement();
}
