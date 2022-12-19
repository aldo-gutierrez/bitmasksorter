package com.aldogg.sorter.intType.mt;

import com.aldogg.sorter.intType.IntSorter;

import java.util.Arrays;

public class JavaSorterMTInt implements IntSorter {

    @Override
    public void sort(int[] array, int start, int end) {
        Arrays.parallelSort(array, start, end);
    }

}
