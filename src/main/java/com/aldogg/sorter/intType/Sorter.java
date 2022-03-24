package com.aldogg.sorter.intType;

public interface Sorter {
    default String name() {
        return this.getClass().getSimpleName();
    }

    default boolean isUnsigned() {
        return false;
    }

    void setUnsigned(boolean unsigned);

    default boolean isStable() {
        return false;
    }

}
