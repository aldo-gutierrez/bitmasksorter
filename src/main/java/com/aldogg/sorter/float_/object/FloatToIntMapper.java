package com.aldogg.sorter.float_.object;

import com.aldogg.sorter.int_.object.IntMapper;

public interface FloatToIntMapper<T> extends IntMapper<T> {

    float value();

    default int value(T o) {
        return Float.floatToRawIntBits(value());
    }
}
