package com.aldogg.sorter.int_.collection;

import java.util.Comparator;

public interface IntComparator<T> extends Comparator<T> {

    int value(T o);

    default int compare(T o1, T o2) {
        return Integer.compare(value(o1), value(o2));
    }
}
