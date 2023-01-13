package com.aldogg.sorter.longType.st;

import com.aldogg.sorter.longType.LongSorter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class JavaSorterLong implements LongSorter {

    @Override
    public void sort(long[] array, int start, int end) {
        Arrays.sort(array, start, end);
    }

    @Override
    public void sort(List<Long> list) {
        Collections.sort(list);
    }
}
