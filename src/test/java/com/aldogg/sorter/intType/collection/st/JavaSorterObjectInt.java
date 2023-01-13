package com.aldogg.sorter.intType.collection.st;

import com.aldogg.sorter.intType.collection.IntComparator;
import com.aldogg.sorter.intType.collection.ObjectIntSorter;

import java.util.Arrays;

public class JavaSorterObjectInt implements ObjectIntSorter {
    @Override
    public void sort(Object[] array, int start, int end, IntComparator comparator) {
        Arrays.sort(array, start, end, comparator);
    }

}

