package com.aldogg.sorter.collection;

import java.util.Arrays;

public class JavaParallelSorterObjectInt implements ObjectSorter {

    @Override
    public void sort(Object[] array, int start, int end, IntComparator comparator) {
        Arrays.parallelSort(array, start, end, comparator);
    }


}
