package com.aldogg.sorter.long_.object;

import com.aldogg.sorter.shared.SorterObjectType;
import com.aldogg.sorter.shared.SorterPrimitive;

public interface SorterObjectLong<T> extends SorterObjectType<T, Long> {

    default void sort(Object[] arrayObject, LongMapper mapper) {
        sort(arrayObject, 0, arrayObject.length, mapper);
    }

    void sort(Object[] arrayObject, int start, int endP1, LongMapper mapper);

}
