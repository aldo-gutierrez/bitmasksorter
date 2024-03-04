package com.aldogg.sorter.double_.object;

import com.aldogg.sorter.FieldOptions;

public interface DoubleMapper<T> extends FieldOptions {

    double value(T o);

    default boolean isStable() {
        return true;
    }

}
