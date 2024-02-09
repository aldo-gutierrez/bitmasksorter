package com.aldogg.sorter.float_.object;

import com.aldogg.sorter.int_.object.IntMapper;

public interface FloatToIntMapper<T> extends IntMapper<T> {

    float valueFloat(T o);

    default int value(T o) {
        return Float.floatToRawIntBits(valueFloat(o));
    }

    @Override
    default boolean isIeee754() {
        return true;
    }
}
