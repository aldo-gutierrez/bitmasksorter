package com.aldogg.sorter.doubleType.st;

import com.aldogg.sorter.doubleType.collection.DoubleComparator;
import com.aldogg.sorter.doubleType.collection.ObjectDoubleSorter;

import java.util.Arrays;

public class JavaSorterObjectDouble implements ObjectDoubleSorter {
    @Override
    public void sort(Object[] array, int start, int end, DoubleComparator comparator) {
        Arrays.sort(array, start, end, comparator);
    }

}
