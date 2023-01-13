package com.aldogg.sorter.floatType.collection;

import com.aldogg.sorter.Sorter;

public interface ObjectFloatSorter extends Sorter {

    default void sort(Object[] arrayObject, FloatComparator comparator) {
        sort(arrayObject, 0, arrayObject.length, comparator);
    }

    void sort(Object[] arrayObject, int start, int end, FloatComparator comparator);

}
