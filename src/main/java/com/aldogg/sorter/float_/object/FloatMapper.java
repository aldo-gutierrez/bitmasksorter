package com.aldogg.sorter.float_.object;

import com.aldogg.sorter.FieldSorterOptions;

public interface FloatMapper<T>  extends FieldSorterOptions {

    float value(T o);

    default boolean isStable() {
        return true;
    }

}
