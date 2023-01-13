package com.aldogg.sorter.intType.collection.mt;

import com.aldogg.sorter.intType.collection.IntComparator;
import com.aldogg.sorter.intType.collection.ObjectIntSorter;

import java.util.Arrays;

public class JavaSorterMTObjectInt implements ObjectIntSorter {

    @Override
    public void sort(Object[] array, int start, int end, IntComparator comparator) {
        Arrays.parallelSort(array, start, end, comparator);
    }


}
