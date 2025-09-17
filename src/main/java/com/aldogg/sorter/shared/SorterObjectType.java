package com.aldogg.sorter.shared;

import com.aldogg.sorter.CollectionOptions;
import com.aldogg.sorter.FieldSortOptions;
import com.aldogg.sorter.MemoryBalance;

public interface SorterObjectType<T, P> extends Sorter {

    default FieldSortOptions getDefaultFieldSortOptions() {
        FieldSortOptions fieldSortOptions = new FieldSortOptions();
        fieldSortOptions.setStable(true);
        return fieldSortOptions;
    }

    default CollectionOptions getCollectionOptions() {
        CollectionOptions collectionOptions = new CollectionOptions();
        collectionOptions.setStable(null);
        collectionOptions.setMemoryBalance(MemoryBalance.BALANCED_MEMORY_CPU);
        collectionOptions.setNullHandling(null);
        return collectionOptions;
    }

}
