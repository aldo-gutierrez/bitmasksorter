package com.aldogg.sorter.int_.object;

import com.aldogg.sorter.FieldSorterOptions;

public interface IntMapper<T> extends FieldSorterOptions {

    int value(T o);

    default boolean isStable() {
        return true;
    }

}
