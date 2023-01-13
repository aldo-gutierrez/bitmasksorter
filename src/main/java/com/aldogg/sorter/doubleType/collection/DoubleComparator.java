package com.aldogg.sorter.doubleType.collection;

import java.util.Comparator;

public interface DoubleComparator<T> extends Comparator<T> {

    double value(T o);

    default int compare(T o1, T o2) {
        return Double.compare(value(o1), value(o2));
    }
}
