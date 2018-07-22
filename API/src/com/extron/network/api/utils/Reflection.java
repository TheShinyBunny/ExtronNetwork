package com.extron.network.api.utils;

import org.bukkit.Bukkit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Reflection {

    public static String version;

    static {
        version = Bukkit.getServer().getClass().getPackage().getImplementationVersion();
    }

    public static void setFieldsAnnotated(Class<?> target, Object targetObj, Class<? extends Annotation> annotationClass, Object value) {
        Field[] fs = getFields(target,annotationClass);
        for (Field f : fs) {
            setField(f,targetObj,value);
        }
    }

    public static void setField(Field f, Object targetObj, Object value) {
        f.setAccessible(true);
        try {
            f.set(targetObj,value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static Field[] getFields(Class<?> target, Class<? extends Annotation> annotationClass) {
        List<Field> fl = new ArrayList<>();
        for (Field f : target.getFields()) {
            if (f.isAnnotationPresent(annotationClass)) {
                fl.add(f);
            }
        }
        return fl.toArray(new Field[]{});
    }

    public static void setValue(Object obj, String fieldName, Object value) {
        Field f = getField(obj,fieldName);
        if (f != null) {
            setField(f,obj,value);
        } else {
            System.out.println("field '" + fieldName + " is null");
        }
    }

    public static void setValue(Class<?> cls, String fieldName, Object value) {
        Field f = getField(cls,fieldName);
        if (f != null) {
            setField(f,null,value);
        }
    }

    public static Field getField(Object obj, String fieldName) {
        if (obj == null) return null;
        Class<?> cls;
        if (obj instanceof Class<?>) {
            cls = (Class<?>) obj;
        } else {
            cls = obj.getClass();
        }
        try {
            Field f = cls.getDeclaredField(fieldName);
            f.setAccessible(true);
            return f;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object invokeMethod(Object obj, String name, Object... args) {
        List<Class> clss = new ArrayList<>();
        for (Object o : args) {
            if (o == null) {
                return null;
            }
            clss.add(o.getClass());
        }
        Method m = getMethod(obj,name,clss.toArray(new Class[0]));
        if (m == null) return null;
        try {
            return m.invoke(obj instanceof Class ? null : obj,args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Method getMethod(Object obj, String name,Class... paramTypes) {
        if (obj == null) return null;
        Class<?> cls;
        if (obj instanceof Class<?>) {
            cls = (Class<?>) obj;
        } else {
            cls = obj.getClass();
        }
        try {
            Method m = cls.getDeclaredMethod(name,paramTypes);
            m.setAccessible(true);
            return m;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getFieldValue(Object obj, String name) {
        Field f = getField(obj,name);
        try {
            return f.get(obj instanceof Class ? null : obj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

}
