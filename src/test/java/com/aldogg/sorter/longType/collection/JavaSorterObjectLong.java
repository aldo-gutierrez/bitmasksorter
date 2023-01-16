package com.aldogg.sorter.longType.collection;

import java.util.Arrays;

public class JavaSorterObjectLong implements ObjectLongSorter {
    @Override
    public void sort(Object[] array, int start, int end, LongComparator comparator) {
        Arrays.sort(array, start, end, comparator);
    }

}