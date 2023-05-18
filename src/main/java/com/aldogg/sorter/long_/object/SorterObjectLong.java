package com.aldogg.sorter.long_.object;

import com.aldogg.sorter.Sorter;

public interface SorterObjectLong extends Sorter {

    default void sort(Object[] arrayObject, LongMapper mapper) {
        sort(arrayObject, 0, arrayObject.length, mapper);
    }

    void sort(Object[] arrayObject, int start, int endP1, LongMapper mapper);

}
