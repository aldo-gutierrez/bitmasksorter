package com.aldogg.sorter.long_.st;

import com.aldogg.sorter.long_.LongSorter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class JavaSorterLong implements LongSorter {

    @Override
    public void sort(long[] array, int start, int endP1) {
        Arrays.sort(array, start, endP1);
    }

    @Override
    public void sort(List<Long> list) {
        Collections.sort(list);
    }
}
