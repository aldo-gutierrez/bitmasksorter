package com.aldogg.sorter.doubleType.collection;

import com.aldogg.sorter.Sorter;

public interface ObjectDoubleSorter extends Sorter {

    default void sort(Object[] arrayObject, DoubleComparator comparator) {
        sort(arrayObject, 0, arrayObject.length, comparator);
    }

    void sort(Object[] arrayObject, int start, int end, DoubleComparator comparator);

}