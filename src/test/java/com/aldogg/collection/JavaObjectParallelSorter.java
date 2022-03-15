package com.aldogg.collection;

import java.util.Arrays;

public class JavaObjectParallelSorter implements ObjectSorter {

    @Override
    public void sort(Object[] list, IntComparator comparator) {
        Arrays.parallelSort(list, comparator);
    }
}
