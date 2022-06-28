package org.odinallfather.odinsworld.reflection;

import org.apache.commons.lang.Validate;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionHelper {

    public static Class<?> classForName(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public static <T> MethodAccessor<T> getMethod(Class<?> clazz, String methodName, Class<?>... args) {
        Validate.notNull(clazz);
        Validate.notNull(methodName);
        try {
            Method m = clazz.getMethod(methodName, args);
            return (instance, args1) -> {
                try {
                    return (T) m.invoke(instance, args1);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            };
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    @Nullable
    public static <V> FieldAccessor<V> getField(Class<?> clazz, String fieldName, Class<V> type) {
        Validate.notNull(clazz);
        Validate.notNull(fieldName);
        Validate.notNull(type);
        try {
            Field f = clazz.getDeclaredField(fieldName);
            f.setAccessible(true);
            return new FieldAccessor<>() {
                @Override
                public V get(Object instance) {
                    try {
                        return (V) f.get(instance);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void set(Object instance, V value) {
                    try {
                        f.set(instance, value);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            };
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

}
