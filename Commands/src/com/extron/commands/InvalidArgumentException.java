package com.extron.commands;

public class InvalidArgumentException extends ExecuteException {

    public InvalidArgumentException(String msg, String input) {
        super(msg + ": '" + input + "'");
    }

    public InvalidArgumentException(String msg) {
        super(msg);
    }

    public static InvalidArgumentException invalidNumber(String argName, String input) {
        return new InvalidArgumentException("Invalid " + argName + " number",input);
    }

    public static InvalidArgumentException unknown(String name, String input) {
        return new InvalidArgumentException("Unknown " + name,input);
    }

    public static InvalidArgumentException numberOutOfRange(int num, Range range) {
        return new InvalidArgumentException("Number " + num + " must be " + stringify(range));
    }

    private static String stringify(Range r) {
        return r.max() == -1 ? "bigger than " + r.min() : "between " + r.min() + " and " + r.max();
    }

    public static InvalidArgumentException onlinePlayer(String name) {
        return new InvalidArgumentException(name + " is online!");
    }

    public static InvalidArgumentException offlinePlayer(String input) {
        return new InvalidArgumentException(input + " is not online!");
    }
}
