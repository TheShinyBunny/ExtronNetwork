package com.extron.commands;

import com.extron.commands.dispatcher.Dispatcher;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandInfo {

    private Method method;
    private String name;
    private Command settings;
    private CommandHolder holder;

    public CommandInfo(CommandHolder holder, Method method, String name, Command settings) {
        this.method = method;
        this.name = name;
        this.settings = settings;
    }

    public Method getMethod() {
        return method;
    }

    public String getName() {
        return name;
    }

    public Command getSettings() {
        return settings;
    }

    public Parameter[] getAllParameters() {
        return method.getParameters();
    }

    public int getArgumentCount() {
        return Dispatcher.getArguments(method).size();
    }

    public List<String> getAliases() {
        List<String> list = new ArrayList<>(Arrays.asList(settings.aliases()));
        list.add(name);
        return list;
    }

    public CommandHolder getHolder() {
        return holder;
    }

    public void run(Object[] array) throws InvocationTargetException, IllegalAccessException {
        this.method.invoke(holder,array);
    }
}
