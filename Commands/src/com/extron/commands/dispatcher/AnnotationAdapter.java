package com.extron.commands.dispatcher;

import com.extron.commands.ExecuteException;
import org.bukkit.command.CommandSender;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;

public interface AnnotationAdapter<A extends Annotation> {

    Class<A> getAnnotationType();

    default Object outOfSyntax(CommandContext context) {
        return null;
    }

    boolean isValidParamType(Class<?> type);

    Object validate(Object obj, A a, Class<?> usedType, String argName) throws ExecuteException;

    default boolean isRequired() {
        return true;
    }

    default Collection<String> getAnnotatedCompletions(Parameter p) {
        return new ArrayList<>();
    }

    default boolean isArgument() {
        return true;
    }

    default Collection<String> getSenderCompletions(CommandSender sender) {
        return new ArrayList<>();
    }
}
