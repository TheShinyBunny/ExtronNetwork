package com.extron.commands.annotations;

import com.extron.commands.ExecuteException;
import com.extron.commands.NotRequired;
import com.extron.commands.dispatcher.AnnotationAdapter;

public class OptionalAdapter implements AnnotationAdapter<NotRequired> {
    @Override
    public Class<NotRequired> getAnnotationType() {
        return NotRequired.class;
    }

    @Override
    public boolean isValidParamType(Class<?> type) {
        return true;
    }

    @Override
    public Object validate(Object obj, NotRequired notRequired, Class<?> usedType, String argName) throws ExecuteException {
        return obj;
    }

    @Override
    public boolean isArgument() {
        return false;
    }
}
