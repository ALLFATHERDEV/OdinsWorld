package org.odinallfather.odinsworld.util;

import javax.annotation.Nullable;

public class BooleanCallback<T> {

    private final boolean result;
    @Nullable
    private final T type;

    private BooleanCallback(boolean result, T type) {
        this.result = result;
        this.type = type;
    }

    public boolean getResult() {
        return result;
    }

    @Nullable
    public T getType() {
        return type;
    }

    public static <T> BooleanCallback<T> fail() {
        return new BooleanCallback<>(false, null);
    }

    public static <T> BooleanCallback<T> success(T type) {
        return new BooleanCallback<>(true, type);
    }
}
