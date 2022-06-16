package com.aldogg.sorter.intType.other;

import com.aldogg.sorter.intType.IntSorter;

import java.util.Arrays;

public class JavaSorterInt implements IntSorter {

    @Override
    public void sort(int[] array, int start, int end) {
        Arrays.sort(array, start, end);
    }
}
