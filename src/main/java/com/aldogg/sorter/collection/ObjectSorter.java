package com.aldogg.sorter.collection;

import com.aldogg.sorter.intType.Sorter;

public interface ObjectSorter extends Sorter {

    void sort(Object[] list, IntComparator comparator);

    void setStable(boolean stable);

}
