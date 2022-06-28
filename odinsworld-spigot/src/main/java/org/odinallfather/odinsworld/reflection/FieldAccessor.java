package org.odinallfather.odinsworld.reflection;

public interface FieldAccessor<V> {

    V get(Object instance);

    void set(Object instance, V value);

}
