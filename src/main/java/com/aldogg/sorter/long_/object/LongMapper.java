package com.aldogg.sorter.long_.object;

import com.aldogg.sorter.FieldOptions;

public interface LongMapper<T>  extends FieldOptions {

    long value(T o);

    default boolean isStable() {
        return true;
    }

}
