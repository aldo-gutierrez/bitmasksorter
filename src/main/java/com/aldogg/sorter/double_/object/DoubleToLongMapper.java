package com.aldogg.sorter.double_.object;

import com.aldogg.sorter.long_.object.LongMapper;

public interface DoubleToLongMapper<T> extends LongMapper<T> {

    double value();

    default long value(T o) {
        return Double.doubleToRawLongBits(value());
    }
}
