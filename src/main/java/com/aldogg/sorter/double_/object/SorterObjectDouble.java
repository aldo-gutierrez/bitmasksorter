package com.aldogg.sorter.double_.object;

import com.aldogg.sorter.shared.SorterObjectType;

public interface SorterObjectDouble<T> extends SorterObjectType<T, Double> {

    default void sort(Object[] arrayObject, DoubleMapper mapper) {
        sort(arrayObject, 0, arrayObject.length, mapper);
    }

    void sort(Object[] arrayObject, int start, int endP1, DoubleMapper mapper);

}