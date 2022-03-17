package com.aldogg.collection;

import java.util.Arrays;

public class JavaParallelSorterObjectInt implements ObjectSorter {

    @Override
    public void sort(Object[] list, IntComparator comparator) {
        Arrays.parallelSort(list, comparator);
    }
}
