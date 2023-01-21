package com.aldogg.sorter.shortType.st;

import com.aldogg.sorter.shortType.ShortSorter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class JavaSorterShort implements ShortSorter {

    @Override
    public void sort(short[] array, int start, int end) {
        Arrays.sort(array, start, end);
    }

    @Override
    public void sort(List<Short> list) {
        Collections.sort(list);
    }
}
