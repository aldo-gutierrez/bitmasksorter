package com.aldogg.collection;

import java.util.Comparator;

public interface IntComparator extends Comparator {

    int intValue(Object o);

    default int compare(Object o1, Object o2) {
        return Integer.compare(intValue(o1), intValue(o2));
    }
}
