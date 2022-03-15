package com.aldogg.collection;

import com.aldogg.intType.Sorter;

public interface ObjectSorter extends Sorter {

    void sort(Object[] list, IntComparator comparator);

}
