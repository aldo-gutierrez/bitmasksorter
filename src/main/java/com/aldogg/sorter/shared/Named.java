package com.aldogg.sorter.shared;

public interface Named {
    default String getName() {
        return this.getClass().getSimpleName();
    }

}
