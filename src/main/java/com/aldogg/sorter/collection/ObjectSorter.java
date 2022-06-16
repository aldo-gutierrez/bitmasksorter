package com.aldogg.sorter.collection;

import com.aldogg.sorter.intType.Sorter;

public interface ObjectSorter extends Sorter {

    default void sort(Object[] arrayObject, IntComparator comparator) {
        sort(arrayObject, 0, arrayObject.length, comparator);
    }

    void sort(Object[] arrayObject, int start, int end, IntComparator comparator);

}
