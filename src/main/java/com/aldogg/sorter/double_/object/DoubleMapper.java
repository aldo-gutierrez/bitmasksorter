package com.aldogg.sorter.double_.object;

import com.aldogg.sorter.FieldSorterOptions;

public interface DoubleMapper<T> extends FieldSorterOptions {

    double value(T o);

    default boolean isStable() {
        return true;
    }

}
