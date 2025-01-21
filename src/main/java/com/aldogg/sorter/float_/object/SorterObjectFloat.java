package com.aldogg.sorter.float_.object;

import com.aldogg.sorter.FieldSortOptions;
import com.aldogg.sorter.shared.FieldType;
import com.aldogg.sorter.shared.SorterObjectType;


public interface SorterObjectFloat<T> extends SorterObjectType<T, Float> {

    default FieldSortOptions getDefaultFieldSortOptions() {
        FieldSortOptions fieldSortOptions = new FieldSortOptions();
        fieldSortOptions.setStable(true);
        fieldSortOptions.setFieldType(FieldType.IEEE764_FLOAT);
        return fieldSortOptions;
    }
    default void sort(Object[] arrayObject, FloatMapper mapper) {
        sort(arrayObject, 0, arrayObject.length, mapper);
    }

    void sort(Object[] arrayObject, int start, int endP1, FloatMapper mapper);

}
