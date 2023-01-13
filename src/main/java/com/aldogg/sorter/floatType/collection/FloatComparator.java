package com.aldogg.sorter.floatType.collection;

import java.util.Comparator;

public interface FloatComparator<T> extends Comparator<T> {

    float value(T o);

    default int compare(T o1, T o2) {
        return Float.compare(value(o1), value(o2));
    }
}
