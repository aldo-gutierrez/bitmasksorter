package com.aldogg.sorter;

public interface Named {
    default String getName() {
        return this.getClass().getSimpleName();
    }

}
