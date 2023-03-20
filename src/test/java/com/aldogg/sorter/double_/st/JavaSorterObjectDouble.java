package com.aldogg.sorter.double_.st;

import com.aldogg.sorter.double_.collection.DoubleComparator;
import com.aldogg.sorter.double_.collection.ObjectDoubleSorter;

import java.util.Arrays;

public class JavaSorterObjectDouble implements ObjectDoubleSorter {
    @Override
    public void sort(Object[] array, int start, int end, DoubleComparator comparator) {
        Arrays.sort(array, start, end, comparator);
    }

}
