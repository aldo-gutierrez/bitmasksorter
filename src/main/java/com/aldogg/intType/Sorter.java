package com.aldogg.intType;

public interface Sorter {
    default String name() {
        return this.getClass().getSimpleName();
    }

    default boolean isUnsigned() {
        return false;
    }

    default boolean isStable() {
        return false;
    }

}
