package com.aldogg.sorter.longType.collection;

import com.aldogg.sorter.Sorter;

public interface ObjectLongSorter extends Sorter {

    default void sort(Object[] arrayObject, LongComparator comparator) {
        sort(arrayObject, 0, arrayObject.length, comparator);
    }

    void sort(Object[] arrayObject, int start, int end, LongComparator comparator);

}
