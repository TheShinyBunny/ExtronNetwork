package com.extron.commands.annotations;

import com.extron.commands.InvalidArgumentException;
import com.extron.commands.Range;
import com.extron.commands.dispatcher.AnnotationAdapter;

public class RangeAdapter implements AnnotationAdapter<Range> {
    @Override
    public Class<Range> getAnnotationType() {
        return Range.class;
    }

    @Override
    public boolean isValidParamType(Class<?> type) {
        return type == Integer.TYPE;
    }

    @Override
    public Object validate(Object obj, Range a, Class<?> usedType, String argName) throws InvalidArgumentException {
        if (obj instanceof Integer) {
            if (a.min() > (Integer) obj) {
                throw InvalidArgumentException.numberOutOfRange((Integer) obj,a);
            }
            if (a.max() != -1 && a.max() < (Integer)obj) {
                throw InvalidArgumentException.numberOutOfRange((Integer) obj,a);
            }
            return obj;
        } else {
            throw InvalidArgumentException.invalidNumber(argName, obj.toString());
        }
    }
}
