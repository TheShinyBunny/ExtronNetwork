package com.extron.commands;

public class ExecuteException extends Exception {

    public ExecuteException() {
        super();
    }

    public ExecuteException(String message) {
        super(message);
    }

    public static ExecuteException invalidUsage() {
        return new ExecuteException("Invalid Usage!");
    }
}
