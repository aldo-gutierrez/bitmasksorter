package com.aldogg.sorter.sorters;


import com.aldogg.sorter.intType.IntSorter;

import java.util.Arrays;

public class JavaParallelSorterInt implements IntSorter {

    @Override
    public void sort(int[] array, int start, int end) {
        Arrays.parallelSort(array, start, end);
    }
}
