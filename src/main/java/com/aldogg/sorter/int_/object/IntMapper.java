package com.aldogg.sorter.int_.object;

import com.aldogg.sorter.FieldOptions;

public interface IntMapper<T> extends FieldOptions {

    int value(T o);

    default boolean isStable() {
        return true;
    }

}
