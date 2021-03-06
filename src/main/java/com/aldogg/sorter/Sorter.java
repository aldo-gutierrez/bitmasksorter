package com.aldogg.sorter;

public interface Sorter {
    default String name() {
        return this.getClass().getSimpleName();
    }

    default boolean isUnsigned() {
        return false;
    }

    default void setUnsigned(boolean unsigned) {
        throw new UnsupportedOperationException();
    }

    default boolean isStable() {
        return false;
    }

    default void setStable(boolean stable) {
        throw new UnsupportedOperationException();
    }

}
