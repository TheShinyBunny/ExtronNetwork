package com.extron.commands.annotations;

import com.extron.commands.DefaultInt;
import com.extron.commands.DefaultString;
import com.extron.commands.ExecuteException;
import com.extron.commands.dispatcher.AnnotationAdapter;

public class DefaultAdapter {

    public static class ForInt implements AnnotationAdapter<DefaultInt> {

        @Override
        public Class<DefaultInt> getAnnotationType() {
            return DefaultInt.class;
        }

        @Override
        public boolean isValidParamType(Class<?> type) {
            return type == Integer.TYPE;
        }

        @Override
        public Object validate(Object obj, DefaultInt defInt, Class<?> usedType, String argName) throws ExecuteException {
            if (obj instanceof Integer) {
                return obj;
            }
            return defInt.value();
        }

        @Override
        public boolean isRequired() {
            return false;
        }
    }

    public static class ForString implements AnnotationAdapter<DefaultString> {

        @Override
        public Class<DefaultString> getAnnotationType() {
            return DefaultString.class;
        }

        @Override
        public boolean isValidParamType(Class<?> type) {
            return type == String.class;
        }

        @Override
        public Object validate(Object obj, DefaultString defStr, Class<?> usedType, String argName) throws ExecuteException {
            if (obj != null) {
                return obj.toString();
            }
            return defStr.value();
        }

        @Override
        public boolean isRequired() {
            return false;
        }
    }

}
