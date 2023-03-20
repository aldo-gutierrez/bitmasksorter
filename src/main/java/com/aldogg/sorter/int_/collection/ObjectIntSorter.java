package com.aldogg.sorter.int_.collection;

import com.aldogg.sorter.Sorter;

public interface ObjectIntSorter extends Sorter {

    default void sort(Object[] arrayObject, IntComparator comparator) {
        sort(arrayObject, 0, arrayObject.length, comparator);
    }

    void sort(Object[] arrayObject, int start, int end, IntComparator comparator);

}
