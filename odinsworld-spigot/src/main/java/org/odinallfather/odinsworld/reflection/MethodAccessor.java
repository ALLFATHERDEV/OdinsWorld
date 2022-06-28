package org.odinallfather.odinsworld.reflection;

public interface MethodAccessor<T> {

    T invoke(Object instance, Object... args);

}
