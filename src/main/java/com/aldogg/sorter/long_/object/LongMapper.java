package com.aldogg.sorter.long_.object;

import com.aldogg.sorter.FieldSorterOptions;

public interface LongMapper<T>  extends FieldSorterOptions {

    long value(T o);

    default boolean isStable() {
        return true;
    }

}
