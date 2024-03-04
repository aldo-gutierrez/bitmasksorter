package com.aldogg.sorter.float_.object;

import com.aldogg.sorter.FieldOptions;

public interface FloatMapper<T>  extends FieldOptions {

    float value(T o);

    default boolean isStable() {
        return true;
    }

}
