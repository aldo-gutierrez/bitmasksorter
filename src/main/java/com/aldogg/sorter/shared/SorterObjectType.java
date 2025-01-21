package com.aldogg.sorter.shared;

import com.aldogg.sorter.FieldSortOptions;

public interface SorterObjectType<T, P> extends Sorter {

    default FieldSortOptions getDefaultFieldSortOptions() {
        FieldSortOptions fieldSortOptions = new FieldSortOptions();
        fieldSortOptions.setStable(true);
        return fieldSortOptions;
    }

}
