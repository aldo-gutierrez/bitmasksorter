package com.aldogg.sorter.long_.collection;

import java.util.Arrays;

public class JavaSorterObjectLong implements ObjectLongSorter {
    @Override
    public void sort(Object[] array, int start, int endP1, LongComparator comparator) {
        Arrays.sort(array, start, endP1, comparator);
    }

}