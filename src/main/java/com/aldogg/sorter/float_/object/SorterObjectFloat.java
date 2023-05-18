package com.aldogg.sorter.float_.object;

import com.aldogg.sorter.Sorter;

public interface SorterObjectFloat extends Sorter {

    default void sort(Object[] arrayObject, FloatMapper mapper) {
        sort(arrayObject, 0, arrayObject.length, mapper);
    }

    void sort(Object[] arrayObject, int start, int endP1, FloatMapper mapper);

}
