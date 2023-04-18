package com.aldogg.sorter.double_.collection;

import com.aldogg.sorter.Sorter;

public interface ObjectDoubleSorter extends Sorter {

    default void sort(Object[] arrayObject, DoubleComparator comparator) {
        sort(arrayObject, 0, arrayObject.length, comparator);
    }

    void sort(Object[] arrayObject, int start, int endP1, DoubleComparator comparator);

}