package com.aldogg.sorter.collection.mt;

import com.aldogg.sorter.collection.IntComparator;
import com.aldogg.sorter.collection.ObjectSorter;

import java.util.Arrays;

public class JavaSorterMTObjectInt implements ObjectSorter {

    @Override
    public void sort(Object[] array, int start, int end, IntComparator comparator) {
        Arrays.parallelSort(array, start, end, comparator);
    }


}
