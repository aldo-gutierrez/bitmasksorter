package com.aldogg.sorter.double_.object;

import com.aldogg.sorter.Sorter;

public interface SorterObjectDouble extends Sorter {

    default void sort(Object[] arrayObject, DoubleMapper mapper) {
        sort(arrayObject, 0, arrayObject.length, mapper);
    }

    void sort(Object[] arrayObject, int start, int endP1, DoubleMapper mapper);

}