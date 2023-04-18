package com.aldogg.sorter.int_.collection.st;

import com.aldogg.sorter.int_.collection.IntComparator;
import com.aldogg.sorter.int_.collection.ObjectIntSorter;

import java.util.Arrays;

public class JavaSorterObjectInt implements ObjectIntSorter {
    @Override
    public void sort(Object[] array, int start, int endP1, IntComparator comparator) {
        Arrays.sort(array, start, endP1, comparator);
    }

}

