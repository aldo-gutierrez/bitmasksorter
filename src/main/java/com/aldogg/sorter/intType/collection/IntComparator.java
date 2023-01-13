package com.aldogg.sorter.intType.collection;

import java.util.Comparator;

public interface IntComparator<T> extends Comparator<T> {

    int intValue(T o);

    default int compare(T o1, T o2) {
        return Integer.compare(intValue(o1), intValue(o2));
    }
}
