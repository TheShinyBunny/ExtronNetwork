package com.extron.commands.annotations;

import com.extron.commands.ExecuteException;
import com.extron.commands.Sender;
import com.extron.commands.dispatcher.AnnotationAdapter;
import com.extron.commands.dispatcher.CommandContext;
import org.bukkit.command.CommandSender;

public class SenderAdapter implements AnnotationAdapter<Sender> {
    @Override
    public Class<Sender> getAnnotationType() {
        return Sender.class;
    }

    @Override
    public Object outOfSyntax(CommandContext context) {
        return context.getSender();
    }

    @Override
    public boolean isValidParamType(Class<?> type) {
        return CommandSender.class.isAssignableFrom(type);
    }

    @Override
    public Object validate(Object obj, Sender sender, Class<?> usedType, String argName) throws ExecuteException {
        if (!usedType.isInstance(obj)) {
            throw new ExecuteException("You must be a " + usedType.getSimpleName().toLowerCase() + " to execute this command.");
        }
        return usedType.cast(obj);
    }

    @Override
    public boolean isArgument() {
        return false;
    }
}
