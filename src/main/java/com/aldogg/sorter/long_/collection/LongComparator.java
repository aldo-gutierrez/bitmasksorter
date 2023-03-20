package com.aldogg.sorter.long_.collection;

import java.util.Comparator;

public interface LongComparator<T> extends Comparator<T> {

    long value(T o);

    default int compare(T o1, T o2) {
        return Double.compare(value(o1), value(o2));
    }
}
