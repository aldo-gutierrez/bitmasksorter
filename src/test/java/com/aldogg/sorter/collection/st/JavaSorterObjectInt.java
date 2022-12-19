package com.aldogg.sorter.collection.st;

import com.aldogg.sorter.collection.IntComparator;
import com.aldogg.sorter.collection.ObjectSorter;

import java.util.Arrays;

public class JavaSorterObjectInt implements ObjectSorter {
    @Override
    public void sort(Object[] array, int start, int end, IntComparator comparator) {
        Arrays.sort(array, start, end, comparator);
    }

}

