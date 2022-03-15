package com.aldogg.intType;

public interface Sorter {
    default String name() {
        return this.getClass().getSimpleName();
    }
}
